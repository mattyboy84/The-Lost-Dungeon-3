package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import root.game.Tear.Tear;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.player.Player;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

public class Enemy_AttackFly extends Enemy {

    public Enemy_AttackFly(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super(enemyTemplate, pos, scaleX, scaleY, screenBounds, shading, parentRoom);

        setVeloLimit(2.5f);

        timelineSetup();
    }

    public void enemySpecificMovement() {//will run every frame
        state=states.attack1;

        Vecc2f dir = new Vecc2f(playerTarget.getCenterPos());
        dir.sub(this.centerPos);

        dir.limit(0.5);
        velocity.add(dir);
        this.position.add(this.velocity);
        //
        linearImageSwapper(this.idleAnimation,this.IDLEimageSwapInterval);
        //
        relocate();
    }

    @Override
    protected void postLoading(Group group) {
        this.activeShader=shader;
        group.getChildren().addAll(this.hitbox.getShape(), this.enemy);
        this.enemy.setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        this.hitbox.getShape().setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        this.hitbox.getShape().setVisible(false);
        this.enemy.relocate(this.position.x, this.position.y);
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
        //
        this.timeline.play();
    }

    @Override
    protected void postUnLoading(Group group) {
        this.activeShader=emptyShader;
        removeShader();
        group.getChildren().removeAll(this.enemy);
        try{
            group.getChildren().remove(this.hitbox.getShape());
        }catch (Exception e){}
        //
        this.timeline.pause();
    }

    @Override
    public boolean collidesWith(Tear tear) {
        return tear.tearHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent());
    }
}