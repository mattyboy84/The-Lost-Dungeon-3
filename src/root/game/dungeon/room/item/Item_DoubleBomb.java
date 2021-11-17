package root.game.dungeon.room.item;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import root.game.player.Player;
import root.game.player.Player_Overlay;
import root.game.util.Vecc2f;

import java.util.ArrayList;

public class Item_DoubleBomb extends Item {
    public Item_DoubleBomb(JsonObject a, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds) {

        super(a, pos, scaleX, scaleY, screenBounds);
    }

    @Override
    public void checkCollision(Player player, ArrayList<Item> items, Group group) {
        if (player.getBodyHitbox().getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent())&& (player.bombNumber< Player_Overlay.MAX_ITEM_NUMBER)) {
            player.updateBombs(this.effect);
            unload(group);
            items.remove(this);
        }
    }
}
