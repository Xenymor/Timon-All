package DaVinciCode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class Player {
    final Process process;
    final Scanner processOutput;
    final Scanner errorOutput;
    final BufferedWriter processInput;

    public Player(final Process process) {
        this.process = process;
        processOutput = new Scanner(process.getInputStream());
        errorOutput = new Scanner(process.getErrorStream());
        processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }

    public boolean shouldPass() {
        writeMessage("Pass?");
        String answer = processOutput.nextLine();
        if (answer.equalsIgnoreCase("Continue")) {
            return false;
        } else if (answer.equalsIgnoreCase("Pass")) {
            return true;
        } else {
            throw new IllegalArgumentException(answer + " could not be parsed");
        }
    }

    public Move guess() {
        writeMessage("Guess");
        int answer = Integer.parseInt(processOutput.nextLine());
        int guess = answer % 100;
        int index = answer / 100;
        return new Move(index, guess);
    }

    private void writeMessage(final String s) {
        try {
            processInput.write(s + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enemyGuess(final Move guess) {
        int guessCode = guess.index * 100 + guess.guess;
        writeMessage("EnemyGuess " + guessCode);
        checkForOK();
    }

    public void guessResult(final boolean correct) {
        if (correct) {
            writeMessage("Correct");
        } else {
            writeMessage("Incorrect");
        }
    }

    public void revealCard(final Card card) {
        int cardCode = card.startSortIndex * 100 + card.number;
        writeMessage("EnemyCard " + (cardCode));
        checkForOK();
    }

    public boolean shouldDrawWhite() {
        writeMessage("Draw");
        String answer = processOutput.nextLine();
        if (answer.equalsIgnoreCase("White")) {
            return true;
        } else if (answer.equalsIgnoreCase("Black")) {
            return false;
        } else {
            throw new IllegalArgumentException(answer + " could not be parsed");
        }
    }

    public void drawn(final Card drawn) {
        writeMessage(Integer.toString(drawn.number));
    }

    public void enemyDrawn(final Card card) {
        writeMessage("EnemyDraw " + (card.isWhite ? "White" : "Black") + card.startSortIndex);
        checkForOK();
    }

    private void checkForOK() {
        String answer = processOutput.nextLine();
        if (!answer.equalsIgnoreCase("OK")) {
            throw new IllegalArgumentException(answer + " was not expected");
        }
    }
}
