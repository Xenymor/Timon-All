package NeuralNetworkProjects.PrisonersDilemma.Strategies;

public class TitForTatWithCounts implements Strategy {
    private int rewardSum;
    private final int deceptionsBeforeRetaliate;
    private final int deceptionCount;
    private int beenDeceptedCounter = 0;
    private boolean isDecepting = false;
    private int deceptionCounter = 0;

    public TitForTatWithCounts(int deceptionsBeforeRetaliate, int deceptionCount) {
        this.deceptionsBeforeRetaliate = deceptionsBeforeRetaliate;
        this.deceptionCount = deceptionCount;
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
        if (isDecepting) {
            deceptionCounter++;
            if (deceptionCounter >= deceptionCount) {
                isDecepting = false;
                beenDeceptedCounter = 0;
                deceptionCounter = 0;
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void otherMove(boolean otherCooperated) {
        if (!otherCooperated && !isDecepting) {
            beenDeceptedCounter++;
            if (beenDeceptedCounter >= deceptionsBeforeRetaliate) {
                isDecepting = true;
                beenDeceptedCounter = 0;
            }
        }
    }

    @Override
    public void reset() {
        setRewardSum(0);
        beenDeceptedCounter = 0;
        isDecepting = false;
        deceptionCounter = 0;
    }

    @Override
    public String getName() {
        return deceptionsBeforeRetaliate + "TitFor" + deceptionCount + "Tat";
    }

    @Override
    public Strategy clone() {
        return new TitForTatWithCounts(deceptionsBeforeRetaliate, deceptionCount);
    }
}