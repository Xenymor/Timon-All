package NeuralNetworkProjects.PrisonersDilemma;

import NeuralNetworkProjects.PrisonersDilemma.Strategies.*;
import StandardClasses.MyArrays;

import java.util.Arrays;
import java.util.stream.IntStream;

public class PrisonersDilemma {
    private final Integer[][] rewards;

    public PrisonersDilemma(Integer[]... rewards) {
        this.rewards = rewards;
    }

    public static void main(String[] args) {
        final PrisonersDilemma dilemma = new PrisonersDilemma(new Integer[]{3, 3}, new Integer[]{5, 0}, new Integer[]{1, 1});
        printRuleset(dilemma);
        final Strategy[] strategies = {new RandomStrategy(), new AlwaysChooseStrategy(true), new AlwaysChooseStrategy(false), new TitForTat(), new TitForTwoTat(), new GrudgeStrategy(), new AnikaStrategy(), new AnalyzerStrategy()};
        Integer[][][][] results = dilemma.doTournament(200, 5, strategies);
        printResults(results, strategies);
    }

    public static void printResults(final Integer[][][][] originalResults, final Strategy[] ogStrats) {
        Integer[] ints = new Integer[ogStrats.length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = i;
        }
        ints = IntStream.range(0, ints.length)
                .boxed()
                .sorted((a, b) -> Float.compare(getAvgScore(a, originalResults), getAvgScore(b, originalResults)))
                .toList().toArray(new Integer[0]);
        Strategy[] strategies = ogStrats.clone();
        strategies = MyArrays.resort(strategies, ints);
        Integer[][][][] results = MyArrays.resort(originalResults, ints);
        for (int i = 0; i < results.length; i++) {
            results[i] = MyArrays.resort(results[i], ints);
        }
        StringBuilder msg = new StringBuilder();
        final String str = "\n";
        final String str1 = " \t";
        final String str2 = ":\t";
        final String str3 = ")";
        final String str4 = "(";
        for (int i = 0; i < results.length; i++) {
            final Integer[][][] result = results[i];
            msg.append(strategies[i].getName()).append(str4).append(getAvgScore(i, results)).append(str3).append(str2).append(str);
            for (int j = 0; j < result.length; j++) {
                final Integer[][] localInts = result[j];
                msg.append(str1).append(strategies[j].getName()).append(str2).append(Arrays.deepToString(localInts)).append(str);
            }
            msg.append(str);
        }
        System.out.print(msg);
    }

    public static float getAvgScore(final int index, final Integer[][][][] results) {
        int[] scores = getScores(index, results);
        int sum = 0;
        for (final Integer score : scores) {
            sum += score;
        }
        return sum / (float) scores.length;
    }

    public static int[] getScores(final int index, final Integer[][][][] results) {
        int[] scores = new int[results.length];
        final Integer[][][] result = results[index];
        for (final Integer[][] ints : result) {
            for (int j = 0; j < ints.length; j++) {
                final Integer[] anInt = ints[j];
                scores[j] += anInt[0];
            }
        }
        return scores;
    }

    public static Integer[] getScores(final Integer[][][] results) {
        Integer[] scores = new Integer[results.length];
        for (final Integer[][] ints : results) {
            for (int j = 0; j < ints.length; j++) {
                final Integer[] anInt = ints[j];
                scores[j] += anInt[0];
            }
        }
        return scores;
    }

    public Integer[][][][] doTournament(final int gameCount, final int matchCount, final Strategy... strategies) {
        Integer[][][][] results = new Integer[strategies.length][strategies.length][][];
        for (int i = 0; i < strategies.length; i++) {
            final Strategy strategy1 = strategies[i];
            for (int j = 0; j < strategies.length; j++) {
                if (j >= i) {
                    Strategy strategy2 = strategies[j];
                    if (strategy2.equals(strategy1)) {
                        strategy2 = strategy2.clone();
                    }
                    results[i][j] = matchStrategies(strategy1, strategy2, gameCount, matchCount);
                    results[j][i] = MyArrays.reverseInner(results[i][j]);
                }
            }
        }
        return results;
    }

    public Integer[][] matchStrategies(Strategy strategy1, Strategy strategy2, int gameCount, int matchCount) {
        Integer[][] record = new Integer[matchCount][2];
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
