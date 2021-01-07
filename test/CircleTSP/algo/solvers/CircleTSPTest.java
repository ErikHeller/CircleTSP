package CircleTSP.algo.solvers;

import CircleTSP.algo.solvers.CircleTSP;
import CircleTSP.entities.Point;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Erik Heller on 29.09.2019.
 */
class CircleTSPTest {

    private static final int N = 1000000;
    private static Point[] points = new Point[N];

    @BeforeAll
    static void setUp() {
        Random rnd = new Random();
        for (int i = 0; i < N; i++) {
            double x = rnd.nextDouble();
            double y = rnd.nextDouble();
            points[i] = new Point("" + i, new double[]{x, y});
        }
    }

    @Test
    void testPointToAngle() {
        for (Point p : points) {
            double x = p.getCoordinates()[0];
            double y = p.getCoordinates()[1];
            assertEquals(
                    Math.round(CircleTSP.pointToAngle2(x,y)*10000)/10000.0,
                    Math.round(CircleTSP.pointToAngle(x, y)*10000)/10000.0
            );
        }
    }

    @Test
    void benchmarkPointToAngle() {
        long time1 = System.currentTimeMillis();
        for (Point p : points) {
            double x = p.getCoordinates()[0];
            double y = p.getCoordinates()[1];
            CircleTSP.pointToAngle(x, y);
        }
        long time2 = System.currentTimeMillis();
        System.out.println("Time used (pointToAngle): " + (time2-time1) + "ms");

        assertTrue(true);
    }

    @Test
    void benchmarkPointToAngle2() {
        long time1 = System.currentTimeMillis();
        for (Point p : points) {
            double x = p.getCoordinates()[0];
            double y = p.getCoordinates()[1];
            CircleTSP.pointToAngle2(x, y);
        }
        long time2 = System.currentTimeMillis();
        System.out.println("Time used (atan2): " + (time2-time1) + "ms");

        assertTrue(true);
    }
}