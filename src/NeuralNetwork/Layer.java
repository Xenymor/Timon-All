package NeuralNetwork;

import StandardClasses.Random;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class Layer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    Node[] nodes;
    final int INPUT_COUNT;

    double[] costGradientB;
    double[][] costGradientW;

    double[] lastInputs;
    double[] lastOutputs;

    public Layer(int layerSize, int inputCount, NeuralNetwork neuralNetwork) {
        this.INPUT_COUNT = inputCount;
        nodes = new Node[layerSize];
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
        double[] output = new double[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            output[i] = nodes[i].getOutput(inputs);
        }
        lastInputs = inputs.clone();
        lastOutputs = output.clone();
        return output;
    }

    public double getCost(double[] outputs, double[] expectedOutputs) {
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

    public void learn(DataPoint trainingData, double h, double originalCost, double learnRate) {
        for (int nodeOut = 0; nodeOut < nodes.length; nodeOut++) {
            nodes[nodeOut].learn(trainingData, h, nodeOut, originalCost, costGradientW, costGradientB);
        }
    }

    double[] calculateOutputLayerNodeValues(double[] expectedOutputs) {
        double[] nodeValues = new double[expectedOutputs.length];

        for (int i = 0; i < nodeValues.length; i++) {
            double costDerivative = nodeCostDerivative(lastOutputs[i], expectedOutputs[i]);
            double activationDerivative = activationDerivative(lastInputs[i]);
            nodeValues[i] = activationDerivative*costDerivative;
        }

        return nodeValues;
    }

    void updateGradients(double[] nodeValues) {
        for (int nodeOut = 0; nodeOut < nodes.length; nodeOut++) {
            for (int nodeIn = 0; nodeIn < nodes[nodeOut].weights.length; nodeIn++) {
                double derivativeCostWrtWeight = lastInputs[nodeIn] * nodeValues[nodeOut];

                costGradientW[nodeIn][nodeOut] += derivativeCostWrtWeight;
            }

            double derivativeCostWrtBias = 1 * nodeValues[nodeOut];
            costGradientB[nodeOut] += derivativeCostWrtBias;
        }
    }

    double[] calculateHiddenLayerNodeValues(Layer oldLayer, double[] oldNodeValues) {
        double[] newNodeValues = new double[nodes.length];

        for (int newNodeIndex = 0; newNodeIndex < newNodeValues.length; newNodeIndex++) {
            double newNodeValue = 0;
            for (int oldNodeIndex = 0; oldNodeIndex < oldNodeValues.length; oldNodeIndex++) {
                double inputDerivative = oldLayer.nodes[oldNodeIndex].weights[newNodeIndex];
                newNodeValue += inputDerivative * oldNodeValues[oldNodeIndex];
            }
            newNodeValue *= activationDerivative(lastInputs[newNodeIndex]);
            newNodeValues[newNodeIndex] = newNodeValue;
        }

        return newNodeValues;
    }

    double activationFunction(double input) {
        return 1 / (1 + Math.exp(-input));
    }

    double activationDerivative(double input) {
        double activation = activationFunction(input);
        return activation * (1-activation);
    }

    double nodeCostDerivative(double outputActivation, double expectedOutput) {
        return 2*(outputActivation-expectedOutput);
    }

    public void clearGradients() {
        Arrays.fill(costGradientB, 0);
        for (double[] doubles : costGradientW) {
            Arrays.fill(doubles, 0);
        }
    }
}
