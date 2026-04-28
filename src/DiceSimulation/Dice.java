package DiceSimulation;

public class Dice {

    private final int diceCount;
    private final int diceSize;
    private final boolean advantage;
    private final boolean disadvantage;
    private final int sign;

    public Dice(int diceCount, int diceSize, int advantage, int sign) {
        this.diceCount = diceCount;
        this.diceSize = diceSize;
        this.advantage = advantage == 1;
        this.disadvantage = advantage == -1;
        this.sign = sign;
    }

    public int roll() {
        int total = 0;
        for (int i = 0; i < diceCount; i++) {
            int roll1 = (int) (Math.random() * diceSize) + 1;
            if (advantage || disadvantage) {
                int roll2 = (int) (Math.random() * diceSize) + 1;
                if (advantage) {
                    total += Math.max(roll1, roll2);
                } else {
                    total += Math.min(roll1, roll2);
                }
            } else {
                total += roll1;
            }
        }
        return total * sign;
    }
}
