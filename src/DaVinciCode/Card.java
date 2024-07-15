package DaVinciCode;

public class Card {

    public final int number;
    public boolean openToOther;
    public final boolean isWhite;

    public Card(final int number, final boolean isWhite) {
        this.number = number;
        openToOther = false;
        this.isWhite = isWhite;
    }

    @Override
    public String toString() {
        return "{" + number + ", " +
                (isWhite ? "White" : "Black") + ", " +
                openToOther + "}";
    }
}
