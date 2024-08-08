package DaVinciCode.Players;

import DaVinciCode.Move;

public class User extends Player {

    public static void main(String[] args) {
        new User().run();
    }

    @Override
    protected void enemyCardRevealed(final int index, final int number) {

    }

    @Override
    protected void resetPlayer() {
        System.out.println("Debug New game");
    }

    @Override
    protected void considerEnemyDraw(final boolean isWhite, final int sortIndex) {

    }

    @Override
    protected void enemyGuessed(final int cardIndex, final int guess) {

    }

    @Override
    protected boolean wantToPass() {
        System.out.println("Debug Do you want to pass?(Y/N)");
        System.out.println("Input");
        scanner.hasNextLine();
        String input = scanner.nextLine();
        return input.equalsIgnoreCase("Y");
    }

    @Override
    protected boolean shouldDrawWhite() {
        System.out.println("Debug Do you want to draw white?(W/B)");
        System.out.println("Input");
        scanner.hasNextLine();
        String input = scanner.nextLine();
        return input.equalsIgnoreCase("W");
    }

    @Override
    protected Move chooseMove() {
        System.out.println("Debug Choose a move(0000-9999)");
        System.out.println("Input");
        scanner.hasNextLine();
        return new Move(scanner.nextLine());
    }
}
