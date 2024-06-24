package NeuralNetwork.NEAT;

import StandardClasses.MyMath;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NeatTest {

    public static final double TEST_RANGE = Math.PI;
    public static final int BLOCK_SIZE = 4;
    public static final int SAMPLE_SIZE = 200;

    public static void main(String[] args) {
        final EasyScenario scenario = new EasyScenario();
        NeatTrainer trainer = new NeatTrainer(1, 1, 100, scenario, 8);
        double bestScore = Double.NEGATIVE_INFINITY;
        int iteration = 0;

        MyFrame frame = new MyFrame(TEST_RANGE, trainer.agents[0], BLOCK_SIZE, scenario);
        frame.setSize(1290, 600);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        while (bestScore < 410) {
            trainer.train();
            if (iteration % 100 == 0) {
                bestScore = trainer.getBestScore();
                final NeatAgent bestAgent = trainer.getBestAgent();
                frame.newAgent = bestAgent;
                frame.repaint();
                final int hiddenCount = bestAgent.getHiddenCount();
                System.out.println(iteration + ":" + (bestScore - hiddenCount * 20) + " \t" + hiddenCount);
            }
            iteration++;
        }
        trainer.stop();
        System.out.println("1: " + trainer.getBestAgent().getOutputs(1d)[0]);
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
                g.fillRect(x * blockSize, (int) (MyMath.map(values[x], lowest, highest, fourTenthsHeight, -fourTenthsHeight)) + halfHeight, blockSize, blockSize);
                g.setColor(Color.red);
                g.fillRect(x * blockSize, (int) (MyMath.map(outputs[x], lowest, highest, fourTenthsHeight, -fourTenthsHeight)) + halfHeight, blockSize, blockSize);
            }
            graphics.drawImage(image, 0, 0, null);
        }
    }

    private static class EasyScenario implements NeatScenario {
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
        public double getExpectedOutput(final double v) {
            return (Math.abs(v) % 2) - 1;
        }

        @Override
        public double getScore(final NeatAgent agent) {
            double score = 0;
            for (int i = 0; i < testNumbers.length; i++) {
                final double r = testNumbers[i];
                double output = agent.getOutputs(r)[0];
                score += Math.abs(expectedOutputs[i] - output);
            }
            return 400 - score + agent.getHiddenCount() * 20;
        }
    }
}
