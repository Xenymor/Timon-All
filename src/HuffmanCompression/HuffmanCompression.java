package HuffmanCompression;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HuffmanCompression {

    public static void main(String[] args) throws IOException {
        byte[] inputByteArray = Files.readAllBytes(Paths.get("src/HuffmanCompression/TrainingDataEnglish.txt"));
        String input = new String(inputByteArray, StandardCharsets.UTF_8);
        CompressionResult compressed = compress(input);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("src/HuffmanCompression/TrainingDataEnglishComrpessed.hfmcpr"));
        objectOutputStream.writeObject(compressed);
        objectOutputStream.close();
        System.out.println(compressed.result.length + "/" + input.length());
    }

    private static String decompress(CompressionResult compressed) {
        BitSet bits = BitSet.valueOf(compressed.result);
        StringBuilder stringBuilder = new StringBuilder();
        TreeNode current = compressed.codeTree;
        for (int i = 0; i < compressed.bitCount; i++) {
            if (bits.get(i)) {
                current = current.getRight();
            } else {
                current = current.getLeft();
            }
            if (current.isLast()) {
                stringBuilder.append(current.getString());
                current = compressed.codeTree;
            }
        }
        return stringBuilder.toString();
    }

    public static CompressionResult compress(String input) {
        char[] chars = input.toCharArray();
        HashMap<Character, Integer> frequency = createFrequencyHashMap(chars);
        frequency = sortByValue(frequency);
        Character[] sortedChars = frequency.keySet().toArray(new Character[0]);
        Integer[] sortedFrequency = frequency.values().toArray(new Integer[0]);
        reverse(sortedChars);
        reverse(sortedFrequency);
        TreeNode[] data = getTreeNodes(sortedChars, sortedFrequency);
        TreeNode codeTree = getCodes(data);
        BitSet bits = new BitSet();
        int count = 0;
        for (char aChar : chars) {
            TreeNode currentTreeNode = codeTree;
            while (true) {
                if (currentTreeNode.left == null) {
                    break;
                } else {
                    if (currentTreeNode.left.getString().contains(Character.toString(aChar))) {
                        bits.set(count, false);
                        currentTreeNode = currentTreeNode.left;
                        count++;
                    } else if (currentTreeNode.right.getString().contains(Character.toString(aChar))) {
                        bits.set(count, true);
                        currentTreeNode = currentTreeNode.right;
                        count++;
                    }
                }
            }
        }
        byte[] result = bits.toByteArray();
        return new CompressionResult(codeTree, result, count);
    }

    private static TreeNode getCodes(TreeNode[] data) {
        List<TreeNode> current = new ArrayList<>(List.of(data));
        Collections.sort(current);
        while (current.size() > 1) {
            TreeNode left = current.get(0);
            TreeNode right = current.get(1);
            current.add(new TreeNode(left.letter + right.letter, left.frequency + right.frequency, left, right));
            current.remove(0);
            current.remove(0);
            Collections.sort(current);
        }
        return current.get(0);
    }

    private static TreeNode[] getTreeNodes(Character[] sortedChars, Integer[] sortedFrequency) {
        TreeNode[] data = new TreeNode[sortedChars.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = new TreeNode(Character.toString(sortedChars[i]), sortedFrequency[i], null, null);
        }
        return data;
    }

    private static <T> void reverse(T[] arr) {
        T[] temp = arr.clone();
        final int l = arr.length;
        for (int i = temp.length - 1; i >= 0; i--) {
            arr[(l - i) - 1] = temp[i];
        }
    }

    private static HashMap<Character, Integer> createFrequencyHashMap(char[] chars) {
        HashMap<Character, Integer> frequency = new HashMap<>();
        for (char aChar : chars) {
            if (frequency.containsKey(aChar)) {
                frequency.put(aChar, frequency.get(aChar) + 1);
            } else {
                frequency.put(aChar, 1);
            }
        }
        return frequency;
    }

    public static HashMap<Character, Integer> sortByValue(HashMap<Character, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Character, Integer>> list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        list.sort(Map.Entry.comparingByValue());

        // put data from sorted list to hashmap
        HashMap<Character, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private static class TreeNode implements Comparable<TreeNode>, Serializable {
        private final String letter;
        private final int frequency;
        private final TreeNode left;
        private final TreeNode right;

        public TreeNode(String letter, Integer frequency, TreeNode left, TreeNode right) {
            this.letter = letter;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        public TreeNode getLeft() {
            return left;
        }

        public TreeNode getRight() {
            return right;
        }

        public String getString() {
            return letter;
        }

        public int getFrequency() {
            return frequency;
        }

        @Override
        public int compareTo(TreeNode o) {
            return Integer.compare(frequency, o.frequency);
        }

        public boolean isLast() {
            return right == null||left==null;
        }
    }

    private static class CompressionResult implements Serializable {
        TreeNode codeTree;
        byte[] result;
        int bitCount;

        public CompressionResult(TreeNode codeTree, byte[] result, int bitCount) {
            this.codeTree = codeTree;
            this.result = result;
            this.bitCount = bitCount;
        }
    }
}
