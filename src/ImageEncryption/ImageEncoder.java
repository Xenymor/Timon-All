package ImageEncryption;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ImageEncoder {

    public static final String ORIG_IMG_PATH = "src/ImageEncryption/saveImg.jpg";
    public static final String ENCR_IMG_PATH = "src/ImageEncryption/encrImg.png";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Do you want to encode(e) or decode(d)");
            String msg = scanner.nextLine();
            if (msg.equalsIgnoreCase("e")) {
                System.out.println("Give me the path to your original image! (Should start with C:/)");
                String origPath = scanner.nextLine();
                System.out.println("Write your message!");
                msg = scanner.nextLine();
                int key = encode(msg, origPath, ENCR_IMG_PATH);
                String wholePath = new File(ENCR_IMG_PATH).getAbsolutePath();
                System.out.println("Your message was encoded to " + wholePath + " and your key is " + key);
            } else if (msg.equalsIgnoreCase("d")) {
                System.out.println("Give me the path to your image, which should be decoded!");
                String encrPath = scanner.nextLine();
                System.out.println("Give me the key!");
                int key = scanner.nextInt();
                System.out.println("Your message is:\n" + decode(encrPath, key));
            } else {
                System.out.println("I didn't understand you. Please try again.");
            }
        }
    }

    private static void test() throws IOException {
        final String msg = "Hello World!";
        int key = ImageEncoder.encode(msg, ORIG_IMG_PATH, ENCR_IMG_PATH);
        System.out.println(key);
        System.out.println(ImageEncoder.decode(ENCR_IMG_PATH, key));
    }

    public static String decode(final String encrImgPath, final int key) throws IOException {
        File encrFile = new File(encrImgPath);
        if (!encrFile.exists()) {
            throw new FileNotFoundException(encrImgPath);
        }
        BufferedImage img = ImageIO.read(encrFile);
        byte[] bytes = new byte[key];
        int i = 0, j = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            outer:
            for (int y = 0; y < img.getHeight(); y++) {
                int RGB = img.getRGB(x, y);
                for (int k = 0; i < bytes.length && k < 3; j += 2, k++) {
                    int curr = (RGB >> (k * 8)) & (255);
                    if (j == 8) {
                        j = 0;
                        i++;
                        if (i >= bytes.length) {
                            continue outer;
                        }
                    }
                    curr = curr & 3;
                    bytes[i] += (byte) (curr << j);
                }
            }
        }
        return new String(bytes);
    }

    public static int encode(final String msg, final String origImgPath, final String encrImgPath) throws IOException {
        File origFile = new File(origImgPath);
        if (!origFile.exists()) {
            throw new FileNotFoundException(origImgPath);
        }
        File encrFile = new File(encrImgPath);
        if (!encrFile.exists()) {
            if (!encrFile.createNewFile()) {
                System.out.println("HUH???");
            }
        }
        BufferedImage img = ImageIO.read(origFile);
        ImageIO.write(img, "jpg", encrFile);

        byte[] bytes = msg.getBytes();

        int i = 0, j = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            outer:
            for (int y = 0; y < img.getHeight(); y++) {
                int RGB = img.getRGB(x, y);
                int resRGB = 0;
                int curr;
                for (int k = 0; i < bytes.length && k < 3; j += 2, k++) {
                    curr = (RGB >> (k * 8)) & (255);
                    if (j == 8) {
                        j = 0;
                        i++;
                        if (i >= bytes.length) {
                            continue outer;
                        }
                    }
                    curr = (curr & 2147483644) + ((bytes[i] >> j) & 3);
                    resRGB += curr << (k * 8);
                }
                if (resRGB != 0) {
                    img.setRGB(x, y, resRGB);
                }
            }
        }
        ImageIO.write(img, "png", encrFile);
        return bytes.length;
    }
}
