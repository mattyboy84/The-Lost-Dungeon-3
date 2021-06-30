package sample;

import com.google.gson.JsonObject;
import javafx.scene.image.ImageView;

public class Background {

    JsonObject backgroundTemplate = null;
    //
    ImageView spriteSheet;
    String name;
    int width, height, rows, columns;
    //


    public Background(JsonObject background) {
        this.backgroundTemplate = background;
        this.name = background.get("name").getAsString();
        this.width = background.get("Width").getAsInt();
        this.height = background.get("Height").getAsInt();
        this.rows = background.get("Rows").getAsInt();
        this.columns = background.get("Columns").getAsInt();
        //
        this.spriteSheet = new ImageView("file:src\\resources\\gfx\\backdrop\\" + this.name + ".png");

    }


}
