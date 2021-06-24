package sample;

public class Dungeon {
    public Dungeon() {
    }

    int mapX = 19;
    int mapY = 19;
    int[][] map;

    int startX = (mapX + 1) / 2;
    int startY = (mapY + 1) / 2;


    public void Generate() {
        map = new int[19][19];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 0;
            }
        }


    }
}
