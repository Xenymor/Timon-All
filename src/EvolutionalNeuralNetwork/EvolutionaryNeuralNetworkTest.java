package EvolutionalNeuralNetwork;

import StandardClasses.Random;
import org.junit.jupiter.api.Test;

class EvolutionaryNeuralNetworkTest {
    @Test
    void reproduce() {
        final int ITERATIONS = 200;
        final int SAMPLE_SIZE = 100;
        final double MUTATION_RATE = 0.01d;
        final double MUTATION_STEP = 0.1d;
        EvolutionaryNeuralNetwork[] testGroup = new EvolutionaryNeuralNetwork[SAMPLE_SIZE];
        EvolutionaryNeuralNetwork[] newGroup = new EvolutionaryNeuralNetwork[SAMPLE_SIZE];
        double startFitness = 100_000;
        int bestIndex = -1;
        double[] fitnesses = new double[testGroup.length];
        double current;
        for (int i = 0; i < testGroup.length; i++) {
            testGroup[i] = new EvolutionaryNeuralNetwork(1, 1);
            current = getFitness(testGroup[i]);
            fitnesses[i] = current;
            if (current <= startFitness) {
                startFitness = current;
                bestIndex = i;
            }
        }
        for (int i = 0; i < ITERATIONS; i++) {
            newGroup[0] = testGroup[bestIndex];
            for (int j = 1; j < newGroup.length; j++) {
                int i1 = Random.randomIntInRange(0, newGroup.length - 1);
                int i2 = Random.randomIntInRange(0, newGroup.length - 1);
                newGroup[j] = EvolutionaryNeuralNetwork.reproduce(MUTATION_RATE, MUTATION_STEP, new double[] {fitnesses[i1], fitnesses[i2]}, testGroup[i1], testGroup[i2]);
            }
            testGroup = newGroup.clone();
            for (int j = 0; j < testGroup.length; j++) {
                current = getFitness(testGroup[j]);
                fitnesses[j] = current;
            }
        }
        double endFitness = 100_000;
        for (int j = 0; j < testGroup.length; j++) {
            current = getFitness(testGroup[j]);
            if (current <= endFitness) {
                endFitness = current;
            }
        }
        System.out.println("Start fitness: " + startFitness);
        System.out.println("End fitness: " + endFitness);
        assert endFitness > startFitness;
    }

    private double getFitness(EvolutionaryNeuralNetwork evolutionaryNeuralNetwork) {
        double v = 1 - evolutionaryNeuralNetwork.getOutputs(0)[0];
        return Math.max(v, -v);
    }
}