package NeuralNetwork;

import StandardClasses.Random;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static StandardClasses.MyArrays.getChunks;

public class Main {

    public static final int FRUIT_COUNT = 1_000;
    private static double learnRate = .05;
    public static final int CHUNK_SIZE = 100;

    public static void main(String[] args) throws InterruptedException {
        new Main().run();
    }

    private void run() throws InterruptedException {
        Fruit[] trainingData = getFruits(FRUIT_COUNT);
        List<Fruit[]> trainingDataChunks = getChunks(Fruit[].class, trainingData, CHUNK_SIZE);
        NeuralNetwork neuralNetwork = new NeuralNetwork(2, 50, 4, 2);
        //NeuralNetwork neuralNetwork = new NeuralNetwork(1, 1, 1);
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
                System.out.println(TimeUnit.NANOSECONDS.toMicros((System.nanoTime()-start)/counter) + " microSec/Batch");
                trues = testNeuralNetwork(trainingData, neuralNetwork);
                start = System.nanoTime();
                double cost = neuralNetwork.getCost(trainingData);
                final double costDifference = cost - lastCost;
                System.out.println(cost + ";\t" + (costDifference >= 0 ? "\033[0;31m +" : "\033[0;32m ") + costDifference + "\033[0m");
                if (cost == lastCost) {
                    learnRate *= 0.1;
                }
                if (cost <= learnRate*2) {
                    learnRate *= 0.1;
                }
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
            if ((outputs[0] >= /*outputs[1]*/0.5) == trainingDatum.isPoisonous) {//checks result
                trues++;
            }
        }
        System.out.println(trues + " of " + trainingData.length + " correct");
        return trues;
    }

    private Fruit[] getFruits(int fruitCount) {
        double[] lengths = new double[fruitCount];
        double[] widths = new double[fruitCount];
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] = Random.randomDoubleInRange(1);
        }
        for (int i = 0; i < widths.length; i++) {
            widths[i] = Random.randomDoubleInRange(1);
        }
        boolean[] poisonous = new boolean[fruitCount];//if true is poisonous
        //1<2y+sin(x^3)*1.2x
        for (int i = 0; i < poisonous.length; i++) {
            //poisonous[i] = lengths[i] + widths[i] > 1;
            //poisonous[i] = 0.25<widths[i]*lengths[i];
            //poisonous[i] = sqr(widths[i] - 0.5) + sqr(lengths[i] - 0.5) < 0.25;
            poisonous[i] = sqr(widths[i] - 0.5) + sqr(lengths[i] - 0.5) < 0.15;
            //poisonous[i] = widths[i] + lengths[i] > Math.pow(widths[i], lengths[i]);
            //poisonous[i] = lengths[i] < 0.5;
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
        boolean isPoisonous;


        public Fruit(boolean poisonous, double... inputs) {
            this.inputs = inputs;
            isPoisonous = poisonous;
            if (isPoisonous) {
                expectedOutputs = new double[] {1, 0};
            } else {
                expectedOutputs = new double[] {0, 1};
            }
        }

        boolean isPoisonous() {
            return isPoisonous;
        }

        @Override
        public double[] getInputs() {
            return inputs.clone();
        }

        @Override
        public double[] getExpectedOutputs() {
            return expectedOutputs.clone();
        }
    }

    private static class MyFrame extends JFrame {
        NeuralNetwork neuralNetwork;
        Fruit[] trainingData;

        public MyFrame(NeuralNetwork neuralNetwork, Fruit[] trainingData) {
            this.neuralNetwork = neuralNetwork;
            this.trainingData = trainingData;
        }

        @Override
        public void paint(Graphics g) {
            g.clearRect(0, 0, getWidth(), getHeight());
            drawNeuralNetwork(neuralNetwork, trainingData, g);
            repaint();
            try {
                TimeUnit.MILLISECONDS.sleep(5_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void drawNeuralNetwork(NeuralNetwork neuralNetwork, Fruit[] trainingData, Graphics g) {
            double[][] outputs = neuralNetwork.getOutputs(trainingData);
            boolean[] isCorrect = new boolean[trainingData.length];
            for (int i = 0; i < isCorrect.length; i++) {
                isCorrect[i] = Math.round(outputs[i][0]) == trainingData[i].getExpectedOutputs()[0];
            }
            for (int i = 0; i < trainingData.length; i++) {
                double[] inputs = trainingData[i].getInputs();
                if (isCorrect[i]) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(136, 0, 145));
                }
                g.fillOval((int) (inputs[0] * getWidth())-3, (int) (inputs[1] * getHeight())-3, 16, 16);
                if (trainingData[i].isPoisonous()) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.BLUE);
                }
                g.fillOval((int) (inputs[0] * getWidth()), (int) (inputs[1] * getHeight())-3, 10, 10);
            }
        }
    }
}
