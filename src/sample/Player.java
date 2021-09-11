package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Player {

    float avgScale;

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
    Vecc2f position = new Vecc2f();
    Vecc2f velocity = new Vecc2f();
    Vecc2f acceleration = new Vecc2f();
    //
    Vecc2f xSpeed = new Vecc2f((float) 0.1, 0);
    Vecc2f ySpeed = new Vecc2f((float) 0, (float) 0.1);
    boolean moving;

    //

    float veloLimit;//default is 7 multiplied by screen scale
    //
    boolean northMOVING, eastMOVING, westMOVING, southMOVING;
    //
    Timeline controller;

    //src\resources\gfx\characters\costumes
    //"file:src\resources\gfx\characters\costumes\[costume].png"

    //        this.rock = (new ImageView(new WritableImage(new Image(file, ((new Image(file).getWidth() * scaleX * sheetScale)), ((new Image(file).getHeight() * scaleY * sheetScale)), false, false).getPixelReader(),
    //        (int) ((this.width * imageX)), (int) ((this.height * imageY)), (int) this.width, (int) this.height)));

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
        readImageINTOArray(file, sheetScale, scaleX, scaleY, UD_body, (int) (192 * scaleX * sheetScale), 0);
        //
        readImageINTOArray(file, sheetScale, scaleX, scaleY, LR_body, 0, (int) (64 * sheetScale * scaleY));
        //
        this.body.setImage(UD_body[2]);
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
            //
            System.out.println(moving);
            accDecider();
            this.acceleration.limit(2 * avgScale);
            this.velocity.add(this.acceleration);
            //
            this.velocity.mult((float) 0.95);
            if (this.velocity.magnitude() < 0.2) {
                this.velocity.set(0, 0);
                this.position.set((int)this.position.x,(int)this.position.y);
                relocate();
            }
            this.velocity.limit(veloLimit);
            //
            this.position.add(this.velocity);
            relocate();
            //
            if (moving){

            }

        }));
        controller.setCycleCount(Timeline.INDEFINITE);
        controller.play();
    }

    private void accDecider() {
        this.moving = true;
        this.acceleration = northMOVING ? (acceleration.sub(ySpeed)) : this.acceleration;
        this.acceleration = southMOVING ? (acceleration.add(ySpeed)) : this.acceleration;
        //
        this.acceleration = eastMOVING ? (acceleration.sub(xSpeed)) : this.acceleration;
        this.acceleration = westMOVING ? (acceleration.add(xSpeed)) : this.acceleration;
        //
        if (northMOVING && southMOVING) {
            acceleration.y = 0;
            velocity.y = 0;
            moving = false;
        }
        if (eastMOVING && westMOVING) {
            acceleration.x = 0;
            velocity.x = 0;
            moving = false;
        }
        if (!northMOVING && !westMOVING && !eastMOVING && !southMOVING) {
            this.acceleration.set(0, 0);
            moving = false;
        }
    }

    private void relocate() {
        this.body.relocate(position.x, position.y);
        this.head.relocate(position.x + headOffset.x, position.y + headOffset.y);
        this.headHitbox.getShape().relocate(position.x + headDelta.x - (this.headHitbox.radius), position.y + headDelta.y - (this.headHitbox.radius));
        this.bodyHitbox.getShape().relocate(position.x + bodyDelta.x, position.y + bodyDelta.y);
    }

    private void readImageINTOArray(String file, int sheetScale, float scaleX, float scaleY, Image[] ARRAY, int startX, int startY) {
        for (int i = 0; i < ARRAY.length; i++) {
            ARRAY[i] = (new ImageView(new WritableImage(new Image(file, ((new Image(file).getWidth() * scaleX * sheetScale)), ((new Image(file).getHeight() * scaleY * sheetScale)), false, false).getPixelReader(),
                    startX, startY, (int) this.width, (int) this.height))).getImage();
            startX = startX + this.width;
            if (startX >= 256 * sheetScale * scaleX) {
                startX = 0;
                startY = startY + this.height;
            }
        }
    }

    public void load(Group group) {
        group.getChildren().addAll(this.headHitbox.getShape(), this.bodyHitbox.getShape(), this.body, this.head);
        //
        this.headHitbox.getShape().setViewOrder(-7);
        this.bodyHitbox.getShape().setViewOrder(-7);
        this.body.setViewOrder(-7);
        this.head.setViewOrder(-7);
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
