package root.game.dungeon.room.boss;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.util.Duration;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.Tear.Tear;
import root.game.dungeon.room.enemy.Enemy;
import root.game.player.Player;
import root.game.util.*;

import java.util.ArrayList;
import java.util.Random;

public abstract class Boss implements Sprite_Splitter {

    Group parentGroup;
    Player playerTarget;
    Random random = new Random();
    //
    int gutNumber;
    //
    String bossName;
    String bossType;
    String filepath;

    ImageView boss;
    Vecc2f position;

    float sheetScale;
    Hitbox hitbox;
    float veloLimit;
    Vecc2f velocity = new Vecc2f(0, 0);
    public Vecc2f centerPos = new Vecc2f(0, 0);
    int damage;
    Timeline mainline;
    //
    int stateTransitionTimer;
    //super setup variables
    JsonObject template;
    Vecc2f startingTemplatePosition;
    float scaleX, scaleY;
    Rectangle2D screenBounds;
    Shading shadingLayer;
    Room parentRoom;
    //health bar
    float maxHealth, health;
    ImageView healthBarImage;
    ImageView healthIndicator;

    Vecc2f healthIndicatorDimensions = new Vecc2f(112, 10);
    Vecc2f healthIndicatorOffset = new Vecc2f(19, 10);

    int healthBarScale = 5;
    //image swapping
    //int imageSwapIntervalCounter = 0;
    //int imageCounter = 0;

    int[] imageSwapIntervalCounter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};//10
    int[] imageCounter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};//10


    public Boss(JsonObject bossTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        this.template = bossTemplate;
        this.bossType = bossTemplate.get("type").getAsString();
        this.filepath = bossTemplate.get("filePath").getAsString();
        this.startingTemplatePosition = new Vecc2f(pos.x * scaleX, pos.y * scaleY);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.screenBounds = screenBounds;
        this.shadingLayer = shading;
        this.parentRoom = parentRoom;
        try {
            this.gutNumber = bossTemplate.get("GutNumber").getAsInt();
        } catch (Exception e) {//'guts' are not necessary in the template
            this.gutNumber = 0;
        }
    }

    public void healthBarSetup() {
        this.healthBarImage = new ImageView(imageGetter("file:src\\resources\\gfx\\ui\\ui_bosshealthbar.png", 0, 32, 150, 32, this.scaleX, this.scaleY, healthBarScale));
        this.healthIndicator = new ImageView(imageGetter("file:src\\resources\\gfx\\ui\\ui_bosshealthbar.png", (int) (healthIndicatorOffset.x), (int) (healthIndicatorOffset.y), (int) (healthIndicatorDimensions.x), (int) (healthIndicatorDimensions.y), this.scaleX, this.scaleY, healthBarScale));
    }

    public void timelineSetup() {
        mainline = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            //every enemy will have the base of shader checking,boundary checking & updating of center pos
            //removeShader();
            updateCenterPos();
            //timers that may be used amongst bosses.
            stateTransitionTimer++;
            //
            velocityLimit();
            //
            seperationSetter();
            //
            bossSpecificMovement();//will be overridden for each enemy
            //
            checkBoundaries();
            checkForPlayer();
            //
            //addShader();
        }));
        mainline.setCycleCount(Timeline.INDEFINITE);
    }

    protected void beginRemoval(Group group, ArrayList<Boss> bosses) {
        unload(group);
        bosses.remove(this);
        parentRoom.checkDoors(group);
        debrisCheck(group);
    }

    protected void debrisCheck(Group group) {
        for (int i = 0; i < gutNumber; i++) {
            int x = (int) (this.hitbox.getCenterX() + (((random.nextFloat() * 4) - 2) * random.nextInt(this.hitbox.getWidth())));
            int y = (int) (this.hitbox.getCenterY() + (((random.nextFloat() * 4) - 2) * random.nextInt(this.hitbox.getHeight())));
            parentRoom.newRealTimeProp(group, x, y, Effects.enemyGuts[random.nextInt(Effects.enemyGuts.length - 1)], 0.5 + (2 * random.nextFloat()));
        }
    }

    public void seperationSetter() {
        Vecc2f seperation = seperation(this.centerPos, this.velocity);
        applyForce(seperation.limit(1), 0.6f);
    }

    public Vecc2f seperation(Vecc2f position, Vecc2f velocity) {
        Vecc2f steering = new Vecc2f();
        int total = 0;
        //
        for (Boss boss : parentRoom.bosses) {
            total = getTotal(position, steering, total, boss.centerPos);
        }
        for (Enemy enemy : parentRoom.enemies) {
            total += getTotal(position, steering, total, enemy.centerPos);
        }
        if (total > 0) {
            steering.div(total);
            steering.setMag(12);
            steering.sub(velocity);
            steering.limit((float) 0.5);
        }
        steering.mult((float) 2);
        return steering;
    }

    private int getTotal(Vecc2f position, Vecc2f steering, int total, Vecc2f position2) {
        float d;
        d = (position.distance(position2));
        if ((d < (100 * ((scaleX + scaleY) / 2))) && position != position2) {
            Vecc2f difference = new Vecc2f().sub(position, position2);
            difference.div(d * d);
            steering.add(difference);
            total++;
        }
        return total;
    }

    public int linearImageSwapper(ImageView bossImage, Image[] images, int swapInterval, int ARRAY_LOCATION) {
        try {
            if (++imageSwapIntervalCounter[ARRAY_LOCATION] >= swapInterval) {
                bossImage.setImage(images[imageCounter[ARRAY_LOCATION]]);
                imageCounter[ARRAY_LOCATION]++;
                if (imageCounter[ARRAY_LOCATION] > images.length - 1) {
                    imageCounter[ARRAY_LOCATION] = 0;
                }
                imageSwapIntervalCounter[ARRAY_LOCATION] = 0;
                return imageCounter[ARRAY_LOCATION];
            }
            return -1;
        } catch (Exception e) {
            System.out.println("The ARRAY LOCATION provided( " + ARRAY_LOCATION + " )must be within the array with a size of: " + imageCounter.length);
        }
        return -1;
    }

    public void load(Group group, Player player) {
        this.playerTarget = player;
        this.parentGroup = group;
        //when a boss is loaded it will always have a health-bar
        group.getChildren().addAll(this.healthBarImage, this.healthIndicator);
        this.healthBarImage.relocate(((this.screenBounds.getWidth() / 2) - (this.healthBarImage.getBoundsInParent().getWidth() / 2)),
                (150 - this.healthBarImage.getBoundsInParent().getHeight() / 2) * scaleY);
        this.healthBarImage.setViewOrder(ViewOrder.UI_layer.getViewOrder());
        this.healthBarImage.setOpacity(0.9);
        //
        this.healthIndicator.relocate(this.healthBarImage.getLayoutX() + (healthIndicatorOffset.x * scaleX * healthBarScale),
                this.healthBarImage.getLayoutY() + (healthIndicatorOffset.y * scaleY * healthBarScale));
        this.healthIndicator.setViewOrder(ViewOrder.UI_layer.getViewOrder());
        this.healthIndicator.setOpacity(0.9);
        //
        postLoader(group);
    }

    public void unload(Group group) {
        //
        group.getChildren().removeAll(this.healthBarImage, this.healthIndicator);
        //
        postUnLoader(group);
    }

    protected abstract void bossSpecificMovement();

    protected abstract void updateCenterPos();

    public abstract void checkBoundaries();

    protected void checkForPlayer() {
        if ((this.hitbox.getShape().getBoundsInParent().intersects(playerTarget.getBodyHitbox().getShape().getBoundsInParent()) ||
                this.hitbox.getShape().getBoundsInParent().intersects(playerTarget.getHeadHitbox().getShape().getBoundsInParent())) && playerTarget.isVulnerable()) {
            //
            Vecc2f dir = new Vecc2f(this.hitbox.getCenterX(), this.hitbox.getCenterY()).sub(playerTarget.getCenterPos());

            Vecc2f originalVELO = new Vecc2f(velocity.x, velocity.y);

            Vecc2f enemyPushback = new Vecc2f(velocity.x, velocity.y);
            enemyPushback.mult(-1);
            enemyPushback.setMag((velocity.magnitude() < veloLimit * 0.25) ? (veloLimit) : (velocity.magnitude()));//if enemy is 'slow' the push back is adjusted
            enemyPushback.fromAngle(dir.toAngle());
            applyForce(enemyPushback, 0.3f);
            //
            playerTarget.inflictDamage(1);//TODO REMEMBER current default enemy damage is 1
            Vecc2f pushback = new Vecc2f(originalVELO.x, originalVELO.y);
            pushback.setMag((originalVELO.magnitude() < veloLimit * 0.25) ? (veloLimit) : (originalVELO.magnitude()));//if enemy is 'slow' the push back is adjusted
            pushback.fromAngle(dir.toAngle() - 180);

            playerTarget.applyForce(pushback, 6);
        }
    }

    protected abstract void velocityLimit();

    public abstract void applyForce(Vecc2f dir, float magnitude);

    public void setVeloLimit(float veloLimit) {
        this.veloLimit = veloLimit * ((this.scaleX + this.scaleY) / 2);
    }

    public void inflictDamage(int damage, Group group, ArrayList<Boss> bosses) {
        this.health -= damage;
        this.health = Math.max(this.health, 0);
        try {
            this.healthIndicator.setImage(imageGetter("file:src\\resources\\gfx\\ui\\ui_bosshealthbar.png", (int) (healthIndicatorOffset.x), (int) (healthIndicatorOffset.y),
                    (int) ((healthIndicatorDimensions.x) * (this.health / this.maxHealth)), (int) (healthIndicatorDimensions.y), this.scaleX, this.scaleY, healthBarScale));
        } catch (IllegalArgumentException e) {//when health is 0 - image width set to 0
            this.healthIndicator.setImage(imageGetter("file:src\\resources\\gfx\\ui\\ui_bosshealthbar.png", 1, 1, 1, 1, 1, 1, 1));
        }
        //
        for (Boss boss : bosses) {//for multiple bosses - it will hide all healthbars then reveal the health bar of the just hit boss.
            boss.hideHealth();
        }
        this.unHideHealth();
        inflictDamageSub(damage, group, bosses);
    }

    protected abstract void inflictDamageSub(int damage, Group group, ArrayList<Boss> bosses);

    public abstract boolean collidesWith(Tear tear);

    protected abstract void postLoader(Group group);

    protected abstract void postUnLoader(Group group);

    protected void hideHealth() {
        this.healthBarImage.setVisible(false);
        this.healthIndicator.setVisible(false);
    }

    //
    protected void unHideHealth() {
        this.healthBarImage.setVisible(true);
        this.healthIndicator.setVisible(true);
    }
}