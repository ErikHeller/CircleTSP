package CircleTSP.algo.solvers;

import CircleTSP.entities.Point;
import CircleTSP.entities.Tuple;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PathCircleTSPTest {

    private static Set<Point> pointSet;
    private static Point start;
    private static Point end;

    @BeforeAll
    static void setUp() {
        final int n = 256;
        pointSet = new HashSet<>();
        start = new Point("start", new double[] {0, 0});
        end = new Point("end", new double[] {n,n});
        pointSet.add(start);
        pointSet.add(end);

        Random rnd = new Random();
        double r1 = rnd.nextDouble();
        double r2 = rnd.nextDouble();

        for (int i = 0; i< n-2; i++) {
            double[] coords = new double[] {1+i+(r1*0.5), 1+i+(r2*0.5)};
            pointSet.add(new Point("" + i, coords));
        }
    }
}