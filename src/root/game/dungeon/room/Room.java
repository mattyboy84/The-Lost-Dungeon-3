package root.game.dungeon.room;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import root.game.Tear.Tear_Enemy;
import root.game.Tear.Tear_Player;
import root.game.dungeon.Dungeon;
import root.game.dungeon.room.boss.Boss;
import root.game.dungeon.room.boss.Boss_DukeOfFlies;
import root.game.dungeon.room.boss.Boss_Fistula;
import root.game.dungeon.room.enemy.*;
import root.game.dungeon.Shading;
import root.game.dungeon.room.item.*;
import root.game.music.Music;
import root.game.player.Player;
import root.game.Tear.Tear;
import root.game.util.Vecc2f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;


public class Room implements Runnable {

    public static int finishedRoom;
    //
    public Thread t;
    private final String threadName;
    //

    Random random = new Random();
    public Background background;
    public Shading shading;
    public String room;

    Dungeon parentDungeon;
    int i;
    int j;
    int type;
    int upType, downType, leftType, rightType;
    int floorLevel;
    float scaleX, scaleY;
    boolean startRoom;
    Rectangle2D screenBounds;
    //
    JsonObject roomTemplate = null;
    Background_items backgroundItems;
    public ArrayList<Door> doors = new ArrayList<>();
    public ArrayList<Enemy> enemies = new ArrayList<>();
    public ArrayList<Boss> bosses = new ArrayList<>();
    public ArrayList<Item> items = new ArrayList<>();
    public ArrayList<Rock> rocks = new ArrayList<>();
    //
    ArrayList<Active_Bomb> bombs = new ArrayList<>();
    public ArrayList<Tear> tears = new ArrayList<>();
    public ArrayList<Smoke> smokeClouds = new ArrayList<>();
    //
    //public Underlay underlay=null;
    //
    String music = null;
    //
    Player playerTarget;
    //
    Door trapDoor = null;
    //
    String parentThreadName;

    public Room(int i, int j, int type, int up, int down, int left, int right, int floorLevel, float scaleX, float scaleY, Rectangle2D screenBounds, String threadName, Shading shading, Dungeon dungeon, boolean startRoom) {
        //
        this.parentDungeon=dungeon;
        this.parentThreadName = threadName;
        this.threadName = threadName;
        //
        this.i = i;
        this.j = j;
        this.type = type;
        //
        this.upType = up;
        this.downType = down;
        this.leftType = left;
        this.rightType = right;
        //
        this.floorLevel = floorLevel;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.screenBounds = screenBounds;
        //
        this.shading = shading;
        this.startRoom = startRoom;
    }

    public Room() {
        threadName = "";
    }

    public void run() {
        this.backgroundItems = new Background_items();
        //
        this.roomTemplate = new JsonParser().parse(String.valueOf(roomTemplateGetter("src\\room templates\\Floor-" + this.floorLevel + "\\Type-" + this.type))).getAsJsonObject();
        if (startRoom)
            this.roomTemplate = new JsonParser().parse(String.valueOf(templateGetterSub("src\\room templates\\Floor-" + this.floorLevel + "\\Start.json"))).getAsJsonObject();
        //
        this.background = new Background(this.roomTemplate.getAsJsonObject("Background"), scaleX, scaleY, screenBounds);
        System.out.println("Thread: " + threadName + " Background Complete");
        //
        try {
            this.backgroundItems.addProps(this.roomTemplate.getAsJsonObject("Props"), scaleX, scaleY, screenBounds,this.background.borderX,this.background.borderY);
            System.out.println("Thread: " + threadName + " Props Complete");
        }catch (NullPointerException e){
            //prop object is not present in room template - no props in the room.
        }catch (Exception e){
            e.printStackTrace();
        }
        //
        itemAdder(this.roomTemplate.getAsJsonArray("items"), scaleX, scaleY, screenBounds, this);
        System.out.println("Thread: " + threadName + " Items Complete");
        //
        rockAdder(this.roomTemplate.getAsJsonObject("Rocks"), scaleX, scaleY);
        System.out.println("Thread: " + threadName + " Rocks Complete");
        //
        enemyAdder(this.roomTemplate.getAsJsonArray("enemies"), scaleX, scaleY, screenBounds, shading);
        System.out.println("Thread: " + threadName + " Enemies Complete");
        try {
            bossAdder(this.roomTemplate.getAsJsonArray("bosses"), scaleX, scaleY, screenBounds, shading);
            System.out.println("Thread: " + threadName + " Bosses Complete");
        }catch (Exception e){
            e.printStackTrace();
        }
        //TODO add underlays
        try{
            underlayAdder(this.roomTemplate.getAsJsonObject("Underlay"),this.background.borderX,this.background.borderY,scaleX, scaleY,screenBounds);
            System.out.println("Thread: " + threadName + " Underlay Complete");
        } catch (NullPointerException e) {
        }catch (Exception e){
            e.printStackTrace(); //error in underlay
        }
        //
        try {
            this.music = this.roomTemplate.get("Music").getAsJsonObject().get("music").getAsString();
        } catch (Exception ignored) {
        }
        //213 x 180
        if (upType > 0) {
            doors.add(new Door("up", 0, this.upType, this.type, scaleX, scaleY, screenBounds, background));
        } else {
            background.extendUp(screenBounds);
        }
        if (downType > 0) {
            doors.add(new Door("down", 180, this.downType, this.type, scaleX, scaleY, screenBounds, background));
        } else {
            background.extendDown(screenBounds);
        }
        if (leftType > 0) {
            doors.add(new Door("left", 270, this.leftType, this.type, scaleX, scaleY, screenBounds, background));
        } else {
            background.extendLeft(screenBounds);
        }
        if (rightType > 0) {
            doors.add(new Door("right", 90, this.rightType, this.type, scaleX, scaleY, screenBounds, background));
        } else {
            background.extendRight(screenBounds);
        }
        if (type == 3) {
            trapDoor = new Door(scaleX, scaleY, 4, screenBounds);
        }
        System.out.println("Thread: " + threadName + " Doors Complete");
        //
        finishedRoom += 1;
    }

    private void underlayAdder(JsonObject underlayTemplate, int borderX, int borderY, float scaleX, float scaleY, Rectangle2D screenBounds) {
        //underlay=new Underlay(underlayTemplate,borderX,borderY,scaleX,scaleY,screenBounds);
    }


    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    private void rockAdder(JsonObject rockTemplate, float scaleX, float scaleY) {
        int width, height, borderX, borderY;
        float sheetScale;
        String name = rockTemplate.get("name").getAsString();
        sheetScale = rockTemplate.get("SheetScale").getAsFloat();
        width = rockTemplate.get("Width").getAsInt();
        height = rockTemplate.get("Height").getAsInt();
        borderX = rockTemplate.get("BorderX").getAsInt();
        borderY = rockTemplate.get("BorderY").getAsInt();
        for (int k = 0; k < rockTemplate.get("rocksARR").getAsJsonArray().size(); k++) {
            int a = rockTemplate.get("rocksARR").getAsJsonArray().get(k).getAsJsonObject().get("PositionX").getAsInt();
            int b = rockTemplate.get("rocksARR").getAsJsonArray().get(k).getAsJsonObject().get("PositionY").getAsInt();
            String type = rockTemplate.get("rocksARR").getAsJsonArray().get(k).getAsJsonObject().get("Type").getAsString();
            rocks.add(new Rock(a, b, type, name, sheetScale, width, height, borderX, borderY, scaleX, scaleY));
        }
    }

    private void itemAdder(JsonArray itemsArray, float scaleX, float scaleY, Rectangle2D screenBounds, Room parentRoom) {
        for (int k = 0; k < itemsArray.size(); k++) {

            JsonObject itemTemplate = new JsonParser().parse(String.valueOf(templateGetterSub("src\\resources\\gfx\\items\\pick ups\\" + itemsArray.get(k).getAsJsonObject().get("item").getAsString() + ".json"))).getAsJsonObject();
            Vecc2f pos = new Vecc2f(itemsArray.get(k).getAsJsonObject().get("PositionX").getAsInt(), itemsArray.get(k).getAsJsonObject().get("PositionY").getAsInt());

            switch (itemsArray.get(k).getAsJsonObject().get("item").getAsString()) {
                case "coin" -> items.add(new Item_Coin(itemTemplate, pos, scaleX, scaleY, screenBounds, parentRoom));
                case "key", "double-key" -> items.add(new Item_Key(itemTemplate, pos, scaleX, scaleY, screenBounds, parentRoom));
                case "bomb", "double-bomb" -> items.add(new Item_Bomb(itemTemplate, pos, scaleX, scaleY, screenBounds, parentRoom));
                case "heart", "double-heart", "half-heart" -> items.add(new Item_Heart(itemTemplate, pos, scaleX, scaleY, screenBounds, parentRoom));
            }
        }
    }

    private void enemyAdder(JsonArray enemyArray, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading) {
        for (int k = 0; k < enemyArray.size(); k++) {

            JsonObject enemytemplate = new JsonParser().parse(String.valueOf(templateGetterSub("src\\resources\\gfx\\monsters\\" + enemyArray.get(k).getAsJsonObject().get("type").getAsString() + "\\" + enemyArray.get(k).getAsJsonObject().get("enemy").getAsString() + ".json"))).getAsJsonObject();
            Vecc2f pos = new Vecc2f(enemyArray.get(k).getAsJsonObject().get("PositionX").getAsInt(), enemyArray.get(k).getAsJsonObject().get("PositionY").getAsInt());
            switch (enemyArray.get(k).getAsJsonObject().get("enemy").getAsString()) {
                case "fly" -> enemies.add(new Enemy_Fly(enemytemplate, pos, scaleX, scaleY, screenBounds, shading, this));
                case "attack fly" -> enemies.add(new Enemy_AttackFly(enemytemplate, pos, scaleX, scaleY, screenBounds, shading, this));
                case "pooter" -> enemies.add(new Enemy_Pooter(enemytemplate, pos, scaleX, scaleY, screenBounds, shading, this));
                case "spider" -> enemies.add(new Enemy_Spider(enemytemplate, pos, scaleX, scaleY, screenBounds, shading, this));
                case "gaper" -> enemies.add(new Enemy_Gaper(enemytemplate, pos, scaleX, scaleY, screenBounds, shading, this, enemyArray.get(k).getAsJsonObject().get("Rotate").getAsInt()));
            }
        }
    }

    private void bossAdder(JsonArray bossArray, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading) {
        for (int k = 0; k < bossArray.size(); k++) {

            JsonObject bossTemplate = new JsonParser().parse(String.valueOf(templateGetterSub("src\\resources\\gfx\\bosses\\" + bossArray.get(k).getAsJsonObject()
                    .get("type").getAsString() + "\\" + bossArray.get(k).getAsJsonObject().get("boss").getAsString() + ".json"))).getAsJsonObject();
            Vecc2f pos = new Vecc2f(bossArray.get(k).getAsJsonObject().get("PositionX").getAsInt(), bossArray.get(k).getAsJsonObject().get("PositionY").getAsInt());
            switch (bossArray.get(k).getAsJsonObject().get("boss").getAsString()) {
                //case "pin" -> bosses.add(new Boss_Pin(bossTemplate, pos, scaleX, scaleY, screenBounds, shading, this));
                case "fistula" -> bosses.add(new Boss_Fistula(bossTemplate, pos, scaleX, scaleY, screenBounds, shading, this));
                case "dukeofflies" -> bosses.add(new Boss_DukeOfFlies(bossTemplate, pos, scaleX, scaleY, screenBounds, shading, this));
            }
        }
    }

    private StringBuilder roomTemplateGetter(String file1) {
        File directPath = new File(file1);
        String[] contents = directPath.list((dir, name) -> !name.contains("!"));//TODO a '!' in template title will exclude the template from randomly being chosen
        String room = null;

        if (contents != null) {
            room = contents[random.nextInt(contents.length)];
        }
        this.room = room;
        return templateGetterSub("src\\room templates\\Floor-" + this.floorLevel + "\\Type-" + this.type + "\\" + room);
    }

    private StringBuilder templateGetterSub(String file2) {
        StringBuilder json = new StringBuilder();
        try {
            File file = new File(file2);

            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null) {
                json.append(st);
            }
        } catch (Exception e) {
            System.out.println("Cannot find room template - Room");
        }
        return json;
    }

    public void load(Group group, Player player) {
        this.playerTarget = player;
        this.shading.load(group);
        //
        //if (this.underlay!=null){
        //    this.underlay.load(group);
        //}
        //
        this.background.load(group);
        this.backgroundItems.load(group);
        //will attempt to add the music to the array
        Music.addMusic(this.music, true, this.hashCode());
        //
        for (Enemy enemy : enemies) {
            enemy.load(group, player);
        }
        //
        for (Boss boss : bosses) {
            boss.load(group, player);
        }
        //
        if (bosses.size() == 0 && type == 3) {
            trapDoor.loadTrapDoor(group);
        }
        //
        for (Item item : items) {
            item.load(group);
        }
        //
        for (Door door : doors) {
            door.load(group);
        }
        //
        for (Rock rock : rocks) {
            rock.load(group);
        }
        for (int k = 0; k < bombs.size(); k++) {
            bombs.get(k).load(group, this, bombs);
        }
    }

    public void unload(Group group) {
        this.shading.unload(group);
        //
        //if (this.underlay!=null){
        //    this.underlay.unload(group);
        //}
        //
        this.background.unload(group);
        this.backgroundItems.unload(group);
        //
        for (Enemy enemy : enemies) {
            enemy.unload(group);
        }
        //
        for (Boss boss : bosses) {
            boss.unload(group);
        }
        if (this.trapDoor != null && group.getChildren().contains(this.trapDoor.getTrapDoor())) {
            trapDoor.unloadTrapDoor(group);
        }
        //
        for (Item item : items) {
            item.unload(group);
        }
        //
        for (Door door : doors) {
            door.unload(group);
        }
        //
        for (Rock rock : rocks) {
            rock.unload(group);
        }
        for (int k = 0; k < bombs.size(); k++) {
            bombs.get(k).unload(group, bombs);
        }
        for (int k = tears.size() - 1; k > -1; k--) {//doing to back-to-front avoids concurrent errors from terminating while in a loop
            tears.get(k).destroy(group, tears);
        }
        for (int k = smokeClouds.size() - 1; k > -1; k--) {//smokes are short lives - self terminate & delete when leaving a room
            smokeClouds.get(k).unload(group);//leads to the destroy method
        }
    }

    public void checkTrapDoor(Group group) {
        if (this.trapDoor!=null&& bosses.size() == 0){
            this.trapDoor.loadTrapDoor(group);
        }
    }

    public void addBombSub(Group group, String bombTemplate, Vecc2f centerPos, int fuse) {
        bombs.add(new Active_Bomb(bombTemplate, centerPos, scaleX, scaleY, fuse, this));
        bombs.get(bombs.size() - 1).load(group, this, bombs);
    }

    public void addBomb(Group group, String bombTemplate, Vecc2f centerPos) {
        addBombSub(group, bombTemplate, centerPos, 3);
    }

    //public void addNewArcTear(int damage, Vecc2f startPos, Vecc2f endPos, Group parentGroup, Player playerTarget) {
    //    tears.add(new Arc_Tear(damage, startPos, endPos, scaleX, scaleY, tears, enemies, bosses, parentGroup));
    //}

    public void addNewPlayerTear(String lookingDirection, int damage, int tearSize, Group group, Vecc2f pos, Vecc2f velocity, float scaleX, float scaleY, float veloLimit) {
        tears.add(new Tear_Player(lookingDirection, damage, tearSize, group, pos, velocity, scaleX, scaleY, veloLimit, tears, enemies, bosses, getAllBoundaries(), bombs));
    }

    public void addNewEnemyTear(int damage, int tearSize, Group group, Vecc2f position, Vecc2f velocity, float scaleX, float scaleY, float veloLimit) {
        tears.add(new Tear_Enemy(damage, tearSize, group, position, velocity, scaleX, scaleY, veloLimit, tears, getAllBoundaries(), playerTarget));
    }

    public void explosionDamageAroundPoint(float x, float y, int radius, Group group) {
        radius *= ((scaleX + scaleY) / 2);

        if (Vecc2f.distance(x, y, Player.centerPos.x, Player.centerPos.y) < radius) {//player check - player will be pushed away from bomb & damaged
            Vecc2f dir = new Vecc2f(Player.centerPos).sub(new Vecc2f(x, y));
            dir.limit(1);
            System.out.println(dir);
            System.out.println("player hit");
            playerTarget.inflictDamage(1);
            playerTarget.applyForce(dir, 40);
        }

        for (Active_Bomb bomb : bombs) {//force applied to other active bombs in room
            if ((Vecc2f.distance(x, y, bomb.centerPos.x, bomb.centerPos.y) < radius) && !((bomb.centerPos.x == x) && (bomb.centerPos.y == y))) {
                Vecc2f dir = new Vecc2f(bomb.centerPos).sub(new Vecc2f(x, y));
                dir.limit(1);
                bomb.applyForce(dir, 10);
            }
        }

        {//rock checker
            //checks all rocks and rock parts if they're to be destroyed
            for (Rock rock : rocks) {
                rock.check(group, x, y, radius);
                if (rock.rock_parts.size() == 0) {
                    System.out.println("rock destroyed");
                    rock.markedDelete = true;
                }
                //System.out.println(rock.rock_parts.size());
            }
            rocks.removeIf(rock -> rock.markedDelete);
        }
        {//enemies
            for (int k = enemies.size() - 1; k >= 0; k--) {//Reverse - to avoid concurrent exceptions
                if ((Vecc2f.distance(x, y, enemies.get(k).centerPos.x, enemies.get(k).centerPos.y) < (radius * 0.8))) {
                    //
                    Vecc2f dir = new Vecc2f(enemies.get(k).centerPos).sub(new Vecc2f(x, y));
                    dir.limit(1);
                    enemies.get(k).applyForce(dir, 30);
                    //
                    enemies.get(k).inflictDamage(5, group, enemies);//TODO Remember bomb default damage is 5

                }
            }
        }
        //
        {//bosses
            for (int k = bosses.size() - 1; k >= 0; k--) {//Reverse - to avoid concurrent exceptions
                if ((Vecc2f.distance(x, y, bosses.get(k).centerPos.x, bosses.get(k).centerPos.y) < (radius * 0.8))) {
                    //
                    Vecc2f dir = new Vecc2f(bosses.get(k).centerPos).sub(new Vecc2f(x, y));
                    dir.limit(1);
                    bosses.get(k).applyForce(dir, 10);
                    //
                    bosses.get(k).inflictDamage(5, group, bosses);//TODO Remember bomb default damage is 5

                }
            }
        }
        //
        for (Item item : items) {//item checker - items in range will be pushed away from bomb
            if ((Vecc2f.distance(x, y, item.centerPos.x, item.centerPos.y) < (radius * 0.8))) {
                Vecc2f dir = new Vecc2f(item.centerPos).sub(new Vecc2f(x, y));
                dir.limit(1);
                item.applyForce(dir, 10);
            }
        }
        for (Door door : doors) {//doors in range will  have their frame damaged and be opened if closed (not locked)
            if (Vecc2f.distance(x, y, door.centerPos.x, door.centerPos.y) < (int) (radius * 0.8)) {
                door.blowUp(group);
            }
        }
    }

    public void explosionDamageAroundPoint(Vecc2f point, int radius, Group group) {
        explosionDamageAroundPoint(point.x, point.y, radius, group);
    }

    public void newRealTimeProp(Group group, float centerX, float centerY, Image RealTimeProp) {
        newRealTimeProp(group, centerX, centerY, RealTimeProp, 1.0);
    }

    public void newRealTimeProp(Group group, float centerX, float centerY, Image RealTimeProp, double opacity) {
        this.backgroundItems.newRealTimeProp(group, centerX, centerY, RealTimeProp, opacity);
    }

    public void newRealTimeEnemy(String type, String enemyName, Vecc2f pos, Group group) {
        newRealTimeEnemy(type, enemyName, pos, group, 0);
    }

    public void newRealTimeEnemy(String type, String enemyName, Vecc2f pos, Group group, int rotate) {
        JsonObject enemytemplate = new JsonParser().parse(String.valueOf(templateGetterSub("src\\resources\\gfx\\monsters\\" + type + "\\" + enemyName + ".json"))).getAsJsonObject();
        newRealTimeEnemySub(enemyName, enemytemplate, pos, group, rotate);
    }

    public void newRealTimeEnemySub(String enemyName, JsonObject enemytemplate, Vecc2f pos, Group group, int optionalRotate) {
        //position is unscaled so that it can be rescaled by the Enemy base class
        Vecc2f newpos = new Vecc2f(pos);
        newpos.div(scaleX, scaleY);
        switch (enemyName) {
            case "fly" -> enemies.add(new Enemy_Fly(enemytemplate, newpos, scaleX, scaleY, screenBounds, shading, this));
            case "attack fly" -> enemies.add(new Enemy_AttackFly(enemytemplate, newpos, scaleX, scaleY, screenBounds, shading, this));
            case "pooter" -> enemies.add(new Enemy_Pooter(enemytemplate, newpos, scaleX, scaleY, screenBounds, shading, this));
            case "spider" -> enemies.add(new Enemy_Spider(enemytemplate, newpos, scaleX, scaleY, screenBounds, shading, this));
            case "gaper" -> enemies.add(new Enemy_Gaper(enemytemplate, newpos, scaleX, scaleY, screenBounds, shading, this, optionalRotate));
        }
        enemies.get(enemies.size() - 1).load(group, this.playerTarget);
        smokeClouds.add(new Smoke(enemies.get(enemies.size() - 1).getCenterPos(), group, smokeClouds, scaleX, scaleY, this));
        Music.addSFX(false, 15, Music.sfx.enemy_appear_smoke_1, Music.sfx.enemy_appear_smoke_2, Music.sfx.enemy_appear_smoke_3, Music.sfx.enemy_appear_smoke_4);
    }

    public void newRealTimeBoss(String type, String bossName, Vecc2f pos, Group group) {
        JsonObject bossTemplate = new JsonParser().parse(String.valueOf(templateGetterSub("src\\resources\\gfx\\bosses\\" + type + "\\" + bossName + ".json"))).getAsJsonObject();
        newRealtimeBossSub(bossName, bossTemplate, pos, group);
    }

    public void newRealtimeBossSub(String bossName, JsonObject bossTemplate, Vecc2f pos, Group group) {
        //position is unscaled so that it can be rescaled by the Boss
        Vecc2f newpos = new Vecc2f(pos);
        newpos.div(scaleX, scaleY);
        switch (bossName) {
            case "fistula" -> bosses.add(new Boss_Fistula(bossTemplate, newpos, scaleX, scaleY, screenBounds, shading, this));
        }
        bosses.get(bosses.size() - 1).load(group, this.playerTarget);
    }

    public ArrayList<Rectangle> getBoundaries() {//provides an arraylist of obstacles.
        ArrayList<Rectangle> a = new ArrayList<>(background.getBoundaries());
        for (Door door : doors) {
            if (door.getState() == Door.State.closed || door.getState() == Door.State.locked) {
                a.add(door.getDoorBlock());
            }
        }

        return a;
    }

    public ArrayList<Rectangle> getAllBoundaries() {//provides an arraylist of obstacles.
        ArrayList<Rectangle> a = new ArrayList<>(getBoundaries());
        for (Door door : doors) {
            a.add(door.getDoorTrigger());
        }
        //
        for (Rock rock : rocks) {//each rock gets its parts hitboxes.
            for (int k = 0; k < rock.rock_parts.size(); k++) {
                a.add((Rectangle) rock.rock_parts.get(k).hitbox.getShape());
            }
        }
        return a;
    }

    public ArrayList<Rectangle> getTopBottomBounds() {
        ArrayList<Rectangle> a = new ArrayList(background.get_TOP_BOTTOM_boundaries());
        //
        for (Door door : doors) {
            if (door.direction.equalsIgnoreCase("up") || door.direction.equalsIgnoreCase("down")) {
                a.add(door.getDoorBlock());
            }
        }
        //
        return a;
    }

    public ArrayList<Rectangle> getLeftRightBounds() {
        ArrayList<Rectangle> a = new ArrayList(background.get_LEFT_RIGHT_boundaries());
        //
        for (Door door : doors) {
            if (door.direction.equalsIgnoreCase("left") || door.direction.equalsIgnoreCase("right")) {
                a.add(door.getDoorBlock());
            }
        }
        //
        return a;
    }

    public void openDoors(Group group) {//opens doors that are closed because of enemies - wont open locked doors
        for (Door door : doors) {
            door.open(group);
        }
    }

    public void forceOpenDoors(Group group) {//forces all doors to open
        for (Door door : doors) {
            door.forceOpen(group);
        }
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFloorLevel() {
        return floorLevel;
    }

    public Door getTrapDoor() {
        return trapDoor;
    }

    public JsonObject getRoomTemplate() {
        return roomTemplate;
    }

    public String getMusic() {
        return music;
    }

    public void setRoomTemplate(JsonObject roomTemplate) {
        this.roomTemplate = roomTemplate;
    }

    public void displayNeighbours() {
        System.out.println("Up: " + this.upType + " Down: " + this.downType + " Left: " + this.leftType + " Right: " + this.rightType);
    }

    @Override
    public String toString() {
        return "Room";
    }

    public void checkDoors(Group group) {
        if (enemies.size() == 0 && bosses.size() == 0) {
            openDoors(group);
        }
    }

    public void pause() {
        for (int k = 0; k < enemies.size(); k++) {
            enemies.get(k).timeline.pause();
        }
    }

    public void beginFloorTransition() {
        parentDungeon.beginFloorTransition();
    }
}