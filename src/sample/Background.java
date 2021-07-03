package sample;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;

import java.util.Random;

public class Background {

    JsonObject backgroundTemplate = null;
    //
    ImageView topLeft, topRight, bottomLeft, bottomRight;
    String name;
    int width, height, rows, columns;
    int borderX,borderY;
    //
    Rectangle up,down,left,right;

    //
    Random random = new Random();


    public Background(JsonObject background, float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.backgroundTemplate = background;
        this.name = background.get("name").getAsString();
        this.width = background.get("Width").getAsInt();
        this.height = background.get("Height").getAsInt();
        this.rows = background.get("Rows").getAsInt();
        this.columns = background.get("Columns").getAsInt();
        this.borderX=(int)(background.get("BorderX").getAsInt()*scaleX);
        this.borderY=(int)(background.get("BorderY").getAsInt()*scaleY);
        //
        up=new Rectangle(0,0,screenBounds.getWidth(),this.borderY);
        left=new Rectangle(0,0,this.borderX,screenBounds.getHeight());
        down=new Rectangle(0,screenBounds.getHeight()-this.borderY,screenBounds.getWidth(),screenBounds.getHeight());
        right=new Rectangle(screenBounds.getWidth()-this.borderX,0,screenBounds.getWidth(),screenBounds.getHeight());

        System.out.println(up.getBoundsInParent());
        System.out.println(left.getBoundsInParent());
        System.out.println(right.getBoundsInParent());
        System.out.println(down.getBoundsInParent());


        //
        int randRow = random.nextInt(this.rows);
        int randCol = random.nextInt(this.columns);

        //this.spriteSheet = new ImageView("file:src\\resources\\gfx\\backdrop\\" + this.name + ".png");


        String file = "file:src\\resources\\gfx\\backdrop\\" + this.name + ".png";

        //(file, (new Image(file).getWidth() * scaleX), (new Image(file).getHeight() * scaleY), true, false)

        float a = (float) (screenBounds.getWidth() / 2) / this.width;
        float b = (float) (screenBounds.getHeight() / 2) / this.height;

        this.topLeft = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b), false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));
        this.topLeft.relocate(0, 0);
        this.topRight = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b), false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));
        this.topRight.relocate(screenBounds.getWidth() / 2, 0);
        this.topRight.setScaleX(-1);
        this.bottomLeft = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b), false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));
        this.bottomLeft.relocate(0, screenBounds.getHeight() / 2);
        this.bottomLeft.setScaleY(-1);
        this.bottomRight = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b), false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));
        this.bottomRight.relocate(screenBounds.getWidth() / 2, screenBounds.getHeight() / 2);
        this.bottomRight.setScaleX(-1);
        this.bottomRight.setScaleY(-1);
    }

    public void load(Group group) {
        group.getChildren().addAll(up,down,left,right);
        up.setViewOrder(1);
        down.setViewOrder(1);
        left.setViewOrder(1);
        right.setViewOrder(1);
        //
        group.getChildren().addAll(topLeft, topRight, bottomLeft, bottomRight);
        topLeft.setViewOrder(0);
        topRight.setViewOrder(0);
        bottomLeft.setViewOrder(0);
        bottomRight.setViewOrder(0);


    }

    public void unload(Group group) {
        group.getChildren().removeAll(topLeft, topRight, bottomLeft, bottomRight);
    }
}
