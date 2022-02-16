package root.game.dungeon.room;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import root.Main;
import root.game.dungeon.Dungeon;
import root.game.dungeon.room.enemy.*;
import root.game.dungeon.Shading;
import root.game.dungeon.room.item.*;
import root.game.music.Music;
import root.game.player.Player;
import root.game.player.Tear;
import root.game.util.Vecc2f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
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

    int i;
    int j;
    int type;
    int upType, downType, leftType, rightType;
    int floorLevel;
    float scaleX, scaleY;
    boolean skipEnemy;
    Rectangle2D screenBounds;
    //
    JsonObject roomTemplate = null;
    Background_items backgroundItems;
    public ArrayList<Door> doors = new ArrayList<>();
    public ArrayList<Enemy> enemies = new ArrayList<>();
    public ArrayList<Item> items = new ArrayList<>();
    public ArrayList<Rock> rocks = new ArrayList<>();
    //
    ArrayList<Active_Bomb> bombs = new ArrayList<>();
    public ArrayList<Tear> tears = new ArrayList<Tear>();
    //
    String music = null;

    //
    Door trapDoor;
    //
    String parentThreadName;
    //ShadingThread shadingThread;

    public Room(int i, int j, int type, int up, int down, int left, int right, int floorLevel, float scaleX, float scaleY, Rectangle2D screenBounds, String threadName, Shading shading, boolean enemyOverrise) {
        //
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
        this.skipEnemy = enemyOverrise;
    }

    public Room() {
        threadName = "";
    }

    public void run() {
        this.backgroundItems = new Background_items();
        //
        this.roomTemplate = new JsonParser().parse(String.valueOf(roomTemplateGetter("src\\room templates\\Floor-" + this.floorLevel + "\\Type-" + this.type))).getAsJsonObject();
        //
        this.background = new Background(this.roomTemplate.getAsJsonObject("Background"), scaleX, scaleY, screenBounds);
        System.out.println("Thread: " + threadName + " Background Complete");
        //
        this.backgroundItems.addProps(this.roomTemplate.getAsJsonObject("Props"), scaleX, scaleY, screenBounds);
        System.out.println("Thread: " + threadName + " BackgroundItems Complete");
        //
        itemAdder(this.roomTemplate.getAsJsonArray("items"), scaleX, scaleY, screenBounds);
        System.out.println("Thread: " + threadName + " Items Complete");
        //
        rockAdder(this.roomTemplate.getAsJsonObject("Rocks"), scaleX, scaleY);
        System.out.println("Thread: " + threadName + " Rocks Complete");
        //
        if (!skipEnemy) {
            enemyAdder(this.roomTemplate.getAsJsonArray("enemies"), scaleX, scaleY, screenBounds, shading);
            System.out.println("Thread: " + threadName + " Enemies Complete");
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
            trapDoor = new Door(scaleX, scaleY, screenBounds);
        }
        System.out.println("Thread: " + threadName + " Doors Complete");
        //
        finishedRoom += 1;
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

    private void itemAdder(JsonArray itemsArray, float scaleX, float scaleY, Rectangle2D screenBounds) {
        for (int k = 0; k < itemsArray.size(); k++) {

            JsonObject a = new JsonParser().parse(String.valueOf(templateGetterSub("src\\resources\\gfx\\items\\pick ups\\" + itemsArray.get(k).getAsJsonObject().get("item").getAsString() + ".json"))).getAsJsonObject();
            Vecc2f pos = new Vecc2f(itemsArray.get(k).getAsJsonObject().get("PositionX").getAsInt(), itemsArray.get(k).getAsJsonObject().get("PositionY").getAsInt());

            switch (itemsArray.get(k).getAsJsonObject().get("item").getAsString()) {
                //TODO might be able to combine unique items (heart & half-heart etc) into one class - depends on how different they become
                case "coin" -> items.add(new Item_Coin(a, pos, scaleX, scaleY, screenBounds));

                case "key" -> items.add(new Item_Key(a, pos, scaleX, scaleY, screenBounds));
                case "double-key" -> items.add(new Item_DoubleKey(a, pos, scaleX, scaleY, screenBounds));

                case "bomb" -> items.add(new Item_Bomb(a, pos, scaleX, scaleY, screenBounds));
                case "double-bomb" -> items.add(new Item_DoubleBomb(a, pos, scaleX, scaleY, screenBounds));

                case "heart" -> items.add(new Item_Heart(a, pos, scaleX, scaleY, screenBounds));
                case "half-heart" -> items.add(new Item_HalfHeart(a, pos, scaleX, scaleY, screenBounds));
                case "double-heart" -> items.add(new Item_DoubleHeart(a, pos, scaleX, scaleY, screenBounds));
            }
        }
    }

    private void enemyAdder(JsonArray enemyArray, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading) {
        for (int k = 0; k < enemyArray.size(); k++) {

            JsonObject enemytemplate = new JsonParser().parse(String.valueOf(templateGetterSub("src\\resources\\gfx\\monsters\\"+enemyArray.get(k).getAsJsonObject().get("type").getAsString()+"\\" + enemyArray.get(k).getAsJsonObject().get("enemy").getAsString() + ".json"))).getAsJsonObject();
            Vecc2f pos = new Vecc2f(enemyArray.get(k).getAsJsonObject().get("PositionX").getAsInt(), enemyArray.get(k).getAsJsonObject().get("PositionY").getAsInt());
            switch (enemyArray.get(k).getAsJsonObject().get("enemy").getAsString()) {
                case "fly" -> enemies.add(new Enemy_Fly(enemytemplate, pos, scaleX, scaleY, screenBounds, shading, this));
                case "attack fly" -> enemies.add(new Enemy_AttackFly(enemytemplate, pos, scaleX, scaleY, screenBounds, shading, this));
                case "pooter" -> enemies.add(new Enemy_Pooter(enemytemplate, pos, scaleX, scaleY, screenBounds, shading, this));
            }
        }
    }

    private StringBuilder roomTemplateGetter(String file1) {
        File directPath = new File(file1);
        String[] contents = directPath.list();
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

    public void load(Group group) {
        this.shading.load(group);
        this.background.load(group);
        this.backgroundItems.load(group);
        //will attempt to add the music to the array
        Music.addMusic(this.music,true,this.hashCode());
        //
        for (Enemy enemy : enemies) {
            enemy.load(group);
        }
        /*
        //base work for when a boss is defeated.
        if (type==3){
            trapDoor.loadTrapDoor(group);
        }
         */
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
        this.background.unload(group);
        this.backgroundItems.unload(group);
        //
        for (Enemy enemy : enemies) {
            enemy.unload(group);
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
    }

    public void addBombSub(Group group, String bombTemplate, Vecc2f centerPos, int fuse) {
        bombs.add(new Active_Bomb(bombTemplate, centerPos, scaleX, scaleY, fuse));
        bombs.get(bombs.size() - 1).load(group, this, bombs);
    }

    public void addBomb(Group group, String bombTemplate, Vecc2f centerPos) {
        addBombSub(group, bombTemplate, centerPos, 3);
    }

    public void addNewTear(String direction, int damage, Group group, Vecc2f pos, Vecc2f velocity, float scaleX, float scaleY, float veloLimit, Tear.Target tearTarget) {
        tears.add(new Tear(direction, damage, group, pos, velocity, scaleX, scaleY, veloLimit, tears, enemies, getAllBoundaries(), tearTarget));
    }

    public void explosionDamageAroundPoint(Active_Bomb currentBomb, float x, float y, int radius, Group group) {
        radius *= ((scaleX + scaleY) / 2);

        if (Vecc2f.distance(x, y, Player.centerPos.x, Player.centerPos.y) < radius) {//player check - player will be pushed away from bomb & damaged
            Vecc2f dir = new Vecc2f(Player.centerPos).sub(new Vecc2f(x, y));
            dir.limit(1);
            System.out.println(dir);
            System.out.println("player hit");
            Main.player.inflictDamage(-1);
            Main.player.applyForce(dir, 40);
        }

        for (Active_Bomb bomb : bombs) {//force applied to other active bombs in room
            if ((Vecc2f.distance(x, y, bomb.centerPos.x, bomb.centerPos.y) < radius) && (currentBomb != bomb)) {
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
        {//
            for (Enemy enemy : enemies) {
                enemy.inflictDamage(5, group, enemies);//TODO Remember bomb default damage is 5
                //
                Vecc2f dir = new Vecc2f(enemy.centerPos).sub(new Vecc2f(x, y));
                dir.limit(1);
                enemy.applyForce(dir, 30);
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

    public void explosionDamageAroundPoint(Active_Bomb bomb, Vecc2f point, int radius, Group group) {
        explosionDamageAroundPoint(bomb, point.x, point.y, radius, group);
    }

    public void newRealTimeProp(Group group, float centerX, float centerY, Image RealTimeProp) {
        newRealTimeProp(group, centerX, centerY, RealTimeProp, 1.0);
    }

    public void newRealTimeProp(Group group, float centerX, float centerY, Image RealTimeProp, double opacity) {
        this.backgroundItems.newRealTimeProp(group, centerX, centerY, RealTimeProp, opacity);
    }

    public ArrayList<Rectangle> getBoundaries() {//provides an arraylist of obstacles.
        ArrayList<Rectangle> a = new ArrayList<>(background.getBoundaries());
        for (Door door : doors) {
            if (door.getState() == Door.State.closed || door.getState() == Door.State.locked) {
                a.add(door.getDoorBlock());
            }
        }
        for (Rock rock : rocks) {//each rock gets its parts hitboxes.
            for (int k = 0; k < rock.rock_parts.size(); k++) {
                a.add((Rectangle) rock.rock_parts.get(k).hitbox.getShape());
            }
        }

        return a;
    }

    public ArrayList<Rectangle> getAllBoundaries() {//provides an arraylist of obstacles.
        ArrayList<Rectangle> a = new ArrayList<>(getBoundaries());
        for (Door door : doors) {
            a.add(door.getDoorTrigger());
        }
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

    public void setFloorLevel(int floorLevel) {
        this.floorLevel = floorLevel;
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
        System.out.println("UP: " + this.upType + " Down: " + this.downType + " Left: " + this.leftType + " Right: " + this.rightType);
    }

    @Override
    public String toString() {
        return "Room";
    }

    public void checkDoors(Group group) {
        if (enemies.size() == 0) {
            openDoors(group);
        }
    }
}