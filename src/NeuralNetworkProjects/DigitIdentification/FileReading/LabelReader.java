package NeuralNetworkProjects.DigitIdentification.FileReading;

import java.io.File;
import java.io.IOException;

public class LabelReader extends FileReader {
    private byte currentValue;

    public LabelReader(final File mnistLabelFile) throws IOException {
        super(mnistLabelFile, 2049);
        System.out.println(mnistLabelFile.getName() + " contains " + getNumberOfItems() + " labels");
    }

    public void selectNext() throws IOException {
        incrementCurrentIndex();
        final byte[] buffy = readData(new byte[1]);
        currentValue = buffy[0];
    }

    public byte getCurrentValue() {
        return currentValue;
    }

}