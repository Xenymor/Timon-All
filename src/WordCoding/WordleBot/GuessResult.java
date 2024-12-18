package WordCoding.WordleBot;

public class GuessResult {
    Result[] results;
    boolean isCorrect;
    String guess;

    public GuessResult(String guess) {
        results = new Result[5];
        isCorrect = true;
        this.guess = guess;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < results.length; i++) {
            switch (results[i]) {
                case CORRECT -> result.append(guess.charAt(i)).append(" ");
                case WRONG_PLACE -> result.append("[").append(guess.charAt(i)).append("] ");
                case WRONG -> result.append("_ ");
                default -> System.out.println("Unknown type: " + results[i]);
            }
        }
        return result.toString();
    }
}
