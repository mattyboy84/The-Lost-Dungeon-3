package sample;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Shape;

import java.util.ArrayList;

public class Enemy {

    JsonObject template;
    //
    String name;
    String type, filePath;
    int maxHealth, health;
    int startX, startY;
    int width, height;
    int rows, columns;
    int numImages;
    ///////////////////////
    ArrayList<Hitbox> hitboxes = new ArrayList<>();
    ///////////////////////
    boolean hasDeathImages;
    String deathFilePath;
    int deathStartX, deathStartY;
    int deathWidth, deathHeight;
    int deathRows, deathColumns;
    int numDeathImages;
    ImageView enemy;
    Image[] images;
    //
    Vecc2f position;
    //
    int sheetScale;


    public Enemy(JsonObject enemyTemplate, float positionX, float positionY, float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.template = enemyTemplate;
        //System.out.println(this.template);
        this.name = enemyTemplate.get("enemy").getAsString();
        this.type = enemyTemplate.get("type").getAsString();
        this.filePath = enemyTemplate.get("filePath").getAsString();
        this.maxHealth = enemyTemplate.get("Health").getAsInt();
        this.health = enemyTemplate.get("Health").getAsInt();
        this.startX = enemyTemplate.get("StartX").getAsInt();
        this.startY = enemyTemplate.get("StartY").getAsInt();
        this.width = enemyTemplate.get("Width").getAsInt();
        this.height = enemyTemplate.get("Height").getAsInt();
        this.rows = enemyTemplate.get("Rows").getAsInt();
        this.columns = enemyTemplate.get("Columns").getAsInt();
        this.numImages = enemyTemplate.get("Images").getAsInt();
        ////////////////

        ////////////////
        this.hasDeathImages = enemyTemplate.get("hasDeathImages").getAsBoolean();
        if (this.hasDeathImages) {
            this.deathFilePath = enemyTemplate.get("DeathFilePath").getAsString();
            this.deathStartX = enemyTemplate.get("DeathStartX").getAsInt();
            this.deathStartY = enemyTemplate.get("DeathStartY").getAsInt();
            this.deathWidth = enemyTemplate.get("DeathWidth").getAsInt();
            this.deathHeight = enemyTemplate.get("DeathHeight").getAsInt();
            this.deathRows = enemyTemplate.get("DeathRows").getAsInt();
            this.deathColumns = enemyTemplate.get("DeathColumns").getAsInt();
            this.numDeathImages = enemyTemplate.get("DeathImages").getAsInt();
        }
        this.position = new Vecc2f((int) (positionX * scaleX), (int) (positionY * scaleY));


        //D:\- JAVA Projects -\- Lost Dungeon -\The-Lost-Dungeon-3\src\resources\gfx\monsters\classic

        String file = "file:src\\resources\\gfx\\monsters\\classic\\" + this.filePath + ".png";

        images = new Image[enemyTemplate.get("Images").getAsInt()];

        //System.out.println(scaleX);
        sheetScale = enemyTemplate.get("SheetScale").getAsInt();
        //
        int topLeftX = (int) (enemyTemplate.get("StartX").getAsInt() * scaleX * sheetScale);
        int topLeftY = (int) (enemyTemplate.get("StartY").getAsInt() * scaleY * sheetScale);
        //
        for (int i = 0; i < images.length; i++) {
            images[i] = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX * sheetScale), (new Image(file).getHeight() * scaleY * sheetScale), false, false).getPixelReader(), (int) topLeftX, (int) topLeftY, (int) (this.width * scaleX * sheetScale), (int) (this.height * scaleY * sheetScale))).getImage());
            //System.out.println(enemyTemplate.get("enemy").getAsString() + " TLX: " + topLeftX +" TLY: " + topLeftY);
            topLeftX = (int) (topLeftX + (enemyTemplate.get("Width").getAsInt() * scaleX * sheetScale));
            if (topLeftX >= (new Image(file).getWidth() * scaleX * sheetScale)) {
                topLeftX = 0;
                topLeftY = (int) (topLeftY + (enemyTemplate.get("Height").getAsInt() * scaleY * sheetScale));
            }
        }
        //
        enemy = new ImageView(images[0]);
        //System.out.println(images[0].getHeight() + " " + images[0].getWidth());

        hitboxGenerator(enemyTemplate.getAsJsonArray("Hitboxes"), sheetScale,scaleX,scaleY);


        //System.out.println(scaleX);
    }

    private void hitboxGenerator(JsonArray hitboxes, int sheetScale, float scaleX, float scaleY) {
        for (int i = 0; i < hitboxes.size(); i++) {
            this.hitboxes.add(new Hitbox(hitboxes.get(i).getAsJsonObject(), sheetScale, scaleX, scaleY));

            //System.out.println(hitboxes.get(i).getAsJsonObject());
        }
    }

    private void hitboxRelocator() {
        for (Hitbox hitbox : hitboxes) {
            hitbox.getShape().relocate((this.position.x + hitbox.getxDelta()), (this.position.y + hitbox.getyDelta()));
        }
    }

    public void load(Group group) {
        //
        for (Hitbox hitbox : this.hitboxes) {
            group.getChildren().add(hitbox.shape);
            hitbox.shape.relocate(this.position.x, this.position.y);
            hitbox.shape.setViewOrder(-5);
            hitbox.shape.setVisible(false);
            //System.out.println("Hitbox: " +hitbox.getShape().getBoundsInParent());
        }
        //
        group.getChildren().add(this.enemy);
        this.enemy.relocate(this.position.x, this.position.y);
        this.enemy.setViewOrder(-5);
        //
        hitboxRelocator();
        //System.out.println("Enemy: " + enemy.getBoundsInParent());

    }


    public void unload(Group group) {
        //
        for (int i = 0; i < hitboxes.size(); i++) {
            group.getChildren().remove(hitboxes.get(0).shape);
        }
        //
        group.getChildren().remove(this.enemy);
    }
    /*
    public String toString() {

        return "woop";
    }
    */

}
