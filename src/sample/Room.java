package sample;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

public class Room {

    Random random = new Random();
    Background background;

    int i;
    int j;
    int type;
    int floorLevel;
    JsonObject roomTemplate = null;

    public Room(int i, int j, int type, int floorLevel, float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.i = i;
        this.j = j;
        this.type = type;
        this.floorLevel = floorLevel;
        this.roomTemplate = new JsonParser().parse(String.valueOf(templateGetter())).getAsJsonObject();

        //System.out.println(this.roomTemplate.getAsJsonObject("Background"));

        this.background = new Background(this.roomTemplate.getAsJsonObject("Background"), scaleX, scaleY,screenBounds);

        //System.out.println(roomTemplate);
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
}