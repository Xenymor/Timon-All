package DaVinciCode.Players.HelperClasses;

import java.util.ArrayList;
import java.util.List;

public class Card {
    public List<Integer> possibilities;
    public int number;
    public final boolean isWhite;
    public boolean isOpen = false;

    public Card(final boolean isWhite) {
        this.isWhite = isWhite;
        possibilities = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            possibilities.add(i);
        }
    }

    public Card(final boolean isWhite, final int number) {
        this.isWhite = isWhite;
        this.number = number;
        possibilities = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            possibilities.add(i);
        }
    }

    public void removePossibility(final int number) {
        possibilities.remove(Integer.valueOf(number));
    }

    @Override
    public String toString() {
        return "{number=" + number +
                ", isWhite=" + isWhite +
                ", isOpen=" + isOpen +
                "} ";
    }
}
