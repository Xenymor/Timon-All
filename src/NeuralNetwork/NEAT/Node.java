package NeuralNetwork.NEAT;

import java.util.List;

public interface Node {

    void addConnection();

    List<Double> getWeights();

    double getBias();

    void setBias(double value);

    void setInput(int inputIndex, double input);

    double getOutput();

    Node clone();

    NodeType getType();

    void chooseRandomActivation();
}
