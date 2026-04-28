package Benchmark;

public class TimingSystemOutPrintln {

    static void main() {
        final int ITERATIONS = 1_000_000;
        long sum = 0;
        final long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            System.nanoTime();
        }
        final long endTime = System.nanoTime();
        System.out.println("Time taken: " + (endTime - startTime) + " ns Avg:" + (endTime - startTime) / (float)ITERATIONS + "ns" + " -> " + sum);
    }

}
