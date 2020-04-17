package CircleTSP.entities;

import java.util.Collection;
import java.util.LinkedList;

public class Tour extends LinkedList<Point> {

    public Tour() {
        super();
    }

    public Tour(Collection<Point> points) {
        super(points);
    }

    /**
     * Gets the index of the point that occurs subsequent to the specified index i in this current circular tour.
     * If the index of the point prior to i would be out of bounds of the underlying list, this previous index wraps
     * around to the end of the list and returns the point at the last index.
     * @param i Index of a point in the tour from which to get the next following index in the tour.
     * @return The index of the point subsequent to the point at index i in the tour.
     * @throws IndexOutOfBoundsException Is thrown If the provided index i is itself out of bounds of the tour.
     * @see TourIterator For iterating through a whole tour once.
     */
    public int getSubsequentIndex(int i) throws IndexOutOfBoundsException {
        if (i == this.size()-1)
            return 0;
        else if (0 <= i && i < this.size()-1)
            return i+1;
        else
            throw new IndexOutOfBoundsException("There is no next point in the tour to this index! " +
                    "currentIndex: " + i + ", tourSize: " + this.size());
    }

    /**
     * Gets the index of the point that occurs prior to the specified index i in this current circular tour.
     * If the index of the point prior to i would be out of bounds of the underlying list, this previous index wraps
     * around to the end of the list and returns the point at the last index.
     * @param i Index of a point in the tour from which to get the index prior to i in the tour.
     * @return The index of the point prior to the point at index i in the tour.
     * @throws IndexOutOfBoundsException Is thrown If the provided index i is itself out of bounds of the tour.
     * @see TourIterator For iterating through a whole tour once.
     */
    public int getPreviousIndex(int i) throws IndexOutOfBoundsException {
        if (i == 0)
            return this.size()-1;
        else if (0 < i && i < this.size())
            return i-1;
        else
            throw new IndexOutOfBoundsException("There is no previous point in the tour to this index! " +
                    "currentIndex: " + i + ", tourSize: " + this.size());
    }

    /**
     * Gets the point that occurs next to the specified index i in this current circular tour.
     * If the index of the next point to i would be out of bounds of the underlying list, this next index wraps around
     * to the beginning of the list and returns the point at the first index.
     * Other than iterating with a TourIterator, this method can be called arbitrarily often, which can lead to more
     * points returned than present in the tour.
     * For iterating through a tour with the purpose of retrieving a correct tour or sequence of Points
     * from a given start index, use a TourIterator.
     * @param i Index of a point in the tour from which to get the next following point in the tour.
     * @return The point next in the tour relative to index i.
     * @throws IndexOutOfBoundsException Is thrown If the provided index i is itself out of bounds of the tour.
     * @see TourIterator For iterating through a whole tour once.
     */
    public Point getNextPoint(int i) throws IndexOutOfBoundsException {
        return this.get(getSubsequentIndex(i));
    }

    /**
     * Gets the point that occurs prior to the specified index i in this current circular tour.
     * If the index of the point prior to i would be out of bounds of the underlying list, this previous index wraps
     * around to the end of the list and returns the point at the last index.
     * Other than iterating with a TourIterator, this method can be called arbitrarily often, which can lead to more
     * points returned than present in the tour.
     * For iterating through a tour with the purpose of retrieving a correct tour or sequence of Points
     * from a given start index, use a TourIterator.
     * @param i Index of a point in the tour from which to get the point with index prior to i in the tour.
     * @return The point prior in the tour relative to index i.
     * @throws IndexOutOfBoundsException Is thrown If the provided index i is itself out of bounds of the tour.
     * @see TourIterator For iterating through a whole tour once.
     */
    public Point getPreviousPoint(int i) throws IndexOutOfBoundsException {
        return this.get(getPreviousIndex(i));
    }

    public TourIterator tourIterator(int startIndex) {
        return new TourIterator(this, startIndex);
    }

    public TourIterator tourIterator(int startIndex, boolean reversedDirection) {
        return new TourIterator(this, startIndex, reversedDirection);
    }
}
