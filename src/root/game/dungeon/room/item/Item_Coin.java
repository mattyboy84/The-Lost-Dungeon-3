package root.game.dungeon.room.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.util.Duration;
import root.game.dungeon.room.Room;
import root.game.music.Music;
import root.game.player.Player;
import root.game.player.Player_Overlay;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

import java.util.ArrayList;
import java.util.Random;

public class Item_Coin extends Item implements Item_Animation, Sprite_Splitter {

    Image[] idle = new Image[6];
    Image[] pickup = new Image[5];

    Timeline idleTimeline;
    Timeline pickupTimeline;

    Random random = new Random();

    int idlePointer = random.nextInt(idle.length-1);
    int pickupPointer = 1;

    public Item_Coin(JsonObject a, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Room parentRoom) {
        super(a, pos, scaleX, scaleY, screenBounds,parentRoom);

        JsonArray array = a.getAsJsonArray("IdleAnimation");
        for (int i = 0; i < array.size(); i++) {

            int startX = array.get(i).getAsJsonObject().get("StartX").getAsInt();
            int startY = array.get(i).getAsJsonObject().get("StartY").getAsInt();
            int width = array.get(i).getAsJsonObject().get("Width").getAsInt();
            int height = array.get(i).getAsJsonObject().get("Height").getAsInt();

            idle[i] = imageGetter("file:src\\resources\\gfx\\items\\pick ups\\" + a.get("Sprite").getAsString()
                    + ".png", startX, startY, width, height, scaleX, scaleY,a.get("SheetScale").getAsInt());
        }
        idleTimelineSetup();
        //
        JsonArray arrayB = a.getAsJsonArray("PickupAnimation");
        for (int i = 0; i < arrayB.size(); i++) {

            int startX = arrayB.get(i).getAsJsonObject().get("StartX").getAsInt();
            int startY = arrayB.get(i).getAsJsonObject().get("StartY").getAsInt();
            int width = arrayB.get(i).getAsJsonObject().get("Width").getAsInt();
            int height = arrayB.get(i).getAsJsonObject().get("Height").getAsInt();

            pickup[i] = imageGetter("file:src\\resources\\gfx\\items\\pick ups\\" + a.get("Sprite").getAsString()
                    + ".png", startX, startY, width, height, scaleX, scaleY,a.get("SheetScale").getAsInt());        }
        pickupTimelineSetup();


        //2, 3,
    }

    private void pickupTimelineSetup() {
        pickupTimeline = new Timeline(new KeyFrame(Duration.millis(80), event -> {
            this.item.setImage(pickup[pickupPointer]);
            pickupPointer = (pickupPointer >= pickup.length - 1) ? (0) : ++pickupPointer;
        }));
        pickupTimeline.setCycleCount(pickup.length - 1);
    }

    private void idleTimelineSetup() {
        idleTimeline = new Timeline(new KeyFrame(Duration.millis(150), event -> {
            this.item.setImage((idlePointer<idle.length ? (idle[idlePointer]) : (idle[0])));
            idlePointer = (idlePointer >= 9) ? (0) : ++idlePointer;
        }));
        idleTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    @Override
    public void checkCollision(Player player, ArrayList<Item> items, Group group) {
        if (player.getBodyHitbox().getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent()) && !(pickupTimeline.getStatus()== Animation.Status.RUNNING)&& (player.coinNumber< Player_Overlay.MAX_ITEM_NUMBER)) {

            player.updateCoins(this.effect);
            this.idleTimeline.stop();
            group.getChildren().remove(this.hitbox.getShape());
            //
            try{
                this.forceListener.stop();
                this.velocity.set(0,0);
                relocate();
            }catch (Exception e){

            }
            //the pickup animations are larger so an offset is applied to re-center it.
            this.position.sub(16 * scaleX * sheetScale, 23 * scaleY * sheetScale);
            this.item.relocate(this.position.x, this.position.y);

            this.item.setImage(pickup[0]);
            //
            Music.addSFX(false,this.hashCode(), Music.sfx.penny_pickup_1);
            //
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
