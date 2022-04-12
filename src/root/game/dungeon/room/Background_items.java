package root.game.dungeon.room;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class Background_items {

    ArrayList<Props> props = new ArrayList<>();

    Random random = new Random();

    public Background_items() {
    }

    public void addProps(JsonObject props, float scaleX, float scaleY, Rectangle2D screenBounds, int borderX, int borderY) {

        JsonObject propsSheetTemplate = new JsonParser().parse(String.valueOf(templateGetterSub("src\\resources\\gfx\\grid\\" + props.get("name").getAsString() + ".json"))).getAsJsonObject();

        for (int i = 0; i < props.get("Number").getAsInt() + random.nextInt(props.get("RandNumber").getAsInt()); i++) {
            this.props.add(new Props(props.get("name").getAsString(),propsSheetTemplate, scaleX, scaleY, screenBounds,borderX,borderY));
        }
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
        for (Props prop : props) {
            prop.load(group);
        }
    }

    public void unload(Group group) {
        for (Props prop : props) {
            prop.unload(group);
        }
    }

    public void newRealTimeProp(Group group, float centerX, float centerY, Image image, double opacity) {
        this.props.add(new Props(image, centerX, centerY, group, opacity));
    }
}