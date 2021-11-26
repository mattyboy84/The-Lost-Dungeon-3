package root.game.dungeon.room;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import root.game.util.Effects;
import root.game.util.Hitbox;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Active_Bomb implements Sprite_Splitter {

    ImageView bomb;
    Image[] activeAnimation;
    Image[] explodeAnimation;
    Vecc2f position = new Vecc2f();
    Hitbox hitbox;
    JsonObject template = null;


    public Active_Bomb(String bombTemplate, Vecc2f centerPos, float scaleX, float scaleY) {
        this.template = new JsonParser().parse(String.valueOf(templateGetter("src\\resources\\gfx\\items\\pick ups\\" + bombTemplate + ".json"))).getAsJsonObject();
        //
        String file = "file:src\\resources\\gfx\\items\\pick ups\\" + this.template.get("Sprite").getAsString() + ".png";
        int startX = this.template.get("StartX").getAsInt();
        int startY = this.template.get("StartY").getAsInt();
        int width = this.template.get("Width").getAsInt();
        int height = this.template.get("Height").getAsInt();
        float sheetScale = this.template.get("SheetScale").getAsFloat();
        this.hitbox = new Hitbox(this.template.getAsJsonObject("Hitbox"), (int) sheetScale, scaleX, scaleY);
        this.bomb = new ImageView(imageGetter(file, startX, startY, width, height, scaleX, scaleY, sheetScale));
        this.position.set(centerPos.x, centerPos.y);
        activeAnimationSetup(file, scaleX, scaleY, sheetScale, this.template.get("ActiveAnimation").getAsJsonArray());
    }

    private void activeAnimationSetup(String file, float scaleX, float scaleY, float sheetScale, JsonArray activeAnimationARR) {
        this.activeAnimation = new Image[activeAnimationARR.size()];
        //EXAMPLE
        /*{
      "StartX": 32,
      "StartY": 32,
      "Width": 32,
      "Height": 16}
         */
        for (int i = 0; i < activeAnimationARR.size(); i++) {
            int x = activeAnimationARR.get(i).getAsJsonObject().get("StartX").getAsInt();
            int y = activeAnimationARR.get(i).getAsJsonObject().get("StartY").getAsInt();
            int width = activeAnimationARR.get(i).getAsJsonObject().get("Width").getAsInt();
            int height = activeAnimationARR.get(i).getAsJsonObject().get("Height").getAsInt();
            activeAnimation[i] = imageGetter(file, x, y, width, height, scaleX, scaleY, sheetScale);
        }
    }

    private StringBuilder templateGetter(String file2) {
        StringBuilder json = new StringBuilder();
        try {
            File file = new File(file2);

            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null) {
                json.append(st);
            }
        } catch (Exception e) {
            System.out.println("Cannot find template - Active_Bomb");
        }
        return json;
    }

    public void load(Group group, Room room) {
        group.getChildren().addAll(this.bomb, this.hitbox.getShape());
        this.bomb.relocate(this.position.x, this.position.y);
        this.bomb.setViewOrder(-4);
        //
        this.hitbox.getShape().relocate(this.position.x + this.hitbox.getxDelta(), this.position.y + this.hitbox.getyDelta());
        this.hitbox.getShape().setViewOrder(-4);
        this.hitbox.getShape().setVisible(false);
        //START TIMELINES



        //at end of timelines when bomb detonates call back to damage to damage/ destroy stuff
    }
    public void unload(Group group) {
        group.getChildren().removeAll(this.bomb, this.hitbox.getShape());
        //
        //STOP TIMELINES


    }

}