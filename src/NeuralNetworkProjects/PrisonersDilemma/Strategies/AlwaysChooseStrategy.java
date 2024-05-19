package NeuralNetworkProjects.PrisonersDilemma.Strategies;

import java.util.Objects;

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

    @Override
    public String getName() {
        final String s = "AlwaysChoose" + choice;
        final char ch = s.charAt(12);
        return s.replace(ch, Character.toUpperCase(ch));
    }

    @Override
    public Strategy clone() {
        final Strategy strategy = (Strategy) super.clone();
        return new AlwaysChooseStrategy(choice);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AlwaysChooseStrategy that = (AlwaysChooseStrategy) o;
        return choice == that.choice;
    }

    @Override
    public int hashCode() {
        return Objects.hash(choice, rewardSum);
    }
}
