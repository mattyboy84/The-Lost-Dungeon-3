package root.game.Tear;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import root.game.dungeon.room.Active_Bomb;
import root.game.dungeon.room.boss.Boss;
import root.game.dungeon.room.enemy.Enemy;
import root.game.util.Effects;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public class Tear_Player extends Tear {

    ArrayList<Tear> tears;
    Group group;
    ArrayList<Enemy> enemies;
    ArrayList<Boss> bosses;
    ArrayList<Active_Bomb> bombs;

    public Tear_Player(String lookingDirection, int damage, int tearSize, Group group, Vecc2f pos, Vecc2f velocity, float scaleX, float scaleY, float veloLimit, ArrayList<Tear> tears, ArrayList<Enemy> enemies, ArrayList<Boss> bosses, ArrayList<Rectangle> allBoundaries, ArrayList<Active_Bomb> bombs) {
        super(scaleX, scaleY, pos, damage, velocity, 2, tearSize);
        //
        switch (lookingDirection) {
            case "north":
                this.velocity.add(0, (float) (-veloLimit * 1.5));
                break;
            case "south":
                this.velocity.add(0, (float) (veloLimit * 1.5));
                break;
            case "west":
                this.velocity.add((float) (-veloLimit * 1.5), 0);
                break;
            case "east":
                this.velocity.add((float) (veloLimit * 1.5), 0);
                break;
        }
        float scale = 2.5f;
        //
        hitboxSetup(scale);

        this.tearImage.setImage(imageGetter("file:src\\resources\\gfx\\tears.png", (32 * ((tearSize > 7) ? (tearSize - 7) : (tearSize))), (32 * ((tearSize > 7) ? (1) : (0))), 32, 32, scaleX, scaleY, scale));
        //
        shadowSetup();
        //
        this.tearImage.setViewOrder(ViewOrder.player_attacks_layer.getViewOrder());
        this.tearImage.relocate(this.position.x - this.tearImage.getBoundsInParent().getWidth() / 2, this.position.y - this.tearImage.getBoundsInParent().getHeight() / 2);
        //
        group.getChildren().addAll(this.tearImage, this.tearHitbox.getShape(), this.shadowImage);
        //
        this.tears = tears;
        this.group = group;
        this.bosses = bosses;
        this.enemies = enemies;
        this.bombs=bombs;

        //
        opponentCheck();
        timeline(tears, allBoundaries, group);
        explodeTimelineSetup();
    }

    public void opponentCheck() {
        enemyCheck(enemies, group, tears);
        bossCheck(bosses, group, tears);
        bombChecker(bombs,group,tears);//bombs can be 'pushed' by the players tear
    }

    @Override
    public void tearColourEffect(int explodeCounter) {
        this.tearImage.setImage(Effects.BLUEtearCollideAnimation[explodeCounter]);
    }
}