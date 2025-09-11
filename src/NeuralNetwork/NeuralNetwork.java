package NeuralNetwork;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class NeuralNetwork implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    private final NeuralNetworkType NEURAL_NETWORK_TYPE;
    public Layer[] layers;
    NetworkLearnData networkLearnData;

    public NeuralNetwork(NeuralNetworkType neuralNetworkType, int inputs, int... layerSizes) {
        layers = new Layer[layerSizes.length];
        for (int i = 0; i < layers.length; i++) {
            if (i == 0) {
                layers[i] = new Layer(layerSizes[i], inputs);//gives size and inputs
            } else {
                layers[i] = new Layer(layerSizes[i], layerSizes[i - 1]);//same
            }
        }

        if (neuralNetworkType.equals(NeuralNetworkType.GRADIENT_DESCENT))
            networkLearnData = new NetworkLearnData(layers);

        NEURAL_NETWORK_TYPE = neuralNetworkType;
    }

    public double[] getOutputs(double... inputs) {
        for (Layer layer : layers) {
            inputs = layer.getOutputs(inputs);
        }
        return inputs.clone();
    }

    public double[] getOutputs(DataPoint input) {
        double[] inputs = input.inputs();
        return getOutputs(inputs);
    }

    /**
     * Only for gradient descent network
     * @param data
     */
    public double getCost(DataPoint data) {
        double[] inputs = data.inputs();
        double[] expectedOutputs = data.expectedOutputs();
        double[] outputs = getOutputs(inputs);
        return Cost.costFunction(outputs, expectedOutputs);
    }

    /**
     * Only for gradient descent network
     * @param trainingData
     */
    public double getCost(DataPoint[] trainingData) {
        double totalCost = 0;
        for (DataPoint trainingDatum : trainingData) {
            totalCost += getCost(trainingDatum);
        }

        return (totalCost / trainingData.length);
    }

    void applyAllGradients(double learnRate, int trainingBatchSize, double regularization, double momentum) {
        for (Layer layer : layers) {
            layer.applyGradients(learnRate / trainingBatchSize, regularization, momentum);
        }
    }

    void updateAllGradients(DataPoint data, NetworkLearnData learnData) {
        // Feed data through the network to calculate outputs.
        // Save all inputs/weightedinputs/activations along the way to use for backpropagation.
        double[] inputsToNextLayer = data.inputs();

        for (int i = 0; i < layers.length; i++) {
            inputsToNextLayer = layers[i].calculateOutputs(inputsToNextLayer, learnData.layerData[i]);
        }

        // -- Backpropagation --
        int outputLayerIndex = layers.length - 1;
        Layer outputLayer = layers[outputLayerIndex];
        LayerLearnData outputLearnData = learnData.layerData[outputLayerIndex];

        // Update output layer gradients
        outputLayer.calculateOutputLayerNodeValues(outputLearnData, data.expectedOutputs());
        outputLayer.updateGradients(outputLearnData);

        // Update all hidden layer gradients
        for (int i = outputLayerIndex - 1; i >= 0; i--) {
            LayerLearnData layerLearnData = learnData.layerData[i];
            Layer hiddenLayer = layers[i];

            hiddenLayer.calculateHiddenLayerNodeValues(layerLearnData, layers[i + 1], learnData.layerData[i + 1].nodeValues);
            hiddenLayer.updateGradients(layerLearnData);
        }

    }

    /**
     * Only for gradient descent network
     * @param trainingBatch
     * @param learnRate
     */
    public void learn(DataPoint[] trainingBatch, double learnRate) {
        learn(trainingBatch, learnRate, .1, .9);
    }

    public void learn(DataPoint[] trainingBatch, double learnRate, double regularization, double momentum) {
        // Use the backpropagation algorithm to calculate the gradient of the cost function
        // (with respect to the network's weights and biases). This is done for each data
        // point, and the gradients are added together.

        for (DataPoint dataPoint : trainingBatch) {
            updateAllGradients(dataPoint, networkLearnData);
        }
        // Gradient descent step: update all the weights and biases in the network
        applyAllGradients(learnRate, trainingBatch.length, regularization, momentum);
    }

    public double[][] getOutputs(DataPoint[] trainingData) {
        double[][] outputs = new double[trainingData.length][layers[layers.length - 1].OUTPUT_COUNT];
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = getOutputs(trainingData[i]);
        }
        return outputs;
    }


    /**
     * Only for evolutionary Neural Network
     * @param standardDeviation
     */
    public void mutate(double standardDeviation) {
        for (final Layer layer : layers) {
            layer.mutate(standardDeviation);
        }
    }

    public NeuralNetwork clone() {
        NeuralNetwork result = new NeuralNetwork(NEURAL_NETWORK_TYPE, 0);
        result.layers = new Layer[layers.length];
        for (int i = 0; i < layers.length; i++) {
            result.layers[i] = layers[i].clone();
        }
        if (NEURAL_NETWORK_TYPE == NeuralNetworkType.GRADIENT_DESCENT)
            result.networkLearnData = networkLearnData.clone();
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final NeuralNetwork that = (NeuralNetwork) o;
        return NEURAL_NETWORK_TYPE == that.NEURAL_NETWORK_TYPE && Arrays.equals(layers, that.layers);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(NEURAL_NETWORK_TYPE, networkLearnData);
        result = 31 * result + Arrays.hashCode(layers);
        return result;
    }
}