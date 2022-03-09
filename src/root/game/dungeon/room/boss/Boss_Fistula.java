package root.game.dungeon.room.boss;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import root.game.Tear.Tear;
import root.game.dungeon.Shading;
import root.game.dungeon.room.Room;
import root.game.util.Hitbox;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

public class Boss_Fistula extends Boss {

    Hitbox hitbox;
    ImageView boss;
    Vecc2f position;

    public Boss_Fistula(JsonObject bossTemplate, Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading, Room parentRoom) {
        super();
        this.position = new Vecc2f(pos);
        this.maxHealth = bossTemplate.get("Health").getAsInt();
        this.health = maxHealth;
        this.sheetScale = bossTemplate.get("SheetScale").getAsInt();
        this.gutNumber = bossTemplate.get("GutNumber").getAsInt();
        this.bossType = bossTemplate.get("type").getAsString();
        this.filepath = bossTemplate.get("filePath").getAsString();
        String file = "file:src\\resources\\gfx\\bosses\\" + this.bossType + "\\" + this.filepath + ".png";
        //
        int startX = bossTemplate.get("image").getAsJsonObject().get("startX").getAsInt();
        int startY = bossTemplate.get("image").getAsJsonObject().get("startY").getAsInt();
        int width = bossTemplate.get("image").getAsJsonObject().get("width").getAsInt();
        int height = bossTemplate.get("image").getAsJsonObject().get("height").getAsInt();
        this.boss = new ImageView(imageGetter(file, startX, startY, width, height, scaleX, scaleY, sheetScale));
        //
        this.hitbox = new Hitbox(bossTemplate.get("Hitbox").getAsJsonObject(), (int) sheetScale, scaleX, scaleY);
        //
    }

    @Override
    protected void postLoader(Group group) {
        group.getChildren().addAll(this.boss,this.hitbox.getShape());
        this.boss.relocate(this.position.x, this.position.y);
        this.boss.setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        //
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
        this.hitbox.getShape().setViewOrder(ViewOrder.enemy_boss_layer.getViewOrder());
        this.hitbox.getShape().setVisible(false);


    }

    @Override
    protected void postUnLoader(Group group) {

    }

    @Override
    public boolean collidesWith(Tear tear) {
        return tear.tearHitbox.getShape().getBoundsInParent().intersects(this.hitbox.getShape().getBoundsInParent());

    }
}
