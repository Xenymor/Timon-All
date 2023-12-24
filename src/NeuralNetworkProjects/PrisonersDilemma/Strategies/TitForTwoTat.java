package NeuralNetworkProjects.PrisonersDilemma.Strategies;

public class TitForTwoTat implements Strategy {
    int rewardSum = 0;
    int notCoopCount = 0;

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
        return notCoopCount <= 1;
    }

    @Override
    public void otherMove(final boolean otherCooperated) {
        if (otherCooperated) {
            notCoopCount = 0;
        } else {
            notCoopCount++;
        }
    }

    @Override
    public void reset() {
        rewardSum = 0;
        notCoopCount = 0;
    }

    @Override
    public String getName() {
        return "TitForTwoTat";
    }

    @Override
    public Strategy clone() {
        return new TitForTwoTat();
    }
}
