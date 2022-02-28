package root.game.Tear;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import root.game.player.Player;
import root.game.util.Effects;
import root.game.util.Hitbox;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public class Tear_Enemy extends Tear {

    public Tear_Enemy(int damage, int tearSize, Group group, Vecc2f position, Vecc2f velocity, float scaleX, float scaleY, float veloLimit, ArrayList<Tear> tears, ArrayList<Rectangle> allBoundaries, Player playerTarget) {
        super(scaleX, scaleY, position, damage, velocity,1, tearSize);
        float scale = 2.5f;
        //
        this.tearHitbox = new Hitbox("Circle", tearSize + 1, tearSize + 1, scale, scaleX, scaleY, tearSize, tearSize);
        this.tearHitbox.getShape().setVisible(false);
        float a = (float) (((this.tearHitbox.radius)) / new Image("file:src\\resources\\gfx\\shadow.png").getWidth()) * 2;//scale shadow to tear size

        this.tearImage.setImage(imageGetter("file:src\\resources\\gfx\\tears.png", (32 * ((tearSize > 7) ? (tearSize - 7) : (tearSize))), 64+(32 * ((tearSize > 7) ? (1) : (0))), 32, 32, scaleX, scaleY, scale));
        //
        this.shadowImage.setImage(imageGetter("file:src\\resources\\gfx\\shadow.png", 0, 0, (int) (120), (int) (49), a, a, 1));
        this.shadowImage.setViewOrder(ViewOrder.enemy_boss_attacks_layer.getViewOrder());
        //
        this.tearImage.setViewOrder(ViewOrder.enemy_boss_attacks_layer.getViewOrder());
        this.shadowImage.setViewOrder(ViewOrder.background_layer.getViewOrder());
        this.tearImage.relocate(this.position.x - this.tearImage.getBoundsInParent().getWidth() / 2, this.position.y - this.tearImage.getBoundsInParent().getHeight() / 2);
        //
        group.getChildren().addAll(this.tearImage, this.tearHitbox.getShape(), this.shadowImage);

        timeline(tears, playerTarget, allBoundaries, group);
        explodeTimelineSetup();
    }

    private void timeline(ArrayList<Tear> tears, Player player, ArrayList<Rectangle> boundaries, Group group) {
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
            playerCheck(player,group,tears);

        }));
        tearTimeline.setCycleCount(Timeline.INDEFINITE);
        tearTimeline.play();
    }
    @Override
    public void tearColourEffect(int explodeCounter) {
        this.tearImage.setImage(Effects.REDtearCollideAnimation[explodeCounter]);
    }
}