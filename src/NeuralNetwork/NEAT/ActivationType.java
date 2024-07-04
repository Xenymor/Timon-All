package NeuralNetwork.NEAT;

@SuppressWarnings("SpellCheckingInspection")
public enum ActivationType {
    RELU(ActivationTypeType.ONLY_HIDDEN),
    ULER(ActivationTypeType.ONLY_HIDDEN),
    TANH(ActivationTypeType.BOTH),
    SIGMOID(ActivationTypeType.ONLY_HIDDEN),
    SIN(ActivationTypeType.BOTH);

    final ActivationTypeType type;

    ActivationType(final ActivationTypeType type) {
        this.type = type;
    }

    public ActivationTypeType getType() {
        return type;
    }

    enum ActivationTypeType {
        ONLY_HIDDEN, ONLY_OUTPUTS, BOTH
    }
}
