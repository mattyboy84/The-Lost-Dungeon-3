package sample;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Room {

    Random random = new Random();
    Background background;
    Shading shading;

    int i;
    int j;
    int type;
    int upType, downType, leftType, rightType;
    int floorLevel;
    //
    JsonObject roomTemplate = null;
    Background_items backgroundItems;
    ArrayList<Door> doors = new ArrayList<Door>();
    ArrayList<Enemy> enemies = new ArrayList<>();

    public Room(int i, int j, int type, int up, int down, int left, int right, int floorLevel, float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.backgroundItems = new Background_items();
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


        //
        this.floorLevel = floorLevel;
        this.roomTemplate = new JsonParser().parse(String.valueOf(templateGetter())).getAsJsonObject();

        //System.out.println(this.roomTemplate.getAsJsonObject("Background"));
        //
        
        this.background = new Background(this.roomTemplate.getAsJsonObject("Background"), scaleX, scaleY, screenBounds);
        this.shading = new Shading(scaleX, scaleY, screenBounds);
        this.backgroundItems.addProps(this.roomTemplate.getAsJsonObject("Props"), scaleX, scaleY, screenBounds);
        enemyAdder(this.roomTemplate.getAsJsonArray("enemies"),scaleX,scaleY,screenBounds);
        //
        //213 x 180

        if (upType > 0) {
            doors.add(new Door("up", 0, this.upType, this.type, scaleX, scaleY, screenBounds));
        }
        if (downType > 0) {
            doors.add(new Door("down", 180, this.downType, this.type, scaleX, scaleY, screenBounds));
        }
        if (leftType > 0) {
            doors.add(new Door("left", 270, this.leftType, this.type, scaleX, scaleY, screenBounds));
        }
        if (rightType > 0) {
            doors.add(new Door("right", 90, this.rightType, this.type, scaleX, scaleY, screenBounds));
        }

        //System.out.println(roomTemplate);
    }

    private void enemyAdder(JsonArray enemyArray, float scaleX, float scaleY, Rectangle2D screenBounds) {
        for (int k = 0; k <enemyArray.size() ; k++) {
            switch (enemyArray.get(k).getAsJsonObject().get("enemy").getAsString()) {
                case "fly" -> enemies.add(new Enemy_Fly(enemyArray.get(k).getAsJsonObject(), enemyArray.get(k).getAsJsonObject().get("PositionX").getAsInt(), enemyArray.get(k).getAsJsonObject().get("PositionY").getAsInt(),scaleX,scaleY,screenBounds));
                case "attack fly" -> enemies.add(new Enemy_attackFly(enemyArray.get(k).getAsJsonObject(), enemyArray.get(k).getAsJsonObject().get("PositionX").getAsInt(), enemyArray.get(k).getAsJsonObject().get("PositionY").getAsInt(),scaleX,scaleY,screenBounds));
            }
        }
        //System.out.println(scaleX);
    }

    private StringBuilder templateGetter() {
        StringBuilder json = new StringBuilder();

        File directPath = new File("src\\room templates\\Floor-" + this.floorLevel + "\\Type-" + this.type);
        //System.out.println(directPath);
        String[] contents = directPath.list();
        //System.out.println(contents.length);
        String room = null;
        if (contents != null) {
            room = contents[random.nextInt(contents.length)];
        }
        try {
            File file = new File("src\\room templates\\Floor-" + this.floorLevel + "\\Type-" + this.type + "\\" + room);
            //System.out.println(file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                json.append(st);
                //System.out.println(st);
            }

            //System.out.println(json);
        } catch (Exception e) {
            System.out.println("Cannot find room template - Room");
        }
        //System.out.println(json);
        return json;
    }

    public void load(Group group) {
        this.background.load(group);
        this.shading.load(group);
        this.backgroundItems.load(group);
        //
        for (Door door : doors) {
            door.load(group);
        }
        //
        for (Enemy enemy : enemies) {
            enemy.load(group);
        }
    }

    public void unload(Group group) {
        this.background.unload(group);
        this.shading.unload(group);
        this.backgroundItems.unload(group);
        //
        for (Door door : doors) {
            door.unload(group);
        }
        //
        for (Enemy enemy : enemies) {
            enemy.unload(group);
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