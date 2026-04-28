package DiceSimulation;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Scanner;

public class DiceSimulation {

    public static final int TRIAL_COUNT = 10_000_000;

    static void main() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the first dice configuration (e.g.: 1d20'.+2d6,)");
        RollConfig config1 = new RollConfig(scanner.nextLine());

        System.out.println("Enter the second dice configuration (e.g.: 1d20'.+2d6,)");
        RollConfig config2 = new RollConfig(scanner.nextLine());

        Map<Integer, Integer> distribution1 = config1.simulate(TRIAL_COUNT);
        Map<Integer, Integer> distribution2 = config2.simulate(TRIAL_COUNT);

        int min = Math.min(
            distribution1.keySet().stream().min(Integer::compareTo).orElse(0),
            distribution2.keySet().stream().min(Integer::compareTo).orElse(0)
        );
        int max = Math.max(
            distribution1.keySet().stream().max(Integer::compareTo).orElse(0),
            distribution2.keySet().stream().max(Integer::compareTo).orElse(0)
        );
        int globalMaxCount = Math.max(
            distribution1.values().stream().mapToInt(Integer::intValue).max().orElse(1),
            distribution2.values().stream().mapToInt(Integer::intValue).max().orElse(1)
        );

        JFrame frame = new JFrame("Dice Distribution");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 1));

        frame.add(new BarChartPanel(distribution1, "Konfiguration 1", min, max, globalMaxCount, TRIAL_COUNT));
        frame.add(new BarChartPanel(distribution2, "Konfiguration 2", min, max, globalMaxCount, TRIAL_COUNT));

        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
