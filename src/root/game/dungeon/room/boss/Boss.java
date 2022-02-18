package root.game.dungeon.room.boss;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

public abstract class Boss implements Sprite_Splitter {
    
    Group parentGroup;

    public Boss(JsonObject bossTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        System.out.println(bossTemplate);
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

}
