package EvolutionalCatchingGame;

import StandardClasses.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static final int INPUT_COUNT = 2;
    public static final int OUTPUT_COUNT = 2;
    public static final double SPEED = 1;
    private static final double MUTATION_RATE = .05d;
    public static final int MAX_MOVES = 250;
    public static final int AGENT_COUNT = 300;
    public static final Vector2 START_POS = new Vector2(100, 100);
    public static final int ZOOM_FACTOR = 2;
    public static final String SAVE_PATH = "C:\\Users\\timon\\IdeaProjects\\Timon-All\\src\\EvolutionalCatchingGame\\best.enl";
    private static final int MAX_ITERATIONS = 100_000;
    public static final int FIELD_SIZE = 200;
    GameCharacter lastBest = null;
    GameCharacter best = null;
    final GameCharacter[] agents = new GameCharacter[AGENT_COUNT];
    final Rectangle[] obstacles = new Rectangle[2];
    final Vector2 targetPos = new Vector2(0, FIELD_SIZE);
    int outerCounter = 0;

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {
        new Main().start();
    }

    private void loadBest() throws IOException, ClassNotFoundException {
        if (Files.exists(Path.of(SAVE_PATH))) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(SAVE_PATH));
            best = (GameCharacter) objectInputStream.readObject();
            objectInputStream.close();
        }
    }

    public Main() {
        obstacles[0] = new Rectangle(25, 15, 0, 0);
        obstacles[1] = new Rectangle(150, 15, 0, 0);
        for (int i = 0; i < agents.length; i++) {
            agents[i] = new GameCharacter(SPEED, new Vector2(START_POS), INPUT_COUNT + obstacles.length * 4, 12, 5, OUTPUT_COUNT);
        }
    }

    double bestFitness = Double.NEGATIVE_INFINITY;

    private void start() throws InterruptedException, IOException, ClassNotFoundException {
        AtomicBoolean showSim = new AtomicBoolean(false);
        final MyThread thread = new MyThread();
        thread.start();
        if (showSim.get()) {
            thread.myStart();
        } else {
            loadBest();
            if (best != null) {
                agents[0] = best.mutate(0);
                best.reset(START_POS);
            }
        }

        final AtomicBoolean isRunning = new AtomicBoolean(true);
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                if (scanner.hasNextLine()) {
                    String msg = scanner.nextLine();
                    switch (msg) {
                        case "s" -> {
                            save();
                            System.out.println("Saved");
                        }
                        case "e" -> {
                            isRunning.set(false);
                            System.out.println("Exited");
                            System.exit(0);
                        }
                        case "t" -> {
                            final boolean newValue = !showSim.get();
                            showSim.set(newValue);
                            if (newValue) {
                                thread.myStart();
                            } else {
                                thread.myStop();
                            }
                            System.out.println("Toggled to " + showSim);
                        }
                        default -> System.out.println("Didn't understand command");
                    }
                }
            }
        }).start();
        while (isRunning.get()) {
            gameLoop(obstacles, agents, targetPos);
            if (counter >= MAX_MOVES - 1) {
                if (outerCounter % 300 == 0 || showSim.get()) {
                    System.out.println("Best fitness: " + bestFitness);
                    save();
                }
                outerCounter++;
            }
            if (outerCounter >= MAX_ITERATIONS) {
                save();
                isRunning.set(false);
                System.exit(0);
            }
            if (showSim.get())
                Thread.sleep(2);
        }
    }

    private void save() {
        try {
            if (!Files.exists(Path.of(SAVE_PATH))) {
                Files.createFile(Path.of(SAVE_PATH));
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(SAVE_PATH));
            objectOutputStream.writeObject(best);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int counter = 0;
    int startOuterCounter = 0;

    private void gameLoop(Rectangle[] obstacles, GameCharacter[] agents, Vector2 targetPos) {
        for (GameCharacter agent : agents) {
            if (!agent.isDisabled()) {
                Vector2 currentPos = agent.gameLoop(obstacles, targetPos);
                final double x = currentPos.getX();
                final double y = currentPos.getY();
                for (final Rectangle obstacle : obstacles) {
                    if (obstacle.contains(x, y)) {
                        agent.disable();
                        break;
                    }
                }
                if (currentPos.getDist(targetPos) < 3) {
                    agent.finished(counter);
                }
            }
        }
        counter++;
        if (counter >= MAX_MOVES) {
            int finishedCounter = 0;
            DoubleInt[] fitnesses = new DoubleInt[agents.length];
            for (int i = 0; i < agents.length; i++) {
                final GameCharacter agent = agents[i];
                final boolean notFinished = agent.finishCounter == -1;
                if (!notFinished) {
                    finishedCounter++;
                }
                fitnesses[i] = new DoubleInt(notFinished ? Math.min(1 / agent.getPos().getDist(targetPos), 1_000) - (agent.isDisabled() ? 100 : 0) : -agent.finishCounter + 10_000, i);
                if (Double.isNaN(fitnesses[i].getV())) {
                    System.out.println();
                }
            }
            Arrays.sort(fitnesses);
            GameCharacter[] parents = new GameCharacter[agents.length];
            for (int i = 0; i < parents.length; i++) {
                parents[i] = agents[fitnesses[fitnesses.length - 1 - i].getI()];
            }
            bestFitness = fitnesses[fitnesses.length - 1].getV();
            //System.out.println("\tBest fitness: " + fitnesses[fitnesses.length - 1].getV());
            best = parents[0];
            for (int i = 0; i < agents.length; i++) {
                if (i < agents.length/30) {
                    agents[i] = parents[i].mutate(0);
                } else {
                    agents[i] = parents[(int) (Math.pow(Math.random(), 4) * parents.length)].mutate(Main.MUTATION_RATE);
                }
                agents[i].reset(new Vector2(START_POS));
            }
            if (finishedCounter > agents.length/10 || outerCounter-startOuterCounter > 100) {
                if (outerCounter-startOuterCounter > 100 && lastBest != null) {
                    agents[1] = lastBest;
                } else {
                    lastBest = best;
                }
                startOuterCounter = outerCounter;
                final double x = START_POS.getX();
                final double y = START_POS.getY();
                for (final Rectangle obstacle : obstacles) {
                    if (outerCounter < 1500) {
                        obstacle.width = 0;
                        obstacle.height = 0;
                    } else {
                        do {
                            obstacle.width = (int) (Math.random() * (FIELD_SIZE * 0.875));
                            obstacle.height = (int) (Math.random() * (FIELD_SIZE * 0.875));
                        } while (obstacle.contains(x, y));
                    }
                }
                do {
                    targetPos.setX(Math.random() * FIELD_SIZE);
                    targetPos.setY(Math.random() * FIELD_SIZE);
                } while (anyContains(obstacles, targetPos));
            }
            counter = 0;
        }
    }

    private boolean anyContains(final Rectangle[] obstacles, final Vector2 targetPos) {
        final double x = targetPos.getX();
        final double y = targetPos.getY();
        for (final Rectangle obstacle : obstacles) {
            if (obstacle.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    private class MyFrame extends JFrame {
        @Override
        public void paint(final Graphics g) {
            final int width = getWidth();
            final int height = getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            final Graphics graphics = image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);
            for (final Rectangle obstacle : obstacles) {
                graphics.fillRect(obstacle.x * ZOOM_FACTOR, obstacle.y * ZOOM_FACTOR, obstacle.width * ZOOM_FACTOR, obstacle.height * ZOOM_FACTOR);
            }
            graphics.setColor(Color.BLUE);
            Vector2 bestPos = null;
            for (final GameCharacter agent : agents) {
                Vector2 pos = agent.getPos();
                if (agent.equals(best))
                    bestPos = pos;
                graphics.fillOval(((int) Math.round(pos.getX() - 2)) * ZOOM_FACTOR, ((int) Math.round(pos.getY() - 2)) * ZOOM_FACTOR, 4 * ZOOM_FACTOR, 4 * ZOOM_FACTOR);
            }
            if (bestPos != null) {
                graphics.setColor(Color.RED);
                graphics.fillOval(((int) Math.round(bestPos.getX() - 2)) * ZOOM_FACTOR, ((int) Math.round(bestPos.getY() - 2)) * ZOOM_FACTOR, 4 * ZOOM_FACTOR, 4 * ZOOM_FACTOR);
            }
            graphics.setColor(Color.GREEN);
            graphics.fillOval(((int) targetPos.getX()) * ZOOM_FACTOR, ((int) targetPos.getY()) * ZOOM_FACTOR, 5 * ZOOM_FACTOR, 5 * ZOOM_FACTOR);
            g.clearRect(0, 0, width, height);
            g.drawImage(image, 0, 0, null);
            repaint();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyThread extends Thread {
        private boolean isVisible = false;

        public MyThread() {

        }

        public void run() {
            MyFrame frame = new MyFrame();
            frame.setSize(MAX_MOVES * ZOOM_FACTOR, MAX_MOVES * ZOOM_FACTOR);
            frame.setUndecorated(true);
            frame.setVisible(true);
            while (true) {
                frame.setVisible(isVisible);
            }
        }

        public void myStart() {
            isVisible = true;
        }

        public void myStop() {
            isVisible = false;
        }
    }
}
