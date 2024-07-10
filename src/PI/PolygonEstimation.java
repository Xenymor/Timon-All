package PI;

public class PolygonEstimation {
    public static void main(String[] args) {
        double diff = Double.POSITIVE_INFINITY;
        final double pow = Math.pow(10, -20);
        long i = 1;
        double x = 0;
        while (diff > pow) {
            x = estimatePi(i);
            diff = Math.abs(Math.PI - x);
            if (i % 10_000 == 0) {
                System.out.println(i + ": " + x);
            }
            i *= 2;
        }
        System.out.println(i + ": " + x);
    }

    private static double estimatePi(final long sideCount) {
        return Math.sin(Math.PI / sideCount) * sideCount;
    }
}
