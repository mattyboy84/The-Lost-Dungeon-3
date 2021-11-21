package root.game.dungeon;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.util.ArrayList;

import root.game.util.Vecc2f;

public class Shading {

    float xMult = (float) 1.02;
    float yMult = (float) 1.044;

    Image shading;
    Canvas overlay;
    String file = "file:src\\resources\\gfx\\backdrop\\shading2.0.png";
    Timeline timeline;
    //
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    PixelReader pixelReader;

    Float[][] screen = new Float[(int) screenBounds.getWidth()][(int) screenBounds.getHeight()];
    ArrayList<Points> sources = new ArrayList<Points>();
    ArrayList<Points> activeSources = new ArrayList<Points>();
    //
    float scaleX, scaleY, avgScale;

    //        this.topLeft = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b), false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));
    public Shading(float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.shading = new Image(file, (int) (screenBounds.getWidth() * (xMult)), (int) (screenBounds.getHeight() * (yMult)), false, false);
        this.pixelReader = shading.getPixelReader();
        //
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.avgScale = (((scaleX + scaleY)) / 2);

        this.overlay = new Canvas(shading.getWidth(), shading.getHeight());
        overlay.relocate(Math.round(-20*scaleX), Math.round(-28*scaleY));//offset cords
        //
        //

        timelineStarter(screenBounds, scaleX, scaleY);
    }

    private void timelineStarter(Rectangle2D screenBounds, float scaleX, float scaleY) {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds((float) 1 / 60), event -> {
            shade();
        }));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void shade() {
        //PixelReader pixelReader = shading.getPixelReader();

        //
        overlay.getGraphicsContext2D().clearRect(0, 0, overlay.getWidth(), overlay.getHeight());
        overlay.getGraphicsContext2D().drawImage(shading, 0, 0);
        //
        for (Points source : activeSources) {
            int posX = (int) source.position.x, posY = (int) source.position.y;
            for (int i = 0; i < ((source.shader.length)); i++) {
               for (int j = 0; j < ((source.shader.length)); j++) {

                    if (posX+i < 0 || posX+i > (screenBounds.getWidth()-1) || posY+j < 0 || posY+j > (screenBounds.getHeight()-1)) {
                        break;
                    }
                    if (screen[posX + i][posY + j] == null) {
                        screen[posX + i][posY + j] = (float) (pixelReader.getColor(posX + i, posY + j).getOpacity());
                    }
                    overlay.getGraphicsContext2D().getPixelWriter().setColor(posX + i, posY + j, Color.rgb(0, 0, 0, (screen[posX + i][posY + j] * source.shader[i][j])));
                    screen[posX + i][posY + j] = screen[posX + i][posY + j] * source.shader[i][j];

                }
            }
        }
        //
        screen = new Float[(int) screenBounds.getWidth()][(int) screenBounds.getHeight()];
    }

    public void removeActiveSource(int name) {
        for (int i = 0; i < activeSources.size(); i++) {
            if (activeSources.get(i).getName() == name) {
                activeSources.remove(activeSources.get(i));
            }
        }
    }

    public void addActiveSource(float x, float y, float[][] shader, int name) {
        activeSources.add(new Points(x, y, shader, name));
    }

    public void load(Group group) {
        group.getChildren().addAll(this.overlay);
        this.overlay.setViewOrder(-10);
        this.timeline.play();
        shade();
    }

    public void unload(Group group) {
        group.getChildren().removeAll(this.overlay);
        this.timeline.pause();
    }

    private class Points {

        Vecc2f position;
        int radius;
        int name;
        float[][] shader;

        public Points(float x, float y, float[][] shader, int name) {
            this.position = new Vecc2f(x, y);
            this.shader = shader;
            this.name = name;
        }

        public Vecc2f getPosition() {
            return position;
        }

        public int getName() {
            return name;
        }

        public void setName(int name) {
            this.name = name;
        }

        public int getRadius() {
            return radius;
        }
    }
}
