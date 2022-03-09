package root.game.dungeon.room.boss;

import com.google.gson.JsonObject;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.Tear.Tear;
import root.game.player.Player;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

import java.util.Random;

public abstract class Boss implements Sprite_Splitter {
    
    Group parentGroup;
    Room parentRoom;
    Player playerTarget;
    Random random=new Random();
    //
    int gutNumber;
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

    public Boss() {

    }

    public void load(Group group, Player player){
        this.playerTarget=player;
        this.parentGroup=group;
        postLoader(group);
    }

    public void unload(Group group){
        postUnLoader(group);
    }


    protected abstract void postLoader(Group group);

    protected abstract void postUnLoader(Group group);

    public abstract  boolean collidesWith(Tear tear);
}