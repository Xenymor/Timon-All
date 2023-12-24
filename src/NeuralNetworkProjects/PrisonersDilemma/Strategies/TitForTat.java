package NeuralNetworkProjects.PrisonersDilemma.Strategies;

public class TitForTat implements Strategy {
    int rewardSum = 0;
    boolean lastAnswer = true;

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
        return lastAnswer;
    }

    @Override
    public void otherMove(final boolean otherCooperated) {
        lastAnswer = otherCooperated;
    }

    @Override
    public void reset() {
        rewardSum = 0;
        lastAnswer = true;
    }

    @Override
    public String getName() {
        return "TitForTat";
    }

    @Override
    public Strategy clone() {
        return new TitForTat();
    }
}
