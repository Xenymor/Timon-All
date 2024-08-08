package DaVinciCode.Players;

import DaVinciCode.Move;
import StandardClasses.Random;

public class RandomBot extends Player {

    public static void main(String[] args) {
        new RandomBot().run();
    }

    public Move chooseMove() {
        Move[] moves = moveGenerator.getPossibleMoves();
        return moves[Random.randomIntInRange(moves.length)];
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
        return Random.chanceOf(0.5) ? whiteCount > 0 : blackCount <= 0;
    }
}
