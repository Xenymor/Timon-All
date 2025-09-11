package NeuralNetworkProjects.DigitIdentification;

import NeuralNetwork.DataPoint;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.NeuralNetworkType;
import NeuralNetworkProjects.DigitIdentification.FileReading.ImageReader;
import NeuralNetworkProjects.DigitIdentification.FileReading.LabelReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static StandardClasses.MyArrays.getChunks;

@SuppressWarnings("FieldCanBeLocal")
public class MainDigitIdentification {

    public static final int CHUNK_SIZE = 100;
    private static final String SAVE_FILE_PATH = "src/NeuralNetworkProjects/DigitIdentification/neuralNetwork.nn";
    private static final int ZOOM = 10;
    public static final int SIZE = 28 * ZOOM;
    private double learnRate = 0.05;
    private static final double LEARN_RATE_DECAY = 0.000001;
    private final double MOMENTUM = 0.8;
    private final double REGULARIZATION = 0.1;

    public MainDigitIdentification(final NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    public MainDigitIdentification() {
        neuralNetwork = new NeuralNetwork(NeuralNetworkType.GRADIENT_DESCENT, 784, 1400, 500, 250, 100, 10);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //new MainDigitIdentification().run();
        new MainDigitIdentification().test();
    }

    private void test() throws IOException, ClassNotFoundException {
        if (!loadNeuralNetwork())
            throw new FileNotFoundException();
        MyFrame myFrame = new MyFrame();
        myFrame.setSize(SIZE + 100, SIZE);
        myFrame.setUndecorated(true);
        myFrame.setVisible(true);
        myFrame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent e) {
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                myFrame.mouseDown = true;
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                myFrame.mouseDown = false;
            }

            @Override
            public void mouseEntered(final MouseEvent e) {

            }

            @Override
            public void mouseExited(final MouseEvent e) {
                myFrame.mouseDown = false;
            }
        });
    }

    NeuralNetwork neuralNetwork;

    private void run() throws IOException, ClassNotFoundException {
        ImageReader imageReader = new ImageReader(new File("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\DigitIdentification\\train-images-idx3-ubyte\\train-images-idx3-ubyte"));
        LabelReader labelReader = new LabelReader(new File("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\DigitIdentification\\train-labels-idx1-ubyte\\train-labels-idx1-ubyte"));

        TrainingDataPoint[] dataPoints = getDataPoints(imageReader, labelReader);
        imageReader.close();
        labelReader.close();
        List<TrainingDataPoint[]> dataChunks = getChunks(TrainingDataPoint[].class, dataPoints, CHUNK_SIZE);

        imageReader = new ImageReader(new File("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\DigitIdentification\\t10k-images-idx3-ubyte\\t10k-images-idx3-ubyte"));
        labelReader = new LabelReader(new File("C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\DigitIdentification\\t10k-labels-idx1-ubyte\\t10k-labels-idx1-ubyte"));
        TrainingDataPoint[] testData = getDataPoints(imageReader, labelReader);
        imageReader.close();
        labelReader.close();

        loadNeuralNetwork();

        double lastCost = neuralNetwork.getCost(testData);
        System.out.println("Starting training");
        int epochCounter = 0;
        while (true) {
            for (int i = 0; i < dataChunks.size(); i++) {
                neuralNetwork.learn(dataChunks.get(i), learnRate, REGULARIZATION, MOMENTUM);
                if (i % 10 == 0)
                    System.out.println("Learned: " + Math.round((double) i / dataChunks.size() * 10000) / 100d + "%");
            }
            double cost = neuralNetwork.getCost(testData);
            final double costDifference = cost - lastCost;
            System.out.println(cost + ";\t" + (costDifference >= 0 ? "\033[0;31m +" : "\033[0;32m ") + costDifference + "\033[0m");
            if (cost < lastCost) {
                saveNeuralNetwork(neuralNetwork);
            }
            if (cost == lastCost) {
                learnRate *= 0.1;
            }
            System.out.println("Learnrate: " + learnRate);
            learnRate *= 1 / (1 + LEARN_RATE_DECAY * epochCounter);
            lastCost = cost;
        }
    }

    private boolean loadNeuralNetwork() throws IOException, ClassNotFoundException {
        if (Files.exists(Path.of(SAVE_FILE_PATH))) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(SAVE_FILE_PATH));
            neuralNetwork = (NeuralNetwork) objectInputStream.readObject();
            objectInputStream.close();
            System.out.println("Loaded Neural Network");
            return true;
        }
        return false;
    }

    private void saveNeuralNetwork(final NeuralNetwork neuralNetwork) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_PATH));
        objectOutputStream.writeObject(neuralNetwork);
        objectOutputStream.close();
        System.out.println("Saved Neural Network");
    }

    private TrainingDataPoint[] getDataPoints(final ImageReader imageReader, final LabelReader labelReader) throws IOException {
        if (imageReader.getNumberOfItems() != labelReader.getNumberOfItems()) {
            throw new RuntimeException("The count of items differs");
        }
        TrainingDataPoint[] result = new TrainingDataPoint[imageReader.getNumberOfItems()];
        for (int i = 0; i < result.length; i++) {
            imageReader.selectNext();
            labelReader.selectNext();

            final byte label = labelReader.getCurrentValue();
            byte[] imgBytes = imageReader.getCurrentData();
            double[] inputs = new double[imgBytes.length];
            for (int j = 0; j < imgBytes.length; j++) {
                final byte data = imgBytes[j];
                int gray = 255 - (((int) data) & 0xFF);
                inputs[j] = gray;
            }
            result[i] = new TrainingDataPoint(label, inputs);
        }

        labelReader.close();
        imageReader.close();

        return result;
    }

    public static class TrainingDataPoint implements DataPoint {
        final double[] inputs;
        final double[] expectedOutputs;

        public TrainingDataPoint(final byte label, final double[] inputs) {
            this.inputs = inputs;
            this.expectedOutputs = getEmptyArrayExcept(label);
        }

        @Override
        public double[] inputs() {
            return inputs;
        }

        @Override
        public double[] expectedOutputs() {
            return expectedOutputs;
        }
    }

    private static double[] getEmptyArrayExcept(final int index) {
        double[] result = new double[10];
        Arrays.fill(result, 0);
        result[index] = 1;
        return result;
    }

    private class MyFrame extends JFrame {
        public boolean mouseDown = false;
        final boolean[][] pixels;

        public MyFrame() throws HeadlessException {
            pixels = new boolean[SIZE][SIZE];
        }

        BufferedImage buffer;
        final BufferedImage resizedImage = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
        final Graphics2D graphics1 = resizedImage.createGraphics();

        @Override
        public void paint(final Graphics g) {
            if (buffer == null)
                buffer = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
            if (mouseDown) {
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                final Point locationOnScreen = getLocationOnScreen();
                int xPos = mousePos.x - locationOnScreen.x;
                int yPos = mousePos.y - locationOnScreen.y;
                if (xPos < SIZE && yPos < SIZE && xPos > 0 && yPos > 0) {
                    pixels[(xPos / ZOOM) * ZOOM][(yPos / ZOOM) * ZOOM] = true;
                }
            }
            Graphics graphics = buffer.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, getWidth(), getHeight());
            graphics.setColor(Color.BLACK);
            for (int x = 0; x < pixels.length; x += ZOOM) {
                for (int y = 0; y < pixels[x].length; y += ZOOM) {
                    if (pixels[x][y]) {
                        graphics.fillRect(x, y, ZOOM, ZOOM);
                    }
                }
            }
            graphics1.drawImage(buffer, 0, 0, 28, 28, null);
            for (int x = 0; x < resizedImage.getWidth(); x++) {
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    int rgb = resizedImage.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    int gray = (red + green + blue) / 3;
                    resizedImage.setRGB(x, y, gray);
                }
            }
            double[] inputs = new double[28 * 28];
            for (int i = 0; i < inputs.length; i++) {
                inputs[i] = 255 - (resizedImage.getRGB(i % 28, i / 28) & 0xFF);
            }
            double[] outputs = neuralNetwork.getOutputs(inputs);
            g.clearRect(0, 0, getWidth(), getHeight());
            g.drawString("Guess: " + getHighestIndex(outputs), SIZE + 10, SIZE / 2);
            g.drawImage(buffer, 0, 0, null);
            repaint(20);
        }

        private int getHighestIndex(final double[] outputs) {
            double highest = Double.NEGATIVE_INFINITY;
            int highestIndex = -1;
            for (int i = 0; i < outputs.length; i++) {
                final double v = outputs[i];
                if (v > highest) {
                    highest = v;
                    highestIndex = i;
                }
            }
            return highestIndex;
        }
    }
}