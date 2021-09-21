package sample;

import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    //||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();

    //
    //1080p
    int madeWithWidth = 1920;
    int madeWithHeight = 1080;
    //
    int screenWidth = (int) screenBounds.getWidth();
    int screenHeight = (int) screenBounds.getHeight();
    //
    float scaleX = (float) screenWidth / madeWithWidth;
    float scaleY = (float) screenHeight / madeWithHeight;

    Group group = new Group();
    Group menuGroup = new Group();
    Scene scene = new Scene(group, screenWidth, screenHeight);
    Scene menuScene = new Scene(menuGroup, screenWidth, screenHeight);
    //
    Dungeon dungeon = new Dungeon();
    Player player = new Player();


    @Override
    public void start(Stage stage) throws Exception {

        System.out.println(screenBounds);
        //1920 x 1080
        //2560 × 1440

        Button newGame = new Button("New Game");
        newGame.relocate(300, 300);
        Button loadGame = new Button("Load Game");
        loadGame.relocate(450, 300);
        System.out.println("ScaleX: " + scaleX + " ScaleY: " + scaleY);
        menuGroup.getChildren().addAll(newGame, loadGame);

        // newGame.setOnMouseClicked(mouseEvent -> {
        //    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        int floor = 0;
        dungeon.Generate(18, 19, 19, floor, scaleX, scaleY, screenBounds);
        dungeon.displayMap();
        dungeon.loadRoom(dungeon.startX, dungeon.startY, group);
        //

        player.Generate("character_001_isaac", dungeon.startX, dungeon.startY, scaleX, scaleY, screenBounds, 3,dungeon);
        player.load(group);
        //
        stage.setScene(scene);
        //    }
        //});

        loadGame.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                System.out.println("load game");
            }
        });

        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    player.setNorthMOVING(true);
                    break;
                case A:
                    player.setWestMOVING(true);
                    break;
                case S:
                    player.setSouthMOVING(true);
                    break;
                case D:
                    player.setEastMOVING(true);
                    break;
                case F:
                    stage.setFullScreen(!stage.isFullScreen());
                    break;
                case J:
                    player.currentRoom.forceOpenDoors(group);
                    break;
            }
        });
        scene.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    player.setNorthMOVING(false);
                    break;
                case A:
                    player.setWestMOVING(false);
                    break;
                case S:
                    player.setSouthMOVING(false);
                    break;
                case D:
                    player.setEastMOVING(false);
                    break;
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