package DaVinciCode.Players;

import DaVinciCode.Move;
import DaVinciCode.Players.HelperClasses.Card;
import StandardClasses.Random;

import java.util.List;

public class GreedyBot extends Player {
    public static void main(String[] args) {
        new GreedyBot().run();
    }

    public GreedyBot() {
        moveGenerator.includeOwnCardsInfo = true;
    }

    @Override
    protected void enemyCardRevealed(final int index, final int number) {

    }

    @Override
    protected void resetPlayer() {

    }

    @Override
    protected void considerEnemyDraw(final boolean isWhite, final int sortIndex) {

    }

    @Override
    protected void enemyGuessed(final int cardIndex, final int guess) {

    }

    @Override
    protected boolean wantToPass() {
        return Random.chanceOf(0.5);
    }

    @Override
    protected boolean shouldDrawWhite() {
        return whiteCount > blackCount;
    }

    @Override
    protected Move chooseMove() {
        moveGenerator.getPossibleMoves();
        List<Card> enemyCards = playerInformation.enemyCards;
        int bestIndex = -1;
        int possibilityCount = Integer.MAX_VALUE;
        List<Integer> bestPossibilities = null;
        for (int i = 0; i < enemyCards.size(); i++) {
            final Card card = enemyCards.get(i);
            if (card.isOpen) {
                continue;
            }
            final List<Integer> possibilities = card.possibilities;
            final int currCount = possibilities.size();
            if (currCount < possibilityCount) {
                bestIndex = i;
                possibilityCount = currCount;
                bestPossibilities = possibilities;
            }
        }
        if (bestIndex == -1) {
            System.out.println("Debug Alaaaaaaaarm");
        }
        return new Move(bestIndex, bestPossibilities.get(Random.randomIntInRange(possibilityCount)));
    }
}
