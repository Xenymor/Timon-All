package NeuralNetwork.NEAT;

import java.util.List;

public interface Node {

    void connectFrom(int fromIndex);

    Integer[] getPredecessors();

    List<Double> getWeights();

    double getBias();

    void setBias(double value);

    void setInput(int inputIndex, double input);

    double getOutput();

    Node clone();

    NodeType getType();

    void setPredecessor(int index, int newValue);
}
