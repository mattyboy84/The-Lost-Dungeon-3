package root.game.dungeon.room.boss;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import root.Main;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.Tear.Tear;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

public class Boss_Pin extends Boss implements Sprite_Splitter {

    ImageView segment_head=new ImageView();
    Image[] segment_head_images = new Image[3];
    //
    ImageView body=new ImageView();
    Image[] body_images = new Image[3];
    //
    ImageView ground=new ImageView();
    Image[] ground_images = new Image[4];
    Image ground_close;

    Vecc2f bodyPosition = new Vecc2f(600, 600);

    states state;

    private enum states {
        Idle,
        Attack1,
        Attack2;
    }


    public Boss_Pin(JsonObject bossTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super(bossTemplate, pos, scaleX, scaleY, screenBounds, shading, parentRoom);
        this.state = states.Idle;
        String completePath = "file:src\\resources\\gfx\\bosses\\" + this.bossType + "\\" + this.filepath + ".png";
        //3 head segments
        for (int i = 0; i < 3; i++) {
            segment_head_images[i] = imageGetter(completePath, i * 64, 0, 64, 64, this.scaleX, this.scaleY, this.sheetScale);
        }
        //3 body images
        for (int i = 0; i < 3; i++) {
            body_images[i] = imageGetter(completePath, i * 64, 64, 64, 64, this.scaleX, this.scaleY, this.sheetScale);
        }
        //4 ground images - animation
        for (int i = 0; i < 4; i++) {
            ground_images[i] = imageGetter(completePath, 256 + (i * 64), 32, 64, 48, this.scaleX, this.scaleY, this.sheetScale);
        }
        ground_close = imageGetter(completePath, 384, 80, 64, 48, this.scaleX, this.scaleY, this.sheetScale);
        //
        mainlineSetup();
        attack1Setup();
        attack2Setup();
        //
        this.body.setImage(body_images[0]);
        this.ground.setImage(ground_images[1]);

    }

    private void attack2Setup() {
        attack2 = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            //
            attack2Cycle++;
            //
        }));
        attack2.setCycleCount(Timeline.INDEFINITE);
    }

    private void attack1Setup() {
        attack1 = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            //
            switch (attack1Cycle){
                case 0:
                    try {
                        parentGroup.getChildren().addAll(this.body, this.ground);
                    }catch (Exception e){}this.body.relocate(this.bodyPosition.x, this.bodyPosition.y);
                    this.ground.relocate(this.bodyPosition.x,this.bodyPosition.y+(32*sheetScale*scaleY));
                    this.body.setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
                    this.ground.setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
                    break;
                case 20:
                    this.body.setImage(body_images[2]);
                    break;
                case 40:
                    this.body.setImage(body_images[1]);
                    break;
                case 80:
                    this.parentRoom.addNewArc_Tear(damage,this.bodyPosition,
                            playerTarget.getCenterPos(),this.parentGroup,Tear.Target.player,playerTarget);
                    this.state=states.Idle;
                    this.attack1.stop();
                    this.mainline.play();
                    break;
            }
            attack1Cycle++;

            //
        }));
        attack1.setCycleCount(Timeline.INDEFINITE);
    }

    private void mainlineSetup() {
        mainline = new Timeline(new KeyFrame(Duration.seconds(1.5), event -> {
            //
            switch (state) {

                case Idle -> {//randomly chooses attack 1/2
                    switch (random.nextInt(1)) {
                        case 0:
                            this.state = states.Attack1;

                            break;
                        case 1:
                            this.state = states.Attack2;
                            break;
                    }
                }
                case Attack1 -> {
                    transitionToAttack1();
                }
                case Attack2 -> {
                    transitionToAttack2();

                }
            }
            //
        }));
        mainline.setCycleCount(Timeline.INDEFINITE);
    }

    private void transitionToAttack1() {
        this.mainline.stop();
        attack1Cycle = 0;
        attack1.play();
    }

    private void transitionToAttack2() {
        this.mainline.stop();
        attack2Cycle = 0;
    }

    @Override
    public void postLoader(Group group) {
        this.mainline.play();

    }

    @Override
    public void postUnLoader(Group group) {

    }

    @Override
    public boolean collidesWith(Tear tear) {
        return false;
    }
}