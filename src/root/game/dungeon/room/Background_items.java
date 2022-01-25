package root.game.dungeon.room;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import root.game.dungeon.room.Props;

import java.util.ArrayList;
import java.util.Random;

public class Background_items {

    ArrayList<Props> props = new ArrayList<>();

    Random random = new Random();

    public Background_items() {
    }

    public void addProps(JsonObject props, float scaleX, float scaleY, Rectangle2D screenBounds) {
        if (props.get("Prop").getAsBoolean()) {
            for (int i = 0; i < props.get("Number").getAsInt() + random.nextInt(props.get("RandNumber").getAsInt()); i++) {
                this.props.add(new Props(props, scaleX, scaleY, screenBounds));
            }
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

    public void newRealTimeProp(Group group, float centerX, float centerY, Image image,double opacity) {
        this.props.add(new Props(image,centerX,centerY,group,opacity));

    }
}