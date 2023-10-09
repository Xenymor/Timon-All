package CodeWars.DigitalRoot;

public class DRoot {
    public static int digital_root(int n) {
        int sum = 0;
        char[] chars = Integer.toString(n).toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sum += Integer.parseInt(String.valueOf(chars[i]));
        }
        while (sum >= 10) {
            chars = Integer.toString(sum).toCharArray();
            sum = 0;
            for (int i = 0; i < chars.length; i++) {
                sum += Integer.parseInt(String.valueOf(chars[i]));
            }
        }
        return sum;
    }
}