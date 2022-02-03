package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import root.Main;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.player.Player;
import root.game.player.Tear;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

public class Enemy_Pooter extends Enemy {
    Vecc2f pushBack = new Vecc2f();
    float viewDistance = 500f;

    public Enemy_Pooter(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super(enemyTemplate, pos, scaleX, scaleY, screenBounds, shading, parentRoom);

        setVeloLimit(2f);

        timelineSetup();
    }

    @Override
    public void enemySpecificMovement() {//will run every frame
        //movement
        switch (state) {
            case idle:
                Vecc2f dir = new Vecc2f(Player.centerPos);
                dir.sub(this.centerPos);
                dir.limit(0.5);
                velocity.add(dir);
                this.position.add(this.velocity);
                //
                linearImageSwapper(idleAnimation, IDLEimageSwapInterval);
                //
                if (stateTransitionTimer > 90 + rand.nextInt(30)) {
                    if ((Vecc2f.distance(Main.player.getCenterPos().x, Main.player.getCenterPos().y, this.centerPos.x, this.centerPos.y)) < viewDistance) {//close enough to start shooting
                        changeStateIdleToAttack1();
                    }
                }
                break;
            case attack1:
                //
                pushBack.random2D(20 + rand.nextInt(10));
                this.acceleration.mult((float) 0.8);
                this.acceleration.add(pushBack);
                this.velocity.add(this.acceleration);
                this.position.add(this.velocity);
                this.velocity.mult(0.95);
                //
                int currentFrame = (linearImageSwapper(attack1Animation, ATTACK1imageSwapInterval));
                final int finalLength = attack1Animation.length;
                switch (currentFrame) {
                    case 7://7th frame visually looks the best for a projectile to appear.
                        Vecc2f tearVELO = new Vecc2f(Main.player.getCenterPos());
                        tearVELO.sub(this.centerPos);
                        tearVELO.limit(8);
                        parentRoom.addNewTear("direction", 1, parentGroup, this.centerPos.add(0, 20 * this.scaleY), tearVELO, this.scaleX, this.scaleY, this.veloLimit, Tear.Target.player);
                        break;
                    case 0://distance check on first frame of the animation.
                        if ((Vecc2f.distance(Main.player.getCenterPos().x, Main.player.getCenterPos().y, this.centerPos.x, this.centerPos.y)) > viewDistance) {//get closer to player - idle state
                            changeStateAttackToIdle();
                        }
                        break;
                }
                break;
        }
        //
        relocate();
    }

    private void changeStateIdleToAttack1() {
        this.state = states.attack1;
    }

    private void changeStateAttackToIdle() {
        this.state = states.idle;
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
}