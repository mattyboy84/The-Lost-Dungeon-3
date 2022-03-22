package root.game.dungeon.room.boss;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import root.game.Tear.Tear;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Hitbox;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public class Boss_DukeOfFlies extends Boss {

    int ATTACK1imageSwapInterval;
    Image[] attack1Animation;
    //

    Image[] backOfBossImages;
    JsonObject[] spawnEnemies;
    ImageView backOfBoss;
    //
    Vecc2f pushBack = new Vecc2f(0, 0);
    Vecc2f acceleration = new Vecc2f(0, 0);
    //
    ArrayList<Rectangle> UP_DOWN = parentRoom.getTopBottomBounds();
    ArrayList<Rectangle> LEFT_RIGHT = parentRoom.getLeftRightBounds();
    boolean cooldown = false;

    enum states {
        idle,
        attack1
    }

    states state;

    public Boss_DukeOfFlies(JsonObject bossTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room room) {
        super(bossTemplate, pos, scaleX, scaleY, screenBounds, shading, room);
        //
        this.sheetScale = bossTemplate.get("SheetScale").getAsInt();
        String file = "file:src\\resources\\gfx\\bosses\\" + this.bossType + "\\" + this.filepath + ".png";
        healthBarSetup();
        this.maxHealth = bossTemplate.get("Health").getAsInt();
        this.health = maxHealth;
        //
        setVeloLimit(bossTemplate.get("velocity").getAsFloat()*((scaleY+scaleX)/2));
        //
        this.position = new Vecc2f(this.startingTemplatePosition);//start pos is scaled in super()
        this.hitbox = new Hitbox(bossTemplate.get("Hitbox").getAsJsonObject(), (int) sheetScale, scaleX, scaleY);
        //attack 1 animation - boss' face images
        this.attack1Animation = prepareImages(bossTemplate, "attack1Animation", file);
        ATTACK1imageSwapInterval = bossTemplate.get("attack1Animation").getAsJsonObject().get("SwapInterval").getAsInt();
        this.boss = new ImageView(attack1Animation[3]);
        //
        this.position.sub(this.boss.getBoundsInParent().getWidth() / 2, this.boss.getBoundsInParent().getHeight() / 2);//center the boss image
        //back of the boss' mouth images
        this.backOfBossImages = prepareImages(bossTemplate, "backImages", file);
        this.backOfBoss = new ImageView(backOfBossImages[3]);
        //gets the templates of enemies that the boss will spawn during attacks
        JsonArray ARRspawnEnemies = bossTemplate.get("spawnEnemies").getAsJsonArray();
        this.spawnEnemies = new JsonObject[ARRspawnEnemies.size()];
        for (int i = 0; i < ARRspawnEnemies.size(); i++) {
            spawnEnemies[i] = ARRspawnEnemies.get(i).getAsJsonObject();
        }
        this.state = states.idle;
        timelineSetup();
    }

    @Override
    protected void bossSpecificMovement() {
        //
        pushBack.random2D(20 + random.nextInt(1));
        this.acceleration.mult((float) 0.8);
        this.acceleration.add(pushBack);
        this.velocity.add(this.acceleration);
        this.position.add(this.velocity);
        this.velocity.setMag(this.veloLimit);
        this.velocity.mult(0.6);
        //
        //
        switch (state) {
            case idle -> {
                if (!cooldown && stateTransitionTimer > 30) {
                    this.boss.setImage(attack1Animation[3]);
                    this.backOfBoss.setImage(backOfBossImages[3]);
                    cooldown = true;
                }
                if (stateTransitionTimer > (200 + random.nextInt(30))) {
                    transitionToAttack();
                }
            }
            case attack1 -> {
                linearImageSwapper(this.boss, this.attack1Animation, this.ATTACK1imageSwapInterval, 0);
                if (linearImageSwapper(this.backOfBoss, this.backOfBossImages, this.ATTACK1imageSwapInterval, 1) == 3) {

                    for (int i = 0; i < 1; i++) {
                        parentRoom.newRealTimeEnemySub("fly", spawnEnemies[0], this.centerPos.add(new Vecc2f(0, i)), parentGroup, 0);
                    }
                    //
                    for (int i = 0; i < 2; i++) {
                        if (random.nextFloat() > 0.4) {
                            parentRoom.newRealTimeEnemySub("attack fly", spawnEnemies[1], this.centerPos.add(new Vecc2f(1, i)), parentGroup, 0);
                        }
                    }

                    transitionToIdle();
                }
            }
        }
        relocate();
    }

    private void transitionToIdle() {
        //this.boss.setImage(att);
        stateTransitionTimer = 0;
        this.state = states.idle;
    }

    private void transitionToAttack() {
        stateTransitionTimer = 0;
        this.state = states.attack1;
        cooldown = false;
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
                this.velocity.mult(3);
                relocate();
                //relocate();
                break;
            }
        }
        //
        for (Rectangle rectangle : LEFT_RIGHT) {
            if (this.hitbox.getShape().getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                this.velocity.x *= -1;
                this.velocity.mult(3);
                relocate();
                //relocate();
                break;
            }
        }
    }

    private Image[] prepareImages(JsonObject bossTemplate, String imagesToGet, String file) {
        JsonObject backOfEnemyImagesList = bossTemplate.get(imagesToGet).getAsJsonObject();
        Image[] arrayOfImages = new Image[backOfEnemyImagesList.get("Images").getAsJsonArray().size()];
        int width = backOfEnemyImagesList.get("Width").getAsInt();
        int height = backOfEnemyImagesList.get("Height").getAsInt();
        for (int i = 0; i < arrayOfImages.length; i++) {
            int x = backOfEnemyImagesList.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("x").getAsInt();
            int y = backOfEnemyImagesList.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("y").getAsInt();
            arrayOfImages[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, this.sheetScale);
        }
        return arrayOfImages;
    }

    private void relocate() {
        this.boss.relocate(this.position.x, this.position.y);
        this.backOfBoss.relocate(this.position.x, this.position.y);
        this.hitbox.relocate(this.position);
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
    protected void inflictDamageSub(int damage, Group group, ArrayList<Boss> bosses) {
        if (this.health <= 0) {
            beginRemoval(group, bosses);
        }
    }

    @Override
    public boolean collidesWith(Tear tear) {
        return tear.tearHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent());
    }

    @Override
    protected void postLoader(Group group) {
        group.getChildren().addAll(this.hitbox.getShape(), this.backOfBoss, this.boss);
        this.backOfBoss.setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        this.boss.setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        this.hitbox.getShape().setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        //
        this.hitbox.getShape().setVisible(false);
        //this.hitbox.getShape().setOpacity(0.4);
        //this.hitbox.getShape().toFront();
        //
        relocate();
        this.mainline.play();
    }

    @Override
    protected void postUnLoader(Group group) {
        group.getChildren().removeAll(this.hitbox.getShape(), this.backOfBoss, this.boss);
        this.mainline.pause();
    }

}
