package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import root.game.Tear.Tear;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

public class Enemy_Fly extends Enemy {
    Vecc2f pushBack = new Vecc2f();

    public Enemy_Fly(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super(enemyTemplate, pos, scaleX, scaleY, screenBounds, shading, parentRoom);

        setVeloLimit(2.5f);//

        timelineSetup();
    }

    @Override
    public void enemySpecificMovement() {//will run every frame
        //movement
        pushBack.random2D(20 + rand.nextInt(10));
        this.acceleration.mult((float) 0.8);
        this.acceleration.add(pushBack);
        this.velocity.add(this.acceleration);
        this.position.add(this.velocity);
        this.velocity.mult(0.95);
        //
        linearImageSwapper(this.idleAnimation, this.IDLEimageSwapInterval);
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
        try {
            group.getChildren().remove(this.hitbox.getShape());
        } catch (Exception e) {
        }
        //
        this.timeline.pause();
    }

    @Override
    public boolean collidesWith(Tear tear) {
        return tear.tearHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent());
    }
}