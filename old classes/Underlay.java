package root.game.dungeon.room;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import root.game.util.Sprite_Splitter;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public class Underlay implements Sprite_Splitter {

    ArrayList<ImageView> leftWall = new ArrayList<ImageView>();
    ArrayList<ImageView> rightWall = new ArrayList<ImageView>();
    ArrayList<ImageView> topWall = new ArrayList<ImageView>();
    ArrayList<ImageView> bottomWall = new ArrayList<ImageView>();

    String underType;
    int width = 64;
    int height = 64;
    float sheetScale = 3f;
    Timeline timeline;
    int loop = 0;
    int loopCounter = 11;
    float scaleX, scaleY;
    float borderX, borderY;

    public Underlay(JsonObject underlay, int borderX, int borderY, float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.borderX = borderX;
        this.borderY = borderY;
        this.width *= this.scaleX * sheetScale;
        this.height *= this.scaleY * sheetScale;
        this.loopCounter *= this.scaleX * sheetScale;

        underType = underlay.get("underlay").getAsString();
        String file = "file:src\\resources\\gfx\\backdrop\\" + underType + ".png";

        int newHeight = (int) (screenBounds.getHeight() - (2 * this.borderY));
        int newWidth = (int) (screenBounds.getWidth() - (2 * this.borderX));
        //System.out.println("ADC " + newHeight/height);
        //LEFT
        {
            for (int j = -1; j < 2; j++) {
                for (int i = 0; i < (int) (newHeight / height); i++) {
                    leftWall.add(new ImageView(imageGetter(file, 0, 0, 64, 64, scaleX, scaleY, sheetScale)));
                    leftWall.get(leftWall.size() - 1).setRotate(0);

                    leftWall.get(leftWall.size() - 1).relocate((j * width), this.borderY + (i * height));
                }
            }
            //
            int offset = newHeight % height;
            for (int i = -1; i < 2; i++) {
                leftWall.add(new ImageView(imageGetter(file, 0, 0, 64, (int) (offset / sheetScale / scaleY), scaleX, scaleY, sheetScale)));
                leftWall.get(leftWall.size() - 1).setRotate(0);
                leftWall.get(leftWall.size() - 1).relocate((i * width), (int) (this.borderY + ((newHeight / height) * height)));
            }
        }
        //RIGHT
        {
            for (int j = -1; j < 2; j++) {
                for (int i = 0; i < (int) (newHeight / height); i++) {
                    rightWall.add(new ImageView(imageGetter(file, 0, 0, 64, 64, scaleX, scaleY, sheetScale)));
                    rightWall.get(rightWall.size() - 1).setRotate(180);
                    rightWall.get(rightWall.size() - 1).relocate((screenBounds.getWidth() - (j * width)) - width, this.borderY + (i * height));
                }
            }
            //
            int offset = newHeight % height;
            for (int i = -1; i < 2; i++) {
                rightWall.add(new ImageView(imageGetter(file, 0, 0, 64, (int) (offset / sheetScale / scaleY), scaleX, scaleY, sheetScale)));
                rightWall.get(rightWall.size() - 1).setRotate(180);
                rightWall.get(rightWall.size() - 1).relocate((screenBounds.getWidth() - (i * width)) - width, (int) (this.borderY + ((newHeight / height) * height)));
            }
        }
        //UP
        {
            for (int j = -1; j < 2; j++) {
                for (int i = 0; i < (int) (newWidth / width); i++) {
                    topWall.add(new ImageView(imageGetter(file, 0, 0, 64, 64, scaleX, scaleY, sheetScale)));
                    topWall.get(topWall.size() - 1).setRotate(0);
                    topWall.get(topWall.size() - 1).relocate(this.borderX + ((i * width)), -height + (j * height));
                }
            }
            //
            int offset = newWidth % width;
            for (int i = -1; i < 2; i++) {
                topWall.add(new ImageView(imageGetter(file, 0, 0, (int) 64, (int) 64, scaleX, scaleY, sheetScale)));
                topWall.get(topWall.size() - 1).setRotate(0);
                topWall.get(topWall.size() - 1).relocate(((this.borderX + newWidth) - width), (-height + (i * height)));
            }
        }
        //BOTTOM
        {
            for (int j = -1; j < 2; j++) {
                for (int i = 0; i < (int) (newWidth / width); i++) {
                    bottomWall.add(new ImageView(imageGetter(file, 0, 0, 64, 64, scaleX, scaleY, sheetScale)));
                    bottomWall.get(bottomWall.size() - 1).setRotate(0);
                    bottomWall.get(bottomWall.size() - 1).relocate(this.borderX + ((i * width)), (screenBounds.getHeight() - height) + (i * height));
                }
            }
            //
            //int offset = newWidth % width;
            //for (int i = -1; i < 2; i++) {
            //    bottomWall.add(new ImageView(imageGetter(file, 0, 0, (int) 64, (int) 64, scaleX, scaleY, sheetScale)));
            //    bottomWall.get(bottomWall.size() - 1).setRotate(0);
            //    bottomWall.get(bottomWall.size() - 1).relocate(((this.borderX + newWidth) - width), (-height + (i * height)));
            //}
        }
        timelineSetup();
    }

    private void timelineSetup() {
        timeline = new Timeline(new KeyFrame(Duration.millis(80), event -> {

            if (loop == width) {
                //left
                for (ImageView image : leftWall) {
                    image.relocate(image.getLayoutX() - (int) width, image.getLayoutY());
                }
                //right
                for (ImageView image : rightWall) {
                    image.relocate(image.getLayoutX() + (int) width, image.getLayoutY());
                }
                //top
                for (ImageView image : topWall) {
                    image.relocate(image.getLayoutX(), image.getLayoutY() - height);
                }
                //bottom
                for (ImageView image : bottomWall) {
                    image.relocate(image.getLayoutX(), image.getLayoutY() + height);
                }
                loop = 0;
                //
            } else {
                //left
                for (ImageView image : leftWall) {
                    image.relocate(image.getLayoutX() + 1, image.getLayoutY());
                }
                //right
                for (ImageView image : rightWall) {
                    image.relocate(image.getLayoutX() - 1, image.getLayoutY());
                }
                //top
                for (ImageView image : topWall) {
                    image.relocate(image.getLayoutX(), image.getLayoutY() + 1);
                }
                //bottom
                for (ImageView image : bottomWall) {
                    image.relocate(image.getLayoutX(), image.getLayoutY() - 1);
                }
            }
            loop++;
            //
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }


    public void load(Group group) {
        for (ImageView image : leftWall) {
            group.getChildren().add(image);
            image.setViewOrder(ViewOrder.background_layer.getViewOrder());
        }
        //
        for (ImageView image : rightWall) {
            group.getChildren().add(image);
            image.setViewOrder(ViewOrder.background_layer.getViewOrder());
        }
        //
        for (ImageView image : topWall) {
            group.getChildren().add(image);
            image.setViewOrder(ViewOrder.background_layer.getViewOrder());
        }
        //
        for (ImageView image : bottomWall) {
            group.getChildren().add(image);
            image.setViewOrder(ViewOrder.UI_layer.getViewOrder());
        }
        //
        this.timeline.play();
    }

    public void unload(Group group) {
        for (ImageView image : leftWall) {
            group.getChildren().remove(image);
        }
        //
        for (ImageView image : rightWall) {
            group.getChildren().remove(image);
        }
        //
        for (ImageView image : topWall) {
            group.getChildren().remove(image);
        }
        //
        for (ImageView image : bottomWall) {
            group.getChildren().remove(image);
        }
        //
        this.timeline.pause();
    }
}
