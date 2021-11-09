package sample;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Room implements Runnable {
    public static int finishedRoom;
    //
    public Thread t;
    private final String threadName;
    //

    Random random = new Random();
    Background background;
    Shading shading;

    int i;
    int j;
    int type;
    int upType, downType, leftType, rightType;
    int floorLevel;
    float scaleX, scaleY;
    Rectangle2D screenBounds;
    //
    JsonObject roomTemplate = null;
    Background_items backgroundItems;
    ArrayList<Door> doors = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<Rock> rocks = new ArrayList<>();
    //
    Door trapDoor;
    //
    String parentThreadName;
    //ShadingThread shadingThread;

    public Room(int i, int j, int type, int up, int down, int left, int right, int floorLevel, float scaleX, float scaleY, Rectangle2D screenBounds, String threadName, Shading shading) {
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
        enemyAdder(this.roomTemplate.getAsJsonArray("enemies"), scaleX, scaleY, screenBounds, shading);
        System.out.println("Thread: " + threadName + " Enemies Complete");
        //
        rockAdder(this.roomTemplate.getAsJsonObject("Rocks"), scaleX, scaleY);
        System.out.println("Thread: " + threadName + " Rocks Complete");
        //
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
        int width, height, rows, columns, borderX, borderY;
        float sheetScale;
        String name = rockTemplate.get("name").getAsString();
        sheetScale = rockTemplate.get("SheetScale").getAsFloat();
        width = rockTemplate.get("Width").getAsInt();
        height = rockTemplate.get("Height").getAsInt();
        rows = rockTemplate.get("Rows").getAsInt();
        columns = rockTemplate.get("Columns").getAsInt();
        borderX = rockTemplate.get("BorderX").getAsInt();
        borderY = rockTemplate.get("BorderY").getAsInt();
        for (int k = 0; k < rockTemplate.get("rocksARR").getAsJsonArray().size(); k++) {
            int a = rockTemplate.get("rocksARR").getAsJsonArray().get(k).getAsJsonObject().get("PositionX").getAsInt();
            int b = rockTemplate.get("rocksARR").getAsJsonArray().get(k).getAsJsonObject().get("PositionY").getAsInt();
            int c = rockTemplate.get("rocksARR").getAsJsonArray().get(k).getAsJsonObject().get("ImageX").getAsInt();
            int d = rockTemplate.get("rocksARR").getAsJsonArray().get(k).getAsJsonObject().get("ImageY").getAsInt();
            rocks.add(new Rock(a, b, c, d, name, sheetScale, width, height, rows, columns, borderX, borderY, scaleX, scaleY));
        }
    }

    private void enemyAdder(JsonArray enemyArray, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading) {
        for (int k = 0; k < enemyArray.size(); k++) {

            JsonObject a = new JsonParser().parse(String.valueOf(templateGetterSub("src\\resources\\gfx\\monsters\\classic\\" + enemyArray.get(k).getAsJsonObject().get("enemy").getAsString() + ".json"))).getAsJsonObject();
            Vecc2f pos = new Vecc2f(enemyArray.get(k).getAsJsonObject().get("PositionX").getAsInt(), enemyArray.get(k).getAsJsonObject().get("PositionY").getAsInt());

            switch (enemyArray.get(k).getAsJsonObject().get("enemy").getAsString()) {
                case "fly" -> enemies.add(new Enemy_Fly(a, pos, scaleX, scaleY, screenBounds, shading));
                case "attack fly" -> enemies.add(new Enemy_attackFly(a, pos, scaleX, scaleY, screenBounds, shading));
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
        //
        for (Door door : doors) {
            door.load(group);
        }
        /*
        //groundwork for when a boss is defeated.
        if (type==3){
            trapDoor.loadTrapDoor(group);
        }
         */
        //

        for (Enemy enemy : enemies) {
            enemy.load(group);
        }
        //
        for (Rock rock : rocks) {
            rock.load(group);
        }

    }

    public void unload(Group group) {
        this.shading.unload(group);
        this.background.unload(group);
        this.backgroundItems.unload(group);
        //
        for (Door door : doors) {
            door.unload(group);
        }
        //
        for (Enemy enemy : enemies) {
            enemy.unload(group);
        }
        //
        for (Rock rock : rocks) {
            rock.unload(group);
        }
    }

    public ArrayList<Rectangle> getBoundaries() {//provides an arraylist of obstacles.
        ArrayList<Rectangle> a = new ArrayList<>(background.getBoundaries());
        for (Door door : doors) {
            if (door.getState() == Door.State.closed) {
                a.add(door.getDoorBlock());
            }
        }
        for (Rock rock : rocks) {
            a.add((Rectangle) rock.hitbox.shape);
        }
        return a;
    }

    /*
        public ArrayList<Rectangle> getDoorTriggers() {//provides an arraylist of doorTriggers
           ArrayList<Rectangle> a = new ArrayList<>();
            for (Door door : doors) {
                a.add(door.getDoorTrigger());
            }
        return a;
        }
        */
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


}