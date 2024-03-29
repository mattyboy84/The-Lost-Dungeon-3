package root;

import animatefx.animation.RubberBand;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import root.game.dungeon.Dungeon;
import root.game.dungeon.room.Room;
import root.game.music.Music;
import root.game.player.Player;
import root.game.util.Effects;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

public class Main extends Application implements Sprite_Splitter {

    //||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    static Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    //
    //1080p
    static int madeWithWidth = 1920;
    static int madeWithHeight = 1080;
    //
    static int screenWidth = (int) screenBounds.getWidth();
    static int screenHeight = (int) screenBounds.getHeight();
    //
    public static float scaleX = (float) screenWidth / madeWithWidth;
    public static float scaleY = (float) screenHeight / madeWithHeight;

    Stage parentStage;

    public static Group group = new Group();
    Group menuGroup = new Group();
    Group loadingGroup = new Group();
    Timeline loadPlayerMove;
    //
    Scene scene = new Scene(group, screenWidth, screenHeight);
    Scene menuScene = new Scene(menuGroup, screenWidth, screenHeight);
    Scene loadingScene = new Scene(loadingGroup, screenWidth, screenHeight, Color.BLACK);
    //
    Dungeon dungeon = new Dungeon();
    Dungeon newDungeon = new Dungeon();
    public Player player = new Player();
    int floor = 1;
    int minRooms = 18;
    int mapXWidth = 19;
    int mapYHeight = 19;

    @Override
    public void start(Stage stage) throws Exception {
        this.parentStage = stage;

        System.out.println(screenBounds);
        //1920 x 1080
        //2560 × 1440
        //3840 x 2160
        //
        Button newGame = new Button("New Game");
        newGame.relocate(300, 300);
        Button loadGame = new Button("Load Game");
        loadGame.relocate(450, 300);
        System.out.println("ScaleX: " + scaleX + " ScaleY: " + scaleY);
        menuGroup.getChildren().addAll(newGame, loadGame);
        //
        prepareLoadingScene();

        //
        Effects effects = new Effects();//initialise used effects on separate thread
        effects.start();
        //
        Music musics = new Music();
        musics.start();
        //
        dungeon.Generate(minRooms, mapXWidth, mapYHeight, floor, scaleX, scaleY, screenBounds, this);
        Dungeon.displayMap(dungeon.map);
        //
        player.Generate("character_001_isaac", dungeon.startX, dungeon.startY, scaleX, scaleY, screenBounds, 3, dungeon, "playerCon", group);
        player.start();
        while (!Player.loaded) {
            Thread.sleep(100);
        }
        //
        //newGame.setOnMouseClicked(mouseEvent -> {
        //    if (mouseEvent.getButton() == MouseButton.PRIMARY && Player.loaded) {
        dungeon.loadRoom(dungeon.startX, dungeon.startY, group, player);
        player.currentRoom.openDoors(group);
        player.load(group);
        //
        stage.setScene(scene);
        //     }
        // });
        //
        loadGame.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                System.out.println("load game");
            }
        });

        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W -> player.setNorthMOVING(true);
                case A -> player.setWestMOVING(true);
                case S -> player.setSouthMOVING(true);
                case D -> player.setEastMOVING(true);
                case UP -> player.setNorthLOOKING(true);
                case LEFT -> player.setWestLOOKING(true);
                case DOWN -> player.setSouthLOOKING(true);
                case RIGHT -> player.setEastLOOKING(true);
                case E -> player.placeBomb(group, "bomb", player.getCenterPos());
                case F -> stage.setFullScreen(!stage.isFullScreen());
                case J -> player.currentRoom.forceOpenDoors(group);
                case M -> player.getOverlay().revealMap();
                case TAB -> player.getOverlay().swapMapView(group);
                case C -> player.changeHealthBy(1);
                case V -> player.changeHealthBy(-1);
                case B -> player.changeMaxHealthBy(2, group);
                case N -> player.changeMaxHealthBy(-2, group);
                case T -> player.inflictDamage(1);
                case L -> player.currentRoom.newRealTimeEnemy("classic", "attack fly", new Vecc2f(600, 500), group);
            }
        });
        scene.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W -> player.setNorthMOVING(false);
                case A -> player.setWestMOVING(false);
                case S -> player.setSouthMOVING(false);
                case D -> player.setEastMOVING(false);
                case UP -> player.setNorthLOOKING(false);
                case LEFT -> player.setWestLOOKING(false);
                case DOWN -> player.setSouthLOOKING(false);
                case RIGHT -> player.setEastLOOKING(false);
            }
        });
        scene.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                System.out.println("Mouse: " + mouseEvent.getX() + " " + mouseEvent.getY());
            }
        });

        stage.setScene(scene);//bypassed the menu scene for now
        stage.setFullScreen(true);
        stage.show();
    }

    private void prepareLoadingScene() {
        String url = "file:src\\resources\\gfx\\ui\\stage\\" + "nightmares_bg_mask" + ".png";
        //
        addItem("file:src\\resources\\gfx\\ui\\stage\\" + "nightmares_bg_mask" + ".png", 0, 0, (float) ((menuScene.getWidth())/(new Image(url).getWidth())), (float) ((menuScene.getHeight())/(new Image(url).getHeight())), 1);
        //
        addItem("file:src\\resources\\gfx\\ui\\stage\\" + "playerspot" + ".png", 0, 0, scaleX, scaleY, 4);
        //
        addItem("file:src\\resources\\gfx\\ui\\stage\\" + "playerportrait_isaac" + ".png", 0, 0, scaleX, scaleY, 4);
    }

    private ImageView addItem(String url, int startX, int startY, float scaleX, float scaleY, int scale) {
        ImageView prepImage = new ImageView(imageGetter(url,
                startX,
                startY,
                (int) new Image(url).getWidth(),
                (int) new Image(url).getHeight(),
                (float) scaleX,
                (float) scaleY,
                scale));
        loadingGroup.getChildren().add(prepImage);
        prepImage.relocate((menuScene.getWidth() / 2 - (prepImage.getBoundsInParent().getWidth() / 2)), (menuScene.getHeight() - (prepImage.getBoundsInParent().getHeight())));

        return prepImage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void beginFloorTransition() {
        boolean isFull = this.parentStage.isFullScreen();
        this.parentStage.setScene(loadingScene);//prepares & swaps to the loading screen
        this.parentStage.setFullScreen(isFull);
        //
        this.floor++;
        dungeon = null;
        System.gc();
        Room.finishedRoom = 0;//this static int tracks dungeon gen progress
        Music.clearAll();
        //
        newDungeon.Generate(minRooms, mapXWidth, mapYHeight, floor, scaleX, scaleY, screenBounds, this);
        Dungeon.displayMap(newDungeon.map);
        newDungeon.loadRoom(newDungeon.startX, newDungeon.startY, group, player);
        dungeon = newDungeon;

        player.transitionToNewDungeon(newDungeon.startX, newDungeon.startY, dungeon, group);
        player.currentRoom.openDoors(group);
        //this.parentStage.setScene(scene);
    }
}