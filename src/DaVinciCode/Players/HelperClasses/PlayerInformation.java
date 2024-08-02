package DaVinciCode.Players.HelperClasses;

import java.util.ArrayList;
import java.util.List;

public class PlayerInformation {
    public List<Card> myCards = new ArrayList<>();
    public List<Card> enemyCards = new ArrayList<>();

    public void revealCard(final int index, final int number) {
        final Card card = enemyCards.get(index);
        card.number = number;
        card.isOpen = true;
    }

    public void addEnemyCard(final boolean isWhite, final int sortIndex) {
        final Card card = new Card(isWhite);
        enemyCards.add(sortIndex, card);
        updatePossibilities(sortIndex, card);
    }

    private void updatePossibilities(final int sortIndex, final Card card) {
        int min;
        final Card lastCard = sortIndex > 0 ? enemyCards.get(sortIndex - 1) : null;
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
        final Card nextCard = sortIndex < enemyCards.size() - 1 ? enemyCards.get(sortIndex + 1) : null;
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
        card.possibilities.clear();
        for (int i = min; i < max + 1; i++) {
            card.possibilities.add(i);
        }
    }

    public void addDrawnCard(final boolean isWhite, final int number) {
        Card toAdd = new Card(isWhite, number);
        for (int i = 0; i < myCards.size(); i++) {
            final int otherNumber = myCards.get(i).number;
            if ((number < otherNumber)) {
                myCards.add(i, toAdd);
                return;
            } else if (number == otherNumber) {
                if (toAdd.isWhite) {
                    myCards.add(i + 1, toAdd);
                } else {
                    myCards.add(i, toAdd);
                }
                return;
            }
        }
        myCards.add(toAdd);
    }

    public void removePossibility(final int index, final int number) {
        enemyCards.get(index).removePossibility(number);
    }

    @Override
    public String toString() {
        return "PlayerInformation{" +
                "myCards=" + myCards +
                ", enemyCards=" + enemyCards +
                '}';
    }
}
