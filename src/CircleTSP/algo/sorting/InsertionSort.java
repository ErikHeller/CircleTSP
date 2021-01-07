package CircleTSP.algo.sorting;

import CircleTSP.entities.Point;

import java.util.List;

public class InsertionSort implements PointSorter {
    @Override
    // Rewritten from reference to match Types, ignoring cases with length 0 and 1
    // Reference: http://www.java-programmieren.com/insertionsort-java.php
    public List<Point> sort(List<Point> points) {
        if (points.size() > 1) {
            Point temp;
            for (int i = 1; i < points.size(); i++) {
                temp = points.get(i);
                int j = i;
                while (j > 0 && points.get(j - 1).getAngle() > temp.getAngle()) {
                    points.set(j, points.get(j - 1));
                    j--;
                }
                points.set(j, temp);
            }
        }
        return points;
    }
}
