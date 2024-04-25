package HuffmanCompression;

import java.io.Serializable;

public class CompressionResult implements Serializable {
    TreeNode codeTree;
    byte[] result;
    int bitCount;

    public CompressionResult(TreeNode codeTree, byte[] result, int bitCount) {
        this.codeTree = codeTree;
        this.result = result;
        this.bitCount = bitCount;
    }
}