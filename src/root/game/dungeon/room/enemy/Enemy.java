package root.game.dungeon.room.enemy;

import com.google.gson.JsonObject;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import root.game.dungeon.Shading;
import root.game.util.Entity_Shader;
import root.game.util.Hitbox;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

import java.util.ArrayList;

public class Enemy implements Sprite_Splitter, Entity_Shader {

    Vecc2f position;
    Vecc2f velocity=new Vecc2f();
    float[][] shader;
    String name;
    String type;
    String filePath;
    int health, maxHealth;
    int sheetScale;
    Hitbox hitbox;
    ImageView enemy;
    Image[] images;
    int lightRadius;
    Image[] deathImages;
    ArrayList<Rectangle> boundaries;
    Shading roomShading;

    Timeline timeline, deathTimeline;

    public Enemy(JsonObject enemyTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, ArrayList<Rectangle> roomBoundaries) {
        this.position = new Vecc2f(pos);
        this.boundaries = roomBoundaries;
        this.roomShading=shading;

        this.name = enemyTemplate.get("enemy").getAsString();
        this.type = enemyTemplate.get("type").getAsString();
        this.filePath = enemyTemplate.get("filePath").getAsString();

        this.maxHealth = enemyTemplate.get("Health").getAsInt();
        this.health = maxHealth;
        this.sheetScale = enemyTemplate.get("SheetScale").getAsInt();

        this.hitbox = new Hitbox(enemyTemplate.get("Hitbox").getAsJsonObject(), sheetScale, scaleX, scaleY);

        String file = "file:src\\resources\\gfx\\monsters\\" + type + "\\" + filePath + ".png";
        images = new Image[enemyTemplate.get("EnemyImages").getAsJsonObject().get("Images").getAsJsonArray().size()];
        JsonObject object = enemyTemplate.get("EnemyImages").getAsJsonObject();
        int width = object.get("Width").getAsInt();
        int height = object.get("Width").getAsInt();
        for (int i = 0; i < images.length; i++) {
            int x = object.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("x").getAsInt();
            int y = object.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("y").getAsInt();
            images[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
        }
        //
        enemy = new ImageView(images[0]);
        //
        if (enemyTemplate.get("Light").getAsBoolean()) {
            lightRadius = enemyTemplate.get("Radius").getAsInt();
            shader = setupShader(lightRadius);
        }
        //
        if (enemyTemplate.get("hasDeathImages").getAsBoolean()) {
            JsonObject object1 = enemyTemplate.get("DeathImages").getAsJsonObject();
            deathImages = new Image[enemyTemplate.get("DeathImages").getAsJsonObject().get("Images").getAsJsonArray().size()];
            int Dwidth = object1.get("DeathWidth").getAsInt();
            int Dheight = object1.get("DeathHeight").getAsInt();
            for (int i = 0; i < deathImages.length; i++) {
                int x = object1.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("x").getAsInt();
                int y = object1.get("Images").getAsJsonArray().get(i).getAsJsonObject().get("y").getAsInt();
                deathImages[i] = imageGetter("file:" + object1.get("DeathFilePath").getAsString(), x, y, Dwidth, Dheight, scaleX, scaleY, sheetScale);
            }
        }
    }

    public void checkBoundaries() {
        for (Rectangle boundary : this.boundaries) {
            if (boundary.getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent())) {
                this.position.sub(this.velocity);
                this.position.set((int) this.position.x, (int) this.position.y);
                this.velocity.set(0, 0);
                this.enemy.relocate(this.position.x,this.position.y);
                this.hitbox.getShape().relocate(this.position.x+this.hitbox.getxDelta(),this.position.y+this.hitbox.getyDelta());
            }
        }
    }

    public void load(Group group) {

    }

    public void unload(Group group) {

    }
}
