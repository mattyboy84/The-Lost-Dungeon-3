package sample;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class Player {

    int roomX, roomY;
    String costume;
    Image[] heads = new Image[6];
    Image[] LR_body = new Image[10];
    Image[] UD_body = new Image[10];
    //
    ImageView head = new ImageView();
    ImageView body = new ImageView();
    //
    int width, height;
    //

//src\resources\gfx\characters\costumes
    //"file:src\resources\gfx\characters\costumes\[costume].png"

    //        this.rock = (new ImageView(new WritableImage(new Image(file, ((new Image(file).getWidth() * scaleX * sheetScale)), ((new Image(file).getHeight() * scaleY * sheetScale)), false, false).getPixelReader(),
    //        (int) ((this.width * imageX)), (int) ((this.height * imageY)), (int) this.width, (int) this.height)));

    public void Generate(String costume, int startX, int startY, float scaleX, float scaleY, Rectangle2D screenBounds, int sheetScale) {
        this.width = (int) (32 * scaleX * sheetScale);
        this.height = (int) (32 * scaleY * sheetScale);
        //
        this.costume = costume;
        this.roomX = startX;
        this.roomY = startY;
        String file = "file:src\\resources\\gfx\\characters\\costumes\\" + this.costume + ".png";
        for (int i = 0; i < this.heads.length; i++) {//head images
            this.heads[i] = (new ImageView(new WritableImage(new Image(file, ((new Image(file).getWidth() * scaleX * sheetScale)), ((new Image(file).getHeight() * scaleY * sheetScale)), false, false).getPixelReader(),
                    (this.width * i), 0, (int) this.width, (int) this.height))).getImage();
        }
        //
        readImageINTOArray(file,sheetScale,scaleX,scaleY,UD_body, (int) (192 * scaleX * sheetScale),0);
        //
        readImageINTOArray(file, sheetScale, scaleX, scaleY, LR_body,0,(int) (64 * sheetScale * scaleY));

        this.body.setImage(LR_body[LR_body.length-1]);
    }

    private void readImageINTOArray(String file, int sheetScale, float scaleX, float scaleY, Image[] ARRAY,int startX,int startY) {
        for (int i = 0; i < ARRAY.length; i++) {
            ARRAY[i] = (new ImageView(new WritableImage(new Image(file, ((new Image(file).getWidth() * scaleX * sheetScale)), ((new Image(file).getHeight() * scaleY * sheetScale)), false, false).getPixelReader(),
                    startX, startY, (int) this.width, (int) this.height))).getImage();
            startX = startX + this.width;
            if (startX >= 256 * sheetScale * scaleX) {
                startX = 0;
                startY = startY + this.height;
            }
        }
    }


    public void load(Group group) {
        group.getChildren().add(this.body);
        //this.head.relocate(500, 500);
    }
}
