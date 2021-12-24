package root.game.util;

public enum ViewOrder {
    background_layer(0),          //Background 0
    props_layer(-1),              //props Layer (all debris and enemy remains/blood) & rocks -1
    props_above_layer(-2),        //ground insects/moving background objects -2
    door_layer(-3),               //Door part layer -3
    items_layer(-4),              //Items Layer (Coins, Bombs Keys etc) -4
    enemy_boss_layer(-5),         //Enemy/Boss Layer -5
    enemy_boss_attacks_layer(-6), //Enemy/Boss Tears/Attacks -6
    player_layer(-7),             //Player Layer -7
    player_attacks_layer(-8),     //Player Tears/Attacks -8
    foreground_entities_layer(-9),//foreground insects (tiny flies etc) -9
    shading_layer(-10),           //Shading layer -10
    UI_layer(-11),                //UI Layer (Health, coins/keys/bombs counter,dungeon map,overlay text) -11
    UI_above_layer(-12);          //UI Map overlay border (appears on top of minimap) -12

    private final int viewOrder;

    ViewOrder(int i) {
        this.viewOrder = i;
    }

    public int getViewOrder(){
        return this.viewOrder;
    }
}