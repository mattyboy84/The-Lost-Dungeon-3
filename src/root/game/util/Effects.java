package root.game.util;

import javafx.scene.image.Image;

import static root.Main.scaleX;
import static root.Main.scaleY;

public class Effects implements Runnable, Sprite_Splitter {

    private Thread t;
    final String threadName = "Effects";
    //effects
    public static Image[] explodeAnimation;
    public static Image[] explosionMarkImages;
    public static Image[] BLUEtearCollideAnimation;
    public static Image[] REDtearCollideAnimation;

    @Override
    public void run() {


        explodeAnimationSetup("file:src\\resources\\gfx\\effects\\effect_029_explosion.png", scaleX, scaleY, 2);
        explosionMarkAnimationSetup("file:src\\resources\\gfx\\effects\\effect_017_bombradius.png", scaleX, scaleY, 2);
        BLUEtearCollideAnimation=tearCollideAnimationSetup("file:src\\resources\\gfx\\effects\\effect_015_tearpoof.png", scaleX, scaleY, 2);
        REDtearCollideAnimation=tearCollideAnimationSetup("file:src\\resources\\gfx\\effects\\effect_003_bloodtear.png", scaleX, scaleY, 2);
    }

    private Image[] tearCollideAnimationSetup(String s, float scaleX, float scaleY, int sheetScale) {
        Image[] a = new Image[16];
        int x=0,y=0;
        int width = 64, height = 64;
        for (int i = 0; i < a.length - 1; i++) {
            a[i] = imageGetter(s, x, y, width, height, scaleX, scaleY, sheetScale);
            x += width;
            if (x >= (4 * width)) {
                x = 0;
                y += height;
            }
        }
        return a;
    }

    private void explosionMarkAnimationSetup(String s, float scaleX, float scaleY, int sheetScale) {
        int x = 0, y = 0;
        int width = 96, height = 64;
        explosionMarkImages = new Image[8];
        for (int i = 0; i < explosionMarkImages.length - 1; i++) {
            explosionMarkImages[i] = imageGetter(s, x, y, width, height, scaleX, scaleY, sheetScale);
            x += width;
            if (x >= (2 * width)) {
                x = 0;
                y += height;
            }
        }
    }

    private void explodeAnimationSetup(String file, float scaleX, float scaleY, float sheetScale) {
        int x = 0, y = 0;
        int width = 96, height = 96;
        explodeAnimation = new Image[12];
        for (int i = 0; i < explodeAnimation.length - 1; i++) {
            explodeAnimation[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
            x += width;
            if (x >= 4 * width) {
                x = 0;
                y += height;
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
