package StandardClasses;

import java.io.Serializable;

public class Vector2 implements Serializable {
    double x;
    double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
    }

    public double getDist(Vector2 o) {
        return Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(y - o.y, 2));
    }

    public Vector2 getDir(Vector2 o) {
        return new Vector2(x - o.x, y - o.y);
    }

    public Vector2L rounded() {
        return new Vector2L(Math.round(x), Math.round(y));
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return Double.compare(vector2.x, x) == 0 && Double.compare(vector2.y, y) == 0;
    }

    public void add(Vector2 o) {
        this.x += o.x;
        this.y += o.y;
    }

    public void sub(Vector2 o) {
        this.x -= o.x;
        this.y -= o.y;
    }

    public void clamp(final double length) {
        double currLength = Math.sqrt(x*x+y*y);
        x = (x/(currLength/length));
        y = (y/(currLength/length));
    }
}
