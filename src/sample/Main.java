package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
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
    Scene scene = new Scene(group, screenWidth, screenHeight);
    //
    Dungeon dungeon = new Dungeon();


    @Override
    public void start(Stage stage) throws Exception {

        dungeon.Generate();

        for (int i = 0; i <dungeon.getMap().length ; i++) {
            for (int j = 0; j <dungeon.getMap()[0].length ; j++) {
                System.out.print(dungeon.getMap()[i][j] + "  ");
            }
            System.out.println("");
        }





        stage.setScene(scene);
        //stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}