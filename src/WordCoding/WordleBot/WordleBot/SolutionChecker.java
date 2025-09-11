package WordCoding.WordleBot.WordleBot;

import java.nio.file.*;
import java.util.*;
import java.io.*;

public class SolutionChecker {
	public static void main(String[] args) throws IOException {
		Set<String> solutions = new HashSet<>(Files.readAllLines(Paths.get("src/WordCoding/WordleBot/Wordle/solutions.txt")));
		Set<String> found = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader("src/WordCoding/WordleBot/WordleBot/guesses.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length > 0) {
					found.add(parts[parts.length - 1].trim());
				}
			}
		}

		solutions.removeAll(found);
		if (solutions.isEmpty()) {
			System.out.println("All solution words appear as the last word in guesses.txt.");
		} else {
			System.out.println("Missing words:");
			solutions.forEach(System.out::println);
		}
	}
}