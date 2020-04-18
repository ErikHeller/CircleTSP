package CircleTSP;

import CircleTSP.algo.*;
import CircleTSP.benchmark.ProblemGenerator;
import CircleTSP.entities.Cluster;
import CircleTSP.entities.Point;
import CircleTSP.entities.TSPClusterSolver;
import CircleTSP.entities.Tour;
import CircleTSP.gui.Display;
import CircleTSP.gui.GraphDraw;
import CircleTSP.util.Distance;
import CircleTSP.util.ParameterLearner;
import CircleTSP.util.TSPLIB;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void devMode(String[] args) throws IOException {
        // Read points from file
        String pointsFile = "points.txt";
        Tour optimalTour = null;

        if (args.length > 0)
            pointsFile = args[0];
        HashMap<String, Point> pointMap = TSPLIB.readPoints(pointsFile);
        if (args.length == 2) {
            optimalTour = TSPLIB.readOpt(args[1], pointMap);
        } else if (args.length > 2)
            throw new IllegalArgumentException("Invalid number of arguments!");

        Set<Point> pointSet = new HashSet<>(pointMap.values());

        // CircleTSP
        System.out.println("\n=== CircleTSP ===\n");
        long time1 = System.currentTimeMillis();
        Tour tour = CircleTSP.calculateTour(pointSet);
        long time2 = System.currentTimeMillis();
        double timeUsed = time2 - time1;
        double tourLength = Distance.calculateTourLength(tour);
        Display.displayResults(tour, CircleTSP.getCenterPoint(pointSet), tourLength, timeUsed, false);

        // Branch and Bound TSP
        double timeUsedOpt = 0;
        double approxRatio = 0;
        if (pointSet.size() < 20 && optimalTour == null) {
            long time1_opt = System.currentTimeMillis();
            optimalTour = BnBTSP.Run(new ArrayList<>(pointSet));
            long time2_opt = System.currentTimeMillis();
            timeUsedOpt = time2_opt - time1_opt;
        }

        // CircleTSP with clusters
        System.out.println("\n=== CircleTSP with clusters ===\n");
        long time1_cluster = System.currentTimeMillis();
        ClusteredCircleTSP cluster_solver = new ClusteredCircleTSP();
        Tour tour_cluster = cluster_solver.calculateTour(pointSet, 4, 0.25);
        long time2_cluster = System.currentTimeMillis();
        double timeUsed_cluster = time2_cluster - time1_cluster;
        double tourLength_cluster = Distance.calculateTourLength(tour_cluster);
        Display.displayResults(tour_cluster, CircleTSP.getCenterPoint(pointSet), tourLength_cluster, timeUsed_cluster, false);

        // CircleTSP with clusters and paths
        System.out.println("\n=== CircleTSP with clusters and paths ===\n");
        long time1_path = System.currentTimeMillis();
        PathCircleTSP path_solver = new PathCircleTSP();
        Tour tour_path = path_solver.calculateTour(pointSet, 4, 0.25);
        long time2_path = System.currentTimeMillis();
        double timeUsed_path = time2_path - time1_path;
        double tourLength_path = Distance.calculateTourLength(tour_path);
        Display.displayResults(tour_path, CircleTSP.getCenterPoint(pointSet), tourLength_path, timeUsed_path, true);

        if (optimalTour != null) {
            System.out.println("\n=== Optimal Tour ===\n");
            double optimalLength = Distance.calculateTourLength(optimalTour);
            Display.displayResults(optimalTour, new Point("center", new double[] {0,0}),
                    optimalLength, timeUsedOpt, false);

            approxRatio = ((double)Math.round((tourLength/optimalLength)*1000))/1000;
            double approxRatio2 = ((double)Math.round((tourLength_cluster/optimalLength)*1000))/1000;
            double approxRatio3 = ((double)Math.round((tourLength_path/optimalLength)*1000))/1000;
            System.out.println("\n=== CircleTSP approximation ratio: " + approxRatio);
            System.out.println("\n=== ClusteredCircleTSP approximation ratio: " + approxRatio2);
            System.out.println("\n=== PathCircleTSP approximation ratio: " + approxRatio3);
            if (approxRatio == 1)
                System.out.println("The solution of CircleTSP is optimal for this instance!");
        }
    }

    public static void main(String[] args) throws IOException {
        // devMode(args);

        Collection<Point> points = ProblemGenerator.fourCircleClusters(80);
        TSPClusterSolver solver = new ClusteredCircleTSP();

        if (args.length > 0) {
            String pointsFile = args[0];
            points = TSPLIB.readPoints(pointsFile).values();
        }

        double epsilon = ParameterLearner.learnEpsilon2(points, 4, solver);
        System.out.println("minPts: 4, epsilon: " + epsilon);
        DBSCAN clusterer = new DBSCAN(points, 4, epsilon);
        List<Cluster> clusters = clusterer.getClusters();
        System.out.println("Number of clusters: " + clusters.size());

        double time1 = System.nanoTime();
        Tour tour_cluster = solver.calculateTour(points, 4, epsilon);
        double time2 = System.nanoTime();


        Display.displayResults(tour_cluster,
                CircleTSP.getCenterPoint(points),
                Distance.calculateTourLength(tour_cluster),
                (time2 - time1) / 1000000.0,
                true);
    }
}
