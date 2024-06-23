package NeuralNetwork.NEAT;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NeatTest {

    public static final double TEST_RANGE = Math.PI;
    public static final int BLOCK_SIZE = 4;
    public static final int SAMPLE_SIZE = 200;

    public static void main(String[] args) {
        final EasyScenario easyScenario = new EasyScenario();
        NeatTrainer trainer = new NeatTrainer(1, 1, 100, easyScenario, 8);
        double bestScore = Double.NEGATIVE_INFINITY;
        int iteration = 0;

        MyFrame panel = new MyFrame(TEST_RANGE, trainer.agents[0], BLOCK_SIZE);
        panel.setSize(1290, 600);
        panel.setUndecorated(true);
        panel.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        panel.setVisible(true);

        while (bestScore < 410) {
            trainer.train();
            if (iteration % 100 == 0) {
                bestScore = trainer.getBestScore();
                final NeatAgent bestAgent = trainer.getBestAgent();
                panel.agent = bestAgent;
                panel.repaint();
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
        NeatAgent agent;
        final int blockSize;
        BufferedImage image;

        public MyFrame(final double range, NeatAgent agent, int blockSize) {
            this.range = range;
            this.agent = agent;
            this.blockSize = blockSize;
        }

        @Override
        public void setSize(final int width, final int height) {
            super.setSize(width, height);
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void paint(final Graphics graphics) {
            Graphics g = image.getGraphics();
            final int height = getHeight();
            final int halfHeight = height / 2;
            g.clearRect(0, 0, getWidth(), height);
            final int width = getWidth();
            final int valueCount = width / blockSize;
            double[] values = new double[valueCount];
            double[] outputs = new double[valueCount];
            double stepSize = (range * BLOCK_SIZE) / (valueCount);
            int i = 0;
            for (double x = -range; i < values.length; x += stepSize, i++) {
                values[i] = Math.sin(x);
                outputs[i] = agent.getOutputs(x)[0];
            }

            double valueScale = 0.4 * height;
            for (int x = 0; x < valueCount; x++) {
                g.setColor(Color.green);
                g.fillRect(x * blockSize, (int) (values[x] * valueScale) + halfHeight, blockSize, blockSize);
                g.setColor(Color.red);
                g.fillRect(x * blockSize, (int) (outputs[x] * valueScale) + halfHeight, blockSize, blockSize);
            }
            graphics.drawImage(image, 0, 0, null);
        }
    }

    private static class EasyScenario implements NeatScenario {
        final double[] testNumbers;

        public EasyScenario() {
            testNumbers = new double[SAMPLE_SIZE];
            double stepSize = (TEST_RANGE * 2d) / SAMPLE_SIZE;
            for (int i = 0; i < testNumbers.length; i++) {
                testNumbers[i] = -TEST_RANGE + stepSize * i;
            }
        }

        @Override
        public double getScore(final NeatAgent agent) {
            double score = 0;
            for (double r : testNumbers) {
                double output = agent.getOutputs(r)[0];
                score += Math.abs(Math.sin(r) - output);
            }
            return 400 - score + agent.getHiddenCount() * 20;
        }
    }
}
