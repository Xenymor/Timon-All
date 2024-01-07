package NeuralNetworkProjects.PrisonersDilemma.Strategies;

import java.util.Random;

public class TitForTatWithRandomness implements Strategy {
    private final double deceitProbability;
    private int rewardSum;
    private boolean lastMove;
    private final double forgivenessProbability;
    Random random = new Random();

    public TitForTatWithRandomness(double forgivenessProbability, double deceitProbability) {
        this.forgivenessProbability = forgivenessProbability;
        this.deceitProbability = deceitProbability;
        reset();
    }

    @Override
    public int getRewardSum() {
        return rewardSum;
    }

    @Override
    public void setRewardSum(int value) {
        rewardSum = value;
    }

    @Override
    public boolean getMove() {
        return lastMove;
    }

    @Override
    public void otherMove(boolean otherCooperated) {
        if (otherCooperated) {
            lastMove = random.nextDouble() > deceitProbability;
        } else {
            // Defect, but with forgiveness probability
            lastMove = random.nextDouble() < forgivenessProbability;
        }
    }

    @Override
    public void reset() {
        rewardSum = 0;
        lastMove = true;  // Start with cooperation
    }

    @Override
    public String getName() {
        return "TitForTatWithRandomness" + Math.round(forgivenessProbability*100)/100f + ";" + Math.round(deceitProbability*100)/100f;
    }

    @Override
    public Strategy clone() {
        return new TitForTatWithRandomness(forgivenessProbability, deceitProbability);
    }
}

