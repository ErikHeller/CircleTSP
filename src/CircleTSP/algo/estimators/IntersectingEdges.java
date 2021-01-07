package CircleTSP.algo.estimators;

import CircleTSP.entities.*;
import CircleTSP.util.Distance;
import CircleTSP.util.Intersection;

public class IntersectingEdges implements EntrypointHeuristic {

    /**
     * Finds the edge in a localTour that intersects with an auxiliary edge between localCenter and globalCenter,
     * and whose center has the shortest distance to the center of the global tour.
     * The points of the edge will be returned as entry points for the local tour.
     * Entry points are used to determine which points in the local tour will be connected to the global tour.
     * @param localTour Local (cluster) tour to find the intersecting edge from.
     * @param localCenter Center point of the local tour.
     * @param globalTour Tour to be connected with the local tour.
     * @param globalCenter Center point of the global tour.
     * @return Tuple containing two entry points from the local tour.
     */
    public Tuple<Point, Point> findEntryPoints(Tour localTour, Point localCenter, Tour globalTour, Point globalCenter) {
        Edge result = null;
        double shortestDistance = Double.POSITIVE_INFINITY;

        // Create auxiliary edge between globalCenter and clusterCenter
        Edge ec = new Edge(localCenter, globalCenter);

        // For each edge corresponding to a point...
        for (int i = 0; i < localTour.size(); i++) {
            Edge currentEdge;

            // Check if edge intersects
            if (i+1 < localTour.size())
                currentEdge = new Edge(localTour.get(i), localTour.get(i+1));
            else
                currentEdge = new Edge(localTour.get(i), localTour.get(0));
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
        // TODO: Return some edge if no intersecting edge exists
        return result;
    }
}
