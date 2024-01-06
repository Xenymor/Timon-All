package NeuralNetworkProjects.PrisonersDilemma.Strategies;

public class AnikaStrategy implements Strategy {
    int rewardSum = 0;
    boolean lastMove = true;

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
        return !lastMove;
    }

    @Override
    public void otherMove(final boolean otherCooperated) {
        lastMove = otherCooperated;
    }

    @Override
    public void reset() {
        rewardSum = 0;
        lastMove = true;
    }

    @Override
    public String getName() {
        return "AnikaStrategy";
    }

    @Override
    public Strategy clone() {
        return new AnikaStrategy();
    }
}
