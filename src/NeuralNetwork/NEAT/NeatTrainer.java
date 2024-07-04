package NeuralNetwork.NEAT;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NeatTrainer {

    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    final int inputCount;
    final int outputCount;
    final int agentCount;
    final NeatAgent[] agents;
    final AgentScore[] agentScores;
    private AgentScore best;
    private final ExecutorService threadPool;
    private final NeatScenario scenario;
    private final Random[] randoms;

    public NeatTrainer(final int inputCount, final int outputCount, final int agentCount, final NeatScenario scenario, final int threadCount) {
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        this.agentCount = agentCount;

        this.scenario = scenario;
        threadPool = Executors.newFixedThreadPool(threadCount);

        agents = new NeatAgent[agentCount];
        agentScores = new AgentScore[agentCount];
        for (int i = 0; i < agents.length; i++) {
            agents[i] = new NeatAgent(inputCount, outputCount);
        }
        randoms = new Random[threadCount];
        for (int i = 0; i < randoms.length; i++) {
            randoms[i] = new Random();
        }
    }

    public AgentScore getBest() {
        return new AgentScore(getBestAgent(), getBestScore());
    }

    public void train() {
        CountDownLatch countDown = startTraining();
        waitForThreads(countDown);
        double sum = prepareLists();
        prepareNextGeneration(sum);
    }

    private void waitForThreads(final CountDownLatch countDown) {
        try {
            countDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //TODO delete; only for profiling purposes
    private void waitForThreadsMutate(final CountDownLatch countDown) {
        try {
            countDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void prepareNextGeneration(final double sum) {
        final int percentageKept = agentCount / Configuration.KEPT_AGENT_PERCENTAGE;
        CountDownLatch countDownLatch = new CountDownLatch(agentCount);
        for (int i = 0; i < agentCount; i++) {
            final int finalI = i;
            threadPool.submit(() -> {
                if (finalI < percentageKept) {
                    agents[finalI] = agentScores[finalI].agent;
                } else {
                    double r = randoms[finalI % randoms.length].nextDouble() * sum;
                    for (int j = agentScores.length - 1; j >= 0; j--) {
                        final AgentScore agentScore = agentScores[j];
                        if (r >= agentScore.score) {
                            agents[finalI] = agentScore.agent.clone();
                            agents[finalI].mutate();
                            break;
                        }
                    }
                }
                countDownLatch.countDown();
            });
        }
        waitForThreadsMutate(countDownLatch);
    }

    private double prepareLists() {
        Arrays.sort(agentScores, (a, b) -> Double.compare(b.score, a.score));
        final AgentScore agentScore1 = agentScores[0];

        best = new AgentScore(agentScore1.agent.clone(), agentScore1.score);
        double sum = 0;
        for (final AgentScore agentScore : agentScores) {
            sum += agentScore.score;
            agentScore.score = sum;
        }
        return sum;
    }

    private CountDownLatch startTraining() {
        CountDownLatch countDownLatch = new CountDownLatch(agentCount);
        for (int i = 0; i < agentCount; i++) {
            final int finalI = i;
            threadPool.submit(() -> {
                final NeatAgent agent = agents[finalI];
                gradientDescent(agent, scenario, 3, 0.1, 0.01);
                agentScores[finalI] = new AgentScore(agent, scenario.getScore(agent));
                countDownLatch.countDown();
            });
        }
        return countDownLatch;
    }

    private void gradientDescent(final NeatAgent agent, final NeatScenario scenario, final int iterations, final double learnRate, final double sampleSize) {
        List<Node> nodes = agent.nodes;

        double lastScore;

        for (int i = 0; i < iterations; i++) {
            for (int j = agent.getInputCount(); j < nodes.size(); j++) {
                final Node node = nodes.get(j);
                lastScore = scenario.getScore(agent);

                List<Double> weights = node.getWeights();
                for (int k = 0; k < weights.size(); k++) {
                    final Double oldWeight = weights.get(k);
                    weights.set(k, oldWeight + sampleSize);
                    final double newScore = scenario.getScore(agent);
                    if (newScore > lastScore) {
                        weights.set(k, oldWeight + learnRate);
                    } else if (newScore < lastScore) {
                        weights.set(k, oldWeight - learnRate);
                    } else {
                        weights.set(k, oldWeight);
                    }
                    lastScore = scenario.getScore(agent);
                }

                double bias = node.getBias();
                node.setBias(bias + sampleSize);
                final double newScore = scenario.getScore(agent);
                if (newScore > lastScore) {
                    node.setBias(bias + learnRate);
                } else if (newScore < lastScore) {
                    node.setBias(bias - learnRate);
                } else {
                    node.setBias(bias);
                }
            }
        }
    }

    public double getBestScore() {
        return best.score;
    }

    public NeatAgent getBestAgent() {
        return best.agent.clone();
    }

    public void stop() {
        threadPool.shutdownNow();
    }

    static class AgentScore {
        final NeatAgent agent;
        double score;

        AgentScore(NeatAgent agent, double score) {
            this.agent = agent;
            this.score = score;
        }
    }
}
