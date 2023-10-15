package NeuralNetwork;

import java.io.Serial;
import java.io.Serializable;

public class NetworkLearnData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    public LayerLearnData[] layerData;

    public NetworkLearnData(Layer[] layers) {
        layerData = new LayerLearnData[layers.length];
        for (int i = 0; i < layers.length; i++) {
            layerData[i] = new LayerLearnData(layers[i]);
        }
    }
}
