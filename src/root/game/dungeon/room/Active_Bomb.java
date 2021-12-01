package root.game.dungeon.room;

import animatefx.animation.RubberBand;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import root.game.util.Effects;
import root.game.util.Hitbox;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Active_Bomb implements Sprite_Splitter {

    ImageView bomb;
    Image[] activeAnimation;
    Vecc2f position = new Vecc2f();
    Hitbox hitbox;
    JsonObject template = null;
    int fuse;
    int damageRadius = 150;

    Timeline activeTimeline;
    int activePointer = 0;

    Timeline pulseTimeline;

    Timeline explosionTimeline;
    int explosionPointer;

    //ColorAdjust blackout = new ColorAdjust();


    public Active_Bomb(String bombTemplate, Vecc2f centerPos, float scaleX, float scaleY, int fuse) {
        this.fuse = fuse;
        this.template = new JsonParser().parse(String.valueOf(templateGetter("src\\resources\\gfx\\items\\pick ups\\" + bombTemplate + ".json"))).getAsJsonObject();
        //
        String file = "file:src\\resources\\gfx\\items\\pick ups\\" + this.template.get("Sprite").getAsString() + ".png";
        int startX = this.template.get("StartX").getAsInt();
        int startY = this.template.get("StartY").getAsInt();
        int width = this.template.get("Width").getAsInt();
        int height = this.template.get("Height").getAsInt();
        float sheetScale = this.template.get("SheetScale").getAsFloat();
        this.hitbox = new Hitbox(this.template.getAsJsonObject("Hitbox"), (int) sheetScale, scaleX, scaleY);
        this.bomb = new ImageView(imageGetter(file, startX, startY, width, height, scaleX, scaleY, sheetScale));
        this.position.set(centerPos.x - (this.bomb.getBoundsInParent().getWidth() / 2), centerPos.y - (this.bomb.getBoundsInParent().getHeight() / 2));
        activeAnimationSetup(file, scaleX, scaleY, sheetScale, this.template.get("ActiveAnimation").getAsJsonArray());
        //
        pulseAnimationTimeline();

        activeAnimationTimeline();

        explosionAnimationTimeline();
    }

    private void explosionAnimationTimeline() {
        explosionTimeline = new Timeline(new KeyFrame(Duration.millis(120), event -> {
            subExplosion();
        }));
        explosionTimeline.setCycleCount(Effects.explodeAnimation.length - 1);
    }

    private void subExplosion() {
        this.bomb.setImage(Effects.explodeAnimation[explosionPointer]);
        explosionPointer = explosionPointer + 1;
        explosionPointer = (explosionPointer == Effects.explodeAnimation.length) ? (0) : (explosionPointer);
    }

    private void pulseAnimationTimeline() {
        pulseTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            new RubberBand(this.bomb).play();
        }));
        pulseTimeline.setCycleCount((fuse) - 1);
    }


    private void activeAnimationTimeline() {
        int fps = 5;
        activeTimeline = new Timeline(new KeyFrame(Duration.seconds((float) 1 / fps), event -> {
            subActive();
        }));
        activeTimeline.setCycleCount((fps * fuse) - 1);
    }

    private void subActive() {
        if (activeAnimation.length > 0) {
            this.bomb.setImage(activeAnimation[activePointer]);
            activePointer = activePointer + 1;
            activePointer = (activePointer == activeAnimation.length - 1) ? (0) : (activePointer);
        }
    }


    private void activeAnimationSetup(String file, float scaleX, float scaleY, float sheetScale, JsonArray activeAnimationARR) {
        this.activeAnimation = new Image[activeAnimationARR.size()];
        //EXAMPLE
        for (int i = 0; i < activeAnimationARR.size(); i++) {
            int x = activeAnimationARR.get(i).getAsJsonObject().get("StartX").getAsInt();
            int y = activeAnimationARR.get(i).getAsJsonObject().get("StartY").getAsInt();
            int width = activeAnimationARR.get(i).getAsJsonObject().get("Width").getAsInt();
            int height = activeAnimationARR.get(i).getAsJsonObject().get("Height").getAsInt();
            activeAnimation[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
        }
    }

    private StringBuilder templateGetter(String file2) {
        StringBuilder json = new StringBuilder();
        try {
            File file = new File(file2);

            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null) {
                json.append(st);
            }
        } catch (Exception e) {
            System.out.println("Cannot find template - Active_Bomb");
        }
        return json;
    }

    public void load(Group group, Room room, ArrayList<Active_Bomb> bombs) {
        group.getChildren().addAll(this.bomb, this.hitbox.getShape());
        this.bomb.relocate(this.position.x, this.position.y);
        this.bomb.setViewOrder(-4);
        //
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
        this.hitbox.getShape().setViewOrder(-4);
        this.hitbox.getShape().setVisible(false);
        //START TIMELINES

        //starts the bomb pulsing
        new RubberBand(this.bomb).play();
        this.pulseTimeline.play();

        //starts to swap active bomb images
        subActive();
        this.activeTimeline.play();
        this.activeTimeline.setOnFinished(actionEvent -> {//starts the explosion
            group.getChildren().remove(this.hitbox.getShape());
            room.explosionDamageAroundPoint((float) (this.position.x+(this.bomb.getBoundsInParent().getWidth()/2)), (float) (this.position.y+(this.bomb.getBoundsInParent().getHeight()/2)),200,group);
            this.position.sub((int) ((Effects.explodeAnimation[0].getWidth() / 2) - (this.bomb.getBoundsInParent().getWidth() / 2)), (int) ((Effects.explodeAnimation[0].getHeight() / 2) - (this.bomb.getBoundsInParent().getHeight() / 2)));
            subExplosion();
            this.bomb.relocate(this.position.x, this.position.y);
            this.explosionTimeline.play();
            //

        });
        this.explosionTimeline.setOnFinished(actionEvent -> {
            deleteObject(group, bombs);
            //System.out.println("bomb removed");
        });


        //at end of timelines when bomb detonates call back to damage to damage/ destroy stuff
    }

    private void deleteObject(Group group, ArrayList<Active_Bomb> bombs) {
        group.getChildren().remove(this.bomb);
        try {
            group.getChildren().remove(this.hitbox.getShape());
        } catch (Exception e) {
        }
        bombs.remove(this);
    }

    public void unload(Group group, ArrayList<Active_Bomb> bombs) {
        //
        group.getChildren().remove(this.bomb);
        try {
            group.getChildren().remove(this.hitbox.getShape());
        } catch (Exception e) {
        }
        //STOP TIMELINES
        pulseTimeline.stop();
        activeTimeline.stop();
        activePointer = 0;
        if (explosionTimeline.getStatus() == Animation.Status.RUNNING) {
            deleteObject(group, bombs);
        }
    }


}