package sample;

import com.google.gson.JsonObject;
import javafx.geometry.Rectangle2D;

public class Enemy_attackFly extends  Enemy{


    public Enemy_attackFly(JsonObject enemyTemplate, float positionX, float positionY, float scaleX, float scaleY, Rectangle2D screenBounds) {


        super(enemyTemplate,positionX,positionY,scaleX,scaleY,screenBounds);


    }


}
