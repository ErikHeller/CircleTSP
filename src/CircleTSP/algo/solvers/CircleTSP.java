package CircleTSP.algo.solvers;

import CircleTSP.algo.estimators.AverageCenter;
import CircleTSP.algo.estimators.CenterpointEstimator;
import CircleTSP.algo.Sorting;
import CircleTSP.entities.Point;
import CircleTSP.entities.Tour;

import java.util.*;


/**
 * Created by Erik Heller on 13.11.2018.
 */
public class CircleTSP implements TSPSolver {

    /** Projects a point in a two dimensional euclidean space onto a point of the unit circle
     * and calculates the angle of that point on the unit circle in degrees.
     * @param x x coordinate of the point.
     * @param y y coordinate of the point.
     * @return Angle of the point projected to the unit circle in degrees.
     */
    static double pointToAngle(double x, double y) {
        double lambda = 1/Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        if (x == 0 && y == 0)
            return 0;
        else if (x == 0 && y != 0) {
            if (y > 0)
                return 90;
            else
                return 270;
        }
        else if (x != 0 && y == 0)
            if (x > 0)
                return 0;
            else
                return 180;

        if (x > 0 && y > 0)
            return Math.toDegrees(Math.asin(y*lambda));
        else if (x > 0 && y < 0)
            return 360 + Math.toDegrees(Math.asin(y*lambda));
        else if (x < 0 && y < 0)
            return 360 - Math.toDegrees(Math.acos(x*lambda));
        else if (x < 0 && y > 0)
                return Math.toDegrees(Math.acos(x*lambda));

        return -1;
    }

    /** Does the same as the pointToAngle() function but uses the faster
     * built-in Math.atan2() function for mapping cartesian coordinates of a
     * point to an angle.
     * @param x x coordinate of the point.
     * @param y y coordinate of the point.
     * @return Angle of the point projected to the unit circle in degrees.
     */
    static double pointToAngle2(double x, double y) {
        double angle = Math.atan2(y, x);
        if (angle < 0)
            angle = 2*Math.PI + angle;
        if (angle == Math.PI*2)
            return 0;
        return Math.toDegrees(angle);
    }

    public static Tour calculateTour(Collection<Point> points) {
        // Step 1: Find a center point for all given points
        Point center = getCenterPoint(points);

        // Step 2: Move all points according to found center, project point to unit circle and calculate angle
        for (Point p : points) {

            double x = p.getCoordinates()[0] - center.getCoordinates()[0];
            double y = p.getCoordinates()[1] - center.getCoordinates()[1];

            double phi = pointToAngle2(x, y);
            p.setAngle(phi);
        }

        // Step 3: Sort points
        // TODO: Let user define startpoint
        // TODO: Evaluate if relative scaling performs better than absolute scaling by 360 degrees
        for (Point p : points)
            p.setAngle(p.getAngle() / 360);
        return new Tour(Sorting.bucketSort(new LinkedList<>(points)));
    }

    public static Point getCenterPoint(Collection<Point> points) {
        CenterpointEstimator centerEstimator = new AverageCenter();
        return centerEstimator.estimateCenter(points);
    }
}
