package sample;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class Item_Coin extends Item implements Item_Animation{

    Image[] idle = new Image[6];
    Image[] pickup = new Image[5];

    Timeline idleTimeline;
    Timeline pickupTimeline;

    Random random = new Random();

    int idlePointer = random.nextInt(idle.length-1);
    int pickupPointer = 1;

    public Item_Coin(JsonObject a, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds) {
        super(a, pos, scaleX, scaleY, screenBounds);

        JsonArray array = a.getAsJsonArray("IdleAnimation");
        for (int i = 0; i < array.size(); i++) {

            int startX = array.get(i).getAsJsonObject().get("StartX").getAsInt();
            int startY = array.get(i).getAsJsonObject().get("StartY").getAsInt();
            int width = array.get(i).getAsJsonObject().get("Width").getAsInt();
            int height = array.get(i).getAsJsonObject().get("Height").getAsInt();

            idle[i] = Item.imageGetter("file:src\\resources\\gfx\\items\\pick ups\\" + a.get("Sprite").getAsString() + ".png", scaleX, scaleY, a.get("SheetScale").getAsInt(), startX, startY, width, height).getImage();
        }
        idleTimelineSetup();
        //
        JsonArray arrayB = a.getAsJsonArray("PickupAnimation");
        for (int i = 0; i < arrayB.size(); i++) {

            int startX = arrayB.get(i).getAsJsonObject().get("StartX").getAsInt();
            int startY = arrayB.get(i).getAsJsonObject().get("StartY").getAsInt();
            int width = arrayB.get(i).getAsJsonObject().get("Width").getAsInt();
            int height = arrayB.get(i).getAsJsonObject().get("Height").getAsInt();

            pickup[i] = Item.imageGetter("file:src\\resources\\gfx\\items\\pick ups\\" + a.get("Sprite").getAsString() + ".png", scaleX, scaleY, a.get("SheetScale").getAsInt(), startX, startY, width, height).getImage();
        }
        pickupTimelineSetup();
    }

    private void pickupTimelineSetup() {
        pickupTimeline = new Timeline(new KeyFrame(Duration.millis(150), event -> {
            this.item.setImage(pickup[pickupPointer]);
            pickupPointer = (pickupPointer >= pickup.length - 1) ? (0) : ++pickupPointer;
        }));
        pickupTimeline.setCycleCount(pickup.length - 1);
    }

    private void idleTimelineSetup() {
        idleTimeline = new Timeline(new KeyFrame(Duration.millis(150), event -> {
            this.item.setImage(idle[idlePointer]);
            idlePointer = (idlePointer >= idle.length - 1) ? (0) : ++idlePointer;
        }));
        idleTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    @Override
    public void checkCollision(Player player, ArrayList<Item> items, Group group) {
        if (player.bodyHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent()) && !(pickupTimeline.getStatus()== Animation.Status.RUNNING)&& (player.coinNumber<Player_Overlay.MAX_ITEM_NUMBER)) {

            player.updateCoins(this.effect);
            this.idleTimeline.stop();

            this.position.sub(16 * scaleX * sheetScale, 23 * scaleY * sheetScale);
            this.item.relocate(this.position.x, this.position.y);

            this.item.setImage(pickup[0]);
            this.pickupTimeline.play();

            this.pickupTimeline.setOnFinished(actionEvent -> {
                unload(group);
                items.remove(this);
            });
        }
    }

    @Override
    public void postLoader(Group group) {
        try {
            this.idleTimeline.play();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void postUnLoader(Group group) {
        try {
            this.idleTimeline.pause();
        } catch (Exception ignored) {

        }
    }
}
