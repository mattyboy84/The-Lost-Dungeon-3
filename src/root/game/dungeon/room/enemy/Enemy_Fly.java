package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import root.game.dungeon.Shading;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public class Enemy_Fly extends Enemy {

    public Enemy_Fly(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, ArrayList<Rectangle> allBoundaries) {
        super(enemyTemplate, pos, scaleX, scaleY, screenBounds, shading, allBoundaries);

        setVeloLimit(1.5f);//TODO - maybe move veloLimit to enemy templates and set it up in main class

    }

    @Override
    public void enemySpecificMovement() {

    }

    //public void timelineSetup() {
    //    timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> {
    //        removeShader();
    //        //
    //        this.position.add(this.velocity);
    //        //
    //        this.enemy.relocate(this.position.x, this.position.y);
    //        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
    //        checkBoundaries();
    //        //
    //        addShader();
    //    }));
    //    timeline.setCycleCount(Timeline.INDEFINITE);
    //}

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
        group.getChildren().removeAll(this.enemy, this.hitbox.getShape());
        this.timeline.pause();
    }
}