package EvolutionalCatchingGame;

public record DoubleInt(double v, int i) implements Comparable<DoubleInt> {

    public double getV() {
        return v;
    }

    public int getI() {
        return i;
    }


    @Override
    public int compareTo(DoubleInt o) {
        return Double.compare(v, o.v);
    }
}
