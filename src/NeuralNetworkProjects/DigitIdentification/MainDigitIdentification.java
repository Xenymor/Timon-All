package NeuralNetworkProjects.DigitIdentification;

import NeuralNetwork.NeuralNetwork;

public class MainDigitIdentification {
    public MainDigitIdentification(final NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    public MainDigitIdentification() {
        neuralNetwork = new NeuralNetwork(0, 0);
    }

    public static void main(String[] args) {
        new MainDigitIdentification().run();
    }

    NeuralNetwork neuralNetwork;

    private void run() {

    }
}
