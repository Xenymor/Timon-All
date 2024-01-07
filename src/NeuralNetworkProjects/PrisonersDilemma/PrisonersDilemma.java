package NeuralNetworkProjects.PrisonersDilemma;

import NeuralNetworkProjects.PrisonersDilemma.Strategies.Strategy;
import StandardClasses.MyArrays;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrisonersDilemma {
    private final int[][] rewards;

    public PrisonersDilemma(int[]... rewards) {
        this.rewards = rewards;
    }

    public static void main(String[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final PrisonersDilemma dilemma = new PrisonersDilemma(new int[]{3, 3}, new int[]{5, 0}, new int[]{1, 1});
        printRuleset(dilemma);
        Collection<Class> strategyClasses = getAllClasses("NeuralNetworkProjects.PrisonersDilemma.Strategies");
        List<Strategy> strategiesList = new ArrayList<>();
        for (Class foundClass : strategyClasses) {
            if (!foundClass.isInterface()) {
                final Constructor<?>[] constructors = ((Class<Strategy>) foundClass).getConstructors();
                for (Constructor<?> constructor : constructors) {
                    final Class<?>[] parameterTypes = constructor.getParameterTypes();
                    List<List<Object>> allParameters = getAllParameterCombinations(parameterTypes, 0, new ArrayList<>(), new ArrayList<>());
                    for (List<Object> parameters : allParameters) {
                        strategiesList.add((Strategy) constructor.newInstance(parameters.toArray()));
                    }
                }
            }
        }
        //final Strategy[] strategies = {new RandomStrategy(), new AlwaysChooseStrategy(true), new AlwaysChooseStrategy(false), new TitForTat(), new TitForTwoTat(), new GrudgeStrategy(), new AnikaStrategy(), new AnalyzerStrategy()};
        final Strategy[] strategies = strategiesList.toArray(new Strategy[0]);
        int[][][][] results = dilemma.doTournament(200, 5, strategies);
        //printResults(results, strategies);
        printRanking(results, strategies);
    }

    private static void printRanking(final int[][][][] results, final Strategy[] ogStrategies) {
        int[] ints = new int[ogStrategies.length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = i;
        }
        float[] scores = new float[ogStrategies.length];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = getAvgScore(i, results);
        }
        final float[] finalScores = scores;
        ints = IntStream.of(ints)
                .boxed()
                .sorted((a, b) -> Float.compare(finalScores[a], finalScores[b]))
                .mapToInt(Integer::intValue)
                .toArray();
        Strategy[] strategies = ogStrategies.clone();
        strategies = MyArrays.resort(strategies, ints);
        scores = MyArrays.resort(scores, ints);
        strategies = MyArrays.reverse(strategies);
        scores = MyArrays.reverse(scores);
        for (int i = 0; i < strategies.length; i++) {
            System.out.println((i + 1) + ". " + strategies[i].getName() + "\t" + scores[i]);
        }
    }

    private static List<List<Object>> getAllParameterCombinations(final Class<?>[] parameterTypes, final int i, final ArrayList<Object> currentParameters, final List<List<Object>> result) {
        if (i == parameterTypes.length) {
            result.add(new ArrayList<>(currentParameters));
        } else {
            switch (parameterTypes[i].getName()) {
                case "boolean" -> {
                    currentParameters.add(true);
                    getAllParameterCombinations(parameterTypes, i + 1, currentParameters, result);
                    currentParameters.remove(currentParameters.size() - 1); // backtrack
                    currentParameters.add(false);
                    getAllParameterCombinations(parameterTypes, i + 1, currentParameters, result);
                    currentParameters.remove(currentParameters.size() - 1); // backtrack
                }
                case "int" -> {
                    for (int j = 1; j < 10; j++) {
                        currentParameters.add(j);
                        getAllParameterCombinations(parameterTypes, i + 1, currentParameters, result);
                        currentParameters.remove(currentParameters.size() - 1); // backtrack
                    }
                }
                case "double" -> {
                    for (double j = 0.01; j < 0.5; j += 0.05) {
                        currentParameters.add(j);
                        getAllParameterCombinations(parameterTypes, i + 1, currentParameters, result);
                        currentParameters.remove(currentParameters.size() - 1); // backtrack
                    }
                }
                default -> System.out.println("Couldn't get parameter " + parameterTypes[i]);
            }
        }
        return result;
    }


    public static void printResults(final int[][][][] originalResults, final Strategy[] ogStrategies) {
        int[] ints = new int[ogStrategies.length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = i;
        }
        ints = IntStream.of(ints)
                .boxed()
                .sorted((a, b) -> Float.compare(getAvgScore(a, originalResults), getAvgScore(b, originalResults)))
                .mapToInt(Integer::intValue)
                .toArray();
        Strategy[] strategies = ogStrategies.clone();
        strategies = MyArrays.resort(strategies, ints);
        int[][][][] results = MyArrays.resort(originalResults, ints);
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
            final int[][][] result = results[i];
            msg.append(strategies[i].getName()).append(str4).append(getAvgScore(i, results)).append(str3).append(str2).append(str);
            for (int j = 0; j < result.length; j++) {
                final int[][] localInts = result[j];
                msg.append(str1).append(strategies[j].getName()).append(str2).append(Arrays.deepToString(localInts)).append(str);
            }
            msg.append(str);
        }
        System.out.print(msg);
    }

    public static float getAvgScore(final int index, final int[][][][] results) {
        int[] scores = getScores(index, results);
        int sum = 0;
        for (final int score : scores) {
            sum += score;
        }
        return sum / (float) scores.length;
    }

    public static int[] getScores(final int index, final int[][][][] results) {
        int[] scores = new int[results.length];
        final int[][][] result = results[index];
        for (final int[][] ints : result) {
            for (int j = 0; j < ints.length; j++) {
                final int[] anInt = ints[j];
                scores[j] += anInt[0];
            }
        }
        return scores;
    }

    public static int[] getScores(final int[][][] results) {
        int[] scores = new int[results.length];
        for (final int[][] ints : results) {
            for (int j = 0; j < ints.length; j++) {
                final int[] anInt = ints[j];
                scores[j] += anInt[0];
            }
        }
        return scores;
    }

    public int[][][][] doTournament(final int gameCount, final int matchCount, final Strategy... strategies) {
        int[][][][] results = new int[strategies.length][strategies.length][][];
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

    public static Set<Class> getAllClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
