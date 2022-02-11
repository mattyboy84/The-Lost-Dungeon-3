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
        thunder4("thunder4");

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
    static ArrayList<Sound> MUSIC_Sounds = new ArrayList<>();

    private static double SFXVolume = 15;//this is a percent from 0% to 100%
    private static double MusicVolume = 0;//this is a percent

    public static HashMap<String, MediaPlayer> mediaTable = new HashMap();
    public static HashMap<String, MediaPlayer> sfxTable = new HashMap();


    public static void addSFX(String sound, @NamedArg("repeat") boolean repeat, @NamedArg("Unique Hashcode") int hashcode) {
        SFX_Sounds.add(new Sound(sound, repeat, hashcode, SFX_Sounds));
    }

    public static void removeSFX(int targetCode) {
        for (int k = SFX_Sounds.size() - 1; k > -1; k--) {
            SFX_Sounds.get(k).check(targetCode, SFX_Sounds);
        }
    }

    public static void clearSFX() {
        for (Sound music_sound : SFX_Sounds) {
            music_sound.mediaSound.pause();
        }
        SFX_Sounds.clear();
    }

    public static void changeSFXVolume(Double newVolume) {
        newVolume = Math.min(Math.max(newVolume, 0), 100);//keep total volume between 0 & 100
        SFXVolume = newVolume;
        for (Sound sfx_sound : SFX_Sounds) {
            sfx_sound.changeVolume(SFXVolume);
        }
    }

    public static void changeMusicVolume(Double newVolume) {
        newVolume = Math.min(Math.max(newVolume, 0), 100);//keep total volume between 0 & 100
        MusicVolume = newVolume;
        for (Sound music_sound : MUSIC_Sounds) {
            music_sound.changeVolume(MusicVolume);
        }
    }

    /**
     * will attempt to add a new music track to the array
     */
    public static void addMusic(String musicName, @NamedArg("repeat") boolean repeat, @NamedArg("Unique Hashcode") int hashcode) {
        boolean newMusic = true;

        for (Sound musicSound : MUSIC_Sounds) {
            if (musicSound.musicName.equalsIgnoreCase(musicName)) {
                newMusic = false;
                break;
            }
        }
        //
        if (newMusic) {
            System.out.println("new music added to array");
            for (Sound music_sound : MUSIC_Sounds) {
                music_sound.mediaSound.pause();
            }
            MUSIC_Sounds.add(new Sound(musicName, repeat, hashcode, MUSIC_Sounds, true));
        }
    }

    public static void transition(String oldMusic, String newMusic) {
        if (!(oldMusic.equalsIgnoreCase(newMusic) || oldMusic.equalsIgnoreCase("") || newMusic.equalsIgnoreCase(""))) {
            //System.out.println("music transitioned");
            mediaTable.get(oldMusic).pause();
            mediaTable.get(newMusic).play();
        } else {
            //System.out.println("no music transition needed");
            //do nothing
        }
    }

    @Override
    public void run() {//reading in full media files in real time causes lag
        prepareSFX("explosion_strong1");
        prepareSFX("explosion_strong2");
        prepareSFX("explosion_strong3");
        prepareSFX("tear fire 4");
        prepareSFX("tear fire 5");
        prepareSFX("player","hurt grunt 0");
        prepareSFX("player","hurt grunt 1");
        prepareSFX("player","hurt grunt 2");

        //
        prepareMedia("the caves");//room 1
        prepareMedia("library");//room 2
        prepareMedia("fight ogg", "basic boss fight");//room 3
    }

    private void prepareSFX(String fileName) {
        MediaPlayer temp;
        File f3 = new File("src\\resources\\sfx\\" + fileName + ".wav");
        Media media3 = new Media(f3.toURI().toString());
        temp = new MediaPlayer(media3);
        sfxTable.putIfAbsent(fileName, temp);

    }

    private void prepareSFX(String subfolder,String fileName) {
        MediaPlayer temp;
        File f3 = new File("src\\resources\\sfx\\" + subfolder + "\\" + fileName + ".wav");
        Media media3 = new Media(f3.toURI().toString());
        temp = new MediaPlayer(media3);
        sfxTable.putIfAbsent(fileName, temp);

    }

    private void prepareMedia(String fileName) {
        MediaPlayer temp;
        File f3 = new File("src\\resources\\music\\" + fileName + ".mp3");
        Media media3 = new Media(f3.toURI().toString());
        temp = new MediaPlayer(media3);
        mediaTable.putIfAbsent(fileName, temp);
    }

    private void prepareMedia(String subfolder, String fileName) {
        MediaPlayer temp;
        File f3 = new File("src\\resources\\music\\" + subfolder + "\\" + fileName + ".mp3");
        Media media3 = new Media(f3.toURI().toString());
        temp = new MediaPlayer(media3);
        mediaTable.putIfAbsent(fileName, temp);
    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    public static class Sound {

        private MediaPlayer mediaSound;
        private boolean repeat;
        private int parentHashCode;
        private String musicName;

        public Sound(String sound, boolean repeat, int hashcode, ArrayList<Sound> SFX_Sounds) {//SFX
            this.repeat = repeat;
            this.parentHashCode = hashcode;//TODO Hash code alone may not be enough - 1 object may have multiple sounds
            //
            this.musicName = sound;
            System.out.println("added sound: " + musicName);
            //
            this.mediaSound = sfxTable.get(sound);
            System.out.println(sfxTable.get(sound).getStatus());

            this.mediaSound.setVolume(0.01 * SFXVolume);
            this.mediaSound.play();

            endOfMedia(SFX_Sounds);
        }

        public Sound(String sound, boolean repeat, int hashcode, ArrayList<Sound> music_sounds, boolean a) {//MUSIC
            this.repeat = repeat;
            this.parentHashCode = hashcode;//TODO Hash code alone may not be enough - 1 object may have multiple sounds
            //
            this.musicName = sound;
            System.out.println("added music: " + sound);
            //
            this.mediaSound = mediaTable.get(sound);

            this.mediaSound.setVolume(0.01 * MusicVolume);
            this.mediaSound.play();

            endOfMedia(music_sounds);
        }

        public void check(int targetCode, ArrayList<Sound> activeSounds) {
            if ((this.parentHashCode) == targetCode) {
                this.mediaSound.stop();
                activeSounds.remove(this);
            }
        }

        public void changeVolume(double sfxVolume) {
            mediaSound.setVolume(0.01 * sfxVolume);
        }

        private void endOfMedia(ArrayList<Sound> sounds) {
            if (repeat) {
                this.mediaSound.setOnEndOfMedia(() -> {
                    this.mediaSound.seek(Duration.ONE);

                });
            } else {
                this.mediaSound.setOnEndOfMedia(() -> {
                    try {
                        System.out.println(this.musicName + " Removed");
                        //this.mediaSound.dispose();
                        sounds.remove(this);
                        this.mediaSound.seek(Duration.ONE);
                        this.mediaSound.stop();
                    } catch (Exception e) {//self terminates
                        System.out.println(e);
                    }
                });
            }
        }
    }
}