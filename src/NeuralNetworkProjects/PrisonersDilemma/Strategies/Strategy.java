package NeuralNetworkProjects.PrisonersDilemma.Strategies;

public interface Strategy {

    default void addReward(int reward) {
        setRewardSum(getRewardSum()+reward);
    }

    int getRewardSum();

    void setRewardSum(int value);

    boolean getMove();

    void otherMove(boolean otherCooperated);

    void reset();

    String getName();

    Strategy clone();
}
