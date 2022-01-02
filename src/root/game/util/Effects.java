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


    @Override
    public void run() {
        explodeAnimationSetup("file:src\\resources\\gfx\\effects\\effect_029_explosion.png", scaleX, scaleY, 2);
        explosionMarkAnimationSetup("file:src\\resources\\gfx\\effects\\effect_017_bombradius.png", scaleX, scaleY, 2);
        BLUEtearCollideAnimation = tearCollideAnimationSetup("file:src\\resources\\gfx\\effects\\effect_015_tearpoof.png", scaleX, scaleY, 2);
        REDtearCollideAnimation = tearCollideAnimationSetup("file:src\\resources\\gfx\\effects\\effect_003_bloodtear.png", scaleX, scaleY, 2);
        bloodPool_small = bloodPoolSetup("file:src\\resources\\gfx\\effects\\effect_016_bloodpool.png", scaleX, scaleY, 1
                , 12, 0, 0, 32, 32, 64);
        bloodPool_medium = bloodPoolSetup("file:src\\resources\\gfx\\effects\\effect_016_bloodpool.png", scaleX, scaleY, 1
                , 6, 64, 0, 48, 48, 160);
        bloodPool_large = bloodPoolSetup("file:src\\resources\\gfx\\effects\\effect_016_bloodpool.png", scaleX, scaleY, 1
                , 6, 160, 0, 96, 96, 448);
        enemyGutsSetup("file:src\\resources\\gfx\\effects\\effect_030_bloodgibs.png", scaleX, scaleY, 1);
        tinyEnemyGutsSetup("file:src\\resources\\gfx\\effects\\effect_030_bloodgibs.png", scaleX, scaleY, 1);
    }

    private void tinyEnemyGutsSetup(String file, float scaleX, float scaleY, int sheetScale) {
        int x = 0, y = 48;
        int width = 4, height = 4;
        tiny_enemyGuts = new Image[12];
        for (int i = 0; i < tiny_enemyGuts.length; i++) {//might need -1
            tiny_enemyGuts[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
            x += width;
            if (x >= (8 * width)) {
                x = 0;
                y += height;
            }
        }
    }

    private void enemyGutsSetup(String file, float scaleX, float scaleY, int sheetScale) {
        int x = 0, y = 0;
        int width = 16, height = 16;
        enemyGuts = new Image[12];
        for (int i = 0; i < enemyGuts.length; i++) {//might need -1
            enemyGuts[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
            x += width;
            if (x >= (4 * width)) {
                x = 0;
                y += height;
            }
        }
    }

    private Image[] bloodPoolSetup(String file, float scaleX, float scaleY, int sheetScale, int arraySize,
                                   int startX, int startY, int width, int height, int xReset) {
        int x = startX, y = startY;
        Image[] array = new Image[arraySize];
        for (int i = 0; i < array.length; i++) {
            array[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
            x += width;
            if (x >= xReset) {
                x = 0;
                y += height;
            }
        }
        return array;
    }

    private Image[] tearCollideAnimationSetup(String file, float scaleX, float scaleY, int sheetScale) {
        Image[] a = new Image[16];
        int x = 0, y = 0;
        int width = 64, height = 64;
        for (int i = 0; i < a.length; i++) {//might need -1
            a[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
            x += width;
            if (x >= (4 * width)) {
                x = 0;
                y += height;
            }
        }
        return a;
    }

    private void explosionMarkAnimationSetup(String file, float scaleX, float scaleY, int sheetScale) {
        int x = 0, y = 0;
        int width = 96, height = 64;
        explosionMarkImages = new Image[8];
        for (int i = 0; i < explosionMarkImages.length; i++) {//might need -1
            explosionMarkImages[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
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
        for (int i = 0; i < explodeAnimation.length; i++) {//might need -1
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
