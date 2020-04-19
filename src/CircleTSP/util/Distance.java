package CircleTSP.util;

import CircleTSP.entities.Point;
import CircleTSP.entities.Tour;

import java.util.List;

public class Distance {

    public static double euclidianDistance(Point p1, Point p2) {
        return euclidianDistance(p1.getCoordinates(), p2.getCoordinates());
    }

    public static double euclidianDistance(double[] p1, double[] p2) {

        return Math.sqrt(Math.pow(p1[0] - p2[0], 2)
                + Math.pow(p1[1] - p2[1], 2));
    }

    public static double calculatePathLength(List<Point> path) {
        double tourLength = 0;
        for (int i = 0; i < path.size()-1; i++) {
            Point currentPoint = path.get(i);
            Point nextPoint = path.get(i+1);
            tourLength += euclidianDistance(currentPoint.getCoordinates(), nextPoint.getCoordinates());
        }
        return tourLength;
    }

    public static double calculateTourLength(Tour tour) {
        double tourLength = 0;
        for (int i = 0; i < tour.size()-1; i++) {
            Point currentPoint = tour.get(i);
            Point nextPoint = tour.get(i+1);
            tourLength += euclidianDistance(currentPoint.getCoordinates(), nextPoint.getCoordinates());
        }
        Point startPoint = tour.get(0);
        Point endPoint = tour.get(tour.size()-1);
        tourLength += euclidianDistance(startPoint.getCoordinates(), endPoint.getCoordinates());
        return tourLength;
    }
}
