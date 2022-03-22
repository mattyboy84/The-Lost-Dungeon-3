package root.game.player;

import animatefx.animation.RubberBand;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import root.game.dungeon.Dungeon;
import root.game.dungeon.room.Door;
import root.game.dungeon.room.Room;
import root.game.music.Music;
import root.game.util.*;

import java.util.Random;

public class Player implements Runnable, Entity_Shader, Sprite_Splitter {

    public static boolean loaded = false;
    Random random = new Random();
    //
    int animateCounter;
    //
    int shootCooldown = 36;
    //
    float avgScale;
    String movingDirection = "south";
    String lookingDirection = "south";
    int roomX, roomY;
    float scaleX, scaleY;
    String costume;
    Image[] heads = new Image[6];
    Image[] LR_body = new Image[10];
    Image[] UD_body = new Image[10];
    Image[] deathImages = new Image[3];
    //
    ImageView head = new ImageView();
    ImageView body = new ImageView();
    ImageView fullIsaac = new ImageView();
    //
    public int coinNumber = 5, keyNumber = 1, bombNumber = 3;
    int score = 100;
    public int health = 6, TOTAL_Health = 6;
    public final int MIN_Health = 0, MAXIMUM_HEALTH = 32;
    //
    Vecc2f VECscale = new Vecc2f();
    Vecc2f bodyOffset, headOffset;
    Vecc2f bodyDelta, headDelta;
    Vecc2f fullIsaacOffset;
    Hitbox headHitbox;
    Hitbox bodyHitbox;
    Hitbox nextXFrameBodyHitbox;
    Hitbox nextYFrameBodyHitbox;
    //
    Vecc2f direction = new Vecc2f();
    Vecc2f position = new Vecc2f();
    Vecc2f velocity = new Vecc2f();
    Vecc2f acceleration = new Vecc2f();
    public static Vecc2f centerPos = new Vecc2f();
    //
    Vecc2f xSpeed = new Vecc2f((float) 0.2, 0);
    Vecc2f ySpeed = new Vecc2f((float) 0, (float) 0.2);
    boolean attacking;
    boolean collide = false;
    boolean justShot = false;
    boolean start = false;
    //timers;
    int animationTimer;
    int doorTriggerTimer;
    int attackingTimer;
    int shotTimer;
    int scoreTimer;
    int bombTimer;
    int vulnerableTimer;
    int stateTransitionTimer;
    final int vulnerableDuration = 60;
    //
    public Room currentRoom;
    Player_Overlay overlay;
    //
    int lightRadius = 100;//default is 100
    float[][] shader;
    int damage = 5;
    int tearSize = 5;
    //
    float veloLimit;//default is 7 multiplied by screen scale
    //
    boolean northMOVING, eastMOVING, westMOVING, southMOVING;
    boolean northLOOKING, eastLOOKING, westLOOKING, southLOOKING;
    //
    Timeline controller;
    //
    Rectangle2D screenBounds;
    int sheetScale;
    Dungeon dungeon;
    String threadName;
    private Thread t;
    Group group;

    enum states {
        idle,
        moving,
        hurt,
        dying,
        death,
        transitioning
    }

    states state = null;

    public void Generate(String costume, int startX, int startY, float scaleX, float scaleY, Rectangle2D screenBounds, int sheetScale, Dungeon dungeon, String threadName, Group group) {
        this.group = group;
        this.costume = costume;
        this.roomX = startX;
        this.roomY = startY;
        System.out.println("");
        System.out.println("StartX: " + this.roomX + " StartY: " + this.roomY);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.screenBounds = screenBounds;
        this.sheetScale = sheetScale;
        this.dungeon = dungeon;
        this.avgScale = ((scaleX + scaleY) / 2);
        this.threadName = threadName;
        this.lightRadius = (int) (this.lightRadius * avgScale);
        shader = setupShader(this.lightRadius);
    }

    public void run() {
        overlay = new Player_Overlay(scaleX, scaleY, screenBounds, sheetScale, score, dungeon.map);
        overlay.setupHealth(3, TOTAL_Health, MAXIMUM_HEALTH, group);
        overlay.miniMap.load(group);

        overlay.miniMap.updateMinimap(this.roomX, this.roomY);
        overlay.largeMap.updateLargemap(this.roomX, this.roomY, screenBounds);

        xSpeed.mult(scaleX);
        ySpeed.mult(scaleY);
        veloLimit = 7 * avgScale;
        VECscale.set(scaleX, scaleY);
        //
        bodyOffset = new Vecc2f(0, 0).mult(VECscale).mult(sheetScale);
        headOffset = new Vecc2f(0, -10).mult(VECscale).mult(sheetScale);
        bodyDelta = new Vecc2f(8, 13).mult(VECscale).mult(sheetScale);
        headDelta = new Vecc2f(16, 4).mult(VECscale).mult(sheetScale);
        fullIsaacOffset = new Vecc2f(-16, -32).mult(VECscale).mult(sheetScale);
        //
        String file = "file:src\\resources\\gfx\\characters\\costumes\\" + this.costume + ".png";
        for (int i = 0; i < this.heads.length; i++) {//head images
            this.heads[i] = imageGetter(file, 32 * i, 0, 32, 32, scaleX, scaleY, sheetScale);
        }
        //
        readImageINTOArray(file, sheetScale, scaleX, scaleY, UD_body, (int) Math.round(192), 0);//prepares the Up & Down body animation
        //
        readImageINTOArray(file, sheetScale, scaleX, scaleY, LR_body, 0, (int) Math.round(64));//prepares the Left & Right body animation
        //
        prepareDeathImages(file, sheetScale);//prepares the Isaac death animation
        //
        roomFinder(dungeon);
        //
        this.body.setImage(UD_body[2]);//default is 2
        this.head.setImage(heads[0]);
        this.state = states.idle;
        //
        bodyHitbox = new Hitbox("Rectangle", 16, 11, sheetScale, scaleX, scaleY, 8, 13);
        nextXFrameBodyHitbox = new Hitbox("Rectangle", 16, 11, sheetScale, scaleX, scaleY, 8, 13);
        nextYFrameBodyHitbox = new Hitbox("Rectangle", 16, 11, sheetScale, scaleX, scaleY, 8, 13);

        headHitbox = new Hitbox("Circle", 12, 12, sheetScale, scaleX, scaleY, 16, 4);
        //caching
        {
            this.body.setCache(true);
            this.body.setCacheHint(CacheHint.QUALITY);
            this.head.setCache(true);
            this.head.setCacheHint(CacheHint.QUALITY);
            this.bodyHitbox.getShape().setCache(true);
            this.bodyHitbox.getShape().setCacheHint(CacheHint.QUALITY);
            this.headHitbox.getShape().setCache(true);
            this.headHitbox.getShape().setCacheHint(CacheHint.QUALITY);
        }
        //
        updateItems(0);

        try {
            playerController(dungeon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        loaded = true;
    }

    public void start() {
        System.out.println("");
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }


    private void playerController(Dungeon dungeon) {
        controller = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            currentRoom.shading.removeActiveSource(hashCode());
            //timers
            stateTransitionTimer++;
            vulnerableTimer++;
            animationTimer++;
            doorTriggerTimer++;
            attackingTimer++;
            scoreTimer++;
            bombTimer++;
            //
            this.direction.set(velocity.x, velocity.y);
            this.direction.limit(1);
            //
            //System.out.println("State: " + this.state + " Acc: " + this.acceleration + " Velo:" + this.velocity);
            //
            this.attacking = this.northLOOKING || this.eastLOOKING || this.westLOOKING || this.southLOOKING;
            this.velocity.limit((this.velocity.magnitude() > veloLimit * 1.5) ? (this.velocity.magnitude() * 0.9) : (veloLimit));
            //
            if (!this.northLOOKING && !this.southLOOKING && !this.eastLOOKING && !this.westLOOKING) {
                lookingDirection = null;
            }
            //
            switch (state) {
                case idle:
                    //
                    startToMove();
                    relocate();
                    //
                    if (!justShot) {
                        MOVINGheadChanger();//changes head image to looking direction
                        LOOKINGheadChanger();//priority to change head to attacking direction
                    } else {
                        shotTimer++;
                        if (shotTimer >= (shootCooldown / 3)) {
                            justShot = false;
                        }
                    }
                    //
                    attackingDecider();//decides the looking direction in order to shoot - gives preference to north/south axis
                    //
                    playerAttackChecker();
                    //
                    if (this.velocity.magnitude() > 0.2) {
                        this.state = states.moving;
                        stateTransitionTimer = 0;
                    }
                    break;
                case moving:
                    //
                    startToMove();
                    relocate();
                    //
                    if (!justShot) {
                        MOVINGheadChanger();//changes head image to looking direction
                        LOOKINGheadChanger();//priority to change head to attacking direction
                    } else {
                        shotTimer++;
                        if (shotTimer >= (shootCooldown / 3)) {
                            justShot = false;
                        }
                    }
                    //
                    attackingDecider();//decides the looking direction in order to shoot - gives preference to north/south axis
                    //
                    playerAttackChecker();
                    //
                    playerAnimator();//if moving, body will animate every x frames
                    //
                    transitionToIdle();//checks if state should be changed to Idle
                    break;
                case hurt:

                    startToMove();//enables moving in this state
                    relocate();

                    if (stateTransitionTimer >= 45) {
                        this.fullIsaac.setVisible(false);
                        this.head.setVisible(true);
                        this.body.setVisible(true);
                        //
                        if (this.velocity.magnitude() > 0.2) {
                            this.state = states.moving;
                            stateTransitionTimer = 0;
                        } else {
                            transitionToIdle();
                        }
                    }
                    break;
                case dying:

                    RubberBand a = new RubberBand(this.fullIsaac);//uses the AnimateFX Lib
                    a.play();
                    a.setOnFinished(endOfBandEvent -> {//at the end of the Rubberband - Isaac will rotate 90 degrees (falling to the floor)
                        this.fullIsaac.setImage(deathImages[1]);
                        Timeline rotateIsaac = new Timeline(new KeyFrame(Duration.millis(6), rotateIsaacEvent -> {
                            this.fullIsaac.getTransforms().add(new Rotate(1, this.fullIsaac.getBoundsInParent().getWidth() / 2, (this.fullIsaac.getBoundsInParent().getHeight() / 2)));
                        }));
                        rotateIsaac.setCycleCount(90);
                        rotateIsaac.play();
                        //
                        rotateIsaac.setOnFinished(event1 -> {//when Isaac has fallen to the floor it will change to the 'curled up' position and corrects for offset. Isaac is now defeated.
                            this.fullIsaac.getTransforms().clear();
                            this.position.sub(-(10 * sheetScale * scaleX), (19 * sheetScale * scaleY));
                            relocate();
                            this.fullIsaac.setImage(deathImages[2]);
                        });
                    });
                    stateTransitionTimer = 0;
                    //
                    this.state = states.death;
                    break;
                case death:
                    //TODO At this point, the game could add the current score to a database, provide an option to re-try or close the game.
                    break;
                case transitioning:

                    break;
            }
            //
            centerPos.set(this.bodyHitbox.getCenterX(), ((this.bodyHitbox.getCenterY() + this.headHitbox.getCenterY()) / 2));
            doorTriggerChecker();//looks for door triggers to transfer rooms
            boundaryChecker(group);
            itemCollisionChecker();
            uiStarter();
            //
            currentRoom.shading.addActiveSource((float) (headHitbox.getCenterX()), (float) (headHitbox.getCenterY()), shader, hashCode());
        }));
        controller.setCycleCount(Timeline.INDEFINITE);
        controller.play();
    }

    private void startToMove() {
        accDecider();
        this.acceleration.limit(2 * avgScale);
        this.velocity.add(this.acceleration);
        //
        this.velocity.mult((float) 0.9);
        this.position.add(this.velocity);
    }

    private void transitionToIdle() {
        if (this.velocity.magnitude() < 0.2) {
            this.velocity.set(0, 0);
            //
            if (!colliding()) {
                this.position.set((int) this.position.x, (int) this.position.y);
            }
            this.body.setImage(UD_body[2]);
            if (!justShot) {
                this.head.setImage(heads[0]);
            }
            this.movingDirection = "south";
            relocate();
            this.state = states.idle;
            stateTransitionTimer = 0;
        }
    }

    private void transitionToHurt() {
        this.fullIsaac.setImage(deathImages[0]);
        this.fullIsaac.setVisible(true);
        this.head.setVisible(false);
        this.body.setVisible(false);
        this.state = states.hurt;
        stateTransitionTimer = 0;
    }

    public void inflictDamage(int damage) {
        if ((vulnerableTimer > vulnerableDuration) && (state.equals(states.idle) || state.equals(states.moving))) {//can only be 'hurt' while idle or moving
            vulnerableTimer = 0;
            Music.addSFX(false, random.nextInt(Integer.MAX_VALUE), Music.sfx.hurt_grunt_0, Music.sfx.hurt_grunt_1, Music.sfx.hurt_grunt_2);
            //will choose 1 of 3 hurt sound effects
            this.changeHealthBy(-damage);

        }
    }

    private void playerAttackChecker() {
        if (attacking && attackingTimer >= shootCooldown) {
            SHOOTINGheadChanger();
            //shoot tear
            currentRoom.addNewPlayerTear(this.lookingDirection, damage, tearSize, group, new Vecc2f(this.headHitbox.getShape().getLayoutX(), this.headHitbox.getShape().getLayoutY() + (15 * scaleY)), this.velocity, scaleX, scaleY, this.veloLimit);
            //
            justShot = true;
            shotTimer = 0;
            attackingTimer = 0;
        }
    }

    private void playerAnimator() {
        if (animationTimer >= 6) {
            playerAnimatorSub();
            animationTimer = 0;
        }
    }

    private void doorTriggerChecker() {
        if (doorTriggerTimer >= 6) {
            doorTriggerTimer = 0;
            doorTriggerCheckerSub(dungeon);
        }
    }

    private void uiStarter() {
        if (scoreTimer >= 60 && start) {//waits until initial movement then starts the UI
            scoreTimer = 1;
            updateScore(-1);
            updateTime();
        }
    }

    public void placeBomb(Group group, String bombTemplate, Vecc2f centerPos) {
        if (bombTimer >= 60 && this.bombNumber > 0) {
            updateBombs(-1);
            bombTimer = 0;
            System.out.println("bomb placed");

            currentRoom.addBomb(this.group, bombTemplate, this.centerPos);

        }
    }

    private void itemCollisionChecker() {
        for (int i = 0; i < currentRoom.items.size(); i++) {
            currentRoom.items.get(i).checkCollision(this, currentRoom.items, group);
        }
    }

    private void roomFinder(Dungeon dungeon) {
        for (int i = 0; i < dungeon.rooms.size(); i++) {
            if (this.roomX == dungeon.rooms.get(i).getI() && this.roomY == dungeon.rooms.get(i).getJ()) {
                currentRoom = dungeon.rooms.get(i);
                break;
            }
        }
    }

    private void doorTriggerCheckerSub(Dungeon dungeon) {//"LOAD" "UNLOAD" "MAP"
        for (int i = 0; i < currentRoom.doors.size(); i++) {
            if (currentRoom.doors.get(i).getDoorTrigger().getBoundsInParent().intersects(this.bodyHitbox.getShape().getBoundsInParent())) {
                this.position.set(currentRoom.doors.get(i).relocatePos);
                relocate();
                this.acceleration.set(0, 0);
                this.velocity.set(0, 0);
                switch (currentRoom.doors.get(i).direction) {
                    case "up" -> this.roomX = this.roomX - 1;
                    case "down" -> this.roomX = this.roomX + 1;
                    case "left" -> this.roomY = this.roomY - 1;
                    case "right" -> this.roomY = this.roomY + 1;
                }

                overlay.miniMap.updateMinimap(this.roomX, this.roomY);
                overlay.largeMap.updateLargemap(this.roomX, this.roomY, screenBounds);
                currentRoom.unload(group);
                Music.clearSFX();
                String oldMusic = currentRoom.getMusic();
                roomFinder(dungeon);
                currentRoom.load(group, this);
                String newMusic = currentRoom.getMusic();
                Music.transition(oldMusic, newMusic, currentRoom);
                //
                System.out.println("changing room to: " + currentRoom.room);
                //
                if (currentRoom.enemies.size() == 0 && currentRoom.bosses.size() == 0) {
                    currentRoom.openDoors(group);
                }
            }
        }
    }

    public void changeHealthBy(int change) {
        this.health += change;
        this.health = Math.min(Math.max(this.health, MIN_Health), TOTAL_Health);//keep health between min & max
        overlay.updateHearts(this.health);
        if (health == MIN_Health) {
            transitionToDying();
        } else if (change < 0) {//player is being 'hurt'
            transitionToHurt();//changes the state to 'Hurt'
        }
    }

    private void transitionToDying() {
        this.state = states.dying;
        //
        this.fullIsaac.setImage(deathImages[0]);
        this.head.setVisible(false);
        this.body.setVisible(false);
        this.fullIsaac.setVisible(true);
    }

    public void changeMaxHealthBy(int change, Group group) {
        this.TOTAL_Health += change;
        this.TOTAL_Health = Math.min(Math.max(this.TOTAL_Health, MIN_Health), MAXIMUM_HEALTH);//keep total health between min & max
        //
        this.health += change;
        this.health = Math.min(Math.max(this.health, MIN_Health), TOTAL_Health);//keep health between min & new max
        //
        overlay.updateMaxHealth(this.health, this.MAXIMUM_HEALTH, group, change);
    }

    private void updateTime() {
        overlay.updateTime();
    }

    public void updateScore(int diff) {
        this.score += diff;
        this.score = Math.max(this.score, 0);//stops score from going below 0
        overlay.updateScore(score);
    }

    private void updateItems(int startPoint) {
        updateCoins(startPoint);
        updateKeys(startPoint);
        updateBombs(startPoint);
    }

    public void updateCoins(int diff) {
        if (this.coinNumber < Player_Overlay.MAX_ITEM_NUMBER) {
            this.coinNumber += diff;
            overlay.updateCoinNumber(this.coinNumber);
        }
    }

    public void updateKeys(int diff) {
        if (this.keyNumber < Player_Overlay.MAX_ITEM_NUMBER) {
            this.keyNumber += diff;
            overlay.updateKeyNumber(this.keyNumber);
        }
    }

    public void updateBombs(int diff) {
        if (this.bombNumber < Player_Overlay.MAX_ITEM_NUMBER) {
            this.bombNumber += diff;
            overlay.updateBombNumber(this.bombNumber);
        }
    }

    public boolean colliding() {
        //boolean a=false;
        for (int i = 0; i < currentRoom.getBoundaries().size(); i++) {
            if ((currentRoom.getBoundaries().get(i).getBoundsInParent().intersects(this.nextXFrameBodyHitbox.getShape().getBoundsInParent()) ||
                    currentRoom.getBoundaries().get(i).getBoundsInParent().intersects(this.nextYFrameBodyHitbox.getShape().getBoundsInParent())) && !collide) {
                return true;
            }
        }
        return false;
    }

    private void boundaryChecker(Group group) {
        for (int i = 0; i < currentRoom.doors.size(); i++) {
            if (currentRoom.doors.get(i).getDoorBlock().getBoundsInParent().intersects(this.bodyHitbox.getShape().getBoundsInParent()) && (currentRoom.doors.get(i).state == Door.State.locked)) {
                System.out.println("opening locked door");//unlock door
                currentRoom.doors.get(i).forceOpen(group);
                Music.addSFX(false, Integer.MAX_VALUE, Music.sfx.lock_break_0, Music.sfx.lock_break_1);
                updateKeys(-1);
            }
        }
        if (currentRoom.getTrapDoor() != null) {
            if (currentRoom.getTrapDoor().getState() == Door.State.closed && (group.getChildren().contains(currentRoom.getTrapDoor().getTrapDoor())) && Vecc2f.distance(centerPos.x, centerPos.y, currentRoom.getTrapDoor().getCenterPos().x, currentRoom.getTrapDoor().getCenterPos().y) > (350 * avgScale)) {
                currentRoom.getTrapDoor().openTrapDoor();
            }
            if (currentRoom.getTrapDoor().getState() == Door.State.open && (group.getChildren().contains(currentRoom.getTrapDoor().getTrapDoor())) && (currentRoom.getTrapDoor().getDoorTrigger().getBoundsInParent().intersects(this.bodyHitbox.getShape().getBoundsInParent()))) {
                beginFloorTransition();
            }
        }
        for (int i = 0; i < currentRoom.getBoundaries().size(); i++) {
            if (currentRoom.getBoundaries().get(i).getBoundsInParent().intersects(this.nextXFrameBodyHitbox.getShape().getBoundsInParent())
                    || currentRoom.getBoundaries().get(i).getBoundsInParent().intersects(this.nextYFrameBodyHitbox.getShape().getBoundsInParent())) {
                collide = true;
                this.position.sub(this.velocity);
                this.velocity.mult((float) 0.8);
                //this.velocity.set(this.velocity.x*0.8,this.velocity.y);
                this.acceleration.mult((float) 0.8);
                //
            } else {
                collide = false;
            }
        }
    }

    private void beginFloorTransition() {
        currentRoom.unload(group);
        currentRoom.beginFloorTransition();
    }

    private void attackingDecider() {
        if (eastLOOKING && westLOOKING) {
            eastLOOKING = false;
            westLOOKING = false;
        }
        if (northLOOKING && southLOOKING) {
            southLOOKING = false;
            northLOOKING = false;
        }
        if (eastLOOKING) {
            lookingDirection = "east";
        }
        if (westLOOKING) {
            lookingDirection = "west";
        }
        if (northLOOKING) {
            lookingDirection = "north";
        }
        if (southLOOKING) {
            lookingDirection = "south";
        }
    }

    private void accDecider() {
        this.acceleration.mult((float) 0.95);
        //
        this.acceleration = northMOVING ? (acceleration.sub(ySpeed)) : this.acceleration;
        this.acceleration = southMOVING ? (acceleration.add(ySpeed)) : this.acceleration;
        //
        this.acceleration = eastMOVING ? (acceleration.add(xSpeed)) : this.acceleration;
        this.acceleration = westMOVING ? (acceleration.sub(xSpeed)) : this.acceleration;
        //
        this.acceleration.x = (eastLOOKING && westLOOKING) ? (acceleration.x = 0) : (acceleration.x);
        this.velocity.x = (eastLOOKING && westLOOKING) ? (velocity.x = 0) : (velocity.x);
        //
        this.acceleration.y = (eastLOOKING && westLOOKING) ? (acceleration.y = 0) : (acceleration.y);
        this.velocity.y = (eastLOOKING && westLOOKING) ? (velocity.y = 0) : (velocity.y);
        //
        if (!northMOVING && !westMOVING && !eastMOVING && !southMOVING) {
            this.acceleration.set(0, 0);
        }
    }

    private void prepareDeathImages(String file, int sheetScale) {//three death images used in the death animation
        deathImages[0] = imageGetter(file, 0, 128, 64, 64, scaleX, scaleY, sheetScale);
        deathImages[1] = imageGetter(file, 128, 192, 64, 64, scaleX, scaleY, sheetScale);
        deathImages[2] = imageGetter(file, 192, 128, 64, 64, scaleX, scaleY, sheetScale);
    }

    private void readImageINTOArray(String file, int sheetScale, float scaleX, float scaleY, Image[] ARRAY, int startX, int startY) {
        for (int i = 0; i < ARRAY.length; i++) {
            ARRAY[i] = imageGetter(file, startX, startY, 32, 32, scaleX, scaleY, sheetScale);

            startX = startX + 32;
            if (startX >= (int) (32 * 8)) {
                startX = 0;
                startY = startY + 32;
            }
        }
    }

    private void playerAnimatorSub() {
        float angle = this.direction.toAngle() + 90;
        angle = (angle > 360) ? (angle - 360) : (angle);
        if (angle > 45 && angle < 135) {//right
            this.movingDirection = "east";
            body.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            subPlayerAnimator(LR_body);
        } else if (angle > 225 && angle < 315) {//left
            this.movingDirection = "west";
            body.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            subPlayerAnimator(LR_body);
            //
        } else if (angle > 135 && angle < 225) {//down
            this.movingDirection = "south";
            subPlayerAnimator(UD_body);
        } else if (angle > 315 || angle < 45) {//up
            this.movingDirection = "north";
            subPlayerAnimator(UD_body);
        }
    }

    private void subPlayerAnimator(Image[] arr) {
        animateCounter++;
        if (animateCounter > arr.length - 1) {
            animateCounter = 0;
        }
        body.setImage(arr[animateCounter]);
    }

    private void SHOOTINGheadChanger() {
        BASEheadChanger(this.lookingDirection, 5, 1, 3, 3);
    }

    private void LOOKINGheadChanger() {
        if (this.lookingDirection != null) {
            BASEheadChanger(this.lookingDirection, 4, 0, 2, 2);
        }
    }

    private void MOVINGheadChanger() {
        BASEheadChanger(this.movingDirection, 4, 0, 2, 2);
    }

    private void BASEheadChanger(String sw, int i, int i1, int i2, int i3) {
        switch (sw) {
            case "north":
                this.head.setImage(heads[i]);
                break;
            case "south":
                this.head.setImage(heads[i1]);
                break;
            case "west":
                this.head.setImage(heads[i2]);
                this.head.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                break;
            case "east":
                this.head.setImage(heads[i3]);
                this.head.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                break;
        }
    }

    public void applyForce(Vecc2f direction, int magnitude) {
        direction.add(0.1, 0.1);
        direction.mult(magnitude);
        this.acceleration.set(0, 0);
        this.velocity.add(direction);
    }


    private void relocate() {
        this.body.relocate(position.x, position.y);
        this.head.relocate(position.x + headOffset.x, position.y + headOffset.y);
        this.headHitbox.getShape().relocate(position.x + headDelta.x - (this.headHitbox.radius), position.y + headDelta.y - (this.headHitbox.radius));
        this.bodyHitbox.getShape().relocate(position.x + bodyDelta.x, position.y + bodyDelta.y);
        this.nextXFrameBodyHitbox.getShape().relocate(bodyHitbox.getShape().getLayoutX() + this.velocity.x, bodyHitbox.getShape().getLayoutY() + this.velocity.y);
        this.nextYFrameBodyHitbox.getShape().relocate(bodyHitbox.getShape().getLayoutX(), bodyHitbox.getShape().getLayoutY() + this.velocity.y);
        this.fullIsaac.relocate(position.x + fullIsaacOffset.x, position.y + fullIsaacOffset.y);
    }

    public void load(Group group) {
        group.getChildren().addAll(this.headHitbox.getShape(), this.nextXFrameBodyHitbox.getShape(), this.nextYFrameBodyHitbox.getShape(), this.bodyHitbox.getShape(), this.body, this.head/*, this.c*/);
        group.getChildren().add(this.fullIsaac);
        this.fullIsaac.setVisible(false);
        //
        this.headHitbox.getShape().setViewOrder(ViewOrder.player_layer.getViewOrder());
        this.bodyHitbox.getShape().setViewOrder(ViewOrder.player_layer.getViewOrder());
        this.nextXFrameBodyHitbox.getShape().setViewOrder(ViewOrder.player_layer.getViewOrder());
        this.nextYFrameBodyHitbox.getShape().setViewOrder(ViewOrder.player_layer.getViewOrder());
        //
        this.body.setViewOrder(ViewOrder.player_layer.getViewOrder());
        this.head.setViewOrder(ViewOrder.player_layer.getViewOrder());
        this.fullIsaac.setViewOrder(ViewOrder.player_layer.getViewOrder());
        //
        this.body.setVisible(true);
        this.head.setVisible(true);
        this.headHitbox.getShape().setVisible(false);
        this.bodyHitbox.getShape().setVisible(false);
        this.nextXFrameBodyHitbox.getShape().setVisible(false);
        this.nextYFrameBodyHitbox.getShape().setVisible(false);
        //
        this.position.set(800, 400);
        relocate();

        overlay.load(group);
    }

    public void transitionToNewDungeon(int startX, int startY, Dungeon dungeon, Group group) {
        this.roomX = startX;
        this.roomY = startY;
        this.dungeon = dungeon;
        //
        overlay.transitionToNewDungeon(group, dungeon.map);
        overlay.miniMap.load(group);

        overlay.miniMap.updateMinimap(this.roomX, this.roomY);
        overlay.largeMap.updateLargemap(this.roomX, this.roomY, screenBounds);

        //
        roomFinder(dungeon);
    }

    public boolean isVulnerable() {
        return (vulnerableTimer > vulnerableDuration);
    }

    private void startChecker() {
        if (!start) {
            start = true;
        }
    }

    public boolean isNorthMOVING() {
        return northMOVING;
    }

    public void setNorthMOVING(boolean northMOVING) {
        this.northMOVING = northMOVING;
        startChecker();
    }


    public boolean isEastMOVING() {
        return eastMOVING;
    }

    public void setEastMOVING(boolean eastMOVING) {
        this.eastMOVING = eastMOVING;
        startChecker();

    }

    public boolean isWestMOVING() {
        return westMOVING;
    }

    public void setWestMOVING(boolean westMOVING) {
        this.westMOVING = westMOVING;
        startChecker();

    }

    public boolean isSouthMOVING() {
        return southMOVING;
    }

    public void setSouthMOVING(boolean southMOVING) {
        this.southMOVING = southMOVING;
        startChecker();

    }

    public boolean isNorthLOOKING() {
        return northLOOKING;
    }

    public void setNorthLOOKING(boolean northLOOKING) {
        this.northLOOKING = northLOOKING;
        startChecker();
    }

    public boolean isEastLOOKING() {
        return eastLOOKING;
    }

    public void setEastLOOKING(boolean eastLOOKING) {
        this.eastLOOKING = eastLOOKING;
        startChecker();
    }

    public boolean isWestLOOKING() {
        return westLOOKING;
    }

    public void setWestLOOKING(boolean westLOOKING) {
        this.westLOOKING = westLOOKING;
        startChecker();
    }

    public boolean isSouthLOOKING() {
        return southLOOKING;
    }

    public void setSouthLOOKING(boolean southLOOKING) {
        this.southLOOKING = southLOOKING;
        startChecker();
    }

    public Hitbox getHeadHitbox() {
        return headHitbox;
    }

    public void setHeadHitbox(Hitbox headHitbox) {
        this.headHitbox = headHitbox;
    }

    public Hitbox getBodyHitbox() {
        return bodyHitbox;
    }

    public void setBodyHitbox(Hitbox bodyHitbox) {
        this.bodyHitbox = bodyHitbox;
    }

    public Player_Overlay getOverlay() {
        return overlay;
    }

    public Vecc2f getCenterPos() {
        return centerPos;
    }

    public Vecc2f getPosition() {
        return position;
    }
}
