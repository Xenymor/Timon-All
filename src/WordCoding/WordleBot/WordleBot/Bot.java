package WordCoding.WordleBot.WordleBot;

import WordCoding.WordleBot.Wordle.Result;

import java.util.*;

//Average guesses: 5.735223
public class Bot {
    final List<String> originalWords;
    final List<String> originalSolutions;
    final List<String> possibleWords;
    final List<String> possibleSolutions;

    final Set<Character>[] possibilities;
    //TODO double letters
    final List<Character> mustHave;
    final List<Character> charList = new ArrayList<>();

    public Bot(final List<String> possibleWords, final List<String> possibleSolutions) {
        this.possibleWords = possibleWords;
        this.possibleSolutions = possibleSolutions;
        originalWords = new ArrayList<>(possibleWords);
        originalSolutions = new ArrayList<>(possibleSolutions);

        mustHave = new ArrayList<>();

        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char c : chars) {
            charList.add(c);
        }

        possibilities = new HashSet[5];
        for (int i = 0; i < possibilities.length; i++) {
            possibilities[i] = new HashSet<>(charList);
        }
    }

    public String guess() {
        return possibleSolutions.get(0);
    }

    public void reset() {
        possibleWords.clear();
        possibleWords.addAll(originalWords);
        possibleSolutions.clear();
        possibleSolutions.addAll(originalSolutions);

        for (final Set<Character> possibility : possibilities) {
            possibility.clear();
            possibility.addAll(charList);
        }

        mustHave.clear();
    }

    final Map<Character, Integer> counts = new HashMap<>();

    public void update(final String guess, final Result[] results) {
        counts.clear();
        char[] charArray = guess.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            final char c = charArray[i];
            counts.put(c, counts.containsKey(c) ? counts.get(c) + 1 : 0);
            switch (results[i]) {
                case CORRECT -> {
                    possibilities[i].clear();
                    possibilities[i].add(c);

                    mustHave.add(c);
                }
                case WRONG -> {
                    if (counts.get(c) == 0) {
                        for (final Set<Character> possibility : possibilities) {
                            possibility.remove(c);
                        }
                    } else {
                        //TODO make better
                        for (final Set<Character> possibility : possibilities) {
                            possibility.add(c);
                        }
                        possibilities[i].remove(c);
                    }
                }
                case WRONG_PLACE -> {
                    //TODO make better
                    possibilities[i].remove(c);

                    mustHave.add(c);
                }
            }
        }

        outer: for (int i = possibleSolutions.size() - 1; i >= 0; i--) {
            final String word = possibleSolutions.get(i);
            final char[] chars = word.toCharArray();

            for (char c : mustHave) {
                if (!word.contains(Character.toString(c))) {
                    possibleSolutions.remove(i);
                    continue outer;
                }
            }

            for (int j = 0; j < chars.length; j++) {
                char c = chars[j];
                if (!possibilities[j].contains(c)) {
                    possibleSolutions.remove(i);
                    continue outer;
                }
            }
        }
    }
}
