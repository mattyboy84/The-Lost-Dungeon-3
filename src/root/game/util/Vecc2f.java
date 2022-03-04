package root.game.util;

import javafx.geometry.NodeOrientation;

import java.util.Random;

/**
 * A 2-dimensional, single-precision, floating-point vector.
 */
public class Vecc2f {
    /**
     * The x coordinate.
     */
    public float x;

    /**
     * The y coordinate.
     */
    public float y;
    /**
     * Random
     */
    Random random = new Random();

    public Vecc2f() {
    }

    public Vecc2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vecc2f(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Vecc2f(Vecc2f v) {
        this.x = v.x;
        this.y = v.y;
    }


    /**
     * Sets the location of this <code>Vec2f</code> to the same
     * coordinates as the specified <code>Vec2f</code> object.
     *
     * @param v the specified <code>Vec2f</code> to which to set
     *          this <code>Vec2f</code>
     */
    public void set(Vecc2f v) {
        this.x = v.x;
        this.y = v.y;
    }

    /**
     * Sets the location of this <code>Vec2f</code> to the
     * specified <code>float</code> coordinates.
     *
     * @param x the new X coordinate of this {@code Vec2f}
     * @param y the new Y coordinate of this {@code Vec2f}
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(double centerX, double centerY) {
        this.x = (float) centerX;
        this.y = (float) centerY;

    }

    public void set(int centerX, int centerY) {
        this.x = (float) centerX;
        this.y = (float) centerY;

    }

    /**
     * Returns the square of the distance between two points.
     *
     * @param x1 the X coordinate of the first specified point
     * @param y1 the Y coordinate of the first specified point
     * @param x2 the X coordinate of the second specified point
     * @param y2 the Y coordinate of the second specified point
     * @return the square of the distance between the two
     * sets of specified coordinates.
     */
    public static float distanceSq(float x1, float y1, float x2, float y2) {
        x1 -= x2;
        y1 -= y2;
        return (x1 * x1 + y1 * y1);
    }

    /**
     * Returns the distance between two points.
     *
     * @param x1 the X coordinate of the first specified point
     * @param y1 the Y coordinate of the first specified point
     * @param x2 the X coordinate of the second specified point
     * @param y2 the Y coordinate of the second specified point
     * @return the distance between the two sets of specified
     * coordinates.
     */
    public static float distance(float x1, float y1, float x2, float y2) {
        x1 -= x2;
        y1 -= y2;
        return (float) Math.sqrt(x1 * x1 + y1 * y1);
    }

    public Vecc2f limit(float limit) {
        if (limit < magnitude()) {
            float constVar = limit / magnitude();
            this.x = this.x * constVar;
            this.y = this.y * constVar;
        }
        return new Vecc2f(x, y);
    }

    public Vecc2f limit(double limit) {
        if (limit < magnitude()) {
            double constVar = limit / magnitude();
            this.x = (float) (this.x * constVar);
            this.y = (float) (this.y * constVar);
        }
        return new Vecc2f(x, y);
    }

    public Vecc2f add(Vecc2f vec) {
        this.x = this.x + vec.x;
        this.y = this.y + vec.y;
        return new Vecc2f(this.x, this.y);
    }

    public Vecc2f add(float x, float y) {
        this.x = this.x + x;
        this.y = this.y + y;
        return new Vecc2f(this.x, this.y);
    }

    public Vecc2f add(double x, double y) {
        this.x = (float) (this.x + x);
        this.y = (float) (this.y + y);
        return new Vecc2f(this.x, this.y);
    }

    public Vecc2f sub(Vecc2f vec) {
        this.x = this.x - vec.x;
        this.y = this.y - vec.y;
        return new Vecc2f(this.x, this.y);
    }

    public Vecc2f sub(float x, float y) {
        this.x = this.x - x;
        this.y = this.y - y;
        return new Vecc2f(this.x, this.y);
    }

    public Vecc2f sub(double x, double y) {
        this.x = (float) (this.x - x);
        this.y = (float) (this.y - y);
        return new Vecc2f(this.x, this.y);
    }

    public Vecc2f sub(Vecc2f v1, Vecc2f v2) {
        return sub(v1, v2, null);
    }

    static public Vecc2f sub(Vecc2f v1, Vecc2f v2, Vecc2f target) {
        if (target == null) {
            target = new Vecc2f(v1.x - v2.x, v1.y - v2.y);
        } else {
            target.set(v1.x - v2.x, v1.y - v2.y);
        }
        return target;
    }

    public Vecc2f mult(float n) {
        this.x = this.x * n;
        this.y = this.y * n;

        return new Vecc2f(x, y);
    }

    public void mult(double n) {
        this.x = (float) (this.x * n);
        this.y = (float) (this.y * n);
    }

    public Vecc2f mult(Vecc2f multVec) {
        this.x *= multVec.x;
        this.y *= multVec.y;

        return new Vecc2f(x, y);
    }

    public void div(float divideBy) {
        this.x = (this.x / divideBy);
        this.y = (this.y / divideBy);
    }

    public void div(double divideBy) {
        this.x = (float) (this.x / divideBy);
        this.y = (float) (this.y / divideBy);
    }

    public void normalise() {
        float m = magnitude();
        if (m != 0 && m != 1) {
            div(m);
        }
    }

    public Vecc2f random2D(int modifier) {
        //unit vector with a magnitude of 1
        this.x = (((random.nextFloat() * 2) - 1) / modifier);
        this.y = (((random.nextFloat() * 2) - 1) / modifier);
        // System.out.println(this.x + "  " + this.y + "  " + magnitude());
        return new Vecc2f(x, y);
    }


    public float magnitude() {
        return (float) Math.sqrt((this.x * this.x) + (this.y * this.y));
    }

    public void setMag(float magnitude) {

        float constA = magnitude / magnitude();
        this.x = this.x * constA;
        this.y = this.y * constA;

    }

    public void setMag(double magnitude) {

        float constA = (float) (magnitude / magnitude());
        this.x = this.x * constA;
        this.y = this.y * constA;

    }

    /**
     * Returns the square of the distance from this
     * <code>Vec2f</code> to a specified point.
     *
     * @param vx the X coordinate of the specified point to be measured
     *           against this <code>Vec2f</code>
     * @param vy the Y coordinate of the specified point to be measured
     *           against this <code>Vec2f</code>
     * @return the square of the distance between this
     * <code>Vec2f</code> and the specified point.
     */
    public float distanceSq(float vx, float vy) {
        vx -= x;
        vy -= y;
        return (vx * vx + vy * vy);
    }

    /**
     * Returns the square of the distance from this
     * <code>Vec2f</code> to a specified <code>Vec2f</code>.
     *
     * @param v the specified point to be measured
     *          against this <code>Vec2f</code>
     * @return the square of the distance between this
     * <code>Vec2f</code> to a specified <code>Vec2f</code>.
     */
    public float distanceSq(Vecc2f v) {
        float vx = v.x - this.x;
        float vy = v.y - this.y;
        return (vx * vx + vy * vy);
    }

    /**
     * Returns the distance from this <code>Vec2f</code> to
     * a specified point.
     *
     * @param vx the X coordinate of the specified point to be measured
     *           against this <code>Vec2f</code>
     * @param vy the Y coordinate of the specified point to be measured
     *           against this <code>Vec2f</code>
     * @return the distance between this <code>Vec2f</code>
     * and a specified point.
     */
    public float distance(float vx, float vy) {
        vx -= x;
        vy -= y;
        return (float) Math.sqrt(vx * vx + vy * vy);
    }

    /**
     * Returns the distance from this <code>Vec2f</code> to a
     * specified <code>Vec2f</code>.
     *
     * @param v the specified point to be measured
     *          against this <code>Vec2f</code>
     * @return the distance between this <code>Vec2f</code> and
     * the specified <code>Vec2f</code>.
     */
    public float distance(Vecc2f v) {
        float vx = v.x - this.x;
        float vy = v.y - this.y;
        return (float) Math.sqrt(vx * vx + vy * vy);
    }

    /**
     * Returns the hashcode for this <code>Vec2f</code>.
     *
     * @return a hash code for this <code>Vec2f</code>.
     */
    @Override
    public int hashCode() {
        int bits = 7;
        bits = 31 * bits + Float.floatToIntBits(x);
        bits = 31 * bits + Float.floatToIntBits(y);
        return bits;
    }

    /**
     * Determines whether or not two 2D points or vectors are equal.
     * Two instances of <code>Vec2f</code> are equal if the values of their
     * <code>x</code> and <code>y</code> member fields, representing
     * their position in the coordinate space, are the same.
     *
     * @param obj an object to be compared with this <code>Vec2f</code>
     * @return <code>true</code> if the object to be compared is
     * an instance of <code>Vec2f</code> and has
     * the same values; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Vecc2f v) {
            return (x == v.x) && (y == v.y);
        }
        return false;
    }

    /**
     * Returns a <code>String</code> that represents the value
     * of this <code>Vec2f</code>.
     *
     * @return a string representation of this <code>Vec2f</code>.
     */
    @Override
    public String toString() {
        return "Vec2f[" + x + ", " + y + "]";
    }

    public Vecc2f fromAngle(double v) {
        float a = magnitude();
        this.x = (float) Math.cos(Math.toRadians(v)) * a;
        this.y = (float) Math.sin(Math.toRadians(v)) * a;

        return new Vecc2f(x, y);
    }

    public float toAngle() {
        if (x == 0) // special cases
            return (y > 0) ? 90 : (y == 0) ? 0 : 270;
        else if (y == 0) // special cases
            return (x >= 0) ? 0 : 180;
        float ret = (float) Math.toDegrees((Math.atan((float) y / x)));
        if (x < 0 && y < 0) // quadrant Ⅲ
            ret = 180 + ret;
        else if (x < 0) // quadrant Ⅱ
            ret = 180 + ret; // it actually subtracts
        else if (y < 0) // quadrant Ⅳ
            ret = 270 + (90 + ret); // it actually subtracts
        return ret;
    }
}