package CodeWars;

import java.util.ArrayList;

public class DeadFish {
    public static int[] parse(String data) {
        char[] symbols = data.toCharArray();
        int value = 0;
        ArrayList<Integer> result = new ArrayList<>();
        for (final char symbol : symbols) {
            switch (symbol) {
                case ('i') -> value++;
                case ('d') -> value--;
                case ('s') -> value *= value;
                case ('o') -> result.add(value);
            }
        }
        int[] resultArr = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            resultArr[i] = result.get(i);
        }
        return resultArr;
    }
}
