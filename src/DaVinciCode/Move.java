package DaVinciCode;

public class Move {
    public final int index;
    public final int guess;

    public Move(final String moveString) {
        index = Integer.parseInt(moveString.substring(0, 2));
        guess = Integer.parseInt(moveString.substring(2, 4));
    }

    public Move(final int index, final int guess) {
        this.index = index;
        this.guess = guess;
    }

    @Override
    public String toString() {
        return "Move{" +
                "index=" + index +
                ", guess=" + guess +
                '}';
    }

    public int getCode() {
        return index * 100 + guess;
    }

    public String getCodeString() {
        return codeToString(getCode(), 4);
    }

    private String codeToString(final int code, final int targetLength) {
        StringBuilder output = new StringBuilder(Integer.toString(code));
        while (output.length() < targetLength) output.insert(0, "0");
        return output.toString();
    }
}
