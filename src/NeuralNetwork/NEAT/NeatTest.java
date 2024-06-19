package NeuralNetwork.NEAT;

public class NeatTest {
    public static void main(String[] args) {
        final EasyScenario easyScenario = new EasyScenario();
        NeatTrainer trainer = new NeatTrainer(1, 1, 100, easyScenario, 8);
        double bestScore = Double.NEGATIVE_INFINITY;
        int iteration = 0;
        while (bestScore < 219) {
            trainer.train();
            if (iteration % 100 == 0) {
                bestScore = trainer.getBestScore();
                final int hiddenCount = trainer.getBestAgent().getHiddenCount();
                System.out.println(iteration + ":" + (bestScore-hiddenCount*20) + " \t" + hiddenCount);
            }
            iteration++;
        }
        trainer.stop();
        System.out.println("1: " + trainer.getBestAgent().getOutputs(1d)[0]);
    }

    private static class EasyScenario implements NeatScenario {
        final double[] random;
        public EasyScenario() {
            random = new double[100];
            for (int i = 0; i < random.length; i++) {
                random[i] = StandardClasses.Random.randomDoubleInRange(-2, 2);
            }
        }

        @Override
        public double getScore(final NeatAgent agent) {
            double score = 0;
            for (double r : random) {
                double output = agent.getOutputs(r)[0];
                score += Math.abs(Math.sin(r) - output);
            }
            return 200 - score + agent.getHiddenCount() * 20;
        }
    }
}
