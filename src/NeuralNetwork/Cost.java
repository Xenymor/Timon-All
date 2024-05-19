package NeuralNetwork;

public class Cost {
    public static double costFunction(double[] predictedOutputs, double[] expectedOutputs)
    {
        // cost is sum (for all x,y pairs) of: 0.5 * (x-y)^2
        double cost = 0;
        for (int i = 0; i < predictedOutputs.length; i++)
        {
            double x = predictedOutputs[i];
            double y = expectedOutputs[i];
            //double v = (y == 1) ? -Math.log(x) : -Math.log(1 - x);
            double v = x-y;
            v *= v;
            cost += Double.isNaN(v) ? 0 : v;
        }
        return cost;
    }

    public static double costDerivative(double predictedOutput, double expectedOutput)
    {
        return 2 * (predictedOutput-expectedOutput);
    }
}
