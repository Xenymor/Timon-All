package NeuralNetworkProjects.DigitIdentification;

import NeuralNetwork.DataPoint;
import NeuralNetwork.NeuralNetwork;
import NeuralNetworkProjects.DigitIdentification.FileReading.ImageReader;
import NeuralNetworkProjects.DigitIdentification.FileReading.LabelReader;
import StandardClasses.MyArrays;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static StandardClasses.MyArrays.getChunks;

public class MainDigitIdentification {

    public static final int CHUNK_SIZE = 100;
    private static final String SAVE_FILE_PATH = "src/NeuralNetworkProjects/DigitIdentification/neuralNetwork.nn";
    private double learnRate = 0.05;

    public MainDigitIdentification(final NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    public MainDigitIdentification() {
        neuralNetwork = new NeuralNetwork(784, 1400, 500, 250, 100, 10);
    }

    public static void main(String[] args) throws IOException {
        new MainDigitIdentification().run();
    }

    NeuralNetwork neuralNetwork;

    private void run() throws IOException {
        ImageReader imageReader = new ImageReader(new File("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\DigitIdentification\\train-images-idx3-ubyte\\train-images-idx3-ubyte"));
        LabelReader labelReader = new LabelReader(new File("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\DigitIdentification\\train-labels-idx1-ubyte\\train-labels-idx1-ubyte"));

        TrainingDataPoint[] dataPoints = getDataPoints(imageReader, labelReader);
        imageReader.close();
        labelReader.close();
        List<TrainingDataPoint[]> dataChunks = getChunks(TrainingDataPoint[].class, dataPoints, CHUNK_SIZE);

        imageReader = new ImageReader(new File("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\DigitIdentification\\t10k-images-idx3-ubyte\\t10k-images-idx3-ubyte"));
        labelReader = new LabelReader(new File("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\DigitIdentification\\t10k-labels-idx1-ubyte\\t10k-labels-idx1-ubyte"));
        TrainingDataPoint[] testData = getDataPoints(imageReader, labelReader);
        imageReader.close();
        labelReader.close();

        double lastCost = neuralNetwork.getCost(testData);
        System.out.println("Starting training");
        while (true) {
            for (int i = 0; i < dataChunks.size(); i++) {
                neuralNetwork.learn(dataChunks.get(i), learnRate);
                System.out.println("Learned: " + Math.round((double)i/dataChunks.size()*10000)/100d + "%");
            }
            double cost = neuralNetwork.getCost(testData);
            final double costDifference = cost - lastCost;
            System.out.println(cost + ";\t" + (costDifference >= 0 ? "\033[0;31m +" : "\033[0;32m ") + costDifference + "\033[0m");
            if (cost < lastCost) {
                saveNeuralNetwork(neuralNetwork);
            }
            if (cost == lastCost) {
                learnRate *= 0.1;
            }
        }
    }

    private void saveNeuralNetwork(final NeuralNetwork neuralNetwork) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_PATH));
        objectOutputStream.writeObject(neuralNetwork);
        objectOutputStream.close();
    }

    private TrainingDataPoint[] getDataPoints(final ImageReader imageReader, final LabelReader labelReader) throws IOException {
        if (imageReader.getNumberOfItems() != labelReader.getNumberOfItems()) {
            throw new RuntimeException("The count of items differs");
        }
        TrainingDataPoint[] result = new TrainingDataPoint[imageReader.getNumberOfItems()];
        for (int i = 0; i < result.length; i++) {
            imageReader.selectNext();
            labelReader.selectNext();

            final byte label = labelReader.getCurrentValue();
            byte[] imgBytes = imageReader.getCurrentData();
            double[] inputs = new double[imgBytes.length];
            for (int j = 0; j < imgBytes.length; j++) {
                final byte data = imgBytes[j];
                int gray = 255 - (((int) data) & 0xFF);
                inputs[j] = gray;
            }
            result[i] = new TrainingDataPoint(label, inputs);
        }

        labelReader.close();
        imageReader.close();

        return result;
    }

    public static class TrainingDataPoint implements DataPoint {
        double[] inputs;
        double[] expectedOutputs;

        public TrainingDataPoint(final byte label, final double[] inputs) {
            this.inputs = inputs;
            this.expectedOutputs = getEmptyArrayExcept(10, label, 1);
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

    private static double[] getEmptyArrayExcept(final int length, final int index, final int value) {
        double[] result = new double[length];
        Arrays.fill(result, 0);
        result[index] = value;
        return result;
    }
}
