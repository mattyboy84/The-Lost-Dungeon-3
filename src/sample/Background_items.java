package sample;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.Random;

public class Background_items {

    ArrayList<Props> props = new ArrayList<>();
    int propNumber = 11;

    Random random = new Random();

    public Background_items() {
    }

    public void addProps(JsonObject props, float scaleX, float scaleY, Rectangle2D screenBounds) {
        for (int i = 0; i < propNumber + random.nextInt(5); i++) {
            this.props.add(new Props(props, scaleX, scaleY, screenBounds));

        }


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
}