package root.game.dungeon.room;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import root.game.util.Hitbox;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.Random;

public class Rock implements Sprite_Splitter {

    ImageView rock;
    Image debris;
    Vecc2f position;
    Vecc2f centerPos;
    int sheetScale, width, height, rows, columns, borderX, borderY;
    Hitbox hitbox;
    Random random = new Random();

    //boolean intact;

    enum State {
        Intact,
        Destroyed
    }

    State state;

    public Rock(int positionX, int positionY, int imageX, int imageY, String name, float sheetScale, int width, int height, int rows, int columns, int borderX, int borderY, float scaleX, float scaleY) {
        String file = "file:src\\resources\\gfx\\grid\\" + name + ".png";
        this.state = State.Intact;

        this.width = (int) ((width * sheetScale * scaleX));
        this.height = (int) ((height * sheetScale * scaleY));
        this.rows = rows;
        this.columns = columns;
        this.borderX = (int) (borderX * scaleX);
        this.borderY = (int) (borderY * scaleY);

        this.hitbox = new Hitbox("Rectangle", (int) Math.ceil(((width * 0.8))), (int) Math.ceil(((height * 0.8))), sheetScale, scaleX, scaleY, (int) Math.ceil((width * 0.1)), (int) Math.ceil((height * 0.1)));

        //System.out.println((new Image(file).getWidth() * scaleX * sheetScale) +" " + (new Image(file).getHeight() * scaleY * sheetScale));

        this.rock = new ImageView((imageGetter(file, width * imageX, height * imageY, width, height, scaleX, scaleY, sheetScale)));
        this.debris = ((imageGetter(file, width * 3, 0, width, height, scaleX, scaleY, sheetScale)));

        this.position = new Vecc2f(this.borderX + (positionX * scaleX), this.borderY + (positionY * scaleY));
        //
        this.centerPos = new Vecc2f(this.position.x + this.rock.getBoundsInParent().getWidth() / 2, this.position.y + this.rock.getBoundsInParent().getHeight() / 2);
    }

    public void load(Group group) {
        switch (state) {
            case Intact:
                group.getChildren().addAll(this.hitbox.getShape(), this.rock);
                this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
                this.hitbox.getShape().setViewOrder(ViewOrder.props_layer.getViewOrder());
                this.hitbox.getShape().setVisible(false);
                break;
            case Destroyed:
                group.getChildren().addAll(this.rock);
                break;
        }
        this.rock.relocate(this.position.x, this.position.y);
        this.rock.setViewOrder(ViewOrder.props_layer.getViewOrder());
        //
        //
        this.centerPos.set((this.hitbox.getCenterX()), (this.hitbox.getCenterY()));

    }

    public void unload(Group group) {
        try {
            group.getChildren().remove(this.hitbox.getShape());
        } catch (Exception e) {
        }
        group.getChildren().removeAll(this.rock);
    }

    public void blowUp(Group group) {
        setState(Rock.State.Destroyed);
        this.rock.setRotate(90 * random.nextInt(4));
        try {
            group.getChildren().remove(this.hitbox.getShape());
        } catch (Exception e) {
        }
        this.rock.setImage(this.debris);


    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Vecc2f getCenterPos() {
        return centerPos;
    }
}