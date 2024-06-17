package Minesweeper;

public class Field {
    private boolean isMine;
    private boolean isExplored = false;
    private boolean marked = false;

    public Field(final boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(final boolean mine) {
        this.isMine = mine;
    }

    public boolean isExplored() {
        return isExplored;
    }

    public void setExplored(final boolean explored) {
        this.isExplored = explored;
    }

    public void setMarked(final boolean marked) {
        this.marked = marked;
    }

    public boolean isMarked() {
        return marked;
    }
}
