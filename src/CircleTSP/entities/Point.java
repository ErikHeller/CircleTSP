package CircleTSP.entities;

import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * Point class containing an identifier and the coordinates of a point.
 * During a run of a CircleTSP instance a Point object can be tagged with an angle the point has in the current instance.
 *
 * Created by Erik Heller on 02.09.2019.
 */
public class Point implements Clusterable {

    private static final int DIMENSIONS = 2;

    private String id;
    private double[] coordinates;
    private double angle;

    public Point(final String idArg, final double[] coordinatesArg) {
        id = idArg;
        if (coordinatesArg.length != DIMENSIONS)
            throw new IllegalArgumentException("The input vector must be" + DIMENSIONS + " dimensions long.");
        coordinates = coordinatesArg;
    }

    public static int getDIMENSIONS() {
        return DIMENSIONS;
    }

    public final String getId() {
        return id;
    }

    public final double[] getCoordinates() {
        return coordinates;
    }

    public final double getAngle() {
        return angle;
    }

    public void setAngle(final double angleArg) {
        if (angleArg < 0 || angleArg >= 360)
            throw new IllegalArgumentException("The angle of a point must" +
                    " be between 0 and 360 degrees");
        angle = angleArg;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) {
            return false;
        }

        Point p = (Point) obj;

        boolean coordsEqual = (this.coordinates.length == p.coordinates.length);
        for (int i = 0; (i < this.coordinates.length) && coordsEqual; i++) {
            coordsEqual = (this.coordinates[i] == p.coordinates[i]);
        }

        return this.id.equals(p.id) && coordsEqual;
    }

    @Override
    public double[] getPoint() {
        return coordinates;
    }
}
