package sample;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
    Rectangle doorTrigger;
    Rectangle doorBlock;
    int triggerHeight = 80;
    int triggerWidth=115;
    int blockHeight = 20;

    enum State {
        open,
        closed,
        locked
    }

    State state;


    public Door(String direction, int rotation, int directionType, int type, float scaleX, float scaleY, Rectangle2D screenBounds, Background background) {
        //String file = "file:src\\resources\\gfx\\grid\\" + this.name + ".png";
        String a = types[directionType];
        if (type != 1) {
            a = types[type];
        }
        this.state = State.closed;
        String file = "file:src\\resources\\gfx\\grid\\" + a + ".png";
        //System.out.println(directionType);

        //this.doorFrame = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX*spriteScaleX), (new Image(file).getHeight() * scaleY*spriteScaleY), false, false).getPixelReader(), (int) (0 *((width*scaleX*spriteScaleX))), (int) (0*((height*scaleY*spriteScaleY))), (int)(width*scaleX*spriteScaleX), (int)(height*scaleY*spriteScaleY))));

        this.triggerHeight *= ((scaleX + scaleY) / 2);
        this.blockHeight *= ((scaleX + scaleY) / 2);

        this.doorFrame = imageGetter(file, 0, 0, scaleX, scaleY);
        this.doorShadow = imageGetter(file, 1, 0, scaleX, scaleY);
        this.doorPartLeft = imageGetter(file, 0, 1, scaleX, scaleY);
        this.doorPartRight = imageGetter(file, 1, 1, scaleX, scaleY);
        this.doorPartRightLocked = imageGetter(file, 1, 2, scaleX, scaleY);
        //x, y, width, height
        switch (direction) {
            case "up" -> {
                this.position = new Vecc2f((float) ((screenBounds.getWidth() / 2) - this.doorFrame.getBoundsInParent().getWidth() / 2), (doorBoundary * scaleY));
                this.doorTrigger = new Rectangle(background.topLeft.getBoundsInParent().getMaxX(), 0, (((screenBounds.getWidth() / 2) - background.topLeft.getWidth())) * 2, triggerHeight);
                this.doorBlock = new Rectangle(background.topLeft.getBoundsInParent().getMaxX(), background.topLeft.getBoundsInParent().getMaxY() - blockHeight, (((screenBounds.getWidth() / 2) - background.topLeft.getWidth())) * 2, blockHeight);
            }
            case "down" -> {
                this.position = new Vecc2f((float) ((screenBounds.getWidth() / 2) - this.doorFrame.getBoundsInParent().getWidth() / 2), (float) (screenBounds.getHeight() - this.doorFrame.getBoundsInParent().getHeight() - (doorBoundary * scaleY)));
                this.doorTrigger = new Rectangle(background.bottomLeft.getBoundsInParent().getMaxX(), screenBounds.getHeight() - (triggerHeight), (((screenBounds.getWidth() / 2) - background.bottomLeft.getWidth())) * 2, triggerHeight);
                this.doorBlock = new Rectangle(background.bottomLeft.getBoundsInParent().getMaxX(), background.bottomLeft.getBoundsInParent().getMinY(), (((screenBounds.getWidth() / 2) - background.bottomLeft.getWidth())) * 2, blockHeight);
            }
            case "left" -> {
                this.position = new Vecc2f(0 + (doorBoundary * scaleX), (float) ((screenBounds.getHeight() / 2) - (this.doorFrame.getBoundsInParent().getHeight() / 2)));
                this.doorTrigger = new Rectangle(0, background.leftUp.getBoundsInParent().getMaxY(), triggerWidth, (((screenBounds.getHeight() / 2) - background.leftUp.getHeight())) * 2);
                this.doorBlock = new Rectangle(background.leftUp.getBoundsInParent().getMaxX() - blockHeight, background.leftUp.getBoundsInParent().getMaxY(), blockHeight, (((screenBounds.getHeight() / 2) - background.leftUp.getHeight())) * 2);
            }
            case "right" -> {
                this.position = new Vecc2f((float) (screenBounds.getWidth() - this.doorFrame.getBoundsInParent().getWidth() - (doorBoundary * scaleX)), (float) ((screenBounds.getHeight() / 2) - (this.doorFrame.getBoundsInParent().getHeight() / 2)));
                this.doorTrigger = new Rectangle(screenBounds.getWidth() - triggerWidth, background.rightUp.getBoundsInParent().getMaxY(), triggerWidth, (((screenBounds.getHeight() / 2) - background.rightUp.getHeight())) * 2);
                this.doorBlock = new Rectangle(background.rightUp.getBoundsInParent().getMinX(), background.rightUp.getBoundsInParent().getMaxY(), blockHeight, (((screenBounds.getHeight() / 2) - background.rightUp.getHeight())) * 2);
            }
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
            case open -> group.getChildren().addAll(this.doorShadow, this.doorFrame, this.doorTrigger);
            case closed -> group.getChildren().addAll(this.doorShadow, this.doorPartLeft, this.doorPartRight, this.doorFrame, this.doorBlock, this.doorTrigger);
            case locked -> group.getChildren().addAll(this.doorShadow, this.doorPartLeft, this.doorPartRightLocked, this.doorFrame, this.doorBlock, this.doorTrigger);
        }
        this.doorFrame.relocate(position.x, position.y);
        this.doorShadow.relocate(position.x, position.y);
        this.doorPartRight.relocate(position.x, position.y);
        this.doorPartLeft.relocate(position.x, position.y);
        //
        this.doorFrame.setViewOrder(-3);
        this.doorShadow.setViewOrder(-3);
        this.doorPartRight.setViewOrder(-8);
        this.doorPartLeft.setViewOrder(-8);
        //
        this.doorTrigger.toFront();
        this.doorTrigger.setFill(Color.RED);
        this.doorTrigger.setViewOrder(-12);
        //
        this.doorBlock.toFront();
        this.doorBlock.setFill(Color.GREEN);
        this.doorBlock.setViewOrder(-12);
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
        try {
            group.getChildren().removeAll(this.doorTrigger, this.doorBlock);
        } catch (Exception e) {

        }
    }

    public void open(Group group) {

        switch (state) {
            case open -> {

            }
            case closed -> {
                group.getChildren().removeAll(this.doorPartLeft, this.doorPartRight, this.doorBlock);
            }
            case locked -> {
                group.getChildren().removeAll(this.doorPartLeft, this.doorPartRightLocked, this.doorBlock);

            }
        }
        this.state = State.open;

    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public ImageView getDoorFrame() {
        return doorFrame;
    }

    public void setDoorFrame(ImageView doorFrame) {
        this.doorFrame = doorFrame;
    }

    public ImageView getDoorShadow() {
        return doorShadow;
    }

    public void setDoorShadow(ImageView doorShadow) {
        this.doorShadow = doorShadow;
    }

    public ImageView getDoorPartLeft() {
        return doorPartLeft;
    }

    public void setDoorPartLeft(ImageView doorPartLeft) {
        this.doorPartLeft = doorPartLeft;
    }

    public ImageView getDoorPartRight() {
        return doorPartRight;
    }

    public void setDoorPartRight(ImageView doorPartRight) {
        this.doorPartRight = doorPartRight;
    }

    public ImageView getDoorPartRightLocked() {
        return doorPartRightLocked;
    }

    public void setDoorPartRightLocked(ImageView doorPartRightLocked) {
        this.doorPartRightLocked = doorPartRightLocked;
    }

    public int getSpriteScaleX() {
        return spriteScaleX;
    }

    public void setSpriteScaleX(int spriteScaleX) {
        this.spriteScaleX = spriteScaleX;
    }

    public int getSpriteScaleY() {
        return spriteScaleY;
    }

    public void setSpriteScaleY(int spriteScaleY) {
        this.spriteScaleY = spriteScaleY;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Vecc2f getPosition() {
        return position;
    }

    public void setPosition(Vecc2f position) {
        this.position = position;
    }

    public int getDoorBoundary() {
        return doorBoundary;
    }

    public void setDoorBoundary(int doorBoundary) {
        this.doorBoundary = doorBoundary;
    }

    public Rectangle getDoorTrigger() {
        return doorTrigger;
    }

    public void setDoorTrigger(Rectangle doorTrigger) {
        this.doorTrigger = doorTrigger;
    }

    public Rectangle getDoorBlock() {
        return doorBlock;
    }

    public void setDoorBlock(Rectangle doorBlock) {
        this.doorBlock = doorBlock;
    }

    public int getTriggerHeight() {
        return triggerHeight;
    }

    public void setTriggerHeight(int triggerHeight) {
        this.triggerHeight = triggerHeight;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
