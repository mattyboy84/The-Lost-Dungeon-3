package root.game.dungeon.room.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import root.game.player.Player;
import root.game.player.Player_Overlay;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

import java.util.ArrayList;

public class Item_Key extends Item implements Item_Animation, Sprite_Splitter {

    ImageView sparkle = new ImageView();
    Image[] spark = new Image[4];
    int sparkleOffsetX,sparkleOffsetY;
    //
    Timeline idleTimeline;
    int idlePointer=0;

    public Item_Key(JsonObject a, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds) {
        super(a, pos, scaleX, scaleY, screenBounds);
        //
        JsonArray array = a.getAsJsonArray("Sparkle");
        for (int i = 0; i < array.size(); i++) {

            int startX = array.get(i).getAsJsonObject().get("StartX").getAsInt();
            int startY = array.get(i).getAsJsonObject().get("StartY").getAsInt();
            int width = array.get(i).getAsJsonObject().get("Width").getAsInt();
            int height = array.get(i).getAsJsonObject().get("Height").getAsInt();

            spark[i] = imageGetter("file:src\\resources\\gfx\\items\\pick ups\\" + a.get("Sprite").getAsString() + ".png", startX, startY, width,height,scaleX,scaleY,sheetScale);
        }
        sparkleOffsetX= (int) (a.get("SparkleOffsetX").getAsInt()*scaleX);
        sparkleOffsetY= (int) (a.get("SparkleOffsetY").getAsInt()*scaleY);

        sparkle.setImage(spark[0]);
        idleTimelineSetup();
    }

    private void idleTimelineSetup() {
        idleTimeline = new Timeline(new KeyFrame(Duration.millis(180), event -> {
            this.sparkle.setImage(spark[idlePointer]);
            idlePointer = (idlePointer >= spark.length - 1) ? (0) : ++idlePointer;
        }));
        idleTimeline.setCycleCount(Timeline.INDEFINITE);
    }


    @Override
    public void checkCollision(Player player, ArrayList<Item> items, Group group) {
        if (player.getBodyHitbox().getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent())&& (player.keyNumber< Player_Overlay.MAX_ITEM_NUMBER)) {
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
            this.sparkle.relocate(this.position.x+sparkleOffsetX,this.position.y+sparkleOffsetY);
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
