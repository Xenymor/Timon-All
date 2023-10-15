package NeuralNetwork;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class NeuralNetwork implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    Layer[] layers;
    NetworkLearnData networkLearnData;

    public NeuralNetwork(int inputs, int... layerSizes) {
        layers = new Layer[layerSizes.length];
        for (int i = 0; i < layers.length; i++) {
            if (i == 0) {
                layers[i] = new Layer(layerSizes[i], inputs);//gives size and inputs
            } else {
                layers[i] = new Layer(layerSizes[i], layerSizes[i - 1]);//same
            }
        }

        networkLearnData = new NetworkLearnData(layers);
    }

    public double[] getOutputs(double... inputs) {
        for (Layer layer : layers) {
            inputs = layer.getOutputs(inputs);
        }
        return inputs.clone();
    }

    public double[] getOutputs(DataPoint input) {
        double[] inputs = input.getInputs();
        return getOutputs(inputs);
    }

    public double getCost(DataPoint data) {
        double[] inputs = data.getInputs();
        double[] expectedOutputs = data.getExpectedOutputs();
        double[] outputs = getOutputs(inputs);
        return Cost.costFunction(outputs, expectedOutputs);
    }

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

    void updateAllGradients(DataPoint data, NetworkLearnData learnData)
    {
        // Feed data through the network to calculate outputs.
        // Save all inputs/weightedinputs/activations along the way to use for backpropagation.
        double[] inputsToNextLayer = data.getInputs();

        for (int i = 0; i < layers.length; i++)
        {
            inputsToNextLayer = layers[i].calculateOutputs(inputsToNextLayer, learnData.layerData[i]);
            if (Double.isNaN(inputsToNextLayer[0])) {
                System.out.println(Arrays.toString(data.getInputs()));
                System.out.println(Arrays.toString(inputsToNextLayer));
                System.out.println(Arrays.toString(learnData.layerData[i].inputs));
                System.out.println(Arrays.toString(learnData.layerData[i].weightedInputs));
                System.exit(0);
            }
        }

        // -- Backpropagation --
        int outputLayerIndex = layers.length - 1;
        Layer outputLayer = layers[outputLayerIndex];
        LayerLearnData outputLearnData = learnData.layerData[outputLayerIndex];

        // Update output layer gradients
        outputLayer.calculateOutputLayerNodeValues(outputLearnData, data.getExpectedOutputs());
        outputLayer.updateGradients(outputLearnData);

        // Update all hidden layer gradients
        for (int i = outputLayerIndex - 1; i >= 0; i--)
        {
            LayerLearnData layerLearnData = learnData.layerData[i];
            Layer hiddenLayer = layers[i];

            hiddenLayer.calculateHiddenLayerNodeValues(layerLearnData, layers[i + 1], learnData.layerData[i + 1].nodeValues);
            hiddenLayer.updateGradients(layerLearnData);
        }

    }

    public void learn(DataPoint[] trainingBatch, double learnRate) {
        // Use the backpropagation algorithm to calculate the gradient of the cost function
        // (with respect to the network's weights and biases). This is done for each data
        // point, and the gradients are added together.

        for (DataPoint dataPoint : trainingBatch) {
            updateAllGradients(dataPoint, networkLearnData);
        }
        // Gradient descent step: update all the weights and biases in the network
         applyAllGradients(learnRate, trainingBatch.length, 0.1, 0.9);
    }

    public double[][] getOutputs(DataPoint[] trainingData) {
        double[][] outputs = new double[trainingData.length][layers[layers.length - 1].OUTPUT_COUNT];
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = getOutputs(trainingData[i]);
        }
        return outputs;
    }
}