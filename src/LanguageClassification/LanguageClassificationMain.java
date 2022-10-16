package LanguageClassification;

import NeuralNetwork.DataPoint;
import NeuralNetwork.NeuralNetwork;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class LanguageClassificationMain {
    private static final double LEARN_RATE = 0.2d;
    File english;
    List<String> englishLines;
    File german;
    List<String> germanLines;
    File italian;
    List<String> italianLines;
    List<Character> usedChars;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //new LanguageClassificationMain().test();
        new LanguageClassificationMain().run();
    }

    private void test() throws IOException, ClassNotFoundException {
        loadFiles();
        usedChars = getChars();
        NeuralNetwork neuralNetwork = (NeuralNetwork) new ObjectInputStream(new FileInputStream("src/LanguageClassification/neuralNetwork.nn")).readObject();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter your Sentence!");
            String sentence = scanner.nextLine();
            double[] outputs = neuralNetwork.getOutputs(new LanguageLine(sentence, 0, 0, 0));
            int highestIndex = getHighestIndex(outputs);
            switch (highestIndex) {
                case 0:
                    System.out.println("German (" + getPercentage(outputs[0], outputs) * 100 + "% confidence)");
                    break;
                case 1:
                    System.out.println("English (" + getPercentage(outputs[1], outputs) * 100 + "% confidence)");
                    break;
                case 2:
                    System.out.println("Italian (" + getPercentage(outputs[2], outputs) * 100 + "% confidence)");
                    break;
            }
        }
    }

    private double getPercentage(double output, double[] outputs) {
        double sum = 0;
        for (double v : outputs) {
            sum += v;
        }
        return output / sum;
    }

    private void run() throws IOException {
        loadFiles();
        usedChars = getChars();
        //122
        NeuralNetwork neuralNetwork = new NeuralNetwork(usedChars.size(), 50, 3);
        DataPoint[] dataPoints = getDataPointArray();
        List<DataPoint[]> trainingBatches = getChunks(DataPoint[].class, dataPoints, 10);
        double performance;
        while ((performance = getOverallCost(trainingBatches, neuralNetwork)) > 0.1d) {
            printState(neuralNetwork, trainingBatches, performance);
            long startingTime = System.nanoTime();
            for (int i = 0; i < trainingBatches.size(); i++) {
                if (System.nanoTime() - startingTime >= TimeUnit.SECONDS.toNanos(10)) {
                    startingTime = System.nanoTime();
                    performance = getOverallCost(trainingBatches, neuralNetwork);
                    printState(neuralNetwork, trainingBatches, performance);
                }
                neuralNetwork.learn(trainingBatches.get(i), LEARN_RATE);
            }
            System.out.println(performance);
        }
        System.out.println(performance);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("src/LanguageClassification/neuralNetwork.nn"));
        objectOutputStream.writeObject(neuralNetwork);
        objectOutputStream.close();
    }

    private void printState(NeuralNetwork neuralNetwork, List<DataPoint[]> trainingBatches, double performance) {
        System.out.println(performance + "; " + getPerformance(trainingBatches, neuralNetwork));
    }

    private double getOverallCost(List<DataPoint[]> trainingBatches, NeuralNetwork neuralNetwork) {
        double cost = 0;
        for (int i = 0; i < trainingBatches.size(); i++) {
            cost += neuralNetwork.getCost(trainingBatches.get(i));
        }
        cost /= trainingBatches.size();
        return cost;
    }

    private double getPerformance(List<DataPoint[]> trainingBatches, NeuralNetwork neuralNetwork) {
        int corrects = 0;
        int[] langCorrects = new int[trainingBatches.get(0)[0].getExpectedOutputs().length];
        int whole = 0;
        for (int i = 0; i < trainingBatches.size(); i++) {
            DataPoint[] dataPoints = trainingBatches.get(i);
            for (int j = 0; j < dataPoints.length; j++) {
                double[] outputs = neuralNetwork.getOutputs(dataPoints[j]);
                double[] expectedOutputs = dataPoints[j].getExpectedOutputs();
                if (hasSameHighestIndex(outputs, expectedOutputs)) {
                    corrects++;
                    langCorrects[getHighestIndex(expectedOutputs)]++;
                }
                whole++;
            }
        }
        System.out.println("Correct: " + corrects + "; Whole: " + whole + ";    " + Arrays.toString(langCorrects));
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

    public static <T> List<T[]> getChunks(Class<T[]> ctor, T[] array, int chunkSize) {
        List<T[]> outList = new ArrayList<>();
        int counter = 0;
        T[] current = ctor.cast(java.lang.reflect.Array.newInstance(ctor.getComponentType(), chunkSize));
        for (T t : array) {
            if (counter == chunkSize) {
                counter = 0;
                outList.add(current);
                current = ctor.cast(java.lang.reflect.Array.newInstance(ctor.getComponentType(), chunkSize));
            }
            current[counter] = t;
            counter++;
        }
        return outList;
    }

    private DataPoint[] getDataPointArray() {
        List<List<DataPoint>> dataPoints = new ArrayList<>();
        dataPoints.add(getDataPoints(germanLines, 1, 0, 0));
        dataPoints.add(getDataPoints(englishLines, 0, 1, 0));
        dataPoints.add(getDataPoints(italianLines, 0, 0, 1));
        List<DataPoint> dataPointList = new ArrayList<>();
        for (List<DataPoint> currentDataPoints : dataPoints) {
            Collections.addAll(dataPointList, currentDataPoints.toArray(new DataPoint[0]));
        }
        Collections.shuffle(dataPointList);
        return dataPointList.toArray(new DataPoint[0]);
    }

    private List<DataPoint> getDataPoints(List<String> lines, double... expectedOutputs) {
        List<DataPoint> dataPoints = new ArrayList<>();
        int offset = 0;
        StringBuilder current;
        for (int i = 0; i + offset < lines.size(); i++) {
            current = new StringBuilder(lines.get(i + offset));
            while (current.length() < 200) {
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

    private int countChars() throws IOException {
        int chars = 0;
        List<Character> charsList = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(english.getAbsolutePath()));
        englishLines = lines;
        for (int i = 0; i < lines.size(); i++) {
            char[] current = lines.get(i).toCharArray();
            for (int j = 0; j < current.length; j++) {
                char o = current[j];
                if (!charsList.contains(o)) {
                    charsList.add(o);
                    chars++;
                }
            }
        }
        lines = Files.readAllLines(Paths.get(german.getAbsolutePath()));
        germanLines = lines;
        for (int i = 0; i < lines.size(); i++) {
            char[] current = lines.get(i).toCharArray();
            for (int j = 0; j < current.length; j++) {
                char o = current[j];
                if (!charsList.contains(o)) {
                    charsList.add(o);
                    chars++;
                }
            }
        }
        lines = Files.readAllLines(Paths.get(italian.getAbsolutePath()));
        italianLines = lines;
        for (int i = 0; i < lines.size(); i++) {
            char[] current = lines.get(i).toCharArray();
            for (int j = 0; j < current.length; j++) {
                char o = current[j];
                if (!charsList.contains(o)) {
                    charsList.add(o);
                    chars++;
                }
            }
        }
        return chars;
    }

    private List<Character> getChars() throws IOException {
        List<Character> charsList = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(english.getAbsolutePath()));
        englishLines = lines;
        for (int i = 0; i < lines.size(); i++) {
            char[] current = lines.get(i).toCharArray();
            for (int j = 0; j < current.length; j++) {
                char o = current[j];
                if (!charsList.contains(o)) {
                    charsList.add(o);
                }
            }
        }
        lines = Files.readAllLines(Paths.get(german.getAbsolutePath()));
        germanLines = lines;
        for (int i = 0; i < lines.size(); i++) {
            char[] current = lines.get(i).toCharArray();
            for (int j = 0; j < current.length; j++) {
                char o = current[j];
                if (!charsList.contains(o)) {
                    charsList.add(o);
                }
            }
        }
        lines = Files.readAllLines(Paths.get(italian.getAbsolutePath()));
        italianLines = lines;
        for (int i = 0; i < lines.size(); i++) {
            char[] current = lines.get(i).toCharArray();
            for (int j = 0; j < current.length; j++) {
                char o = current[j];
                if (!charsList.contains(o)) {
                    charsList.add(o);
                }
            }
        }
        return charsList;
    }

    private void loadFiles() {
        english = new File("src/LanguageClassification/ TrainingDataEnglish.txt");
        german = new File("src/LanguageClassification/TrainingDataGerman.txt");
        italian = new File("src/LanguageClassification/TrainingDataItalian.txt");
    }

    private class LanguageLine implements DataPoint {
        final double[] inputs;
        final double[] expectedOutputs;

        public LanguageLine(String s, double... ints) {
            inputs = getInputs(s);
            expectedOutputs = ints;
        }

        private double[] getInputs(String s) {
            double[] count = new double[usedChars.size()];
            char[] chars = s.toCharArray();
            for (char aChar : chars) {
                count[usedChars.indexOf(aChar)]++;
            }
            for (int i = 0; i < count.length; i++) {
                count[i] = count[i] / chars.length;
            }
            return count;
        }

        @Override
        public double[] getInputs() {
            return inputs;
        }

        @Override
        public double[] getExpectedOutputs() {
            return expectedOutputs;
        }
    }
}