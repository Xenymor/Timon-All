package NeuralNetworkProjects.PrisonersDilemma.Strategies;

import java.io.Serializable;

public interface Strategy extends Serializable {

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
