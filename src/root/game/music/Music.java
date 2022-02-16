package root.game.music;

import javafx.beans.NamedArg;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import root.game.dungeon.room.Room;

import java.io.File;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Music implements Runnable {

    public enum sfx {
        explosion_strong1("explosion_strong1"),
        explosion_strong2("explosion_strong2"),
        explosion_strong3("explosion_strong3"),
        tear_fire_4("tear fire 4"),
        tear_fire_5("tear fire 5"),
        hurt_grunt_0("hurt grunt 0"),
        hurt_grunt_1("hurt grunt 1"),
        hurt_grunt_2("hurt grunt 2"),
        splatter_0("splatter 0"),
        splatter_1("splatter 1"),
        splatter_2("splatter 2"),
        splatter_3("splatter 3"),
        splatter_4("splatter 4"),
        splatter_5("splatter 5"),
        key_pickup("key pickup guantlet 4"),
        lock_break_0("lock break 0"),
        lock_break_1("lock break 1"),
        penny_pickup_1("penny pickup 1");

        private final String sound;

        sfx(String a) {
            this.sound = a;
        }

        public String getSound() {
            return this.sound;
        }
    }
    /*
    public enum music {
        explosion_strong1("the caves"),
        explosion_strong2("library"),
        explosion_strong3("basic boss fight");

        private String sound;

        music(String a) {
            this.sound = a;
        }
    }
    */
    //

    static Random random = new Random();
    private Thread t;
    String threadName = "Music thread";
    static ArrayList<Sound> SFX_Sounds = new ArrayList<>();
    static ArrayList<Sound> MUSIC_Sounds = new ArrayList<>();

    private static double SFXVolume = 15;//this is a percent from 0% to 100%
    private static double MusicVolume = 6;//this is a percent

    public static HashMap<String, Media> musicTable = new HashMap();
    public static HashMap<String, Media> sfxTable = new HashMap();

    public static void addSFX(@NamedArg("repeat") boolean repeat, @NamedArg("Unique Hashcode") int hashcode, sfx... soundARR) {
        //can be sent multiple sfx to choose a random one.
        SFX_Sounds.add(new Sound(soundARR[random.nextInt(soundARR.length)].getSound(), repeat, hashcode, SFX_Sounds, sfxTable));
    }

    public static void removeSFX(int targetCode) {
        for (int k = SFX_Sounds.size() - 1; k > -1; k--) {
            SFX_Sounds.get(k).check(targetCode, SFX_Sounds);
        }
    }

    public static void clearSFX() {
        for (Sound music_sound : SFX_Sounds) {
            music_sound.mediaSound.stop();
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
            //
            MUSIC_Sounds.add(new Sound(musicName, repeat, hashcode, MUSIC_Sounds, musicTable, true));
            //
            //MUSIC_Sounds.add(new Sound(musicName + " intro", false, hashcode, MUSIC_Sounds, musicTable, true));
        }
    }

    public static void changeMusicVolume(Double newVolume) {
        newVolume = Math.min(Math.max(newVolume, 0), 100);//keep total volume between 0 & 100
        MusicVolume = newVolume;
        for (Sound music_sound : MUSIC_Sounds) {
            music_sound.changeVolume(MusicVolume);
        }
    }

    public static void transition(String oldMusic, String newMusic, Room currentRoom) {
        if ((oldMusic.equalsIgnoreCase("") || newMusic.equalsIgnoreCase(""))) {
            return;
        }
        for (Sound music_sound : MUSIC_Sounds) {
            if (music_sound.musicName.equalsIgnoreCase(oldMusic) && !(oldMusic.equalsIgnoreCase(newMusic))) {
                music_sound.mediaSound.pause();
            }
            if (music_sound.mediaLayer != null) {
                music_sound.mediaLayer.pause();
            }
        }
        //
        for (Sound music_sound : MUSIC_Sounds) {
            if (music_sound.musicName.equalsIgnoreCase(newMusic) && !(oldMusic.equalsIgnoreCase(newMusic))) {
                music_sound.mediaSound.play();
            }
            if (music_sound.mediaLayer != null && layerCheck(currentRoom)) {
                music_sound.mediaLayer.play();
                music_sound.mediaLayer.seek(music_sound.mediaSound.getCurrentTime());
                music_sound.mediaLayer.setMute(false);
            }
        }
    }

    private static boolean layerCheck(Room currentRoom) {
        int enemyLayerActivation = 2;//layer is active when more than this many enemies
        boolean enemies = (currentRoom.enemies.size() >= enemyLayerActivation);
        //boolean boss = (currentRoom.bosses.size() > 0);// TODO Remember to activate this when bosses are added
        if (enemies /*|| boss*/) {
            //System.out.println("--- layer active ---");
            return true;
        }
        return false;
    }

    @Override
    public void run() {//reading in media files in real time causes lag
        prepareSFX("explosion_strong1");
        prepareSFX("explosion_strong2");
        prepareSFX("explosion_strong3");

        prepareSFX("tear fire 4");
        prepareSFX("tear fire 5");

        prepareSFX("player", "hurt grunt 0");
        prepareSFX("player", "hurt grunt 1");
        prepareSFX("player", "hurt grunt 2");

        prepareSFX("v2", "splatter 0");
        prepareSFX("v2", "splatter 1");
        prepareSFX("v2", "splatter 2");
        prepareSFX("v2", "splatter 3");
        prepareSFX("v2", "splatter 4");
        prepareSFX("v2", "splatter 5");

        prepareSFX("feedback", "key pickup guantlet 4");
        prepareSFX("feedback", "lock break 0");
        prepareSFX("feedback", "lock break 1");
        prepareSFX("feedback", "penny pickup 1");

        prepareMedia("the caves");//room 1
        prepareMedia("the caves intro");//room 1 intro
        prepareMedia("the caves layer");//room 1 intro

        prepareMedia("library");//room 2
        prepareMedia("fight ogg", "basic boss fight");//room 3
    }

    private void prepareSFX(String fileName) {
        File f3 = new File("src\\resources\\sfx\\" + fileName + ".wav");
        Media media3 = new Media(f3.toURI().toString());
        sfxTable.putIfAbsent(fileName, media3);
    }

    private void prepareSFX(String subfolder, String fileName) {
        File f3 = new File("src\\resources\\sfx\\" + subfolder + "\\" + fileName + ".wav");
        Media media3 = new Media(f3.toURI().toString());
        sfxTable.putIfAbsent(fileName, media3);
    }

    private void prepareMedia(String fileName) {
        File f3 = new File("src\\resources\\music\\" + fileName + ".mp3");
        Media media3 = new Media(f3.toURI().toString());
        musicTable.putIfAbsent(fileName, media3);
    }

    private void prepareMedia(String subfolder, String fileName) {
        File f3 = new File("src\\resources\\music\\" + subfolder + "\\" + fileName + ".mp3");
        Media media3 = new Media(f3.toURI().toString());
        musicTable.putIfAbsent(fileName, media3);
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
        private MediaPlayer mediaLayer;
        //
        private boolean repeat;
        private int parentHashCode;
        private String musicName;

        public Sound(String sound, boolean repeat, int hashcode, ArrayList<Sound> soundsARR, HashMap<String, Media> table) {//SFX
            primarySetup(repeat, hashcode, sound, table, SFXVolume);
            //
            endOfMedia(soundsARR);
        }

        public Sound(String sound, boolean repeat, int hashcode, ArrayList<Sound> soundsARR, HashMap<String, Media> table, Boolean onlyMusic) {//MUSIC
            primarySetup(repeat, hashcode, sound, table, MusicVolume);
            //
            try {
                this.mediaLayer = new MediaPlayer(table.get(sound + " layer"));
                //System.out.println(sound + " layer");
                this.mediaLayer.setVolume(0.01 * (MusicVolume - 2));
                this.mediaLayer.setMute(true);
                this.mediaLayer.play();
            } catch (Exception e) {
                //no layer file
            }
            //
            endOfMedia(soundsARR);
        }

        private void primarySetup(boolean repeat, int hashcode, String sound, HashMap<String, Media> table, double soundVolume) {
            this.repeat = repeat;
            this.parentHashCode = hashcode;//TODO Hash code alone may not be enough - 1 object may have multiple sounds
            //
            this.musicName = sound;
            //System.out.println("added sound: " + musicName);
            //
            this.mediaSound = new MediaPlayer(table.get(sound));
            this.mediaSound.setVolume(0.01 * soundVolume);
            this.mediaSound.play();
        }

        public void check(int targetCode, ArrayList<Sound> activeSounds) {
            if ((this.parentHashCode) == targetCode) {
                this.mediaSound.stop();
                activeSounds.remove(this);
            }
        }

        public void changeVolume(double newVolume) {
            mediaSound.setVolume(0.01 * newVolume);
            try {
                mediaLayer.setVolume(0.01 * (newVolume - 2));
            } catch (Exception ignored) {
            }
        }

        private void endOfMedia(ArrayList<Sound> sounds) {
            if (repeat) {
                this.mediaSound.setOnEndOfMedia(() -> {
                    this.mediaSound.seek(Duration.ONE);
                });
            } else {
                this.mediaSound.setOnEndOfMedia(() -> {
                    try {
                        //System.out.println(this.musicName + " Removed");
                        this.mediaSound.stop();
                        sounds.remove(this);
                    } catch (Exception e) {//self terminates
                        System.out.println(e);
                    }
                });
            }
        }
    }
}