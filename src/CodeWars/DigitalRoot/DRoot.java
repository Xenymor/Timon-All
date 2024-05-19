package CodeWars.DigitalRoot;

public class DRoot {
    public static int digital_root(int n) {
        int sum = 0;
        char[] chars = Integer.toString(n).toCharArray();
        for (final char c : chars) {
            sum += Integer.parseInt(String.valueOf(c));
        }
        while (sum >= 10) {
            chars = Integer.toString(sum).toCharArray();
            sum = 0;
            for (final char aChar : chars) {
                sum += Integer.parseInt(String.valueOf(aChar));
            }
        }
        return sum;
    }
}