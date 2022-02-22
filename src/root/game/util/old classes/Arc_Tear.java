package root.game.Tear;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import root.game.dungeon.room.boss.Boss;
import root.game.dungeon.room.enemy.Enemy;
import root.game.util.Hitbox;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public class Arc_Tear extends Tear {

    Group parentGroup;

    float time = 60;
    float travelTime = 2.5f;
    float tearHeight = 400;
    float g = 0.06f;
    //
    float hitY;
    Vecc2f mouthOffset = new Vecc2f(10, -10);


    public Arc_Tear(int damage, Vecc2f startPos, Vecc2f endPos, float scaleX, float scaleY, ArrayList<Tear> tears, ArrayList<Enemy> enemies, ArrayList<Boss> bosses, Target tearTarget, Group parentGroup) {
        this.avgScale = ((scaleX + scaleY) / 2);
        this.parentGroup = parentGroup;
        float scale = 2.5f;

        this.mouthOffset.set(this.mouthOffset.x * scaleX, this.mouthOffset.y * scaleY);
        this.position = new Vecc2f(startPos);
        this.position.add(mouthOffset);
        this.tearImage.setImage(imageGetter("file:src\\resources\\gfx" +
                        "\\tears.png", 128, 64,
                32, 32, scaleX, scaleY, scale));
        this.target = tearTarget;
        this.hitY = endPos.y;


        double deltaX = (endPos.x - (startPos.x + this.tearImage.getBoundsInParent().getWidth() + this.mouthOffset.x));
        double deltaY = (endPos.y - (startPos.y + this.tearImage.getBoundsInParent().getHeight() + this.mouthOffset.y));

        float b = (float) Math.atan(deltaY / deltaX);

        System.out.println(b);

        this.velocity = new Vecc2f((((endPos.x - (startPos.x + this.tearImage.getBoundsInParent().getWidth() + this.mouthOffset.x))
                / (time * travelTime))), -((g * time * travelTime) / 2));


        this.damage = damage;
        this.tearHitbox = new Hitbox("Circle", damage + 1, damage + 1, scale, scaleX, scaleY, damage, damage);
        this.tearHitbox.getShape().setVisible(false);


        //this.position.sub(this.tearImage.getBoundsInParent().getWidth() / 2,this.tearImage.getBoundsInParent().getHeight() / 2);

        this.tearImage.setViewOrder(ViewOrder.player_attacks_layer.getViewOrder());
        this.tearImage.relocate(this.position.x, this.position.y);

        parentGroup.getChildren().addAll(this.tearImage);

        explodeTimelineSetup();
        timeline(tears, enemies, bosses, parentGroup);
    }

    private void timeline(ArrayList<Tear> tears, ArrayList<Enemy> enemies, ArrayList<Boss> bosses, Group parentGroup) {
        tearTimeline = new Timeline(new KeyFrame(Duration.seconds((float) (1 / time)), event -> {
            this.position.add(this.velocity);
            this.velocity.add(0, g);


            this.tearImage.relocate(this.position.x, this.position.y);
            this.tearHitbox.getShape().relocate(this.position.x - this.tearHitbox.getxDelta(), this.position.y - this.tearHitbox.getyDelta());

            if (this.velocity.y > 0 && ((this.position.y + this.tearImage.getBoundsInParent().getHeight() / 2) > hitY)) {
                this.position.add(this.tearImage.getBoundsInParent().getWidth() / 2, this.tearImage.getBoundsInParent().getHeight() / 2);
                hitSomething(parentGroup, tears);
            }

            //

        }));
        tearTimeline.setCycleCount(Timeline.INDEFINITE);
        tearTimeline.play();
    }
}
