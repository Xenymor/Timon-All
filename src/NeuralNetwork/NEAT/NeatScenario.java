package NeuralNetwork.NEAT;

public interface NeatScenario {
    /**
     * Has to be multiThreadable.
     * @param agent the agent to test
     * @return The score the agent has. Must be positive. A higher score is better.
     */
    double getScore(NeatAgent agent);

    double getExpectedOutput(double v);
}
