package root.game.Tear;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import root.Main;
import root.game.dungeon.room.boss.Boss;
import root.game.dungeon.room.enemy.Enemy;
import root.game.music.Music;
import root.game.util.*;

import java.util.ArrayList;
import java.util.Random;

public class Tear implements Sprite_Splitter {
    ImageView tearImage = new ImageView();
    ImageView shadowImage = new ImageView();
    public Hitbox tearHitbox;
    int damage;
    Vecc2f position, shadowPosition;
    Vecc2f velocity;

    Timeline tearTimeline;
    Timeline explodeTimeline;
    int explodeCounter = 1;
    float flyDistance = 650;
    float travelDistance = 0;
    float yDrop = 0.5f;
    float yAcc = 0.09f;
    float avgScale;
    Random random = new Random();

    public Target target;

    public enum Target {
        player,
        enemy
    }

    public Tear() {

    }

    public Tear(String direction, int damage, Group group, Vecc2f position, Vecc2f velocity, float scaleX, float scaleY, float baseVELO, ArrayList<Tear> tears, ArrayList<Enemy> enemies, ArrayList<Boss> bosses, ArrayList<Rectangle> boundaries, Target tearTarget) {
        this.target = tearTarget;
        Music.addSFX(false, this.hashCode(), Music.sfx.tear_fire_4, Music.sfx.tear_fire_5);
        this.avgScale = ((scaleX + scaleY) / 2);
        this.position = new Vecc2f(position);
        this.damage = damage;
        this.shadowPosition = new Vecc2f(position);
        this.shadowPosition.add(0, 30 * (scaleY));//scaled 30 pixels under the tear to start
        this.velocity = new Vecc2f(velocity.x / 2, velocity.y / 2);
        this.flyDistance *= scaleY;
        //
        switch (direction) {
            case "north":
                this.velocity.add(0, (float) (-baseVELO * 1.5));
                break;
            case "south":
                this.velocity.add(0, (float) (baseVELO * 1.5));
                break;
            case "west":
                this.velocity.add((float) (-baseVELO * 1.5), 0);
                break;
            case "east":
                this.velocity.add((float) (baseVELO * 1.5), 0);
                break;
        }
        //
        float scale = 2.5f;

        if (target == Target.player)
            damage = damage + 4;

        if (damage > 12) {
            damage = 12;
        }

        this.tearHitbox = new Hitbox("Circle", damage + 1, damage + 1, scale, scaleX, scaleY, damage, damage);
        this.tearHitbox.getShape().setVisible(false);
        float a = (float) (((this.tearHitbox.radius)) / new Image("file:src\\resources\\gfx\\shadow.png").getWidth()) * 2;//scale shadow to tear size

        if (target == Target.enemy)
            this.tearImage.setImage(imageGetter("file:src\\resources\\gfx\\tears.png", (32 * ((damage > 7) ? (damage - 7) : (damage))), (32 * ((damage > 7) ? (1) : (0))), 32, 32, scaleX, scaleY, scale));
        else if (target == Target.player) {
            this.tearImage.setImage(imageGetter("file:src\\resources\\gfx\\tears.png", (32 * ((damage > 7) ? (damage - 7) : (damage))), (32 * ((damage > 7) ? (1) : (0))) + 64, 32, 32, scaleX, scaleY, scale));
        }
        //
        this.shadowImage.setImage(imageGetter("file:src\\resources\\gfx\\shadow.png", 0, 0, (int) (120), (int) (49), a, a, 1));
        this.shadowImage.setViewOrder(ViewOrder.player_attacks_layer.getViewOrder());
        //
        group.getChildren().addAll(this.tearImage, this.tearHitbox.getShape(), this.shadowImage);
        if (target == Target.enemy) {
            this.tearImage.setViewOrder(ViewOrder.player_attacks_layer.getViewOrder());
        } else if (target == Target.player) {
            this.tearImage.setViewOrder(ViewOrder.enemy_boss_attacks_layer.getViewOrder());
        }
        this.shadowImage.setViewOrder(ViewOrder.background_layer.getViewOrder());

        this.tearImage.relocate(this.position.x - this.tearImage.getBoundsInParent().getWidth() / 2, this.position.y - this.tearImage.getBoundsInParent().getHeight() / 2);
        //
        timeline(tears, enemies, bosses, boundaries, group);
        explodeTimelineSetup();
    }

    protected void explodeTimelineSetup() {
        explodeTimeline = new Timeline(new KeyFrame(Duration.millis(70), event -> {
            if (target == Target.enemy)
                this.tearImage.setImage(Effects.BLUEtearCollideAnimation[explodeCounter]);//runs through the collision animation
            else if (target == Target.player)
                this.tearImage.setImage(Effects.REDtearCollideAnimation[explodeCounter]);
            explodeCounter++;
        }));
        explodeTimeline.setCycleCount(Effects.BLUEtearCollideAnimation.length - 1);
    }

    private void timeline(ArrayList<Tear> tears, ArrayList<Enemy> enemies, ArrayList<Boss> bosses, ArrayList<Rectangle> boundaries, Group group) {
        tearTimeline = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            this.travelDistance += this.velocity.magnitude();
            this.position.add(this.velocity);
            if (travelDistance > flyDistance) {//after 'flyDistance' pixels, the tear will start to fall
                this.position.add(0, yDrop);
                yDrop += yAcc;
            }
            this.shadowPosition.add(this.velocity);
            this.tearImage.relocate(this.position.x - this.tearImage.getBoundsInParent().getWidth() / 2, this.position.y - this.tearImage.getBoundsInParent().getHeight() / 2);
            this.shadowImage.relocate((this.shadowPosition.x - this.shadowImage.getBoundsInParent().getWidth() / 2) + 1, this.shadowPosition.y);
            this.tearHitbox.getShape().relocate(this.position.x - this.tearHitbox.getxDelta(), this.position.y - this.tearHitbox.getyDelta());
            //
            boundaryCheck(boundaries, group, tears);
            shadowCheck(group, tears);//when the hitbox hits the center of the shadow it will hit the floor and explode
            //
            if (target == Target.enemy) {
                enemyCheck(enemies, group, tears);//when enemy & tear hitbox overlap, the enemy will be damaged and pushed
                bossCheck(bosses, group, tears);
            }
            //
            else if (target == Target.player)
                playerCheck(group, tears);//when a player and enemy's tear overlap, the player will be damaged and pushed.
        }));
        tearTimeline.setCycleCount(Timeline.INDEFINITE);
        tearTimeline.play();
    }

    private void playerCheck(Group group, ArrayList<Tear> tears) {
        if (this.tearHitbox.getShape().getBoundsInParent().intersects(Main.player.getHeadHitbox().getShape().getBoundsInParent()) ||
                this.tearHitbox.getShape().getBoundsInParent().intersects(Main.player.getBodyHitbox().getShape().getBoundsInParent()) && Main.player.isVulnerable()) {//overlap & vulnerable
            Main.player.inflictDamage(this.damage);
            Main.player.applyForce(new Vecc2f(this.velocity.x, this.velocity.y).limit(1), (int) (10 * this.avgScale));
            //
            System.out.println("Player hit by Enemy tear");
            hitSomething(group, tears);
        }
    }

    private void shadowCheck(Group group, ArrayList<Tear> tears) {
        if (this.tearHitbox.getShape().getBoundsInParent().intersects(this.shadowPosition.x + this.shadowImage.getBoundsInParent().getWidth() / 2, this.shadowPosition.y + this.shadowImage.getBoundsInParent().getHeight() / 2, 1, 1)) {
            hitSomething(group, tears);
        }
    }

    private void enemyCheck(ArrayList<Enemy> enemies, Group group, ArrayList<Tear> tears) {//check for enemies
        try {
            for (int k = enemies.size() - 1; k > -1; k--) {
                if (enemies.get(k) != null && enemies.get(k).collidesWith(this) && !(enemies.get(k).state == Enemy.states.dying)) {

                    //System.out.println("enemy Hit");
                    hitSomething(group, tears);
                    //
                    enemies.get(k).applyForce(new Vecc2f(this.velocity.x, this.velocity.y).limit(1), 10 * this.avgScale);
                    enemies.get(k).inflictDamage(damage, group, enemies);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void bossCheck(ArrayList<Boss> bosses, Group group, ArrayList<Tear> tears) {
        for (Boss boss : bosses) {
            if (boss != null && boss.collidesWith(this)) {

            }
        }
    }

    public void boundaryCheck(ArrayList<Rectangle> boundaries, Group group, ArrayList<Tear> tears) {//check for boundaries
        for (Rectangle boundary : boundaries) {
            if (boundary.getBoundsInParent().intersects(this.tearHitbox.getShape().getBoundsInParent())) {
                hitSomething(group, tears);
                break;
            }
        }
    }

    protected void hitSomething(Group group, ArrayList<Tear> tears) {
        Music.addSFX(false, this.hashCode(), Music.sfx.splatter_0, Music.sfx.splatter_1, Music.sfx.splatter_2, Music.sfx.splatter_3, Music.sfx.splatter_4, Music.sfx.splatter_5);
        //
        this.tearTimeline.stop();
        group.getChildren().removeAll(this.tearHitbox.getShape(), this.shadowImage);
        if (target == Target.enemy)
            this.tearImage.setImage(Effects.BLUEtearCollideAnimation[0]);
        else if (target == Target.player)
            this.tearImage.setImage(Effects.REDtearCollideAnimation[0]);
        this.position.sub(this.tearImage.getBoundsInParent().getWidth() / 2, this.tearImage.getBoundsInParent().getHeight() / 2);
        this.tearImage.relocate(this.position.x, this.position.y);

        this.explodeTimeline.play();
        this.explodeTimeline.setOnFinished(Event -> {
            destroy(group, tears);
        });
    }

    public void destroy(Group group, ArrayList<Tear> tears) {
        //System.out.println("Tear destroyed");
        group.getChildren().remove(this.tearImage);//this component is always on screen
        try {
            group.getChildren().remove(this.tearHitbox.getShape());//hitbox is removed when colliding but also needs to be removed when leaving a room - error would occur otherwise.
        } catch (Exception e) {
        }
        try {
            this.tearTimeline.stop();
        } catch (Exception e) {
        }
        try {
            this.explodeTimeline.stop();
        } catch (Exception e) {
        }
        try {
            group.getChildren().remove(this.shadowImage);
        } catch (Exception e) {
        }
        tears.remove(this);
    }
}