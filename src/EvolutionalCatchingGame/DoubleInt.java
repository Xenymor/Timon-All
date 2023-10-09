package EvolutionalCatchingGame;

public class DoubleInt implements Comparable {
    private double v;
    private int i;

    public DoubleInt(double v, int i) {
        this.v = v;
        this.i = i;
    }

    public double getV() {
        return v;
    }

    public int getI() {
        return i;
    }


    @Override
    public int compareTo(Object o) {
        if (o.getClass() == getClass()) {
            return Double.compare(v, ((DoubleInt) o).v);
        } else {
            return -1;
        }
    }
}
