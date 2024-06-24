package StandardClasses;

public class MyMath {
    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) / (inMax - inMin) * (outMax - outMin) + outMin;
    }
}
