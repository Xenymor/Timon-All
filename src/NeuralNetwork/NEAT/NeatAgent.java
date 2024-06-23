package NeuralNetwork.NEAT;

import StandardClasses.Random;

import java.util.*;

import static NeuralNetwork.NEAT.Configuration.*;
import static NeuralNetwork.NEAT.NodeType.HIDDEN;
import static NeuralNetwork.NEAT.NodeType.OUTPUT;
import static StandardClasses.Random.*;

public class NeatAgent {
    private final int inputCount;
    private final int outputCount;
    private int hiddenCount = 0;

    private final List<Node> nodes;
    private final List<Connection> connections = new ArrayList<>();
    private final List<Integer> order = new ArrayList<>();

    private final Map<Integer, Set<Integer>> ancestors = new HashMap<>();
    private final Map<Integer, Set<Integer>> tempAncestors = new HashMap<>();
    private final Map<Integer, List<Integer>> parents = new HashMap<>();

    private NeatAgent(final int inputCount, final int outputCount, final List<Node> nodes) {
        this.nodes = nodes;
        this.inputCount = inputCount;
        this.outputCount = outputCount;
    }

    public NeatAgent(final int inputCount, final int outputCount) {
        nodes = new ArrayList<>();
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
                forceConnection(i, j, nodes.get(j).getWeights().size());
            }
        }
        createLists();
    }

    public int getInputCount() {
        return inputCount;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public int getHiddenCount() {
        return hiddenCount;
    }

    private void createLists() {
        createAncestorMap();
        createOrder();
    }

    private void createOrder() {
        order.clear();
        List<Integer> unfinishedNodes = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            unfinishedNodes.add(i);
        }
        int i = 0;
        while (unfinishedNodes.size() > 0) {
            int index = unfinishedNodes.get(i);
            if (tempAncestors.get(index).size() == 0) {
                order.add(index);
                unfinishedNodes.remove(i);
                removeTempConnectionsFrom(index);
            }
            i++;
            if (i >= unfinishedNodes.size()) {
                i = 0;
            }
        }
    }

    private void removeTempConnectionsFrom(final int index) {
        for (int i = 0; i < nodes.size(); i++) {
            tempAncestors.get(i).remove(index);
        }
    }

    private void createAncestorMap() {
        parents.clear();
        for (Connection connection : connections) {
            if (parents.containsKey(connection.to)) {
                parents.get(connection.to).add(connection.from);
            } else {
                final ArrayList<Integer> list = new ArrayList<>();
                list.add(connection.from);
                parents.put(connection.to, list);
            }
        }

        Stack<Integer> toCheck = new Stack<>();
        for (int i = 0; i < nodes.size(); i++) {
            if (parents.containsKey(i)) {
                Set<Integer> result = new HashSet<>();
                final List<Integer> outerParents = parents.get(i);
                for (final Integer parent : outerParents) {
                    toCheck.push(parent);
                    result.add(parent);
                }
                while (!toCheck.isEmpty()) {
                    Integer current = toCheck.pop();
                    if (parents.containsKey(current)) {
                        final List<Integer> currentParents = parents.get(current);
                        for (final Integer parent : currentParents) {
                            toCheck.push(parent);
                            result.add(parent);
                        }
                    }
                }
                ancestors.put(i, result);
                tempAncestors.put(i, new HashSet<>(result));
            } else {
                ancestors.put(i, new HashSet<>());
                tempAncestors.put(i, new HashSet<>());
            }
        }
    }

    private boolean addConnection(final int fromNodeIndex, final int toNodeIndex, final int toIndex) {
        if (((fromNodeIndex < inputCount || fromNodeIndex > inputCount + outputCount) && toNodeIndex >= inputCount)
                && !(isAncestor(toNodeIndex, fromNodeIndex))) {
            forceConnection(fromNodeIndex, toNodeIndex, toIndex);
            return true;
        } else {
            return false;
        }
    }

    private void forceConnection(final int fromNodeIndex, final int toNodeIndex, final int toIndex) {
        connections.add(new Connection(fromNodeIndex, toNodeIndex, toIndex));
        nodes.get(toNodeIndex).addConnection();
    }

    private boolean isAncestor(final int potentialAncestor, final int other) {
        return ancestors.get(other).contains(potentialAncestor);
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
            createLists();
        }

        if (chanceOf(NEW_CONNECTION_PROBABILITY)) {
            createRandomConnection();
            createLists();
        }

    }

    private void createRandomConnection() {
        int i1 = randomIntInRange(inputCount + hiddenCount);
        if (i1 >= inputCount) {
            i1 += outputCount;
        }
        int i2 = randomIntInRange(outputCount + hiddenCount) + inputCount;
        addConnection(i1, i2, nodes.get(i2).getWeights().size());
    }

    private void createNode() {
        //Create Node
        hiddenCount++;
        final HiddenNode hiddenNode = new HiddenNode();
        nodes.add(hiddenNode);
        final int newNodeIndex = nodes.size() - 1;

        //Pick random connection
        final int connectionIndex = randomIntInRange(connections.size());
        Connection old = connections.get(connectionIndex);

        //Split the connection
        connections.set(connectionIndex, new Connection(old.from, newNodeIndex, 0));
        connections.add(new Connection(newNodeIndex, old.to, old.index));
        hiddenNode.addConnection();
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

        for (int i = 0; i < order.size() - outputCount; i++) {
            final Integer index = order.get(i);
            Node node = nodes.get(index);
            setInputs(node, index);
        }

        double[] outputs = new double[outputCount];
        for (int i = 0; i < outputCount; i++) {
            Node node = nodes.get(i + inputCount);
            setInputs(node, i + inputCount);
            outputs[i] = node.getOutput();
        }
        return outputs;
    }

    private void setInputs(Node node, int index) {
        final NodeType type = node.getType();
        if (type.equals(HIDDEN) || type.equals(OUTPUT)) {
            for (Connection connection : connections) {
                if (connection.to == index) {
                    node.setInput(connection.index, nodes.get(connection.from).getOutput());
                }
            }
        }
    }

    public NeatAgent clone() {
        List<Node> cloned = new ArrayList<>();
        for (Node node : nodes) {
            cloned.add(node.clone());
        }
        final NeatAgent neatAgent = new NeatAgent(inputCount, outputCount, cloned);
        neatAgent.connections.addAll(connections);
        neatAgent.createLists();
        return neatAgent;
    }

    public int getNodeCount() {
        return hiddenCount;
    }
}
