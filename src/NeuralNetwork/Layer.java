package NeuralNetwork;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class Layer implements Serializable {
    @Serial
    private static final long serialVersionUID = 2;

    final int INPUT_COUNT;
    final int OUTPUT_COUNT;

    private final double[] weights;
    private final double[] biases;

    double[] costGradientB;
    double[] costGradientW;
    private final double[] weightVelocities;
    private final double[] biasVelocities;

    public Layer(int layerSize, int inputCount) {
        this.INPUT_COUNT = inputCount;
        OUTPUT_COUNT = layerSize;

        weights = new double[INPUT_COUNT * OUTPUT_COUNT];
        costGradientW = new double[weights.length];
        weightVelocities = new double[weights.length];

        biases = new double[OUTPUT_COUNT];
        costGradientB = new double[biases.length];
        biasVelocities = new double[biases.length];

        initializeRandomWeightsAndBiases();
    }

    public double[] getWeights() {
        return weights;
    }

    public double[] getBiases() {
        return biases;
    }

    private void initializeRandomWeightsAndBiases() {
        for (int i = 0; i < weights.length; i++)
        {
            weights[i] = RandomInNormalDistribution(0, 1) / Math.sqrt(INPUT_COUNT);
        }
        for (int i = 0; i < biases.length; i++) {
            biases[i] = RandomInNormalDistribution(0, 1);
        }
    }

    double RandomInNormalDistribution(double mean, double standardDeviation)
    {
        double x1 = 1 - Math.random();
        double x2 = 1 - Math.random();

        double y1 = Math.sqrt(-2.0 * Math.log(x1)) * Math.cos(2.0 * Math.PI * x2);
        return y1 * standardDeviation + mean;
    }

    public double[] getOutputs(double[] inputs) {
        double[] weightedInputs = new double[OUTPUT_COUNT];

        for (int nodeOut = 0; nodeOut < OUTPUT_COUNT; nodeOut++)
        {
            double weightedInput = biases[nodeOut];

            for (int nodeIn = 0; nodeIn < INPUT_COUNT; nodeIn++)
            {
                weightedInput += inputs[nodeIn] * getWeight(nodeIn, nodeOut);
            }
            weightedInputs[nodeOut] = weightedInput;
        }

        // Apply activation function
        double[] activations = new double[OUTPUT_COUNT];
        for (int i = 0; i < OUTPUT_COUNT; i++)
        {
            activations[i] = Activation.activate(weightedInputs, i);
        }

        return activations;
    }

    public double[] calculateOutputs(double[] inputs, LayerLearnData learnData)
    {
        learnData.inputs = inputs;

        for (int nodeOut = 0; nodeOut < OUTPUT_COUNT; nodeOut++)
        {
            double weightedInput = biases[nodeOut];
            for (int nodeIn = 0; nodeIn < INPUT_COUNT; nodeIn++)
            {
                weightedInput += inputs[nodeIn] * getWeight(nodeIn, nodeOut);
            }
            learnData.weightedInputs[nodeOut] = weightedInput;
        }

        // Apply activation function
        for (int i = 0; i < learnData.activations.length; i++)
        {
            learnData.activations[i] = Activation.activate(learnData.weightedInputs, i);
            if (Double.isNaN(learnData.activations[i])) {
                System.out.println();
            }
        }

        return learnData.activations;
    }

    private double getWeight(final int nodeIn, final int nodeOut) {
        return weights[nodeOut * INPUT_COUNT + nodeIn];
    }

    public void applyGradients(double learnRate, double regularization, double momentum) {
        double weightDecay = (1 - regularization * learnRate);
        for (int i = 0; i < weights.length; i++)
        {
            double weight = weights[i];
            double velocity = weightVelocities[i] * momentum - costGradientW[i] * learnRate;
            weightVelocities[i] = velocity;
            weights[i] = weight * weightDecay + velocity;
            costGradientW[i] = 0;
            if (Double.isNaN(weights[i])) {
                System.out.println();
            }
        }

        for (int i = 0; i < biases.length; i++)
        {
            double velocity = biasVelocities[i] * momentum - costGradientB[i] * learnRate;
            biasVelocities[i] = velocity;
            biases[i] += velocity;
            costGradientB[i] = 0;
        }
    }

    public void updateGradients(LayerLearnData layerLearnData) {
        for (int nodeOut = 0; nodeOut < OUTPUT_COUNT; nodeOut++)
        {
            double nodeValue = layerLearnData.nodeValues[nodeOut];
            for (int nodeIn = 0; nodeIn < INPUT_COUNT; nodeIn++)
            {
                // Evaluate the partial derivative: cost / weight of current connection
                double derivativeCostWrtWeight = layerLearnData.inputs[nodeIn] * nodeValue;
                if (Double.isNaN(derivativeCostWrtWeight)) {
                    System.out.println();
                }
                // The costGradientW array stores these partial derivatives for each weight.
                // Note: the derivative is being added to the array here because ultimately we want
                // to calculate the average gradient across all the data in the training batch
                costGradientW[getFlatWeightIndex(nodeIn, nodeOut)] += derivativeCostWrtWeight;
            }
        }

        for (int nodeOut = 0; nodeOut < OUTPUT_COUNT; nodeOut++)
        {
            // Evaluate partial derivative: cost / bias
            double derivativeCostWrtBias = 1 * layerLearnData.nodeValues[nodeOut];
            costGradientB[nodeOut] += derivativeCostWrtBias;
        }
    }

    public int getFlatWeightIndex(int inputNeuronIndex, int outputNeuronIndex)
    {
        return outputNeuronIndex * INPUT_COUNT + inputNeuronIndex;
    }

    public void calculateOutputLayerNodeValues(LayerLearnData layerLearnData, double[] expectedOutputs) {
        for (int i = 0; i < layerLearnData.nodeValues.length; i++)
        {
            // Evaluate partial derivatives for current node: cost/activation & activation/weightedInput
            double costDerivative = Cost.costDerivative(layerLearnData.activations[i], expectedOutputs[i]);
            double activationDerivative = Activation.derivative(layerLearnData.weightedInputs, i);
            layerLearnData.nodeValues[i] = costDerivative * activationDerivative;
            if (Double.isNaN(layerLearnData.nodeValues[i])) {
                System.out.println();
            }
        }
    }

    public void calculateHiddenLayerNodeValues(LayerLearnData layerLearnData, Layer oldLayer, double[] oldNodeValues)
    {
        for (int newNodeIndex = 0; newNodeIndex < OUTPUT_COUNT; newNodeIndex++)
        {
            double newNodeValue = 0;
            for (int oldNodeIndex = 0; oldNodeIndex < oldNodeValues.length; oldNodeIndex++)
            {
                // Partial derivative of the weighted input with respect to the input
                double weightedInputDerivative = oldLayer.getWeight(newNodeIndex, oldNodeIndex);
                newNodeValue += weightedInputDerivative * oldNodeValues[oldNodeIndex];
            }
            if (Double.isNaN(newNodeValue)) {
                System.out.println();
            }
            newNodeValue *= Activation.derivative(layerLearnData.weightedInputs, newNodeIndex);
            layerLearnData.nodeValues[newNodeIndex] = newNodeValue;
            if (Double.isNaN(layerLearnData.nodeValues[newNodeIndex])) {
                System.out.println();
            }
        }
    }
}
