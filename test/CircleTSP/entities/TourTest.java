package CircleTSP.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TourTest {

    private Tour testTour;

    @BeforeEach
    void setUp() {
        testTour = new Tour();
        List<Point> list = new ArrayList<>();

        Point p1 = new Point("p1", new double[]{0,0});
        Point p2 = new Point("p2", new double[]{0,1});
        Point p3 = new Point("p3", new double[]{1,0});
        Point p4 = new Point("p4", new double[]{1,1});

        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);

        testTour = new Tour(list);
    }

    @Test
    void testGetNextPoint() {
        Point first = testTour.get(0);
        Point second = testTour.get(1);
        Point last = testTour.get(testTour.size()-1);

        assertEquals(second, testTour.getNextPoint(0));
        assertEquals(first, testTour.getNextPoint(testTour.size()-1));
        assertEquals(last, testTour.getNextPoint(testTour.size()-2));

        assertThrows(IndexOutOfBoundsException.class, () -> {
           testTour.getNextPoint(-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            testTour.getNextPoint(testTour.size());
        });
    }

    @Test
    void testGetPreviousPoint() {
        Point first = testTour.get(0);
        Point penultimate = testTour.get(testTour.size()-2);
        Point last = testTour.get(testTour.size()-1);

        assertEquals(last, testTour.getPreviousPoint(0));
        assertEquals(first, testTour.getPreviousPoint(1));
        assertEquals(penultimate, testTour.getPreviousPoint(testTour.size()-1));

        assertThrows(IndexOutOfBoundsException.class, () -> {
            testTour.getPreviousPoint(-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            testTour.getPreviousPoint(testTour.size());
        });
    }

    @Test
    void testTourIterator() {
        TourIterator it = testTour.tourIterator(0);
        assertNotNull(it);

        assertThrows(IllegalArgumentException.class, () -> {
           testTour.tourIterator(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            testTour.tourIterator(testTour.size());
        });
    }
}
