package DaVinciCode;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Player {
    final Process process;
    final Scanner processOutput;
    final BufferedReader errorOutput;
    final BufferedWriter processInput;
    boolean showOutputs;

    public Player(final Process process, boolean hideOutputs) {
        this.process = process;
        this.showOutputs = !hideOutputs;
        processOutput = new Scanner(process.getInputStream());
        errorOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }

    public boolean shouldPass() {
        writeMessage("Pass?");
        String answer = getAnswer();
        if (answer.equalsIgnoreCase("Continue")) {
            return false;
        } else if (answer.equalsIgnoreCase("Pass")) {
            return true;
        } else {
            throw new IllegalArgumentException(answer + " could not be parsed");
        }
    }

    private String getAnswer() {
        do {
            if (processOutput.hasNextLine()) {
                String answer = processOutput.nextLine();
                final String[] words = answer.split(" ");
                final String command = words[0];
                if (command.equalsIgnoreCase("Debug")) {
                    if (words.length > 1) {
                        for (int i = 1; i < words.length; i++) {
                            System.out.print(words[i]);
                        }
                    }
                    System.out.println();
                } else if (command.equalsIgnoreCase("Input")) {
                    writeMessage(ChallengeController.userInput.nextLine());
                } else {
                    if (showOutputs) {
                        System.out.println(answer);
                    }
                    return answer;
                }
            } else {
                errorOutput.lines().forEach(System.out::println);
                throw new NoSuchElementException("No line found in process output");
            }
        } while (true);
    }

    public Move guess() {
        writeMessage("Guess");
        processOutput.hasNextLine();
        int answer = Integer.parseInt(getAnswer());
        int guess = answer % 100;
        int index = answer / 100;
        return new Move(index, guess);
    }

    private void writeMessage(final String s) {
        try {
            final String str = s + "\n";
            if (showOutputs) {
                System.out.print(str);
            }
            processInput.write(str);
            processInput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enemyGuess(final Move guess) {
        sendCode(guess.index, guess.guess, "EnemyGuess ");
    }

    public void guessResult(final boolean correct) {
        if (correct) {
            writeMessage("Correct");
        } else {
            writeMessage("Incorrect");
        }
    }

    public void revealCard(final Card card) {
        sendCode(card.startSortIndex, card.number, "EnemyCard ");
    }

    public void revealCard(final Game game, final int index) {
        final Card card = (game.playerToMove ? game.player2 : game.player1).get(index);
        sendCode(index, card.number, "EnemyCard ");
    }

    private void sendCode(final int higherDigits, final int lowerDigits, final String message) {
        final int code = higherDigits * 100 + lowerDigits;
        writeMessage(message + codeToString(code));
        checkForOK();
    }

    String zeros = "0000";

    private String codeToString(final int code) {
        StringBuilder output = new StringBuilder(Integer.toString(code));
        output.insert(0, zeros.substring(output.length()));
        return output.toString();
    }

    public boolean shouldDrawWhite() {
        writeMessage("Draw");
        String answer = getAnswer();
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
        writeMessage("EnemyDraw " + (card.isWhite ? "White " : "Black ") + card.startSortIndex);
        checkForOK();
    }

    private void checkForOK() {
        String answer = getAnswer();
        if (!answer.equalsIgnoreCase("OK")) {
            throw new IllegalArgumentException(answer + " was not expected");
        }
    }
}
