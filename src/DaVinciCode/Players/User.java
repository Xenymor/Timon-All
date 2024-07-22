package DaVinciCode.Players;

import DaVinciCode.Game;
import DaVinciCode.Move;

import java.util.List;
import java.util.Scanner;

public class User {
    Scanner scanner = new Scanner(System.in);

    public boolean drawStartCardWhite(final Game game) {
        printState();
        return getDrawWhite();
    }

    public boolean drawWhite(final Game game) {
        printState();
        return getDrawWhite();
    }

    public Move getMove(final Game game) {
        printState();
        return getMove();
    }

    public boolean shouldPass(final Game game) {
        printState();
        return getPass();
    }



    private Move getMove() {
        System.out.println("Enter your move (format:aabb, where aa is the index, bb is the guess)");
        String answer = scanner.nextLine();
        return new Move(answer);
    }

    private boolean getPass() {
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

    private boolean getDrawWhite() {
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

    private void printState() {
 /*       List<Card> myCards;
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
        System.out.println(msg);*/
    }
}
