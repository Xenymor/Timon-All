package DaVinciCode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class ChallengeController {

    static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        Game game = new Game(12);

        if (args.length != 2) {
            throw new IllegalArgumentException("Wrong number of parameters " + Arrays.toString(args));
        }
        Process process1 = Runtime.getRuntime().exec(args[0]);
        Player player1 = new Player(process1);
        Process process2 = Runtime.getRuntime().exec(args[1]);
        Player player2 = new Player(process2);

        prepareGame(game, player1, player2);

        while (!game.isFinished()) {
            Player current = getCurrentPlayer(game, player1, player2);
            Player opponent = getCurrentPlayer(game, player2, player1);

            if (game.canDraw()) {
                drawCard(game, current, opponent);
            } else {
                game.couldDraw = false;
            }
            boolean correct = guess(game, current, opponent);
            if (correct) {
                if (current.shouldPass()) {
                    game.pass();
                }
            } else {
                game.pass();
            }
        }

        System.out.println(game.getWinner());
    }

    private static boolean guess(final Game game, final Player current, final Player opponent) {
        final Move guess = current.guess();
        opponent.enemyGuess(guess);
        boolean correct = game.guess(guess);
        current.guessResult(correct);
        if (!correct && game.couldDraw) {
            opponent.revealCard(game.lastDrawn);
        }
        return correct;
    }

    private static Player getCurrentPlayer(final Game game, final Player player1, final Player player2) {
        return game.playerToMove ? player1 : player2;
    }

    private static void prepareGame(final Game game, final Player player1, final Player player2) {
        for (int i = 0; i < 4; i++) {
            drawCard(game, player1, player2);
            game.pass();

            drawCard(game, player2, player1);
            game.pass();
        }
    }

    private static void drawCard(final Game game, final Player currentPlayer, final Player opponent) {
        Card drawn = game.draw(currentPlayer.shouldDrawWhite());
        currentPlayer.drawn(drawn);
        opponent.enemyDrawn(drawn);
    }
}