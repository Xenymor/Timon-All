package CodeWars;

import java.util.Arrays;

public class CoinThrowing {
    private static final int THROW_COUNT = 1000000;
    private static final long BUDGET = 1000L;
    private static final long STAKE = 20L;
    private static final long GOAL = 1_000_000_000L;
    private static final long TRIES = 20;

    public static void main(String[] args) {
        getMedian(THROW_COUNT);
        for (int stake = 0; stake <= STAKE; stake++) {
            int loseCount = 0;
            int winCount = 0;
            long sum = 0;
            for (int i = 0; i < TRIES; i++) {
                long result = playGameWithBudget(BUDGET, stake, GOAL);
                sum += result;
                if (result >= GOAL) {
                    winCount++;
                } else {
                    loseCount++;
                }
            }
            System.out.println("Stake: " + stake + ", Losses: " + loseCount + ", Wins: " + winCount + ", Average: " + (sum / TRIES));
        }
    }

    private static long playGameWithBudget(final long budget, final long stake, final long goal) {
        long money = budget;
        long gamesPlayed = 0;
        while (money > 0 && money < goal) {
            money -= stake;
            money += playGame();
            gamesPlayed++;
            /*if (gamesPlayed % 100_000_000 == 0) {
                System.out.println("Games played: " + gamesPlayed + ", Money left: " + money);
            }*/
        }
        //System.out.println("Games played: " + gamesPlayed + ", Money left: " + money);
        return money;
    }

    private static void getMedian(int throwCount) {
        long[] results = new long[throwCount];
        for (int i = 0; i < throwCount; i++) {
            results[i] = playGame();
        }
        Arrays.sort(results);
        System.out.println("Median: " + results[throwCount / 2]);
    }

    private static long playGame() {
        long sum = 2;
        while (Math.random() < 0.5) {
            sum *= 2;
        }
        return sum;
    }
}
