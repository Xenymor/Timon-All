package MapTest;

import java.util.concurrent.TimeUnit;

public class Main {
    private static int i;

    public static void main(String[] args) {
        long start, end;
        final int LIMIT = 1000000;

        start = System.nanoTime();
        for (int i = 7; i < LIMIT; i += 2) {
            boolean isPrime = true;
            if (((i % 3) == 0)
                    || ((i % 5) == 0)
                    || ((i % 7) == 0)
                    || ((i % 11) == 0)
                    || ((i % 13) == 0)
                    || ((i % 17) == 0)
                    || ((i % 19) == 0)
                    || ((i % 23) == 0)
                    || ((i % 29) == 0)
                    || ((i % 31) == 0)
                    || ((i % 37) == 0)
                    || ((i % 41) == 0)
                    || ((i % 43) == 0)
                    || ((i % 47) == 0)
                    || ((i % 53) == 0)
                    || ((i % 59) == 0)
                    || ((i % 61) == 0)
                    || ((i % 67) == 0)
                    || ((i % 71) == 0)
                    || ((i % 73) == 0)
                    || ((i % 79) == 0)
                    || ((i % 83) == 0)
                    || ((i % 89) == 0)
                    || ((i % 97) == 0)) {
                continue;
            }
            for (int j = 101, len = (int) Math.sqrt(i); j <= len; j += 2) {
                if ((i % j) == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime && i < 100) {
                System.out.println("Is prime " + i);
            }
        }
        end = System.nanoTime();

        System.out.println(TimeUnit.NANOSECONDS.toMillis((end - start)));
    }
}
