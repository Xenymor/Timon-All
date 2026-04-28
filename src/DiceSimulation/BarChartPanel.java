package DiceSimulation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarChartPanel extends JPanel {

    private final Map<Integer, Integer> distribution;
    private final String title;
    private final int globalMin;
    private final int globalMax;
    private final int globalMaxCount;
    private final int trialCount;
    private static final int PADDING = 50;
    private static final Color BAR_COLOR = new Color(70, 130, 180);

    public BarChartPanel(Map<Integer, Integer> distribution, String title, int globalMin, int globalMax, int globalMaxCount, int trialCount) {
        this.distribution = new HashMap<>(distribution);
        this.title = title;
        this.globalMin = globalMin;
        this.globalMax = globalMax;
        this.globalMaxCount = globalMaxCount;
        this.trialCount = trialCount;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (distribution.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int chartWidth = width - 2 * PADDING;
        int chartHeight = height - 2 * PADDING - 20;

        int globalRange = globalMax - globalMin + 1;
        int barWidth = Math.max(1, chartWidth / globalRange - 2);

        // Title
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (width - fm.stringWidth(title)) / 2, 20);

        // Statistics
        double expectedValue = calculateExpectedValue();
        double median = calculateMedian();
        double stdDev = calculateStandardDeviation();
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        String statsText = String.format("E(X) = %.2f  |  Median = %.2f  |  σ = %.2f", expectedValue, median, stdDev);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(statsText, (width - g2.getFontMetrics().stringWidth(statsText)) / 2, 38);

        // Axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        int chartTop = PADDING + 20;
        g2.drawLine(PADDING, chartTop, PADDING, chartTop + chartHeight);
        g2.drawLine(PADDING, chartTop + chartHeight, PADDING + chartWidth, chartTop + chartHeight);

        // Bars positioned based on global scale
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        for (Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
            int value = entry.getKey();
            int count = entry.getValue();

            int barHeight = (int) ((double) count / globalMaxCount * chartHeight);
            int slotIndex = value - globalMin;
            int x = PADDING + slotIndex * (chartWidth / globalRange) + (chartWidth / globalRange - barWidth) / 2;
            int y = chartTop + chartHeight - barHeight;

            g2.setColor(BAR_COLOR);
            g2.fillRect(x, y, barWidth, barHeight);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, barWidth, barHeight);
        }

        // X-axis labels based on global range
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        int labelStep = Math.max(1, globalRange / 20);
        for (int val = globalMin; val <= globalMax; val++) {
            int slotIndex = val - globalMin;
            if (slotIndex % labelStep == 0 || val == globalMin || val == globalMax) {
                int x = PADDING + slotIndex * (chartWidth / globalRange) + (chartWidth / globalRange) / 2;
                g2.setColor(Color.BLACK);
                String label = String.valueOf(val);
                int labelX = x - g2.getFontMetrics().stringWidth(label) / 2;
                g2.drawString(label, labelX, chartTop + chartHeight + 15);
            }
        }

        // Y-axis labels
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        double maxPercent = (double) globalMaxCount / trialCount * 100;
        g2.drawString(String.format("%.1f%%", maxPercent), 5, chartTop + 5);
        g2.drawString(String.format("%.1f%%", maxPercent / 2), 5, chartTop + chartHeight / 2);
        g2.drawString("0%", 15, chartTop + chartHeight);
    }

    private double calculateExpectedValue() {
        long totalCount = 0;
        long weightedSum = 0;
        for (Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
            totalCount += entry.getValue();
            weightedSum += (long) entry.getKey() * entry.getValue();
        }
        return totalCount == 0 ? 0 : (double) weightedSum / totalCount;
    }

    private double calculateMedian() {
        List<Integer> sortedValues = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                sortedValues.add(entry.getKey());
            }
        }
        if (sortedValues.isEmpty()) return 0;
        Collections.sort(sortedValues);
        int size = sortedValues.size();
        if (size % 2 == 0) {
            return (sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2.0;
        } else {
            return sortedValues.get(size / 2);
        }
    }

    private double calculateStandardDeviation() {
        double expectedValue = calculateExpectedValue();
        long totalCount = 0;
        double sumSquaredDiff = 0;
        for (Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
            int value = entry.getKey();
            int count = entry.getValue();
            totalCount += count;
            sumSquaredDiff += Math.pow(value - expectedValue, 2) * count;
        }
        return totalCount == 0 ? 0 : Math.sqrt(sumSquaredDiff / totalCount);
    }
}