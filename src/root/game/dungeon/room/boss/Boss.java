package root.game.dungeon.room.boss;

import com.google.gson.JsonObject;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.Tear.Tear;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

import java.util.Random;

public abstract class Boss implements Sprite_Splitter {
    
    Group parentGroup;
    Room parentRoom;
    Random random=new Random();
    //
    String bossName;
    String bossType;
    String filepath;
    int maxHealth,health;
    float sheetScale;
    float scaleX,scaleY;
    int damage;
    Timeline mainline;
    Timeline attack1;
    int attack1Cycle=0;
    Timeline attack2;
    int attack2Cycle=0;

    public Boss(JsonObject bossTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        this.parentRoom=parentRoom;
        System.out.println("Boss created: " + bossTemplate.get("boss").getAsString());
        this.bossName=bossTemplate.get("boss").getAsString();
        this.bossType=bossTemplate.get("type").getAsString();
        this.filepath=bossTemplate.get("filePath").getAsString();
        this.maxHealth=bossTemplate.get("Health").getAsInt();
        this.health=maxHealth;
        this.sheetScale=bossTemplate.get("SheetScale").getAsFloat();
        this.scaleX=scaleX;
        this.scaleY=scaleY;
        this.damage=bossTemplate.get("damage").getAsInt();

    }

    public void load(Group group){
        this.parentGroup=group;
        postLoader(group);
    }

    protected abstract void postLoader(Group group);

    public void unload(Group group){
        postUnLoader(group);
    }

    protected abstract void postUnLoader(Group group);

    public abstract  boolean collidesWith(Tear tear);
}