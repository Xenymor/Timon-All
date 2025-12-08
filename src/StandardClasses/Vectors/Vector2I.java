package StandardClasses.Vectors;

import java.util.Objects;

public class Vector2I {
    private int x;
    private int y;

    public Vector2I(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2I(final Vector2I other) {
        this.x = other.x;
        this.y = other.y;
    }

    public double getDist(Vector2I o) {
        return Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(y - o.y, 2));
    }

    public Vector2I getDir(Vector2I o) {
        return new Vector2I(x - o.x, y - o.y);
    }

    public Vector2I rounded() {
        return new Vector2I(x, y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2I vector2I = (Vector2I) o;
        return x == vector2I.x && y == vector2I.y;
    }

    public void add(Vector2I dir) {
        this.x += dir.x;
        this.y += dir.y;
    }

    @Override
    public String toString() {
        return "Vector2I{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
