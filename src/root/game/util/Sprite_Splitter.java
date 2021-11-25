package root.game.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

//https://blog.idrsolutions.com/2019/01/image-scaling-options-in-java/
//https://stackoverflow.com/questions/25958699/java-image-re-sizing-nearest-neighbor
//https://openjfx-dev.openjdk.java.narkive.com/EqrL6rsl/displaying-pixel-perfect-images-without-blur-when-zooming

public interface Sprite_Splitter {

    default Image imageGetter(String file, int startX, int startY, int width, int height, float scaleX, float scaleY, float sheetScale) {

        Image a = (new ImageView(new WritableImage(new Image(file, ((new Image(file).getWidth() * sheetScale * scaleX)),
                ((new Image(file).getHeight() * sheetScale * scaleY)), false, false).getPixelReader(),
                (int) (startX * sheetScale * scaleX), (int) (startY * sheetScale * scaleY),
                (int) (width * sheetScale * scaleX), (int) (height * sheetScale * scaleY))).getImage());

        return a;
    }
}
