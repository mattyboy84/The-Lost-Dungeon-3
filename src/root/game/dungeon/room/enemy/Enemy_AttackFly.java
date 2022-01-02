package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import root.game.dungeon.Shading;
import root.game.player.Player;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public class Enemy_AttackFly extends  Enemy{

    public Enemy_AttackFly(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, ArrayList<Rectangle> allBoundaries) {
        super(enemyTemplate,pos,scaleX,scaleY,screenBounds,shading,allBoundaries);

        setVeloLimit(2.5f);

        timelineSetup();
    }

    public void enemySpecificMovement() {//will run every frame
        Vecc2f dir = new Vecc2f(Player.centerPos);
        dir.sub(this.centerPos);
        dir.limit(0.5);
        velocity.add(dir);
        velocity.limit(this.veloLimit);
        this.position.add(this.velocity);
        //
        this.enemy.relocate(this.position.x, this.position.y);
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
    }

    @Override
    public void load(Group group) {
        group.getChildren().addAll(this.hitbox.getShape(),this.enemy);
        this.enemy.setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        this.hitbox.getShape().setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        this.hitbox.getShape().setVisible(false);
        this.enemy.relocate(this.position.x,this.position.y);
        this.hitbox.getShape().relocate(this.position.x+this.hitbox.getxDelta(),this.position.y+this.hitbox.getyDelta());
        //
        this.timeline.play();
    }

    @Override
    public void unload(Group group) {
        group.getChildren().removeAll(this.enemy,this.hitbox.getShape());
        this.timeline.pause();
    }
}