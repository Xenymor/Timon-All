package Minesweeper;

public class Field {
    private boolean isMine;
    private boolean isFound = false;

    public Field(final boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(final boolean mine) {
        this.isMine = mine;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(final boolean found) {
        this.isFound = found;
    }
}
