package sample;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;

import java.util.ArrayList;

public class Item_Key extends Item {
    public Item_Key(JsonObject a, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds) {
        super(a, pos, scaleX, scaleY, screenBounds);
    }


    @Override
    public void checkCollision(Player player, ArrayList<Item> items, Group group) {
        if (player.bodyHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent())) {
            player.updateKeys(this.effect);
            unload(group);
            items.remove(this);
        }
    }

}
