package sample;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;

public class Enemy_attackFly extends  Enemy{


    public Enemy_attackFly(JsonObject enemyTemplate,Vecc2f pos, float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading) {


        super(enemyTemplate,pos,scaleX,scaleY,screenBounds,shading);


    }


}
