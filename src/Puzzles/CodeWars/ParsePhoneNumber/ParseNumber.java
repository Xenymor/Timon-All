package Puzzles.CodeWars.ParsePhoneNumber;

public class ParseNumber {
    public static String createPhoneNumber(int[] numbers) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numbers.length; i++) {
            if (i == 0) {
                result.append("(");
            } else if (i == 3) {
                result.append(") ");
            } else if (i == 6) {
                result.append("-");
            }
            result.append(numbers[i]);
        }
        return result.toString();
    }
}
