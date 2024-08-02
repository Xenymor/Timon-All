package DaVinciCode.Players.HelperClasses;

import DaVinciCode.Move;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    PlayerInformation playerInformation;

    public MoveGenerator(final PlayerInformation playerInformation) {
        this.playerInformation = playerInformation;
    }

    public Move[] getPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        List<Card> enemyCards = playerInformation.enemyCards;
        Card lastCard = null;
        Card nextCard = enemyCards.get(0);
        final int enemyCardsLength = enemyCards.size();
        for (int i = 0; i < enemyCardsLength; i++) {
            final Card card = nextCard;
            if (i < enemyCardsLength - 1) {
                nextCard = enemyCards.get(i + 1);
            } else {
                nextCard = null;
            }
            if (card.isOpen) {
                lastCard = card;
                continue;
            }
            List<Integer> possibilities = card.possibilities;
            if (possibilities.size() == 1) {
                moves.add(new Move(i, possibilities.get(0)));
                lastCard = card;
                continue;
            }
            int min;
            if (lastCard != null) {
                if (lastCard.isOpen) {
                    if (card.isWhite && !lastCard.isWhite) {
                        min = lastCard.number;
                    } else {
                        min = lastCard.number + 1;
                    }
                } else {
                    final List<Integer> lastPossibilities = lastCard.possibilities;
                    final Integer lastMin = lastPossibilities.get(0);
                    if (card.isWhite && !lastCard.isWhite) {
                        min = lastMin;
                    } else {
                        min = lastMin + 1;
                    }
                }
            } else {
                min = 0;
            }
            int max;
            if (nextCard != null) {
                if (nextCard.isOpen) {
                    if (!card.isWhite && nextCard.isWhite) {
                        max = nextCard.number;
                    } else {
                        max = nextCard.number - 1;
                    }
                } else {
                    final List<Integer> nextPossibilities = nextCard.possibilities;
                    final Integer nextMax = nextPossibilities.get(nextPossibilities.size() - 1);
                    if (!card.isWhite && nextCard.isWhite) {
                        max = nextMax;
                    } else {
                        max = nextMax - 1;
                    }
                }
            } else {
                max = 11;
            }
            for (int j = 0; j < possibilities.size(); j++) {
                int possibility = possibilities.get(j);
                if (possibility < min && possibility > max) {
                    possibilities.remove(j);
                    j--;
                } else {
                    moves.add(new Move(i, possibility));
                }
            }
            lastCard = card;
        }
        return moves.toArray(Move[]::new);
    }
}
