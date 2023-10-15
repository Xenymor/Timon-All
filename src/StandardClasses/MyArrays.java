package StandardClasses;

public class MyArrays {
    public static double[] multiply(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i]*b[i];
        }
        return result;
    }
}