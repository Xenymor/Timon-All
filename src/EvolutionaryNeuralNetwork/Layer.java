package EvolutionaryNeuralNetwork;

public class Layer {
    final Node[] nodes;
    final int INPUT_COUNT;

    public Layer(int layerSize, int inputCount) {
        this.INPUT_COUNT = inputCount;
        nodes = new Node[layerSize];
        for (int i = 0; i < layerSize; i++) {
            nodes[i] = new Node(inputCount);
        }
    }

    public double[] getOutputs(double[] inputs) {
        for (int i = 0; i < nodes.length; i++) {
            inputs[i] = nodes[i].getOutput(inputs);
        }
        return inputs;
    }
}
