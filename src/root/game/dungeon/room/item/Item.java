package root.game.dungeon.room.item;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import root.game.player.Player;
import root.game.util.Hitbox;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;
import java.util.ArrayList;

public class Item implements Sprite_Splitter {

    Hitbox hitbox;
    Vecc2f position;
    Vecc2f velocity;
    ImageView item=new ImageView();
    int effect;
    float scaleX, scaleY;
    int sheetScale;

    public Item() {

    }

    public Item(JsonObject a, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.sheetScale = a.get("SheetScale").getAsInt();
        int startX = a.get("StartX").getAsInt();
        int startY = a.get("StartY").getAsInt();
        int width = a.get("Width").getAsInt();
        int height = a.get("Height").getAsInt();
        //
        this.effect = a.get("Effect").getAsInt();

        this.position = new Vecc2f(((213 + pos.x) * scaleX), ((180 + pos.y) * scaleY));

        String file = "file:src\\resources\\gfx\\items\\pick ups\\" + a.get("Sprite").getAsString() + ".png";
        this.item.setImage(imageGetter(file, startX, startY, width, height,scaleX,scaleY,sheetScale));
        //
        //rooms are made up of 103 x 103 'grids' the rocks obey this rule and now items are centered in that grid to match the format.
        int x = (int) ((103*scaleX)-(this.item.getBoundsInParent().getWidth()))/2;
        int y = (int) ((103*scaleY)-(this.item.getBoundsInParent().getHeight()))/2;
        this.position.add(x,y);

        this.hitbox = new Hitbox(a.getAsJsonObject("Hitbox"), sheetScale, scaleX, scaleY);
    }

    public void load(Group group) {
        group.getChildren().addAll(this.item, this.hitbox.getShape());
        this.item.relocate(this.position.x, this.position.y);
        this.item.setViewOrder(-4);
        //
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
        this.hitbox.getShape().setViewOrder(-4);
        this.hitbox.getShape().setVisible(false);
        //
        postLoader(group);
    }

    public void postLoader(Group group) {

    }

    public void unload(Group group) {
        group.getChildren().removeAll(this.item, this.hitbox.getShape());
        //
        postUnLoader(group);
    }

    protected void postUnLoader(Group group) {

    }

    public void checkCollision(Player player, ArrayList<Item> items, Group group) {
    }
}
