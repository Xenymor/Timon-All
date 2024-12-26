package HuffmanCompression;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HuffmanCompression {

    public static void main(String[] args) throws IOException {
        String path = "src/HuffmanCompression/TrainingDataEnglish.txt";
        if (args.length != 0) {
            char[] chars = args[0].toCharArray();
            if (chars[0] == '-') {
                path = args[0].substring(1);
            }
        }
        byte[] inputByteArray = Files.readAllBytes(Paths.get(path));
        String input = new String(inputByteArray, StandardCharsets.UTF_8);
        long startingTime = System.nanoTime();
        CompressionResult compressed = compress(input);
        System.out.println(compressed.bitCount);
        long decompressStart = System.nanoTime();
        String output = decompress(compressed);
        long finish = System.nanoTime();
        System.out.println("Time for compressing: " + TimeUnit.NANOSECONDS.toMillis(decompressStart - startingTime));
        System.out.println("Time for decompressing: " + TimeUnit.NANOSECONDS.toMillis(finish - decompressStart));
        System.out.println(input.equals(output));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(path + ".hfmcpr"));
        objectOutputStream.writeObject(compressed);
        objectOutputStream.close();
        System.out.println(compressed.result.length + "/" + input.length());
        //System.out.println(Arrays.toString(compressed.result));
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
                stringBuilder.append(current.uncompressedChar);
                current = compressed.codeTree;
            }
        }
        return stringBuilder.toString();
    }

    public static CompressionResult compress(String input) {
        char[] chars = input.toCharArray();
        HashMap<Character, Integer> frequency = createFrequencyHashMap(chars);
        Character[] sortedChars = frequency.keySet().toArray(new Character[0]);
        Integer[] sortedFrequency = frequency.values().toArray(new Integer[0]);
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
                    if (currentTreeNode.left.contains(aChar)) {
                        bits.set(count, false);
                        currentTreeNode = currentTreeNode.left;
                        count++;
                    } else if (currentTreeNode.right.contains(aChar)) {
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
            BitSet clone = (BitSet) left.letter.clone();
            clone.or(right.letter);
            current.add(new TreeNode(clone, left.frequency + right.frequency, left, right));
            current.remove(0);
            current.remove(0);
            Collections.sort(current);
        }
        return current.get(0);
    }

    private static TreeNode[] getTreeNodes(Character[] sortedChars, Integer[] sortedFrequency) {
        TreeNode[] data = new TreeNode[sortedChars.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = new TreeNode(sortedChars[i], sortedFrequency[i], null, null);
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
}
