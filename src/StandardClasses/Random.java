package StandardClasses;

import java.util.ArrayList;

public class Random {
    public static int randomIntInRange(int min, int max) {
        return (int) (min + Math.random() * (max - min));
    }

    public static float randomFloatInRange(float min, float max) {
        return (float) (min + Math.random() * (max - min));
    }

    public static double randomDoubleInRange(double min, double max) {
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

    public static boolean chanceOf(final double chance) {
        return Math.random() <= chance;
    }

    public static int randomIntInRange(final int max) {
        return (int) (Math.random()*max);
    }

    /**
     * Returns the index of a random element, with a chance proportional to its value.
     * @param chances All values must be positive.
     * @return Index of the chosen element.
     */
    public static int pickElementProportionalToValue(final int[] chances) {
        int[] temp = new int[chances.length];
        int sum = 0;
        for (int i = 0; i < chances.length; i++) {
            sum += chances[i];
            temp[i] = sum;
        }
        int pick = randomIntInRange(sum);
        for (int i = chances.length - 1; i >= 0; i--) {
            if (pick > temp[i]) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Returns the index of a random element, with a chance proportional to its value.
     * @param chances All values must be positive.
     * @return Index of the chosen element.
     */
    public static int pickElementProportionalToValue(final double[] chances) {
        double[] temp = new double[chances.length];
        double sum = 0;
        for (int i = 0; i < chances.length; i++) {
            sum += chances[i];
            temp[i] = sum;
        }
        double pick = randomDoubleInRange(sum);
        for (int i = chances.length - 1; i >= 0; i--) {
            if (pick > temp[i]) {
                return i;
            }
        }
        return 0;
    }
}
