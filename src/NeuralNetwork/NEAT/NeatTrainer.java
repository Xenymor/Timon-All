package NeuralNetwork.NEAT;

import StandardClasses.Random;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class NeatTrainer {

    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    final int inputCount;
    final int outputCount;
    final int agentCount;
    final NeatAgent[] agents;
    final AgentScore[] agentScores;
    private final int gradientDescentIterations;
    private AgentScore best;
    private final TrainThread[] threads;

    public NeatTrainer(final int inputCount, final int outputCount, final int agentCount, final NeatScenario scenario, final int threadCount, final int gradientDescentIterations) {
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        this.agentCount = agentCount;
        this.gradientDescentIterations = gradientDescentIterations;

        agents = new NeatAgent[agentCount];
        agentScores = new AgentScore[agentCount];
        for (int i = 0; i < agents.length; i++) {
            agents[i] = new NeatAgent(inputCount, outputCount);
        }

        threads = new TrainThread[threadCount];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new TrainThread(scenario, this, gradientDescentIterations);
            threads[i].start();
        }
    }

    public AgentScore getBest() {
        return new AgentScore(getBestAgent(), getBestScore());
    }

    public void train() {
        startTraining();
        waitForThreads();
        double sum = prepareLists();
        prepareNextGeneration(sum);
    }

    private void prepareNextGeneration(final double sum) {
        final int percentageKept = agentCount / Configuration.KEPT_AGENT_PERCENTAGE;
        if (Random.chanceOf(0.005)) {
            final CountDownLatch count = new CountDownLatch(agents.length);
            for (final var agent : agents) {
                executor.execute(() -> {
                    threads[0].gradientDescent(agent);
                    count.countDown();
                });
            }
            try {
                count.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < agentCount; i++) {
            if (i < percentageKept) {
                agents[i] = agentScores[i].agent;

            } else {
                double r = Random.randomDoubleInRange(0, sum);
                for (int j = agentScores.length - 1; j >= 0; j--) {
                    final AgentScore agentScore = agentScores[j];
                    if (r >= agentScore.score) {
                        agents[i] = agentScore.agent.clone();
                        agents[i].mutate();
                        break;
                    }
                }
            }
        }
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

    private void waitForThreads() {
        boolean allFree = false;
        outer:
        while (!allFree) {
            for (TrainThread thread : threads) {
                if (!thread.isFree) {
                    continue outer;
                }
            }
            allFree = true;
        }
    }

    private void startTraining() {
        for (int i = 0; i < agentCount; i++) {
            boolean shouldContinue = false;
            while (!shouldContinue) {
                for (final TrainThread thread : threads) {
                    if (thread.isFree) {
                        thread.startTask(i);
                        shouldContinue = true;
                        break;
                    }
                }
                //scores[i] = scenario.getScore(agents[i]);
                //agentScores[i] = new AgentScore(agents[i], scores[i]);
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
        for (TrainThread thread : threads) {
            thread.shouldRun = false;
        }
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
