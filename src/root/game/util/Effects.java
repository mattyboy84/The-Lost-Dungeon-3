package root.game.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.image.Image;

import static root.Main.scaleX;
import static root.Main.scaleY;

public class Effects implements Runnable, Sprite_Splitter {

    private Thread t;
    final String threadName="Effects";
    //effects
    public static Image[] explodeAnimation;

    @Override
    public void run() {


        explodeAnimationSetup("file:src\\resources\\gfx\\effects\\effect_029_explosion.png", scaleX, scaleY, 2);




    }

    private void explodeAnimationSetup(String file, float scaleX, float scaleY, float sheetScale) {
        int x = 0, y = 0;
        int width = 96, height = 96;
        explodeAnimation = new Image[12];
        for (int i = 0; i < explodeAnimation.length-1; i++) {
            explodeAnimation[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
            x+=width;
            if (x>=4*width){
                x=0;
                y+=height;
            }
        }
    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
