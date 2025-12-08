import StandardClasses.Vectors.Vector2I;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestAdjacencyGenerator {
    public static void main(String[] args) {
        List<Vector2I> adj = new ArrayList<>();
        Map<Integer, Integer> codeToIndex = new HashMap<>();
        List<Integer> indexToCode = new ArrayList<>();

        List<Integer> nodeCounts = new ArrayList<>();
        List<Integer> edgeCounts = new ArrayList<>();

        for (int size = 0; size < 50; size++) {
            int nodeCount = 0;
            codeToIndex.clear();
            adj.clear();
            indexToCode.clear();
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    for (int z = 0; z < size; z++) {
                        final int nodeCode = (x << (6 * 3)) + (y << (6 * 2)) + (z << 6);
                        codeToIndex.put(nodeCode, nodeCount);
                        indexToCode.add(nodeCode);
                        nodeCount++;
                    }
                }
            }
            for (int x = 0; x < nodeCount; x++) {
                for (int y = 0; y < nodeCount; y++) {
                    if (x != y) {
                        if (isAdjacent(indexToCode.get(x), indexToCode.get(y))) {
                            adj.add(new Vector2I(x, y));
                        }
                    }
                }
            }
            System.out.println("Ingr.: " + size + "\t|V|=" + nodeCount + "\t|E|=" + adj.size());
            nodeCounts.add(nodeCount);
            edgeCounts.add(adj.size());
            /*for (Vector2I edge : adj) {
                System.out.println(toName(indexToCode.get(edge.getX())) + ">" + toName(indexToCode.get(edge.getY())));
            }*/
        }
        StringBuilder x1 = new StringBuilder();
        StringBuilder x2 = new StringBuilder();
        StringBuilder x3 = new StringBuilder();
        for (int i = 0; i < nodeCounts.size(); i++) {
            x1.append(i).append(", ");
            x2.append(nodeCounts.get(i)).append(", ");
            x3.append(edgeCounts.get(i)).append(", ");
        }
        System.out.println(x1);
        System.out.println(x2);
        System.out.println(x3);
    }

    private static boolean isAdjacent(final Integer x, final Integer y) {
        return (((x >> (6 * 2)) & 63) == ((y >> (6 * 3)) & 63)) && (((x >> (6)) & 63) == ((y >> (6 * 2)) & 63));
    }

}
