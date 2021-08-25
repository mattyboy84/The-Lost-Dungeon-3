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
    ArrayList<Enemy_part> parts = new ArrayList<>();
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


    public Enemy(JsonObject enemyTemplate, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading) {
        this.template = enemyTemplate;
        //System.out.println(this.template);
        this.name = enemyTemplate.get("enemy").getAsString();
        this.type = enemyTemplate.get("type").getAsString();
        this.filePath = enemyTemplate.get("filePath").getAsString();
        this.maxHealth = enemyTemplate.get("Health").getAsInt();
        this.health = maxHealth;
        this.sheetScale = enemyTemplate.get("SheetScale").getAsInt();

        //System.out.println(enemyTemplate);
        for (int i = 0; i < enemyTemplate.get("enemyParts").getAsJsonArray().size(); i++) {
            parts.add(new Enemy_part(enemyTemplate.get("enemyParts").getAsJsonArray().get(i).getAsJsonObject(), this.type, this.filePath, sheetScale, scaleX, scaleY, shading));
        }
        ////////////////

        ////////////////
        this.hasDeathImages = enemyTemplate.get("hasDeathImages").getAsBoolean();
        if (this.hasDeathImages) {
            this.deathFilePath = enemyTemplate.get("DeathFilePath").getAsString();
            this.deathStartX = (int) (enemyTemplate.get("DeathStartX").getAsInt() * sheetScale * scaleX);
            this.deathStartY = (int) (enemyTemplate.get("DeathStartY").getAsInt() * sheetScale * scaleY);
            this.deathWidth = (int) (enemyTemplate.get("DeathWidth").getAsInt() * sheetScale * scaleX);
            this.deathHeight = (int) (enemyTemplate.get("DeathHeight").getAsInt() * sheetScale * scaleY);
            this.deathRows = enemyTemplate.get("DeathRows").getAsInt();
            this.deathColumns = enemyTemplate.get("DeathColumns").getAsInt();
            this.numDeathImages = enemyTemplate.get("DeathImages").getAsInt();
        }
        //this.position = new Vecc2f((int) (positionX * scaleX), (int) (positionY * scaleY));

        String file = "file:src\\resources\\gfx\\monsters\\" + this.type + "\\" + this.filePath + ".png";

        //D:\- JAVA Projects -\- Lost Dungeon -\The-Lost-Dungeon-3\src\resources\gfx\monsters\classic

        //System.out.println(scaleX);
    }


    public void load(Group group) {
        //
        for (Enemy_part part : parts) {
            part.load(group);
        }
        //
    }


    public void unload(Group group) {
        //
        for (Enemy_part part : parts) {
            part.unload(group);
        }

        //
    }
    /*
    public String toString() {

        return "woop";
    }
    */

}
