package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Player implements Runnable {

    public static boolean loaded = false;
    //
    int XAnimateCounter, YAnimateCounter;
    //
    int shootCooldown = 45;
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
    //
    ImageView head = new ImageView();
    ImageView body = new ImageView();
    //
    int width, height;
    //
    int coinNumber = 0, keyNumber = 0, bombNumber = 0;
    int score = 100;
    int health = 6, TOTAL_Health = 6;
    final int MIN_Health = 0, MAXIMUM_HEALTH = 32;
    //
    Vecc2f VECscale = new Vecc2f();
    Vecc2f bodyOffset, headOffset;
    Vecc2f bodyDelta, headDelta;
    Hitbox headHitbox, bodyHitbox, nextXFrameBodyHitbox, nextYFrameBodyHitbox;
    //
    Vecc2f direction = new Vecc2f();
    Vecc2f position = new Vecc2f();
    Vecc2f velocity = new Vecc2f();
    Vecc2f acceleration = new Vecc2f();
    //
    Vecc2f xSpeed = new Vecc2f((float) 0.1, 0);
    Vecc2f ySpeed = new Vecc2f((float) 0, (float) 0.1);
    boolean moving;
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
    //
    Room currentRoom;
    Player_Overlay overlay;
    //
    int lightRadius = 110;
    //
    float veloLimit;//default is 7 multiplied by screen scale
    //
    boolean northMOVING, eastMOVING, westMOVING, southMOVING;
    boolean northLOOKING, eastLOOKING, westLOOKING, southLOOKING;
    //
    Timeline controller;
    //

    Circle center = new Circle(1);
    Rectangle2D screenBounds;
    int sheetScale;
    Dungeon dungeon;
    String threadName;
    private Thread t;
    Circle c;

    Group group;

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
    }

    public void run() {
        overlay = new Player_Overlay(scaleX, scaleY, screenBounds, sheetScale, score, dungeon.map);
        overlay.updateHealth(health, TOTAL_Health, MAXIMUM_HEALTH, group);
        overlay.miniMap.load(group);

        overlay.miniMap.updateMinimap(this.roomX, this.roomY);
        overlay.largeMap.updateLargemap(this.roomX, this.roomY, screenBounds);

        xSpeed.mult(scaleX);
        ySpeed.mult(scaleY);
        veloLimit = 7 * avgScale;
        VECscale.set(scaleX, scaleY);
        //
        bodyOffset = new Vecc2f(0 * scaleX, 0 * scaleY);
        headOffset = new Vecc2f(0 * scaleX, -10 * scaleY);
        bodyDelta = new Vecc2f(8 * scaleX, 13 * scaleY);
        headDelta = new Vecc2f(16 * scaleX, 4 * scaleY);
        bodyOffset.mult(sheetScale);
        headOffset.mult(sheetScale);
        bodyDelta.mult(sheetScale);
        headDelta.mult(sheetScale);
        //
        this.width = (int) (32 * scaleX * sheetScale);
        this.height = (int) (32 * scaleY * sheetScale);
        //
        String file = "file:src\\resources\\gfx\\characters\\costumes\\" + this.costume + ".png";
        for (int i = 0; i < this.heads.length; i++) {//head images
            this.heads[i] = (new ImageView(new WritableImage(new Image(file, ((new Image(file).getWidth() * scaleX * sheetScale)), ((new Image(file).getHeight() * scaleY * sheetScale)), false, false).getPixelReader(),
                    (this.width * i), 0, (int) this.width, (int) this.height))).getImage();
        }
        //
        readImageINTOArray(file, sheetScale, scaleX, scaleY, UD_body, (int) Math.round(192 * scaleX * sheetScale), 0);
        //
        readImageINTOArray(file, sheetScale, scaleX, scaleY, LR_body, 0, (int) Math.round(64 * sheetScale * scaleY));
        //
        roomFinder(dungeon);
        //
        this.body.setImage(UD_body[2]);//default is 2
        this.head.setImage(heads[0]);
        //
        bodyHitbox = new Hitbox("Rectangle", 16, 11, sheetScale, scaleX, scaleY, 8, 13);
        nextXFrameBodyHitbox = new Hitbox("Rectangle", 16, 11, sheetScale, scaleX, scaleY, 8, 13);
        nextYFrameBodyHitbox = new Hitbox("Rectangle", 16, 11, sheetScale, scaleX, scaleY, 8, 13);


        //int radius = (int) (13 * sheetScale * (scaleX + scaleY) / 2);
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
        c = new Circle(1);
        //
        updateItems();

        try {
            playerController(dungeon);
        } catch (Exception e) {

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
            animationTimer++;
            doorTriggerTimer++;
            attackingTimer++;
            scoreTimer++;
            //
            this.direction.set(velocity.x, velocity.y);
            this.direction.limit(1);
            accDecider();
            this.acceleration.limit(2 * avgScale);
            this.velocity.add(this.acceleration);
            //
            this.velocity.mult((float) 0.95);
            //
            //
            this.attacking = this.northLOOKING || this.eastLOOKING || this.westLOOKING || this.southLOOKING;
            //
            //
            if (this.velocity.magnitude() < 0.2) {
                moving = false;
                this.velocity.set(0, 0);
                //this.position.set((int) this.position.x, (int) this.position.y);
                if (!colliding()) {
                    this.position.set((int) this.position.x, (int) this.position.y);
                }
                this.body.setImage(UD_body[2]);
                if (!justShot) {
                    this.head.setImage(heads[0]);
                }
                this.movingDirection = "south";
                relocate();
            }
            this.velocity.limit(veloLimit);
            //
            this.position.add(this.velocity);
            relocate();
            //
            boundaryChecker();
            //
            if (!this.northLOOKING && !this.southLOOKING && !this.eastLOOKING && !this.westLOOKING) {
                lookingDirection = null;
            }
            if (!justShot) {
                MOVINGheadChanger();
                LOOKINGheadChanger();
            } else {
                shotTimer++;
                if (shotTimer >= (int) (shootCooldown / 3)) {
                    justShot = false;
                }
            }
            //
            attackingDecider();
            //
            if (attacking && attackingTimer >= shootCooldown) {
                SHOOTINGheadChanger();
                //shoot tear
                justShot = true;
                shotTimer = 0;
                attackingTimer = 0;
            }
            //
            if (moving && animationTimer >= 6) {
                playerAnimator();
                animationTimer = 0;
            }
            //
            if (doorTriggerTimer >= 6) {
                doorTriggerTimer = 0;
                doorTriggerChecker(dungeon);
            }
            if (scoreTimer >= 60 && start) {//waits until initial movement then starts the UI
                scoreTimer = 1;
                updateScore(-1);
                updateTime();
            }
            //
            c.relocate(this.position.x, this.position.y);
            //
            currentRoom.shading.addActiveSource((float) (this.headHitbox.getShape().getLayoutX() + (this.headHitbox.radius * (1 / Math.sqrt(2)))), (float) (this.headHitbox.getShape().getLayoutY() + (this.headHitbox.radius * (1 / Math.sqrt(2)))), this.lightRadius, hashCode());
        }));
        controller.setCycleCount(Timeline.INDEFINITE);
        controller.play();
    }


    private void roomFinder(Dungeon dungeon) {
        for (int i = 0; i < dungeon.rooms.size(); i++) {
            if (this.roomX == dungeon.rooms.get(i).getI() && this.roomY == dungeon.rooms.get(i).getJ()) {
                currentRoom = dungeon.rooms.get(i);
                break;
            }
        }
    }

    private void doorTriggerChecker(Dungeon dungeon) {
        for (int i = 0; i < currentRoom.doors.size(); i++) {
            if (currentRoom.doors.get(i).getDoorTrigger().getBoundsInParent().intersects(this.bodyHitbox.shape.getBoundsInParent())) {
                System.out.println("changing room");
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
                roomFinder(dungeon);
                currentRoom.load(group);
            }
        }
    }

    public void increaseHealth(int change, Group group) {
        this.health += change;
        this.health = Math.min(this.health, TOTAL_Health);
        overlay.updateHealth(this.health, this.TOTAL_Health, this.MAXIMUM_HEALTH, group);
    }

    public void decreaseHealth(int change, Group group) {
        this.health -= change;
        this.health = Math.max(this.health, MIN_Health);
        overlay.updateHealth(this.health, this.TOTAL_Health, this.MAXIMUM_HEALTH, group);
    }

    public void increaseMaxHealth(int change, Group group) {
        this.TOTAL_Health += change;
        if (TOTAL_Health > MAXIMUM_HEALTH) {
            TOTAL_Health = MAXIMUM_HEALTH;
            //this.health=MAXIMUM_HEALTH;
        } else {
            this.health += 2;
        }
        overlay.updateHealth(this.health, this.TOTAL_Health, this.MAXIMUM_HEALTH, group);
    }

    public void decreaseMaxHealth(int change, Group group) {
        this.TOTAL_Health -= change;
        if (TOTAL_Health < MIN_Health) {
            TOTAL_Health = MIN_Health;
        } else {
            this.health -= 2;
        }
        overlay.updateHealth(this.health, this.TOTAL_Health, this.MAXIMUM_HEALTH, group);
    }

    private void updateTime() {
        overlay.updateTime();
    }

    public void updateScore(int diff) {
        this.score += diff;
        overlay.updateScore(score);
    }

    private void updateItems() {
        updateCoins(0);
        updateKeys(0);
        updateBombs(0);
    }

    public void updateCoins(int diff) {
        this.coinNumber += diff;
        overlay.updateCoinNumber(this.coinNumber);
    }

    public void updateKeys(int diff) {
        this.keyNumber += diff;
        overlay.updateKeyNumber(this.keyNumber);
    }

    public void updateBombs(int diff) {
        this.bombNumber += diff;
        overlay.updateBombNumber(this.bombNumber);
    }

    public boolean colliding() {
        //boolean a=false;
        for (int i = 0; i < currentRoom.getBoundaries().size(); i++) {
            if ((currentRoom.getBoundaries().get(i).getBoundsInParent().intersects(this.nextXFrameBodyHitbox.shape.getBoundsInParent()) ||
                    currentRoom.getBoundaries().get(i).getBoundsInParent().intersects(this.nextYFrameBodyHitbox.shape.getBoundsInParent())) && !collide) {
                return true;
            }
        }
        return false;
    }

    private void boundaryChecker() {
        for (int i = 0; i < currentRoom.getBoundaries().size(); i++) {
            if (currentRoom.getBoundaries().get(i).getBoundsInParent().intersects(this.nextXFrameBodyHitbox.shape.getBoundsInParent())) {
                collide = true;
                this.position.sub(this.velocity);
                this.velocity.mult((float) 0.8);
                //this.velocity.set(this.velocity.x*0.8,this.velocity.y);
                this.acceleration.mult((float) 0.8);
                //
            } else {
                collide = false;
            }
            if (currentRoom.getBoundaries().get(i).getBoundsInParent().intersects(this.nextYFrameBodyHitbox.shape.getBoundsInParent())) {
                collide = true;
                this.position.sub(this.velocity);
                this.velocity.mult((float) 0.8);
                //this.velocity.set(this.velocity.x,this.velocity.y*0.8);
                this.acceleration.mult((float) 0.8);
                //
            } else {
                collide = false;
            }
        }
        //collide = false;
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
        if (this.velocity.magnitude() > 0.2) {
            this.moving = true;
        }
    }

    private void readImageINTOArray(String file, int sheetScale, float scaleX, float scaleY, Image[] ARRAY, int startX, int startY) {
        for (int i = 0; i < ARRAY.length; i++) {
            ARRAY[i] = (new ImageView(new WritableImage(new Image(file, ((int) (new Image(file).getWidth() * scaleX * sheetScale)), ((int) (new Image(file).getHeight() * scaleY * sheetScale)), false, false).getPixelReader(),
                    startX, startY, (int) this.width, (int) this.height))).getImage();
            startX = startX + this.width;
            if (startX >= (int) this.width * 8) {
                startX = 0;
                startY = startY + this.height;
            }
        }
    }

    private void playerAnimator() {
        float angle = this.direction.toAngle();
        if (angle > 45 && angle < 135) {//right
            this.movingDirection = "east";
            body.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            subPlayerAnimator(XAnimateCounter, LR_body);
        } else if (angle > 225 && angle < 315) {//left
            this.movingDirection = "west";
            body.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            subPlayerAnimator(XAnimateCounter, LR_body);
            //
        } else if (angle > 135 && angle < 225) {//down
            this.movingDirection = "south";
            subPlayerAnimator(YAnimateCounter, UD_body);
        } else if (angle > 315 || angle < 45) {//up
            this.movingDirection = "north";
            subPlayerAnimator(YAnimateCounter, UD_body);
        }
    }

    private void subPlayerAnimator(int counter, Image[] arr) {
        counter++;
        if (counter > arr.length - 1) {
            counter = 0;
        }
        body.setImage(arr[counter]);
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


    private void relocate() {
        this.body.relocate(position.x, position.y);
        this.head.relocate(position.x + headOffset.x, position.y + headOffset.y);
        this.headHitbox.getShape().relocate(position.x + headDelta.x - (this.headHitbox.radius), position.y + headDelta.y - (this.headHitbox.radius));
        this.bodyHitbox.getShape().relocate(position.x + bodyDelta.x, position.y + bodyDelta.y);
        this.nextXFrameBodyHitbox.getShape().relocate(bodyHitbox.shape.getLayoutX() + this.velocity.x, bodyHitbox.shape.getLayoutY() + this.velocity.y);
        this.nextYFrameBodyHitbox.getShape().relocate(bodyHitbox.shape.getLayoutX(), bodyHitbox.shape.getLayoutY() + this.velocity.y);
    }

    public void load(Group group) {
        group.getChildren().addAll(this.headHitbox.getShape(), this.nextXFrameBodyHitbox.getShape(), this.nextYFrameBodyHitbox.getShape(), this.bodyHitbox.getShape(), this.body, this.head, this.c);
        //
        this.headHitbox.getShape().setViewOrder(-7);
        this.bodyHitbox.getShape().setViewOrder(-7);
        this.nextXFrameBodyHitbox.getShape().setViewOrder(-7);
        this.nextYFrameBodyHitbox.getShape().setViewOrder(-7);
        //
        this.body.setViewOrder(-7);
        this.head.setViewOrder(-7);
        //
        group.getChildren().add(center);
        center.setViewOrder(-12);
        //
        this.body.setVisible(true);
        this.head.setVisible(true);
        this.headHitbox.shape.setVisible(false);
        this.bodyHitbox.shape.setVisible(false);
        this.nextXFrameBodyHitbox.shape.setVisible(false);
        this.nextYFrameBodyHitbox.shape.setVisible(false);
        //
        this.position.set(800, 400);
        relocate();


        overlay.load(group);
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
}
