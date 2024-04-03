package EvolutionaryNeuralNetwork;

public class EvolutionaryNeuralNetwork {
    Layer[] layers;
    int[] layerSizes;
    int inputCount;

    public EvolutionaryNeuralNetwork(int inputCount, int... layerSizes) {
        this.layerSizes = layerSizes;
        this.inputCount = inputCount;
        layers = new Layer[layerSizes.length];
        for (int i = 0; i < layers.length; i++) {
            if (i == 0) {
                layers[i] = new Layer(layerSizes[i], inputCount);
            } else {
                layers[i] = new Layer(layerSizes[i], layerSizes[i - 1]);
            }
        }
    }

    public double[] getOutputs(double... inputs) {
        for (Layer layer : layers) {
            inputs = layer.getOutputs(inputs);
            for (int j = 0; j < inputs.length; j++) {
                inputs[j] = activationFunction(inputs[j]);
            }
        }
        return inputs.clone();
    }

    double activationFunction(double input) {
        return 1 / (1 + Math.exp(-input));
    }

    public static EvolutionaryNeuralNetwork reproduce(double mutationRate, double mutationStep, double[] fitnesses, EvolutionaryNeuralNetwork... parents) {
        if (fitnesses.length != parents.length) {
            return null;
        }
        long fitnessSum = 0;
        for (int i = 0; i < fitnesses.length; i++) {
            fitnessSum += fitnesses[i];
        }
        double[] percentages = new double[fitnesses.length];
        for (int i = 0; i < fitnesses.length; i++) {
            percentages[i] = fitnesses[i] / (double) fitnessSum;
        }
        EvolutionaryNeuralNetwork result = new EvolutionaryNeuralNetwork(parents[0].inputCount, parents[0].layerSizes);
        Layer[] layers = result.layers;
        for (int i = 0; i < layers.length; i++) {
            Node[] nodes = layers[i].nodes;
            for (int j = 0; j < nodes.length; j++) {
                setNodeRandom(nodes[j], percentages, parents, i, j);
                mutateNode(nodes[j], mutationStep, mutationRate);
            }
        }
        return result;
    }

    private static void mutateNode(Node node, double mutationStep, double mutationRate) {
        double[] weights = node.weights;
        double p;
        for (int i = 0; i < weights.length; i++) {
            if (Math.random() < mutationRate) {
                p = Math.random();
                weights[i] += (p < 0.5) ? mutationStep : -mutationStep;
            }
        }
    }

    private static void setNodeRandom(Node node, double[] percentages, EvolutionaryNeuralNetwork[] parents, int layerIndex, int nodeIndex) {
        double[] weights = node.weights;
        for (int i = 0; i < weights.length; i++) {
            weights[i] = parents[getRandomIndex(percentages)].layers[layerIndex].nodes[nodeIndex].weights[i];
        }
        node.bias = parents[getRandomIndex(percentages)].layers[layerIndex].nodes[nodeIndex].bias;
    }

    private static int getRandomIndex(double[] percentages) {
        double sum = 0;
        double p = Math.random();
        for (int i = 0; i < percentages.length; i++) {
            sum += percentages[i];
            if (p < sum) {
                return i;
            }
        }
        return 0;
    }
}
