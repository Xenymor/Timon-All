package NeuralNetwork;

import StandardClasses.Random;

import java.io.Serializable;
import java.util.Arrays;

public class Layer implements Serializable {
    private static final long serialVersionUID = 1;
    final Node[] nodes;
    private double[] myOutputCache;
    final int INPUT_COUNT;

    double[] costGradientB;
    double[][] costGradientW;

    public Layer(int layerSize, int inputCount, NeuralNetwork neuralNetwork) {
        this.INPUT_COUNT = inputCount;
        nodes = new Node[layerSize];
        this.myOutputCache = new double[nodes.length];
        for (int i = 0; i < layerSize; i++) {
            nodes[i] = new Node(inputCount, neuralNetwork);
        }
        costGradientB = new double[layerSize];
        costGradientW = new double[inputCount][layerSize];
        initializeRandomWeights();
        applyGradients(1);
        clearGradients();
    }

    private void initializeRandomWeights() {
        for (int i = 0; i < costGradientW.length; i++) {
            for (int j = 0; j < costGradientW[i].length; j++) {
                double r = Random.randomDoubleInRange(-5, 5);
                costGradientW[i][j] = r / Math.sqrt(costGradientW.length);
            }
        }
    }

    public double[] getOutputs(double[] inputs) {
        double[] output = myOutputCache;
        for (int i = 0; i < nodes.length; i++) {
            output[i] = nodes[i].getOutput(inputs);
        }
        return output;
    }

    public double getCost2(double[] outputs, double[] expectedOutputs) {
        double cost = 0;

        for (int i = 0; i < nodes.length; i++) {
            cost += nodes[i].getNodeCost(outputs[i], expectedOutputs[i]);
        }

        return cost;
    }

    public void applyGradients(double learnRate) {
        for (int nodeOut = 0; nodeOut < nodes.length; nodeOut++) {
            nodes[nodeOut].applyGradients(learnRate, costGradientB[nodeOut], costGradientW, nodeOut);
        }
    }

    public void learn(DataPoint[] trainingData, double h, double originalCost) {
        for (int nodeOut = 0; nodeOut < nodes.length; nodeOut++) {
            nodes[nodeOut].learn(trainingData, h, nodeOut, originalCost, costGradientW, costGradientB);
        }
    }

    public void learn(DataPoint trainingData, double h, double originalCost) {
        for (int nodeOut = 0; nodeOut < nodes.length; nodeOut++) {
            nodes[nodeOut].learn(trainingData, h, nodeOut, originalCost, costGradientW, costGradientB);
        }
    }

    public void clearGradients() {
        Arrays.fill(costGradientB, 0);
        for (double[] doubles : costGradientW) {
            Arrays.fill(doubles, 0);
        }
    }
}
