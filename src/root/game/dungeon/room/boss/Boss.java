package root.game.dungeon.room.boss;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.effect.ColorAdjust;
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
    Room parentRoom;
    Player playerTarget;
    Random random = new Random();
    //
    int gutNumber;
    //
    String bossName;
    String bossType;
    String filepath;

    float sheetScale;
    Hitbox hitbox;

    int damage;
    Timeline mainline;
    Timeline attack1;
    int attack1Cycle = 0;
    Timeline attack2;
    int attack2Cycle = 0;
    int stateTransitionTimer;
    //super setup variables
    JsonObject template;
    Vecc2f startingTemplatePosition;
    float scaleX, scaleY;
    Rectangle2D screenBounds;
    Shading shadingLayer;
    Room ParentRoom;
    //health bar
    float maxHealth, health;
    ImageView healthBarImage;
    Rectangle healthIndicator;
    Vecc2f healthIndicatorDimensions = new Vecc2f(107, 8);
    Vecc2f healthIndicatorOffset = new Vecc2f(22, 11);
    int healthBarScale = 5;

    public Boss(JsonObject bossTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        this.template = bossTemplate;
        this.startingTemplatePosition = pos;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.screenBounds = screenBounds;
        this.shadingLayer = shading;
        this.parentRoom = parentRoom;
    }

    public void healthBarSetup() {
        this.healthBarImage = new ImageView(imageGetter("file:src\\resources\\gfx\\ui\\ui_bosshealthbar.png", 0, 32, 150, 32, this.scaleX, this.scaleY, this.sheetScale + 2));
        this.healthIndicator = new Rectangle(healthIndicatorDimensions.x * scaleX * healthBarScale, healthIndicatorDimensions.y * scaleY * healthBarScale);
        this.healthIndicator.setFill(Color.rgb(178, 34, 34));
        // healthBarImage = new ImageView("file:src\\resources\\gfx\\ui\\ui_bosshealthbar.png");
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
            //seperationSetter();
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

    protected void debrisCheck(Group group) {
        for (int i = 0; i < gutNumber; i++) {
            int x = (int) (this.hitbox.getCenterX() + (((random.nextFloat() * 4) - 2) * random.nextInt(this.hitbox.getWidth())));
            int y = (int) (this.hitbox.getCenterY() + (((random.nextFloat() * 4) - 2) * random.nextInt(this.hitbox.getHeight())));
            parentRoom.newRealTimeProp(group, x, y, Effects.enemyGuts[random.nextInt(Effects.enemyGuts.length - 1)], 0.5 + (2 * random.nextFloat()));
        }
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

    protected abstract void updateCenterPos();

    public abstract void checkBoundaries();

    protected abstract void checkForPlayer();

    protected abstract void bossSpecificMovement();

    protected abstract void velocityLimit();

    public abstract void applyForce(Vecc2f dir, float magnitude);

    public void inflictDamage(int damage, Group group, ArrayList<Boss> bosses) {
        for (Boss boss : bosses) {//for multiple bosses - if will hide all healthbars then reveal the health bar of the just hit boss.
            boss.hideHealth();
        }
        this.unHideHealth();
        inflictDamageSub(damage, group, bosses);
    }

    protected abstract void inflictDamageSub(int damage, Group group, ArrayList<Boss> bosses);

    protected abstract void postLoader(Group group);

    protected abstract void postUnLoader(Group group);

    public abstract boolean collidesWith(Tear tear);

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