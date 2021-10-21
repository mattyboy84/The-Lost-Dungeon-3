package sample;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.Random;

public class Dungeon {

    ArrayList<Neighbour_Rooms> neighbours = new ArrayList<>();
    ArrayList<Room> rooms = new ArrayList<>();
    //Room Threads
    ArrayList<Room> rooms1 = new ArrayList<>();

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
    int floorLevel;


    int startX;
    int startY;
    Random random = new Random();
    //
    Shading shading;


    /*
    //D:\- JAVA Projects -\- Lost Dungeon -\The-Lost-Dungeon-3\src\resources\gfx\grid
    grid_pit
    props_01_basement
    rocks_basement
    grid_bridge
     */
    int borderBoundary = 1;//creates a safe zone around the dungeon. As 1, Row/Column 0 and length-1 are free of rooms

    public void Generate(int minRooms, int mapXWidth, int mapYWidth, int floorLevel, float scaleX, float scaleY, Rectangle2D screenBounds) {
        this.mapX = mapXWidth;
        this.mapY = mapYWidth;
        //
        map = new int[this.mapX][this.mapY];
        //
        this.startX = (mapX - 1) / 2;
        this.startY = (mapY - 1) / 2;
        this.minimumRooms = minRooms;
        this.floorLevel = floorLevel + 1;

        while ((finRooms < minimumRooms)) {
            mapclearer();
            map[startX][startY] = 1;
            deltaX = 0;
            deltaY = 0;
            finRooms = 0;
            for (int i = 0; i < (int) (this.minimumRooms * 1.2); i++) {
                roomAdder();
            }
            //
            map[this.startX - 1][this.startY] = 1;
            map[this.startX + 1][this.startY] = 1;//TODO Remember this
            map[this.startX][this.startY + 1] = 1;
            map[this.startX][this.startY - 1] = 1;
            //
            for (int[] ints : map) {
                for (int j = 0; j < map[0].length; j++) {
                    if (ints[j] == 1) {
                        finRooms++;
                    }
                }
            }
        }
        //
        this.shading = new Shading(scaleX, scaleY, screenBounds);
        //
        //dungeon complete
        neighbourAdder();

        specialRoomAdder(2);//adds shop
        specialRoomAdder(3);//adds boss

        neighbourCleaner();//removes map border

        System.out.println("Dungeon has: " + finRooms + " Rooms");

        //Image[][] rocks = rockGetter();

        finalDungeonGen(scaleX, scaleY, screenBounds, this.shading);

    }

    private void finalDungeonGen(float scaleX, float scaleY, Rectangle2D screenBounds, Shading shading) {
        int up, down, left, right;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {

                    up = roomChecker(i, j, -1, 0);

                    down = roomChecker(i, j, +1, 0);

                    right = roomChecker(i, j, 0, +1);

                    left = roomChecker(i, j, 0, -1);

                    rooms.add(new Room(i, j, map[i][j], up, down, left, right, this.floorLevel, scaleX, scaleY, screenBounds, String.valueOf(rooms.size()), this.shading));
                    rooms.get(rooms.size() - 1).start();
                }
            }
        }
        int a = 0;
        int increment = 200;
        while (Room.finishedRoom != finRooms) {
            System.out.println(Room.finishedRoom + " " + finRooms);
            try {
                a = a + increment;
                Thread.sleep(increment);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Prolonged time: " + a);
    }

    private int roomChecker(int i, int j, int II, int JJ) {
        try {
            return Math.max(map[i + II][j + JJ], 0);
        } catch (Exception e) {
            return 0;
        }
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
        System.out.println("I down, J across");
        StringBuilder a = new StringBuilder();
        a.append("   ");
        for (int i = 0; i < map.length; i++) {
            a.append(i);
            if (i < 10) {
                a.append("  ");
            } else {
                a.append(" ");
            }
        }
        System.out.println(a);

        for (int i = 0; i < map.length; i++) {
            if (i < 10) {
                System.out.print(i + "  ");
            } else {
                System.out.print(i + " ");
            }
            for (int j = 0; j < map[0].length; j++) {
                    System.out.print(map[i][j] + "  ");
            }
            System.out.println("");
            //System.out.print(i + " ");
        }
        System.out.print("-");//start with 1 to account for I column
        //
        for (int i = 0; i < map.length; i++) {//3 for every unit in map,
            System.out.print("---");
        }
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
            if (i >= borderBoundary && j >= borderBoundary && i < (map.length - borderBoundary) && j < (map.length - borderBoundary)) {
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
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println(e);
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
        if (startX + deltaX >= map.length - borderBoundary || startX + deltaX <= borderBoundary ||
                startY + deltaY >= map.length - borderBoundary || startY + deltaY <= borderBoundary) {
            //System.out.println((startX + deltaX)+"  " + (startY + deltaY));
            deltaX = 0;
            deltaY = 0;
        }
        try {
            switch (random.nextInt(2)) {
                case 0 -> deltaX = deltaX + (random.nextInt(2) * 2) - 1;
                case 1 -> deltaY = deltaY + (random.nextInt(2) * 2) - 1;
            }
            map[startX + deltaX][startY + deltaY] = 1;
            localRooms++;

        } catch (Exception e) {
            System.out.println("Map gen exceeds set boundaries - its become sentient");
        }
    }

    public void loadRoom(int x, int y, Group group) {
        for (Room room : rooms) {
            if (room.getI() == x && room.getJ() == y) {
                room.load(group);
                break;
            }
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

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public int getFloorLevel() {
        return floorLevel;
    }

    public void setFloorLevel(int floorLevel) {
        this.floorLevel = floorLevel;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public int getBorderBoundary() {
        return borderBoundary;
    }

    public void setBorderBoundary(int borderBoundary) {
        this.borderBoundary = borderBoundary;
    }
}