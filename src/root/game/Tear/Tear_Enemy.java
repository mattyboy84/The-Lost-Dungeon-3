package root.game.Tear;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import root.game.dungeon.room.Active_Bomb;
import root.game.player.Player;
import root.game.util.Effects;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public class Tear_Enemy extends Tear {

    Player player;
    ArrayList<Tear> tears;
    Group group;

    public Tear_Enemy(int damage, int tearSize, Group group, Vecc2f position, Vecc2f velocity, float scaleX, float scaleY, float veloLimit, ArrayList<Tear> tears, ArrayList<Rectangle> allBoundaries, Player playerTarget) {
        super(scaleX, scaleY, position, damage, velocity, 1, tearSize);
        float scale = 2.5f;
        //
        hitboxSetup(scale);

        this.tearImage.setImage(imageGetter("file:src\\resources\\gfx\\tears.png", (32 * ((tearSize > 7) ? (tearSize - 7) : (tearSize))), 64 + (32 * ((tearSize > 7) ? (1) : (0))), 32, 32, scaleX, scaleY, scale));
        //
        shadowSetup();
        //
        this.tearImage.setViewOrder(ViewOrder.enemy_boss_attacks_layer.getViewOrder());
        this.tearImage.relocate(this.position.x - this.tearImage.getBoundsInParent().getWidth() / 2, this.position.y - this.tearImage.getBoundsInParent().getHeight() / 2);
        //
        group.getChildren().addAll(this.tearImage, this.tearHitbox.getShape(), this.shadowImage);
        //
        player=playerTarget;
        this.tears=tears;
        this.group=group;
        //
        opponentCheck();
        timeline(tears, allBoundaries, group);
        explodeTimelineSetup();
    }

    public void opponentCheck(){
        playerCheck(player,group,tears);
    }

    @Override
    public void tearColourEffect(int explodeCounter) {
        this.tearImage.setImage(Effects.REDtearCollideAnimation[explodeCounter]);
    }
}