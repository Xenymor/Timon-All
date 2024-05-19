package NeuralNetwork;

public class Activation {
    public static double activate(double value)
    {
        return 1.0 / (1 + Math.exp(-value));
    }

    public static double derivative(double[] inputs, int index)
    {
        //return 1 / (1+inputs[index]*inputs[index]);
        double a = activate(inputs[index]);
        return a * (1 - a);
    }
}
