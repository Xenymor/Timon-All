package CodeWars;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NumberAnd {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a number: ");
        int number = scanner.nextInt();
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < Math.pow(2, getHighestBit(number) + 1); i++) {
            final int result = i & number;
            if (!numbers.contains(result)) {
                numbers.add(result);
            }
            System.out.println(i + " & " + number + " = " + result);
        }
        System.out.println("The unique results are: " + numbers);
    }

    private static int getHighestBit(final int number) {
        int highestBit = 0;
        for (int i = 0; i < 32; i++) {
            if ((number & (1 << i)) != 0) {
                highestBit = i;
            }
        }
        return highestBit;
    }
}
