package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

public class Enemy_Pooter extends Enemy {
    public Enemy_Pooter(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super(enemyTemplate, pos, scaleX, scaleY, screenBounds, shading, parentRoom);

        setVeloLimit(3f);

        timelineSetup();
    }

    @Override
    public void enemySpecificMovement() {//will run every frame
        //movement

        switch (state) {
            case idle:

                break;
            case attack1:

                break;
        }
        //linearImageSwapper(attack1Animation, ATTACK1imageSwapInterval); - works
        //
        relocate();
    }

    @Override
    public void load(Group group) {
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
    public void unload(Group group) {
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
}