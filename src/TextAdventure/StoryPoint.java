package TextAdventure;

import java.io.Serializable;

public class StoryPoint implements Serializable {
    private final boolean isEnd;
    final String message;
    final String[] options;
    final String[] keys;
    static final long serialVersionUID = 1;

    public StoryPoint(final String message, final boolean isEnd, final String[] options, final String[] keys) {
        this.message = message;
        this.options = options;
        this.keys = keys;
        this.isEnd = isEnd;
    }

    public void printMsg() {
        System.out.println(message);
        for (int i = 0; i < options.length; i++) {
            System.out.println(" \t" + (i+1) + ": " + options[i]);
        }
    }

    public String getKey(final int choice) {
        return keys[choice];
    }

    public boolean isEnd() {
        return isEnd;
    }
}
