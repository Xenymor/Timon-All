package NeuralNetwork.NEAT;

import StandardClasses.Random;

import java.util.List;

public class TrainThread extends Thread {
    private final NeatScenario scenario;
    private final NeatTrainer trainer;
    private final int gradientDescentIterations;

    public TrainThread(final NeatScenario scenario, final NeatTrainer trainer, final int gradientDescentIterations) {
        this.scenario = scenario;
        this.trainer = trainer;
        this.gradientDescentIterations = gradientDescentIterations;
    }

    volatile boolean isFree = true;
    volatile boolean shouldRun = true;
    volatile int index = -1;

    @Override
    public void run() {
        while (shouldRun) {
            if (!isFree) {
                NeatAgent agent = trainer.agents[index];
                double score = scenario.getScore(agent);
                trainer.agentScores[index] = new NeatTrainer.AgentScore(agent, score);
                index = -1;
                isFree = true;
            }
        }
    }

    void gradientDescent(final NeatAgent agent) {
        List<Node> nodes = agent.nodes;
        final double stepSize = 0.1d;
        final double sampleValue = 0.01d;

        for (int i = 0; i < gradientDescentIterations; i++) {
            for (int j = agent.getInputCount(); j < nodes.size(); j++) {
                if (Random.chanceOf(0.9)) {
                    continue;
                }
                final Node node = nodes.get(j);
                double lastScore = scenario.getScore(agent);

                List<Double> weights = node.getWeights();
                for (int k = 0; k < weights.size(); k++) {
                    final Double oldWeight = weights.get(k);
                    weights.set(k, oldWeight + sampleValue);
                    final double newScore = scenario.getScore(agent);
                    if (newScore > lastScore) {
                        weights.set(k, oldWeight + stepSize);
                    } else if (newScore < lastScore) {
                        weights.set(k, oldWeight - stepSize);
                    } else {
                        weights.set(k, oldWeight);
                    }
                    lastScore = scenario.getScore(agent);
                }

                double bias = node.getBias();
                node.setBias(bias + sampleValue);
                final double newScore = scenario.getScore(agent);
                if (newScore > lastScore) {
                    node.setBias(bias + stepSize);
                } else if (newScore < lastScore) {
                    node.setBias(bias - stepSize);
                } else {
                    node.setBias(bias);
                }
            }
        }
    }

    public void startTask(final int i) {
        index = i;
        isFree = false;
    }
}
