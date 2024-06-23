package NeuralNetwork.NEAT;

import StandardClasses.Random;

import java.util.ArrayList;
import java.util.List;

public class OutputNode implements Node {
    private final List<Double> inputs = new ArrayList<>();
    private final List<Double> weights = new ArrayList<>();
    double bias = Random.randomDoubleInRange(-Configuration.WEIGHT_RANGE, Configuration.WEIGHT_RANGE);
    boolean recalculate = false;
    double lastOutput = 0;

    @Override
    public NodeType getType() {
        return NodeType.OUTPUT;
    }

    @Override
    public void addConnection() {
        weights.add(Random.randomDoubleInRange(-Configuration.WEIGHT_RANGE, Configuration.WEIGHT_RANGE));
        inputs.add(0.0);
    }

    @Override
    public List<Double> getWeights() {
        return weights;
    }

    @Override
    public double getBias() {
        return bias;
    }

    @Override
    public void setBias(final double value) {
        bias = value;
    }

    @Override
    public void setInput(final int inputIndex, final double input) {
        inputs.set(inputIndex, input);
        recalculate = true;
    }

    @Override
    public double getOutput() {
        if (recalculate) {
            double sum = bias;
            for (Double input : inputs) {
                sum += input;
            }
            lastOutput = Math.tanh(sum);
            recalculate = false;
        }
        return lastOutput;
    }

    @Override
    public Node clone() {
        OutputNode result = new OutputNode();
        result.recalculate = true;
        result.bias = bias;
        result.weights.addAll(weights);
        result.inputs.addAll(inputs);
        return result;
    }
}
