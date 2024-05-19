package NeuralNetworkProjects.PrisonersDilemma.Strategies;

import java.util.Random;

public class ParamTitForTat implements Strategy{
    private int rewardSum;
    private final int deceptionsBeforeRetaliate;
    private final int deceptionCount;
    private int beenDeceptedCounter = 0;
    private boolean isDecepting = false;
    private int deceptionCounter = 0;
    final Random random = new Random();
    boolean lastMove = true;
    private final double forgivenessProbability;
    private final double deceitProbability;

    public ParamTitForTat(int deceptionsBeforeRetaliate, int deceptionCount, final double forgivenessProbability, final double deceitProbability) {
        this.deceptionsBeforeRetaliate = deceptionsBeforeRetaliate;
        this.deceptionCount = deceptionCount;
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
        if (isDecepting) {
            deceptionCounter++;
            if (deceptionCounter >= deceptionCount) {
                isDecepting = false;
                beenDeceptedCounter = 0;
                deceptionCounter = 0;
            }
            return false;
        } else {
            return random.nextDouble() > deceitProbability;
        }
    }

    @Override
    public void otherMove(boolean otherCooperated) {
        if (otherCooperated) {
            lastMove = true;
        } else {
            lastMove = random.nextDouble() < forgivenessProbability;
        }
        if (!lastMove && !isDecepting) {
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
        return deceptionsBeforeRetaliate + "TitFor" + deceptionCount + "Tat" + Math.round(forgivenessProbability*100)/100f + ";" + Math.round(deceitProbability*100)/100f;
    }

    @Override
    public Strategy clone() {
        return new ParamTitForTat(deceptionsBeforeRetaliate, deceptionCount, forgivenessProbability, deceitProbability);
    }
}
