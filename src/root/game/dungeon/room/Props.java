package root.game.dungeon.room;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.Random;

public class Props implements Sprite_Splitter {

    Vecc2f position;
    ImageView prop;
    //
    String name;
    int width, height, rows, columns, borderX, borderY;
    Random random = new Random();
    //

    //    ImageView imageView = new ImageView(new Image("file:src\\default_floor.png", (new Image("file:src\\default_floor.png").getWidth() * scaleX), (new Image("file:src\\default_floor.png").getHeight() * scaleY), true, false));
    public Props(JsonObject props, float scaleX, float scaleY, Rectangle2D screenBounds) {
        //
        this.name = props.get("name").getAsString();
        this.width = props.get("Width").getAsInt();
        this.height = props.get("Height").getAsInt();
        this.rows = props.get("Rows").getAsInt();
        this.columns = props.get("Columns").getAsInt();
        this.borderX = (int) (props.get("BorderX").getAsInt() * scaleX);
        this.borderY = (int) (props.get("BorderY").getAsInt() * scaleY);
        //
        scaleX=scaleX*props.get("SheetScale").getAsInt();
        scaleY=scaleY*props.get("SheetScale").getAsInt();
        //
        int randX = random.nextInt(this.rows);
        int randY = random.nextInt(this.columns);
        //
        String file = "file:src\\resources\\gfx\\grid\\" + this.name + ".png";
        //

        this.prop=new ImageView(imageGetter(file, this.width*randX, this.height*randY, width, height, scaleX, scaleY,1));

        this.position = new Vecc2f((float) (this.borderX + random.nextInt((int) (screenBounds.getWidth() - (2 * this.borderX) - this.prop.getBoundsInParent().getWidth()))), (float) (this.borderY + random.nextInt((int) (screenBounds.getHeight() - (2 * this.borderY) - this.prop.getBoundsInParent().getHeight()))));
        //this.prop.setRotate(90 * (random.nextInt(4)));
    }

    public Props(Image image, float centerX, float centerY, Group group, double opacity) {//real Time props
        this.prop=new ImageView(image);
        this.prop.setOpacity(opacity);
        this.position=new Vecc2f(centerX-(this.prop.getBoundsInParent().getWidth()/2),centerY-(this.prop.getBoundsInParent().getHeight()/2));
        load(group);
    }

    public void load(Group group) {
        group.getChildren().add(this.prop);
        this.prop.setViewOrder(ViewOrder.props_layer.getViewOrder());
        this.prop.relocate(position.x, position.y);
    }

    public void unload(Group group) {
        group.getChildren().remove(this.prop);
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