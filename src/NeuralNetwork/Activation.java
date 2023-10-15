package NeuralNetwork;

public class Activation {
    /*public static double activate(double[] inputs, int index)
    {
        return 1.0 / (1 + Math.exp(-inputs[index]));
    }

    public static double derivative(double[] inputs, int index)
    {
        double a = activate(inputs, index);
        return a * (1 - a);
    }*/
    public static double activate(double[] inputs, int index)
    {
        /*double expSum = 0;
        for (final double input : inputs) {
            expSum += Math.exp(input);
        }

        return Math.exp(inputs[index]) / expSum;*/
        return Math.atan(inputs[index]);
    }

    public static double derivative(double[] inputs, int index)
    {
        /*double expSum = 0;
        for (final double input : inputs) {
            expSum += Math.exp(input);
        }

        double ex = Math.exp(inputs[index]);

        if (expSum == 0) {
            System.out.println();
        }

        //final double v = (ex * expSum - ex * ex) / (expSum * expSum);
        final double v = (ex/expSum) * ((expSum -  ex)/expSum);
        if (Double.isNaN(v)) {
            System.out.println();
        }
        return v;*/
        /*if (inputs[index] <= 0) {
            return 0;
        } else {
            return 1;
        }*/
        return 1 / (1+inputs[index]*inputs[index]);
    }
}
