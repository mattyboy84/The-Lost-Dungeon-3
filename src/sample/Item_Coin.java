package sample;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Timer;

public class Item_Coin extends Item {

    Image[] idle = new Image[6];
    Timeline idleTimeline;
    int idlePointer = 0;


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
        timelineSetup();
    }

    private void timelineSetup() {
        idleTimeline = new Timeline(new KeyFrame(Duration.millis(150), event -> {
            this.item.setImage(idle[idlePointer]);
            idlePointer = (idlePointer>=idle.length-1) ? (0) : ++idlePointer;
        }));
        idleTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    @Override
    public void checkCollision(Player player, ArrayList<Item> items, Group group) {
        if (player.bodyHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent())) {
            player.updateCoins(this.effect);
            unload(group);
            items.remove(this);
        }
    }

    @Override
    public void postLoader() {
        this.idleTimeline.play();
    }

    @Override
    public void postUnLoader() {
        this.idleTimeline.pause();
    }
}
