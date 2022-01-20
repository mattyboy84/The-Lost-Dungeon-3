package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Entity_Shader;
import root.game.util.Hitbox;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

import java.util.ArrayList;
import java.util.Random;

public abstract class Enemy implements Sprite_Splitter, Entity_Shader {

    public boolean markedDelete;

    int deathImagePointer = 1;
    Random rand=new Random();

    Vecc2f startPosition;
    Vecc2f position;
    public Vecc2f centerPos = new Vecc2f();
    Vecc2f velocity = new Vecc2f();
    Vecc2f acceleration = new Vecc2f();
    float veloLimit;
    float avgScale;
    float[][] shader;
    String name;
    String type;
    String filePath;
    int health, maxHealth;
    int sheetScale;
    Hitbox hitbox;
    ImageView enemy;
    Image[] images;
    int lightRadius;
    Image[] deathImages;
    Room parentRoom;
    Shading roomShading;

    Timeline timeline = new Timeline();
    Timeline deathTimeline;
    //
    int imageSwapInterval = 0;
    int imageSwapIntervalCounter = 0;
    int imageCounter = 0;
    //

    public Enemy(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        this.avgScale = ((scaleX + scaleY) / 2);
        this.startPosition=new Vecc2f(pos.x * scaleX, pos.y * scaleY);
        this.position = new Vecc2f(this.startPosition.x,this.startPosition.y);
        this.parentRoom = parentRoom;
        this.roomShading = shading;

        this.name = enemyTemplate.get("enemy").getAsString();
        this.type = enemyTemplate.get("type").getAsString();
        this.filePath = enemyTemplate.get("filePath").getAsString();

        this.maxHealth = enemyTemplate.get("Health").getAsInt();
        this.health = maxHealth;
        this.sheetScale = enemyTemplate.get("SheetScale").getAsInt();

        this.hitbox = new Hitbox(enemyTemplate.get("Hitbox").getAsJsonObject(), sheetScale, scaleX, scaleY);

        String file = "file:src\\resources\\gfx\\monsters\\" + type + "\\" + filePath + ".png";
        images = new Image[enemyTemplate.get("EnemyImages").getAsJsonObject().get("Images").getAsJsonArray().size()];
        JsonObject object = enemyTemplate.get("EnemyImages").getAsJsonObject();
        int width = object.get("Width").getAsInt();
        int height = object.get("Width").getAsInt();
        for (int i = 0; i < images.length; i++) {
            int x = object.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("x").getAsInt();
            int y = object.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("y").getAsInt();
            images[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
        }
        //
        enemy = new ImageView(images[0]);
        //
        if (enemyTemplate.get("Light").getAsBoolean()) {
            lightRadius = enemyTemplate.get("Radius").getAsInt();
            shader = setupShader(lightRadius);
        }
        //
        if (enemyTemplate.get("hasDeathImages").getAsBoolean()) {
            JsonObject object1 = enemyTemplate.get("DeathImages").getAsJsonObject();
            deathImages = new Image[enemyTemplate.get("DeathImages").getAsJsonObject().get("Images").getAsJsonArray().size()];
            int Dwidth = object1.get("DeathWidth").getAsInt();
            int Dheight = object1.get("DeathHeight").getAsInt();
            for (int i = 0; i < deathImages.length; i++) {
                int x = object1.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("x").getAsInt();
                int y = object1.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("y").getAsInt();
                deathImages[i] = imageGetter("file:" + object1.get("DeathFilePath").getAsString(), x, y, Dwidth, Dheight, scaleX, scaleY, sheetScale);
            }
            deathTimelineSetup();
        }
    }

    public void deathTimelineSetup() {
        deathTimeline = new Timeline(new KeyFrame(Duration.millis(80), event -> {
            //
            this.enemy.setImage(deathImages[deathImagePointer]);
            deathImagePointer = (deathImagePointer >= deathImages.length - 1) ? (0) : ++deathImagePointer;
            //
        }));
        deathTimeline.setCycleCount(deathImages.length - 1);
    }

    public void checkBoundaries() {
        //System.out.println(boundaries.size());
        for (Rectangle boundary : this.parentRoom.getAllBoundaries()) {
            //System.out.println(boundary.getBoundsInParent().getMinX());
            if (boundary.getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent())) {
                this.position.sub(this.velocity);
                this.position.set((int) this.position.x, (int) this.position.y);
                this.velocity.set(0, 0);
                this.enemy.relocate(this.position.x, this.position.y);
                this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
            }
        }
    }

    public void timelineSetup() {
        timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            //every enemy will have the base of shader checking,boundary checking & updating of center pos
            removeShader();
            updateCenterPos();
            //
            this.velocity.limit((this.velocity.magnitude() > veloLimit * 1.5) ? (this.velocity.magnitude() * 0.8f) : (veloLimit));
            //
            enemySpecificMovement();//will be overridden for each enemy
            //
            checkBoundaries();
            //
            addShader();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public abstract void enemySpecificMovement();

    public void inflictDamage(int damage, Group group, ArrayList<Enemy> enemies) {
        this.health -= damage;
        this.health = Math.max(0, this.health);
        //
        if (this.health == 0) {
            beginDeath(group, enemies);
        }
    }

    public void beginDeath(Group group, ArrayList<Enemy> enemies) {
        if (deathImages.length > 0) {
            //starts death animation
            this.timeline.pause();
            int x = (int) (deathImages[0].getWidth() - this.enemy.getBoundsInParent().getWidth());
            int y = (int) (deathImages[0].getHeight() - this.enemy.getBoundsInParent().getWidth());
            this.enemy.setImage(deathImages[0]);
            group.getChildren().remove(this.hitbox.getShape());
            this.position.sub(x / 2, y / 2);
            this.enemy.relocate(this.position.x, this.position.y);
            this.deathTimeline.play();
            removeShader();
            this.deathTimeline.setOnFinished(event -> {
                unload(group);
                enemies.remove(this);
            });
        } else {
            unload(group);
            removeShader();
            this.markedDelete = true;
        }
    }

    public void linearImageSwapper(Image[] images) {
        if (++imageSwapIntervalCounter >= imageSwapInterval) {
            linearImageSwapperSub(images);
            imageSwapIntervalCounter = 0;
        }
    }

    public void linearImageSwapperSub(Image[] images) {
        this.enemy.setImage(images[imageCounter]);
        imageCounter++;
        if (imageCounter > images.length - 1) {
            imageCounter = 0;
        }
    }

    public void applyForce(Vecc2f dir, int magnitude) {
        dir.mult(magnitude);
        this.velocity.add(dir);
    }

    public void addShader() {
        if (this.lightRadius > 0) {
            roomShading.addActiveSource((float) (this.hitbox.getCenterX()), (float) (this.hitbox.getCenterY()), shader, this.hashCode());
        }
    }

    public void removeShader() {
        if (this.lightRadius > 0) {
            this.roomShading.removeActiveSource(hashCode());
        }
    }

    public void setVeloLimit(float i) {
        this.veloLimit = i * avgScale;
    }

    public void updateCenterPos() {
        this.centerPos.set(this.hitbox.getCenterX(), this.hitbox.getCenterY());
    }

    public void load(Group group) {

    }

    public void unload(Group group) {

    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public void setHitbox(Hitbox hitbox) {
        this.hitbox = hitbox;
    }

}