package root.game.dungeon.room;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import root.game.util.Hitbox;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

public class Rock_Part implements Sprite_Splitter {

    Vecc2f position;
    public Vecc2f centerPos;
    ImageView part;
    Hitbox hitbox;
    boolean single=true;
    boolean markedDelete;

    public Rock_Part(String name, int positionX, int positionY, int width, int height, float sheetScale, float scaleX, float scaleY, int i, int j, int borderX, int borderY,int startX,int startY) {
        this.position = new Vecc2f((positionX + (103 * scaleX * j)), (positionY + (103 *scaleY* i)));
        this.position.add((borderX*scaleX), (borderY*scaleY));
        this.part = new ImageView(imageGetter("file:src\\resources\\gfx\\grid\\" + name + ".png", startX, startY, width, height, scaleX, scaleY, sheetScale));
        this.hitbox = new Hitbox("Rectangle", (int) Math.ceil(((width * 0.8))), (int) Math.ceil(((height * 0.8))), sheetScale, scaleX, scaleY, (int) Math.ceil((width * 0.1)), (int) Math.ceil((height * 0.1)));
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
        this.centerPos=new Vecc2f((this.hitbox.getCenterX()), (this.hitbox.getCenterY()));
    }

    public void load(Group group) {
        group.getChildren().addAll(this.hitbox.getShape(),this.part);
        //this.hitbox.getShape().setVisible(true);
        //this.hitbox.getShape().toFront();
        //
        this.part.relocate(this.position.x, this.position.y);
        this.part.setViewOrder(ViewOrder.props_layer.getViewOrder());
    }

    public void unload(Group group) {
        group.getChildren().removeAll(this.part,this.hitbox.getShape());
    }

    public void setAsSingle(String name, int startX, int startY, float scaleX, float scaleY, float sheetScale) {
        this.part.setImage(imageGetter("file:src\\resources\\gfx\\grid\\" + name + ".png", startX, startY, 32, 32, scaleX, scaleY, sheetScale));

    }
}