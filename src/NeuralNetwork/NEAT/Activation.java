package NeuralNetwork.NEAT;

public class Activation {

    public static double get(final double value, final ActivationType activationType) {
        switch (activationType) {
            case RELU -> {
                return Math.max(0, value);
            }
            case TANH -> {
                return Math.tanh(value);
            }
            case SIGMOID -> {
                return 1 / (1 + Math.exp(-value));
            }
            case SIN -> {
                return Math.sin(value);
            }
            default -> {
                return value;
            }
        }
    }
}
