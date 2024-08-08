package DaVinciCode;

import java.io.IOException;
import java.util.Scanner;

public class ChallengeController {

    static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        Game game = new Game(12);

        Process process1 = Runtime.getRuntime().exec("java -classpath C:\\Users\\timon\\IdeaProjects\\Timon-All\\out\\production\\Timon-All;C:\\Users\\timon\\.m2\\repository\\junit\\junit\\4.13.1\\junit-4.13.1.jar;C:\\Users\\timon\\.m2\\repository\\org\\hamcrest\\hamcrest-core\\1.3\\hamcrest-core-1.3.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\jupiter\\junit-jupiter\\5.7.0\\junit-jupiter-5.7.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\jupiter\\junit-jupiter-api\\5.7.0\\junit-jupiter-api-5.7.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\apiguardian\\apiguardian-api\\1.1.0\\apiguardian-api-1.1.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\opentest4j\\opentest4j\\1.2.0\\opentest4j-1.2.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\platform\\junit-platform-commons\\1.7.0\\junit-platform-commons-1.7.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\jupiter\\junit-jupiter-params\\5.7.0\\junit-jupiter-params-5.7.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\jupiter\\junit-jupiter-engine\\5.7.0\\junit-jupiter-engine-5.7.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\platform\\junit-platform-engine\\1.7.0\\junit-platform-engine-1.7.0.jar DaVinciCode.Players.RandomBot");
        Player player1 = new Player(process1);
        Process process2 = Runtime.getRuntime().exec("java -classpath C:\\Users\\timon\\IdeaProjects\\Timon-All\\out\\production\\Timon-All;C:\\Users\\timon\\.m2\\repository\\junit\\junit\\4.13.1\\junit-4.13.1.jar;C:\\Users\\timon\\.m2\\repository\\org\\hamcrest\\hamcrest-core\\1.3\\hamcrest-core-1.3.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\jupiter\\junit-jupiter\\5.7.0\\junit-jupiter-5.7.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\jupiter\\junit-jupiter-api\\5.7.0\\junit-jupiter-api-5.7.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\apiguardian\\apiguardian-api\\1.1.0\\apiguardian-api-1.1.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\opentest4j\\opentest4j\\1.2.0\\opentest4j-1.2.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\platform\\junit-platform-commons\\1.7.0\\junit-platform-commons-1.7.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\jupiter\\junit-jupiter-params\\5.7.0\\junit-jupiter-params-5.7.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\jupiter\\junit-jupiter-engine\\5.7.0\\junit-jupiter-engine-5.7.0.jar;C:\\Users\\timon\\.m2\\repository\\org\\junit\\platform\\junit-platform-engine\\1.7.0\\junit-platform-engine-1.7.0.jar DaVinciCode.Players.RandomBot");
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