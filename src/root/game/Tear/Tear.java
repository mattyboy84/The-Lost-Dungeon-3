package root.game.Tear;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import root.game.dungeon.room.boss.Boss;
import root.game.dungeon.room.enemy.Enemy;
import root.game.music.Music;
import root.game.player.Player;
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
    float scaleX;
    float scaleY;
    Random random = new Random();
    int tearSize;

    public Tear() {

    }

    public Tear(float scaleX, float scaleY, Vecc2f position, int damage, Vecc2f velocity,int veloDELTA, int tearSize) {
        Music.addSFX(false, this.hashCode(), Music.sfx.tear_fire_4, Music.sfx.tear_fire_5);
        this.avgScale = ((scaleX + scaleY) / 2);
        this.scaleX=scaleX;
        this.scaleY=scaleY;
        this.position = new Vecc2f(position);
        this.damage = damage;
        this.shadowPosition = new Vecc2f(position);
        this.shadowPosition.add(0, 30 * (scaleY));//scaled 30 pixels under the tear to start
        this.velocity = new Vecc2f(velocity.x / veloDELTA, velocity.y / veloDELTA);
        this.flyDistance *= scaleY;
        this.tearSize= Math.min(tearSize, 12);
        //

    }

    protected void explodeTimelineSetup() {
        explodeTimeline = new Timeline(new KeyFrame(Duration.millis(70), event -> {
            tearColourEffect(explodeCounter);
            explodeCounter++;
        }));
        explodeTimeline.setCycleCount(Effects.BLUEtearCollideAnimation.length - 1);
    }

    public void playerCheck(Player player, Group group, ArrayList<Tear> tears) {
        if ((this.tearHitbox.getShape().getBoundsInParent().intersects(player.getHeadHitbox().getShape().getBoundsInParent()) ||
                this.tearHitbox.getShape().getBoundsInParent().intersects(player.getBodyHitbox().getShape().getBoundsInParent())) && player.isVulnerable()) {//overlap & vulnerable
            player.inflictDamage(this.damage);
            player.applyForce(new Vecc2f(this.velocity.x, this.velocity.y).limit(1), (int) (10 * this.avgScale));
            //
            System.out.println("Player hit by Enemy tear");
            hitSomething(group, tears);
        }
    }

    public void shadowCheck(Group group, ArrayList<Tear> tears) {
        if (this.tearHitbox.getShape().getBoundsInParent().intersects(this.shadowPosition.x + this.shadowImage.getBoundsInParent().getWidth() / 2, this.shadowPosition.y + this.shadowImage.getBoundsInParent().getHeight() / 2, 1, 1)) {
            hitSomething(group, tears);
        }
    }

    public void enemyCheck(ArrayList<Enemy> enemies, Group group, ArrayList<Tear> tears) {//check for enemies
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

    public void bossCheck(ArrayList<Boss> bosses, Group group, ArrayList<Tear> tears) {
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

    public void hitSomething(Group group, ArrayList<Tear> tears) {
        Music.addSFX(false, this.hashCode(), Music.sfx.splatter_0, Music.sfx.splatter_1, Music.sfx.splatter_2, Music.sfx.splatter_3, Music.sfx.splatter_4, Music.sfx.splatter_5);
        //
        this.tearTimeline.stop();
        group.getChildren().removeAll(this.tearHitbox.getShape(), this.shadowImage);

        tearColourEffect(0);
        this.position.sub(this.tearImage.getBoundsInParent().getWidth() / 2, this.tearImage.getBoundsInParent().getHeight() / 2);
        this.tearImage.relocate(this.position.x, this.position.y);

        this.explodeTimeline.play();
        this.explodeTimeline.setOnFinished(Event -> {
            destroy(group, tears);
        });
    }

    public void tearColourEffect(int explodeCounter) {
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