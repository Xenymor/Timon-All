package NeuralNetworkProjects.ChessEvaluation;

import NeuralNetwork.DataPoint;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.NeuralNetworkType;
import StandardClasses.MyArrays;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Main().run();
    }

    private void run() throws IOException, ClassNotFoundException {
        NeuralNetwork trained = loadAndTrainNetwork(1_000_000_000);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\ChessEvaluation\\neuralNetworkChess.nn"));
        objectOutputStream.writeObject(trained);
        System.out.println("Finished training");
    }

    private NeuralNetwork loadAndTrainNetwork(int iterations) throws IOException, ClassNotFoundException {
        DataPoint[] trainingData = loadData();
        List<DataPoint[]> trainingChunks = MyArrays.getChunks(DataPoint[].class, trainingData, 128);
        int validationDataCount = trainingChunks.size() / 10 * 2;
        DataPoint[] validationData = trainingChunks.get(0);
        for (int i = 0; i < validationDataCount; i++) {
            validationData = MyArrays.concatenate(validationData, trainingChunks.get(0));
            trainingChunks.remove(0);
        }
        Collections.shuffle(trainingChunks);
        NeuralNetwork neuralNetwork = new NeuralNetwork(NeuralNetworkType.GRADIENT_DESCENT, 64, 1048, 500, 50, 1);
        if (Files.exists(Path.of("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\ChessEvaluation\\neuralNetworkChess.nn"))) {
            neuralNetwork = (NeuralNetwork) new ObjectInputStream(new FileInputStream("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\ChessEvaluation\\neuralNetworkChess.nn")).readObject();
            System.out.println("Loaded");
        }
        double learnRate = 0.05;
        int counter = 0;
        double bestCost = neuralNetwork.getCost(validationData);
        double lastCost = bestCost;
        System.out.println(bestCost);
        long lastTime = System.nanoTime();
        while (counter < iterations) {
            if (TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - lastTime) >= 300) {
                double cost = neuralNetwork.getCost(validationData);
                System.out.println(cost + " after " + counter + " learnings");
                if (cost == lastCost || cost <= learnRate * 10) {
                    learnRate *= 0.1;
                }
                if (cost < bestCost) {
                    bestCost = cost;
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\ChessEvaluation\\neuralNetworkChess.nn"));
                    objectOutputStream.writeObject(neuralNetwork);
                }
                lastCost = cost;
                lastTime = System.nanoTime();
            }
            neuralNetwork.learn(trainingChunks.get(counter % (trainingChunks.size())), learnRate);
            counter++;
        }
        return neuralNetwork;
    }

    private DataPoint[] loadData() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\ChessEvaluation\\datasetNeuralNetwork.csv"));
        EvaluationData[] evaluationData = new EvaluationData[lines.size()];
        for (int i = 0; i < evaluationData.length; i++) {
            evaluationData[i] = getEvaluationDatum(lines.get(i));
        }
        return evaluationData;
    }

    private EvaluationData getEvaluationDatum(final String line) {
        String[] argumentStrings = line.split(",");
        double[] arguments = new double[argumentStrings.length - 1];
        double output = Integer.parseInt(argumentStrings[0]) / 60_000d + 0.5d;
        for (int i = 1; i < arguments.length; i++) {
            arguments[i] = Integer.parseInt(argumentStrings[i]);
        }
        return new EvaluationData(arguments, output);
    }

    private class EvaluationData implements DataPoint {

        private final double[] inputs;
        private final double[] outputs;

        public EvaluationData(final double[] inputs, final double... outputs) {
            this.inputs = inputs;
            this.outputs = outputs;
        }

        @Override
        public double[] getInputs() {
            return inputs;
        }

        @Override
        public double[] getExpectedOutputs() {
            return outputs;
        }
    }
}
