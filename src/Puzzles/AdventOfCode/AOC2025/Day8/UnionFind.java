package Puzzles.AdventOfCode.AOC2025.Day8;

public class UnionFind {
    private final int[] parent;
    final int[] size;

    public UnionFind(int size) {

        // Initialize the parent array with each
        // element as its own representative
        parent = new int[size];
        this.size = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            this.size[i] = 1;
        }
    }

    // Find the representative (root) of the
    // set that includes element i
    public int find(int i) {

        // if i itself is root or representative
        if (parent[i] == i) {
            return i;
        }

        // Else recursively find the representative
        // of the parent
        return find(parent[i]);
    }

    // Unite (merge) the set that includes element
    // i and the set that includes element j
    public void union(int i, int j) {

        // Representative of set containing i
        int irep = find(i);

        // Representative of set containing j
        int jrep = find(j);

        // Make the representative of i's set be
        // the representative of j's set
        if (irep == jrep) {
            return;
        }
        if (size[jrep] == 0 || size[irep] == 0) {
            System.out.println("Zero size detected");
        }
        if (size[jrep] < 0 || size[irep] < 0) {
            System.out.println("Negative size detected");
        }
        parent[irep] = jrep;
        size[jrep] += size[irep];
    }
}

