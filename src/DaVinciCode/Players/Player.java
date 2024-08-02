package DaVinciCode.Players;

import DaVinciCode.Move;
import DaVinciCode.Players.HelperClasses.MoveGenerator;
import DaVinciCode.Players.HelperClasses.PlayerInformation;

import java.util.Scanner;

public abstract class Player {
    PlayerInformation playerInformation = new PlayerInformation();
    MoveGenerator moveGenerator = new MoveGenerator(playerInformation);
    int whiteCount = 12;
    int blackCount = 12;

    void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            String[] words = input.split(" ");
            if ("Draw".equals(words[0])) {
                draw(scanner);
            } else if ("Guess".equals(words[0])) {
                //System.out.println("Debug " + playerInformation.toString());
                guess(scanner);
            } else if ("Pass?".equals(words[0])) {
                shouldPass();
            } else if ("EnemyGuess".equals(words[0])) {
                enemyGuess(words);
            } else if ("EnemyDraw".equals(words[0])) {
                enemyDraw(words);
            } else if ("Reset".equals(words[0])) {
                reset();
            } else if ("EnemyCard".equals(words[0])) {
                try {
                    enemyCard(words);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else if ("Exit".equals(words[0])) {
                exit();
                break;
            } else {
                System.out.println(input + " could not be parsed");
            }
        }
    }

    private void exit() {
        System.out.println("exit");
        System.exit(0);
    }

    private void enemyCard(final String[] parameters) {
        int code = Integer.parseInt(parameters[1]);
        int number = code % 100;
        int index = code / 100;
        playerInformation.revealCard(index, number);
        enemyCardRevealed(index, number);
        System.out.println("OK");
    }

    protected abstract void enemyCardRevealed(final int index, final int number);

    private void reset() {
        whiteCount = 12;
        blackCount = 12;
        playerInformation = new PlayerInformation();
        moveGenerator = new MoveGenerator(playerInformation);
        resetPlayer();
        System.out.println("Done");
    }

    protected abstract void resetPlayer();

    private void enemyDraw(final String[] parameters) {
        boolean isWhite = false;
        final String colorString = parameters[1];
        if (colorString.equalsIgnoreCase("White")) {
            isWhite = true;
        } else if (!colorString.equalsIgnoreCase("Black")) {
            System.out.println(colorString + " is no valid color");
        }
        if (isWhite) {
            whiteCount--;
        } else {
            blackCount--;
        }
        int sortIndex = Integer.parseInt(parameters[2]);
        playerInformation.addEnemyCard(isWhite, sortIndex);
        considerEnemyDraw(isWhite, sortIndex);
        System.out.println("OK");
    }

    protected abstract void considerEnemyDraw(final boolean isWhite, final int sortIndex);

    private void enemyGuess(final String[] parameters) {
        int guessCode = Integer.parseInt(parameters[1]);
        int cardIndex = guessCode / 100;
        int guess = guessCode % 100;
        enemyGuessed(cardIndex, guess);
        System.out.println("OK");
    }

    protected abstract void enemyGuessed(final int cardIndex, final int guess);

    protected void shouldPass() {
        if (wantToPass()) {
            System.out.println("Pass");
        } else {
            System.out.println("Continue");
        }
    }

    protected abstract boolean wantToPass();

    protected void draw(final Scanner scanner) {
        boolean drawWhite = shouldDrawWhite();
        if (drawWhite) {
            System.out.println("White");
            whiteCount--;
        } else {
            System.out.println("Black");
            blackCount--;
        }
        int number = Integer.parseInt(scanner.nextLine());
        playerInformation.addDrawnCard(drawWhite, number);
    }

    protected abstract boolean shouldDrawWhite();

    void guess(final Scanner scanner) {
        final Move move = chooseMove();
        final String moveCode = move.getCodeString();
        System.out.println(moveCode);
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("Correct")) {
            playerInformation.revealCard(move.index, move.guess);
        } else if (answer.equalsIgnoreCase("Incorrect")) {
            playerInformation.removePossibility(move.index, move.guess);
        } else {
            System.out.println(answer + " was not expected");
        }
    }

    protected abstract Move chooseMove();
}
