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
    public static Image[] bloodPool_small;
    public static Image[] bloodPool_medium;
    public static Image[] bloodPool_large;
    public static Image[] enemyGuts;
    public static Image[] tiny_enemyGuts;
    public static Image[] poof;

    @Override
    public void run() {
        explodeAnimation = perepareEffect("file:src\\resources\\gfx\\effects\\effect_029_explosion.png", scaleX, scaleY, 2, 0, 0, 96, 96, 12, 384);
        explosionMarkImages = perepareEffect("file:src\\resources\\gfx\\effects\\effect_017_bombradius.png", scaleX, scaleY, 2, 0, 0, 96, 64, 8, 192);
        BLUEtearCollideAnimation = perepareEffect("file:src\\resources\\gfx\\effects\\effect_015_tearpoof.png", scaleX, scaleY, 2, 0, 0, 64, 64, 16, 256);
        REDtearCollideAnimation = perepareEffect("file:src\\resources\\gfx\\effects\\effect_003_bloodtear.png", scaleX, scaleY, 2, 0, 0, 64, 64, 16, 256);
        bloodPool_small = perepareEffect("file:src\\resources\\gfx\\effects\\effect_016_bloodpool.png", scaleX, scaleY, 2, 0, 0, 32, 32, 12, 64);
        bloodPool_medium = perepareEffect("file:src\\resources\\gfx\\effects\\effect_016_bloodpool.png", scaleX, scaleY, 2, 64, 0, 48, 48, 6, 160);
        bloodPool_large = perepareEffect("file:src\\resources\\gfx\\effects\\effect_016_bloodpool.png", scaleX, scaleY, 2, 160, 0, 96, 96, 6, 448);
        enemyGuts = perepareEffect("file:src\\resources\\gfx\\effects\\effect_030_bloodgibs.png", scaleX, scaleY, 3, 0, 0, 16, 16, 12, 64);
        tiny_enemyGuts = perepareEffect("file:src\\resources\\gfx\\effects\\effect_030_bloodgibs.png", scaleX, scaleY, 3, 0, 48, 4, 4, 12, 32);
        poof = perepareEffect("file:src\\resources\\gfx\\effects\\effect_009_poof01.png", scaleX, scaleY, 1, 0,0, 64, 64, 8, 256);

    }

    private Image[] perepareEffect(String file, float scaleX, float scaleY, int sheetScale, int x, int y, int width, int height, int size, int xReset) {
        Image[] a = new Image[size];
        for (int i = 0; i < a.length; i++) {
            a[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
            x += width;
            if (x >= xReset) {
                x = 0;
                y += height;
            }
        }
        return a;
    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
