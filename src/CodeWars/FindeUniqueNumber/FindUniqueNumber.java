package CodeWars.FindeUniqueNumber;

public class FindUniqueNumber {
    public static double findUnique(double[] arr) {
        double firstVal = arr[0];
        Double secondVal = null;
        for (int i = 1; i < arr.length; i++) {
            if (secondVal != null) {
                if (firstVal == arr[i]) {
                    return secondVal;
                } else if (secondVal == arr[i]) {
                    return firstVal;
                }
            }
            if (arr[i] != firstVal) {
                secondVal = arr[i];
            }
        }
        return -1;
    }
}