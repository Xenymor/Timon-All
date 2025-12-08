package StandardClasses.Vectors;

public class Vector2L {
    private long x;
    private long y;

    public Vector2L(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public Vector2L(final Vector2L other) {
        this.x = other.x;
        this.y = other.y;
    }

    public double getDist(Vector2L o) {
        return Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(y - o.y, 2));
    }

    public Vector2L getDir(Vector2L o) {
        return new Vector2L(x - o.x, y - o.y);
    }

    public Vector2L rounded() {
        return new Vector2L(Math.round(x), Math.round(y));
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2L vector2L = (Vector2L) o;
        return x == vector2L.x && y == vector2L.y;
    }

    public void add(Vector2L dir) {
        this.x += dir.x;
        this.y += dir.y;
    }

    @Override
    public String toString() {
        return "Vector2L{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
