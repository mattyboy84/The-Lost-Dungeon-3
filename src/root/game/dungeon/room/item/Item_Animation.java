package root.game.dungeon.room.item;

import javafx.scene.Group;

public interface Item_Animation {

    public abstract void postLoader(Group group);
    public abstract void postUnLoader(Group group);
}
