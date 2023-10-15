package NeuralNetwork;

import java.io.Serial;
import java.io.Serializable;

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
}
