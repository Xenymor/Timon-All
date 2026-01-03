package BWINF44Test.giessroboter.Solvers;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Exakter TSP-Solver mit Choco-solver.
 * Verwendet circuit Constraint für garantiert optimale Hamilton-Kreise.
 * 100% Java - keine nativen Bibliotheken, funktioniert mit Java 24.
 */
public class TSPSolver {

    /**
     * Löst das Traveling Salesman Problem exakt für die gegebenen Punkte.
     * Verwendet Choco-solver mit circuit Constraint.
     *
     * @param points Liste der zu besuchenden Punkte
     * @return Optimale Reihenfolge der Punkte als Rundtour
     */
    public static List<Point> solve(final List<Point> points) {
        if (points == null || points.isEmpty()) {
            return new ArrayList<>();
        }
        if (points.size() == 1) {
            return new ArrayList<>(points);
        }
        if (points.size() == 2) {
            return new ArrayList<>(points);
        }

        int n = points.size();

        // Distanzmatrix berechnen (skaliert auf int für Choco)
        int[][] distanceMatrix = computeDistanceMatrix(points);

        // Choco Model erstellen
        Model model = new Model("TSP");

        // successor[i] = j bedeutet: von Knoten i geht es zu Knoten j
        IntVar[] successor = model.intVarArray("successor", n, 0, n - 1);

        // Circuit Constraint: Garantiert Hamilton-Kreis
        // Alle Knoten werden genau einmal besucht und bilden einen Zyklus
        model.subCircuit(successor, 0, model.intVar(n)).post();

        // Kantenlängen-Variablen für die Zielfunktion
        IntVar[] edgeCosts = new IntVar[n];
        int maxDist = getMaxDistance(distanceMatrix);
        
        for (int i = 0; i < n; i++) {
            // edgeCosts[i] = distanceMatrix[i][successor[i]]
            edgeCosts[i] = model.intVar("cost_" + i, 0, maxDist);
            model.element(edgeCosts[i], distanceMatrix[i], successor[i]).post();
        }

        // Gesamtlänge der Tour
        IntVar totalDistance = model.intVar("totalDistance", 0, n * maxDist);
        model.sum(edgeCosts, "=", totalDistance).post();

        // Zielfunktion: Minimiere Gesamtlänge
        model.setObjective(Model.MINIMIZE, totalDistance);

        // Optimale Lösung finden
        Solution solution = model.getSolver().findOptimalSolution(totalDistance, Model.MINIMIZE);

        if (solution == null) {
            // Fallback: Originale Reihenfolge zurückgeben
            return new ArrayList<>(points);
        }

        // Tour aus der Lösung extrahieren
        return extractTour(points, successor, solution, n);
    }

    /**
     * Berechnet die Distanzmatrix für alle Punktpaare.
     * Skaliert auf int-Werte (Faktor 1000) für Genauigkeit.
     */
    private static int[][] computeDistanceMatrix(List<Point> points) {
        int n = points.size();
        int[][] matrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            Point p1 = points.get(i);
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 0;
                } else {
                    // Euklidische Distanz, skaliert um Präzision zu erhalten
                    double dist = p1.distance(points.get(j));
                    matrix[i][j] = (int) (dist * 1000);
                }
            }
        }
        return matrix;
    }

    /**
     * Findet die maximale Distanz in der Matrix.
     */
    private static int getMaxDistance(int[][] matrix) {
        int max = 0;
        for (int[] row : matrix) {
            for (int val : row) {
                if (val > max) {
                    max = val;
                }
            }
        }
        return max;
    }

    /**
     * Extrahiert die optimale Tour aus der Choco Lösung.
     */
    private static List<Point> extractTour(List<Point> points, IntVar[] successor,
                                           Solution solution, int n) {
        List<Point> tour = new ArrayList<>(n);

        // Starte bei Knoten 0 und folge den Successors
        int current = 0;
        for (int step = 0; step < n; step++) {
            tour.add(points.get(current));
            current = solution.getIntVal(successor[current]);
        }

        return tour;
    }
}
