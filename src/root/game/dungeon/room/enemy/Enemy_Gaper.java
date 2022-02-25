package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.stage.Stage;
import root.game.Tear.Tear;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

public class Enemy_Gaper extends Enemy {

    int range = 600;

    public Enemy_Gaper(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super(enemyTemplate, pos, scaleX, scaleY, screenBounds, shading, parentRoom);
        //
        setVeloLimit(4f);

        this.range *= ((scaleX + scaleY) / 2);

        timelineSetup();
    }

    @Override
    public void enemySpecificMovement() {
        switch (state) {
            case idle -> {
                if (stateTransitionTimer > 60) {
                    Vecc2f disToPlayer = new Vecc2f(playerTarget.getCenterPos());
                    disToPlayer.sub(this.centerPos);
                    if ((Math.abs(disToPlayer.x) < 100 && disToPlayer.y < range)) {
                        state=states.attack1;
                    }
                }
            }
            case attack1 -> {
                
            }
        }
    }

    @Override
    protected void postLoading(Group group) {
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
        this.activeShader = emptyShader;
        removeShader();
        group.getChildren().removeAll(this.enemy);
        try {
            group.getChildren().remove(this.hitbox.getShape());
        } catch (Exception e) {
        }
        //
        this.timeline.pause();
        removeShader();
    }

    @Override
    public boolean collidesWith(Tear tear) {
        return tear.tearHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent());
    }
}
