package CircleTSP.algo.sorting;

import CircleTSP.entities.Point;

import java.util.List;

public class MergeSort implements PointSorter {
    @Override
    // Collection.sort() uses a variant of MergeSort
    public List<Point> sort(List<Point> points) {
        points.sort((p1, p2) -> (int) Math.signum(p1.getAngle() - p2.getAngle()));
        return points;
    }
}
