package DaVinciCode.Players;

import DaVinciCode.Move;

import java.util.Random;

public class RandomBot extends Player {
    Random random = new Random();

    public static void main(String[] args) {
        new RandomBot().run();
    }

    public Move chooseMove() {
        Move[] moves = moveGenerator.getPossibleMoves();
        return moves[random.nextInt(moves.length)];
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
        return chanceOf(0.5, random);
    }

    @Override
    protected boolean shouldDrawWhite() {
        return chanceOf(0.5, random) ? whiteCount > 0 : blackCount <= 0;
    }

    boolean chanceOf(final double chance, final Random random) {
        return random.nextDouble() < chance;
    }
}
