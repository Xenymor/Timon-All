package NeuralNetworkProjects.DigitIdentification.FileReading;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileReader {


    private final FileInputStream inputStream;
    private final long numberOfItems;
    private int currentIndex = -1;

    public FileReader(final File mnistFile, final long magic) throws IOException {
        this.inputStream = new FileInputStream(mnistFile);

        if (readUnsigned32() != magic) throw new IOException("Header is not correct");

        numberOfItems = readUnsigned32();
    }

    protected void incrementCurrentIndex() {
        currentIndex++;
    }

    protected long readUnsigned32() throws IOException {
        final byte[] buffy = readData(new byte[4]);

        long n = 0;
        int shift = 24;
        for (byte b : buffy) {
            final long v = (((long) b) & 0xFF) << shift;
            n = n + v;
            shift -= 8;
        }

        return n;
    }

    protected byte[] readData(final byte[] data) throws IOException {
        int n = 0;
        do {
            final int len = inputStream.read(data, n, data.length - n);
            if (len < 1) {
                throw new IOException("no more data");
            }
            n += len;
        }
        while (n < data.length);

        return data;
    }

    public int getNumberOfItems() {
        return (int) numberOfItems;
    }

    public boolean hasNext() {
        return currentIndex + 1 < numberOfItems;
    }

    public void close() throws IOException {
        inputStream.close();
    }

    public long currentIndex() {
        return currentIndex;
    }
}
