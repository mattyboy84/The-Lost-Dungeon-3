package sample;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;

public class Item_DoubleKey extends Item {

    ImageView sparkle = new ImageView();
    Image[] spark = new Image[4];
    int sparkleOffsetX,sparkleOffsetY;
    //
    Timeline idleTimeline;
    int idlePointer=0;

    public Item_DoubleKey(JsonObject a, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds) {
        super(a, pos, scaleX, scaleY, screenBounds);
        //
        JsonArray array = a.getAsJsonArray("Sparkle");
        for (int i = 0; i < array.size(); i++) {

            int startX = array.get(i).getAsJsonObject().get("StartX").getAsInt();
            int startY = array.get(i).getAsJsonObject().get("StartY").getAsInt();
            int width = array.get(i).getAsJsonObject().get("Width").getAsInt();
            int height = array.get(i).getAsJsonObject().get("Height").getAsInt();

            spark[i] = Item.imageGetter("file:src\\resources\\gfx\\items\\pick ups\\" + a.get("Sprite").getAsString() + ".png", scaleX, scaleY, a.get("SheetScale").getAsInt(), startX, startY, width, height).getImage();
        }
        sparkleOffsetX = (int) (a.get("SparkleOffsetX").getAsInt() * scaleX)*sheetScale;
        sparkleOffsetY = (int) (a.get("SparkleOffsetY").getAsInt() * scaleY)*sheetScale;

        sparkle.setImage(spark[0]);
        idleTimelineSetup();
    }

    private void idleTimelineSetup() {
        idleTimeline = new Timeline(new KeyFrame(Duration.millis(180), event -> {
            System.out.println("key");
            this.sparkle.setImage(spark[idlePointer]);
            idlePointer = (idlePointer >= spark.length - 1) ? (0) : ++idlePointer;
        }));
        idleTimeline.setCycleCount(Timeline.INDEFINITE);
    }


    @Override
    public void checkCollision(Player player, ArrayList<Item> items, Group group) {
        if (player.bodyHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent()) && (player.keyNumber < Player_Overlay.MAX_ITEM_NUMBER)) {
            player.updateKeys(this.effect);
            group.getChildren().remove(this.sparkle);
            unload(group);
            items.remove(this);
        }
    }

    @Override
    public void postLoader(Group group) {
        try {
            group.getChildren().add(this.sparkle);
            this.sparkle.relocate(this.position.x + sparkleOffsetX, this.position.y + sparkleOffsetY);
            this.sparkle.setViewOrder(-4);
            this.idleTimeline.play();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void postUnLoader(Group group) {
        try {
            group.getChildren().remove(this.sparkle);
            this.idleTimeline.pause();
        } catch (Exception ignored) {
        }
    }
}