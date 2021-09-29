package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.util.ArrayList;

public class Shading {

    float xMult = (float) 1.02;
    float yMult = (float) 1.044;

    Image shading;
    Canvas overlay;
    String file = "file:src\\resources\\gfx\\backdrop\\shading2.0.png";
    Timeline timeline;
    //
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();

    Double[][] screen = new Double[(int) screenBounds.getWidth()][(int) screenBounds.getHeight()];
    ArrayList<Points> sources = new ArrayList<Points>();
    ArrayList<Points> activeSources = new ArrayList<Points>();
    //
    float scaleX,scaleY,avgScale;

    //        this.topLeft = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b), false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));
    public Shading(float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.shading = new Image(file, (int) (screenBounds.getWidth() * xMult), (int) (screenBounds.getHeight() * yMult), false, false);
        //
        this.scaleX=scaleX;
        this.scaleY=scaleY;
        this.avgScale = (((scaleX+scaleY))/2);


        //activeSources.add(new Points(300*scaleX, 300*scaleY, 50));

        //addActiveSource(300,300,50);
        //activeSources.add(new Points(500, 500, 50));
        //activeSources.add(new Points(500, 550, 50));
        //activeSources.add(new Points(1400, 900, 50));
        //
        /*
        Timeline externalTimeline = new Timeline(new KeyFrame(Duration.seconds((float) 1 / 60), event -> {

            //this external timeline is how other classes (player,enemies, background lights) will be pass a light point to the shading layer.

            removeActiveSource(a.x, a.y);

            //position of a moving object is updated or similar
            a = new Vecc2f(MouseInfo.getPointerInfo().getLocation().x - deltaX, MouseInfo.getPointerInfo().getLocation().y - deltaY);

            addActiveSource(a.x, a.y, 175);//new position is sent to the shading layer
        }));
        externalTimeline.setCycleCount(Timeline.INDEFINITE);
        externalTimeline.play();
        */

        this.overlay = new Canvas(shading.getWidth(), shading.getHeight());
        overlay.relocate(-20,-28);//offset cords
        //
        PixelReader pixelReader = shading.getPixelReader();
        //

        timelineStarter(pixelReader, screenBounds, scaleX, scaleY);
    }

    private void timelineStarter(PixelReader pixelReader, Rectangle2D screenBounds, float scaleX, float scaleY) {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds((float) 1 / 60), event -> {
            shade();
        }));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void shade() {
        PixelReader pixelReader = shading.getPixelReader();

        //
        overlay.getGraphicsContext2D().clearRect(0, 0, overlay.getWidth(), overlay.getHeight());
        overlay.getGraphicsContext2D().drawImage(shading, 0, 0);
        //
        for (Points activeSource : activeSources) {
            sources.add(new Points(activeSource.getPosition().x, activeSource.getPosition().y, activeSource.getRadius()));
        }
        //sources.add(new Points(1500,800,50));
        //
        for (Points source : sources) {
            float localRadius = (int) (source.getRadius() * ((scaleX) + (scaleY)) / 2);
            for (int i = (int) Math.max((source.getPosition().x - localRadius), 0); i < Math.min((source.getPosition().x + localRadius), screenBounds.getWidth()); i++) {
                for (int j = Math.max((int) (source.getPosition().y - localRadius), 0); j < Math.min((source.getPosition().y + localRadius), screenBounds.getHeight()); j++) {
                    //i = width
                    //j = height
                    float d = Math.min(calc(i, j, source.getPosition()), localRadius);
                    //
                    if (screen[i][j] == null) {
                        screen[i][j] = pixelReader.getColor(i, j).getOpacity();
                    }
                    overlay.getGraphicsContext2D().getPixelWriter().setColor(i, j, Color.rgb(0, 0, 0, screen[i][j] * (d / (localRadius))));
                    screen[i][j] = screen[i][j] * (d / (localRadius));
                }
            }
        }
        screen = new Double[(int) screenBounds.getWidth()][(int) screenBounds.getHeight()];
        sources.clear();
    }

    public void removeActiveSource(float x, float y) {
        for (int i = 0; i < activeSources.size(); i++) {
            if (activeSources.get(i).getPosition().x == x && activeSources.get(i).getPosition().y == y) {
                activeSources.remove(activeSources.get(i));
            }
        }
    }

    public void addActiveSource(float x, float y, int radius) {
        activeSources.add(new Points(x, y, (int) (radius*this.avgScale)));
    }

    public void removeActiveSource(String name) {
        for (int i = 0; i < activeSources.size(); i++) {
            if (activeSources.get(i).getName().equals(name)) {
                activeSources.remove(activeSources.get(i));
            }
        }
    }

    public void addActiveSource(float x, float y, int radius,String name) {
        activeSources.add(new Points(x, y, (int) (radius*this.avgScale),name));
    }

    private int calc(int i, int j, Vecc2f source) {
        int dX = (int) (i - source.x);
        int dY = (int) (j - source.y);
        return (int) Math.sqrt(((dX * dX) + (dY * dY)));
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
        String name;

        public Points(float x, float y, int i) {
            this.position = new Vecc2f(x, y);
            this.radius = i;
            this.name="";
        }
        public Points(float x, float y, int i,String name) {
            this.position = new Vecc2f(x, y);
            this.radius = i;
            this.name=name;
        }

        public Vecc2f getPosition() {
            return position;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRadius() {
            return radius;
        }
    }
}
