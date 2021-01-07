package CircleTSP.algo.solvers;

import CircleTSP.algo.cluster.DBSCAN;
import CircleTSP.algo.cluster.PCA;
import CircleTSP.algo.estimators.EntrypointHeuristic;
import CircleTSP.algo.estimators.IntersectingEdges;
import CircleTSP.algo.path.LinearPath;
import CircleTSP.entities.*;
import CircleTSP.util.Distance;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;

public class PathCircleTSP extends TSPClusterSolver {

    // private static final double DELTA = 0.382;
    // private static final double PATH_LIMIT = 128;

    /** Enhancement of the ClusteredCircleTSP algorithm.
     * Similar to ClusteredCircleTSP this algorithm uses DBSCAN to find clusters but distinguishes between two
     * types of clusters to use different algorithms to find sub tours.
     * Both CircleTSP and LinearPath will be executed on a cluster and the tour with the
     * lower costs will be used as local tour for this cluster.
     * @param pointSet Set of points to calculate a tour from.
     * @param minPts DBSCAN parameter, minimum number of points in an
     *               epsilon-neighborhood to consider a point a core point.
     * @param epsilon DBSCAN parameter, maximum distance in which a point is
     *                considered a neighbor to another point.
     * @return A tour containing all points from pointSet.
     */
    public Tour calculateTour(Collection<Point> pointSet,
                                     int minPts, double epsilon) {
        return calculateTour(pointSet, minPts, epsilon, -1);
    }

    /** Enhancement of the ClusteredCircleTSP algorithm.
     * Similar to ClusteredCircleTSP this algorithm uses DBSCAN to find clusters but distinguishes between two
     * types of clusters to use different algorithms to find sub tours.
     * If a cluster corresponds to a circular shape CircleTSP will be used to find sub tours and if the cluster
     * corresponds to a flat shape a path finding algorithm (AllStar/LinearPath) will be
     * used.
     * To distinguish the clusters, principal component analysis (PCA) is used and the eigenvalues of the first
     * two principal components are compared.
     * If their ratio is below a value delta, the cluster is considered flat.
     * If delta is set below 0, both CircleTSP and LinearPath will be executed on a cluster and the tour with the
     * lower costs will be used as local tour for this cluster.
     * @param pointSet Set of points to calculate a tour from.
     * @param minPts DBSCAN parameter, minimum number of points in an
     *               epsilon-neighborhood to consider a point a core point.
     * @param epsilon DBSCAN parameter, maximum distance in which a point is
     *                considered a neighbor to another point.
     * @param delta Describes the threshold for the ratio between the eigenvalues of a cluster below which a cluster is
     *              considered to be flat.
     * @return A tour containing all points from pointSet.
     */
    public Tour calculateTour(Collection<Point> pointSet,
                              int minPts, double epsilon, double delta) {

        // Create names for cluster centers
        Deque<String> clusterCenterNames = new ArrayDeque<>();
        for (char t = 'a'; t <= 'z'; t++)
            clusterCenterNames.add(""+t);
        for (char t1 = 'a'; t1 <= 'z'; t1++) {
            for (char t2 = 'a'; t2 <= 'z'; t2++) {
                clusterCenterNames.add("" + t1 + t2);
            }
        }

        DBSCAN clusterer = new DBSCAN(pointSet, minPts, epsilon);
        List<Cluster> clusters = clusterer.getClusters();
        clusters.removeIf(cluster -> cluster.getPoints().size() <= 2);

        List<Tour> clusterTours = new ArrayList<>();
        List<Point> centerPoints = new ArrayList<>();
        List<Tuple<Point, Point>> entryPoints = new ArrayList<>();
        // TODO: Find goal points
        // List<Tuple<Point, Point>> goalPoints = new ArrayList<>();
        Set<Point> clusterCentersAndNoise = new HashSet<>(Set.copyOf(pointSet));

        for (Cluster cluster : clusters) {
            Collection<Point> clusterPoints = cluster.getPoints();

            Tour candidateTour;
            Tuple<Point, Point> localEntryPoints;

            // Calculate two tours with CircleTSP and LinearPath if delta is smaller than 0
            if (delta < 0) {
                // Calculate tour with LinearPath
                List<Point> path = LinearPath.findPath(clusterPoints);
                Tour linearTour = new Tour(path);
                double linearPathLength = Distance.calculatePathLength(path);

                // Calculate tour with CircleTSP
                Tour circleTour = CircleTSP.calculateTour(clusterPoints);
                double circleTourLength = Distance.calculateTourLength(circleTour);

                // Use subtour with lower costs
                if (linearPathLength < circleTourLength) {
                    candidateTour = linearTour;
                    Point e1 = path.get(0);
                    Point e2 = path.get(path.size()-1);
                    localEntryPoints = new Tuple<>(e1, e2);
                } else {
                    candidateTour = circleTour;
                    // Entry points have to be calculated later using an entry point heuristic
                    localEntryPoints = null;
                }
            }
            else {
                PCA pca = new PCA(clusterPoints);
                if (isFlat(pca, delta)) {
                    // Call LinearPath for flat cluster

                    RealVector pc = pca.getEigenvector(0);
                    List<Point> path = LinearPath.findPath(clusterPoints, pc);
                    candidateTour = new Tour(path);
                    Point e1 = path.get(0);
                    Point e2 = path.get(path.size()-1);
                    localEntryPoints = new Tuple<>(e1, e2);
                }
                else {
                    candidateTour = CircleTSP.calculateTour(clusterPoints);
                    // Entry points have to be calculated later using an entry point heuristic
                    localEntryPoints = null;
                }
            }

            // Add better tour to localTours (clusterTours) and add fitting entrypoints
            clusterTours.add(candidateTour);
            Point centerPoint = new Point(clusterCenterNames.removeFirst(),
                    CircleTSP.getCenterPoint(clusterPoints).getCoordinates());
            centerPoints.add(centerPoint);

            entryPoints.add(localEntryPoints);

            // Remove points of sub tour from V' and add cluster center
            clusterCentersAndNoise.removeAll(clusterPoints);
            clusterCentersAndNoise.add(centerPoint);
        }

        // Calculate global tour from noise points and cluster centers (V')
        Tour globalTour = CircleTSP.calculateTour(clusterCentersAndNoise);

        // Merge local cluster tours with global tour using the IntersectingEdges heuristic
        EntrypointHeuristic heuristic = new IntersectingEdges();
        return ClusteredCircleTSP.mergeTours(globalTour, CircleTSP.getCenterPoint(clusterCentersAndNoise),
                clusterTours, centerPoints, entryPoints, heuristic);
    }

    /**
     * Checks if a cluster is flat by comparing the first two eigenvalues of the PCA of a cluster.
     * If the eigenvalues are close to each other, the variances of the points in the cluster are similar.
     * This also means that the first two principal components have similar length, which can correspond to a cluster
     * that is more of circular than of flat nature.
     * If the ratio between the two eigenvalues is below a threshold delta (if they are less similar than delta),
     * the cluster is considered flat.
     * This approach can not distinguish between clusters that have a circular outline but have a more dense distribution
     * of points along an axis and clusters that have a longer, flatter outline.
     * @param pca Results of the principal component analysis of a cluster to be classified as flat.
     * @param delta Describes the threshold for the ratio between the eigenvalues of a cluster below which a cluster is
     *              considered to be flat.
     * @return True if ratio of the first two eigenvalues is below delta.
     */
    private static boolean isFlat(PCA pca, double delta) {
        double e1 = pca.getEigenvalue(0);
        double e2 = pca.getEigenvalue(1);
        // This ratio describes how close the ellipse spanned by the two first principal components is to
        // a perfect circle
        double ratio = Math.max(0, Math.abs(Math.min(e1,e2) / Math.max(e1,e2)));

        return ratio < delta;
    }
}
