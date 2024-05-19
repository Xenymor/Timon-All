package NeuralNetworkProjects.PrisonersDilemma.Strategies;

import java.util.Arrays;

public class lbHistoryGrudgeHolder implements Strategy {

    private int rewardSum;
    private final int lookbackRounds;

    final boolean[] opponentHistory;

    public lbHistoryGrudgeHolder(int lookbackRounds) {
        this.rewardSum = 0;
        this.lookbackRounds = lookbackRounds;
        opponentHistory = new boolean[lookbackRounds];
        Arrays.fill(opponentHistory, true);
    }

    @Override
    public void addReward(int reward) {
        setRewardSum(getRewardSum() + reward);
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

        // Check if the recent opponent's history contains a 'D' (deception)

        return contains(false);
    }

    private boolean contains(@SuppressWarnings("SameParameterValue") final boolean b) {
        for (final boolean value : opponentHistory) {
            if (value == b) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void otherMove(boolean otherCooperated) {
        if (lookbackRounds - 1 >= 0) System.arraycopy(opponentHistory, 1, opponentHistory, 0, lookbackRounds - 1);
        if (lookbackRounds > 0)
            opponentHistory[lookbackRounds - 1] = otherCooperated;
    }

    @Override
    public void reset() {
        rewardSum = 0;
        Arrays.fill(opponentHistory, true);
    }

    @Override
    public String getName() {
        return "lbHistory" + lookbackRounds;
    }

    @Override
    public Strategy clone() {
        return new lbHistoryGrudgeHolder(lookbackRounds);
    }

}
