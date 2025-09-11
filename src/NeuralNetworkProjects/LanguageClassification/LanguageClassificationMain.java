package NeuralNetworkProjects.LanguageClassification;

import NeuralNetwork.DataPoint;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.NeuralNetworkType;
import StandardClasses.MyArrays;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class LanguageClassificationMain {
    public static final String SAVE_FILE_PATH = "src/NeuralNetworkProjects/LanguageClassification/neuralNetwork.nn";
    public static final int CHARS_PER_INPUT = 100;
    public static final int CHUNK_SIZE = 200;
    private static final double LEARN_RATE_DECAY = 0.001;
    private static double learnRate = 0.5d;

    public static final String OTHER_COMBO = "\1\7";

    File[] files;
    List<String>[] linesArray;
    HashMap<String, Integer> usedStringCombos;
    private SeldomnessChecker seldomnessChecker;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new LanguageClassificationMain().run();
        //new LanguageClassificationMain().test();
    }

    private void test() throws IOException, ClassNotFoundException {
        setUpFiles();
        usedStringCombos = getCharCombos(getChars());
        NeuralNetwork neuralNetwork = (NeuralNetwork) new ObjectInputStream(new FileInputStream(SAVE_FILE_PATH)).readObject();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter your Sentence!");
            String sentence = scanner.nextLine();
            double[] outputs = neuralNetwork.getOutputs(new LanguageLine(sentence, 0, 0, 0));
            int highestIndex = getHighestIndex(outputs);
            switch (highestIndex) {
                case 0 -> print(outputs, "German", 0);
                case 1 -> print(outputs, "English", 1);
                case 2 -> print(outputs, "Italian", 2);
                case 3 -> print(outputs, "Spanish", 3);
                case 4 -> print(outputs, "French", 4);
            }
        }
    }

    private void print(final double[] outputs, final String s, final int i) {
        System.out.println(s + " (" + getPercentage(outputs[i], outputs) * 100 + "% confidence)");
    }

    private HashMap<String, Integer> getCharCombos(final List<Character> usedChars) {
        HashMap<String, Integer> result = new HashMap<>();

        String current;
        int index = 0;
        char[] arr = new char[2];

        for (Character ch1 : usedChars) {
            for (Character ch2 : usedChars) {
                arr[0] = Character.toLowerCase(ch1);
                arr[1] = Character.toLowerCase(ch2);
                current = new String(arr);
                if (!result.containsKey(current)) {
                    result.put(current, index);
                    index++;
                }
            }
        }

        return result;
    }

    private double getPercentage(double output, double[] outputs) {
        double sum = 0;
        for (double v : outputs) {
            sum += Math.abs(v);
        }
        return Math.abs(output) / sum;
    }

    volatile boolean shouldBreak = false;

    private void run() throws IOException, ClassNotFoundException {
        setUpBreak();
        setUpFiles();
        usedStringCombos = new HashMap<>();
        getChars();
        System.out.println("Got Char Combos");
        printInformation();

        NeuralNetwork neuralNetwork = new NeuralNetwork(NeuralNetworkType.GRADIENT_DESCENT, usedStringCombos.size(), 600, 50, 20, 5);

        File file = new File(SAVE_FILE_PATH);
        if (file.exists()) {
            neuralNetwork = (NeuralNetwork) new ObjectInputStream(new FileInputStream(file.getAbsolutePath())).readObject();
            System.out.println("Loaded Neural Network");
        }
        DataPoint[] dataPoints = getDataPointArray();
        List<DataPoint[]> trainingBatches = MyArrays.getChunks(DataPoint[].class, dataPoints, CHUNK_SIZE);
        long learnings = 0;
        double cost = getOverallCost(trainingBatches, neuralNetwork);
        double lastCost = Double.MAX_VALUE;
        long startingTime = System.nanoTime();
        printState(cost, getPerformance(trainingBatches, neuralNetwork), learnings);
        int counter = 0;
        outer:
        while (true) {
            for (DataPoint[] trainingBatch : trainingBatches) {
                if (System.nanoTime() - startingTime >= TimeUnit.SECONDS.toNanos(60) && learnings > 0) {
                    startingTime = System.nanoTime();
                    //performance = getOverallCost(trainingBatches, neuralNetwork);
                    cost = getOverallCost(trainingBatches, neuralNetwork);
                    printState(cost, getPerformance(trainingBatches, neuralNetwork), learnings);
                    if (shouldBreak) {
                        break outer;
                    }
                }
                neuralNetwork.learn(trainingBatch, learnRate);
                learnings++;
            }
            if ((learnings / trainingBatches.size())%10 == 0) {
                final double overallCost = getOverallCost(trainingBatches, neuralNetwork);
                if (overallCost == lastCost) {
                    counter++;
                    if (counter > 1) {
                        learnRate *= 0.1;
                        counter = 0;
                    }
                } else {
                    counter = 0;
                }
                if (overallCost < lastCost) {
                    saveNeuralNetwork(neuralNetwork);
                }
                lastCost = overallCost;
                System.out.println(overallCost + " after " + learnings + " learnings");
            }
            //learnRate *= 1 - LEARN_RATE_DECAY;
        }
    }

    private void printInformation() {
        List<Map<Integer, Double>> percentageMapList = new ArrayList<>();

        this.seldomnessChecker = new SeldomnessChecker(Arrays.stream(linesArray).flatMap(Collection::stream).toList());

        for (final List<String> strings : linesArray) {
            percentageMapList.add(getCharPercentages(strings));
        }
        StringBuilder msg = new StringBuilder();
        for (String combo : usedStringCombos.keySet().stream().sorted((string1, string2) -> {
            double percentage1 = getPercentage(percentageMapList, string1);
            double percentage2 = getPercentage(percentageMapList, string2);
            return Double.compare(percentage1, percentage2);
        }).toList()) {
            final int index = usedStringCombos.get(combo);
            msg.append(combo).append(" : ");
            for (Map<Integer, Double> integerDoubleMap : percentageMapList) {
                msg.append(integerDoubleMap.get(index)).append(", ");
            }
            msg.append("\n");
            System.out.println(msg);
        }
        System.out.println(usedStringCombos.size());
    }

    private void setUpBreak() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                if (scanner.hasNextLine()) {
                    if (scanner.nextLine().equalsIgnoreCase("exit")) {
                        shouldBreak = true;
                    }
                }
            }
        }).start();
    }

    private double getPercentage(final List<Map<Integer, Double>> percentageMapList, final String string) {
        int index = usedStringCombos.get(seldomnessChecker.isSeldom(string) ? OTHER_COMBO : string);
        double percentage1 = 0;
        for (Map<Integer, Double> integerDoubleMap : percentageMapList) {
            percentage1 += integerDoubleMap.getOrDefault(index, 0d);
        }
        return percentage1;
    }

    private void saveNeuralNetwork(final NeuralNetwork neuralNetwork) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_PATH));
        objectOutputStream.writeObject(neuralNetwork);
        objectOutputStream.close();
        System.out.println("Saved Neural Network");
    }

    private Map<Integer, Double> getCharPercentages(List<String> lines) {
        Map<Integer, Double> percentages = new HashMap<>();
        long total = 0;

        if (!usedStringCombos.containsKey(OTHER_COMBO)) {
            usedStringCombos.put(OTHER_COMBO, usedStringCombos.size());
        }

        char[] arr = new char[2];
        for (String line : lines) {
            char[] lineChars = line.toCharArray();
            for (int i = 0; i < lineChars.length - 1; i++) {
                arr[0] = Character.toLowerCase(lineChars[i]);
                arr[1] = Character.toLowerCase(lineChars[i + 1]);
                final String charCombo = new String(arr);
                int comboIndex;

                if (usedStringCombos.containsKey(charCombo)) {
                    comboIndex = usedStringCombos.get(charCombo);
                } else {
                    if (seldomnessChecker.isSeldom(charCombo)) {
                        comboIndex = usedStringCombos.get(OTHER_COMBO);
                    } else {
                        final int size = usedStringCombos.size();
                        usedStringCombos.put(charCombo, size);
                        comboIndex = size;
                    }
                }
                if (percentages.containsKey(comboIndex)) {
                    percentages.put(comboIndex, percentages.get(comboIndex) + 1);
                } else {
                    percentages.put(comboIndex, 1d);
                }
                total++;
            }
        }
        for (int i = 0; i < percentages.size(); i++) {
            try {
                percentages.put(i, percentages.get(i) / total);
            } catch (NullPointerException e) {
                percentages.put(i, 0d);
            }
        }
        return percentages;
    }


    double[] percentage;

    private double[] getCharPercentage(String line) {
        if (percentage == null) {
            percentage = new double[usedStringCombos.size()];
        }

        long total = 0;
        char[] lineChars = line.toCharArray();

        char[] arr = new char[2];
        for (int i = 0; i < lineChars.length - 1; i++) {
            arr[0] = Character.toLowerCase(lineChars[i]);
            arr[1] = Character.toLowerCase(lineChars[i + 1]);
            String key = new String(arr);
            if (seldomnessChecker.isSeldom(key)) {
                key = OTHER_COMBO;
            }
            final Integer index = usedStringCombos.get(key);
            percentage[index]++;
            total++;
        }

        for (int i = 0; i < percentage.length; i++) {
            percentage[i] /= total;
        }
        return percentage.clone();
    }

    private void printState(double cost, double performance, long learnings) {
        System.out.println("Cost: " + cost + ";\tCorrect Percentage: " + Math.round(performance * 10_000) / 100d + "%\tafter " + learnings + " learnings");
    }

    private double getOverallCost(List<DataPoint[]> trainingBatches, NeuralNetwork neuralNetwork) {
        double cost = 0;
        for (DataPoint[] trainingBatch : trainingBatches) {
            cost += neuralNetwork.getCost(trainingBatch);
        }
        cost /= trainingBatches.size();
        return cost;
    }

    private double getPerformance(List<DataPoint[]> trainingBatches, NeuralNetwork neuralNetwork) {
        int corrects = 0;
        int[] langCorrects = new int[trainingBatches.getFirst()[0].expectedOutputs().length];
        int whole = 0;
        for (DataPoint[] dataPoints : trainingBatches) {
            for (DataPoint dataPoint : dataPoints) {
                double[] outputs = neuralNetwork.getOutputs(dataPoint);
                double[] expectedOutputs = dataPoint.expectedOutputs();
                if (hasSameHighestIndex(outputs, expectedOutputs)) {
                    corrects++;
                    langCorrects[getHighestIndex(expectedOutputs)]++;
                }
                whole++;
            }
        }
        System.out.println("Correct: " + corrects + ";\tWhole: " + whole + ";\t" + Arrays.toString(langCorrects));
        return (double) corrects / (double) whole;
    }

    private boolean hasSameHighestIndex(double[] outputs, double[] expectedOutputs) {
        return getHighestIndex(outputs) == getHighestIndex(expectedOutputs);
    }

    private int getHighestIndex(double[] array) {
        int index = -1;
        double highest = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (highest < array[i]) {
                highest = array[i];
                index = i;
            }
        }
        return index;
    }

    private DataPoint[] getDataPointArray() {
        List<DataPoint> dataPointList = new ArrayList<>();

        for (int i = 0; i < linesArray.length; i++) {
            double[] expectedOutputs = new double[linesArray.length];
            expectedOutputs[i] = 1;
            addDataPoints(dataPointList, linesArray[i], expectedOutputs);
        }

        Collections.shuffle(dataPointList);
        return dataPointList.toArray(new DataPoint[0]);
    }

    private void addDataPoints(final List<DataPoint> dataPointList, final List<String> lines, final double... expectedOutputs) {
        Collections.addAll(dataPointList, getDataPoints(lines, expectedOutputs).toArray(new DataPoint[0]));
    }

    private List<DataPoint> getDataPoints(List<String> lines, double... expectedOutputs) {
        List<DataPoint> dataPoints = new ArrayList<>();
        int offset = 0;
        StringBuilder current;
        for (int i = 0; i + offset < lines.size(); i++) {
            current = new StringBuilder(lines.get(i + offset));
            while (current.length() < CHARS_PER_INPUT) {
                if (i + offset + 1 == lines.size()) {
                    break;
                }
                current.append(" ").append(lines.get(i + 1 + offset));
                offset++;
            }
            dataPoints.add(new LanguageLine(current.toString(), expectedOutputs));
        }
        return dataPoints;
    }

    private List<Character> getChars() throws IOException {
        List<Character> charsList = new ArrayList<>();
        linesArray = new List[files.length];

        for (int i = 0; i < linesArray.length; i++) {
            linesArray[i] = addChars(charsList, Paths.get(files[i].getAbsolutePath()));
        }

        return charsList;
    }

    private List<String> addChars(final List<Character> charsList, final Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        getChars(charsList, lines);
        return lines;
    }

    private void getChars(List<Character> charsList, List<String> lines) {
        for (String line : lines) {
            char[] current = line.toCharArray();
            for (char o : current) {
                if (!charsList.contains(o)) {
                    charsList.add(o);
                }
            }
        }
    }

    private void setUpFiles() {
        files = new File[5];
        files[0] = new File("src/NeuralNetworkProjects/LanguageClassification/TrainingDataEnglish.txt");
        files[1] = new File("src/NeuralNetworkProjects/LanguageClassification/TrainingDataGerman.txt");
        files[2] = new File("src/NeuralNetworkProjects/LanguageClassification/TrainingDataItalian.txt");
        files[3] = new File("src/NeuralNetworkProjects/LanguageClassification/TrainingDataSpanish.txt");
        files[4] = new File("src/NeuralNetworkProjects/LanguageClassification/TrainingDataFrench.txt");
    }

    private class LanguageLine implements DataPoint {
        final String line;
        final double[] inputs;
        final double[] expectedOutputs;

        public LanguageLine(String s, double... ints) {
            expectedOutputs = ints;
            line = s;
            inputs = getCharPercentage(line);
        }

        private double[] getInputs(String s) {
            return getCharPercentage(s);
        }

        @Override
        public double[] inputs() {
            return inputs;
        }

        @Override
        public double[] expectedOutputs() {
            return expectedOutputs;
        }
    }
}