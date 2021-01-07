package CircleTSP.algo.sorting;

import CircleTSP.algo.sorting.BucketSort;
import CircleTSP.entities.Point;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Erik Heller on 08.09.2019.
 */
class BucketSortTest {

    private static int N = 10000;
    private List<Point> points;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        Random rnd = new Random();
        points = new LinkedList<>();
        for (int i = 0; i < N; i++) {
            double x = 0, y = 0;
            double angle = rnd.nextDouble() % 1;
            Point p = new Point(i + "", new double[]{x,y});
            p.setAngle(angle);
            points.add(p);
        }
    }

    @org.junit.jupiter.api.Test
    void testBucketSort() {
        BucketSort bucketSort = new BucketSort();
        List<Point> sortedPoints = bucketSort.sort(points, 10);
        boolean isSorted = checkSorted(sortedPoints);
        assertTrue(isSorted);
    }

    @org.junit.jupiter.api.Test
    void testLessPointsThanLength() {
        BucketSort bucketSort = new BucketSort();
        List<Point> threePoints = new LinkedList<>(points.subList(0,2));
        List<Point> sortedPoints = bucketSort.sort(threePoints);
        boolean isSorted = checkSorted(sortedPoints);
        assertTrue(isSorted);

        assertThrows(IllegalArgumentException.class, () -> bucketSort.sort(points, 0));
    }

    private boolean checkSorted(List<Point> points){
        boolean isSorted = true;
        for (int i = 0; i < points.size()-1; i++) {
            if (!isSorted)
                return false;
            Point p1 = points.get(i);
            Point p2 = points.get(i+1);
            isSorted = p1.getAngle() < p2.getAngle();
        }
        return isSorted;
    }
}