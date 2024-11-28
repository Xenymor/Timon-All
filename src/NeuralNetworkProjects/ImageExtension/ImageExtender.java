package NeuralNetworkProjects.ImageExtension;

import NeuralNetwork.DataPoint;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.NeuralNetworkType;
import StandardClasses.MyArrays;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageExtender {
    private static final String IMAGE_PATH = "C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\NeuralNetworkProjects\\ImageExtension\\camouflage.png";
    public static final int INPUT_WIDTH = 5;
    public static final int INPUT_HEIGHT = 6;
    private static final int CHUNK_SIZE = 100;
    private static final int TO_EXTEND = 100;
    private static final double LEARN_RATE_DECAY = 0.000001;

    private final BufferedImage image;
    private final NeuralNetwork neuralNetwork;
    final List<DataPoint[]> trainingData;
    private double learnRate = 0.01;
    @SuppressWarnings("FieldCanBeLocal")
    private final double momentum = 0.8;
    @SuppressWarnings("FieldCanBeLocal")
    private final double regularization = 0.1;

    public ImageExtender(final BufferedImage image) {
        this.image = image;
        neuralNetwork = new NeuralNetwork(NeuralNetworkType.GRADIENT_DESCENT, INPUT_HEIGHT * INPUT_WIDTH * 3, 300, 50, 10, 3);
        ArrayList<PixelDataPoint> allTrainingData = new ArrayList<>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                allTrainingData.add(getDataPoint(image, x, y));
            }
        }
        Collections.shuffle(allTrainingData);
        trainingData = MyArrays.getChunks(DataPoint[].class, allTrainingData.toArray(new DataPoint[0]), CHUNK_SIZE);
    }

    private PixelDataPoint getDataPoint(final BufferedImage image, final int x, final int y) {
        double[] inputs = getInputs(image, x, y);
        double[] outputs = getRGBArray(image.getRGB(x, y));
        return new PixelDataPoint(inputs, outputs);
    }

    private double[] getRGBArray(final int rgb) {
        return new double[]{
                ((rgb >> 16) & 0xFF) / 255d,
                ((rgb >> 8) & 0xFF) / 255d,
                (rgb & 0xFF) / 255d
        };
    }

    private int toRGBInt(final double[] rgbArray) {
        int result = 0;
        result += (int) (rgbArray[0] * 255) << 16;
        result += (int) (rgbArray[1] * 255) << 8;
        result += (int) (rgbArray[2] * 255);
        return result;
    }

    private double[] getInputs(final BufferedImage image, final int x, final int y) {
        double[] inputs = new double[INPUT_HEIGHT * INPUT_WIDTH * 3];
        int index = 0;
        final int endY = Math.min(y + INPUT_HEIGHT / 2, image.getHeight());
        final int endX = Math.min(x, image.getWidth());
        final int startX = Math.max(x - INPUT_WIDTH, 0);
        final int startY = Math.max(y - INPUT_HEIGHT / 2, 0);
        for (int currX = startX; currX < endX; currX++) {
            for (int currY = startY; currY < endY; currY++) {
                int rgb = image.getRGB(currX, currY);
                inputs[index] = ((rgb >> 16) & 0xFF);
                inputs[index + 1] = ((rgb >> 8) & 0xFF);
                inputs[index + 2] = (rgb & 0xFF);
                index += 3;
            }
        }
        return inputs;
    }

    public static void main(String[] args) throws IOException {
        File imageFile = new File(IMAGE_PATH);
        BufferedImage image = ImageIO.read(imageFile);
        ImageExtender imageExtender = new ImageExtender(image);
        MyFrame myFrame = new MyFrame(image);
        myFrame.setSize(image.getWidth()+TO_EXTEND, image.getHeight());
        myFrame.setVisible(true);
        new Thread(() -> {
            long startTime = System.nanoTime();
            int epochCounter = 0;
            while (true) {
                imageExtender.train();
                if (epochCounter % 5 == 0) {
                    double total = 0;
                    final int size = imageExtender.trainingData.size();
                    for (int i = 0; i < size; i++) {
                        total += imageExtender.neuralNetwork.getCost(imageExtender.trainingData.get(i));
                    }
                    total /= size;
                    System.out.println("Cost: " + total + "\tAfter " + epochCounter + " epochs\tLearnrate: " + imageExtender.learnRate + "; With: " + (((double)epochCounter)/(System.nanoTime()-startTime))*1_000_000_000*60 + "e/min");
                }
                epochCounter++;
                myFrame.image = imageExtender.getExtendedImage();
                imageExtender.learnRate *= 1/(1+LEARN_RATE_DECAY*epochCounter);
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private BufferedImage getExtendedImage() {
        BufferedImage newImage = new BufferedImage(image.getWidth() + ImageExtender.TO_EXTEND, image.getHeight(), BufferedImage.TYPE_INT_RGB);
        newImage.getGraphics().drawImage(image, 0, 0, null);
        for (int x = image.getWidth(); x < newImage.getWidth(); x++) {
            for (int y = 0; y < newImage.getHeight(); y++) {
                newImage.setRGB(x, y, toRGBInt(neuralNetwork.getOutputs(getInputs(newImage, x, y))));
            }
        }
        return newImage;
    }

    private void train() {
        for (DataPoint[] batch : trainingData) {
            neuralNetwork.learn(batch, learnRate, regularization, momentum);
        }
    }

    private static class MyFrame extends JFrame {
        BufferedImage image;

        public MyFrame(final BufferedImage image) {
            this.image = image;
        }

        @Override
        public void paint(final Graphics g) {
            BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics graphics = buffer.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, getWidth(), getHeight());
            graphics.drawImage(image, 0, 0, null);
            g.drawImage(buffer, 0, 0, null);
            repaint(1_000);
        }
    }

    private static class PixelDataPoint implements DataPoint {
        final double[] inputs;
        final double[] expectedOutputs;

        public PixelDataPoint(final double[] inputs, final double[] expectedOutputs) {
            this.inputs = inputs;
            this.expectedOutputs = expectedOutputs;
        }

        @Override
        public double[] getInputs() {
            return inputs;
        }

        @Override
        public double[] getExpectedOutputs() {
            return expectedOutputs;
        }
    }
}
