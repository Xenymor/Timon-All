package NeuralNetwork.NEAT;

public class Configuration {
    /*
            namespace mut
            {
                constexpr float new_node_proba = 0.05f;
                constexpr float new_conn_proba = 0.8f;

                constexpr float new_value_proba      = 0.2f;
                constexpr double weight_range        = 1.0;
                constexpr double weight_small_range  = 0.01;

                constexpr uint32_t mut_count = 4;
                constexpr uint32_t max_hidden_nodes = 30;
            }
        */
    public static final int MAX_HIDDEN_NODES = 30;
    public static final int KEPT_AGENT_PERCENTAGE = 3;

    public static final double WEIGHT_RANGE = 1.0;
    public static final double SMALL_RANGE_MULT = 0.01;

    public static final int MUTATION_COUNT = 4;
    public static final double MUTATION_CHANCE = 0.25;

    public static final double BIG_MUTATION_PROBABILITY = 0.25;
    public static final double NEW_VALUE_PROBABILITY = 0.2;
    public static final double NEW_CONNECTION_PROBABILITY = 0.8;
    public static final double NEW_NODE_PROBABILITY = 0.05;
}
