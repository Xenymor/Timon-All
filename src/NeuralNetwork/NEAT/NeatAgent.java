package NeuralNetwork.NEAT;

import StandardClasses.Random;

import java.util.ArrayList;
import java.util.List;

import static NeuralNetwork.NEAT.Configuration.*;
import static NeuralNetwork.NEAT.NodeType.HIDDEN;
import static NeuralNetwork.NEAT.NodeType.OUTPUT;
import static StandardClasses.Random.*;

public class NeatAgent {
    private List<Node> nodes = new ArrayList<>();
    final private int inputCount;
    final private int outputCount;
    private int hiddenCount = 0;

    public int getInputCount() {
        return inputCount;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public int getHiddenCount() {
        return hiddenCount;
    }

    public NeatAgent(final int inputCount, final int outputCount) {
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        for (int i = 0; i < inputCount; i++) {
            nodes.add(new InputNode());
        }
        for (int i = 0; i < outputCount; i++) {
            nodes.add(new OutputNode());
        }
        for (int i = 0; i < inputCount; i++) {
            for (int j = inputCount; j < outputCount + inputCount; j++) {
                boolean couldConnect = addConnection(nodes.get(j), i, j);
                if (!couldConnect) {
                    System.out.println("Error while setting up");
                }
            }
        }
    }

    private NeatAgent(final int inputCount, final int outputCount, final List<Node> nodes) {
        this.nodes = nodes;
        this.inputCount = inputCount;
        this.outputCount = outputCount;
    }

    private boolean addConnection(final Node to, final int fromIndex, final int toIndex) {
        if (fromIndex < toIndex || (toIndex < outputCount + inputCount && toIndex >= inputCount)) {
            to.connectFrom(fromIndex);
            return true;
        } else {
            return false;
        }
    }


    public void mutate() {
        for (int i = 0; i < MUTATION_COUNT; i++) {
            if (chanceOf(MUTATION_CHANCE)) {
                if (chanceOf(0.5)) {
                    mutateBiases();
                } else {
                    mutateWeights();
                }
            }
        }

        if (chanceOf(NEW_NODE_PROBABILITY) && hiddenCount < MAX_HIDDEN_NODES) {
            createNode();
        }

        if (chanceOf(NEW_CONNECTION_PROBABILITY)) {
            createRandomConnection();
        }
    }

    private void createRandomConnection() {
        int i1 = randomIntInRange(inputCount + hiddenCount);
        if (i1 >= inputCount) {
            i1 += outputCount;
        }
        int i2 = randomIntInRange(outputCount + hiddenCount) + inputCount;
        addConnection(nodes.get(i2), i1, i2);
    }

    private void createNode() {
        //Create Node
        hiddenCount++;
        final HiddenNode hiddenNode = new HiddenNode();
        nodes.add(hiddenNode);
        final int newNodeIndex = nodes.size() - 1;

        //Pick random OutputNode
        int[] chances = new int[outputCount];
        for (int i = 0; i < outputCount; i++) {
            chances[i] = nodes.get(i + inputCount).getPredecessors().length;
        }
        int nodeIndex = Random.pickElementProportionalToValue(chances);
        Node node = nodes.get(nodeIndex + inputCount);

        //Pick random connection
        Integer[] predecessors = node.getPredecessors();
        int connectionIndex = Random.randomIntInRange(predecessors.length);

        //Split the connection
        if (predecessors.length > 0) {
            int predecessorIndex = predecessors[connectionIndex];
            node.setPredecessor(connectionIndex, newNodeIndex);
            addConnection(hiddenNode, predecessorIndex, newNodeIndex);
        } else {
            System.out.println("0 predecessors");
        }

    }

    private void mutateWeights() {
        int[] chances = new int[hiddenCount + outputCount];
        for (int i = 0; i < hiddenCount + outputCount; i++) {
            chances[i] = nodes.get(i + inputCount).getWeights().size();
        }
        int nodeIndex = Random.pickElementProportionalToValue(chances);
        Node node = nodes.get(nodeIndex + inputCount);
        int weightIndex = Random.randomIntInRange(chances[nodeIndex]);

        final List<Double> weights = node.getWeights();
        if (weights.size() > 0) {
            if (chanceOf(NEW_VALUE_PROBABILITY)) {
                weights.set(weightIndex, randomDoubleInRange(-WEIGHT_RANGE, WEIGHT_RANGE));
            } else {
                if (chanceOf(BIG_MUTATION_PROBABILITY)) {
                    weights.set(weightIndex, weights.get(weightIndex) + randomDoubleInRange(-WEIGHT_RANGE, WEIGHT_RANGE));
                } else {
                    weights.set(weightIndex, weights.get(weightIndex) + randomDoubleInRange(-WEIGHT_RANGE, WEIGHT_RANGE) * SMALL_RANGE_MULT);
                }
            }
        }
    }

    private void mutateBiases() {
        Node node = nodes.get(randomIntInRange(inputCount, nodes.size()));
        if (chanceOf(NEW_VALUE_PROBABILITY)) {
            node.setBias(randomDoubleInRange(-WEIGHT_RANGE, WEIGHT_RANGE));
        } else {
            if (chanceOf(BIG_MUTATION_PROBABILITY)) {
                node.setBias(node.getBias() + randomDoubleInRange(-WEIGHT_RANGE, WEIGHT_RANGE));
            } else {
                node.setBias(node.getBias() + randomDoubleInRange(-WEIGHT_RANGE, WEIGHT_RANGE) * SMALL_RANGE_MULT);
            }
        }
    }

    public double[] getOutputs(final double... inputs) {
        for (int i = 0; i < inputCount; i++) {
            nodes.get(i).setInput(0, inputs[i]);
        }

        for (int i = inputCount + outputCount; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            setInputs(node);
        }

        double[] outputs = new double[outputCount];
        for (int i = 0; i < outputCount; i++) {
            Node node = nodes.get(i + inputCount);
            setInputs(node);
            outputs[i] = node.getOutput();
        }
        return outputs;
    }

    private void setInputs(Node node) {
        final NodeType type = node.getType();
        assert (type.equals(HIDDEN) || type.equals(OUTPUT));
        Integer[] connectedNodes = node.getPredecessors();
        for (int j = 0; j < connectedNodes.length; j++) {
            final Integer index = connectedNodes[j];
            final Node predecessor = nodes.get(index);
            node.setInput(j, predecessor.getOutput());
        }
    }

    public NeatAgent clone() {
        List<Node> cloned = new ArrayList<>();
        for (Node node : nodes) {
            cloned.add(node.clone());
        }
        return new NeatAgent(inputCount, outputCount, cloned);
    }

    public int getNodeCount() {
        return hiddenCount;
    }
}
