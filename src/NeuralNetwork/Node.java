package NeuralNetwork;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class Node implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    double[] weights;
    double bias;
    final NeuralNetwork neuralNetwork;

    public Node(int inputCount, NeuralNetwork neuralNetwork) {
        weights = new double[inputCount];
        Arrays.fill(weights, Math.random());
        bias = 0;
        this.neuralNetwork = neuralNetwork;
    }

    public double getOutput(double[] inputs) {
        if (inputs.length != weights.length) {
            try {
                throw new MismatchingLengthException();
            } catch (MismatchingLengthException e) {
                e.printStackTrace();
            }
            System.exit(-111);
        }
        double output = bias;
        for (int i = 0; i < weights.length; i++) {
            output += inputs[i] * weights[i];
        }
        return output;
    }

    double getNodeCost(double outputActivation, double expectedOutput) {
        double error = outputActivation - expectedOutput;
        return error * error;
    }

    public void applyGradients(double learnRate, double costGradientB, double[][] costGradientW, int nodeOut) {
        bias -= costGradientB * learnRate;
        for (int weight = 0; weight < this.weights.length; weight++) {
            weights[weight] -= costGradientW[weight][nodeOut] * learnRate;
        }
    }

    public void learn(DataPoint[] trainingData, double h, int nodeOut, double originalCost, double[][] costGradientW, double[] costGradientB) {
        for (int nodeIn = 0; nodeIn < weights.length; nodeIn++) {
            weights[nodeIn] += h;
            double deltaCost = neuralNetwork.getCost(trainingData) - originalCost;
            weights[nodeIn] -= h;
            costGradientW[nodeIn][nodeOut] = deltaCost / h;
        }
        bias += h;
        double deltaCost = neuralNetwork.getCost(trainingData) - originalCost;
        bias -= h;
        costGradientB[nodeOut] = deltaCost / h;
    }

    public void learn(DataPoint trainingData, double h, int nodeOut, double originalCost, double[][] costGradientW, double[] costGradientB) {
        for (int nodeIn = 0; nodeIn < weights.length; nodeIn++) {
            weights[nodeIn] += h;
            double deltaCost = neuralNetwork.getCost(trainingData) - originalCost;
            weights[nodeIn] -= h;
            costGradientW[nodeIn][nodeOut] = deltaCost / h;
        }
        bias += h;
        double deltaCost = neuralNetwork.getCost(trainingData) - originalCost;
        bias -= h;
        costGradientB[nodeOut] = deltaCost / h;
    }

    private static class MismatchingLengthException extends Throwable {
    }
}
