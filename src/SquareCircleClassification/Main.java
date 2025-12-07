package SquareCircleClassification;

import NeuralNetwork.DataPoint;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.NeuralNetworkType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("SameParameterValue")
public class Main {
    private static final double LEARN_RATE = 0.1;
    public static final int CHUNK_SIZE = 10;
    private static final int TRAINING_REPETITIONS = 5;
    final int IMAGE_COUNT = 300;//Should be even
    final String filePath = "src/SquareCircleClassification/Images";

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        //main.createImages();
        main.run();
    }

    private void createImages() throws IOException {
        for (int i = 0; i < IMAGE_COUNT / 2; i++) {
            BufferedImage img = getCircleImage(28, 28, CHUNK_SIZE, CHUNK_SIZE);
            //makeGray(img);
            File output = new File(filePath + "/CircleImage" + i + ".png");
            output.createNewFile();
            ImageIO.write(img, "jpg", output);
        }
        for (int i = 0; i < IMAGE_COUNT / 2; i++) {
            BufferedImage img = getSquareImage(28, 28, CHUNK_SIZE, CHUNK_SIZE);
            //makeGray(img);
            File output = new File(filePath + "/SquareImage" + i + ".png");
            output.createNewFile();
            ImageIO.write(img, "png", output);
        }
    }

    private BufferedImage getSquareImage(int imgWidth, int imgHeight, int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        //drawing the circle
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.white);
        g.fillRect((int) (Math.random() * (imgWidth - width)), (int) (Math.random() * (imgHeight - height)), width, height);
        return bufferedImage;
    }

    public static BufferedImage getCircleImage(int imgWidth, int imgHeight, int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        //drawing the circle
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.white);
        g.fillOval((int) (Math.random() * (imgWidth - width)), (int) (Math.random() * (imgHeight - height)), width, height);
        return bufferedImage;
    }

    public static void makeGray(BufferedImage img) {
        for (int x = 0; x < img.getWidth(); ++x)
            for (int y = 0; y < img.getHeight(); ++y) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                final int grayLevel = getGrayLevel(rgb, r);
                int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
                img.setRGB(x, y, gray);
            }
    }

    private static int getGrayLevel(final int rgb, final int r) {
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb & 0xFF);

        // Normalize and gamma correct:
        double rr = Math.pow(r / 255.0, 2.2);
        double gg = Math.pow(g / 255.0, 2.2);
        double bb = Math.pow(b / 255.0, 2.2);

        // Calculate luminance:
        double lum = 0.2126 * rr + 0.7152 * gg + 0.0722 * bb;

        // Gamma compand and rescale to byte range:
        return (int) (255.0 * Math.pow(lum, 1.0 / 2.2));
    }

    private void run() throws IOException {
        Form[] trainingData = getTrainingData();
        NeuralNetwork neuralNetwork = new NeuralNetwork(NeuralNetworkType.GRADIENT_DESCENT, 784, 50, 5, 2);
        System.out.println(neuralNetwork.getCost(trainingData));
        trainInChunks(trainingData, neuralNetwork, CHUNK_SIZE, LEARN_RATE, TRAINING_REPETITIONS);
    }

    private void trainInChunks(Form[] trainingData, NeuralNetwork neuralNetwork, int chunkSize, double learnRate, int trainingRepetitions) {
        int chunkCount = trainingData.length / chunkSize;
        List<Form> list = Arrays.asList(trainingData);
        Collections.shuffle(list);
        trainingData = list.toArray(trainingData);
        Form[][] chunks = new Form[chunkCount][chunkSize];
        int counter = 0;
        for (int i = 0; i < chunkCount; i++) {
            for (int j = 0; j < chunkSize; j++) {
                chunks[i][j] = trainingData[counter];
                counter++;
            }
        }
        for (int i = 0; i < trainingRepetitions; i++) {
            for (final Form[] chunk : chunks) {
                neuralNetwork.learn(chunk, learnRate);
                System.out.println(neuralNetwork.getCost(trainingData));
            }
        }
    }

    private Form[] getTrainingData() throws IOException {
        BufferedImage[] circles = new BufferedImage[IMAGE_COUNT / 2];
        for (int i = 0; i < IMAGE_COUNT / 2; i++) {
            circles[i] = ImageIO.read(new File(filePath + "/CircleImage" + i + ".png"));
        }
        BufferedImage[] squares = new BufferedImage[IMAGE_COUNT / 2];
        for (int i = 0; i < IMAGE_COUNT / 2; i++) {
            squares[i] = ImageIO.read(new File(filePath + "/SquareImage" + i + ".png"));
        }
        Form[] trainingData = new Form[IMAGE_COUNT];
        for (int i = 0; i < circles.length; i++) {
            trainingData[i] = new Form(circles[i], true);
        }
        for (int i = 0; i < squares.length; i++) {
            trainingData[i + IMAGE_COUNT / 2] = new Form(squares[i], false);
        }
        return trainingData;
    }

    private record Form(double[] inputs, double[] expectedOutputs) implements DataPoint {
        private Form(BufferedImage inputs, boolean expectedOutputs) {
            double[] expOut = expectedOutputs ? new double[]{1, 0} : new double[]{0, 1};
            double[] inp = new double[inputs.getHeight() * inputs.getWidth()];
            int counter = 0;
            for (int i = 0; i < inputs.getWidth(); i++) {
                for (int j = 0; j < inputs.getHeight(); j++) {
                    int color = inputs.getRGB(i, j);
                    int red = (color >>> 16) & 0xFF;
                    int green = (color >>> 8) & 0xFF;
                    int blue = (color) & 0xFF;
                    double luminance = (red * 0.2126d + green * 0.7152d + blue * 0.0722d) / 255d;
                    inp[counter] = luminance;
                    counter++;
                }
            }
            this(inp, expOut);
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
}
