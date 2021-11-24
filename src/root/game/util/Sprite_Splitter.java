package root.game.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public interface Sprite_Splitter {

    default Image imageGetter(String file, int startX, int startY, int width, int height, float scaleX, float scaleY, float sheetScale) {

        Image a = (new ImageView(new WritableImage(new Image(file, ((new Image(file).getWidth() * sheetScale*scaleX)),
                ((new Image(file).getHeight() * sheetScale*scaleY)), false, false).getPixelReader(),
                Math.round(startX * sheetScale*scaleX), Math.round(startY * sheetScale*scaleY), (int)(width * sheetScale*scaleX), (int)(height * sheetScale*scaleY))).getImage());

        return a;
    }
}
