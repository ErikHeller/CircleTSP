package CircleTSP.entities;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TourIterator implements Iterator<Point> {

    private Tour tour;
    private int startIndex;
    private int currentIndex;
    private int nextIndex;

    private boolean directionReversed;

    public TourIterator(Tour tour, int startIndex) {
        this(tour, startIndex, false);
    }

    public TourIterator(Tour tour, int startIndex, boolean reverseDirection) {
        super();
        this.tour = tour;

        if (startIndex >= tour.size() || startIndex < 0)
            throw new IllegalArgumentException("Start index can't be outside of tour bounds!");

        this.startIndex = startIndex;
        this.currentIndex = startIndex;
        this.nextIndex = startIndex;
        this.directionReversed = reverseDirection;
    }

    private boolean hasSubsequent() {
        if (tour.getSubsequentIndex(currentIndex) == startIndex)
            return false;
        else
            return true;
    }

    private Point subsequent() {
        if (this.hasSubsequent()) {
            currentIndex = nextIndex;
            nextIndex = tour.getSubsequentIndex(currentIndex);
            return tour.get(currentIndex);
        } else
            throw new NoSuchElementException("The tour has no next point from it's current index! Index: "
                    + currentIndex + ", start index: " + startIndex);
    }

    private boolean hasPrevious() {
        if (tour.getPreviousIndex(currentIndex) == startIndex)
            return false;
        else
            return true;
    }

    private Point previous() {
        if (this.hasPrevious()) {
            currentIndex = nextIndex;
            nextIndex = tour.getPreviousIndex(currentIndex);
            return tour.get(currentIndex);
        } else
            throw new NoSuchElementException("The tour has no previous point from it's current index! Index: "
                    + currentIndex + ", start index: " + startIndex);
    }

    @Override
    public boolean hasNext() {
        if (!directionReversed)
            return hasSubsequent();
        else
            return hasPrevious();
    }

    @Override
    public Point next(){
        if (!directionReversed)
            return subsequent();
        else
            return previous();
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    private boolean isDirectionReversed() {
        return directionReversed;
    }
}
