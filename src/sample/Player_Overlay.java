package sample;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Player_Overlay {

    String file = "file:src\\resources\\gfx\\ui\\hudpickups.png";

    ImageView icon_coin, icon_bomb, icon_key;
    Vecc2f posCoin, posKey, posBomb, posScore, posTime;
    Text txtCoin, txtKey, txtBomb;
    Text txtScore;
    Text txtTime;
    int hour = 0, minute = 0, second = 0;
    ArrayList<Heart> hearts = new ArrayList<Heart>();
    //
    boolean halfHeart = false;

    public Player_Overlay(float scaleX, float scaleY, Rectangle2D screenBounds, int sheetScale, int score) {
        float g = ((scaleX + scaleY) / 2);

        this.icon_coin = imageGetter(scaleX, scaleY, sheetScale, 0, 0);
        this.icon_bomb = imageGetter(scaleX, scaleY, sheetScale, 0, 16);
        this.icon_key = imageGetter(scaleX, scaleY, sheetScale, 16, 0);
        //
        this.posCoin = new Vecc2f(75 * scaleX, 150 * scaleY);
        this.posBomb = new Vecc2f(this.posCoin.x, this.posCoin.y + this.icon_coin.getBoundsInParent().getWidth());
        this.posKey = new Vecc2f(this.posBomb.x, this.posBomb.y + this.icon_bomb.getBoundsInParent().getWidth());
        //
        this.txtCoin = new Text("00");
        this.txtBomb = new Text("00");
        this.txtKey = new Text("00");
        //
        this.txtCoin.setOpacity(0.9);
        this.txtBomb.setOpacity(0.9);
        this.txtKey.setOpacity(0.9);
        //
        Font font = Font.loadFont("file:src\\resources\\font\\upheavtt.ttf", 50 * g);
        //
        this.txtCoin.setFont(font);
        this.txtBomb.setFont(font);
        this.txtKey.setFont(font);
        //
        this.txtCoin.setStroke(Color.WHITE);
        this.txtCoin.setStrokeWidth(1.5 * g);
        this.txtBomb.setStroke(Color.WHITE);
        this.txtBomb.setStrokeWidth(1.5 * g);
        this.txtKey.setStroke(Color.WHITE);
        this.txtKey.setStrokeWidth(1.5 * g);
        //
        this.txtScore = new Text("Score: " + score);
        this.txtScore.setFill(Color.WHITE);
        this.txtScore.setFont(font);
        this.posScore = new Vecc2f(((screenBounds.getWidth() / 2)) - ((this.txtScore.getBoundsInParent().getWidth() / 2)), 180 * scaleY);
        this.txtScore.relocate(this.posScore.x, this.posScore.y);
        this.txtScore.setOpacity(0.4);
        //
        this.txtTime = new Text("Time: 00:00:00");
        this.txtTime.setFill(Color.WHITE);
        this.txtTime.setFont(font);
        this.posTime = new Vecc2f(((screenBounds.getWidth() / 2)) - ((this.txtTime.getBoundsInParent().getWidth() / 2)), (this.txtScore.getBoundsInParent().getMaxY()));
        this.txtTime.setOpacity(0.4);

    }

    private ImageView imageGetter(float scaleX, float scaleY, int sheetScale, int startX, int startY) {
        return (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX * sheetScale), (new Image(file).getHeight() * scaleY * sheetScale),
                false, false).getPixelReader(), (int) (startX * scaleX * sheetScale),
                (int) (startY * scaleY * sheetScale), (int) (16 * scaleX * sheetScale), (int) (16 * scaleY * sheetScale))));
    }

    public void load(Group group) {
        group.getChildren().addAll(this.icon_bomb, this.icon_coin, this.icon_key, this.txtCoin, this.txtKey, this.txtBomb, this.txtScore, this.txtTime);
        //
        this.icon_coin.relocate(posCoin.x, posCoin.y);
        this.icon_bomb.relocate(posBomb.x, posBomb.y);
        this.icon_key.relocate(posKey.x, posKey.y);
        //
        this.icon_coin.setViewOrder(-11);
        this.icon_bomb.setViewOrder(-11);
        this.icon_key.setViewOrder(-11);
        //
        this.txtCoin.relocate(this.posCoin.x + (this.icon_coin.getBoundsInParent().getWidth()), this.posCoin.y);
        this.txtBomb.relocate(this.posBomb.x + (this.icon_bomb.getBoundsInParent().getWidth()), this.posBomb.y);
        this.txtKey.relocate(this.posKey.x + (this.icon_key.getBoundsInParent().getWidth()), this.posKey.y);
        this.txtScore.relocate(this.posScore.x, this.posScore.y);
        this.txtTime.relocate(this.posTime.x, this.posTime.y);
        //
        this.txtCoin.setViewOrder(-11);
        this.txtBomb.setViewOrder(-11);
        this.txtKey.setViewOrder(-11);
        this.txtScore.setViewOrder(-11);
        this.txtTime.setViewOrder(-11);
        //
        //this.txtTime.setVisible(false);
        //this.txtScore.setVisible(false);
    }

    public void updateTime() {
        if (hour < 99) {
            StringBuilder result = new StringBuilder();
            second++;
            if (second > 59) {
                minute++;
                second = 0;
            }
            if (minute > 59) {
                hour++;
                minute = 0;
            }

            if (hour < 10) {
                result.append("0");
            }
            result.append(hour).append(":");
            if (minute < 10) {
                result.append("0");
            }
            result.append(minute).append(":");
            if (second < 10) {
                result.append("0");
            }
            result.append(second);
            txtTime.setText("Time: " + result);
        }

    }

    public void updateScore(int score) {
        this.txtScore.setText("Score: " + score);
    }

    public void updateBombNumber(int bombNumber) {
        update(bombNumber, txtBomb);
    }

    public void updateKeyNumber(int keyNumber) {
        update(keyNumber, txtKey);
    }

    public void updateCoinNumber(int coinNumber) {
        update(coinNumber, txtCoin);
    }

    private void update(int ITEM_NUMBER, Text LABEL_TEXT) {
        if (ITEM_NUMBER < 100) {
            if (ITEM_NUMBER < 10) {
                LABEL_TEXT.setText("0" + ITEM_NUMBER);
            } else {
                LABEL_TEXT.setText(String.valueOf(ITEM_NUMBER));
            }
        }
    }

    public void over() {
        this.txtScore.setVisible(!this.txtScore.isVisible());
        this.txtTime.setVisible(!this.txtTime.isVisible());
    }

    public void updateHealth(int health, int total_health, int maximum_health, Group group) {
        halfHeart = false;
        int a = total_health;
        int b = health;
        int diff = a - b;
        for (Heart heart : hearts) {
            try {
                heart.remove(group);
            } catch (Exception e) {

            }
        }
        hearts.clear();

        if (b % 2 == 1) {
            b -= 1;
            halfHeart = true;
        }
        if ((diff % 2) == 1) {
            diff = diff - 1;
        }
        for (int i = 0; i < b / 2; i++) {
            hearts.add(new Heart(hearts.size(), 2, maximum_health));
        }
        if (halfHeart) {
            hearts.add(new Heart(hearts.size(), 1, maximum_health));
        }
        for (int i = 0; i < diff / 2; i++) {
            hearts.add(new Heart(hearts.size(), 0, maximum_health));
        }
        for (Heart heart : hearts) {
            heart.load(group);
        }
    }

    private class Heart {
        Vecc2f position;
        ImageView heart;
        String file = "file:src\\resources\\gfx\\ui\\ui_hearts.png";
        int sheetScale = Main.p ? 3 : 4;
        int width_heart = (int) ((16 * sheetScale) * 0.8);
        //
        ImageView heart_FULL = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * sheetScale), (new Image(file).getHeight() * sheetScale),
                false, true).getPixelReader(), (int) (0 * sheetScale),
                (int) (0 * sheetScale), (int) (16 * sheetScale), (int) (16 * sheetScale))));
        //
        ImageView heart_HALF = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * sheetScale), (new Image(file).getHeight() * sheetScale),
                false, true).getPixelReader(), (int) (16 * sheetScale),
                (int) (0 * sheetScale), (int) (16 * sheetScale), (int) (16 * sheetScale))));
        //
        ImageView heart_EMPTY = (new ImageView(new WritableImage(new Image(file, Math.round(new Image(file).getWidth() * sheetScale), (new Image(file).getHeight() * sheetScale),
                false, true).getPixelReader(), (int) (32 * sheetScale),
                (int) (0 * sheetScale), (int) (16 * sheetScale), (int) (16 * sheetScale))));

        public Heart(int size, int a, int MAX) {
            switch (a) {
                case 2://full heart
                    this.heart = heart_FULL;
                    break;
                case 1://half heart
                    this.heart = heart_HALF;
                    break;
                case 0:
                    this.heart = heart_EMPTY;
                    break;
            }


            this.position = new Vecc2f((size >= (MAX / 4)) ? (size-(int)(MAX/4))*width_heart : size*width_heart, (size >= (MAX / 4)) ? width_heart : 0);
        }

        public void remove(Group group) {
            group.getChildren().remove(this.heart);
        }

        public void load(Group group) {
            group.getChildren().add(this.heart);
            this.heart.relocate(this.position.x, this.position.y);
            this.heart.setViewOrder(-11);
        }
    }
}