package NeuralNetwork;

import java.io.Serial;
import java.io.Serializable;

public class NeuralNetwork implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    Layer[] layers;

    public NeuralNetwork(int inputs, int... layerSizes) {
        layers = new Layer[layerSizes.length];
        for (int i = 0; i < layers.length; i++) {
            if (i == 0) {
                layers[i] = new Layer(layerSizes[i], inputs, this);//gives size and inputs
            } else {
                layers[i] = new Layer(layerSizes[i], layerSizes[i - 1], this);//same
            }
        }
    }

    public double[] getOutputs(double... inputs) {
        for (Layer layer : layers) {
            inputs = layer.getOutputs(inputs);
            for (int j = 0; j < inputs.length; j++) {
                inputs[j] = activationFunction(inputs[j]);
            }
        }
        return inputs;
    }

    public double[] getOutputs(DataPoint input) {
        double[] inputs = input.getInputs();
        if (Double.isNaN(inputs[0])) {
            int v = 0;
        }
        return getOutputs(inputs);
    }

    void updateAllGradients(DataPoint dataPoint) {
        calculateOutputs(dataPoint.getInputs());

        Layer outputLayer = layers[layers.length - 1];
        double[] nodeValues = outputLayer.calculateOutputLayerNodeValues(dataPoint.getExpectedOutputs());
        outputLayer.updateGradients(nodeValues);
        for (int hiddenLayerIndex = layers.length - 2; hiddenLayerIndex >= 0; hiddenLayerIndex--) {
            Layer hiddenLayer = layers[hiddenLayerIndex];
            nodeValues = hiddenLayer.calculateHiddenLayerNodeValues(layers[hiddenLayerIndex + 1], nodeValues);
            hiddenLayer.updateGradients(nodeValues);
        }
    }

    private void calculateOutputs(double[] inputs) {
        for (Layer layer : layers) {
            inputs = layer.getOutputs(inputs);
            for (int j = 0; j < inputs.length; j++) {
                inputs[j] = activationFunction(inputs[j]);
            }
        }
    }

    double activationFunction(double input) {
        return 1 / (1 + Math.exp(-input));
    }

    public double getCost(DataPoint data) {
        double[] inputs = data.getInputs();
        double[] expectedOutputs = data.getExpectedOutputs();
        double[] outputs = getOutputs(inputs);
        return layers[layers.length - 1].getCost(outputs, expectedOutputs);
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

    public double getCost(DataPoint[] trainingData) {
        double totalCost = 0;
        for (DataPoint trainingDatum : trainingData) {
            totalCost += getCost(trainingDatum);
        }

        return (totalCost / trainingData.length);
    }

    void applyAllGradients(double learnRate) {
        for (Layer layer : layers) {
            layer.applyGradients(learnRate);
        }
    }

   /* public void learn(DataPoint[] trainingBatch, double learnRate) {
        for (DataPoint dataPoint : trainingBatch) {
            updateAllGradients(dataPoint);
        }

        applyAllGradients(learnRate/ trainingBatch.length);

        clearAllGradients();
    }*/

    private void clearAllGradients() {
        for (Layer layer : layers) {
            layer.clearGradients();
        }
    }

    public void learn(DataPoint[] trainingData, double learnRate) {
        final double h = 0.01d;
        double originalCost = getCost(trainingData);
        for (Layer value : layers) {
            value.learn(trainingData, h, originalCost);
        }
        applyAllGradients(learnRate);
        clearAllGradients();
    }

    public void learn(DataPoint data, double learnRate) {
        final double h = 0.0001f;
        double originalCost = getCost(data);
        for (Layer layer : layers) {
            layer.learn(data, h, originalCost, learnRate);
        }
        applyAllGradients(learnRate);
        clearAllGradients();
    }

    public double[][] getOutputs(DataPoint[] trainingData) {
        double[][] outputs = new double[trainingData.length][layers[layers.length-1].nodes.length];
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = getOutputs(trainingData[i]);
        }
        return outputs;
    }
}
