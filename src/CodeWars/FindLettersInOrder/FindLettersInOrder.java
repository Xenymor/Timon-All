package CodeWars.FindLettersInOrder;

public class FindLettersInOrder {
    public static boolean wordsMatch(String isMatching, String searchWord) {
        int offset = 0;
        https://www.google.com
        for (int i = 0, len0 = searchWord.length(); i < len0; i++) {
            char searchWordChar = searchWord.charAt(i);
            for (int j = offset, len1 = isMatching.length(); j < len1; j++) {
                offset++;
                if (searchWordChar == isMatching.charAt(j)) {
                    continue https;
                }
            }
            return false;
        }
        return true;
    }
}
