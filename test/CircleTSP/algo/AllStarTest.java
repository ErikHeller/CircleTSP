package CircleTSP.algo;

import CircleTSP.entities.Point;
import CircleTSP.entities.Tuple;
import CircleTSP.util.Distance;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Erik Heller on 04.10.2019.
 */
class AllStarTest {

    private static Set<Point> pointSet;
    private static Point start;
    private static Point end;

    @BeforeAll
    static void setUp() {
        final int n = 64;
        pointSet = new HashSet<>();
        start = new Point("start", new double[] {0, 0});
        end = new Point("end", new double[] {n,n});
        pointSet.add(start);
        pointSet.add(end);

        Random rnd = new Random();
        double r1 = rnd.nextDouble();
        double r2 = rnd.nextDouble();

        // r1 = 0;
        // r2 = 0;

        for (int i = 0; i< n-2; i++) {
            // double[] coords = new double[] {1+i+(r1*32), 1+i+(r2*32)};
            double[] coords = new double[] {1+i+(r1*0.5), 1+i+(r2*0.5)};
            pointSet.add(new Point("" + i, coords));
        }
    }

    @Test
    void testFindPath() {
        long time1 = System.currentTimeMillis();
        List<Point> path = AllStar.findPath(pointSet, start, end);
        long time2 = System.currentTimeMillis();
        System.out.println("Time used (AllStar): " + (time2-time1) + "ms");

        if (path == null)
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

        // AllStar2
        System.out.println("\n=== AllStar2 ===\n");

        time1 = System.currentTimeMillis();
        List<Point> path2 = AllStar2.findPath(pointSet, start, end);
        time2 = System.currentTimeMillis();
        System.out.println("Time used (AllStar2): " + (time2-time1) + "ms");

        if (path2 == null)
            fail();

        assertTrue(path2.containsAll(pointSet));
        assertEquals(path.size(), path2.size());

        for (int i = 0; i < path.size(); i++) {
            Point p1 = path.get(i);
            Point p2 = path2.get(i);
            assertEquals(p1, p2);
        }

        distance = 0;
        System.out.print("Path: ");
        for (int i = 0; i < path2.size()-1; i++) {
            Point p1 = path2.get(i);
            Point p2 = path2.get(i+1);
            System.out.print(" " + p1.getId());
            distance += Distance.euclidianDistance(p1, p2);
        }
        System.out.println(" " + path2.get(path.size()-1));
        System.out.println("Length of path: " + distance);
    }

    @Test
    void findEntryPoints() {
        PCA pca = new PCA(pointSet);
        RealVector pc = pca.getEigenvector(0);
        Tuple<Point, Point> entryPoints = AllStar.findEntryPoints(pointSet, pc);

        assertTrue(entryPoints.getFirst().equals(start) || entryPoints.getSecond().equals(start));
        assertTrue(entryPoints.getFirst().equals(end) || entryPoints.getSecond().equals(end));
    }
}