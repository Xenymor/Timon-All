package NeuralNetworkProjects.PrisonersDilemma;

public class AlwaysChooseStrategy implements Strategy {
    final boolean choice;
    int rewardSum = 0;

    public AlwaysChooseStrategy(final boolean toChoose) {
        choice = toChoose;
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
        return choice;
    }

    @Override
    public void otherMove(final boolean otherCooperated) {

    }

    @Override
    public void reset() {
        rewardSum = 0;
    }
}
