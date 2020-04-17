package CircleTSP.benchmark;

import CircleTSP.entities.Point;
import CircleTSP.util.Distance;

import java.util.*;

public class ProblemGenerator {

    public static Collection<Point> randomUniform(final int n) {
        return randomUniform(n, new double[]{-1.0, 1.0});
    }

    public static Collection<Point> randomUniform(final int n, final double[] range) {
        return randomUniform(n, range, range);
    }

    public static Collection<Point> randomUniform(final int n, final double[] rangeX, final double[] rangeY) {
        Collection<Point> points = new HashSet<>(n);

        if (rangeX.length != 2)
            throw new IllegalArgumentException("RangeX parameter must be of length 2!");
        if (rangeX[1] <= rangeX[0])
            throw new IllegalArgumentException("The second entry of the rangeX parameter must be higher than the first!");
        if (rangeY.length != 2)
            throw new IllegalArgumentException("RangeY parameter must be of length 2!");
        if (rangeY[1] <= rangeY[0])
            throw new IllegalArgumentException("The second entry of the rangeY parameter must be higher than the first!");

        Random rnd = new Random();
        for (int i = 0; i < n; i++) {
            double x = rnd.nextDouble();
            double y = rnd.nextDouble();

            double xNew = (x-(-1)) / (1-(-1)) * (rangeX[1]-rangeX[0]) + rangeX[0];
            double yNew = (y-(-1)) / (1-(-1)) * (rangeY[1]-rangeY[0]) + rangeY[0];

            points.add(new Point("" + i, new double[]{xNew, yNew}));
        }
        return points;
    }


    public static Collection<Point> randomCircle(final int n) {
        return randomCircle(n, 0);
    }

    public static Collection<Point> randomCircle(final int n, final double deviation) {
        return randomCircle(n, deviation, new double[]{-1.0, 1.0});
    }

    public static Collection<Point> randomCircle(final int n, final double deviation, final double[] range) {
        return randomCircle(n, deviation, range, range);
    }

    public static Collection<Point> randomCircle(final int n, final double deviation,
                                          final double[] rangeX, final double[] rangeY) {
        Collection<Point> points = new HashSet<>(n);

        if (rangeX.length != 2)
            throw new IllegalArgumentException("RangeX parameter must be of length 2!");
        if (rangeX[1] <= rangeX[0])
            throw new IllegalArgumentException("The second entry of the rangeX parameter must be higher than the first!");
        if (rangeY.length != 2)
            throw new IllegalArgumentException("RangeY parameter must be of length 2!");
        if (rangeY[1] <= rangeY[0])
            throw new IllegalArgumentException("The second entry of the rangeY parameter must be higher than the first!");

        Random rnd = new Random();
        for (int i=0; i < n; i++) {
            double phi = rnd.nextDouble() * 2*Math.PI;
            double devX = rnd.nextDouble() * 2*deviation - deviation;
            double devY = rnd.nextDouble() * 2*deviation - deviation;

            double x = Math.sin(phi) + devX;
            double y = Math.cos(phi) + devY;

            double xNew = (x-(-1)) / (1-(-1)) * (rangeX[1]-rangeX[0]) + rangeX[0];
            double yNew = (y-(-1)) / (1-(-1)) * (rangeY[1]-rangeY[0]) + rangeY[0];

            points.add(new Point("" + i, new double[]{xNew, yNew}));
        }

        return points;
    }

    public static Collection<Point> randomLinear(final int n) {
        return randomLinear(n, new double[]{-1.0, 1.0});
    }

    public static Collection<Point> randomLinear(final int n, final double[] range) {
        return randomLinear(n, range, 0);
    }

    public static Collection<Point> randomLinear(final int n, final double[] range, final double deviation) {
        return randomLinear(n, range, deviation, 0);
    }

    public static Collection<Point> randomLinear(final int n, final double[] range, final double deviation, final double angle) {
        Collection<Point> points = new HashSet<>();

        if (range.length != 2)
            throw new IllegalArgumentException("Range parameter must be of length 2!");
        if (range[1] <= range[0])
            throw new IllegalArgumentException("The second entry of the range parameter must be higher than the first!");
        if (angle < 0 || angle > 2*Math.PI)
            throw new IllegalArgumentException("The angle must be given in radians (value between 0 and 2*pi)!");

        Random rnd = new Random();
        double factor = Math.abs(range[1] - range[0]);
        for (int i=0; i < n; i++) {
            double section = rnd.nextDouble() * factor + range[0];
            double devX = rnd.nextDouble() * 2*deviation - deviation;
            double devY = rnd.nextDouble() * 2*deviation - deviation;

            double x = section + devX;
            double y = section + devY;

            double xR = x*Math.cos(angle) - y*Math.sin(angle);
            double yR = x*Math.sin(angle) + y*Math.cos(angle);

            points.add(new Point("" + i, new double[]{xR, yR}));
        }
        return points;
    }

    // Utility

    public static Collection<Point> clusterCombiner(List<Collection<Point>> clusters, List<Point> centerPoints) {
        Collection<Point> result = new HashSet<>();

        if (clusters.size() != centerPoints.size())
            throw new IllegalArgumentException("Number of clusters must be equal to number of center points!");

        double maxDistance = 0.0;
        for (int i=0; i < clusters.size(); i++) {
            Collection<Point> cluster = clusters.get(i);
            Point center = centerPoints.get(i);
            int j = 0;
            for (Point p : cluster) {
                double newX = p.getCoordinates()[0] + center.getCoordinates()[0];
                double newY = p.getCoordinates()[1] + center.getCoordinates()[1];

                Point cp = new Point("c" + i + "." + j, new double[]{newX, newY});
                maxDistance = Math.max(maxDistance,
                        Distance.euclidianDistance(new double[] {0.0, 0.0}, cp.getCoordinates()));

                result.add(cp);
                j++;
            }
        }

        // Normalize all points to range [-1, 1]^2
        Collection<Point> resultNorm = new HashSet<>();
        for (Point p : result) {
            double[] old = p.getCoordinates();
            resultNorm.add(new Point(
                    p.getId(), new double[] {old[0] / maxDistance, old[1] / maxDistance}
            ));
        }

        return resultNorm;
    }

    // Higher level generators

    // Simple high level

    public static Collection<Point> fourUniformClusters(final int n) {

        int pointsPerCluster = Math.floorDiv(n, 4);
        int rest = Math.floorMod(n, 4);
        int[] r = new int[] {0, 0, 0, 0};
        for (int i=0; i<rest; i++)
            r[i] = 1;

        List<Point> centers = new ArrayList<>();
        centers.add(new Point("N", new double[]{0.0, 5.0}));
        centers.add(new Point("S", new double[]{0.0, -5.0}));
        centers.add(new Point("W", new double[]{5.0, 0.0}));
        centers.add(new Point("O", new double[]{-5.0, 0.0}));

        List<Collection<Point>> clusters = new ArrayList<>();
        for (int i = 0; i<4; i++)
            clusters.add(randomUniform(pointsPerCluster+r[i]));

        return clusterCombiner(clusters, centers);
    }

    public static Collection<Point> fourCircleClusters(final int n) {

        int pointsPerCluster = Math.floorDiv(n, 4);
        int rest = Math.floorMod(n, 4);
        int[] r = new int[] {0, 0, 0, 0};
        for (int i=0; i<rest; i++)
            r[i] = 1;

        List<Point> centers = new ArrayList<>();
        centers.add(new Point("N", new double[]{0.0, 5.0}));
        centers.add(new Point("S", new double[]{0.0, -5.0}));
        centers.add(new Point("W", new double[]{5.0, 0.0}));
        centers.add(new Point("O", new double[]{-5.0, 0.0}));

        List<Collection<Point>> clusters = new ArrayList<>();
        for (int i = 0; i<4; i++)
            clusters.add(randomCircle(pointsPerCluster+r[i]));

        return clusterCombiner(clusters, centers);
    }

    public static Collection<Point> fourLinearClusters(final int n) {

        int pointsPerCluster = Math.floorDiv(n, 4);
        int rest = Math.floorMod(n, 4);
        int[] r = new int[3];
        for (int i=0; i<rest; i++)
            r[i] = 1;

        List<Point> centers = new ArrayList<>();
        centers.add(new Point("N", new double[]{0.0, 5.0}));
        centers.add(new Point("S", new double[]{0.0, -5.0}));
        centers.add(new Point("W", new double[]{5.0, 0.0}));
        centers.add(new Point("O", new double[]{-5.0, 0.0}));

        List<Collection<Point>> clusters = new ArrayList<>();
        clusters.add(randomLinear(pointsPerCluster + r[0],
                new double[]{-1.0, 1.0}, 0, Math.toRadians(135)));
        clusters.add(randomLinear(pointsPerCluster + r[1],
                new double[]{-1.0, 1.0}, 0, Math.toRadians(135)));
        clusters.add(randomLinear(pointsPerCluster + r[2],
                new double[]{-1.0, 1.0}, 0, Math.toRadians(45)));
        clusters.add(randomLinear(pointsPerCluster,
                new double[]{-1.0, 1.0}, 0, Math.toRadians(45)));

        return clusterCombiner(clusters, centers);
    }
}
