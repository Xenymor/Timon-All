package NeuralNetwork.NEAT;

import java.util.List;

public class InputNode implements Node {
    double input;

    @Override
    public Node clone() {
        return new InputNode();
    }

    @Override
    public NodeType getType() {
        return NodeType.INPUT;
    }

    @Override
    public void setPredecessor(final int index, final int newValue) {

    }

    @Override
    public void connectFrom(final int fromIndex) {}

    @Override
    public Integer[] getPredecessors() {
        return null;
    }

    @Override
    public List<Double> getWeights() {
        return null;
    }

    @Override
    public double getBias() {
        return 0;
    }

    @Override
    public void setBias(final double value) {}

    @Override
    public void setInput(final int inputIndex, final double input) {
        this.input = input;
    }

    @Override
    public double getOutput() {
        return input;
    }
}
