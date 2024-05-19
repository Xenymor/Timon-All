package NeuralNetwork;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class LayerLearnData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    public double[] inputs;
    public double[] weightedInputs;
    public double[] activations;
    public double[] nodeValues;

    public LayerLearnData(Layer layer) {
        weightedInputs = new double[layer.OUTPUT_COUNT];
        activations = new double[layer.OUTPUT_COUNT];
        nodeValues = new double[layer.OUTPUT_COUNT];
    }

    public LayerLearnData clone() {
        final NeuralNetwork.LayerLearnData layerLearnData = (NeuralNetwork.LayerLearnData) super.clone();
        LayerLearnData result = new LayerLearnData(new Layer(0,0));
        result.inputs = inputs.clone();
        result.weightedInputs = weightedInputs.clone();
        result.activations = activations.clone();
        result.nodeValues = nodeValues.clone();
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LayerLearnData that = (LayerLearnData) o;
        return Arrays.equals(inputs, that.inputs) && Arrays.equals(weightedInputs, that.weightedInputs) && Arrays.equals(activations, that.activations) && Arrays.equals(nodeValues, that.nodeValues);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(inputs);
        result = 31 * result + Arrays.hashCode(weightedInputs);
        result = 31 * result + Arrays.hashCode(activations);
        result = 31 * result + Arrays.hashCode(nodeValues);
        return result;
    }
}
