package NeuralNetworkProjects.PrisonersDilemma.Strategies;

import java.util.Objects;

public class GrudgeStrategy implements Strategy {
    boolean alwaysCooperated = true;
    int rewardSum = 0;

    public GrudgeStrategy() {
    }

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
        return alwaysCooperated;
    }

    @Override
    public void otherMove(final boolean otherCooperated) {
        if (!otherCooperated) {
            alwaysCooperated = false;
        }
    }

    @Override
    public void reset() {
        rewardSum = 0;
        alwaysCooperated = true;
    }

    @Override
    public String getName() {
        return "GrudgeHolder";
    }

    @Override
    public Strategy clone() {
        final Strategy strategy = (Strategy) super.clone();
        return new GrudgeStrategy();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(rewardSum);
    }
}
