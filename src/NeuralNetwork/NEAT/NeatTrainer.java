package NeuralNetwork.NEAT;

import StandardClasses.Random;

import java.util.Arrays;

public class NeatTrainer {
    final int inputCount;
    final int outputCount;
    final int agentCount;
    final NeatAgent[] agents;
    final AgentScore[] agentScores;
    private AgentScore best;
    private final TrainThread[] threads;

    public NeatTrainer(final int inputCount, final int outputCount, final int agentCount, final NeatScenario scenario, final int threadCount) {
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        this.agentCount = agentCount;

        agents = new NeatAgent[agentCount];
        agentScores = new AgentScore[agentCount];
        for (int i = 0; i < agents.length; i++) {
            agents[i] = new NeatAgent(inputCount, outputCount);
        }

        threads = new TrainThread[threadCount];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new TrainThread(scenario, this);
            threads[i].start();
        }
    }

    public AgentScore getBest() {
        return new AgentScore(best.agent.clone(), best.score);
    }

    public void train() {
        startTraining();
        waitForThreads();
        double sum = prepareLists();
        prepareNextGeneration(sum);
    }

    private void prepareNextGeneration(final double sum) {
        final int percentageKept = agentCount / Configuration.KEPT_AGENT_PERCENTAGE;
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
        best = agentScores[0];
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
