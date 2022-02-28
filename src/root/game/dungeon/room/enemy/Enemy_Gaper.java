package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import root.game.Tear.Tear;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

public class Enemy_Gaper extends Enemy {

    int yRange = 600;
    int xRange =75;
    int rotate;//0  looking down // 180  looking up

    public Enemy_Gaper(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom,int rotate) {
        super(enemyTemplate, pos, scaleX, scaleY, screenBounds, shading, parentRoom);
        //
        setVeloLimit(4f);

        this.yRange *= scaleY;
        this.xRange *= scaleX;
        this.rotate=rotate;

        this.hitbox.getShape().setVisible(true);

        timelineSetup();
    }

    @Override
    public void enemySpecificMovement() {
        switch (state) {
            case idle -> {
                if (stateTransitionTimer>40){
                    this.enemy.setImage(idleAnimation[0]);
                }
                if (stateTransitionTimer > 60) {
                    Vecc2f disToPlayer = new Vecc2f(playerTarget.getCenterPos());
                    disToPlayer.sub(this.centerPos);
                    disToPlayer.fromAngle(disToPlayer.toAngle()+rotate);
                    if ((Math.abs(disToPlayer.x) < xRange && Math.abs(disToPlayer.y) < yRange)) {
                        System.out.println("attacked");
                        state=states.attack1;
                    }
                }
            }
            case attack1 -> {
                this.enemy.setImage(attack1Animation[0]);
                Vecc2f attackDir = new Vecc2f(0,5).mult(this.avgScale);
                parentRoom.addNewEnemyTear(1,5,parentGroup,this.centerPos,attackDir.fromAngle(attackDir.toAngle()+rotate),scaleX,scaleY,this.veloLimit);
                state=states.idle;
                stateTransitionTimer=0;
            }
        }
    }

    @Override
    protected void postLoading(Group group) {
        group.getChildren().addAll(this.hitbox.getShape(), this.enemy);
        //
        this.enemy.setRotate(this.rotate);
        this.hitbox.getShape().setRotate(this.rotate);
        //
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
