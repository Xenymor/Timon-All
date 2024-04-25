package HuffmanCompression;

import java.io.Serializable;
import java.util.BitSet;

public class TreeNode implements Comparable<TreeNode>, Serializable {
    final BitSet letter;
    final int frequency;
    final TreeNode left;
    final TreeNode right;
    char uncompressedChar;

    public TreeNode(BitSet letter, Integer frequency, TreeNode left, TreeNode right) {
        this.letter = letter;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    public TreeNode(Character character, Integer frequency, TreeNode left, TreeNode right) {
        this(charToBitSet(character), frequency, left, right);
        uncompressedChar = character;
    }

    private static BitSet charToBitSet(Character character) {
        BitSet result = new BitSet();
        result.set(character);
        return result;
    }

    public TreeNode getLeft() {
        return left;
    }

    public TreeNode getRight() {
        return right;
    }

    public BitSet getString() {
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
        return right == null || left == null;
    }

    public boolean contains(char aChar) {
        return letter.get(aChar);
    }
}
