package CircleTSP.entities;

import java.util.Collection;

public class Cluster {

    private double epsilon;
    private int minPts;
    private Collection<Point> points;

    public Cluster(double epsilon, int minPts, Collection<Point> points) {
        this.epsilon = epsilon;
        this.minPts = minPts;
        this.points = points;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public int getMinPts() {
        return minPts;
    }

    public Collection<Point> getPoints() {
        return points;
    }

    public void add(Point p) {
        this.points.add(p);
    }
}
