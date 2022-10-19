package NeuralNetwork;

import java.io.Serializable;

public class NeuralNetwork implements Serializable {
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
        return inputs.clone();
    }

    public double[] getOutputs(DataPoint input) {
        double[] inputs = input.getInputs();
        return getOutputs(inputs);
    }

    double activationFunction(double input) {
        return 1 / (1 + Math.exp(-input));
    }

    public double getCost1(DataPoint data) {
        double[] inputs = data.getInputs();
        double[] expectedOutputs = data.getExpectedOutputs();
        double[] outputs = getOutputs(inputs);
        return layers[layers.length - 1].getCost2(outputs, expectedOutputs);
    }

    public double getCost3(DataPoint[] trainingData) {
        double totalCost = 0;
        for (DataPoint trainingDatum : trainingData) {
            totalCost += getCost1(trainingDatum);
        }

        return (totalCost / trainingData.length);
    }

    void applyAllGradients(double learnRate) {
        for (Layer layer : layers) {
            layer.applyGradients(learnRate);
        }
    }

    private void clearAllGradients() {
        for (Layer layer : layers) {
            layer.clearGradients();
        }
    }

    public void learn(DataPoint[] trainingData, double learnRate) {
        final double h = 0.01d;
        double originalCost = getCost3(trainingData);
        for (Layer value : layers) {
            value.learn(trainingData, h, originalCost);
        }
        applyAllGradients(learnRate);
        clearAllGradients();
    }

    public void learn(DataPoint data, double learnRate) {
        final double h = 0.0001f;
        double originalCost = getCost1(data);
        for (Layer layer : layers) {
            layer.learn(data, h, originalCost);
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
