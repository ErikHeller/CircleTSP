package CircleTSP.algo.path;

import CircleTSP.entities.Point;
import CircleTSP.util.Distance;

import java.util.*;

/**
 * Variant of the AllStar algorithm that is closer to the A* algorithm and formulates its modifications
 * to A* as a heuristic function.
 *
 * Created by Erik Heller on 22.10.2019.
 */
public class AllStar2 {

    private static class DistancePoint implements Comparable {
        Point point;
        DistancePoint predecessor;
        double tourLength;
        double heuristicDistance;

        DistancePoint(Point p) {
            this.point = p;
            this.predecessor = null;
            this.tourLength = Double.POSITIVE_INFINITY;
            this.heuristicDistance = Double.POSITIVE_INFINITY;
        }

        DistancePoint(Point p, DistancePoint predecessor,
                             double tourLength, double heuristicDistance) {
            this.point = p;
            this.predecessor = predecessor;
            this.tourLength = tourLength;
            this.heuristicDistance = heuristicDistance;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == null)
                throw new NullPointerException("Object to compare to is null!");
            if (!(o instanceof DistancePoint))
                throw new ClassCastException("Object is not a DistancePoint!");
            return this.point.equals(((DistancePoint) o).point);
        }

        @Override
        public int hashCode() {
            return point.hashCode();
        }

        @Override
        public int compareTo(final Object o) {
            if (o == null)
                throw new NullPointerException("Object to compare to is null!");

            if (o instanceof DistancePoint) {
                DistancePoint p = (DistancePoint) o;
                return (int) Math.signum(
                        this.heuristicDistance - p.heuristicDistance);
            } else {
                throw new ClassCastException("Object is not a DistancePoint!");
            }
        }
    }

    private static double f(DistancePoint current, DistancePoint next, Point goal, boolean covered) {
        double h2Value = h2(next, goal, covered);
        if (Double.isInfinite(h2Value))
            return Double.POSITIVE_INFINITY;
        else
            return g(current, next) + 0.99 * h1(next, goal) * h2Value;
    }

    private static double g(DistancePoint current, DistancePoint next) {
        double distance = 0;
        if (current.predecessor != null) {
            distance = current.predecessor.tourLength;
            distance += Distance.euclidianDistance(current.predecessor.point, current.point);
        }
        distance += Distance.euclidianDistance(current.point, next.point);
        return distance;
    }

    private static double h1(DistancePoint p, Point goal) {
        return Distance.euclidianDistance(p.point, goal);
    }

    private static double h2(DistancePoint p, Point goal, boolean covered) {
        boolean isGoal = p.point.equals(goal);
        if (isGoal) {
            if (covered)
                return 0;
            else
                return Double.POSITIVE_INFINITY;
        }
        else
            return 1;
    }

    private static boolean isCovered(List<Point> path, Set<DistancePoint> pointSet, Point goal) {
        return (path.size() == pointSet.size()-1) && !path.contains(goal);
    }

    private static void updateFrontier(PriorityQueue<DistancePoint> frontier,
                                Set<DistancePoint> pointSet,
                                DistancePoint current, Point goal) {

        // Build a path of previously visited nodes on the way to current point
        List<Point> path = new ArrayList<>();
        path.add(current.point);
        DistancePoint dp_temp = current;
        while (dp_temp.predecessor != null) {
            path.add(dp_temp.predecessor.point);
            dp_temp = dp_temp.predecessor;
        }

        boolean covered = isCovered(path, pointSet, goal);

        for (DistancePoint next : pointSet) {
            if (!path.contains(next.point)) {
                if (frontier.contains(next)) {
                    // Update point if costs using current route is lower than previous route to this point
                    double newDistance = f(current, next, goal, covered);
                    if (newDistance <= next.heuristicDistance) {
                        frontier.remove(next);
                        next.predecessor = current;
                        next.tourLength = g(current, next);
                        next.heuristicDistance = newDistance;
                        frontier.add(next);
                    }
                } else {
                    // Add a new point to frontier
                    next.predecessor = current;
                    next.tourLength = g(current, next);
                    next.heuristicDistance = f(current, next, goal, covered);
                    frontier.add(next);
                }
            }
        }
    }

    public static List<Point> findPath(Collection<Point> points, Point start, Point goal) {
        List<Point> path = new LinkedList<>();

        // Convert Points to DistancePoints
        Set<DistancePoint> pointSet = new HashSet<>();
        DistancePoint start_dp = new DistancePoint(start, null, 0, 0);
        pointSet.add(start_dp);
        for (Point p : points) {
            if (!p.equals(start))
                pointSet.add(new DistancePoint(p));
        }

        // Init frontier
        PriorityQueue<DistancePoint> frontier = new PriorityQueue<>();
        frontier.add(start_dp);

        DistancePoint goal_dp;

        while (true) {
            if (frontier.isEmpty())
                return null;
            // Get the point with the shortest heuristic distance
            DistancePoint p = frontier.poll();
            // If goal point has been found, finish
            if (p.point.equals(goal)) {
                goal_dp = p;
                break;
            }
            updateFrontier(frontier, pointSet, p, goal);
        }

        // Build path by iterating through predecessors
        path.add(goal);
        DistancePoint dp_temp = goal_dp;
        while (dp_temp.predecessor != null) {
            path.add(0, dp_temp.predecessor.point);
            dp_temp = dp_temp.predecessor;
        }
        return path;
    }
}
