package NeuralNetworkProjects.PrisonersDilemma;

import NeuralNetworkProjects.PrisonersDilemma.Strategies.*;
import StandardClasses.MyArrays;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record PrisonersDilemma(int[]... rewards) {
    private static final int STRATEGY_COUNT = 100;
    private static final int ITER_COUNT = 100_000;
    private static final int ITER_BETWEEN_RESULTS = 200;
    public static final int MATCH_COUNT = 1;
    public static final int[][] REWARDS = {new int[]{3, 3}, new int[]{5, 0}, new int[]{MATCH_COUNT, MATCH_COUNT}};
    public static final int GAME_COUNT = 1000;
    public static final int THREAD_COUNT = 12;

    public static void main(String[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        final PrisonersDilemma dilemma = new PrisonersDilemma(REWARDS);
        printRuleset(dilemma);
        //final Strategy[] allStrategies = getAllStrategies();
        List<Strategy> result = new ArrayList<>();
        getAllClasses(List.of(new Class[]{lbHistoryGrudgeHolder.class}), result);
        Strategy[] allStrategies = result.toArray(new Strategy[0]);
        System.out.println(allStrategies.length);
        Strategy[] strategies = new Strategy[STRATEGY_COUNT];
        for (int i = 0; i < strategies.length; i++) {
            strategies[i] = getRandomStrategy(allStrategies);
        }
        strategies[0] = new AlwaysChooseStrategy(true);
        strategies[MATCH_COUNT] = new AlwaysChooseStrategy(false);
        strategies[2] = new RandomStrategy();
        strategies[3] = new GrudgeStrategy();
        strategies[4] = new AnalyzerStrategy();
        strategies[5] = new ParamTitForTat(1, 1, 0, 0);
        allStrategies = getAllStrategies();
        System.out.println(allStrategies.length);
        if (new File("").exists()) {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("src/NeuralNetworkProjects/PrisonersDilemma/save.nn"));
            strategies = (Strategy[]) inputStream.readObject();
            inputStream.close();
        }
        for (int iter = 0; iter < ITER_COUNT; iter++) {
            int[][][][] results = dilemma.doTournament(GAME_COUNT, MATCH_COUNT, THREAD_COUNT, strategies);
            int[] ints = new int[strategies.length];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = i;
            }
            float[] scores = new float[strategies.length];
            for (int i = 0; i < scores.length; i++) {
                scores[i] = getAvgScore(i, results);
            }
            final float[] finalScores = scores;
            ints = IntStream.of(ints)
                    .boxed()
                    .sorted((a, b) -> Float.compare(finalScores[a], finalScores[b]))
                    .mapToInt(Integer::intValue)
                    .toArray();
            MyArrays.reverse(ints);
            MyArrays.resort(strategies, ints);
            MyArrays.resort(scores, ints);
            if (iter % ITER_BETWEEN_RESULTS == 0) {
                HashMap<String, Integer> counts = new HashMap<>();
                for (final Strategy strategy : strategies) {
                    final String name = strategy.getName();
                    if (counts.containsKey(name)) {
                        counts.put(name, counts.get(name) + 1);
                    } else {
                        counts.put(name, 1);
                    }
                }
                for (String name : counts.keySet()) {
                    System.out.append(name).append(" : ").append(String.valueOf(counts.get(name))).append("\n");
                }
                System.out.println("-----------------------------------------");
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("src/NeuralNetworkProjects/PrisonersDilemma/save.nn"));
                outputStream.writeObject(strategies);
                outputStream.close();
            }
            final int i1 = (int) (strategies.length * 0.3);
            for (int i = i1; i < strategies.length; i++) {
                strategies[i] = getRandomStrategy(allStrategies);
            }
        }
        //printTournamentResults(dilemma, allStrategies);
    }

    private static Strategy getRandomStrategy(final Strategy[] strategies, final int i1) {
        return strategies[(int) (Math.random() * i1)].clone();
    }

    private static Strategy getRandomStrategy(final Strategy[] allStrategies) {
        return allStrategies[(int) (Math.random() * allStrategies.length)].clone();
    }

    private static void printTournamentResults(final PrisonersDilemma dilemma, final Strategy[] strategies) {
        System.out.println(strategies.length + " strategies");
        int[][][][] results = dilemma.doTournament(GAME_COUNT, 5, THREAD_COUNT, strategies);
        //printResults(results, strategies);
        printRanking(results, strategies);
    }

    private static Strategy[] getAllStrategies() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Collection<Class> strategyClasses = getAllClasses("NeuralNetworkProjects.PrisonersDilemma.Strategies");
        List<Strategy> strategiesList = new ArrayList<>();
        getAllClasses(strategyClasses, strategiesList);
        //final Strategy[] strategies = {new RandomStrategy(), new AlwaysChooseStrategy(true), new AlwaysChooseStrategy(false), new TitForTat(), new TitForTwoTat(), new GrudgeStrategy(), new AnikaStrategy(), new AnalyzerStrategy()};
        return strategiesList.toArray(new Strategy[0]);
    }

    private static void getAllClasses(final Collection<Class> strategyClasses, final List<Strategy> strategiesList) throws InstantiationException, IllegalAccessException, InvocationTargetException {
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
        MyArrays.reverse(ints);
        strategies = MyArrays.resort(strategies, ints);
        scores = MyArrays.resort(scores, ints);
        final int length = strategies.length;
        for (int i = 0; i < length / 10; i++) {
            System.out.println((i + MATCH_COUNT) + ". " + strategies[i].getName() + "\t" + scores[i]);
        }
        System.out.println("...");
        final int i = length - MATCH_COUNT;
        System.out.println(length + "." + strategies[i].getName() + "\t" + scores[i]);
        System.out.println("-------------------------------------------------");
    }

    private static List<List<Object>> getAllParameterCombinations(final Class<?>[] parameterTypes, final int i, final ArrayList<Object> currentParameters, final List<List<Object>> result) {
        if (i == parameterTypes.length) {
            result.add(new ArrayList<>(currentParameters));
        } else {
            switch (parameterTypes[i].getName()) {
                case "boolean" -> {
                    currentParameters.add(true);
                    getAllParameterCombinations(parameterTypes, i + MATCH_COUNT, currentParameters, result);
                    currentParameters.removeLast(); // backtrack
                    currentParameters.add(false);
                    getAllParameterCombinations(parameterTypes, i + MATCH_COUNT, currentParameters, result);
                    currentParameters.removeLast(); // backtrack
                }
                case "int" -> {
                    for (int j = 0; j < 100; j += 1) {
                        currentParameters.add(j);
                        getAllParameterCombinations(parameterTypes, i + MATCH_COUNT, currentParameters, result);
                        currentParameters.removeLast(); // backtrack
                    }
                }
                case "double" -> {
                    for (double j = 0.01; j < MATCH_COUNT; j *= 1.5) {
                        currentParameters.add(j);
                        getAllParameterCombinations(parameterTypes, i + MATCH_COUNT, currentParameters, result);
                        currentParameters.removeLast(); // backtrack
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
        MyArrays.reverse(ints);
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

    public int[][][][] doTournament(final int gameCount, final int matchCount, final int threadCount, final Strategy... strategies) {
        int[][][][] results = new int[strategies.length][strategies.length][][];
        List<Thread> threads = new ArrayList<>();
        for (int k = 0; k < threadCount; k++) {
            final int finalK = k;
            final Thread thread = new Thread(() -> {
                for (int i = 0; i < strategies.length; i++) {
                    if (i % threadCount == finalK) {
                        final Strategy strategy1 = strategies[i].clone();
                        for (int j = 0; j < strategies.length; j++) {
                            if (j >= i) {
                                Strategy strategy2 = strategies[j].clone();
                                results[i][j] = matchStrategies(strategy1, strategy2, gameCount, matchCount);
                                results[j][i] = MyArrays.deepClone(results[i][j]);
                                MyArrays.reverseInner(results[j][i]);
                            }
                        }
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }
        while (true) {
            if (allFinished(threads)) {
                break;
            }
        }
        return results;
    }

    private boolean allFinished(final List<Thread> threads) {
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                return false;
            }
        }
        return true;
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
            record[matchCounter][MATCH_COUNT] = strategy2.getRewardSum();
        }
        return record;
    }

    public static void printRuleset(final PrisonersDilemma dilemma) {
        for (int i = 0; i < 4; i++) {
            boolean input1 = (i + MATCH_COUNT) % 2 == 0;
            boolean input2 = i / 2 > 0;
            System.out.println("1: " + input1 + " 2: " + input2 + ";\tOutcome: " + dilemma.getRewards(input1, input2) + "; " + dilemma.getRewards(input2, input1));
        }
    }

    public int getRewards(boolean coop, boolean otherCoop) {
        if (coop) {
            if (otherCoop) {
                return rewards[0][0];
            } else {
                return rewards[MATCH_COUNT][MATCH_COUNT];
            }
        } else {
            if (otherCoop) {
                return rewards[MATCH_COUNT][0];
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
