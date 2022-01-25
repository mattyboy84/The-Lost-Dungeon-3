package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.player.Player;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public class Enemy_Fly extends Enemy {
    Vecc2f        pushBack = new Vecc2f();


    public Enemy_Fly(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super(enemyTemplate, pos, scaleX, scaleY, screenBounds, shading, parentRoom);

        setVeloLimit(1.5f);//TODO - maybe move veloLimit to enemy templates and set it up in main class

        imageSwapInterval=24;


        timelineSetup();

    }

    @Override
    public void enemySpecificMovement() {//will run every frame
        //movement
        pushBack.random2D(20 + rand.nextInt(10));
        pushBack.limit((float) 0.5);
        this.acceleration.mult((float) 0.8);
        this.acceleration.add(pushBack);
        this.acceleration.limit((float) 1.5);
        this.velocity.add(this.acceleration);
        this.velocity.limit(1);
        this.position.add(this.velocity);
        this.velocity.mult(0.85);
        //
        linearImageSwapper(this.images);
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
        group.getChildren().removeAll(this.enemy);
        try{
            group.getChildren().remove(this.hitbox.getShape());
        }catch (Exception e){}
        //
        this.timeline.pause();
        removeShader();
    }
}