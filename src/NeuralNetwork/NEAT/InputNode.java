package NeuralNetwork.NEAT;

import java.util.List;

public class InputNode implements Node {
    double input;

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Node clone() {
        return new InputNode();
    }

    @Override
    public NodeType getType() {
        return NodeType.INPUT;
    }

    @Override
    public void chooseRandomActivation() {

    }

    @Override
    public void addConnection() {

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
