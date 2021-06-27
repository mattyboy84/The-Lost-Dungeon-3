package sample;

import java.util.ArrayList;
import java.util.Random;

public class Dungeon {
    ArrayList<Neighbour_Rooms> neighbours = new ArrayList<Neighbour_Rooms>();

    public Dungeon() {
    }

    int mapX;
    int mapY;
    //
    int deltaX = 0;
    int deltaY = 0;
    //
    int[][] map;
    int localRooms = 0;
    int finRooms = 0;
    int minimumRooms;


    int startX;
    int startY;
    Random random = new Random();

    public void Generate(int minRooms, int mapXWidth, int mapYWidth) {
        map = new int[19][19];
        this.mapX = mapXWidth;
        this.mapY = mapYWidth;
        this.startX = (mapX - 1) / 2;
        this.startY = (mapY - 1) / 2;
        this.minimumRooms = minRooms;


        while (finRooms < minimumRooms) {
            mapclearer();
            map[startX][startY] = 1;
            deltaX = 0;
            deltaY = 0;
            finRooms = 0;
            for (int i = 0; i < (int) (this.minimumRooms * 1.2); i++) {
                roomAdder();
            }
            for (int[] ints : map) {
                for (int j = 0; j < map[0].length; j++) {
                    if (ints[j] == 1) {
                        finRooms++;
                    }
                }
            }

        }
        //dungeon complete
        neighbourAdder();

        specialRoomAdder(2);//adds shop
        specialRoomAdder(3);//adds boss

        neighbourCleaner();//removes map border

        //System.out.println("asd" + neighbours.size());
        //displayMap();
        System.out.println(finRooms);

    }

    private void neighbourCleaner() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 9) {
                    map[i][j] = 0;
                }
            }
        }
    }

    private void specialRoomAdder(int i) {
        int rand = random.nextInt(neighbours.size() - 1);
        map[neighbours.get(rand).getI()][neighbours.get(rand).getJ()] = i;
        neighbourSubAdder(neighbours.get(rand).getI(), neighbours.get(rand).getJ());
        neighbours.remove(rand);
        finRooms++;
    }

    public void displayMap() {
        for (int[] ints : map) {
            for (int j = 0; j < map[0].length; j++) {
                System.out.print(ints[j] + "  ");
            }
            System.out.println("");
        }
        System.out.println("-------------------------------------------------------");
    }

    private void neighbourAdder() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 1) {
                    neighbourSubAdder(i, j);
                }
            }
        }
    }

    private void neighbourSubAdder(int i, int j) {
        try {
            if (map[i + 1][j] == 0) {
                neighbours.add(new Neighbour_Rooms(i + 1, j));
                map[i + 1][j] = 9;
            }
            if (map[i - 1][j] == 0) {
                neighbours.add(new Neighbour_Rooms(i - 1, j));
                map[i - 1][j] = 9;
            }
            if (map[i][j + 1] == 0) {
                neighbours.add(new Neighbour_Rooms(i, j + 1));
                map[i][j + 1] = 9;
            }
            if (map[i][j - 1] == 0) {
                neighbours.add(new Neighbour_Rooms(i, j - 1));
                map[i][j - 1] = 9;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e);
        }


    }

    private void mapclearer() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 0;
            }
        }
    }


    private void roomAdder() {
        if (startX + deltaX >= map.length || startX + deltaX <= 0 ||
                startY + deltaY >= map.length || startY + deltaY <= 0) {
            deltaX = 0;
            deltaY = 0;
        }
        try {
            switch (random.nextInt(2)) {
                case 0:
                    deltaX = deltaX + (random.nextInt(2) * 2) - 1;
                    break;
                case 1:
                    deltaY = deltaY + (random.nextInt(2) * 2) - 1;
                    break;
            }
            map[startX + deltaX][startY + deltaY] = 1;
            localRooms++;

        } catch (Exception e) {
            System.out.println("Map gen exceeds set boundaries - its become sentient");
        }


    }


    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public ArrayList<Neighbour_Rooms> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(ArrayList<Neighbour_Rooms> neighbours) {
        this.neighbours = neighbours;
    }

    public int getMapX() {
        return mapX;
    }

    public void setMapX(int mapX) {
        this.mapX = mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public void setMapY(int mapY) {
        this.mapY = mapY;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }

    public int getLocalRooms() {
        return localRooms;
    }

    public void setLocalRooms(int localRooms) {
        this.localRooms = localRooms;
    }

    public int getFinRooms() {
        return finRooms;
    }

    public void setFinRooms(int finRooms) {
        this.finRooms = finRooms;
    }

    public int getMinimumRooms() {
        return minimumRooms;
    }

    public void setMinimumRooms(int minimumRooms) {
        this.minimumRooms = minimumRooms;
    }
}
