package TextAdventure;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class TextAdventure {
    public static final String STORY_PATH = "src/TextAdventure/";
    public static final String START_KEY = "Start";
    static HashMap<String, StoryPoint> story = new HashMap<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        boolean tellStoryAgain = false;
        String msg = "";
        String storyPath = "";
        while (true) {
            if (!tellStoryAgain) {
                System.out.println("Do you want to create a story(c), play one(p) or quit(q)?");
                msg = scanner.nextLine();
            }
            boolean justOutOfStory = false;
            try {
                if (msg.equalsIgnoreCase("c")) {
                    createStory();
                } else if (msg.equalsIgnoreCase("p") || tellStoryAgain) {
                    if (tellStoryAgain) {
                        tellStoryAgain = false;
                    } else {
                        System.out.println("Enter the story name!");
                        storyPath = STORY_PATH + scanner.nextLine();
                    }
                    tellStory(storyPath);
                    justOutOfStory = true;
                } else if (msg.equalsIgnoreCase("q")) {
                    System.out.println("Quitting");
                    System.exit(0);
                } else {
                    System.out.println("Answer not recognised. Try again!");
                }
                if (justOutOfStory) {
                    System.out.println("Do you want to play the same story again? (p)");
                    msg = scanner.nextLine();
                    if (msg.equalsIgnoreCase("p")) {
                        tellStoryAgain = true;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error. Please try again!");
            }
        }
    }

    public static void createStory() throws IOException {
        final HashSet<String> existingKeys = new HashSet<>();
        existingKeys.add(START_KEY);
        createStoryRecursively(START_KEY, new Scanner(System.in), existingKeys);
        final Path path = Path.of(STORY_PATH);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(STORY_PATH));
        objectOutputStream.writeObject(story);
        objectOutputStream.close();
    }

    private static void createStoryRecursively(final String key, final Scanner scanner, final Set<String> existingKeys) {
        System.out.println("We are at key: \u001B[31m" + key + "\u001B[0m");
        System.out.println("Enter story");
        String msg = getString(scanner);
        System.out.println("Enter number of options");
        int optionCount = getInt(scanner);
        String[] options = new String[optionCount];
        String[] keys = new String[optionCount];
        for (int i = 0; i < optionCount; i++) {
            System.out.println("We are at key: \u001B[31m" + key + " Option" + (i + 1) + "\u001B[0m");
            System.out.println("Enter option");
            options[i] = getString(scanner);
            System.out.println("Enter key");
            keys[i] = getString(scanner);
            if (existingKeys.contains(keys[i])) {
                System.out.println("Loop created");
            } else {
                existingKeys.add(keys[i]);
                createStoryRecursively(keys[i], scanner, existingKeys);
            }
        }
        story.put(key, new StoryPoint(msg, optionCount == 0, options, keys));
    }

    private static String getString(final Scanner scanner) {
        return scanner.nextLine();
    }

    private static int getInt(final Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(getString(scanner));
            } catch (Exception e) {
                System.out.println("Error please try again.");
            }
        }
    }

    public static void tellStory(String storyPath) throws IOException, ClassNotFoundException {
        final ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(storyPath));
        story = (HashMap<String, StoryPoint>) inputStream.readObject();
        inputStream.close();
        StoryPoint current = story.get("Start");
        Scanner scanner = new Scanner(System.in);
        do {
            current.printMsg();
            current = story.get(current.getKey(scanner.nextInt() - 1));
        } while (!current.isEnd());
        current.printMsg();
    }
}