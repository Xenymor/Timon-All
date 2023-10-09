package NeuralNetwork;

import StandardClasses.Random;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final int FRUIT_COUNT = 300;
    private static double learnRate = 0.1;
    public static final int CHUNK_SIZE = 10;

    public static void main(String[] args) throws InterruptedException {
        new Main().run();
    }

    private void run() throws InterruptedException {
        Fruit[] trainingData = getFruits(FRUIT_COUNT);
        List<Fruit[]> trainingDataChunks = getChunks(Fruit[].class, trainingData, CHUNK_SIZE);
        NeuralNetwork neuralNetwork = new NeuralNetwork(2, 20, 3, 1);
        MyFrame myFrame = new MyFrame(neuralNetwork, trainingData);
        myFrame.setSize(1000, 1000);
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.setUndecorated(true);
        myFrame.setVisible(true);
        TimeUnit.MILLISECONDS.sleep(1);
        long start = System.nanoTime();
        double lastCost = -1;
        int trues = testNeuralNetwork(trainingData, neuralNetwork);

        while (trues < trainingData.length) {
            for (Fruit[] fruits : trainingDataChunks) {
                for (int j = 0; j < fruits.length; j++) {
                    neuralNetwork.learn(fruits, learnRate);

                    if (System.nanoTime() - start >= TimeUnit.SECONDS.toNanos(10)) {
                        trues = testNeuralNetwork(trainingData, neuralNetwork);
                        start = System.nanoTime();
                        double cost = neuralNetwork.getCost3(trainingData);
                        if (cost <= learnRate*3) {
                            learnRate *= 0.1;
                        }
                        System.out.println(cost);
                        if (cost == lastCost) {
                            trues = testNeuralNetwork(trainingData, neuralNetwork);
                            System.exit(0);
                        }
                        lastCost = cost;
                    }
                }
            }
        }
    }

    private int testNeuralNetwork(final Fruit[] trainingData, final NeuralNetwork neuralNetwork) {
        int trues;
        trues = 0;
        for (Fruit trainingDatum : trainingData) {//test
            double[] outputs = neuralNetwork.getOutputs(trainingDatum);
            if ((Math.round(outputs[0]) == 1) == trainingDatum.isPoisonous) {//checks result
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
        }
        Fruit[] trainingData = new Fruit[lengths.length];
        for (int i = 0; i < lengths.length; i++) {
            trainingData[i] = new Fruit(lengths[i], widths[i], poisonous[i]);
        }
        return trainingData;
    }

    private double sqr(double v) {
        return v * v;
    }

    public static <T> List<T[]> getChunks(Class<T[]> ctor, T[] array, int chunkSize) {
        List<T[]> outList = new ArrayList<>();
        int counter = 0;
        T[] current = ctor.cast(java.lang.reflect.Array.newInstance(ctor.getComponentType(), chunkSize));
        for (T t : array) {
            if (counter == chunkSize) {
                counter = 0;
                outList.add(current);
                current = ctor.cast(java.lang.reflect.Array.newInstance(ctor.getComponentType(), chunkSize));
            }
            current[counter] = t;
            counter++;
        }
        return outList;
    }

    private static class Fruit implements DataPoint {
        final double[] inputs;
        final double[] expectedOutputs;
        boolean isPoisonous;


        public Fruit(double length, double width, boolean poisonous) {
            inputs = new double[]{length, width};
            expectedOutputs = new double[]{poisonous ? 1 : 0};
            isPoisonous = poisonous;
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
                TimeUnit.MILLISECONDS.sleep(1_000);
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
                g.fillOval((int) (inputs[0] * getWidth())-3, (int) (inputs[1] * getWidth())-3, 16, 16);
                if (trainingData[i].isPoisonous()) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.BLUE);
                }
                g.fillOval((int) (inputs[0] * getWidth()), (int) (inputs[1] * getWidth()), 10, 10);
            }
        }
    }
}
