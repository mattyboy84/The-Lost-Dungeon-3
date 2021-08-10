package sample;

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
    ArrayList<Shape> hitBoxes = new ArrayList<>();
    ///////////////////////
    boolean hasDeathImages;
    String deathFilePath;
    int deathStartX, deathStartY;
    int deathWidth, deathHeight;
    int deathRows, deathColumns;
    int numDeathImages;
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
        this.position = new Vecc2f((int)(positionX * scaleX),(int) (positionY * scaleY));

        /*
    {
      "enemy": "fly",
      "type": "classic",
      "filePath": "monster_010_fly",
      "Health": 6,
      "StartX": 0,
      "StartY": 0,
      "Width": 32,
      "Height": 32,
      "Rows": 2,
      "Columns": 1,
      "Images": 2,
      "Hitboxes": [
        {
          "Type": "Rectangle",
          "Width": 19,
          "Height": 15,
          "xDelta": 6,
          "yDelta": 8
        }
      ],
      "hasDeathImages": "True",
      "DeathFilePath":"src\\resources\\gfx\\monsters\\classic\\monster_010_fly.png",
      "DeathStartX": 0,
      "DeathStartY": 64,
      "DeathWidth": 64,
      "DeathHeight": 64,
      "DeathRows": 4,
      "DeathColumns": 3,
      "DeathImages": 11,
      "PositionX": 100,
      "PositionY": 100
    },
     */

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
            images[i] = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX * sheetScale), (new Image(file).getHeight() * scaleY * sheetScale), false, false).getPixelReader(), (int) topLeftX, (int) topLeftY,(int) (this.width * scaleX * sheetScale), (int) (this.height * scaleY * sheetScale))).getImage());
            //System.out.println(enemyTemplate.get("enemy").getAsString() + " TLX: " + topLeftX +" TLY: " + topLeftY);
            topLeftX = (int) (topLeftX + (enemyTemplate.get("Width").getAsInt() * scaleX * sheetScale));
            if (topLeftX >= (new Image(file).getWidth() * scaleX * sheetScale)) {
                topLeftX=0;
                topLeftY=(int) (topLeftY + (enemyTemplate.get("Height").getAsInt() * scaleY * sheetScale));
            }
        }


        //System.out.println(scaleX);


    }

    public void load(Group group) {


    }

    public void unload(Group group) {
    }
    /*
    public String toString() {

        return "woop";
    }
    */

}
