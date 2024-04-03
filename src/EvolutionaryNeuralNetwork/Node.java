package EvolutionaryNeuralNetwork;

import java.util.Arrays;

public class Node {
    double[] weights;
    double bias;

    public Node(int inputCount) {
        weights = new double[inputCount];
        Arrays.fill(weights, (Math.random() * 2) - 1);
        bias = (Math.random() * 2) - 1;
    }

    public double getOutput(double[] inputs) {
        double output = bias;
        for (int i = 0, n = weights.length; i < n; i++) {
            output += inputs[i] * weights[i];
        }
        return output;
    }
}

