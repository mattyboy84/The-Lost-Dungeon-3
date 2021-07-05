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
import javafx.util.Duration;

public class Shading {

    int offsetX = -20;
    int offsetY = -28;

    float xMult = (float) 1.02;
    float yMult = (float) 1.044;

    int radius;
    Vecc2f mouse = new Vecc2f(480,540);//demo pos until character is made

    ImageView shading;
    Canvas overlay;
    String file = "file:src\\resources\\gfx\\backdrop\\shading.png";
    Timeline timeline;

    //        this.topLeft = new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * a), (new Image(file).getHeight() * b), false, false).getPixelReader(), (int) (this.width * a * randRow), (int) (this.height * b * randCol), (int) (this.width * a), (int) (this.height * b)));
    public Shading(float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.shading = new ImageView(new Image(file, (int) (screenBounds.getWidth() * xMult), (int) (screenBounds.getHeight() * yMult), false, false));
        //this.shading.relocate(offsetX*scaleX,offsetY*scaleY);
        this.overlay = new Canvas(shading.getBoundsInParent().getWidth(), shading.getBoundsInParent().getHeight());
        overlay.relocate(offsetX, offsetY);
        //
        this.radius = (int) (250 * ((scaleX + scaleY) / 2));
        //
        PixelReader pixelReader = shading.getImage().getPixelReader();
        //
        for (int i = 0; i < shading.getBoundsInParent().getWidth(); i++) {
            for (int j = 0; j < shading.getBoundsInParent().getHeight(); j++) {
                overlay.getGraphicsContext2D().getPixelWriter().setColor(i, j, pixelReader.getColor(i, j));
            }
        }

        timelineStarter(pixelReader, screenBounds);


    }

    private void timelineStarter(PixelReader pixelReader, Rectangle2D screenBounds) {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds((float) 1 / 60), event -> {
            overlay.getGraphicsContext2D().clearRect(0, 0, overlay.getWidth(), overlay.getHeight());
            overlay.getGraphicsContext2D().drawImage(shading.getImage(), 0, 0);
            //
            //mouse.sub(offsetX, offsetY);
            for (int i = Math.max((int) (mouse.x - radius), 0); i < Math.min(mouse.x + (radius), screenBounds.getWidth()); i++) {
                for (int j = Math.max((int) (mouse.y - radius), 0); j < Math.min(mouse.y + (radius), screenBounds.getHeight()); j++) {
                    //i = width
                    //j = height
                    double op = 0;


                    op = pixelReader.getColor(i, j).getOpacity();


                    int d = calc(i, j, mouse);

                    if (d < 250) {
                        double a = Math.max(op - 0.02, 0);
                        //System.out.println(a);
                        overlay.getGraphicsContext2D().getPixelWriter().setColor(i, j, Color.rgb(0, 0, 0, a));
                    }
                    if (d < 175) {
                        double a = Math.max(op - 0.05, 0);
                        overlay.getGraphicsContext2D().getPixelWriter().setColor(i, j, Color.rgb(0, 0, 0, a));
                    }
                    if (d < 100) {
                        double a = Math.max(op - 0.08, 0);
                        overlay.getGraphicsContext2D().getPixelWriter().setColor(i, j, Color.rgb(0, 0, 0, a));
                    }

                }
            }

        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private int calc(int i, int j, Vecc2f mouse) {

        int dX = (int) (i - mouse.x);
        int dY = (int) (j - mouse.y);


        return (int) Math.sqrt(((dX * dX) + (dY * dY)));
    }


    public void load(Group group) {
        group.getChildren().addAll(this.overlay);
        this.overlay.setViewOrder(-10);
        this.timeline.play();
    }

    public void unload(Group group) {
        group.getChildren().removeAll(this.shading);
        this.timeline.pause();
    }

}
