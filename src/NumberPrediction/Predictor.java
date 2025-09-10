package NumberPrediction;

import java.util.ArrayList;
import java.util.List;

public class Predictor {
    Node root;
    List<Boolean> choices;

    public Predictor() {
        root = new Node();
        choices = new ArrayList<>();
    }

    public void update(final boolean number) {
        choices.add(number);
        for (int i = 0; i < choices.size(); i++) {
            updateRecursively(root, choices.subList(i, choices.size()), 0);
        }
    }

    private void updateRecursively(final Node node, final List<Boolean> choices, final int index) {
        if (index == choices.size()) {
            node.count++; // Increment count at the leaf node
            return; // Base case: reached the end of the choices list
        }

        boolean choice = choices.get(index);
        if (choice) {
            if (node.right == null) {
                node.right = new Node();
            }
            updateRecursively(node.right, choices, index + 1);
        } else {
            if (node.left == null) {
                node.left = new Node();
            }
            updateRecursively(node.left, choices, index + 1);
        }
    }

    public boolean predict() {
        for (int i = 0; i < choices.size(); i++) {
            Boolean prediction = getMoreCommonChoice(choices.subList(i, choices.size()));
            if (prediction != null) {
                return prediction;
            }
        }
        return true; // Default prediction if no choices are available
    }

    private Boolean getMoreCommonChoice(final List<Boolean> choices) {
        Node currentNode = root;
        for (Boolean choice : choices) {
            if (choice) {
                if (currentNode.right == null) {
                    return null;
                }
                currentNode = currentNode.right;
            } else {
                if (currentNode.left == null) {
                    return null;
                }
                currentNode = currentNode.left;
            }
        }
        if (currentNode.left == null && currentNode.right == null) {
            return null; // No choices available
        }
        if (currentNode.left == null) {
            return true; // Only right child exists, predict true
        }
        if (currentNode.right == null) {
            return false; // Only left child exists, predict false
        }
        return currentNode.right.count > currentNode.left.count;
    }

    public void printChoices() {
        System.out.println("Choices made:");
        System.out.println(choices.stream()
                .map(choice -> choice ? "1" : "0")
                .reduce((a, b) -> a + ", " + b)
                .orElse("No choices made"));
        System.out.println("Total choices: " + choices.size());
    }
}
