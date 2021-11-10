package sample;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class Item {

    Hitbox hitbox;
    Vecc2f position;
    ImageView item;

    public Item() {

    }


    public Item(JsonObject a, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds) {
        int sheetScale = a.get("SheetScale").getAsInt();
        int startX = a.get("StartX").getAsInt();
        int startY = a.get("StartY").getAsInt();
        int width = a.get("Width").getAsInt();
        int height = a.get("Height").getAsInt();

        this.position = new Vecc2f(pos.x, pos.y);

        String file = "file:src\\resources\\gfx\\items\\pick ups\\" + a.get("Sprite").getAsString() + ".png";
        this.item = (new ImageView(new WritableImage(new Image(file, ((new Image(file).getWidth() * scaleX * sheetScale)),
                ((new Image(file).getHeight() * scaleY * sheetScale)), false, false).getPixelReader(),
                (int) ((startX * sheetScale * scaleX)), (int) ((startY * sheetScale * scaleY)), (int) (width * scaleX * sheetScale), (int) (height * scaleY * sheetScale))));

        this.hitbox = new Hitbox(a.getAsJsonObject("Hitbox"), sheetScale, scaleX, scaleY);

        //this.item = new ImageView("file:src\\resources\\gfx\\items\\pick ups\\" + a.get("Sprite").getAsString() + ".png");
    }

    public void load(Group group) {
        group.getChildren().addAll(this.item, this.hitbox.getShape());
        this.item.relocate(this.position.x, this.position.y);
        this.item.setViewOrder(-4);
        //
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
        this.hitbox.getShape().setViewOrder(-4);
        this.hitbox.getShape().setVisible(false);
    }

    public void unload(Group group) {
        group.getChildren().removeAll(this.item,this.hitbox.getShape());

    }
}
