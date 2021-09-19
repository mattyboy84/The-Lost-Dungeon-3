package sample;

import javafx.geometry.Rectangle2D;

import java.util.ArrayList;

class RoomThread implements Runnable {

    private Thread t;
    private final String threadName;

    ArrayList<Room> rooms;
    int i, j, i1, up, down, left, right, floorLevel;
    float scaleX, scaleY;
    Rectangle2D screenbounds;


    public RoomThread(ArrayList<Room> rooms1, int i, int j, int i1, int up, int down, int left, int right, int floorLevel, float scaleX, float scaleY, Rectangle2D screenBounds, String name) {
        this.rooms = rooms1;
        this.i = i;
        this.j = j;
        this.i1 = i1;
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.floorLevel = floorLevel;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.screenbounds = screenBounds;
        threadName = name;
        //System.out.println("Creating " +  threadName );
    }

    public void run() {
        // System.out.println("Running " +  threadName );

        rooms.add(new Room(i, j, i1, up, down, left, right, this.floorLevel, scaleX, scaleY, screenbounds,threadName));
        //t.stop();
        //System.out.println("Thread " +  threadName + " exiting.");
    }

    public void start() {

        // System.out.println("Starting " +  threadName );
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}