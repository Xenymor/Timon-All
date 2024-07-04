package NeuralNetwork.NEAT;

public class Connection {
    final int from;
    final int to;
    final int nodeEntryIndex;

    final Node fromNode;
    final int connectionIndex;

    public Connection(final int fromIndex, final int toIndex, final int nodeEntryIndex, final Node fromNode, final int connectionIndex) {
        from = fromIndex;
        to = toIndex;
        this.nodeEntryIndex = nodeEntryIndex;
        this.fromNode = fromNode;
        this.connectionIndex = connectionIndex;
    }
}
