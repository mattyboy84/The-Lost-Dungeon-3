package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import root.Main;
import root.game.Tear.Tear;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

public class Enemy_Spider extends Enemy {

    int viewDistance;
    int movedDistance;
    int randomMove=50;
    Vecc2f distanceMoved=new Vecc2f(0,0);

    public Enemy_Spider(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super(enemyTemplate, pos, scaleX, scaleY, screenBounds, shading, parentRoom);

        setVeloLimit(4f);
        this.viewDistance = (int) (300 * ((scaleX + scaleY) / 2));
        this.movedDistance = (int) (250 * ((scaleX + scaleY) / 2));

        timelineSetup();
    }

    @Override
    public void enemySpecificMovement() {
        switch (state) {
            case idle -> {
                if (stateTransitionTimer % randomMove == 0) {
                    this.velocity = new Vecc2f(0, 0).random2D(1);
                    this.velocity.mult(veloLimit);
                    state = states.attack1;
                }
                //
                if (stateTransitionTimer > 105 && (Vecc2f.distance(this.position.x, this.position.y, Main.player.getPosition().x, Main.player.getPosition().y) < viewDistance)) {
                    stateTransitionTimer = 0;
                    this.velocity = new Vecc2f(Main.player.getCenterPos()).sub(this.position).limit(1);
                    this.velocity.mult(veloLimit);
                    state = states.attack2;
                }
            }
            //
            case attack1 -> {
                this.position.add(this.velocity);
                this.distanceMoved.add(this.velocity);
                relocate();
                linearImageSwapper(attack1Animation, ATTACK1imageSwapInterval);
                //
                if (distanceMoved.magnitude() >= (int)(this.movedDistance/4)) {
                    transitionToIdle();
                }
            }
            //
            case attack2 -> {
                this.position.add(this.velocity);
                this.distanceMoved.add(this.velocity);
                relocate();
                linearImageSwapper(attack1Animation, ATTACK1imageSwapInterval);
                //
                if (this.distanceMoved.magnitude() > this.movedDistance) {
                   transitionToIdle();
                }

            }

        }
    }

    private void transitionToIdle() {
        this.distanceMoved.set(0,0);
        this.velocity.set(0, 0);
        state = states.idle;
        this.enemy.setImage(idleAnimation[0]);
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

    @Override
    public void checkBoundaries() {
        for (Rectangle boundary : this.parentRoom.getAllBoundaries()) {
            if (boundary.getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent())) {
                this.position.sub(this.velocity);
                this.position.set((int) this.position.x, (int) this.position.y);
                this.velocity.set(0, 0);
                this.enemy.relocate(this.position.x, this.position.y);
                this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
                this.state=states.idle;//resets to idle state - maybe add to base checker?
            }
        }
    }

}
