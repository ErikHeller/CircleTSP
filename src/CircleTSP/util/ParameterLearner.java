package CircleTSP.util;

import CircleTSP.algo.DBSCAN;
import CircleTSP.algo.TSPClusterSolver;
import CircleTSP.entities.Cluster;
import CircleTSP.entities.Point;
import CircleTSP.entities.Tour;

import java.util.*;

public class ParameterLearner {

    /**
     * Learns the epsilon parameter required by a TSPClusterSolver for a collection of points,
     * by setting minPts to a fixed value and performing a local search to find the best epsilon value.
     * The learner iterates stepwise through epsilon values starting from 0 and calculates the cost
     * of this value for each step by calculating a solution using the TSPClusterSolver.
     * It terminates if the costs for the stepwise solutions have risen or stayed equal 5 times consecutively
     * and the epsilon with the minimum calculated cost will be returned.
     *
     * This function calculates maxEpsilon and stepSize automatically, by determining the maximum distance two point can
     * have in the provided point collection. This value will be used for maxEpsilon while maxEpsilon/100 will be used
     * as stepSize.
     * @param points Collection of point to perform the learning on.
     * @param minPts Specifies the minPts value for the solver.
     * @param solver Solver used for calculating the cost of a parameter value.
     * @return Epsilon value with a local minimum cost for solver.
     */
    public static double learnEpsilon2(final Collection<Point> points, final int minPts,
                                       final TSPClusterSolver solver) {
        double maxDistance = 0.0;
        for (Point p1 : points) {
            for (Point p2 : points) {
                maxDistance = Math.max(maxDistance, Distance.euclidianDistance(p1, p2));
            }
        }

        return learnEpsilon2(points, minPts, maxDistance, maxDistance/100, solver);
    }

    /**
     * Learns the epsilon parameter required by a TSPClusterSolver for a collection of points,
     * by setting minPts to a fixed value and performing a local search to find the best epsilon value.
     * The learner iterates stepwise through epsilon values starting from 0 and calculates the cost
     * of this value for each step by calculating a solution using the TSPClusterSolver.
     * It terminates if the costs for the stepwise solutions have risen or stayed equal 5 times consecutively
     * and the epsilon with the minimum calculated cost will be returned.
     * @param points Collection of point to perform the learning on.
     * @param minPts Specifies the minPts value for the solver.
     * @param maxEpsilon Maximum value epsilon is allowed to have.
     * @param stepSize Specifies the value by which epsilon gets increased each learning iteration step.
     * @param solver Solver used for calculating the cost of a parameter value.
     * @return Epsilon value with a local minimum cost for solver.
     */
    public static double learnEpsilon2(final Collection<Point> points, final int minPts,
                                       final double maxEpsilon, final double stepSize,
                                       final TSPClusterSolver solver) {
        double result = 0.0;
        double epsilon = 0.0;
        double minDistance = Double.POSITIVE_INFINITY;
        double previousDistance = Double.POSITIVE_INFINITY;
        int rising = 0;
        while (true) {
            epsilon += stepSize;
            if (epsilon >= maxEpsilon)
                break;

            // Calculate costs for tour by directly calculating a tour using the desired solver
            Tour tour_cluster;
            try {
                tour_cluster = solver.calculateTour(points, minPts, epsilon);
            } catch (NullPointerException e) {
                // TODO: WARNING: This is a clue for a deeper underlying problem!
                continue;
            }
            double currentDistance = Distance.calculateTourLength(tour_cluster);

            // Increase counter if the cost of the solution has increased consecutively.
            // Reset counter if costs have dropped.
            if (currentDistance >= previousDistance)
                rising++;
            else
                rising = 0;

            // Stopping condition - stop if costs have increased 5 consecutive times
            if (rising >= 5)
                break;

            // If costs are below previous minimum, update epsilon and minimum costs value
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                result = epsilon;
            }
            previousDistance = currentDistance;
        }
        return result;
    }

    public static double learnEpsilon(final Collection<Point> points, final int minPts) {
        double delta = 40.0;
        double epsilon = 0.0;
        double maxClusters = 0;
        while(delta > 5) {
            delta -= 1;
            double e = learnEpsilon(points, minPts, 0.9, delta, true);

            DBSCAN clusterer = new DBSCAN(points, minPts, e);
            List<Cluster> clusters = clusterer.getClusters();

            if (clusters.size() > maxClusters) {
                epsilon = e;
            }
        }
        // System.out.println("Delta: " + delta);
        return epsilon;
    }

    public static double learnEpsilon(final Collection<Point> points, final int minPts,
                               final double alpha, final double delta, final boolean relative) {

        // Calculate distances between each point and sort them in ascending order
        TreeSet<Double> distances = new TreeSet<>();
        List<Point> pointList = new ArrayList<>(points);
        for (int i = 0; i < pointList.size(); i++) {
            for (int j = i+1; j < pointList.size(); j++) {
                distances.add(Distance.euclidianDistance(pointList.get(i), pointList.get(j)));
            }
        }

        // If predicted distance differs from distance more than a specified error threshold delta,
        // use the maximum predicted distance as epsilon
        double epsilon = 0.0;
        int n = distances.size();

        Double[] y = new Double[n];     // Ordered distances
        Double[] y_ = new Double[n];    // Predicted distances
        Double[] error = new Double[n];
        y = distances.toArray(y);

        // Initialization
        y_[0] = y[0];

        // Single exponential smoothing
        int breakpoint = 0;
        for (int t = 0; t < n-1; t++) {
            error[t] = y_[t] - y[t];
            // Calculate first minPts points without checking error

            if (t > minPts) {
                // If error exceeds threshold delta, pick the predicted point as epsilon
                boolean exceeded;
                if (relative) {
                    double step_ratio = Math.abs(error[t]/error[t-1]);
                    exceeded = step_ratio > delta;  // Delta as a ratio threshold between current and previous error
                }
                else
                    exceeded = Math.abs(error[t]) > delta;    // Delta as an absolute error threshold
                if (exceeded) {
                    epsilon = y_[t];
                    breakpoint = t;
                    break;
                }
            }
            y_[t+1] = y_[t] - alpha*(error[t]);
        }
        /*
        System.out.print("Distances:  ");
        printList(y);
        System.out.print("\n");
        System.out.print("Predictions:");
        printList(y_);
        System.out.print("\n");
        System.out.print("Errors:     ");
        printList(error);
        System.out.print("\n");
        System.out.println("Breakpoint: " + breakpoint);
         */
        return epsilon;
    }

    private static void printList(Double[] entries) {
        for (Double entry : entries)
            if (entry != null)
                System.out.print(" " + Math.round(entry*1000)/1000.0);
    }

}
