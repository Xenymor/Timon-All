package DaVinciCode.Players.HelperClasses;

import DaVinciCode.Move;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    public boolean includeOwnCardsInfo = false;
    final PlayerInformation playerInformation;

    public MoveGenerator(final PlayerInformation playerInformation) {
        this.playerInformation = playerInformation;
    }

    public Move[] getPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        List<Card> enemyCards = playerInformation.enemyCards;
        final int enemyCardCount = enemyCards.size();

        List<Card> myCards = playerInformation.myCards;

        int min = 0;
        int maxIndex;

        maxIndex = findMaxIndex(1, enemyCards);
        int max;

        if (maxIndex == -1) {
            max = 11;
        } else {
            max = enemyCards.get(maxIndex).number;
        }

        for (int i = 0; i < enemyCardCount; i++) {
            final Card card = enemyCards.get(i);
            if (!card.isOpen) {
                if (includeOwnCardsInfo) {
                    playerInformation.removeAllPossibilities(i, myCards);
                }
                for (int j = 0; j <= 11; j++) {
                    if (j >= min && j <= max) {
                        if (!(includeOwnCardsInfo && contains(myCards, j))) {
                            possibleMoves.add(new Move(i, j));
                        }
                    } else {
                        playerInformation.removePossibility(i, j);
                    }
                }
            } else {
                min = card.number;
                if (i >= maxIndex) {
                    boolean broke = false;
                    for (int j = i + 1; j < enemyCardCount; j++) {
                        final Card currCard = enemyCards.get(j);
                        if (currCard.isOpen) {
                            max = currCard.number;
                            maxIndex = j;
                            broke = true;
                            break;
                        }
                    }
                    if (!broke) {
                        max = 11;
                        maxIndex = enemyCardCount;
                    }
                }
            }
        }

        return possibleMoves.toArray(Move[]::new);
    }

    private boolean contains(final List<Card> myCards, final int j) {
        for (Card card : myCards) {
            if (card.number == j) {
                return true;
            }
        }
        return false;
    }

    private int findMaxIndex(final int startIndex, final List<Card> enemyCards) {
        for (int i = startIndex; i < enemyCards.size(); i++) {
            final Card card = enemyCards.get(i);
            if (card.isOpen) {
                return i;
            }
        }
        return -1;
    }
}
