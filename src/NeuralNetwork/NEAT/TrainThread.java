package NeuralNetwork.NEAT;

public class TrainThread extends Thread {
    private final NeatScenario scenario;
    private final NeatTrainer trainer;

    public TrainThread(final NeatScenario scenario, final NeatTrainer trainer) {
        this.scenario = scenario;
        this.trainer = trainer;
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

    public void startTask(final int i) {
        index = i;
        isFree = false;
    }
}
