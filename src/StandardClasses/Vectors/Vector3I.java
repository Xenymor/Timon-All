package StandardClasses.Vectors;

import java.util.Objects;

public class Vector3I {
    private int x;
    private int y;
    private int z;

    public Vector3I(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3I(final Vector3I other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public double getDist(Vector3I o) {
        return Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(y - o.y, 2) + Math.pow(z - o.z, 2));
    }

    public Vector3I getDir(Vector3I o) {
        return new Vector3I(x - o.x, y - o.y, z - o.z);
    }

    public Vector3I rounded() {
        return new Vector3I(x, y, z);
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

    public int getZ() {
        return z;
    }

    public void setZ(final int z) {
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3I Vector3I = (Vector3I) o;
        return x == Vector3I.x && y == Vector3I.y && z == Vector3I.z;
    }

    public void add(Vector3I dir) {
        this.x += dir.x;
        this.y += dir.y;
        this.z += dir.z;
    }

    @Override
    public String toString() {
        return "Vector3I{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}

