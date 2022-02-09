package root.game.music;

import javafx.beans.NamedArg;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Music implements Runnable {

    //
    //

    public enum sfx {
        thunder("thunder4");

        private String sound;

        sfx(String a) {
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


    private Thread t;
    String threadName = "Music thread";
    static ArrayList<Sound> SFX_Sounds = new ArrayList<>();
    static ArrayList<Sound> Music_Sounds = new ArrayList<>();
    public static double  SFXVolume = 10;//this is a percent from 0% to 100%
    public static double MusicVolume=8;//this is a percent
    //
    public static HashMap<String,MediaPlayer> mediaTable=new HashMap();
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

    public static void removeSFX(int targetCode) {
        for (int k = SFX_Sounds.size()-1; k >-1; k--) {
            SFX_Sounds.get(k).check(targetCode, SFX_Sounds);
        }
    }

    public static void addSFX(String sound, @NamedArg("repeat") boolean repeat, @NamedArg("Unique Hashcode") int hashcode) {
        SFX_Sounds.add(new Sound(sound, repeat,hashcode, SFX_Sounds));
    }

    public static void changeSFXVolume(Double newVolume){
        SFXVolume=newVolume;
        for (Sound sfx_sound : SFX_Sounds) {
            sfx_sound.changeVolume(SFXVolume);
        }
    }
    //
    //
    //

    public static void addMusic(MediaPlayer music,@NamedArg("repeat") boolean repeat, @NamedArg("Unique Hashcode") int hashcode){
        Music_Sounds.add(new Sound(music,repeat,hashcode,Music_Sounds));
    }

    @Override
    public void run() {
        prepareMedia("the caves");

    }

    private void prepareMedia(String fileName) {
        MediaPlayer temp;
        File f3 = new File("src\\resources\\music\\" + fileName + ".wav");
        Media media3 = new Media(f3.toURI().toString());
        temp = new MediaPlayer(media3);
        mediaTable.put(fileName, temp);
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

        public Sound(String sound, boolean repeat, int hashcode, ArrayList<Sound> SFX_Sounds) {
            this.repeat = repeat;
            this.parentHashCode=hashcode;//TODO Hash code alone may not be enough - 1 object may have multiple sounds
            //
            System.out.println("added sound: " + sound);
            //
            File f3 = new File("src\\resources\\sfx\\" + sound + ".wav");
            Media media3 = new Media(f3.toURI().toString());
            mediaSound = new MediaPlayer(media3);
            mediaSound.setVolume(0.01*SFXVolume);
            mediaSound.play();
            //

            mediaSound.setOnEndOfMedia(() -> {
                if (this.repeat) {
                    mediaSound.seek(Duration.ONE);
                } else {
                    try {
                        SFX_Sounds.remove(this);
                    } catch (Exception ignored) {//self terminates
                    }
                }

            });
        }

        public Sound(MediaPlayer music, boolean repeat, int hashcode, ArrayList<Sound> music_sounds) {
            this.repeat = repeat;
            this.parentHashCode=hashcode;//TODO Hash code alone may not be enough - 1 object may have multiple sounds
            //
            System.out.println("added music: " + music);
            //
            this.mediaSound=music;
            mediaSound.setVolume(0.01*MusicVolume);
            this.mediaSound.play();
            //

            mediaSound.setOnEndOfMedia(() -> {
                if (this.repeat) {
                    mediaSound.seek(Duration.ONE);
                } else {
                    try {
                        music_sounds.remove(this);
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

        public void changeVolume(double sfxVolume) {
            mediaSound.setVolume(0.01*sfxVolume);
        }
    }
}