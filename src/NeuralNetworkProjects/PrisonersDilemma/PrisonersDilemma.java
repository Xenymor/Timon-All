package NeuralNetworkProjects.PrisonersDilemma;

import NeuralNetworkProjects.PrisonersDilemma.Strategies.*;

import java.util.Arrays;

public class PrisonersDilemma {
    private final int[][] rewards;

    public PrisonersDilemma(int[]... rewards) {
        this.rewards = rewards;
    }

    public static void main(String[] args) {
        final PrisonersDilemma dilemma = new PrisonersDilemma(new int[]{3, 3}, new int[]{5, 0}, new int[]{1, 1});
        printRuleset(dilemma);
        final Strategy[] strategies = {new RandomStrategy(), new AlwaysChooseStrategy(true), new AlwaysChooseStrategy(false), new TitForTat(), new TitForTwoTat()};
        int[][][][] results = dilemma.doTournament(200, 5, strategies);
        printResults(results, strategies);
    }

    private static void printResults(final int[][][][] results, final Strategy[] strategies) {
        StringBuilder msg = new StringBuilder();
        final String str = "\n";
        final String str1 = " \t";
        final String str2 = ":\t";
        for (int i = 0; i < results.length; i++) {
            final int[][][] result = results[i];
            msg.append(strategies[i].getName()).append(str2).append(str);
            for (int j = 0; j < result.length; j++) {
                final int[][] ints = result[j];
                msg.append(str1).append(strategies[j].getName()).append(str2).append(Arrays.deepToString(ints)).append(str);
            }
            msg.append(str);
        }
        System.out.print(msg);
    }

    private int[][][][] doTournament(final int gameCount, final int matchCount, final Strategy... strategies) {
        int[][][][] results = new int[strategies.length][strategies.length][][];
        for (int i = 0; i < strategies.length; i++) {
            final Strategy strategy1 = strategies[i];
            for (int j = 0; j < strategies.length; j++) {
                Strategy strategy2 = strategies[j];
                if (strategy2.equals(strategy1)) {
                    strategy2 = strategy2.clone();
                }
                results[i][j] = matchStrategies(strategy1, strategy2, gameCount, matchCount);
            }
        }
        return results;
    }

    public int[][] matchStrategies(Strategy strategy1, Strategy strategy2, int gameCount, int matchCount) {
        int[][] record = new int[matchCount][2];
        for (int matchCounter = 0; matchCounter < matchCount; matchCounter++) {
            strategy1.reset();
            strategy2.reset();
            for (int gameCounter = 0; gameCounter < gameCount; gameCounter++) {
                boolean oneCoop = strategy1.getMove();
                boolean twoCoop = strategy2.getMove();
                strategy1.otherMove(twoCoop);
                strategy2.otherMove(oneCoop);
                strategy1.addReward(getRewards(oneCoop, twoCoop));
                strategy2.addReward(getRewards(twoCoop, oneCoop));
            }
            record[matchCounter][0] = strategy1.getRewardSum();
            record[matchCounter][1] = strategy2.getRewardSum();
        }
        return record;
    }

    public static void printRuleset(final PrisonersDilemma dilemma) {
        for (int i = 0; i < 4; i++) {
            boolean input1 = (i + 1) % 2 == 0;
            boolean input2 = i / 2 > 0;
            System.out.println("1: " + input1 + " 2: " + input2 + ";\tOutcome: " + dilemma.getRewards(input1, input2) + "; " + dilemma.getRewards(input2, input1));
        }
    }

    public int getRewards(boolean coop, boolean otherCoop) {
        if (coop) {
            if (otherCoop) {
                return rewards[0][0];
            } else {
                return rewards[1][1];
            }
        } else {
            if (otherCoop) {
                return rewards[1][0];
            } else {
                return rewards[2][0];
            }
        }
    }
}
