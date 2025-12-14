package Puzzles.AdventOfCode.AOC2025.Day12;

public class Shape {

    private final long[] shape;
    private final Shape next;
    private Shape previous;
    final int area;

    public Shape(final long[] shape) {
        this.shape = shape;
        this.area = countBits(shape);
        this.next = new Shape(rotateClockwise(shape), this, this, 2);
    }

    private Shape(final long[] shape, final Shape prev, final Shape first, final int currIdx) {
        this.shape = shape;
        this.area = countBits(shape);
        this.previous = prev;
        if (currIdx < 4) {
            this.next = new Shape(rotateClockwise(shape), this, first, currIdx + 1);
        } else {
            this.next = first;
            first.previous = this;
        }
    }

    public Shape rotate(final int times) {
        int t = times % 4;
        if (t == 0) {
            return this;
        } else if (t > 0 && t <= 2) {
            return next.rotate(times - 1);
        } else {
            assert t == 3;
            return previous.rotate(0);
        }
    }

    public int getWidth() {
        int width = 0;
        for (long line : shape) {
            width = Math.max(width, Long.SIZE - Long.numberOfLeadingZeros(line));
        }
        return width;
    }

    private int countBits(final long[] shape) {
        int count = 0;
        for (long line : shape) {
            count += Long.bitCount(line);
        }
        return count;
    }

    private long[] rotateClockwise(final long[] shape) {
        int size = shape.length;
        long[] rotated = new long[size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (((shape[y] >> x) & 1) == 1) {
                    rotated[x] |= (1L << (size - 1 - y));
                }
            }
        }
        return rotated;
    }

    public int getHeight() {
        return shape.length;
    }

    public long[] getShape() {
        return shape;
    }
}
