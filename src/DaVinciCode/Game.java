package DaVinciCode;

import StandardClasses.Random;

import java.util.*;

public class Game {
    Stack<Card> whiteCards;
    Stack<Card> blackCards;

    List<Card> player1;
    Set<Integer> player1Set;

    List<Card> player2;
    Set<Integer> player2Set;

    int openCount1 = 0;
    int openCount2 = 0;

    Card lastDrawn;

    boolean playerToMove = true;

    final int numberCount;

    public Game(final int numberCount) {
        this.numberCount = numberCount;

        whiteCards = new Stack<>();
        blackCards = new Stack<>();

        List<Card> allWhites = new ArrayList<>(numberCount);
        List<Card> allBlacks = new ArrayList<>(numberCount);
        for (int i = 0; i < numberCount; i++) {
            allWhites.add(new Card(i, true));
            allBlacks.add(new Card(i, false));
        }

        for (int i = 0; i < numberCount; i++) {
            addRandomCard(allWhites, whiteCards);
            addRandomCard(allBlacks, blackCards);
        }

        player1 = new ArrayList<>();
        player2 = new ArrayList<>();
        player1Set = new HashSet<>();
        player2Set = new HashSet<>();
    }

    private void addRandomCard(final List<Card> allCards, final Stack<Card> result) {
        final int random = Random.randomIntInRange(allCards.size());
        result.push(allCards.get(random));
        allCards.remove(random);
    }

    public Card draw(final boolean drawWhite) {
        final Card card = drawWhite ? whiteCards.pop() : blackCards.pop();

        if (playerToMove) {
            addCard(card, player1);
            player1Set.add(card.number);
        } else {
            addCard(card, player2);
            player2Set.add(card.number);
        }
        lastDrawn = card;

        return card;
    }

    private void addCard(final Card card, final List<Card> list) {
        final int number = card.number;
        final int size = list.size();

        for (int i = 0; i < size; i++) {
            if (number < list.get(i).number) {
                list.add(i, card);
                return;
            }
        }

        list.add(card);
    }

    public boolean guess(final Move move) {
        final Card card = playerToMove ? player2.get(move.index) : player1.get(move.index);
        if (card.openToOther) {
            throw new UnsupportedOperationException("Can't guess a card, which is already open");
        }
        if (card.number == move.guess) {
            card.openToOther = true;
            if (playerToMove) {
                openCount2++;
            } else {
                openCount1++;
            }
            return true;
        } else {
            lastDrawn.openToOther = true;
            return false;
        }
    }

    public void pass() {
        playerToMove = !playerToMove;
    }

    public boolean isFinished() {
        return openCount1 == player1.size() || openCount2 == player2.size();
    }

    /**
     * It has to be checked if the game is finished first. Otherwise, this method will return nonsense.
     *
     * @return Returns the winning player. True if first player wins, false if second player wins.
     */
    public boolean getWinner() {
        return openCount1 != player1.size();
    }

    final private Move[] a = new Move[0];

    public Move[] getAllMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        int enemyCardCount;
        if (playerToMove) {
            enemyCardCount = player2.size();
        } else {
            enemyCardCount = player1.size();
        }
        List<Card> enemyCards = playerToMove ? player2 : player1;

        int min = 0;
        int maxIndex;

        maxIndex = findMaxIndex(enemyCardCount, enemyCards);
        int max;

        if (maxIndex == -1) {
            max = numberCount - 1;
        } else {
            max = enemyCards.get(maxIndex).number;
        }

        for (int i = 0; i < enemyCardCount; i++) {
            final Card card = enemyCards.get(i);
            if (!card.openToOther) {
                for (int j = min; j <= max; j++) {
                    possibleMoves.add(new Move(i, j));
                }
            } else {
                min = card.number;
                if (i >= maxIndex) {
                    boolean broke = false;
                    for (int j = i + 1; j < enemyCardCount; j++) {
                        final Card currCard = enemyCards.get(j);
                        if (currCard.openToOther) {
                            max = currCard.number;
                            maxIndex = j;
                            broke = true;
                            break;
                        }
                    }
                    if (!broke) {
                        max = numberCount - 1;
                        maxIndex = enemyCardCount;
                    }
                }
            }
        }

        return possibleMoves.toArray(a);
    }

    private int findMaxIndex(final int enemyCardCount, final List<Card> enemyCards) {
        for (int i = enemyCardCount - 1; i >= 0; i--) {
            final Card card = enemyCards.get(i);
            if (card.openToOther) {
                return i;
            }
        }
        return -1;
    }
}
