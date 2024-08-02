package DaVinciCode.Players;

import DaVinciCode.Move;
import StandardClasses.Random;

public class RandomBot extends Player {

    public static void main(String[] args) {
        new RandomBot().run();
    }

    public Move chooseMove() {
        Move[] moves = moveGenerator.getPossibleMoves();
        //TODO
        return moves[0];
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
        //TODO return Random.chanceOf(0.5);
        return false;
    }

    @Override
    protected boolean shouldDrawWhite() {
        //TODO return Random.chanceOf(0.5) ? whiteCount > 0 : blackCount <= 0;
        return (playerInformation.myCards.size() % 2 == 0) ? whiteCount > 0 : blackCount <= 0;
    }
}
