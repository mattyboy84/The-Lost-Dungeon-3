package root.game.dungeon.room.item;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import root.Main;
import root.game.dungeon.room.Room;
import root.game.player.Player;
import root.game.util.Hitbox;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public abstract class Item implements Sprite_Splitter {

    Hitbox hitbox;
    Vecc2f position;
    Vecc2f velocity = new Vecc2f();
    public Vecc2f centerPos = new Vecc2f();
    ImageView item = new ImageView();
    int effect;
    float scaleX, scaleY;
    int sheetScale;

    Timeline forceListener;
    Room parentRoom;

    public Item() {

    }

    public Item(JsonObject a, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Room parentRoom) {
        this.parentRoom=parentRoom;
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
        this.item.setImage(imageGetter(file, startX, startY, width, height, scaleX, scaleY, sheetScale));
        //
        //rooms are made up of 103 x 103 'grids' the rocks obey this rule and now items are centered in that grid to match the format.
        int x = (int) ((103 * scaleX) - (this.item.getBoundsInParent().getWidth())) / 2;
        int y = (int) ((103 * scaleY) - (this.item.getBoundsInParent().getHeight())) / 2;
        this.position.add(x, y);

        this.hitbox = new Hitbox(a.getAsJsonObject("Hitbox"), sheetScale, scaleX, scaleY);

        forceListenerSetup();
        this.centerPos.set(this.hitbox.getCenterX(), this.hitbox.getCenterY());
    }

    private void forceListenerSetup() {
        forceListener = new Timeline(new KeyFrame(Duration.seconds((float) 1 / 60), event -> {
            collisionCheck();
            this.position.add(this.velocity);
            this.velocity.mult(0.95);
            if (this.velocity.magnitude() < 0.2) {
                this.velocity.set(0, 0);
            }
            relocate();

        }));
        forceListener.setCycleCount(Timeline.INDEFINITE);
    }

    public void relocate() {
        this.item.relocate(this.position.x, this.position.y);
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
        this.centerPos.set(this.hitbox.getCenterX(), this.hitbox.getCenterY());
    }

    public void applyForce(Vecc2f dir, int magnitude) {
        dir.mult(magnitude);
        this.velocity.add(dir);
    }

    private void collisionCheck() {
        for (int i = 0; i < parentRoom.getBoundaries().size(); i++) {
            if (parentRoom.getBoundaries().get(i).getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent())) {
                this.velocity.mult(1.5);
                this.position.sub(this.velocity);
                this.velocity.set(0, 0);
                relocate();
            }
        }
    }

    public void load(Group group) {
        group.getChildren().addAll(this.item, this.hitbox.getShape());
        this.item.relocate(this.position.x, this.position.y);
        this.item.setViewOrder(ViewOrder.items_layer.getViewOrder());
        //
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
        this.hitbox.getShape().setViewOrder(ViewOrder.items_layer.getViewOrder());
        this.hitbox.getShape().setVisible(false);
        //
        this.forceListener.play();
        postLoader(group);
    }

    public void postLoader(Group group) {

    }

    public void unload(Group group) {
        group.getChildren().removeAll(this.item);
        try {
            group.getChildren().remove(this.hitbox.getShape());
        } catch (Exception e) {

        }
        try {
            this.forceListener.stop();
            this.velocity.set(0, 0);
            relocate();
        } catch (Exception e) {

        }
        //
        postUnLoader(group);
    }

    protected void postUnLoader(Group group) {

    }

    public void checkCollision(Player player, ArrayList<Item> items, Group group) {
    }
}
