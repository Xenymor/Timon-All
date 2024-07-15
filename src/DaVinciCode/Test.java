package DaVinciCode;

import java.util.List;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Game game = new Game(12);

        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                boolean drawWhite = getDrawWhite(scanner);
                Card drawn = game.draw(drawWhite);
                System.out.println(drawn.toString());
            }
            game.pass();
        }

        while (!game.isFinished()) {
            boolean pass = false;
            while (!pass) {
                printState(game);
                boolean drawWhite = getDrawWhite(scanner);
                game.draw(drawWhite);
                printState(game);
                final Move move = getMove(scanner);
                boolean successful = game.guess(move);
                printSuccess(successful);
                if (successful) {
                    if (getPass(scanner)) {
                        pass = true;
                    }
                } else {
                    pass = true;
                }
            }
            game.pass();
        }
        System.out.println(game.getWinner());
    }

    private static Move getMove(final Scanner scanner) {
        System.out.println("Enter your move (format:aabb, where aa is the index, bb is the guess)");
        String answer = scanner.nextLine();
        return new Move(answer);
    }

    private static boolean getPass(final Scanner scanner) {
        System.out.println("Do you want to pass or continue? (p/c)");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("p")) {
            return true;
        } else if (answer.equalsIgnoreCase("c")) {
            return false;
        } else {
            throw new IllegalArgumentException("Input couldn't be read");
        }
    }

    private static void printSuccess(final boolean successful) {
        if (successful) {
            System.out.println("Your guess was right.");
        } else {
            System.out.println("Your guess was false.");
        }
    }

    private static boolean getDrawWhite(final Scanner scanner) {
        System.out.println("Do you want to draw a white or black Card? (w/b)");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("w")) {
            return true;
        } else if (answer.equalsIgnoreCase("b")) {
            return false;
        } else {
            throw new IllegalArgumentException("Input couldn't be read");
        }
    }

    private static void printState(final Game game) {
        List<Card> myCards;
        List<Card> enemyCards;
        if (game.playerToMove) {
            myCards = game.player1;
            enemyCards = game.player2;
        } else {
            myCards = game.player2;
            enemyCards = game.player1;
        }

        StringBuilder msg = new StringBuilder();
        msg.append("Your cards: ");
        for (Card myCard : myCards) {
            msg.append(myCard.toString());
        }
        msg.append("\nEnemy cards: ");
        for (final Card card : enemyCards) {
            if (card.openToOther) {
                msg.append(card);
            } else {
                msg.append("{??").append(",").append(card.isWhite ? "White" : "Black").append("}, ");
            }
        }
        System.out.println(msg);
    }
}