package CircleTSP.algo;

import CircleTSP.entities.Point;

import java.util.Collection;

public class BoundingBoxCenterEstimator implements CenterpointEstimator {

    /**
     * Find a center point for a collection of points, by generating a bounding box based on all points and calculating
     * the center of this bounding box.
     * @param points Collection of points for which a center point shall be found.
     * @return Bounding box center point of the points collection.
     */
    @Override
    public Point estimateCenter(Collection<Point> points) {
        // Step 1: Create bounding box for points
        // Alternative: bucketsort & dequeue?
        double minX = 0, minY = 0, maxX = 0, maxY = 0;

        for (Point p : points) {
            double x = p.getCoordinates()[0];
            double y = p.getCoordinates()[1];

            if (x < minX) minX = x;
            else if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            else if (y > maxY) maxY = y;
        }

        // Step 2: Find center
        return new Point("center", new double[]{(maxX + minX)/2, (maxY + minY)/2});
    }
}
