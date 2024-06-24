package NeuralNetwork.NEAT;

import StandardClasses.Random;

import java.util.ArrayList;
import java.util.List;

import static NeuralNetwork.NEAT.ActivationType.ActivationTypeType.BOTH;
import static NeuralNetwork.NEAT.ActivationType.ActivationTypeType.ONLY_HIDDEN;

public class HiddenNode implements Node {
    private final List<Double> inputs = new ArrayList<>();
    private final List<Double> weights = new ArrayList<>();
    double bias = Random.randomDoubleInRange(-Configuration.WEIGHT_RANGE, Configuration.WEIGHT_RANGE);
    ActivationType activationType;
    boolean recalculate = false;
    double lastOutput = 0;

    public HiddenNode() {
        chooseRandomActivation();
    }

    public void chooseRandomActivation() {
        ActivationType[] types = ActivationType.values();
        activationType = types[Random.randomIntInRange(types.length)];
        while (true) {
            final ActivationType.ActivationTypeType type = activationType.getType();
            if (type.equals(BOTH)
                    || type.equals(ONLY_HIDDEN))
                break;
            activationType = types[Random.randomIntInRange(types.length)];
        }
    }

    @Override
    public NodeType getType() {
        return NodeType.HIDDEN;
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
            lastOutput = Activation.get(sum, activationType);
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
        result.inputs.addAll(inputs);
        return result;
    }
}
