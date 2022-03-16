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

    boolean hasChild = false;
    int childNum;

    float rotation=random.nextFloat();
    ArrayList<Rectangle> UP_DOWN = parentRoom.getTopBottomBounds();
    ArrayList<Rectangle> LEFT_RIGHT = parentRoom.getLeftRightBounds();

    public Boss_Fistula(JsonObject bossTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super(bossTemplate, pos, scaleX, scaleY, screenBounds, shading, parentRoom);
        this.sheetScale = bossTemplate.get("SheetScale").getAsInt();
        healthBarSetup();
        //
        setVeloLimit(bossTemplate.get("velocity").getAsFloat());
        //
        this.position = new Vecc2f(this.startingTemplatePosition);
        this.velocity = new Vecc2f().random2D(1).setMag(veloLimit);
        this.maxHealth = bossTemplate.get("Health").getAsInt();
        this.health = maxHealth;
        try {
            //if there isn't a child it will jump to catch and skip setting 'hasChild' to true
            JsonObject a = bossTemplate.get("child").getAsJsonObject();
            hasChild = true;
        } catch (Exception ignored) {
        }
        childNum = bossTemplate.get("childNum").getAsInt();
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
        relocate();
        //
        this.boss.setRotate(this.boss.getRotate()+this.rotation);
        this.hitbox.getShape().setRotate(this.boss.getRotate());

        this.velocity.setMag(this.veloLimit);
    }

    @Override
    protected void updateCenterPos() {
        this.centerPos.set(this.hitbox.getCenterX(), this.hitbox.getCenterY());
    }

    @Override
    public void checkBoundaries() {
        for (Rectangle rectangle : UP_DOWN) {
            if (this.hitbox.getShape().getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                this.velocity.y *= -1;
                relocate(); relocate();
                break;
            }
        }
        //
        for (Rectangle rectangle : LEFT_RIGHT) {
            if (this.hitbox.getShape().getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                this.velocity.x *= -1;
                relocate(); relocate();
                break;
            }
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
    public void inflictDamageSub(int damage, Group group, ArrayList<Boss> bosses) {
        if (this.health <= 0) {
            if (hasChild) {
                for (int i = 0; i < childNum; i++) {
                    Vecc2f b = new Vecc2f(this.centerPos);
                    b.add(0, i + 1);//the boss positions NaN when the seperationSetter runs on bosses with the same start position
                    parentRoom.newRealtimeBossSub("fistula", this.template.get("child").getAsJsonObject(), b, group);
                }
            }
            beginRemoval(group, bosses);
        }
    }

    @Override
    public boolean collidesWith(Tear tear) {
        return tear.tearHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent());
    }

    private void relocate() {
        this.boss.relocate(this.position.x, this.position.y);
        this.hitbox.relocate(this.position);
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
}