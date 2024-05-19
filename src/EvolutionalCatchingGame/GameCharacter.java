package EvolutionalCatchingGame;

import NeuralNetwork.*;
import StandardClasses.Vector2;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

public class GameCharacter implements Serializable {
    public int finishCounter;
    Vector2 pos;
    final double speed;
    NeuralNetwork neuralNetwork;
    private boolean disabled = false;
    private final int INPUT_COUNT;

    public GameCharacter(final double speed, final Vector2 pos, final int inputCount, final int... layerSizes) {
        this.speed = speed;
        this.pos = pos;
        this.neuralNetwork = new NeuralNetwork(NeuralNetworkType.EVOLUTIONARY, inputCount, layerSizes);
        this.INPUT_COUNT = inputCount;
        finishCounter = -1;
    }

    public Vector2 getPos() {
        return pos;
    }

    public Vector2 gameLoop(final Rectangle[] obstacles, final Vector2 targetPos) {
        if (!disabled) {
            double[] inputs = new double[INPUT_COUNT];
            Vector2 relativeTargetPos = pos.getDir(targetPos);
            final double x = pos.getX();
            final double y = pos.getY();
            inputs[0] = relativeTargetPos.getX();
            inputs[1] = relativeTargetPos.getY();
            int j = 2;
            for (final Rectangle obstacle : obstacles) {
                inputs[j] = obstacle.x - x;
                inputs[j + 1] = obstacle.y - y;
                inputs[j + 2] = obstacle.x + obstacle.width - x;
                inputs[j + 3] = obstacle.y + obstacle.height - y;
                j += 4;
            }
            double[] outputs = neuralNetwork.getOutputs(inputs);
            Vector2 movement = new Vector2(outputs[0]-0.5, outputs[1]-0.5);
            movement.clamp(speed);
            pos.add(movement);
        }
        return pos;
    }

    public void setPos(final Vector2 currentPos) {
        pos = currentPos;
    }

    public GameCharacter mutate(double standardDeviation) {
        GameCharacter result = new GameCharacter(speed, pos, INPUT_COUNT, 0);
        result.neuralNetwork = neuralNetwork.clone();
        result.neuralNetwork.mutate(standardDeviation);
        return result;
    }

    public void disable() {
        disabled = true;
    }

    public void finished(final int counter) {
        finishCounter = counter;
        disable();
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void reset(final Vector2 vector2) {
        pos = vector2;
        disabled = false;
        finishCounter = -1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GameCharacter that = (GameCharacter) o;
        return neuralNetwork.equals(that.neuralNetwork);
    }

    @Override
    public int hashCode() {
        return Objects.hash(finishCounter, pos, speed, neuralNetwork, disabled, INPUT_COUNT);
    }
}
