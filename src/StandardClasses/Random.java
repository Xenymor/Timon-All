package StandardClasses;

public class Random {
    public static int randomIntInRange(int min, int max) {
        return (int) Math.round(min + Math.random() * (max - min));
    }

    public static float randomFloatInRange(float min, float max) {
        return (float) (min + Math.random() * (max - min));
    }

    public static double randomDoubleInRange(float min, float max) {
        return (min + Math.random() * (max - min));
    }

    public static float randomFloatInRange(float max) {
        return (float) (Math.random() * max);
    }

    public static double randomDoubleInRange(double max) {
        return Math.random() * max;
    }
}
