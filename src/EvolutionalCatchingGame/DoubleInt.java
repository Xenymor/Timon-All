package EvolutionalCatchingGame;

public record DoubleInt(double v, int i) implements Comparable {

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
