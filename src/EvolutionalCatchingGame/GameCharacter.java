package EvolutionalCatchingGame;

import EvolutionalNeuralNetwork.EvolutionaryNeuralNetwork;
import StandardClasses.Random;
import StandardClasses.Vector2;

import java.awt.*;

public class GameCharacter {
    private final EvolutionaryNeuralNetwork neuralNetwork;
    private double speed;
    private Vector2 pos;

    public static GameCharacter reproduce(double mutationRate, double mutationStep, double[] fitnesses, GameCharacter... parents) {
        Vector2 pos = parents[Random.randomIntInRange(0, parents.length - 1)].getPos();
        EvolutionaryNeuralNetwork[] parentNets = new EvolutionaryNeuralNetwork[parents.length];
        for (int i = 0; i < parents.length; i++) {
            parentNets[i] = parents[i].neuralNetwork;
        }
        return new GameCharacter(EvolutionaryNeuralNetwork.reproduce(mutationRate, mutationStep, fitnesses, parentNets), parents[Random.randomIntInRange(0, parents.length - 1)].speed, pos);
    }

    public Vector2 getPos() {
        return pos;
    }

    public GameCharacter(double speed, Vector2 startingPos, int inputCount, int... layerSizes) {
        this.speed = speed;
        this.pos = startingPos;
        this.neuralNetwork = new EvolutionaryNeuralNetwork(inputCount, layerSizes);
    }

    public GameCharacter(EvolutionaryNeuralNetwork neuralNetwork, double speed, Vector2 pos) {
        this.neuralNetwork = neuralNetwork;
        this.speed = speed;
        this.pos = pos;
    }

    public Vector2 gameLoop(Rectangle[] obstacles, Vector2 targetPos) {
        Vector2 dir1 = pos.getDir(targetPos);
        /*TODO double dist1 = Ray.getDist(pos, new Vector2(1, 1), obstacles);
        double dist2 = Ray.getDist(pos, new Vector2(2, 1), obstacles);
        double dist3 = Ray.getDist(pos, new Vector2(1, 2), obstacles);*/
        double[] outputs = neuralNetwork.getOutputs(dir1.getX(), dir1.getY());
        return new Vector2(outputs[0] * speed, outputs[1] * speed);
    }

    public void move(Vector2 dir) {
        pos.add(dir);
    }

    public void setPos(Vector2 vector2) {
        this.pos = new Vector2(vector2);
    }
}
