package LanguageClassification;

import NeuralNetwork.DataPoint;
import NeuralNetwork.NeuralNetwork;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class LanguageClassificationMain {
    private static final double LEARN_RATE = 0.8d;
    File english;
    List<String> englishLines;
    File german;
    List<String> germanLines;
    File italian;
    List<String> italianLines;
    File spanish;
    List<String> spanishLines;
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
                case 0 -> System.out.println("German (" + getPercentage(outputs[0], outputs) * 100 + "% confidence)");
                case 1 -> System.out.println("English (" + getPercentage(outputs[1], outputs) * 100 + "% confidence)");
                case 2 -> System.out.println("Italian (" + getPercentage(outputs[2], outputs) * 100 + "% confidence)");
                case 3 -> System.out.println("Spanish (" + getPercentage(outputs[3], outputs) * 100 + "% confidence)");
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

    private void run() throws IOException, ClassNotFoundException {
        loadFiles();
        usedChars = getChars();
        List<List<Double>> list = new ArrayList<>();
        list.add(getCharPercentage(germanLines));
        list.add(getCharPercentage(englishLines));
        list.add(getCharPercentage(italianLines));
        list.add(getCharPercentage(spanishLines));
        for (int i = 0; i < usedChars.size(); i++) {
            System.out.println(usedChars.get(i) + " : " + list.get(0).get(i) + ", " + list.get(1).get(i) + ", " + list.get(2).get(i));
        }
        //122
        NeuralNetwork neuralNetwork = new NeuralNetwork(usedChars.size(), 40, 20, 3);
        File file = new File("src/LanguageClassification/neuralNetwork.nn");
        if (file.exists()) {
            neuralNetwork = (NeuralNetwork) new ObjectInputStream(new FileInputStream(file.getAbsolutePath())).readObject();
        }
        DataPoint[] dataPoints = getDataPointArray();
        List<DataPoint[]> trainingBatches = getChunks(DataPoint[].class, dataPoints, 5);
        long learnings = 0;
        double performance;
        while ((performance = getOverallCost(trainingBatches, neuralNetwork)) > 0.5d) {
            printState(neuralNetwork, trainingBatches, performance, learnings);
            long startingTime = System.nanoTime();
            for (int i = 0; i < trainingBatches.size(); i++) {
                if (System.nanoTime() - startingTime >= TimeUnit.SECONDS.toNanos(10)) {
                    startingTime = System.nanoTime();
                    performance = getOverallCost(trainingBatches, neuralNetwork);
                    printState(neuralNetwork, trainingBatches, performance, learnings);
                }
                neuralNetwork.learn(trainingBatches.get(i), LEARN_RATE);
                learnings++;
            }
            System.out.println(performance + " after " + learnings + " learnings");
        }
        System.out.println(performance);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("src/LanguageClassification/neuralNetwork.nn"));
        objectOutputStream.writeObject(neuralNetwork);
        objectOutputStream.close();
    }

    private List<Double> getCharPercentage(List<String> lines) {
        List<Double> percentage = new ArrayList<>();
        for (int i = 0; i < usedChars.size(); i++) {
            percentage.add(0d);
        }
        long total = 0;
        for (String line : lines) {
            char[] lineChars = line.toCharArray();
            for (char lineChar : lineChars) {
                int charIndex = usedChars.indexOf(lineChar);
                percentage.set(charIndex, percentage.get(charIndex) + 1);
                total++;
            }
        }
        for (int i = 0; i < percentage.size(); i++) {
            percentage.set(i, percentage.get(i) / total);
        }
        return percentage;
    }

    private void printState(NeuralNetwork neuralNetwork, List<DataPoint[]> trainingBatches, double performance, long learnings) {
        System.out.println(performance + "; " + getPerformance(trainingBatches, neuralNetwork) + " after " + learnings + " learnings");
    }

    private double getOverallCost(List<DataPoint[]> trainingBatches, NeuralNetwork neuralNetwork) {
        double cost = 0;
        for (DataPoint[] trainingBatch : trainingBatches) {
            cost += neuralNetwork.getCost3(trainingBatch);
        }
        cost /= trainingBatches.size();
        return cost;
    }

    private double getPerformance(List<DataPoint[]> trainingBatches, NeuralNetwork neuralNetwork) {
        int corrects = 0;
        int[] langCorrects = new int[trainingBatches.get(0)[0].getExpectedOutputs().length];
        int whole = 0;
        for (DataPoint[] dataPoints : trainingBatches) {
            for (DataPoint dataPoint : dataPoints) {
                double[] outputs = neuralNetwork.getOutputs(dataPoint);
                double[] expectedOutputs = dataPoint.getExpectedOutputs();
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
        dataPoints.add(getDataPoints(germanLines, 1, 0, 0, 0));
        dataPoints.add(getDataPoints(englishLines, 0, 1, 0, 0));
        dataPoints.add(getDataPoints(italianLines, 0, 0, 1, 0));
        dataPoints.add(getDataPoints(spanishLines, 0, 0, 0, 1));

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
            while (current.length() < 2000) {
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

    /*private int getCharPercentage() throws IOException {
        int chars = 0;
        List<Character> charsList = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(english.getAbsolutePath()));
        englishLines = lines;
        chars = getChars(chars, charsList, lines);
        lines = Files.readAllLines(Paths.get(german.getAbsolutePath()));
        germanLines = lines;
        chars = getChars(chars, charsList, lines);
        lines = Files.readAllLines(Paths.get(italian.getAbsolutePath()));
        italianLines = lines;
        chars = getChars(chars, charsList, lines);
        return chars;
    }

    private int getChars(int chars, List<Character> charsList, List<String> lines) {
        for (String line : lines) {
            char[] current = line.toCharArray();
            for (char o : current) {
                if (!charsList.contains(o)) {
                    charsList.add(o);
                    chars++;
                }
            }
        }
        return chars;
    }*/

    private List<Character> getChars() throws IOException {
        List<Character> charsList = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(english.getAbsolutePath()));
        englishLines = lines;
        getChars(charsList, lines);
        lines = Files.readAllLines(Paths.get(german.getAbsolutePath()));
        germanLines = lines;
        LanguageClassificationMain.this.getChars(charsList, lines);
        lines = Files.readAllLines(Paths.get(italian.getAbsolutePath()));
        italianLines = lines;
        LanguageClassificationMain.this.getChars(charsList, lines);
        lines = Files.readAllLines(Paths.get(spanish.getAbsolutePath()));
        spanishLines = lines;
        LanguageClassificationMain.this.getChars(charsList, lines);
        return charsList;
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

    private void loadFiles() {
        english = new File("src/LanguageClassification/ TrainingDataEnglish.txt");
        german = new File("src/LanguageClassification/TrainingDataGerman.txt");
        italian = new File("src/LanguageClassification/TrainingDataItalian.txt");
        spanish = new File("src/LanguageClassification/TrainingDataSpanish.txt");
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