package root;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.stage.Screen;
import javafx.stage.Stage;
import root.game.dungeon.Dungeon;
import root.game.player.Player;
import root.game.util.Effects;

public class Main extends Application {

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

    public static Group group = new Group();
    Group menuGroup = new Group();
    Scene scene = new Scene(group, screenWidth, screenHeight);
    Scene menuScene = new Scene(menuGroup, screenWidth, screenHeight);
    //
    Dungeon dungeon = new Dungeon();
    public static Player player = new Player();


    @Override
    public void start(Stage stage) throws Exception {

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
        int floor = 0;
        //
        Effects effects = new Effects();//initialise used effects on separate thread
        effects.start();
        //
        dungeon.Generate(18, 19, 19, floor, scaleX, scaleY, screenBounds);
        Dungeon.displayMap(dungeon.map);
        //
        player.Generate("character_001_isaac", dungeon.startX, dungeon.startY, scaleX, scaleY, screenBounds, 3, dungeon, "playerCon", group);
        player.start();
        while (!Player.loaded) {
            Thread.sleep(100);
        }
        //
        //
        //newGame.setOnMouseClicked(mouseEvent -> {
        //    if (mouseEvent.getButton() == MouseButton.PRIMARY && Player.loaded) {
        dungeon.loadRoom(dungeon.startX, dungeon.startY, group);
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
                case E -> player.placeBomb(group, "bomb", player.centerPos);
                case F -> stage.setFullScreen(!stage.isFullScreen());
                case J -> player.currentRoom.forceOpenDoors(group);
                case M -> player.getOverlay().revealMap();
                case TAB -> player.getOverlay().over(group);
                case C -> player.increaseHealth(1, group);
                case V -> player.decreaseHealth(1, group);
                case B -> player.increaseMaxHealth(2, group);
                case N -> player.decreaseMaxHealth(2, group);
                case I -> player.getOverlay().miniMap.updateMinimap(9, 9);
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
        stage.setScene(scene);//bypassed the menu scene for now
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}