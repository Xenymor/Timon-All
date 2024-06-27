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
    private final Map<Integer, Set<Integer>> descendants = new HashMap<>();
    private final Map<Integer, Set<Integer>> tempAncestors = new HashMap<>();

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
        initializePedigree();
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

    private void createOrder() {
        order.clear();
        tempAncestors.clear();
        ancestors.keySet().forEach((a) -> tempAncestors.put(a, new HashSet<>(ancestors.get(a))));

        List<Integer> unfinishedNodes = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            unfinishedNodes.add(i);
        }

        int i = 0;
        int iterationCount = 0;
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
                iterationCount++;
                if (iterationCount > nodes.size()) {
                    return;
                }
            }
        }
    }

    private void removeTempConnectionsFrom(final int index) {
        for (int i = 0; i < nodes.size(); i++) {
            final Set<Integer> integers = tempAncestors.get(i);
            integers.remove(index);
        }
    }

    private void initializePedigree() {
        ancestors.clear();
        descendants.clear();
        for (int i = 0; i < nodes.size(); i++) {
            ancestors.put(i, new HashSet<>());
            descendants.put(i, new HashSet<>());
        }

        for (int i = 0; i < inputCount; i++) {
            Set<Integer> currDescendants = descendants.get(i);
            for (int j = inputCount; j < outputCount + inputCount; j++) {
                currDescendants.add(j);
                ancestors.get(j).add(i);
            }
        }
    }

    private boolean addConnection(final int fromNodeIndex, final int toNodeIndex, final int toIndex) {
        if ((fromNodeIndex != toNodeIndex)
                && ((fromNodeIndex < inputCount || fromNodeIndex >= inputCount + outputCount) && toNodeIndex >= inputCount)
                && !(isAncestor(toNodeIndex, fromNodeIndex))
                && !(isParent(fromNodeIndex, toNodeIndex))) {
            forceConnection(fromNodeIndex, toNodeIndex, toIndex);
            final Set<Integer> currAncestors = ancestors.get(toNodeIndex);
            currAncestors.add(fromNodeIndex);
            currAncestors.addAll(ancestors.get(fromNodeIndex));
            final Set<Integer> currDescendants = descendants.get(fromNodeIndex);
            currDescendants.add(toNodeIndex);
            currDescendants.addAll(descendants.get(toNodeIndex));
            updateAncestors(toNodeIndex, fromNodeIndex);
            updateDescendants(fromNodeIndex, toNodeIndex);
            return true;
        } else {
            return false;
        }
    }

    private boolean isParent(final int fromNodeIndex, final int toNodeIndex) {
        for (Connection connection : connections) {
            if (connection.from == fromNodeIndex && connection.to == toNodeIndex) {
                return true;
            }
        }
        return false;
    }

    private void forceConnection(final int fromNodeIndex, final int toNodeIndex, final int toIndex) {
        final Connection connection = new Connection(fromNodeIndex, toNodeIndex, toIndex);
        connections.add(connection);
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
                if (chanceOf(Configuration.ACTIVATION_MUTATION_CHANCE)) {
                    mutateActivationFunctions();
                }
            }
        }

        if (chanceOf(NEW_NODE_PROBABILITY) && hiddenCount < MAX_HIDDEN_NODES) {
            createNode();
            if (ancestors.get(1).size() < nodes.size() - 1) {
                return;
            }
        }

        if (chanceOf(NEW_CONNECTION_PROBABILITY)) {
            createRandomConnection();
            if (ancestors.get(1).size() < nodes.size() - 1) {
                return;
            }
        }

        createOrder();
    }

    private void mutateActivationFunctions() {
        Node node = nodes.get(randomIntInRange(inputCount, nodes.size()));
        node.chooseRandomActivation();
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

        final HashSet<Integer> newAncestors = new HashSet<>(ancestors.get(old.from));
        newAncestors.add(old.from);
        ancestors.put(newNodeIndex, newAncestors);
        final Set<Integer> oldAncestors = ancestors.get(old.to);
        oldAncestors.addAll(newAncestors);
        oldAncestors.add(newNodeIndex);

        final HashSet<Integer> newDescendants = new HashSet<>(descendants.get(old.to));
        newDescendants.add(old.to);
        descendants.put(newNodeIndex, newDescendants);
        final Set<Integer> oldDescendants = descendants.get(old.from);
        oldDescendants.addAll(newDescendants);
        oldDescendants.add(newNodeIndex);

        updateAncestors(newNodeIndex, old.from);
        updateDescendants(newNodeIndex, old.to);
    }

    private void updateDescendants(final int toUpdate, final int updatedChild) {
        Set<Integer> currDescendants = descendants.get(toUpdate);
        currDescendants.addAll(descendants.get(updatedChild));
        final Set<Integer> currAncestors = ancestors.get(toUpdate);
        for (Integer i : currAncestors) {
            updateDescendants(i, toUpdate);
        }
    }

    private void updateAncestors(final int toUpdate, final int updatedParent) {
        Set<Integer> currAncestors = ancestors.get(toUpdate);
        currAncestors.addAll(ancestors.get(updatedParent));
        final Set<Integer> currDescendants = descendants.get(toUpdate);
        for (Integer i : currDescendants) {
            updateAncestors(i, toUpdate);
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
        neatAgent.hiddenCount = hiddenCount;
        neatAgent.connections.addAll(connections);
        for (int i = 0; i < nodes.size(); i++) {
            neatAgent.ancestors.put(i, new HashSet<>(ancestors.get(i)));
            neatAgent.descendants.put(i, new HashSet<>(descendants.get(i)));
        }
        neatAgent.order.addAll(order);
        return neatAgent;
    }

    public int getNodeCount() {
        return hiddenCount;
    }
}
