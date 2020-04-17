package CircleTSP.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TourIteratorTest {
    private Tour testTour;

    @BeforeEach
    void setUp() {
        testTour = new Tour();
        Point p1 = new Point("p1", new double[]{0,0});
        Point p2 = new Point("p2", new double[]{0,1});
        Point p3 = new Point("p3", new double[]{1,0});
        Point p4 = new Point("p4", new double[]{1,1});
        testTour.add(p1);
        testTour.add(p2);
        testTour.add(p3);
        testTour.add(p4);
    }

    @Test
    void testHasNext() {
        TourIterator it = testTour.tourIterator(0);
        assertTrue(it.hasNext());
        assertEquals(0, it.getCurrentIndex());

        it = testTour.tourIterator(testTour.size()-1);
        assertTrue(it.hasNext());

        for (int i = 0; i < testTour.size(); i++) {
            it.next();
        }
        assertFalse(it.hasNext());
    }

    @Test
    void testHasNextReversed() {
        TourIterator it = testTour.tourIterator(0, true);
        assertTrue(it.hasNext());
        assertEquals(0, it.getCurrentIndex());

        it = testTour.tourIterator(testTour.size()-1, true);
        assertTrue(it.hasNext());

        for (int i = 0; i < testTour.size(); i++) {
            it.next();
        }
        assertFalse(it.hasNext());
    }

    @Test
    void testNext() {
        List<Point> replicatedTour = new ArrayList<>();
        TourIterator it = testTour.tourIterator(0);
        while (it.hasNext()) {
            replicatedTour.add(it.next());
        }

        assertEquals(testTour.size(), replicatedTour.size());

        for (int i = 0; i < testTour.size(); i++) {
            assertEquals(testTour.get(i), replicatedTour.get(i));
        }
    }

    @Test
    void testNextOffset() {
        int offset = testTour.size()/2;
        List<Point> replicatedTour = new ArrayList<>();
        TourIterator it = testTour.tourIterator(offset);
        while (it.hasNext()) {
            replicatedTour.add(it.next());
        }

        assertEquals(testTour.size(), replicatedTour.size());

        for (int i = 0; i < testTour.size(); i++) {
            int j = (i+offset) % testTour.size();
            assertEquals(testTour.get(i), replicatedTour.get(j));
        }
    }

    @Test
    void testNextReversed() {
        List<Point> replicatedTour = new ArrayList<>();
        TourIterator it = testTour.tourIterator(0, true);
        while (it.hasNext()) {
            replicatedTour.add(it.next());
        }

        assertEquals(testTour.size(), replicatedTour.size());

        for (int i = 0; i < testTour.size(); i++) {
            int j = Math.floorMod((0-i), testTour.size());
            assertEquals(testTour.get(i), replicatedTour.get(j));
        }
    }

    @Test
    void testNextOffsetReversed() {
        int offset = testTour.size()/2;
        List<Point> replicatedTour = new ArrayList<>();
        TourIterator it = testTour.tourIterator(offset, true);
        while (it.hasNext()) {
            replicatedTour.add(it.next());
        }

        assertEquals(testTour.size(), replicatedTour.size());

        for (int i = 0; i < testTour.size(); i++) {
            int j = Math.floorMod((offset-i), testTour.size());
            assertEquals(testTour.get(i), replicatedTour.get(j));
        }
    }
}