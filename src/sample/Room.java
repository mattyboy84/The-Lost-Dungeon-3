package sample;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

public class Room {

    Random random = new Random();

    int i;
    int j;
    int type;
    int floorLevel;


    public Room(int i, int j, int type, int floorLevel) {
        this.i = i;
        this.j = j;
        this.type = type;
        this.floorLevel = floorLevel;

        JsonObject jsonObject = null;


            jsonObject = new JsonParser().parse(String.valueOf(templateGetter())).getAsJsonObject();
        System.out.println(jsonObject);


    }

    private StringBuilder templateGetter() {
        StringBuilder json = new StringBuilder();

        File directPath = new File("src\\room templates\\Floor-" + this.floorLevel + "\\Type-" + this.type);
        //System.out.println(directPath);
        String[] contents = directPath.list();
        //System.out.println(contents.length);
        String room = null;
        if (contents != null) {
            room = contents[random.nextInt(contents.length)];
        }
        try {
            File file = new File("src\\room templates\\Floor-" + this.floorLevel + "\\Type-" + this.type + "\\"+room);
            //System.out.println(file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                json.append(st);
                //System.out.println(st);
            }

            //System.out.println(json);
        } catch (Exception e) {

        }
        //System.out.println(json);
        return json;
    }


}
