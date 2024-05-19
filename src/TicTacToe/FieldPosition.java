package TicTacToe;

import Sudoku.Sudoku;

import java.util.Arrays;

public class FieldPosition {

    private int[][] field;
    int player;

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int[][] getField() {
        return this.field;
    }

    public void setField(int[][] newField) {
        this.field = newField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldPosition that = (FieldPosition) o;
        return Arrays.deepEquals(field, that.field) && player == that.player;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(field);
    }

    @Override
    protected FieldPosition clone() throws CloneNotSupportedException {
        FieldPosition result = new FieldPosition();
        result.field = Sudoku.deepClone(field);
        result.player = player;
        return result;
    }
}
