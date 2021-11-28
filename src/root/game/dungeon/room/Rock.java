package root.game.dungeon.room;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import root.game.util.Hitbox;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

public class Rock implements Sprite_Splitter {

    ImageView rock;
    Vecc2f position;
    Vecc2f centerPos;
    int sheetScale, width, height, rows, columns, borderX, borderY;
    Hitbox hitbox;
    boolean intact;

    public Rock(int positionX, int positionY, int imageX, int imageY, String name, float sheetScale, int width, int height, int rows, int columns, int borderX, int borderY, float scaleX, float scaleY) {
        String file = "file:src\\resources\\gfx\\grid\\" + name + ".png";
        this.intact = true;

        this.width = (int) ((width * sheetScale * scaleX));
        this.height = (int) ((height * sheetScale * scaleY));
        this.rows = rows;
        this.columns = columns;
        this.borderX = (int) (borderX * scaleX);
        this.borderY = (int) (borderY * scaleY);

        this.hitbox = new Hitbox("Rectangle", (int) Math.ceil(((width * 0.8))), (int) Math.ceil(((height * 0.8))), sheetScale, scaleX, scaleY, (int) Math.ceil((width * 0.1)), (int) Math.ceil((height * 0.1)));

        //System.out.println((new Image(file).getWidth() * scaleX * sheetScale) +" " + (new Image(file).getHeight() * scaleY * sheetScale));

        this.rock = new ImageView((imageGetter(file, width * imageX, height * imageY, width, height, scaleX, scaleY, sheetScale)));
        this.position = new Vecc2f(this.borderX + (positionX * scaleX), this.borderY + (positionY * scaleY));
        //
        this.centerPos = new Vecc2f(this.position.x + this.rock.getBoundsInParent().getWidth() / 2, this.position.y + this.rock.getBoundsInParent().getHeight() / 2);
    }

    public void load(Group group) {
        group.getChildren().addAll(this.hitbox.getShape(), this.rock);
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
        this.rock.relocate(this.position.x, this.position.y);
        this.hitbox.getShape().setViewOrder(-1);
        this.rock.setViewOrder(-1);
        //
        this.hitbox.getShape().setVisible(true);
        //
        this.centerPos.set(this.position.x, this.position.y);

    }

    public void unload(Group group) {
        group.getChildren().removeAll(this.hitbox.getShape(), this.rock);
    }


    public Vecc2f getCenterPos() {
        return centerPos;
    }

    public void setIntact(boolean intact) {
        this.intact = intact;
    }
}