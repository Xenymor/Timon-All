package NeuralNetworkProjects.PrisonersDilemma.Strategies;

import java.util.Random;

public class RandomStrategy implements Strategy {
    int rewardSum = 0;
    final Random random = new Random((long) (Math.random()*1_000_000_000));

    @Override
    public int getRewardSum() {
        return rewardSum;
    }

    @Override
    public void setRewardSum(final int value) {
        rewardSum = value;
    }

    @Override
    public boolean getMove() {
        return random.nextBoolean();
    }

    @Override
    public void otherMove(final boolean otherCooperated) {

    }

    @Override
    public void reset() {
        rewardSum = 0;
    }

    @Override
    public String getName() {
        return "Random";
    }

    @Override
    public Strategy clone() {
        return new RandomStrategy();
    }
}
