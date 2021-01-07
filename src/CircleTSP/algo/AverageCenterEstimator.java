package CircleTSP.algo;

import CircleTSP.entities.Point;

import java.util.Collection;

public class AverageCenterEstimator implements CenterpointEstimator {

    /**
     * Find a center point for a collection of points, by calculating the mean average of the coordinates of all points.
     * @param points Collection of points for which a center point shall be found.
     * @return Mean average center point of the points collection.
     */
    @Override
    public Point estimateCenter(Collection<Point> points) {
        double x = 0.0, y = 0.0;
        for (Point p : points) {
            x += p.getCoordinates()[0];
            y += p.getCoordinates()[1];
        }
        x = x / points.size();
        y = y / points.size();

        return new Point("center", new double[]{x, y});
    }
}
