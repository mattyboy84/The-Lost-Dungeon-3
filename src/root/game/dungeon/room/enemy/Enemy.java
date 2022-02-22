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
import root.Main;
import root.game.Tear.Tear;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.*;

import java.util.ArrayList;
import java.util.Random;

public abstract class Enemy implements Sprite_Splitter, Entity_Shader {

    int deathImagePointer = 1;
    Random rand = new Random();

    int gutNumber = 0;
    int bloodNumber = 0;

    Vecc2f startPosition;
    Vecc2f position;
    public Vecc2f centerPos = new Vecc2f();
    Vecc2f velocity = new Vecc2f(0,0);
    Vecc2f acceleration = new Vecc2f();
    float veloLimit;
    float avgScale;
    float scaleX,scaleY;
    float[][] activeShader;
    float[][] shader;
    float[][] emptyShader = new float[0][0];
    String name;
    String type;
    String filePath;
    public int health;
    int maxHealth;
    int sheetScale;
    Hitbox hitbox;
    ImageView enemy;
    Image[] idleAnimation;
    Image[] deathAnimation;
    Image[] attack1Animation;
    Image[] attack2Animation;
    //Timers
    int stateTransitionTimer;

    //
    int IDLEimageSwapInterval = 0;
    int ATTACK1imageSwapInterval = 0;
    int ATTACK2imageSwapInterval = 0;
    //
    int lightRadius;
    Room parentRoom;
    Shading roomShading;

    Timeline timeline = new Timeline();
    Timeline deathTimeline;
    //
    int imageSwapIntervalCounter = 0;
    int imageCounter = 0;
    //
    int uniqueID;
    //
    Group parentGroup;

    //
    public enum states {
        idle,
        attack1,
        attack2,

        dying
    }

    public states state;


    //

    public Enemy(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        //
        this.state = states.idle;
        //
        this.uniqueID = rand.nextInt(Integer.MAX_VALUE);
        this.scaleX=scaleX;
        this.scaleY=scaleY;
        this.avgScale = ((scaleX + scaleY) / 2);
        this.startPosition = new Vecc2f(pos.x * scaleX, pos.y * scaleY);
        this.position = new Vecc2f(this.startPosition.x, this.startPosition.y);
        this.parentRoom = parentRoom;
        this.roomShading = shading;

        this.name = enemyTemplate.get("enemy").getAsString();
        this.type = enemyTemplate.get("type").getAsString();
        this.filePath = enemyTemplate.get("filePath").getAsString();

        this.maxHealth = enemyTemplate.get("Health").getAsInt();
        this.health = maxHealth;
        this.sheetScale = enemyTemplate.get("SheetScale").getAsInt();
        //
        try {
            gutNumber = enemyTemplate.get("GutNumber").getAsInt();
        } catch (Exception e) {//catch will trigger if enemy template has no guts then value will remain at 0
        }

        this.hitbox = new Hitbox(enemyTemplate.get("Hitbox").getAsJsonObject(), sheetScale, scaleX, scaleY);
        //Template location
        String file = "file:src\\resources\\gfx\\monsters\\" + type + "\\" + filePath + ".png";
        /**
         * Animation setup
         */
        //IDLE ANIMATION
        try {//attempt to find and set-up the idle animation
            IDLEimageSwapInterval = enemyTemplate.get("idleAnimation").getAsJsonObject().get("SwapInterval").getAsInt();
            idleAnimation = new Image[enemyTemplate.get("idleAnimation").getAsJsonObject().get("Images").getAsJsonArray().size()];
            animationSetup(enemyTemplate, idleAnimation, "idleAnimation", file, scaleX, scaleY);
        } catch (Exception ignored) {
        }
        //ATTACK 1 ANIMATION
        try {//attempt to find and set-up the attack 1 animation
            ATTACK1imageSwapInterval = enemyTemplate.get("attack1Animation").getAsJsonObject().get("SwapInterval").getAsInt();
            attack1Animation = new Image[enemyTemplate.get("attack1Animation").getAsJsonObject().get("Images").getAsJsonArray().size()];
            animationSetup(enemyTemplate, attack1Animation, "attack1Animation", file, scaleX, scaleY);
        } catch (Exception ignored) {
        }
        //ATTACK 2 ANIMATION
        try {//attempt to find and set-up the attack 2 animation
            ATTACK2imageSwapInterval = enemyTemplate.get("attack2Animation").getAsJsonObject().get("SwapInterval").getAsInt();
            attack2Animation = new Image[enemyTemplate.get("attack2Animation").getAsJsonObject().get("Images").getAsJsonArray().size()];
            animationSetup(enemyTemplate, attack2Animation, "attack2Animation", file, scaleX, scaleY);
        } catch (Exception ignored) {
        }
        //
            enemy = new ImageView(idleAnimation[0]);
        //
        try{//attempts to get the light radius if there is one.
            lightRadius = enemyTemplate.get("light").getAsJsonObject().get("Radius").getAsInt();
            shader = setupShader(lightRadius);
        }catch (Exception ignored){}
        //DEATH Animation - setups one the animation if there is one.
        try {
            JsonObject deathObject = enemyTemplate.get("DeathAnimation").getAsJsonObject();
            deathAnimation = new Image[enemyTemplate.get("DeathAnimation").getAsJsonObject().get("Images").getAsJsonArray().size()];
            int Dwidth = deathObject.get("DeathWidth").getAsInt();
            int Dheight = deathObject.get("DeathHeight").getAsInt();
            for (int i = 0; i < deathAnimation.length; i++) {
                int x = deathObject.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("x").getAsInt();
                int y = deathObject.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("y").getAsInt();
                deathAnimation[i] = imageGetter("file:" + deathObject.get("DeathFilePath").getAsString(), x, y, Dwidth, Dheight, scaleX, scaleY, sheetScale);
            }
            deathTimelineSetup();
        } catch (Exception ignored) {
            //no death animation
        }
    }

    private void animationSetup(JsonObject enemyTemplate, Image[] animation, String animationName, String file, float scaleX, float scaleY) {
        JsonObject object = enemyTemplate.get(animationName).getAsJsonObject();
        int width = object.get("Width").getAsInt();
        int height = object.get("Height").getAsInt();
        for (int i = 0; i < animation.length; i++) {
            int x = object.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("x").getAsInt();
            int y = object.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("y").getAsInt();
            animation[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
        }
    }

    public void deathTimelineSetup() {
        deathTimeline = new Timeline(new KeyFrame(Duration.millis(80), event -> {
            //
            this.enemy.setImage(deathAnimation[deathImagePointer]);
            deathImagePointer = (deathImagePointer >= deathAnimation.length - 1) ? (0) : ++deathImagePointer;
            //
        }));
        deathTimeline.setCycleCount(deathAnimation.length - 1);
    }

    public void checkBoundaries() {
        //System.out.println(boundaries.size());
        for (Rectangle boundary : this.parentRoom.getAllBoundaries()) {
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
            //timers that may be used amongst enemies.
            stateTransitionTimer++;
            //
            this.velocity.limit((this.velocity.magnitude() > veloLimit * 1.5) ? (this.velocity.magnitude() * 0.8f) : (veloLimit * 1.0));
            //
            seperationSetter();
            //
            enemySpecificMovement();//will be overridden for each enemy
            //
            checkBoundaries();
            checkForPlayer();
            //
            addShader();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void checkForPlayer() {
        if ((hitbox.getShape().getBoundsInParent().intersects(Main.player.getBodyHitbox().getShape().getBoundsInParent()) ||
                hitbox.getShape().getBoundsInParent().intersects(Main.player.getHeadHitbox().getShape().getBoundsInParent())) && Main.player.isVulnerable()) {
            //
            Vecc2f originalVELO=new Vecc2f(velocity.x,velocity.y);

            Vecc2f enemyPushback = new Vecc2f(velocity.x, velocity.y);
            enemyPushback.mult(-1);
            enemyPushback.setMag((velocity.magnitude() < veloLimit * 0.25) ? (veloLimit) : (velocity.magnitude()));//if enemy is 'slow' the push back is adjusted
            applyForce(enemyPushback, 10);
            //
            Main.player.inflictDamage(1);//TODO REMEMBER current default enemy damage is 1
            Vecc2f pushback = new Vecc2f(originalVELO.x,originalVELO.y);
            pushback.setMag((originalVELO.magnitude() < veloLimit * 0.25) ? (veloLimit) : (originalVELO.magnitude()));//if enemy is 'slow' the push back is adjusted

            Main.player.applyForce(pushback,3);
        }
    }

    public void seperationSetter() {
        Vecc2f seperation = seperation(this.position, this.velocity);
        applyForce(seperation.limit(1), 0.4f);
    }

    public Vecc2f seperation(Vecc2f position, Vecc2f velocity) {
        Vecc2f steering = new Vecc2f();
        int total = 0;
        //
        for (Enemy enemy : parentRoom.enemies) {
            total = getTotal(position, steering, total, enemy.position);
        }
        if (total > 0) {
            steering.div(total);
            steering.setMag(12);
            steering.sub(velocity);
            steering.limit((float) 0.5);
        }
        steering.mult((float) 2);
        return steering;
    }

    private int getTotal(Vecc2f position, Vecc2f steering, int total, Vecc2f position2) {
        float d;
        d = (position.distance(position2));
        if ((d < 100) && position != position2) {
            Vecc2f difference = new Vecc2f().sub(position, position2);
            difference.div(d * d);
            steering.add(difference);
            total++;
        }
        return total;
    }

    public abstract void enemySpecificMovement();

    public void inflictDamage(int damage, Group group, ArrayList<Enemy> enemies) {
        this.health -= damage;
        this.health = Math.max(0, this.health);
        //
        if (this.health == 0 && (state != states.dying)) {
            state = states.dying;
            beginDeath(group, enemies);
        }
    }

    public void beginDeath(Group group, ArrayList<Enemy> enemies) {
        if (deathAnimation != null) {
            //starts death animation
            this.timeline.pause();
            int x = (int) (deathAnimation[0].getWidth() - this.enemy.getBoundsInParent().getWidth());
            int y = (int) (deathAnimation[0].getHeight() - this.enemy.getBoundsInParent().getWidth());
            this.enemy.setImage(deathAnimation[0]);
            group.getChildren().remove(this.hitbox.getShape());
            this.position.sub(x / 2, y / 2);
            this.enemy.relocate(this.position.x, this.position.y);
            this.deathTimeline.play();
            removeShader();
            this.deathTimeline.setOnFinished(event -> {
                beginRemoval(group, enemies);
            });
        } else {//no death animation
            removeShader();
            beginRemoval(group, enemies);
        }
    }

    private void beginRemoval(Group group, ArrayList<Enemy> enemies) {
        unload(group);
        enemies.remove(this);
        parentRoom.checkDoors(group);
        debrisCheck(group);
    }

    private void debrisCheck(Group group) {
        for (int i = 0; i < gutNumber; i++) {
            int x = this.hitbox.getCenterX() + (((rand.nextInt(2) * 2) - 1) * rand.nextInt(this.hitbox.width));
            int y = this.hitbox.getCenterY() + (((rand.nextInt(2) * 2) - 1) * rand.nextInt(this.hitbox.height));
            parentRoom.newRealTimeProp(group, x, y, Effects.enemyGuts[rand.nextInt(Effects.enemyGuts.length - 1)], 0.5 + (2 * rand.nextFloat()));
        }
    }

    /***
     *
     *@param images - will be sent an array of images for it to linearly progress through
     *@param swapInterval - an image will update every 'swapInterval' frames
     *@return the return statement return the current frame for if something needs to be done at that frame
     */
    public int linearImageSwapper(Image[] images, int swapInterval) {
        if (++imageSwapIntervalCounter >= swapInterval) {

            this.enemy.setImage(images[imageCounter]);
            imageCounter++;
            if (imageCounter > images.length - 1) {
                imageCounter = 0;
                //return true;
            }

            imageSwapIntervalCounter = 0;
            return imageCounter;
        }
        return -1;
    }

    public void applyForce(Vecc2f dir, int magnitude) {
        dir.mult(magnitude);
        this.velocity.add(dir);
    }

    public void applyForce(Vecc2f dir, float magnitude) {
        dir.mult(magnitude);
        this.velocity.add(dir);
    }

    public void relocate() {
        this.enemy.relocate(this.position.x, this.position.y);
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
    }

    public void addShader() {
        if (this.lightRadius > 0) {
            roomShading.addActiveSource((float) (this.hitbox.getCenterX()), (float) (this.hitbox.getCenterY()), this.activeShader, this.uniqueID);
        }
    }

    public void removeShader() {
        if (this.lightRadius > 0) {
            this.roomShading.removeActiveSource(this.uniqueID);
        }
    }

    public void setVeloLimit(float i) {
        this.veloLimit = i * avgScale;
    }

    public void updateCenterPos() {
        this.centerPos.set(this.hitbox.getCenterX(), this.hitbox.getCenterY());
    }

    public void load(Group group) {
        this.parentGroup=group;

        postLoading(group);
    }

    protected abstract void postLoading(Group group);

    public void unload(Group group) {

        postUnLoading(group);
    }

    protected abstract void postUnLoading(Group group);

    public abstract  boolean collidesWith(Tear tear);

    public Hitbox getHitbox() {
        return hitbox;
    }

    public void setHitbox(Hitbox hitbox) {
        this.hitbox = hitbox;
    }

    public Vecc2f getPosition() {
        return position;
    }
}