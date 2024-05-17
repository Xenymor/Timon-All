package StandardClasses;

import java.util.ArrayList;

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

    public static int[] shuffle(final int[] ints) {
        int[] result = new int[ints.length];
        ArrayList<Integer> availableIndexes = new ArrayList<>();
        for (int i = 0; i < ints.length; i++) {
            availableIndexes.add(i);
        }
        for (int i = 0; i < result.length; i++) {
            final int index = randomIntInRange(0, availableIndexes.size() - 1);
            result[i] = availableIndexes.get(index);
            availableIndexes.remove(index);
        }
        return result;
    }

    public static Integer[] randomIntArray(final int arrLen) {
        Integer[] result = new Integer[arrLen];
        final int max = arrLen * 10;
        for (int i = 0; i < result.length; i++) {
            result[i] = randomIntInRange(0, max);
        }
        return result;
    }
}
