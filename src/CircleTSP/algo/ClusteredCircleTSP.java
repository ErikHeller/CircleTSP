package CircleTSP.algo;

import CircleTSP.entities.*;
import CircleTSP.util.*;

import java.util.*;

public class ClusteredCircleTSP implements TSPClusterSolver {

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

        Deque<String> clusterCenterNames = new ArrayDeque<>(
                Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));

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

        // 5. Merge local cluster tours with global tour
        return mergeTours(globalTour, CircleTSP.getCenterPoint(clusterCentersAndNoise),
                clusterTours, clusterCenters, null);
    }

    /**
     * Merges a global tour that consists of noise points and center points of
     * clusters with local cluster tours removing all cluster centers from the
     * resulting tour.
     * This function is being used to combine all found tours to one tour
     * after clusters in a point set have been found and their respective
     * sub tours have been calculated as well as a global tour connecting
     * those clusters has been found while including points that have not been
     * classified in clusters.
     * This method uses the intersecting edges heuristic.
     * Following this heuristic, the method checks for each cluster if an edge
     * exists in the cluster tour that intersects an auxiliary edge between
     * globalCenter and the center of this cluster and uses the edge closest to
     * globalCenter as an entry point to merge the cluster tour into the global
     * tour.
     * @param globalTour Global tour connecting clusters and noise points.
     * @param globalCenter Center point of the global tour.
     * @param clusterTours Local tours generated from cluster sets.
     * @param clusterCenters Center points of their respective cluster tours.
     * @param entryPoints Pre-define entry points for each cluster.
     * @return A tour containing all clusterTours merged on their centers
     * with the globalTour.
     */
    static Tour mergeTours(Tour globalTour, Point globalCenter,
                                   List<Tour> clusterTours, List<Point> clusterCenters,
                                   List<Tuple<Point, Point>> entryPoints) {

        if (clusterTours.size() != clusterCenters.size())
            throw new IllegalArgumentException("The number of clusters is not consistent between clusterTours" +
                    "and clusterCenters!");

        if (globalTour.size() == 0) {
            if (clusterTours.size() == 1)
                return clusterTours.get(0);
            else
                throw new IllegalArgumentException("Global tour is empty while there are more than one cluster tours!");
        }

        // TODO: use logging
        System.out.println("Global tour:");
        System.out.println(globalTour.toString());

        for (int i = 0; i < clusterTours.size(); i++) {
            // a) Find goal points g1 and g2
            Point clusterCenter = clusterCenters.get(i);
            Tour clusterTour = clusterTours.get(i);
            // Index of clusterCenter in the global globalTour
            int centerIndex = globalTour.indexOf(clusterCenter);

            // TODO: use logging
            System.out.println("=== Cluster " + (i+1) + " of " + clusterTours.size() + " ===");
            System.out.println("Current centerpoint: " + clusterCenter.getId());
            System.out.println("Local tour:");
            System.out.println(clusterTour.toString());

            Point g1 = globalTour.getPreviousPoint(centerIndex);
            Point g2 = globalTour.getNextPoint(centerIndex);

            // TODO: use logging
            System.out.println("Goal points: " + g1.getId() + " " + g2.getId());

            Tuple<Point, Point> currentEntryPoints = null;
            if (entryPoints != null)
                currentEntryPoints = entryPoints.get(i);

            Point e1, e2;
            if (currentEntryPoints == null) {
                // b) Create auxiliary edge between globalCenter and clusterCenter
                Edge ec = new Edge(globalCenter, clusterCenter);

                // c) Find edge in clusterTour that intersects with ec that is closest to the globalCenter
                // d) Get entry points e1, e2 of that clusterTour from the intersecting edge
                Edge intersectingEdge = findIntersectingEdge(ec, clusterTour, globalCenter);
                e1 = intersectingEdge.getFirst();
                e2 = intersectingEdge.getSecond();
            } else {
                // Extract given entryPoints
                e1 = currentEntryPoints.getFirst();
                e2 = currentEntryPoints.getSecond();
            }

            // Check if (g1,e1),(g2,e2) or (g1,e2),(g2,e1) is shorter & rename so that (g1,e1),(g2,e2) is shortest
            double length1 = Distance.euclidianDistance(e1, g1) + Distance.euclidianDistance(e2, g2);
            double length2 = Distance.euclidianDistance(e1, g2) + Distance.euclidianDistance(e1, g2);
            if (length2 < length1) {
                Point temp = e1;
                e1 = e2;
                e2 = temp;
            }

            // TODO: use logging
            System.out.println("Entry points: " + e1.getId() + " " + e2.getId());

            // e) Connect entry points with goal points and merge cluster tours with global tour
            int e1Index = clusterTour.indexOf(e1);
            int e2Index = clusterTour.indexOf(e2);
            int currentGlobalIndex = centerIndex;
            TourIterator it;

            if (clusterTour.get(e2Index).equals(clusterTour.getNextPoint(e1Index)))
                // Add nodes from clusterTour to resultTour, by going "left" through the clusterTour
                it = clusterTour.tourIterator(e1Index, true);
            else if (clusterTour.get(e2Index).equals(clusterTour.getPreviousPoint(e1Index)))
                // Add nodes from clusterTour to resultTour, by going "left" through the clusterTour
                it = clusterTour.tourIterator(e1Index);
            else
                throw new RuntimeException("The entry points should only be one index apart! e1: " + e1Index
                        + ", e2: " + e2Index);

            while (it.hasNext()) {
                Point p = it.next();
                globalTour.add(currentGlobalIndex, p);
                currentGlobalIndex++;
            }
            globalTour.remove(clusterCenter);

            // TODO: use logging
            System.out.println("Result (global) tour:");
            System.out.println(globalTour.toString());
        }
        return globalTour;
    }

    /**
     * Finds the edge in a clusterTour that intersects with ec, and whose center
     * has the shortest distance to the center of the global tour.
     * @param ec Edge between the center of a global tour and the center of a cluster tour.
     * @param clusterTour Cluster tour to find the intersecting edge from.
     * @param globalCenter Center point of the global tour.
     * @return Edge containing two entry points into the cluster tour.
     */
    private static Edge findIntersectingEdge(Edge ec, Tour clusterTour, Point globalCenter) {
        Edge result = null;
        double shortestDistance = Double.POSITIVE_INFINITY;

        for (int i = 0; i < clusterTour.size(); i++) {
            Edge currentEdge;

            // Check if edge intersects
            if (i+1 < clusterTour.size())
                currentEdge = new Edge(clusterTour.get(i), clusterTour.get(i+1));
            else
                currentEdge = new Edge(clusterTour.get(i), clusterTour.get(0));
            boolean intersects = Intersection.doIntersect(ec, currentEdge);

            // Finds the intersecting edge whose center has the shortest distance to
            // the center of the global tour.
            // TODO: Implement more precise heuristic that finds the intersecting edge resulting in the shortest tour.
            if (intersects) {
                Point edgeCenter = new Point("edgeCenter", new double[]{
                        (currentEdge.getFirst().getCoordinates()[0] + currentEdge.getSecond().getCoordinates()[0]) / 2,
                        (currentEdge.getFirst().getCoordinates()[1] + currentEdge.getSecond().getCoordinates()[1]) / 2
                });
                double distance = Distance.euclidianDistance(edgeCenter, globalCenter);
                if (distance < shortestDistance) {
                    result = currentEdge;
                    shortestDistance = distance;
                }
            }
        }
        return result;
    }
}
