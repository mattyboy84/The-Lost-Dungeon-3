package root.game.dungeon.room;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import root.game.util.Sprite_Splitter;
import root.game.util.Vecc2f;

import java.util.ArrayList;
import java.util.Random;

public class Rock implements Sprite_Splitter {

    Vecc2f centerPos;
    int width, height;
    Random random = new Random();
    String name;
    ArrayList<Rock_Part> rock_parts = new ArrayList<>();
    String type;
    int i, j;
    float scaleX, scaleY;
    float sheetScale;
    boolean markedDelete;

    public Rock(int positionX, int positionY, String type, String name, float sheetScale, int width, int height, int borderX, int borderY, float scaleX, float scaleY) {
        this.type = type;
        this.name = name;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.sheetScale = sheetScale;
        this.width = width;
        this.height = height;

        i = Integer.parseInt(type.substring(0, type.length() - 2));
        j = Integer.parseInt(type.substring(2));
        //rock_parts = new Rock_Part[i][j];

        switch (this.type) {
            case "1x1":
                rock_parts.add(new Rock_Part(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, 0, 0, borderX, borderY, random.nextInt(3) * width, 0));
                rock_parts.get(rock_parts.size() - 1).single = true;
                break;
            case "1x2":
                createRock(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, borderX, borderY, 0, 7 * height);
                break;
            case "2x1":
                createRock(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, borderX, borderY, 0, 5 * height);
                break;
            case "2x2":
                createRock(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, borderX, borderY, 0, 3 * height);
                break;
        }
    }

    private void createRock(String name, int positionX, int positionY, int width, int height, float sheetScale, float scaleX, float scaleY, int borderX, int borderY, int startX, int startY) {
        for (int k = 0; k < i; k++) {
            for (int l = 0; l < j; l++) {
                rock_parts.add(new Rock_Part(name, positionX, positionY, width, height, sheetScale, scaleX, scaleY, k, l, borderX, borderY, startX, startY));
                startX = startX + width;
            }
            startX = 0;
            startY = startY + height;
        }
    }

    public void load(Group group) {
        for (Rock_Part rock_part : rock_parts) {
            rock_part.load(group);
        }
    }

    public void unload(Group group) {
        for (Rock_Part rock_part : rock_parts) {
            rock_part.unload(group);
        }
    }

    public void blowUp(Group group, int partIndex) {
        rock_parts.get(partIndex).unload(group);
        rock_parts.get(partIndex).markedDelete=true;
        for (Rock_Part rock_part : rock_parts) {
            if (rock_part.single && !rock_part.markedDelete) {
                rock_part.setAsSingle(name, random.nextInt(3) * width, 0, scaleX, scaleY, sheetScale);
                rock_part.single = true;
            }
        }
    }

    public Vecc2f getCenterPos() {
        return centerPos;
    }

    public ArrayList<Rectangle> getBoundaries() {
        ArrayList<Rectangle> arrayList = new ArrayList<>();
        for (Rock_Part rock_part : rock_parts) {
            arrayList.add((Rectangle) rock_part.hitbox.getShape());
        }
        return arrayList;
    }

    public void check(Group group, float x, float y, float radius) {
        for (int k = 0; k < rock_parts.size(); k++) {
            if ((Vecc2f.distance(x, y, rock_parts.get(k).centerPos.x, rock_parts.get(k).centerPos.y) < radius)) {
                blowUp(group, k);
            }
        }
        //removing while in a for causes errors - nice solution
        rock_parts.removeIf(rock_part -> rock_part.markedDelete);
    }
}