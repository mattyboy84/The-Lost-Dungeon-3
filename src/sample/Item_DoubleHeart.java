package sample;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;

import java.util.ArrayList;

public class Item_DoubleHeart extends Item {
    public Item_DoubleHeart(JsonObject a, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds) {
        super(a, pos, scaleX, scaleY, screenBounds);
    }

    @Override
    public void checkCollision(Player player, ArrayList<Item> items, Group group) {
        if ((player.bodyHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent()))) {
            if (!(player.health == player.TOTAL_Health)) {//not at full health - pick it up
                player.increaseHealth(this.effect, group);
                unload(group);
                items.remove(this);
            }else {//at full health - a force will be applied to the item to move it (player will be able to push it around
            }
        }
    }
}
