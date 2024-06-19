package NeuralNetwork.NEAT;

import StandardClasses.Random;

import java.util.ArrayList;
import java.util.List;

public class HiddenNode implements Node {

    private final List<Integer> predecessors = new ArrayList<>();
    private final List<Double> inputs = new ArrayList<>();
    private final List<Double> weights = new ArrayList<>();
    double bias = Random.randomDoubleInRange(-Configuration.WEIGHT_RANGE, Configuration.WEIGHT_RANGE);
    boolean recalculate = false;
    double lastOutput = 0;

    @Override
    public NodeType getType() {
        return NodeType.HIDDEN;
    }

    @Override
    public void setPredecessor(final int index, final int newValue) {
        predecessors.set(index, newValue);
    }

    @Override
    public void connectFrom(final int fromIndex) {
        if (!predecessors.contains(fromIndex)) {
            predecessors.add(fromIndex);
            weights.add(Random.randomDoubleInRange(-Configuration.WEIGHT_RANGE, Configuration.WEIGHT_RANGE));
            inputs.add(0.0);
        }
    }

    @Override
    public Integer[] getPredecessors() {
        return predecessors.toArray(Integer[]::new);
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
            lastOutput = Math.max(0, sum);
            recalculate = false;
        }
        return lastOutput;
    }

    @Override
    public Node clone() {
        HiddenNode result = new HiddenNode();
        result.recalculate = true;
        result.bias = bias;
        result.weights.addAll(weights);
        for (int i = 0; i < inputs.size(); i++) {
            result.inputs.add(0d);
        }
        result.predecessors.addAll(predecessors);
        return result;
    }
}
