package root.game.dungeon.room;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.Random;

public class Props implements Sprite_Splitter {

    Vecc2f position;
    ImageView prop;
    //
    String name;
    int width, height, borderX, borderY;
    Random random = new Random();
    boolean animated;
    Image[] animatedFrames;
    Timeline timeline;
    int animateCounter = 1;
    //

    //    ImageView imageView = new ImageView(new Image("file:src\\default_floor.png", (new Image("file:src\\default_floor.png").getWidth() * scaleX), (new Image("file:src\\default_floor.png").getHeight() * scaleY), true, false));
    public Props(String name, JsonObject props, float scaleX, float scaleY, Rectangle2D screenBounds, int borderX, int borderY) {
        //
        this.name = name;
        this.width = props.get("Width").getAsInt();
        this.height = props.get("Height").getAsInt();
        this.borderX = borderX;
        this.borderY = borderY;
        int sheetScale = 3;
        //
        int x;
        int y;

        JsonArray data = props.get("data").getAsJsonArray();
        int randProp = random.nextInt(data.size());

        try {//single image
            x = data.get(randProp).getAsJsonObject().get("x").getAsInt();
            y = data.get(randProp).getAsJsonObject().get("y").getAsInt();
            animated = false;
        } catch (Exception e) {//animated image
            animated = true;
            JsonArray animatedProp = data.get(randProp).getAsJsonArray();
            prepareAnimatedProp(animatedProp, scaleX, scaleY, sheetScale);
            //
            x = animatedProp.get(0).getAsJsonObject().get("x").getAsInt();
            y = animatedProp.get(0).getAsJsonObject().get("y").getAsInt();
        }
        //
        String file = "file:src\\resources\\gfx\\grid\\" + this.name + ".png";
        //

        this.prop = new ImageView(imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale));

        this.position = new Vecc2f((float) (this.borderX + random.nextInt((int) (screenBounds.getWidth() - (2 * this.borderX) - this.prop.getBoundsInParent().getWidth()))), (float) (this.borderY + random.nextInt((int) (screenBounds.getHeight() - (2 * this.borderY) - this.prop.getBoundsInParent().getHeight()))));
    }

    private void prepareAnimatedProp(JsonArray animatedProp, float scaleX, float scaleY, int sheetScale) {
        animatedFrames = new Image[animatedProp.size()];
        for (int i = 0; i < animatedFrames.length; i++) {
            animatedFrames[i] = imageGetter("file:src\\resources\\gfx\\grid\\" + this.name + ".png",
                    animatedProp.get(i).getAsJsonObject().get("x").getAsInt(),
                    animatedProp.get(i).getAsJsonObject().get("y").getAsInt(),
                    this.width, this.height, scaleX, scaleY, sheetScale
            );
        }
        //
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            this.prop.setImage(animatedFrames[animateCounter]);
            animateCounter = (animateCounter == animatedFrames.length-1) ? (0) : (++animateCounter);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }


    public Props(Image image, float centerX, float centerY, Group group, double opacity) {//real Time props
        this.prop = new ImageView(image);
        this.prop.setOpacity(opacity);
        this.position = new Vecc2f(centerX - (this.prop.getBoundsInParent().getWidth() / 2), centerY - (this.prop.getBoundsInParent().getHeight() / 2));
        animated = false;
        load(group);
    }

    public void load(Group group) {
        group.getChildren().add(this.prop);
        this.prop.setViewOrder(ViewOrder.props_layer.getViewOrder());
        this.prop.relocate(position.x, position.y);
        if (animated) timeline.play();
    }

    public void unload(Group group) {
        group.getChildren().remove(this.prop);
        if (animated) timeline.pause();
    }

    public Vecc2f getPosition() {
        return position;
    }

    public void setPosition(Vecc2f position) {
        this.position = position;
    }

    public ImageView getProp() {
        return prop;
    }

    public void setProp(ImageView prop) {
        this.prop = prop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
}