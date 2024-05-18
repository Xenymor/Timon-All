package StandardClasses;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyArrays {
    public static double[] multiply(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] * b[i];
        }
        return result;
    }

    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public static <T> List<T[]> getChunks(Class<T[]> ctor, T[] array, int chunkSize) {
        List<T[]> outList = new ArrayList<>();
        int counter = 0;
        T[] current = ctor.cast(java.lang.reflect.Array.newInstance(ctor.getComponentType(), chunkSize));
        for (T t : array) {
            if (counter == chunkSize) {
                counter = 0;
                outList.add(current);
                current = ctor.cast(java.lang.reflect.Array.newInstance(ctor.getComponentType(), chunkSize));
            }
            current[counter] = t;
            counter++;
        }
        return outList;
    }

    public static <T> T[][] switchCords(final T[][] toSwitch) {
        T[][] result = toSwitch.clone();
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = toSwitch[j][i];
            }
        }
        return result;
    }

    public static <T> void reverseInner(final T[][] toReverse) {
        for (final T[] ts : toReverse) {
            reverse(ts);
        }
    }

    public static void reverseInner(final int[][] toReverse) {
        for (final int[] ints : toReverse) {
            reverse(ints);
        }
    }

    public static void reverse(final int[] toReverse) {
        for (int i = 0; i < toReverse.length / 2; i++) {
            int temp = toReverse[i];
            toReverse[i] = toReverse[toReverse.length - i - 1];
            toReverse[toReverse.length - i - 1] = temp;
        }
    }

    public static <T> void reverse(final T[] toReverse) {
        for (int i = 0; i < toReverse.length / 2; i++) {
            T temp = toReverse[i];
            toReverse[i] = toReverse[toReverse.length - i - 1];
            toReverse[toReverse.length - i - 1] = temp;
        }
    }

    public static float[] reverse(final float[] toReverse) {
        float[] result = toReverse.clone();
        for (int i = 0; i < toReverse.length / 2; i++) {
            float temp = toReverse[i];
            toReverse[i] = toReverse[toReverse.length - i - 1];
            toReverse[toReverse.length - i - 1] = temp;
        }
        return result;
    }

    public static <T> T[] resort(final T[] toSort, final int[] integers) {
        T[] result = toSort.clone();
        for (int i = 0; i < result.length; i++) {
            result[i] = toSort[integers[i]];
        }
        return result;
    }

    public static int[] resort(final int[] toSort, final int[] integers) {
        int[] result = toSort.clone();
        for (int i = 0; i < result.length; i++) {
            result[i] = toSort[integers[i]];
        }
        return result;
    }

    public static float[] resort(final float[] toSort, final int[] integers) {
        float[] result = toSort.clone();
        for (int i = 0; i < result.length; i++) {
            result[i] = toSort[integers[i]];
        }
        return result;
    }

    public static int[][] deepClone(final int[][] ints) {
        int[][] result = ints.clone();
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].clone();
        }
        return result;
    }

    public static boolean[][] deepClone(final boolean[][] arr) {
        boolean[][] result = arr.clone();
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].clone();
        }
        return result;
    }
}