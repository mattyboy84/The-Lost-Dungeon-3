package root.game.player;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import root.game.dungeon.room.enemy.Enemy;
import root.game.util.Effects;
import root.game.util.Hitbox;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

import java.util.ArrayList;

public class Tear implements Sprite_Splitter {
    ImageView tearImage = new ImageView();
    ImageView shadowImage = new ImageView();
    Hitbox tearHitbox;
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


    public Tear(String direction, int damage, Group group, Vecc2f position, Vecc2f velocity, float scaleX, float scaleY, float baseVELO, ArrayList<Tear> tears, ArrayList<Enemy> enemies, ArrayList<Rectangle> boundaries) {
        this.position = new Vecc2f(position);
        this.damage = damage;
        this.shadowPosition = new Vecc2f(position);
        this.shadowPosition.add(0, 30 * (scaleY));//scaled 30 pixels under the tear to start
        this.velocity = new Vecc2f(velocity);
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
        //this.velocity.fromAngle(this.velocity.toAngle()*0.8);
        //
        float scale = 2.5f;

        if (damage > 12) {
            damage = 12;
        }

        this.tearHitbox = new Hitbox("Circle", damage + 1, damage + 1, scale, scaleX, scaleY, damage, damage);
        this.tearHitbox.getShape().setVisible(false);
        this.tearImage.setImage(imageGetter("file:src\\resources\\gfx\\tears.png", 32 * damage, 32 * ((damage > 7) ? (1) : (0)), 32, 32, scaleX, scaleY, scale));
        //
        float a = (float) (((this.tearHitbox.radius)) / new Image("file:src\\resources\\gfx\\shadow.png").getWidth()) * 2;//scale shadow to tear size
        //
        this.shadowImage.setImage(imageGetter("file:src\\resources\\gfx\\shadow.png", 0, 0, (int) (120), (int) (49), a, a, 1));
        this.shadowImage.setViewOrder(-8);
        //
        group.getChildren().addAll(this.tearImage, this.tearHitbox.getShape(), this.shadowImage);
        this.tearImage.setViewOrder(-8);
        this.tearImage.relocate(this.position.x - this.tearImage.getBoundsInParent().getWidth() / 2, this.position.y - this.tearImage.getBoundsInParent().getHeight() / 2);
        //
        timeline(tears, enemies, boundaries, group);
        explodeTimelineSetup();
    }

    private void explodeTimelineSetup() {
        explodeTimeline = new Timeline(new KeyFrame(Duration.millis(70), event -> {
            this.tearImage.setImage(Effects.BLUEtearCollideAnimation[explodeCounter]);
            explodeCounter++;
        }));
        explodeTimeline.setCycleCount(Effects.BLUEtearCollideAnimation.length - 1);
    }

    private void timeline(ArrayList<Tear> tears, ArrayList<Enemy> enemies, ArrayList<Rectangle> boundaries, Group group) {
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
            shadowCheck(group, tears);//when the hitbox hits the center of the shadow it will hit the floor
            //
            enemyCheck(enemies, group, tears);
        }));
        tearTimeline.setCycleCount(Timeline.INDEFINITE);
        tearTimeline.play();
    }

    private void shadowCheck(Group group, ArrayList<Tear> tears) {
        if (this.tearHitbox.getShape().getBoundsInParent().intersects(this.shadowPosition.x + this.shadowImage.getBoundsInParent().getWidth() / 2, this.shadowPosition.y + this.shadowImage.getBoundsInParent().getHeight() / 2, 1, 1)) {
            hitSomething(group, tears);
        }
    }

    private void enemyCheck(ArrayList<Enemy> enemies, Group group, ArrayList<Tear> tears) {//check for enemies
        //TODO add enemy collisions after enemy rework
        //for (int i = 0; i <enemies.size() ; i++) {

        //}
    }

    public void boundaryCheck(ArrayList<Rectangle> boundaries, Group group, ArrayList<Tear> tears) {//check for boundaries
        for (Rectangle boundary : boundaries) {
            if (boundary.getBoundsInParent().intersects(this.tearHitbox.getShape().getBoundsInParent())) {
                hitSomething(group, tears);
                break;
            }
        }
    }

    private void hitSomething(Group group, ArrayList<Tear> tears) {
        this.tearTimeline.stop();
        group.getChildren().removeAll(this.tearHitbox.getShape(), this.shadowImage);
        this.tearImage.setImage(Effects.BLUEtearCollideAnimation[0]);
        this.position.sub(this.tearImage.getBoundsInParent().getWidth() / 2, this.tearImage.getBoundsInParent().getHeight() / 2);
        this.tearImage.relocate(this.position.x, this.position.y);

        this.explodeTimeline.play();
        this.explodeTimeline.setOnFinished(Event -> {
            destroy(group, tears);
        });
    }

    public void destroy(Group group, ArrayList<Tear> tears) {
        group.getChildren().remove(this.tearImage);
        try {
            group.getChildren().remove(this.tearHitbox.getShape());
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