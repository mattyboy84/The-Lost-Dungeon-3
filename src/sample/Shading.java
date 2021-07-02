package sample;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Shading {

    int offsetX = -20;
    int offsetY = -28;

    float xMult = (float) 1.02;
    float yMult = (float) 1.044;

    ImageView shading;
    String file = "file:src\\resources\\gfx\\backdrop\\shading.png";

    //        this.topLeft = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b), false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));
    public Shading(float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.shading=new ImageView(new Image(file, (int)(screenBounds.getWidth()*xMult), (int)(screenBounds.getHeight()*yMult), false, false));
        this.shading.relocate(offsetX*scaleX,offsetY*scaleY);

    }

    public void load(Group group) {
        group.getChildren().addAll(this.shading);
        this.shading.setViewOrder(-10);
    }

    public void unload(Group group) {
        group.getChildren().removeAll(this.shading);
    }

}
