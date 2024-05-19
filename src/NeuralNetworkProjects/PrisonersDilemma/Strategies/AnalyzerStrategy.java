package NeuralNetworkProjects.PrisonersDilemma.Strategies;

public class AnalyzerStrategy implements Strategy {
    int rewardSum = 0;
    boolean myMove = true;
    boolean myLastMove = true;
    boolean answer = true;

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
        if (myLastMove) {
            myLastMove = myMove;
            myMove = false;
            return false;
        } else {
            if (answer) {
                myLastMove = myMove;
                myMove = false;
                return false;
            } else {
                myLastMove = myMove;
                myMove = true;
                return true;
            }
        }
    }


    @Override
    public void otherMove(final boolean otherCooperated) {
        answer = otherCooperated;
    }

    @Override
    public void reset() {
        rewardSum = 0;
    }

    @Override
    public String getName() {
        return "Analyzer";
    }

    @Override
    public Strategy clone() {
        final Strategy strategy = (Strategy) super.clone();
        return new AnalyzerStrategy();
    }
}
