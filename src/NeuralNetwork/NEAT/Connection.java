package NeuralNetwork.NEAT;

public class Connection {
    final int from;
    final int to;
    final int index;

    public Connection(final int fromIndex, final int toIndex, final int index) {
        from = fromIndex;
        to = toIndex;
        this.index = index;
    }
}
