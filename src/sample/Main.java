package sample;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.stage.Screen;
import javafx.stage.Stage;

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
    static float scaleX = (float) screenWidth / madeWithWidth;
    static float scaleY = (float) screenHeight / madeWithHeight;

    Group group = new Group();
    Group menuGroup = new Group();
    Scene scene = new Scene(group, screenWidth, screenHeight);
    Scene menuScene = new Scene(menuGroup, screenWidth, screenHeight);
    //
    Dungeon dungeon = new Dungeon();
    Player player = new Player();
    static boolean p;


    @Override
    public void start(Stage stage) throws Exception {
        p = scaleX != 1;
        //System.out.println(getParameters().getRaw());
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
        //
        dungeon.Generate(18, 19, 19, floor, scaleX, scaleY, screenBounds);
        Dungeon.displayMap(dungeon.map);
        //
        player.Generate("character_001_isaac", dungeon.startX, dungeon.startY, scaleX, scaleY, screenBounds, 3, dungeon, "playerCon", group);
        player.start();
        //
        //
        newGame.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && Player.loaded) {
                dungeon.loadRoom(dungeon.startX, dungeon.startY, group);
                player.currentRoom.openDoors(group);
                player.load(group);
                //
                stage.setScene(scene);
            }
        });
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
                case F -> stage.setFullScreen(!stage.isFullScreen());
                case J -> player.currentRoom.forceOpenDoors(group);
                case M -> player.overlay.revealMap();
                case TAB -> player.overlay.over(group);
                case C -> player.increaseHealth(1, group);
                case V -> player.decreaseHealth(1, group);
                case B -> player.increaseMaxHealth(2, group);
                case N -> player.decreaseMaxHealth(2, group);
                case I -> player.overlay.miniMap.updateMinimap(9, 9);
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
        stage.setScene(menuScene);//bypassed the menu scene for now
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}