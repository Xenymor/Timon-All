package NeuralNetwork;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

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

    public NetworkLearnData clone() {
        final NeuralNetwork.NetworkLearnData networkLearnData = (NeuralNetwork.NetworkLearnData) super.clone();
        NetworkLearnData result = new NetworkLearnData(new Layer[0]);
        result.layerData = new LayerLearnData[layerData.length];
        for (int i = 0; i < layerData.length; i++) {
            result.layerData[i] = layerData[i].clone();
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final NetworkLearnData that = (NetworkLearnData) o;
        return Arrays.equals(layerData, that.layerData);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(layerData);
    }
}
