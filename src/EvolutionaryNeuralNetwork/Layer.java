package EvolutionaryNeuralNetwork;

import java.util.Arrays;
import java.util.Objects;

public final class Layer {
    private final Node[] nodes;
    private final int INPUT_COUNT;

    public Layer(Node[] nodes, int INPUT_COUNT) {
        this.nodes = nodes;
        this.INPUT_COUNT = INPUT_COUNT;
    }

    public Layer(int nodes, int INPUT_COUNT) {
        this.INPUT_COUNT = INPUT_COUNT;
        this.nodes = new Node[nodes];
        for (int i = 0; i < nodes; i++) {
            this.nodes[i] = new Node(INPUT_COUNT);
        }
    }

    public double[] getOutputs(double[] inputs) {
        for (int i = 0; i < nodes.length; i++) {
            inputs[i] = nodes[i].getOutput(inputs);
        }
        return inputs;
    }

    public Node[] nodes() {
        return nodes;
    }

    public int INPUT_COUNT() {
        return INPUT_COUNT;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Layer) obj;
        return Arrays.equals(this.nodes, that.nodes) &&
                this.INPUT_COUNT == that.INPUT_COUNT;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(nodes), INPUT_COUNT);
    }

    @Override
    public String toString() {
        return "Layer[" +
                "nodes=" + Arrays.toString(nodes) + ", " +
                "INPUT_COUNT=" + INPUT_COUNT + ']';
    }

}
