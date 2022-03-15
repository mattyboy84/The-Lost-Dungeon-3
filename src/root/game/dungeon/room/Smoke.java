package root.game.dungeon.room;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.NamedArg;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import root.game.util.Effects;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;

public class Smoke {

    ImageView smoke;
    Vecc2f position;
    Timeline timeline;
    int imagePointer = 1;
    ArrayList<Smoke> parentSmoke;
    float scaleX, scaleY;

    public Smoke(@NamedArg("CENTER POSITION") Vecc2f centerPos, Group group, ArrayList<Smoke> smokeClouds, float scaleX, float scaleY, Room room) {
        this.parentSmoke = smokeClouds;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.smoke = new ImageView(Effects.poof[0]);
        this.position = new Vecc2f(centerPos);
        this.position.sub(Effects.poof[0].getWidth() / 2, Effects.poof[0].getHeight() / 2);
        //
        timelineSetup(group);
        load(group);
    }

    private void timelineSetup(Group group) {
        timeline = new Timeline(new KeyFrame(Duration.millis(120), event -> {
            if (imagePointer == 4) {
                this.position.sub(0, 8 * this.scaleY);
            }
            if (imagePointer == 5) {
                this.position.add(0, 8 * this.scaleY);
            }
            this.smoke.relocate(this.position.x, this.position.y);
            this.smoke.setImage(Effects.poof[imagePointer]);
            imagePointer++;
        }));
        timeline.setCycleCount(Effects.poof.length - 1);
        timeline.setOnFinished(event -> unload(group));
    }

    public void load(Group group) {
        group.getChildren().add(this.smoke);
        this.smoke.relocate(this.position.x, this.position.y);
        this.smoke.setOpacity(0.5);
        this.smoke.setViewOrder(ViewOrder.foreground_entities_layer.getViewOrder());
        this.timeline.play();
    }

    public void unload(Group group) {
        group.getChildren().remove(this.smoke);
        this.timeline.stop();
        destroy();
    }

    public void destroy() {
        this.parentSmoke.remove(this);
    }
}