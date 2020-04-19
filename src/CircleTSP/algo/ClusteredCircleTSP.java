package CircleTSP.algo;

import CircleTSP.entities.*;

import java.util.*;

public class ClusteredCircleTSP extends TSPClusterSolver {

    /** Enhancement of the CircleTSP algorithm using clustering to find sub tours.
     * This algorithm uses DBSCAN to find clusters in a set of points to find
     * sub tours that shall increase the quality of the solution compared to
     * using CircleTSP naively.
     * It creates a CircleTSP instance for each cluster, calculates their
     * tours, calculates a global tour, which connects the clusters by their centers
     * and includes unclassified noise points, and merges the cluster tours with
     * the global tour to a resulting tour.
     * @param pointSet Set of points to calculate a tour from.
     * @param minPts DBSCAN parameter, minimum number of points in an
     *               epsilon-neighborhood to consider a point a core point.
     * @param epsilon DBSCAN parameter, maximum distance in which a point is
     *                considered a neighbor to another point.
     * @return A tour containing all points from pointSet.
     */
    public Tour calculateTour(Collection<Point> pointSet,
                                     int minPts, double epsilon) {

        // Create names for cluster centers
        Deque<String> clusterCenterNames = new ArrayDeque<>();
        for (char t = 'a'; t <= 'z'; t++)
            clusterCenterNames.add(""+t);
        for (char t1 = 'a'; t1 <= 'z'; t1++) {
            for (char t2 = 'a'; t2 <= 'z'; t2++) {
                clusterCenterNames.add("" + t1 + t2);
            }
        }

        // 1. Find clusters
        DBSCAN dbscan = new DBSCAN(pointSet, minPts, epsilon);
        List<Cluster> clusters = dbscan.getClusters();

        // 1.1 Filter clusters
        clusters.removeIf(cluster -> cluster.getPoints().size() <= 2);


        List<Tour> clusterTours = new ArrayList<>();
        List<Point> clusterCenters = new ArrayList<>();
        Set<Point> clusterCentersAndNoise = new HashSet<>(Set.copyOf(pointSet));
        // For each cluster...
        for (Cluster cluster : clusters) {
            // 2. Calculate sub tours
            Collection<Point> clusterPoints = cluster.getPoints();
            Tour clusterTour = CircleTSP.calculateTour(clusterPoints);
            clusterTours.add(clusterTour);

            // 3. Replace points of cluster in pointSet with center point of cluster
            Point clusterCenter = new Point(clusterCenterNames.removeFirst(),
                    CircleTSP.getCenterPoint(clusterPoints).getCoordinates());
            clusterCenters.add(clusterCenter);

            clusterCentersAndNoise.removeAll(clusterPoints);
            clusterCentersAndNoise.add(clusterCenter);
        }

        // 4. Calculate global tour through noise points and cluster centers
        Tour globalTour = CircleTSP.calculateTour(clusterCentersAndNoise);

        // 5. Merge local cluster tours with global tour using the IntersectingEdges heuristic
        EntrypointHeuristic heuristic = new IntersectingEdges();
        return mergeTours(globalTour, CircleTSP.getCenterPoint(clusterCentersAndNoise),
                clusterTours, clusterCenters, null, heuristic);
    }
}
