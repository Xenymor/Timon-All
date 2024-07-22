package DaVinciCode;

public class Card {

    public final int number;
    public boolean openToOther;
    public final boolean isWhite;
    public int startSortIndex;

    public Card(final int number, final boolean isWhite) {
        this.number = number;
        this.isWhite = isWhite;
    }

    public Card(final int number, final boolean isWhite, final int startSortIndex) {
        this.number = number;
        openToOther = false;
        this.isWhite = isWhite;
        this.startSortIndex = startSortIndex;
    }

    @Override
    public String toString() {
        return "{" + number + ", " +
                (isWhite ? "White" : "Black") + ", " +
                openToOther + "}";
    }
}
