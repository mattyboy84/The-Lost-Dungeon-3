package root.game.dungeon.room;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import root.game.util.Hitbox;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;
import root.game.util.ViewOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Rock implements Sprite_Splitter {

    ImageView rock;
    Image debris;
    Vecc2f position;
    Vecc2f centerPos;
    int sheetScale, width, height, borderX, borderY;
    Hitbox hitbox;
    Random random = new Random();
    rock_Part[][] rock_parts;
    String type;

    public Rock(int positionX, int positionY, String type, String name, float sheetScale, int width, int height, int borderX, int borderY, float scaleX, float scaleY) {
        this.type = type;
        rock_parts = new rock_Part[Integer.parseInt(type.substring(0, type.length() - 2))][Integer.parseInt(type.substring(2))];

        switch (this.type){
            case "1x1":
                rock_parts[0][0] = new rock_Part(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, 0, 0, borderX, borderY, random.nextInt(3)*width, 0);
                break;
            case "1x2":
                rock_parts[0][0] = new rock_Part(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, 0, 0, borderX, borderY, 0, (7*width));
                rock_parts[0][1] = new rock_Part(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, 0, 1, borderX, borderY, width, (7*width));
                break;
            case "2x1":
                rock_parts[0][0] = new rock_Part(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, 0, 0, borderX, borderY, 0, (5*width));
                rock_parts[1][0] = new rock_Part(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, 1, 0, borderX, borderY, 0, (6*width));
                break;
            case "2x2":
                rock_parts[0][0] = new rock_Part(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, 0, 0, borderX, borderY, 0, (3*width));
                rock_parts[1][0] = new rock_Part(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, 1, 0, borderX, borderY, 0, (4*width));
                rock_parts[0][1] = new rock_Part(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, 0, 1, borderX, borderY, (1*width), (3*width));
                rock_parts[1][1] = new rock_Part(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, 1, 1, borderX, borderY, (1*width), (4*width));
                break;
        }
        //for (int i = 0; i < rock_parts.length; i++) {
        //    for (int j = 0; j < rock_parts[0].length; j++) {
        //    }
        //}

    }

    public void load(Group group) {
        /*
        this.rock.relocate(this.position.x, this.position.y);
        this.rock.setViewOrder(ViewOrder.props_layer.getViewOrder());
         */
        //
        for (int i = 0; i < rock_parts.length; i++) {
            for (int j = 0; j < rock_parts[0].length; j++) {
                rock_parts[i][j].load(group);
            }
        }
    }

    public void unload(Group group) {
        for (int i = 0; i < rock_parts.length; i++) {
            for (int j = 0; j < rock_parts[0].length; j++) {
                rock_parts[i][j].unload(group);
            }
        }
        //group.getChildren().remove(this.hitbox.getShape());
        //group.getChildren().removeAll(this.rock);
    }

    public void blowUp(Group group) {
        /*
        setState(Rock.State.Destroyed);
        this.rock.setRotate(90 * random.nextInt(4));
        try {
            group.getChildren().remove(this.hitbox.getShape());
        } catch (Exception e) {
        }
        this.rock.setImage(this.debris);
*/
    }

    public ArrayList<rock_Part> getRock_parts(){
        ArrayList<rock_Part> parts=new ArrayList<>();
        for (int i = 0; i < rock_parts.length; i++) {
            for (int j = 0; j < rock_parts[0].length; j++) {
                parts.add(rock_parts[i][j]);
            }
        }
        return parts;
    }

    public Vecc2f getCenterPos() {
        return centerPos;
    }

    public Collection<? extends Rectangle> getBoundaries() {
        ArrayList arrayList=new ArrayList();
        for (rock_Part[] rock_part : rock_parts) {
            for (int j = 0; j < rock_parts[0].length; j++) {
                arrayList.add(rock_part[j].hitbox.getShape());
            }
        }
        return arrayList;
    }

    private class rock_Part implements Sprite_Splitter {

        Vecc2f position;
        public Vecc2f centerPos;
        ImageView part;
        Hitbox hitbox;

        public rock_Part(String name, int positionX, int positionY, int width, int height, float sheetScale, float scaleX, float scaleY, int i, int j, int borderX, int borderY,int startX,int startY) {
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
    }

}