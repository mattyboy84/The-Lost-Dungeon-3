package sample;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.util.Random;

public class Props {

    Vecc2f position;
    ImageView prop;
    //
    String name;
    int width, height, rows, columns, borderX, borderY;
    Random random = new Random();
    //
    int spriteScaleX = 3;
    int spriteScaleY = 3;

    //    ImageView imageView = new ImageView(new Image("file:src\\default_floor.png", (new Image("file:src\\default_floor.png").getWidth() * scaleX), (new Image("file:src\\default_floor.png").getHeight() * scaleY), true, false));
    public Props(JsonObject props, float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.name = props.get("name").getAsString();
        this.width = props.get("Width").getAsInt();
        this.height = props.get("Height").getAsInt();
        this.rows = props.get("Rows").getAsInt();
        this.columns = props.get("Columns").getAsInt();
        this.borderX = (int) (props.get("BorderX").getAsInt() * scaleX);
        this.borderY = (int) (props.get("BorderY").getAsInt() * scaleY);
        //
        int randX = random.nextInt(this.rows);
        int randY = random.nextInt(this.columns);
        //
        String file = "file:src\\resources\\gfx\\grid\\" + this.name + ".png";
        //
        this.prop = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX * spriteScaleX), (new Image(file).getHeight() * scaleY * spriteScaleY), false, false).getPixelReader(), (int) (this.width * randX * scaleX * spriteScaleX), (int) (this.height * randY * scaleY * spriteScaleY), (int) (this.height * scaleX * spriteScaleX), (int) (this.width * scaleY * spriteScaleY))));
        this.position = new Vecc2f((float) (this.borderX + random.nextInt((int) (screenBounds.getWidth() - (2 * this.borderX) - this.prop.getBoundsInParent().getWidth()))), (float) (this.borderY + random.nextInt((int) (screenBounds.getHeight() - (2 * this.borderY) - this.prop.getBoundsInParent().getHeight()))));
        //this.prop.setRotate(90 * (random.nextInt(4)));
    }

    public void load(Group group) {
        group.getChildren().add(this.prop);
        this.prop.setViewOrder(-1);
        this.prop.relocate(position.x, position.y);
        //System.out.println(this.position);
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

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getBorderX() {
        return borderX;
    }

    public void setBorderX(int borderX) {
        this.borderX = borderX;
    }

    public int getBorderY() {
        return borderY;
    }

    public void setBorderY(int borderY) {
        this.borderY = borderY;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
}