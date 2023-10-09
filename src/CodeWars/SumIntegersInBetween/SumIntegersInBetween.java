package CodeWars.SumIntegersInBetween;

public class SumIntegersInBetween {
    public int GetSumInBetween(int a, int b) {
        int smaller = Math.min(a, b);
        int bigger = Math.max(a, b);
        int sum = 0;
        for (int i = smaller; i <= bigger; i++) {
            sum += i;
        }
        return sum;
    }
}
