package sample;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;

public class Item_Coin extends Item {
    public Item_Coin(JsonObject a, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds) {
        super(a,pos,scaleX,scaleY,screenBounds);
    }
}
