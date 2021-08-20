package sample;

import com.google.gson.JsonObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class Enemy_part {
    String part;
    int startX, startY;
    int width, height;
    int rows, columns;
    int numImages;
    //
    Image[] images;


    public Enemy_part(JsonObject enemyParts, String type, String filePath, int sheetScale, float scaleX, float scaleY) {
        this.part=enemyParts.get("part").getAsString();
        this.startX=enemyParts.get("StartX").getAsInt();
        this.startY=enemyParts.get("StartY").getAsInt();
        this.width=enemyParts.get("Width").getAsInt();
        this.height=enemyParts.get("Height").getAsInt();
        this.rows=enemyParts.get("Rows").getAsInt();
        this.columns=enemyParts.get("Columns").getAsInt();
        //
        String file = "file:src\\resources\\gfx\\monsters\\" +type + "\\" +filePath + ".png";

        images = new Image[enemyParts.get("Images").getAsInt()];
        //
        int topLeftX = (int) (enemyParts.get("StartX").getAsInt() * scaleX * sheetScale);
        int topLeftY = (int) (enemyParts.get("StartY").getAsInt() * scaleY * sheetScale);
        //
        for (int i = 0; i < images.length; i++) {
            images[i] = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX * sheetScale), (new Image(file).getHeight() * scaleY * sheetScale), false, false).getPixelReader(), (int) topLeftX, (int) topLeftY, (int) (this.width * scaleX * sheetScale), (int) (this.height * scaleY * sheetScale))).getImage());
            topLeftX = (int) (topLeftX + (enemyParts.get("Width").getAsInt() * scaleX * sheetScale));
            if (topLeftX >= (new Image(file).getWidth() * scaleX * sheetScale)) {
                topLeftX = 0;
                topLeftY = (int) (topLeftY + (enemyParts.get("Height").getAsInt() * scaleY * sheetScale));
            }
        }


    }
}
