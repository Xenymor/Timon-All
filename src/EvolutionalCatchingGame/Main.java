package EvolutionalCatchingGame;

import StandardClasses.Random;
import StandardClasses.Vector2;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static final int INPUT_COUNT = 5;
    public static final int OUTPUT_COUNT = 2;
    public static final double SPEED = 1;
    private static final double MUTATION_STEP = 0.1d;
    private static final double MUTATION_RATE = 0.1d;

    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(1, 2, 5, 7, 3, 10, 15, -1);
        System.out.println(
                nums.stream()
                        .filter(v -> (v) % 2 == 0)
                        .sorted()
                        .map(Object::toString)
                        .collect(Collectors.joining("; ", "[", "]"))
        );
        new Main().start();
    }

    private void start() {
        GameCharacter[] agents = new GameCharacter[10];
        for (int i = 0; i < agents.length; i++) {
            agents[i] = new GameCharacter(SPEED, new Vector2(0, 0), INPUT_COUNT, OUTPUT_COUNT);
        }
        Vector2 targetPos = new Vector2(100, 100);
        //EvolutionaryNeuralNetwork guard = new EvolutionaryNeuralNetwork(INPUT_COUNT, LAYER_SIZE);

        boolean isRunning = true;
        double shortestDist = Double.POSITIVE_INFINITY;
        Rectangle[] obstacles = new Rectangle[1];
        obstacles[0] = new Rectangle(50, 50, 20, 20);
        while (isRunning) {
            gameLoop(obstacles, agents, targetPos, MUTATION_RATE, MUTATION_STEP);
            for (GameCharacter agent : agents) {
                Vector2 pos = agent.getPos();
                if (pos.rounded().equals(targetPos.rounded())) {
                    System.out.println("Did it; " + counter);
                    isRunning = false;
                } else {
                    shortestDist = Math.min(shortestDist, pos.getDist(targetPos));
                }
            }
        }
    }

    long counter = 0;

    private void gameLoop(Rectangle[] obstacles, GameCharacter[] agents, Vector2 targetPos, double mutationRate, double mutationStep) {
        for (int i = 0; i < agents.length; i++) {
            GameCharacter agent = agents[i];
            Vector2 currentPos = agent.gameLoop(obstacles, targetPos);
            currentPos.add(agents[i].getPos());
            for (int j = 0; j < obstacles.length; j++) {
                if (obstacles[i].contains(currentPos.getX(), currentPos.getY())) {
                    currentPos = agent.getPos();
                    break;
                }
            }
            agent.setPos(currentPos);
        }
        counter++;
        if (counter >= 105) {
            counter = 0;
            System.out.println("Reproducing");
            DoubleInt[] fitnesses = new DoubleInt[agents.length];
            for (int i = 0; i < agents.length; i++) {
                fitnesses[i] = new DoubleInt(-agents[i].getPos().getDist(targetPos), i);
            }
            Arrays.sort(fitnesses);
            GameCharacter[] parents = new GameCharacter[agents.length / 5];
            for (int i = 0; i < parents.length; i++) {
                parents[i] = agents[fitnesses[fitnesses.length - 1 - i].getI()];
            }
            for (int i = 0; i < agents.length; i++) {
                int i1 = Random.randomIntInRange(0, parents.length - 1);
                int i2 = Random.randomIntInRange(0, parents.length - 1);
                agents[i] = GameCharacter.reproduce(mutationRate, mutationStep, new double[]{fitnesses[fitnesses.length - 1 - i1].getV(), fitnesses[fitnesses.length - 1 - i2].getV()}, parents[i1], parents[i2]);
                agents[i].setPos(new Vector2(0, 0));
            }
        }
    }
}
