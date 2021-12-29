package root.game.util;

import com.google.gson.JsonObject;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Hitbox {

    public String type;
    public int width;
    public int height;
    public int radius;
    //
    public int xDelta, yDelta;
    //
    Shape shape;

    public Hitbox(JsonObject jsonObject, int sheetScale, float scaleX, float scaleY) {
        //System.out.println(jsonObject);
        this.type = jsonObject.get("Type").getAsString();
        switch (this.type) {
            case "Rectangle":
                this.width = (int) Math.ceil(jsonObject.get("Width").getAsInt() * sheetScale * scaleX);
                this.height = (int) Math.ceil(jsonObject.get("Height").getAsInt() * sheetScale * scaleY);
                this.shape = new Rectangle(this.width, this.height);
                break;
            case "Circle":
                this.radius = (int) Math.ceil(jsonObject.get("Radius").getAsInt() * sheetScale * ((scaleX + scaleY) / 2));
                this.shape = new Circle(this.radius);
                break;
        }
        this.xDelta = (int) Math.ceil(jsonObject.get("xDelta").getAsInt() * sheetScale * scaleX);
        this.yDelta = (int) Math.ceil(jsonObject.get("yDelta").getAsInt() * sheetScale * scaleY);
    }

    public Hitbox(String shape, int width, int height, float sheetScale, float scaleX, float scaleY, int xDelta, int yDelta) {
        this.type = shape;
        switch (this.type) {
            case "Rectangle":
                this.width = (int) Math.ceil(width * sheetScale * scaleX);
                this.height = (int) Math.ceil(height * sheetScale * scaleY);
                this.shape = new Rectangle(this.width, this.height);
                break;
            case "Circle":
                this.radius = (int) Math.ceil(width * sheetScale * ((scaleX + scaleY) / 2));
                this.shape = new Circle(this.radius);
        }
        this.xDelta = (int) Math.ceil(xDelta * sheetScale * scaleX);
        this.yDelta = (int) Math.ceil(yDelta * sheetScale * scaleX);
    }

    public int getCenterX() {
        return (int) shape.getBoundsInParent().getCenterX();
    }

    public int getCenterY() {
        return (int) (shape.getBoundsInParent().getCenterY());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getxDelta() {
        return xDelta;
    }

    public void setxDelta(int xDelta) {
        this.xDelta = xDelta;
    }

    public int getyDelta() {
        return yDelta;
    }

    public void setyDelta(int yDelta) {
        this.yDelta = yDelta;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
}