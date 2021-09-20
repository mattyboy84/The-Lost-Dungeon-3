package sample;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class Background {

    JsonObject backgroundTemplate = null;
    //
    ImageView topLeftIMG, topRightIMG, bottomLeftIMG, bottomRightIMG;
    String name;
    int width, height, rows, columns;
    int borderX, borderY;
    //
    Rectangle topLeft, topRight, leftUp, leftDown, bottomLeft, bottomRight, rightUp, rightDown;
    //
    Random random = new Random();

    public Background(JsonObject background, float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.backgroundTemplate = background;
        this.name = background.get("name").getAsString();
        this.width = background.get("Width").getAsInt();
        this.height = background.get("Height").getAsInt();
        this.rows = background.get("Rows").getAsInt();
        this.columns = background.get("Columns").getAsInt();
        int sheetScale = background.get("SheetScale").getAsInt();
        this.borderX = (int) (background.get("BorderX").getAsInt() * scaleX);
        this.borderY = (int) (background.get("BorderY").getAsInt() * scaleY);
        //32 - inline with door
        //
        int doorOffsetX = (int) (16 * sheetScale * scaleX);
        int doorOffsetY = (int) (16 * sheetScale * scaleY);

        topLeft = new Rectangle(0, 0, screenBounds.getWidth() / 2 - doorOffsetX, this.borderY);
        topRight = new Rectangle(screenBounds.getWidth() / 2 + doorOffsetX, 0, screenBounds.getWidth(), this.borderY);
        bottomLeft = new Rectangle(0, screenBounds.getHeight() - this.borderY, screenBounds.getWidth() / 2 - doorOffsetX, screenBounds.getHeight());
        bottomRight = new Rectangle(screenBounds.getWidth() / 2 + doorOffsetX, screenBounds.getHeight() - this.borderY, screenBounds.getWidth(), screenBounds.getHeight());
        leftUp = new Rectangle(0, 0, this.borderX, screenBounds.getHeight() / 2 - doorOffsetY);
        leftDown = new Rectangle(0, screenBounds.getHeight() / 2 + doorOffsetY, this.borderX, screenBounds.getHeight());
        rightUp = new Rectangle(screenBounds.getWidth() - this.borderX, 0, screenBounds.getWidth(), screenBounds.getHeight() / 2 - doorOffsetY);
        rightDown = new Rectangle(screenBounds.getWidth() - this.borderX, screenBounds.getHeight() / 2 + doorOffsetY, screenBounds.getWidth(), screenBounds.getHeight());

        //
        int randRow = random.nextInt(this.rows);
        int randCol = random.nextInt(this.columns);

        //this.spriteSheet = new ImageView("file:src\\resources\\gfx\\backdrop\\" + this.name + ".png");

        String file = "file:src\\resources\\gfx\\backdrop\\" + this.name + ".png";

        //(file, (new Image(file).getWidth() * scaleX), (new Image(file).getHeight() * scaleY), true, false)

        float a = (float) (screenBounds.getWidth() / 2) / this.width;
        float b = (float) (screenBounds.getHeight() / 2) / this.height;

        this.topLeftIMG = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b),
                false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));
        this.topLeftIMG.relocate(0, 0);
        this.topRightIMG = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b),
                false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));
        this.topRightIMG.relocate(screenBounds.getWidth() / 2, 0);
        this.topRightIMG.setScaleX(-1);
        this.bottomLeftIMG = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b),
                false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));
        this.bottomLeftIMG.relocate(0, screenBounds.getHeight() / 2);
        this.bottomLeftIMG.setScaleY(-1);
        this.bottomRightIMG = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b),
                false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));

        this.bottomRightIMG.relocate(screenBounds.getWidth() / 2, screenBounds.getHeight() / 2);
        this.bottomRightIMG.setScaleX(-1);
        this.bottomRightIMG.setScaleY(-1);
    }

    public void load(Group group) {
        group.getChildren().addAll(topLeft, topRight, bottomLeft, bottomRight, leftUp, leftDown, rightUp, rightDown);
        //topLeft.setViewOrder(1);
        //topRight.setViewOrder(1);
        //leftUp.setViewOrder(1);
        //leftDown.setViewOrder(1);
        //bottomRight.setViewOrder(1);
        //bottomLeft.setViewOrder(1);
        //rightDown.setViewOrder(1);
        //rightUp.setViewOrder(1);
        //
        group.getChildren().addAll(topLeftIMG, topRightIMG, bottomLeftIMG, bottomRightIMG);
        topLeftIMG.setViewOrder(0);
        topRightIMG.setViewOrder(0);
        bottomLeftIMG.setViewOrder(0);
        bottomRightIMG.setViewOrder(0);
        //
        topLeft.toFront();
        topRight.toFront();
        leftUp.toFront();
        leftDown.toFront();
        bottomRight.toFront();
        bottomLeft.toFront();
        rightDown.toFront();
        rightUp.toFront();
    }

    public void unload(Group group) {
        group.getChildren().removeAll(topLeftIMG, topRightIMG, bottomLeftIMG, bottomRightIMG,topLeft, topRight, bottomLeft, bottomRight, leftUp, leftDown, rightUp, rightDown);
    }

    public ArrayList<Rectangle> getBoundaries() {
        ArrayList<Rectangle> a = new ArrayList<>();
        a.add(topLeft);
        a.add(topRight);
        a.add(leftDown);
        a.add(leftUp);
        a.add(rightDown);
        a.add(rightUp);
        a.add(bottomLeft);
        a.add(bottomRight);

        return a;
    }

}
