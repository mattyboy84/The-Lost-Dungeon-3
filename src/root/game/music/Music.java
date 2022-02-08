package root.game.music;

import javafx.beans.NamedArg;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class Music implements Runnable {

    //
    //

    public enum sounds {
        thunder("thunder4");

        private final String sound;

        sounds(String a) {
            this.sound = a;
        }

        public String getSound() {
            return this.sound;
        }
    }
    //
    //
    //
    //

    public static void removeSound(int targetCode) {
        for (int k = activeSounds.size()-1; k >-1; k--) {
            activeSounds.get(k).check(targetCode,activeSounds);
        }
    }

    private Thread t;
    String threadName = "Music thread";
    static ArrayList<Sound> activeSounds = new ArrayList<>();

    /*
    File f3 = new File("src\\resources\\sfx\\thunder4.wav");
        Media media3 = new Media(f3.toURI().toString());
        MediaPlayer test = new MediaPlayer(media3);
        test.setVolume(0.4);
        test.play();
         test.setOnEndOfMedia(() -> {
             System.out.println("ttttt");
             test.seek(Duration.ONE);
             test.play();
         });
     */

    public static void addSFX(String sound, @NamedArg("repeat") boolean repeat, @NamedArg("Unique Hashcode") int hashcode) {
        activeSounds.add(new Sound(sound, repeat,hashcode, activeSounds));
    }


    @Override
    public void run() {


    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    private static class Sound {

        private MediaPlayer mediaSound;
        private boolean repeat;
        private int parentHashCode;

        public Sound(String sound, boolean repeat, int hashcode, ArrayList<Sound> activeSounds) {
            this.repeat = repeat;
            this.parentHashCode=hashcode;//TODO Hash code alone may not be enough - 1 object may have multiple sounds
            //
            System.out.println("added sound: " + sound);
            //
            File f3 = new File("src\\resources\\sfx\\" + sound + ".wav");
            Media media3 = new Media(f3.toURI().toString());
            mediaSound = new MediaPlayer(media3);
            mediaSound.setVolume(0.1);
            mediaSound.play();
            //

            mediaSound.setOnEndOfMedia(() -> {
                if (this.repeat) {
                    mediaSound.seek(Duration.ONE);
                } else {
                    try {
                        activeSounds.remove(this);
                    } catch (Exception ignored) {//self terminates
                    }
                }

            });

        }

        public void check(int targetCode, ArrayList<Sound> activeSounds) {
            if ((this.parentHashCode)==targetCode){
                this.mediaSound.stop();
                activeSounds.remove(this);
            }
        }
    }
}