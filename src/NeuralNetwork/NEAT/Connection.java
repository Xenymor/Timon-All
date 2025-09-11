package NeuralNetwork.NEAT;

public record Connection(int from, int to, int nodeEntryIndex, Node fromNode, int connectionIndex) {
}
