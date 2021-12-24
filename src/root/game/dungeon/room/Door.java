package root.game.dungeon.room;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.Random;

public class Door implements Sprite_Splitter {

    String[] types = {" ", "door_01_normaldoor", "door_02_treasureroomdoor", "door_10_bossroomdoor"};
    //
    ImageView doorFrame, doorShadow, doorPartLeft, doorPartRight, doorPartRightLocked, trapDoor;
    Image brokenDoorFrame;
    //
    int spriteScaleX = 4;
    int spriteScaleY = 4;
    //
    int height;
    int width;
    //
    Vecc2f position, centerPos;
    public Vecc2f relocatePos;
    //
    int doorBoundary = 12;
    Rectangle doorTrigger;
    Rectangle doorBlock;
    int triggerHeight = 90;
    int triggerWidth = 125;
    int blockHeight = 20;
    public String direction;
    Random random = new Random();

    public enum State {
        open,
        closed,
        locked
    }

    public State state;

    public Door(String direction, int rotation, int directionType, int type, float scaleX, float scaleY, Rectangle2D screenBounds, Background background) {
        //String file = "file:src\\resources\\gfx\\grid\\" + this.name + ".png";
        String a = types[directionType];
        if (type != 1) {
            a = types[type];
        }
        //
        this.height = 48;
        this.width = 64;
        //
        this.direction = direction;

        int chance = (type == 1 && (((!a.equals("door_01_normaldoor")) && (!a.equals("door_10_bossroomdoor")))) ? 30 : 0);
        //30 percent chance that a door in a normal room that leads to a special room is locked
        this.state = ((random.nextInt(100)) < chance) ? (State.locked) : (State.closed);
        //
        String file = "file:src\\resources\\gfx\\grid\\" + a + ".png";
        //System.out.println(directionType);

        this.triggerHeight *= ((scaleX + scaleY) / 2);
        this.triggerWidth *= ((scaleX + scaleY) / 2);
        this.blockHeight *= ((scaleX + scaleY) / 2);

        this.doorFrame = imageGetter(file, 0, 0, scaleX, scaleY);
        this.doorShadow = imageGetter(file, 1, 0, scaleX, scaleY);
        this.doorPartLeft = imageGetter(file, 0, 1, scaleX, scaleY);
        this.doorPartRight = imageGetter(file, 1, 1, scaleX, scaleY);
        this.doorPartRightLocked = imageGetter(file, 1, 2, scaleX, scaleY);
        this.brokenDoorFrame = imageGetter(file, 0, 2, scaleX, scaleY).getImage();

        //x, y, width, height
        switch (direction) {
            case "up" -> {
                this.position = new Vecc2f((float) ((screenBounds.getWidth() / 2) - this.doorFrame.getBoundsInParent().getWidth() / 2), (doorBoundary * scaleY));
                this.doorTrigger = new Rectangle(background.topLeft.getBoundsInParent().getMaxX(), 0, (((screenBounds.getWidth() / 2) - background.topLeft.getWidth())) * 2, triggerHeight);
                this.doorBlock = new Rectangle(background.topLeft.getBoundsInParent().getMaxX(), background.topLeft.getBoundsInParent().getMaxY() - blockHeight, (((screenBounds.getWidth() / 2) - background.topLeft.getWidth())) * 2, blockHeight);

                //Rectangle j= new Rectangle(background.bottomLeft.getBoundsInParent().getMaxX(), background.bottomLeft.getBoundsInParent().getMinY(), (((screenBounds.getWidth() / 2) - background.bottomLeft.getWidth())) * 2, blockHeight);

                this.relocatePos = new Vecc2f(913, 817);
            }
            case "down" -> {
                this.position = new Vecc2f((float) ((screenBounds.getWidth() / 2) - this.doorFrame.getBoundsInParent().getWidth() / 2), (float) (screenBounds.getHeight() - this.doorFrame.getBoundsInParent().getHeight() - (doorBoundary * scaleY)));
                this.doorTrigger = new Rectangle(background.bottomLeft.getBoundsInParent().getMaxX(), screenBounds.getHeight() - (triggerHeight), (((screenBounds.getWidth() / 2) - background.bottomLeft.getWidth())) * 2, triggerHeight);
                this.doorBlock = new Rectangle(background.bottomLeft.getBoundsInParent().getMaxX(), background.bottomLeft.getBoundsInParent().getMinY(), (((screenBounds.getWidth() / 2) - background.bottomLeft.getWidth())) * 2, blockHeight);

                //Rectangle j = new Rectangle(background.topLeft.getBoundsInParent().getMaxX(), background.topLeft.getBoundsInParent().getMaxY() - blockHeight, (((screenBounds.getWidth() / 2) - background.topLeft.getWidth())) * 2, blockHeight);

                this.relocatePos = new Vecc2f(912, 182);
            }
            case "left" -> {
                this.position = new Vecc2f(0 + (doorBoundary * scaleX), (float) ((screenBounds.getHeight() / 2) - (this.doorFrame.getBoundsInParent().getHeight() / 2)));
                this.doorTrigger = new Rectangle(0, background.leftUp.getBoundsInParent().getMaxY(), triggerWidth, (((screenBounds.getHeight() / 2) - background.leftUp.getHeight())) * 2);
                this.doorBlock = new Rectangle(background.leftUp.getBoundsInParent().getMaxX() - blockHeight, background.leftUp.getBoundsInParent().getMaxY(), blockHeight, (((screenBounds.getHeight() / 2) - background.leftUp.getHeight())) * 2);

                //Rectangle j = new Rectangle(background.rightUp.getBoundsInParent().getMinX(), background.rightUp.getBoundsInParent().getMaxY(), blockHeight, (((screenBounds.getHeight() / 2) - background.rightUp.getHeight())) * 2);

                this.relocatePos = new Vecc2f(1614, 495);
            }
            case "right" -> {
                this.position = new Vecc2f((float) (screenBounds.getWidth() - this.doorFrame.getBoundsInParent().getWidth() - (doorBoundary * scaleX)), (float) ((screenBounds.getHeight() / 2) - (this.doorFrame.getBoundsInParent().getHeight() / 2)));
                this.doorTrigger = new Rectangle(screenBounds.getWidth() - triggerWidth, background.rightUp.getBoundsInParent().getMaxY(), triggerWidth, (((screenBounds.getHeight() / 2) - background.rightUp.getHeight())) * 2);
                this.doorBlock = new Rectangle(background.rightUp.getBoundsInParent().getMinX(), background.rightUp.getBoundsInParent().getMaxY(), blockHeight, (((screenBounds.getHeight() / 2) - background.rightUp.getHeight())) * 2);

                //Rectangle j = new Rectangle(background.leftUp.getBoundsInParent().getMaxX() - blockHeight, background.leftUp.getBoundsInParent().getMaxY(), blockHeight, (((screenBounds.getHeight() / 2) - background.leftUp.getHeight())) * 2);

                this.relocatePos = new Vecc2f(220, 495);
            }
        }
        this.centerPos = new Vecc2f();
        this.relocatePos.set(this.relocatePos.x * scaleX, this.relocatePos.y * scaleY);
        //
        this.doorFrame.setRotate(rotation);
        this.doorShadow.setRotate(rotation);
        this.doorPartLeft.setRotate(rotation);
        this.doorPartRight.setRotate(rotation);
        this.doorPartRightLocked.setRotate(rotation);
        //
    }

    public Door(float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.state = State.closed;
        this.width = 64;
        this.height = 64;
        this.trapDoor = imageGetter("file:src\\resources\\gfx\\grid\\door_11_trapdoor.png", 0, 0, scaleX, scaleY);
        this.position = new Vecc2f(screenBounds.getWidth() / 2 - ((this.width * spriteScaleX * scaleX) / 2), screenBounds.getHeight() / 2 - ((this.height * spriteScaleY * scaleY) / 2));
        this.doorTrigger = new Rectangle(this.position.x + (16 * spriteScaleX * scaleX), this.position.y + (16 * spriteScaleY * scaleY), (32 * spriteScaleX * scaleX), (32 * spriteScaleY * scaleY));
    }

    public void blowUp(Group group) {
        this.doorFrame.setImage(brokenDoorFrame);
        open(group);

    }

    private ImageView imageGetter(String file, int i, int i1, float scaleX, float scaleY) {
        return new ImageView(imageGetter(file, i * width, i1 * height, width, height, scaleX * spriteScaleX, scaleY * spriteScaleY, 1));
    }

    public void load(Group group) {
        switch (state) {
            case open -> group.getChildren().addAll(this.doorShadow, this.doorFrame, this.doorTrigger);
            case closed -> group.getChildren().addAll(this.doorShadow, this.doorPartLeft, this.doorPartRight, this.doorFrame, this.doorBlock, this.doorTrigger);
            case locked -> group.getChildren().addAll(this.doorShadow, this.doorPartLeft, this.doorPartRightLocked, this.doorFrame, this.doorBlock, this.doorTrigger);
        }
        this.doorFrame.relocate(position.x, position.y);
        this.doorShadow.relocate(position.x, position.y);
        this.doorPartRight.relocate(position.x, position.y);
        this.doorPartRightLocked.relocate(position.x, position.y);
        this.doorPartLeft.relocate(position.x, position.y);
        //
        this.doorFrame.setViewOrder(ViewOrder.door_layer.getViewOrder());
        this.doorShadow.setViewOrder(ViewOrder.door_layer.getViewOrder());
        this.doorPartRight.setViewOrder(ViewOrder.door_layer.getViewOrder());
        this.doorPartRightLocked.setViewOrder(ViewOrder.door_layer.getViewOrder());
        this.doorPartLeft.setViewOrder(ViewOrder.door_layer.getViewOrder());
        //
        this.doorTrigger.toFront();
        this.doorTrigger.setFill(Color.RED);
        this.doorTrigger.setViewOrder(ViewOrder.door_layer.getViewOrder());
        this.doorTrigger.setVisible(false);
        //
        this.doorBlock.toFront();
        this.doorBlock.setFill(Color.GREEN);
        this.doorBlock.setViewOrder(ViewOrder.door_layer.getViewOrder());
        this.doorBlock.setVisible(false);
        //
        this.centerPos.set(this.doorFrame.getBoundsInParent().getCenterX(), this.doorFrame.getBoundsInParent().getCenterY());
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

    public void loadTrapDoor(Group group) {
        group.getChildren().addAll(this.doorTrigger, this.trapDoor);
        this.trapDoor.relocate(this.position.x, this.position.y);
    }

    public void open(Group group) {
        switch (state) {
            case open -> {
            }
            case closed -> {
                group.getChildren().removeAll(this.doorPartLeft, this.doorPartRight, this.doorBlock);
                this.state = State.open;
            }
            case locked -> {
            }
        }
    }

    public void forceOpen(Group group) {
        switch (state) {
            case open -> {
            }
            case closed -> {
                group.getChildren().removeAll(this.doorPartLeft, this.doorPartRight, this.doorBlock);
                this.state = State.open;
            }
            case locked -> {
                group.getChildren().removeAll(this.doorPartLeft, this.doorPartRightLocked, this.doorBlock);
                this.state = State.open;
            }
        }
    }

    public Vecc2f getCenterPos() {
        return centerPos;
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
