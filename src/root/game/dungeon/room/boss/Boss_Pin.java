package root.game.dungeon.room.boss;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Vecc2f;

public class Boss_Pin extends Boss {

    public Boss_Pin(JsonObject bossTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super(bossTemplate, pos, scaleX, scaleY, screenBounds, shading, parentRoom);

    }



    @Override
    public void postLoader(Group group) {

    }

    @Override
    public void postUnLoader(Group group) {

    }
}