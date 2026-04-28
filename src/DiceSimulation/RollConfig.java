package DiceSimulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RollConfig {
    List<Dice> dice;

    public RollConfig(String s) {
        dice = new ArrayList<>();

        if (s.charAt(s.length()-1) != '.') {
            s += ".";
        }
        char[] charArray = s.toCharArray();
        int sign = 1;
        int diceCount = -1;
        int diceSize = -1;
        int advantage = -2;
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (Character.isWhitespace(c) || Character.isAlphabetic(c)) {
                continue;
            }
            if (Character.isDigit(c)) {
                int digitCount = getDigitCount(i, charArray);
                int number = Integer.parseInt(s.substring(i, i + digitCount));
                if (diceCount == -1) {
                    diceCount = number;
                } else if (diceSize == -1) {
                    diceSize = number;
                }
                i += digitCount - 1;
            }
            if (c == '+') {
                sign = 1;
            }
            if (c == '-') {
                sign = -1;
            }
            if (c == '\'') {
                advantage = 1;
            }
            if (c == ',') {
                advantage = -1;
            }
            if (c == '.') {
                //If only modifier
                if (diceSize == -1) {
                    sign *= diceCount;
                    diceSize = 1;
                    diceCount = 1;
                    advantage = -2;
                }
                dice.add(new Dice(diceCount, diceSize, advantage, sign));
                diceCount = -1;
                diceSize = -1;
                advantage = -2;
                sign = 1;
            }
        }

    }

    private int getDigitCount(int i, char[] charArray) {
        int digitCount = 0;
        for (int j = i; j < charArray.length; j++) {
            if (Character.isDigit(charArray[j])) {
                digitCount++;
            } else {
                break;
            }
        }
        return digitCount;
    }

    public Map<Integer, Integer> simulate(int trialCount) {
        Map<Integer, Integer> distribution = new HashMap<>();

        for (int i = 0; i < trialCount; i++) {
            int result = roll();
            distribution.put(result, distribution.getOrDefault(result, 0) + 1);
        }

        return distribution;
    }

    private int roll() {
        int total = 0;
        for (Dice d : dice) {
            total += d.roll();
        }
        return total;
    }
}
