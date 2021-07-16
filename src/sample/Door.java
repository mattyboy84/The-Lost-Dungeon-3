package sample;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class Door {

    String[] types = {" ", "door_01_normaldoor", "door_02_treasureroomdoor", "door_10_bossroomdoor"};
    //
    ImageView doorFrame, doorShadow, doorPartLeft, doorPartRight, doorPartRightLocked;
    //
    int spriteScaleX = 4;
    int spriteScaleY = 4;
    //
    int height = 48;
    int width = 64;
    //
    Vecc2f position;
    //
    int doorBoundary = 12;

    enum State {
        open,
        closed,
        locked
    }

    State state;

    public Door(String direction, int rotation, int directionType, int type, float scaleX, float scaleY, Rectangle2D screenBounds) {
        //String file = "file:src\\resources\\gfx\\grid\\" + this.name + ".png";
        String a = types[directionType];
        if (type != 1) {
            a = types[type];
        }
        this.state = State.closed;
        String file = "file:src\\resources\\gfx\\grid\\" + a + ".png";
        //System.out.println(directionType);

        //this.doorFrame = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX*spriteScaleX), (new Image(file).getHeight() * scaleY*spriteScaleY), false, false).getPixelReader(), (int) (0 *((width*scaleX*spriteScaleX))), (int) (0*((height*scaleY*spriteScaleY))), (int)(width*scaleX*spriteScaleX), (int)(height*scaleY*spriteScaleY))));

        this.doorFrame = imageGetter(file, 0, 0, scaleX, scaleY);
        this.doorShadow = imageGetter(file, 1, 0, scaleX, scaleY);
        this.doorPartLeft = imageGetter(file, 0, 1, scaleX, scaleY);
        this.doorPartRight = imageGetter(file, 1, 1, scaleX, scaleY);
        this.doorPartRightLocked = imageGetter(file, 1, 2, scaleX, scaleY);

        switch (direction) {
            case "up" -> this.position = new Vecc2f((float) ((screenBounds.getWidth() / 2) - this.doorFrame.getBoundsInParent().getWidth() / 2), (doorBoundary * scaleY));
            case "down" -> this.position = new Vecc2f((float) ((screenBounds.getWidth() / 2) - this.doorFrame.getBoundsInParent().getWidth() / 2), (float) (screenBounds.getHeight() - this.doorFrame.getBoundsInParent().getHeight() - (doorBoundary * scaleY)));
            case "left" -> this.position = new Vecc2f(0 + (doorBoundary * scaleX), (float) ((screenBounds.getHeight() / 2) - (this.doorFrame.getBoundsInParent().getHeight() / 2)));
            case "right" -> this.position = new Vecc2f((float) (screenBounds.getWidth() - this.doorFrame.getBoundsInParent().getWidth() - (doorBoundary * scaleX)), (float) ((screenBounds.getHeight() / 2) - (this.doorFrame.getBoundsInParent().getHeight() / 2)));
        }
        //
        this.doorFrame.setRotate(rotation);
        this.doorShadow.setRotate(rotation);
        this.doorPartLeft.setRotate(rotation);
        this.doorPartRight.setRotate(rotation);
        this.doorPartRightLocked.setRotate(rotation);
        //
    }

    private ImageView imageGetter(String file, int i, int i1, float scaleX, float scaleY) {

        return (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX * spriteScaleX), (new Image(file).getHeight() * scaleY * spriteScaleY), false, false).getPixelReader(), (int) (i * ((width * scaleX * spriteScaleX))), (int) (i1 * ((height * scaleY * spriteScaleY))), (int) (width * scaleX * spriteScaleX), (int) (height * scaleY * spriteScaleY))));

    }


    public void load(Group group) {
        //group.getChildren().addAll(this.doorShadow, this.doorPartLeft, this.doorPartRight, this.doorFrame);

        switch (state) {
            case open -> group.getChildren().addAll(this.doorShadow, this.doorFrame);
            case closed -> group.getChildren().addAll(this.doorShadow, this.doorPartLeft, this.doorPartRight, this.doorFrame);
            case locked -> group.getChildren().addAll(this.doorShadow, this.doorPartLeft, this.doorPartRightLocked, this.doorFrame);
        }
        this.doorFrame.relocate(position.x, position.y);
        this.doorShadow.relocate(position.x, position.y);
        this.doorPartRight.relocate(position.x, position.y);
        this.doorPartLeft.relocate(position.x, position.y);
    }

    public void unload(Group group) {
        try {
            group.getChildren().removeAll(this.doorFrame, this.doorShadow);
        } catch (Exception e) {
        }
        try {
            group.getChildren().remove(this.doorPartRight);
        } catch (Exception e) {
        }
        try {
            group.getChildren().remove(this.doorPartLeft);
        } catch (Exception e) {
        }
        try {
            group.getChildren().remove(this.doorPartRightLocked);
        } catch (Exception e) {
        }
    }
}
