package root.game.dungeon.room.boss;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import root.game.Tear.Tear;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Hitbox;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public class Boss_Fistula extends Boss {

    Hitbox hitbox;
    ImageView boss;
    Vecc2f position;
    Vecc2f velocity;
    Vecc2f centerPos=new Vecc2f(0,0);
    float veloLimit;

    public Boss_Fistula(JsonObject bossTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super(bossTemplate,pos,scaleX,scaleY,screenBounds,shading,parentRoom);
        healthBarSetup();
        //
        this.veloLimit= bossTemplate.get("velocity").getAsFloat();
        this.position = new Vecc2f(this.startingTemplatePosition);
        this.velocity = new Vecc2f().random2D(1).setMag(veloLimit);
        this.velocity.mult((this.scaleX + this.scaleY / 2));
        this.maxHealth = bossTemplate.get("Health").getAsInt();
        this.health = maxHealth;
        this.sheetScale = bossTemplate.get("SheetScale").getAsInt();
        this.gutNumber = bossTemplate.get("GutNumber").getAsInt();
        this.bossType = bossTemplate.get("type").getAsString();
        this.filepath = bossTemplate.get("filePath").getAsString();
        String file = "file:src\\resources\\gfx\\bosses\\" + this.bossType + "\\" + this.filepath + ".png";
        //
        int startX = bossTemplate.get("image").getAsJsonObject().get("startX").getAsInt();
        int startY = bossTemplate.get("image").getAsJsonObject().get("startY").getAsInt();
        int width = bossTemplate.get("image").getAsJsonObject().get("width").getAsInt();
        int height = bossTemplate.get("image").getAsJsonObject().get("height").getAsInt();
        this.boss = new ImageView(imageGetter(file, startX, startY, width, height, scaleX, scaleY, sheetScale));
        //
        this.position.sub(this.boss.getBoundsInParent().getWidth() / 2, this.boss.getBoundsInParent().getHeight() / 2);
        //
        this.hitbox = new Hitbox(bossTemplate.get("Hitbox").getAsJsonObject(), (int) sheetScale, scaleX, scaleY);
        //
        timelineSetup();
    }

    @Override
    protected void bossSpecificMovement() {
        this.position.add(this.velocity);
        this.boss.relocate(this.position.x, this.position.y);
        this.hitbox.relocate(this.position);

        this.velocity.setMag(this.veloLimit);
    }

    @Override
    protected void updateCenterPos() {
        this.centerPos.set(this.hitbox.getCenterX(),this.hitbox.getCenterY());
    }

    @Override
    public void checkBoundaries() {
        ArrayList<Rectangle> UP_DOWN = parentRoom.background.get_TOP_BOTTOM_boundaries();
        ArrayList<Rectangle> LEFT_RIGHT = parentRoom.background.get_LEFT_RIGHT_boundaries();
        //
        for (Rectangle rectangle : UP_DOWN) {
            if (this.hitbox.getShape().getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                this.velocity.y *= -1;
                break;
            }
        }
        //
        for (Rectangle rectangle : LEFT_RIGHT) {
            if (this.hitbox.getShape().getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                this.velocity.x *= -1;
                break;
            }
        }
    }

    @Override
    protected void checkForPlayer() {
        if ((hitbox.getShape().getBoundsInParent().intersects(playerTarget.getBodyHitbox().getShape().getBoundsInParent()) ||
                hitbox.getShape().getBoundsInParent().intersects(playerTarget.getHeadHitbox().getShape().getBoundsInParent())) && playerTarget.isVulnerable()) {
            //
            Vecc2f dir = new Vecc2f(this.centerPos).sub(playerTarget.getCenterPos());

            Vecc2f originalVELO=new Vecc2f(velocity.x,velocity.y);

            Vecc2f enemyPushback = new Vecc2f(velocity.x, velocity.y);
            enemyPushback.mult(-1);
            enemyPushback.setMag((velocity.magnitude() < veloLimit * 0.25) ? (veloLimit) : (velocity.magnitude()));//if enemy is 'slow' the push back is adjusted
            enemyPushback.fromAngle(dir.toAngle());
            applyForce(enemyPushback, 0.3f);
            //
            playerTarget.inflictDamage(1);//TODO REMEMBER current default enemy damage is 1
            Vecc2f pushback = new Vecc2f(originalVELO.x,originalVELO.y);
            pushback.setMag((originalVELO.magnitude() < veloLimit * 0.25) ? (veloLimit) : (originalVELO.magnitude()));//if enemy is 'slow' the push back is adjusted
            pushback.fromAngle(dir.toAngle()-180);

            playerTarget.applyForce(pushback,6);
        }
    }

    @Override
    protected void velocityLimit() {
        this.velocity.limit((this.velocity.magnitude() > veloLimit * 1.5) ? (this.velocity.magnitude() * 0.8f) : (veloLimit * 1.0));
    }

    @Override
    public void applyForce(Vecc2f dir, float magnitude) {
        dir.mult(magnitude);
        this.velocity.add(dir);
    }

    @Override
    public void inflictDamage(int damage, Group group, ArrayList<Boss> bosses) {

    }

    @Override
    protected void postLoader(Group group) {
        group.getChildren().addAll(this.boss, this.hitbox.getShape());
        this.boss.relocate(this.position.x, this.position.y);
        this.boss.setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        //
        this.hitbox.relocate(this.position);
        this.hitbox.getShape().setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        this.hitbox.getShape().setVisible(false);
        //
        this.mainline.play();
    }

    @Override
    protected void postUnLoader(Group group) {
        group.getChildren().removeAll(this.boss, this.hitbox.getShape());
        this.mainline.pause();
    }

    @Override
    public boolean collidesWith(Tear tear) {
        return tear.tearHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent());
    }
}