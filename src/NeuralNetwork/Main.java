package NeuralNetwork;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static StandardClasses.MyArrays.getChunks;

public class Main {

    public static final int FRUIT_COUNT = 1_000;
    private static final double INITIAL_LEARNING_RATE = 0.5;
    public static final int CHUNK_SIZE = 100;
    private static final double LEARN_RATE_DECAY = 0.075;

    public static void main(String[] args) throws InterruptedException {
        new Main().run();
    }

    private void run() throws InterruptedException {
        double learnRate = INITIAL_LEARNING_RATE;
        Fruit[] trainingData = getFruits(FRUIT_COUNT);
        List<Fruit[]> trainingDataChunks = getChunks(Fruit[].class, trainingData, CHUNK_SIZE);
        NeuralNetwork neuralNetwork = new NeuralNetwork(NeuralNetworkType.GRADIENT_DESCENT, 2, 50, 4, 2);
        MyFrame myFrame = new MyFrame(neuralNetwork, trainingData);
        myFrame.setSize(1000, 1000);
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.setUndecorated(true);
        myFrame.setVisible(true);
        TimeUnit.MILLISECONDS.sleep(1);
        long start = System.nanoTime();
        double lastCost = neuralNetwork.getCost(trainingData);
        int trues = testNeuralNetwork(trainingData, neuralNetwork);
        int counter = 0;
        while (trues < trainingData.length) {
            for (Fruit[] fruits : trainingDataChunks) {
                neuralNetwork.learn(fruits, learnRate);
                counter++;
            }
            if (System.nanoTime() - start >= TimeUnit.SECONDS.toNanos(10)) {
                System.out.println(TimeUnit.NANOSECONDS.toMicros((System.nanoTime() - start) / counter) + " microSec/Batch");
                trues = testNeuralNetwork(trainingData, neuralNetwork);
                start = System.nanoTime();
                double cost = neuralNetwork.getCost(trainingData);
                final double costDifference = cost - lastCost;
                System.out.println(cost + ";\t" + (costDifference >= 0 ? "\033[0;31m +" : "\033[0;32m ") + costDifference + "\033[0m" + "\tLearnrate: " + learnRate);
                //learnRate = (1.0 / (1.0 + LEARN_RATE_DECAY * (trainingDataChunks.size()/counter))) * INITIAL_LEARNING_RATE;
                learnRate *= 0.99;
                lastCost = cost;
                Thread.sleep(1);
            }
        }
    }

    private int testNeuralNetwork(final Fruit[] trainingData, final NeuralNetwork neuralNetwork) {
        int trues;
        trues = 0;
        for (Fruit trainingDatum : trainingData) {//test
            double[] outputs = neuralNetwork.getOutputs(trainingDatum);
            if ((outputs[0] >= outputs[1]) == trainingDatum.isPoisonous) {//checks result
                trues++;
            }
        }
        System.out.println(trues + " of " + trainingData.length + " correct");
        return trues;
    }

    @SuppressWarnings("CommentedOutCode")
    private Fruit[] getFruits(int fruitCount) {
        double[] lengths = new double[fruitCount];
        double[] widths = new double[fruitCount];
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] = Math.random();
        }
        for (int i = 0; i < widths.length; i++) {
            widths[i] = Math.random();
        }
        boolean[] poisonous = new boolean[fruitCount];//if true is poisonous
        //1<2y+sin(x^3)*1.2x
        for (int i = 0; i < poisonous.length; i++) {
            double x = widths[i];
            double y = lengths[i];
            //poisonous[i] = y + x > 1;
            //poisonous[i] = 0.25<x*y;
            //poisonous[i] = sqr(x - 0.5) + sqr(y - 0.5) < 0.25;
            poisonous[i] = sqr(x - 0.5) + sqr(y - 0.5) < 0.15;
            //poisonous[i] = x + y > Math.pow(x, y);
            //poisonous[i] = y < 0.5;
            //poisonous[i] = sqr(sqr(x - 0.5)) + sqr(y - 0.5) < 0.08;
            //poisonous[i] = (sqr(sqr(x - 0.5)) + sqr(y - 0.5) < 0.08) || (sqr(x - 0.5) + sqr(sqr(y - 0.5)) < 0.08);
            //poisonous[i] = (Math.abs(x - 0.5) < 0.2) || (Math.abs(y - 0.5) < 0.2);
            //poisonous[i] = ((((int) (x * 3)) ^ ((int) (y * 3))) & 1) == 0;
            //poisonous[i] = Math.tan(x*10) > y;
            //poisonous[i] = Math.sin(10*x) > Math.cos(10*y);
            //poisonous[i] = Math.cos(50*x) - Math.pow((y), 1/3d) + 1 < Math.log(50*x);
        }
        Fruit[] trainingData = new Fruit[lengths.length];
        for (int i = 0; i < lengths.length; i++) {
            trainingData[i] = new Fruit(poisonous[i], lengths[i], widths[i]);
        }
        return trainingData;
    }

    private double sqr(double v) {
        return v * v;
    }

    private static class Fruit implements DataPoint {
        final double[] inputs;
        final double[] expectedOutputs;
        final boolean isPoisonous;


        public Fruit(boolean poisonous, double... inputs) {
            this.inputs = inputs;
            isPoisonous = poisonous;
            if (isPoisonous) {
                expectedOutputs = new double[]{1, 0};
            } else {
                expectedOutputs = new double[]{0, 1};
            }
        }

        boolean isPoisonous() {
            return isPoisonous;
        }

        @Override
        public double[] inputs() {
            return inputs.clone();
        }

        @Override
        public double[] expectedOutputs() {
            return expectedOutputs.clone();
        }
    }

    private static class MyFrame extends JFrame {
        final NeuralNetwork neuralNetwork;
        final Fruit[] trainingData;

        public MyFrame(NeuralNetwork neuralNetwork, Fruit[] trainingData) {
            this.neuralNetwork = neuralNetwork;
            this.trainingData = trainingData;
        }

        @Override
        public void paint(Graphics g) {
            drawNeuralNetwork(neuralNetwork, g);
            repaint();
            try {
                TimeUnit.MILLISECONDS.sleep(5_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void drawNeuralNetwork(NeuralNetwork neuralNetwork, Graphics g) {
            double[] outputs;
            final double width = getWidth();
            final double height = getHeight();
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            final WritableRaster raster = image.getRaster();
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    outputs = neuralNetwork.getOutputs(x / width, y / height);
                    image.setRGB(x, y, outputs[0] > outputs[1] ? Color.GREEN.getRGB() : new Color(136, 0, 145).getRGB());
                }
            }
            g.clearRect(0, 0, getWidth(), getHeight());
            g.drawImage(image, 0, 0, null);
            for (final Fruit testDatum : trainingData) {
                double[] inputs = testDatum.inputs();
                if (testDatum.isPoisonous()) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.BLUE);
                }
                g.fillOval((int) (inputs[0] * getWidth()), (int) (inputs[1] * getHeight()) - 3, 10, 10);
            }
        }
    }
}
