package root.game.dungeon.room.enemy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import root.game.dungeon.Shading;
import root.game.util.*;

import java.util.ArrayList;

public class Enemy_part implements Entity_Shader, Sprite_Splitter {
    String part;
    int startX, startY;
    int width, height;
    int rows, columns;
    int numImages;
    //
    Image[] images;
    ImageView enemy;
    //
    Vecc2f position, centerPos;
    //
    public ArrayList<Hitbox> hitboxes = new ArrayList<>();
    //
    boolean light;
    int lightRadius;
    float[][] shader;
    Shading shading;

    public Enemy_part(JsonObject enemyPart, String type, String filePath, int sheetScale, float scaleX, float scaleY, Shading shading, Vecc2f pos) {
        this.part = enemyPart.get("part").getAsString();
        this.startX = (int) (enemyPart.get("StartX").getAsInt());
        this.startY = (int) (enemyPart.get("StartY").getAsInt());
        this.width = (int) (enemyPart.get("Width").getAsInt());
        this.height = (int) (enemyPart.get("Height").getAsInt());
        this.rows = enemyPart.get("Rows").getAsInt();
        this.columns = enemyPart.get("Columns").getAsInt();
        //
        String file = "file:src\\resources\\gfx\\monsters\\" + type + "\\" + filePath + ".png";
        //System.out.println(file);

        images = new Image[enemyPart.get("Images").getAsInt()];
        //
        int topLeftX = (int) startX;
        int topLeftY = (int) startY;
        //System.out.println(this.height);
        //
        //System.out.println(this.width * scaleX * sheetScale);
        for (int i = 0; i < images.length; i++) {
            images[i]=imageGetter(file,topLeftX,topLeftY,this.width,this.height,scaleX,scaleY,sheetScale);

            topLeftX = (int) (topLeftX + this.width);
            if (topLeftX >= (new Image(file).getWidth() * scaleX * sheetScale)) {
                topLeftX = 0;
                topLeftY = (int) (topLeftY + this.height);
            }
        }
        //
        this.position = new Vecc2f(pos.x*scaleX,pos.y*scaleY);
        //

        hitboxGenerator(enemyPart.getAsJsonArray("Hitboxes").getAsJsonArray(), sheetScale, scaleX, scaleY);

        this.enemy = new ImageView((images[0]));

        this.light = enemyPart.get("Light").getAsBoolean();
        if (this.light) {
            this.lightRadius = (int) (enemyPart.get("Radius").getAsInt()*((scaleX+scaleY)/2));
            //shading.addActiveSource((int) (this.position.x + (this.width / 2) + (hitboxes.get(0).getxDelta())), (int) (this.position.y + (this.height / 2) + (hitboxes.get(0).getyDelta())), this.lightRadius);
            this.shading = shading;
            shader=setupShader(this.lightRadius);
            //shading.addActiveSource(this.position.x,this.position.y,this.lightRadius);
            //System.out.println(hitboxes.get(0).xDelta);

            //System.out.println((this.enemy.getBoundsInLocal().getMinX())+ " " +  (int)(this.enemy.getBoundsInLocal().getMinY()) +" " +  this.lightRadius);

        }
    }

    private void hitboxGenerator(JsonArray hitboxes, int sheetScale, float scaleX, float scaleY) {
        for (int i = 0; i < hitboxes.size(); i++) {
            this.hitboxes.add(new Hitbox(hitboxes.get(i).getAsJsonObject(), sheetScale, scaleX, scaleY));

            //System.out.println(hitboxes.get(i).getAsJsonObject());
        }
    }

    public void hitboxRelocator() {
        for (Hitbox hitbox : hitboxes) {
            hitbox.getShape().relocate((this.position.x + hitbox.getxDelta()), (this.position.y + hitbox.getyDelta()));
        }
    }

    public void load(Group group) {
        //Hitbox
        for (Hitbox hitbox : hitboxes) {
            group.getChildren().add(hitbox.getShape());
            hitbox.getShape().setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
            hitbox.getShape().setVisible(true);
            hitbox.getShape().relocate(this.position.x + hitbox.getxDelta(), this.position.y + hitbox.getyDelta());
        }
        //Enemy
        group.getChildren().add(this.enemy);
        this.enemy.relocate(this.position.x, this.position.y);
        this.enemy.setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        //
        shading.addActiveSource(this.position.x,this.position.y, this.shader,hashCode());
        //shading.shade();
    }

    public void unload(Group group) {
        for (Hitbox hitbox : hitboxes) {
            group.getChildren().remove(hitbox.getShape());
        }
        group.getChildren().remove(this.enemy);
        shading.removeActiveSource(hashCode());
    }
}