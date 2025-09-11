package HuffmanCompression;

import java.io.Serializable;

public record CompressionResult(TreeNode codeTree, byte[] result,
                                int bitCount) implements Serializable {
}