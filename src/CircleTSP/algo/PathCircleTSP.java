package CircleTSP.algo;

import CircleTSP.entities.*;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.*;

public class PathCircleTSP implements TSPClusterSolver {

    // TODO: Find better value or make variable
    private static final double DELTA = 0.382;
    // private static final double PATH_LIMIT = 128;

    /** Enhancement of the ClusteredCircleTSP algorithm.
     * Similar to ClusteredCIrcleTSP this algorithm uses DBSCAN to find clusters but distinguishes between two
     * types of clusters to use different algorithms to find sub tours.
     * If a cluster corresponds to a circular shape CircleTSP will be used to find sub tours and if the cluster
     * corresponds to a longer, thinner, more elliptical shape a path finding algorithm (AllStar/LinearPath) will be
     * used.
     * To distinguish the clusters, principal component analysis (PCA) is used by comparing the eigenvalues of the first
     * two principal components.
     * If their ratio is below a constant value DELTA, the cluster is considered elliptical.
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

        DBSCAN clusterer = new DBSCAN(pointSet, minPts, epsilon);
        List<Cluster> clusters = clusterer.getClusters();
        clusters.removeIf(cluster -> cluster.getPoints().size() <= 2);

        List<Tour> clusterTours = new ArrayList<>();
        List<Point> centerPoints = new ArrayList<>();
        List<Tuple<Point, Point>> entryPoints = new ArrayList<>();
        // List<Tuple<Point, Point>> goalPoints = new ArrayList<>();
        Set<Point> clusterCentersAndNoise = new HashSet<>(Set.copyOf(pointSet));

        for (Cluster cluster : clusters) {
            Collection<Point> clusterPoints = cluster.getPoints();
            PCA pca = new PCA(clusterPoints);
            boolean ellipsoid = isEllipsoid(pca);

            if (ellipsoid) {
                // Call LinearPath for ellipsoid clusters
                RealVector pc = pca.getEigenvector(0);
                // TODO: Find goal points
                List<Point> path = LinearPath.findPath(clusterPoints, pc);
                clusterTours.add(new Tour(path));
                Point e1 = path.get(0);
                Point e2 = path.get(path.size()-1);
                Point centerPoint = new Point(clusterCenterNames.removeFirst(),
                        CircleTSP.getCenterPoint(clusterPoints).getCoordinates());

                centerPoints.add(centerPoint);
                entryPoints.add(new Tuple<>(e1, e2));

                clusterCentersAndNoise.removeAll(clusterPoints);
                clusterCentersAndNoise.add(centerPoint);
            }
            else {
                // Call CircleTSP for big & circular clusters
                clusterTours.add(CircleTSP.calculateTour(clusterPoints));
                Point centerPoint = new Point(clusterCenterNames.removeFirst(),
                        CircleTSP.getCenterPoint(clusterPoints).getCoordinates());
                centerPoints.add(centerPoint);
                entryPoints.add(null);

                clusterCentersAndNoise.removeAll(clusterPoints);
                clusterCentersAndNoise.add(centerPoint);
            }
        }

        Tour globalTour = CircleTSP.calculateTour(clusterCentersAndNoise);

        return ClusteredCircleTSP.mergeTours(globalTour, CircleTSP.getCenterPoint(clusterCentersAndNoise),
                clusterTours, centerPoints, entryPoints);
    }

    /**
     * Find entry points for AllStar path search using PCA. For an elliptical cluster, the first principal component
     * will be extracted and all points will be projected onto this principal component. The values of the projected
     * points will be sorted in ascending order and the first and last points will be extracted as entry points,
     * as well as start and end points, for AllStar.
     * @param points Collection of points (cluster) for which the entry points shall be determined.
     * @param pc First prinicpal component of the PCA for a cluster.
     * @return Entry points for a cluster.
     */
    static Tuple<Point, Point> findEntryPoints(Collection<Point> points, RealVector pc) {
        // Red-Black tree as implemented in TreeMap is more efficient than skip list
        SortedMap<Double, Point> projectionMap = new TreeMap<>();

        // Project all points from cluster to the first principal component
        for (Point p : points) {
            RealVector v = new ArrayRealVector(p.getCoordinates());
            double projection = PCA.getProjection(v, pc);
            projectionMap.put(projection, p);
        }

        Point start = projectionMap.get(projectionMap.firstKey());
        Point goal = projectionMap.get(projectionMap.lastKey());

        assert(points.contains(start));
        assert(points.contains(goal));

        return new Tuple<>(start, goal);
    }

    /**
     * Checks if a cluster is ellipsoid by comparing the first two eigenvalues of the PCA of a cluster.
     * If the eigenvalues are close to each other, the variances of the points in the cluster are similar.
     * This also means that the first two principal components have similar length, which can correspond to a cluster
     * that is more of circular than of elliptical nature.
     * If the ratio between the two eigenvalues is below a threshold DELTA (if they are less similar than DELTA),
     * the cluster is considered elliptical.
     * This approach can not distinguish between clusters that have a circular outline but have a more dense distribution
     * of points along an axis and clusters that have an elliptical outline.
     * @param pca Results of the principal component analysis of a cluster to be classified as elliptical.
     * @return True if ratio of the first two eigenvalues is below DELTA.
     */
    private static boolean isEllipsoid(PCA pca) {
        double e1 = pca.getEigenvalue(0);
        double e2 = pca.getEigenvalue(1);
        // This ratio describes how close the ellipse spanned by the two first principal components is to
        // a perfect circle
        double ratio = Math.max(0, Math.abs(Math.min(e1,e2) / Math.max(e1,e2)));

        return ratio < DELTA;
    }
}
