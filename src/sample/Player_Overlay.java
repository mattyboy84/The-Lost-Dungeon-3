package sample;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Player_Overlay {

    String file = "file:src\\resources\\gfx\\ui\\hudpickups.png";

    ImageView icon_coin, icon_bomb, icon_key;
    Vecc2f posCoin,posKey,posBomb;
    Text txtCoin,txtKey,txtBomb;

    public Player_Overlay(float scaleX, float scaleY, Rectangle2D screenBounds, int sheetScale) {
        float g = ((scaleX+scaleY)/2);

        this.icon_coin = imageGetter(scaleX, scaleY, sheetScale, 0, 0);
        this.icon_key = imageGetter(scaleX, scaleY, sheetScale, 0, 16);
        this.icon_bomb = imageGetter(scaleX, scaleY, sheetScale, 16, 0);
        //
        this.posCoin=new Vecc2f(75*scaleX,150*scaleY);
        this.posBomb=new Vecc2f(this.posCoin.x, this.posCoin.y + this.icon_coin.getBoundsInParent().getWidth());
        this.posKey=new Vecc2f(this.posCoin.x, this.posBomb.y + this.icon_bomb.getBoundsInParent().getWidth());
        //
        this.txtCoin=new Text("00");
        this.txtBomb=new Text("00");
        this.txtKey=new Text("00");
        //
        this.txtCoin.setFont(Font.font("Upheaval", FontWeight.BOLD, 35*g));
        this.txtBomb.setFont(Font.font("Upheaval", FontWeight.BOLD, 35*g));
        this.txtKey.setFont(Font.font("Upheaval", FontWeight.BOLD, 35*g));

    }
    /*
             String padded = String.format("%01d" , number);//TODO will add 1 leading zero,
             item counter will be show 00 with a max of 99. If it is less than 10 it will be padded with a leading zero
     */

    private ImageView imageGetter(float scaleX, float scaleY, int sheetScale, int startX, int startY) {
        return (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX * sheetScale), (new Image(file).getHeight() * scaleY * sheetScale),
                false, false).getPixelReader(), (int) (startX * scaleX * sheetScale),
                (int) (startY * scaleY * sheetScale), (int) (16 * scaleX * sheetScale), (int) (16 * scaleY * sheetScale))));
    }

    public void load(Group group) {
        group.getChildren().addAll(this.icon_bomb,this.icon_coin,this.icon_key,this.txtCoin,this.txtKey,this.txtBomb);
        //
        this.icon_coin.relocate(posCoin.x, posCoin.y);
        this.icon_bomb.relocate(posBomb.x, posBomb.y);
        this.icon_key.relocate(posKey.x, posKey.y);
        //
        this.icon_coin.setViewOrder(-11);
        this.icon_bomb.setViewOrder(-11);
        this.icon_key.setViewOrder(-11);
        //
        this.txtCoin.relocate(this.posCoin.x+(this.icon_coin.getBoundsInParent().getWidth()),this.posCoin.y);
        this.txtBomb.relocate(this.posBomb.x+(this.icon_bomb.getBoundsInParent().getWidth()),this.posBomb.y);
        this.txtKey.relocate(this.posKey.x+(this.icon_key.getBoundsInParent().getWidth()),this.posKey.y);
        //
        this.txtCoin.setViewOrder(-11);
        this.txtBomb.setViewOrder(-11);
        this.txtKey.setViewOrder(-11);
    }
}