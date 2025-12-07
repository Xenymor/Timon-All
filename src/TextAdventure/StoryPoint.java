package TextAdventure;

import java.io.Serial;
import java.io.Serializable;

public record StoryPoint(String message, boolean isEnd, String[] options, String[] keys) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    public void printMsg() {
        System.out.println(message);
        for (int i = 0; i < options.length; i++) {
            System.out.println(" \t" + (i + 1) + ": " + options[i]);
        }
    }

    public String getKey(final int choice) {
        return keys[choice];
    }
}
