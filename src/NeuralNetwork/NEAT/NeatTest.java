package NeuralNetwork.NEAT;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.Scanner;

public class NeatTest {

    public static final double TEST_RANGE = 1;
    public static final int BLOCK_SIZE = 4;
    public static final int SAMPLE_SIZE = 200;
    public static final int MAX_SCORE = SAMPLE_SIZE * 2;
    public static final int INPUT_COUNT = 1;
    public static final int OUTPUT_COUNT = 1;
    public static final int AGENT_COUNT = 100;
    public static final int THREAD_COUNT = 8;
    public static final int GRADIENT_DESCENT_ITERATIONS = 2;
    private static long creationNanos = System.nanoTime();

    public static void main(String[] args) throws IOException {
        final EasyScenario scenario = new EasyScenario();
        NeatTrainer trainer = new NeatTrainer(INPUT_COUNT, OUTPUT_COUNT, AGENT_COUNT, scenario, THREAD_COUNT, GRADIENT_DESCENT_ITERATIONS);
        double bestScore = Double.NEGATIVE_INFINITY;
        int iteration = 0;

        MyFrame frame = new MyFrame(TEST_RANGE, trainer.agents[0], BLOCK_SIZE, scenario);
        frame.setSize(1290, 600);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        boolean shouldBreak = false;
        Scanner scanner = new Scanner(System.in);
        while (!shouldBreak) {
            trainer.train();
            if (iteration % 1 == 0) {
                bestScore = printResults(trainer, iteration, frame);
                if (System.in.available() > 0) {
                    if (scanner.hasNextLine() && scanner.nextLine().equalsIgnoreCase("e")) {
                        shouldBreak = true;
                    }
                }
            }
            iteration++;
        }
        trainer.stop();
        printResults(trainer, iteration, frame);
    }

    private static double printResults(final NeatTrainer trainer, final int iteration, final MyFrame frame) {
        NeatTrainer.AgentScore bestAgentScore = trainer.getBest();
        frame.newAgent = bestAgentScore.agent;
        frame.repaint();
        final int hiddenCount = bestAgentScore.agent.getHiddenCount();
        System.out.println(iteration + ":" + (bestAgentScore.score / MAX_SCORE) + " \t" + hiddenCount + " / " + Duration.ofNanos(System.nanoTime() - creationNanos));
        return bestAgentScore.score;
    }

    private static class MyFrame extends JFrame {
        double range;
        NeatAgent newAgent;
        final int blockSize;
        BufferedImage image;
        NeatScenario scenario;

        public MyFrame(final double range, NeatAgent agent, int blockSize, NeatScenario scenario) {
            this.range = range;
            this.newAgent = agent;
            this.blockSize = blockSize;
            this.scenario = scenario;
        }

        @Override
        public void setSize(final int width, final int height) {
            super.setSize(width, height);
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void paint(final Graphics graphics) {
            NeatAgent agent = newAgent;
            Graphics g = image.getGraphics();
            final int height = getHeight();
            final int halfHeight = height / 2;
            final double fourTenthsHeight = height * 0.4;
            final int width = getWidth();

            final int valueCount = width / blockSize;
            double[] values = new double[valueCount];
            double[] outputs = new double[valueCount];
            double stepSize = (range * 2) / valueCount;

            int i = 0;
            double highest = Double.NEGATIVE_INFINITY;
            double lowest = Double.POSITIVE_INFINITY;

            for (double x = -range; i < values.length; x += stepSize, i++) {
                final double expectedOutput = scenario.getExpectedOutput(x);
                highest = Math.max(expectedOutput, highest);
                lowest = Math.min(expectedOutput, lowest);
                values[i] = expectedOutput;
                final double output = agent.getOutputs(x)[0];
                highest = Math.max(output, highest);
                lowest = Math.min(output, lowest);
                outputs[i] = output;
            }

            g.clearRect(0, 0, width, height);
            for (int x = 0; x < valueCount; x++) {
                g.setColor(Color.green);
                //g.fillRect(x * blockSize, (int) (MyMath.map(values[x], lowest, highest, fourTenthsHeight, -fourTenthsHeight)) + halfHeight, blockSize, blockSize);
                g.fillRect(x * blockSize, (int) (-values[x] * fourTenthsHeight) + halfHeight, blockSize, blockSize);
                g.setColor(Color.red);
                //g.fillRect(x * blockSize, (int) (MyMath.map(outputs[x], lowest, highest, fourTenthsHeight, -fourTenthsHeight)) + halfHeight, blockSize, blockSize);
                g.fillRect(x * blockSize, (int) (-outputs[x] * fourTenthsHeight) + halfHeight, blockSize, blockSize);
            }
            graphics.drawImage(image, 0, 0, null);
        }
    }

    public static class EasyScenario implements NeatScenario {
        final double[] testNumbers;
        final double[] expectedOutputs;

        public EasyScenario() {
            testNumbers = new double[SAMPLE_SIZE];
            expectedOutputs = new double[SAMPLE_SIZE];
            double stepSize = (TEST_RANGE * 2d) / SAMPLE_SIZE;
            for (int i = 0; i < testNumbers.length; i++) {
                final double v = -TEST_RANGE + stepSize * i;
                testNumbers[i] = v;
                expectedOutputs[i] = getExpectedOutput(v);
            }
        }

        @Override
        public double getExpectedOutput(final double x) {
            return Math.cos(1 - 5 * x * x * x);
        }

        @Override
        public double getScore(final NeatAgent agent) {
            double score = 0;
            for (int i = 0; i < testNumbers.length; i++) {
                final double r = testNumbers[i];
                double output = agent.getOutputs(r)[0];
                final double diff = Math.abs(expectedOutputs[i] - output);
                score += diff * diff;
            }
            return Math.max(MAX_SCORE - score - agent.getHiddenCount() * 0.25, 0);
        }
    }
}