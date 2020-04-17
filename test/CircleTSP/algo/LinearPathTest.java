package CircleTSP.algo;

import CircleTSP.entities.Point;
import CircleTSP.util.Distance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Erik Heller on 18.10.2019.
 */
class LinearPathTest {

    private static Set<Point> pointSet;
    private static Set<Point> reversedPoints;

    @BeforeAll
    static void setUp() {
        final int n = 1024;
        pointSet = new HashSet<>();
        reversedPoints = new HashSet<>();
        final Point start = new Point("start", new double[]{0, 0});
        final Point end = new Point("end", new double[]{n, n});
        final Point endRev = new Point("end", new double[]{-n, -n});
        pointSet.add(start);
        reversedPoints.add(start);
        pointSet.add(end);
        reversedPoints.add(endRev);

        for (int i = 0; i< n-2; i++) {
            double[] coords = new double[] {1+i, 1+i};
            double[] coordsRev = new double[] {-1-i, -1-i};
            pointSet.add(new Point("" + i, coords));
            reversedPoints.add(new Point("" + i, coordsRev));
        }
    }

    @Test
    void testFindPath() {
        long time1 = System.currentTimeMillis();
        List<Point> path = LinearPath.findPath(pointSet);
        long time2 = System.currentTimeMillis();
        System.out.println("Time used (LinearPath): " + (time2-time1) + "ms");

        List<Point> pathRev = LinearPath.findPath(reversedPoints);

        if (path.isEmpty())
            fail();

        assertTrue(path.containsAll(pointSet));

        double distance = 0;
        System.out.print("Path: ");
        for (int i = 0; i < path.size()-1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i+1);
            System.out.print(" " + p1.getId());
            distance += Distance.euclidianDistance(p1, p2);
        }
        System.out.println(" " + path.get(path.size()-1));
        System.out.println("Length of path: " + distance);

        double distance2 = 0;
        System.out.print("Path (reversed): ");
        for (int i = 0; i < path.size()-1; i++) {
            Point p1 = pathRev.get(i);
            Point p2 = pathRev.get(i+1);
            System.out.print(" " + p1.getId());
            distance2 += Distance.euclidianDistance(p1, p2);
        }

        assertEquals(distance, distance2);
    }
}