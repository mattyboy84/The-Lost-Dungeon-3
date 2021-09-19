package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Arrays;

public class Player {

    //
    int XAnimateCounter, YAnimateCounter;
    //
    float avgScale;
    String movingDirection;
    int roomX, roomY;
    String costume;
    Image[] heads = new Image[6];
    Image[] LR_body = new Image[10];
    Image[] UD_body = new Image[10];
    //
    ImageView head = new ImageView();
    ImageView body = new ImageView();
    //
    int width, height;
    //
    Vecc2f VECscale = new Vecc2f();
    Vecc2f bodyOffset, headOffset;
    Vecc2f bodyDelta, headDelta;
    Hitbox headHitbox, bodyHitbox;
    //
    Vecc2f direction = new Vecc2f();
    Vecc2f position = new Vecc2f();
    Vecc2f velocity = new Vecc2f();
    Vecc2f acceleration = new Vecc2f();
    //
    Vecc2f xSpeed = new Vecc2f((float) 0.1, 0);
    Vecc2f ySpeed = new Vecc2f((float) 0, (float) 0.1);
    boolean moving;
    //timers;
    int animationTimer;

    //

    float veloLimit;//default is 7 multiplied by screen scale
    //
    boolean northMOVING, eastMOVING, westMOVING, southMOVING;
    //
    Timeline controller;

    public void Generate(String costume, int startX, int startY, float scaleX, float scaleY, Rectangle2D screenBounds, int sheetScale) {
        this.avgScale = ((scaleX + scaleY) / 2);
        //
        xSpeed.mult(scaleX);
        ySpeed.mult(scaleY);
        veloLimit = 7 * avgScale;
        VECscale.set(scaleX, scaleY);
        //
        bodyOffset = new Vecc2f(0 * scaleX, 0 * scaleY);
        headOffset = new Vecc2f(0 * scaleX, -10 * scaleY);
        bodyDelta = new Vecc2f(8 * scaleX, 13 * scaleY);
        headDelta = new Vecc2f(16 * scaleX, 4 * scaleY);
        bodyOffset.mult(sheetScale);
        headOffset.mult(sheetScale);
        bodyDelta.mult(sheetScale);
        headDelta.mult(sheetScale);
        //
        this.width = (int) (32 * scaleX * sheetScale);
        this.height = (int) (32 * scaleY * sheetScale);
        //
        this.costume = costume;
        this.roomX = startX;
        this.roomY = startY;
        String file = "file:src\\resources\\gfx\\characters\\costumes\\" + this.costume + ".png";
        for (int i = 0; i < this.heads.length; i++) {//head images
            this.heads[i] = (new ImageView(new WritableImage(new Image(file, ((new Image(file).getWidth() * scaleX * sheetScale)), ((new Image(file).getHeight() * scaleY * sheetScale)), false, false).getPixelReader(),
                    (this.width * i), 0, (int) this.width, (int) this.height))).getImage();
        }
        //
        readImageINTOArray(file, sheetScale, scaleX, scaleY, UD_body, (int) Math.round(192 * scaleX * sheetScale), 0);
        //
        readImageINTOArray(file, sheetScale, scaleX, scaleY, LR_body, 0, (int) Math.round(64 * sheetScale * scaleY));
        //
        this.body.setImage(UD_body[2]);//default is 2
        this.head.setImage(heads[0]);
        //
        bodyHitbox = new Hitbox("Rectangle", 16, 11, sheetScale, scaleX, scaleY, 8, 13);
        //int radius = (int) (13 * sheetScale * (scaleX + scaleY) / 2);
        headHitbox = new Hitbox("Circle", 12, 12, sheetScale, scaleX, scaleY, 16, 4);
        //caching
        {
            this.body.setCache(true);
            this.body.setCacheHint(CacheHint.QUALITY);
            this.head.setCache(true);
            this.head.setCacheHint(CacheHint.QUALITY);
            this.bodyHitbox.getShape().setCache(true);
            this.bodyHitbox.getShape().setCacheHint(CacheHint.QUALITY);
            this.headHitbox.getShape().setCache(true);
            this.headHitbox.getShape().setCacheHint(CacheHint.QUALITY);
        }

        //
        playerController();
        //
    }

    private void playerController() {
        controller = new Timeline(new KeyFrame(Duration.seconds((float) 1 / 60), event -> {
            //timers
            animationTimer++;

            //
            this.direction.set(velocity.x, velocity.y);
            this.direction.limit(1);
            accDecider();
            this.acceleration.limit(2 * avgScale);
            this.velocity.add(this.acceleration);
            //
            this.velocity.mult((float) 0.95);
            if (this.velocity.magnitude() < 0.2) {
                moving = false;
                this.velocity.set(0, 0);
                this.position.set((int) this.position.x, (int) this.position.y);
                this.body.setImage(UD_body[2]);
                relocate();
            }
            this.velocity.limit(veloLimit);
            //
            this.position.add(this.velocity);
            relocate();
            //
            if (moving) {
                if (animationTimer >= 6) {
                    heroAnimator();
                    animationTimer = 0;
                }
            }

        }));
        controller.setCycleCount(Timeline.INDEFINITE);
        controller.play();
    }

    private void accDecider() {
        this.acceleration.mult((float) 0.95);
        //
        this.acceleration = northMOVING ? (acceleration.sub(ySpeed)) : this.acceleration;
        this.acceleration = southMOVING ? (acceleration.add(ySpeed)) : this.acceleration;
        //
        this.acceleration = eastMOVING ? (acceleration.add(xSpeed)) : this.acceleration;
        this.acceleration = westMOVING ? (acceleration.sub(xSpeed)) : this.acceleration;
        //
        if (eastMOVING && westMOVING) {
            acceleration.x = 0;
            velocity.x = 0;
        }
        if (northMOVING && southMOVING) {
            acceleration.y = 0;
            velocity.y = 0;
        }
        if (!northMOVING && !westMOVING && !eastMOVING && !southMOVING) {
            this.acceleration.set(0, 0);
        }
        if (this.velocity.magnitude() > 0.2) {
            this.moving = true;
        }
    }
    
    private void readImageINTOArray(String file, int sheetScale, float scaleX, float scaleY, Image[] ARRAY, int startX, int startY) {
        for (int i = 0; i < ARRAY.length; i++) {
            ARRAY[i] = (new ImageView(new WritableImage(new Image(file, ((int) (new Image(file).getWidth() * scaleX * sheetScale)), ((int) (new Image(file).getHeight() * scaleY * sheetScale)), false, false).getPixelReader(),
                    startX, startY, (int) this.width, (int) this.height))).getImage();
            startX = startX + this.width;
            if (startX >= (int) this.width*8) {
                startX = 0;
                startY = startY + this.height;
            }
        }
    }

    private void heroAnimator() {

        float angle = this.direction.toAngle();
        if (angle > 45 && angle < 135) {//right
            this.movingDirection = "right";
            body.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            subHeroAnimterX();
        } else if (angle > 135 && angle < 225) {//down
            this.movingDirection = "down";
            subHeroAnimterY();
        } else if (angle > 225 && angle < 315) {//left
            this.movingDirection = "left";
            body.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            subHeroAnimterX();
        } else if (angle > 315 || angle < 45) {//up
            this.movingDirection = "up";
            subHeroAnimterY();
        }
    }

    private void subHeroAnimterY() {
        YAnimateCounter++;
        if (YAnimateCounter > UD_body.length - 1) {
            YAnimateCounter = 0;
        }
        body.setImage(UD_body[YAnimateCounter]);
    }

    private void subHeroAnimterX() {
        XAnimateCounter++;
        if (XAnimateCounter > LR_body.length - 1) {
            XAnimateCounter = 0;
        }
        body.setImage(LR_body[XAnimateCounter]);
    }


    private void relocate() {
        this.body.relocate(position.x, position.y);
        this.head.relocate(position.x + headOffset.x, position.y + headOffset.y);
        this.headHitbox.getShape().relocate(position.x + headDelta.x - (this.headHitbox.radius), position.y + headDelta.y - (this.headHitbox.radius));
        this.bodyHitbox.getShape().relocate(position.x + bodyDelta.x, position.y + bodyDelta.y);
    }

    public void load(Group group) {
        group.getChildren().addAll(this.headHitbox.getShape(), this.bodyHitbox.getShape(), this.body, this.head);
        //
        this.headHitbox.getShape().setViewOrder(-7);
        this.bodyHitbox.getShape().setViewOrder(-7);
        this.body.setViewOrder(-7);
        this.head.setViewOrder(-7);
        //
        this.body.setVisible(true);
        this.head.setVisible(true);
        this.headHitbox.shape.setVisible(false);
        this.bodyHitbox.shape.setVisible(false);
        //
        this.position.set(800, 400);
        relocate();

    }

    public boolean isNorthMOVING() {
        return northMOVING;
    }

    public void setNorthMOVING(boolean northMOVING) {
        this.northMOVING = northMOVING;
    }

    public boolean isEastMOVING() {
        return eastMOVING;
    }

    public void setEastMOVING(boolean eastMOVING) {
        this.eastMOVING = eastMOVING;
    }

    public boolean isWestMOVING() {
        return westMOVING;
    }

    public void setWestMOVING(boolean westMOVING) {
        this.westMOVING = westMOVING;
    }

    public boolean isSouthMOVING() {
        return southMOVING;
    }

    public void setSouthMOVING(boolean southMOVING) {
        this.southMOVING = southMOVING;
    }
}
