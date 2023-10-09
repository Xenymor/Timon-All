package CodeWars.FindSmallest;

public class FindSmallest {
    public int findSmallest(int a, int b, int c, int d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    public int findSmallest(int... numbers) {
        int smallest = Integer.MAX_VALUE;
        for (int number : numbers) {
            smallest = Math.min(number, smallest);
        }
        return smallest;
    }

    public int minimum(int a, int b, int c, int d) {
        int smallest = a;
        if (b < smallest) {
            smallest = b;
        }
        if (c < smallest) {
            smallest = c;
        }
        if (d < smallest) {
            smallest = d;
        }
        return smallest;
    }
}
