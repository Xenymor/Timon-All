package Puzzles.Random;

public class MiA {
    static void main() {
        int a = 7;
        for (int b = 1; b <= 9; b++) {
            int d = (a+b) % 10;
            int c = a*b % 10;
            if ((d == 1 || d == 3) && (c==2 || c==8)) {
                System.out.println(b);
            }
        }
    }
}
