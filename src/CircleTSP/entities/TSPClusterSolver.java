package CircleTSP.entities;

import CircleTSP.util.Distance;

import java.util.Collection;
import java.util.List;

public abstract class TSPClusterSolver implements TSPSolver {

    public abstract Tour calculateTour(Collection<Point> pointSet, int minPts, double epsilon);

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
    protected static Tour mergeTours(Tour globalTour, Point globalCenter,
                           List<Tour> clusterTours, List<Point> clusterCenters,
                           List<Tuple<Point, Point>> entryPoints, EntrypointHeuristic entrypointHeuristic) {

        if (clusterTours.size() != clusterCenters.size())
            throw new IllegalArgumentException("The number of clusters is not consistent between clusterTours" +
                    "and clusterCenters!");

        if (globalTour.size() == 0) {
            if (clusterTours.size() == 1)
                return clusterTours.get(0);
            else
                throw new IllegalArgumentException("Global tour is empty while there are more than one cluster tours!");
        }

        for (int i = 0; i < clusterTours.size(); i++) {
            // Find goal points g1 and g2
            Point clusterCenter = clusterCenters.get(i);
            Tour clusterTour = clusterTours.get(i);
            // Index of clusterCenter in the global globalTour
            int centerIndex = globalTour.indexOf(clusterCenter);

            Point g1 = globalTour.getPreviousPoint(centerIndex);
            Point g2 = globalTour.getNextPoint(centerIndex);

            // Get pre-set entry points
            Tuple<Point, Point> currentEntryPoints = null;
            if (entryPoints != null)
                currentEntryPoints = entryPoints.get(i);

            Point e1, e2;
            if (currentEntryPoints == null) {
                // Determine entry points using a given EntrypointHeuristic
                currentEntryPoints = entrypointHeuristic.findEntryPoints(clusterTour, clusterCenter,
                        globalTour, globalCenter);
            }

            // Extract entryPoints
            e1 = currentEntryPoints.getFirst();
            e2 = currentEntryPoints.getSecond();

            // Check if (g1,e1),(g2,e2) or (g1,e2),(g2,e1) is shorter & rename so that (g1,e1),(g2,e2) is shortest
            double length1 = Distance.euclidianDistance(e1, g1) + Distance.euclidianDistance(e2, g2);
            double length2 = Distance.euclidianDistance(e1, g2) + Distance.euclidianDistance(e1, g2);
            if (length2 < length1) {
                Point temp = e1;
                e1 = e2;
                e2 = temp;
            }

            // Connect entry points with goal points and merge cluster tours with global tour
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
        }
        return globalTour;
    }
}
