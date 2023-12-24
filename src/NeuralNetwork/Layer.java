package NeuralNetwork;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Layer implements Serializable {
    @Serial
    private static final long serialVersionUID = 2;

    final int INPUT_COUNT;
    final int OUTPUT_COUNT;

    private double[] weights;
    private double[] biases;

    double[] costGradientB;
    double[] costGradientW;
    private double[] weightVelocities;
    private double[] biasVelocities;

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
        for (int i = 0; i < weights.length; i++) {
            weights[i] = RandomInNormalDistribution(0, 1) / Math.sqrt(INPUT_COUNT);
        }
        for (int i = 0; i < biases.length; i++) {
            biases[i] = RandomInNormalDistribution(0, 1);
        }
    }

    double RandomInNormalDistribution(double mean, double standardDeviation) {
        double x1 = 1 - Math.random();
        double x2 = 1 - Math.random();

        double y1 = Math.sqrt(-2.0 * Math.log(x1)) * Math.cos(2.0 * Math.PI * x2);
        return y1 * standardDeviation + mean;
    }

    public double[] getOutputs(double[] inputs) {
        double[] weightedInputs = new double[OUTPUT_COUNT];

        for (int nodeOut = 0; nodeOut < OUTPUT_COUNT; nodeOut++) {
            double weightedInput = biases[nodeOut];

            for (int nodeIn = 0; nodeIn < INPUT_COUNT; nodeIn++) {
                weightedInput += inputs[nodeIn] * getWeight(nodeIn, nodeOut);
            }
            weightedInputs[nodeOut] =  Activation.activate(weightedInput);
        }

        return weightedInputs;
    }

    public double[] calculateOutputs(double[] inputs, LayerLearnData learnData) {
        learnData.inputs = inputs;

        for (int nodeOut = 0; nodeOut < OUTPUT_COUNT; nodeOut++) {
            double weightedInput = biases[nodeOut];
            for (int nodeIn = 0; nodeIn < INPUT_COUNT; nodeIn++) {
                weightedInput += inputs[nodeIn] * getWeight(nodeIn, nodeOut);
            }
            learnData.weightedInputs[nodeOut] = weightedInput;
            learnData.activations[nodeOut] = Activation.activate(weightedInput);
        }

        return learnData.activations;
    }

    private double getWeight(final int nodeIn, final int nodeOut) {
        return weights[nodeOut * INPUT_COUNT + nodeIn];
    }

    public void applyGradients(double learnRate, double regularization, double momentum) {
        double weightDecay = (1 - regularization * learnRate);
        for (int i = 0; i < weights.length; i++) {
            double weight = weights[i];
            double velocity = weightVelocities[i] * momentum - costGradientW[i] * learnRate;
            weightVelocities[i] = velocity;
            weights[i] = weight * weightDecay + velocity;
            costGradientW[i] = 0;
        }

        for (int i = 0; i < biases.length; i++) {
            double velocity = biasVelocities[i] * momentum - costGradientB[i] * learnRate;
            biasVelocities[i] = velocity;
            biases[i] += velocity;
            costGradientB[i] = 0;
        }
    }

    public void updateGradients(LayerLearnData layerLearnData) {
        for (int nodeOut = 0; nodeOut < OUTPUT_COUNT; nodeOut++) {
            double nodeValue = layerLearnData.nodeValues[nodeOut];
            for (int nodeIn = 0; nodeIn < INPUT_COUNT; nodeIn++) {
                // Evaluate the partial derivative: cost / weight of current connection
                double derivativeCostWrtWeight = layerLearnData.inputs[nodeIn] * nodeValue;
                // The costGradientW array stores these partial derivatives for each weight.
                // Note: the derivative is being added to the array here because ultimately we want
                // to calculate the average gradient across all the data in the training batch
                costGradientW[getFlatWeightIndex(nodeIn, nodeOut)] += derivativeCostWrtWeight;
            }
        }

        for (int nodeOut = 0; nodeOut < OUTPUT_COUNT; nodeOut++) {
            // Evaluate partial derivative: cost / bias
            double derivativeCostWrtBias = 1 * layerLearnData.nodeValues[nodeOut];
            costGradientB[nodeOut] += derivativeCostWrtBias;
        }
    }

    public int getFlatWeightIndex(int inputNeuronIndex, int outputNeuronIndex) {
        return outputNeuronIndex * INPUT_COUNT + inputNeuronIndex;
    }

    public void calculateOutputLayerNodeValues(LayerLearnData layerLearnData, double[] expectedOutputs) {
        for (int i = 0; i < layerLearnData.nodeValues.length; i++) {
            // Evaluate partial derivatives for current node: cost/activation & activation/weightedInput
            double costDerivative = Cost.costDerivative(layerLearnData.activations[i], expectedOutputs[i]);
            double activationDerivative = Activation.derivative(layerLearnData.weightedInputs, i);
            layerLearnData.nodeValues[i] = costDerivative * activationDerivative;
        }
    }

    public void calculateHiddenLayerNodeValues(LayerLearnData layerLearnData, Layer oldLayer, double[] oldNodeValues) {
        for (int newNodeIndex = 0; newNodeIndex < OUTPUT_COUNT; newNodeIndex++) {
            double newNodeValue = 0;
            for (int oldNodeIndex = 0; oldNodeIndex < oldNodeValues.length; oldNodeIndex++) {
                // Partial derivative of the weighted input with respect to the input
                newNodeValue += oldLayer.getWeight(newNodeIndex, oldNodeIndex) * oldNodeValues[oldNodeIndex];
            }
            newNodeValue *= Activation.derivative(layerLearnData.weightedInputs, newNodeIndex);
            layerLearnData.nodeValues[newNodeIndex] = newNodeValue;
        }
    }

    public Layer clone() {
        Layer result = new Layer(OUTPUT_COUNT, INPUT_COUNT);
        result.weights = weights.clone();
        result.costGradientW = costGradientW.clone();
        result.weightVelocities = weightVelocities.clone();
        result.biases = biases.clone();
        result.costGradientB = costGradientB.clone();
        result.biasVelocities = biasVelocities.clone();
        return result;
    }

    public void mutate(final double standardDeviation) {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = RandomInNormalDistribution(weights[i], standardDeviation);
        }
        for (int i = 0; i < biases.length; i++) {
            biases[i] = RandomInNormalDistribution(biases[i], standardDeviation);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Layer layer = (Layer) o;
        return INPUT_COUNT == layer.INPUT_COUNT && OUTPUT_COUNT == layer.OUTPUT_COUNT && Arrays.equals(weights, layer.weights) && Arrays.equals(biases, layer.biases) && Arrays.equals(costGradientB, layer.costGradientB) && Arrays.equals(costGradientW, layer.costGradientW) && Arrays.equals(weightVelocities, layer.weightVelocities) && Arrays.equals(biasVelocities, layer.biasVelocities);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(INPUT_COUNT, OUTPUT_COUNT);
        result = 31 * result + Arrays.hashCode(weights);
        result = 31 * result + Arrays.hashCode(biases);
        result = 31 * result + Arrays.hashCode(costGradientB);
        result = 31 * result + Arrays.hashCode(costGradientW);
        result = 31 * result + Arrays.hashCode(weightVelocities);
        result = 31 * result + Arrays.hashCode(biasVelocities);
        return result;
    }
}
