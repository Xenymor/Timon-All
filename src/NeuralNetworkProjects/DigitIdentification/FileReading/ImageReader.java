package NeuralNetworkProjects.DigitIdentification.FileReading;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageReader extends FileReader {
    private final int imageWidth;
    private final int imageHeight;
    private byte[] currentData;

    public ImageReader(final File mnistImageFile) throws IOException {
        super(mnistImageFile, 2051);

        imageHeight = (int) readUnsigned32();
        imageWidth = (int) readUnsigned32();

        System.out.println(mnistImageFile.getName() + " contains " + getNumberOfItems() + " images with " + imageWidth + "x" + imageHeight + " pixels");
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void selectNext() throws IOException {
        if (!hasNext()) {
            throw new IOException("No more data");
        }

        currentData = readData(new byte[imageWidth * imageHeight]);

        incrementCurrentIndex();
    }

    public byte[] getCurrentData() {
        return currentData;
    }

    public BufferedImage getDataAsBufferedImage(final byte[] values) {
        final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY);

        int index = 0;
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++, index++) {
                final byte data = values[index];
                int gray = 255 - (((int) data) & 0xFF);
                int rgb = gray | (gray << 8) | (gray << 16);
                image.setRGB(x, y, rgb);
            }
        }

        return image;
    }

}