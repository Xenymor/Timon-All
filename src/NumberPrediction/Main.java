package NumberPrediction;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        Predictor predictor = new Predictor();
        int guessCount = 0;
        int correctCount = 0;
        while (true) {
            if (userInput.hasNextLine()) {
                String input = userInput.nextLine();
                if (input.equalsIgnoreCase("q")) {
                    System.out.println("Exiting...");
                    //Save state
                    predictor.printChoices();
                    break;
                }
                try {
                    boolean number = Integer.parseInt(input) == 1;
                    boolean prediction = predictor.predict();
                    guessCount++;
                    if (prediction == number) {
                        correctCount++;
                        System.out.println("Correct prediction!");
                    } else {
                        System.out.println("Incorrect prediction.");
                    }
                    System.out.println("Predicted number: " + (prediction ? 1 : 0) + " | " + ((float) correctCount / guessCount) * 100 + "% correct (" + correctCount + "/" + guessCount + ")");
                    predictor.update(number);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer or type 'q' to quit.");
                }
            } else {
                System.out.println("No input received. Please enter a number.");
            }
        }
    }
}
