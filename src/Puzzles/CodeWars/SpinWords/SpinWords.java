package Puzzles.CodeWars.SpinWords;


import java.util.Arrays;

public class SpinWords {

    public String spinWords(String sentence) {
        String[] words = Arrays.stream(sentence.split(" ")).map(a -> a.length() > 5 ? reverse(a) : a).toArray(String[]::new);
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(word).append(" ");
        }
        result.replace(result.length() - 1, result.length(), "");
        return result.toString();
    }

    private String reverse(String a) {
        StringBuilder stringBuilder = new StringBuilder(a);
        stringBuilder.reverse();
        return stringBuilder.toString();
    }
}


