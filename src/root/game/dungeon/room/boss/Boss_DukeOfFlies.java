package root.game.dungeon.room.boss;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import root.game.Tear.Tear;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Vecc2f;

import java.util.ArrayList;

public class Boss_DukeOfFlies extends Boss {
    public Boss_DukeOfFlies(JsonObject bossTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room room) {
        super(bossTemplate, pos, scaleX, scaleY, screenBounds, shading, room);
        //
        this.sheetScale = bossTemplate.get("SheetScale").getAsInt();
        healthBarSetup();
        //
        setVeloLimit(bossTemplate.get("velocity").getAsFloat());
        //
        this.position = new Vecc2f(this.startingTemplatePosition);



    }

    @Override
    protected void bossSpecificMovement() {

    }

    @Override
    protected void updateCenterPos() {

    }

    @Override
    public void checkBoundaries() {

    }

    @Override
    protected void checkForPlayer() {

    }

    @Override
    protected void velocityLimit() {

    }

    @Override
    public void applyForce(Vecc2f dir, float magnitude) {

    }

    @Override
    protected void inflictDamageSub(int damage, Group group, ArrayList<Boss> bosses) {

    }

    @Override
    public boolean collidesWith(Tear tear) {
        return false;
    }

    @Override
    protected void postLoader(Group group) {

    }

    @Override
    protected void postUnLoader(Group group) {

    }

}
