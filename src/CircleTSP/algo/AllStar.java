package CircleTSP.algo;

import CircleTSP.entities.Point;
import CircleTSP.util.Distance;

import java.util.*;

/**
 * AllStar is an adaptation of A* calculating an optimal path that includes all
 * points in a set, given a set of points, a start point and an end point.
 * Solving this problem is NP-complete (see Travelling Salesman Problem)
 * and therefore may have exponential time complexity in the worst-case
 * for finding the optimal solution.
 * To avoid this worst-case, AllStar uses (similar to A*) an euclidean distance
 * heuristic to the goal point for the distance estimation, additionally to the
 * length of the already traversed path.
 * This can reduce the required time drastically for clusters with
 * "long" and "thin" shape.
 *
 *
 * Created by Erik Heller on 03.10.2019.
 */
public class AllStar {

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

    private static double f(DistancePoint current, DistancePoint next, Point goal) {
        return g(current, next) + 0.99 * h(next, goal);
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

    private static double h(DistancePoint p, Point goal) {
        return Distance.euclidianDistance(p.point, goal);
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


        // Return frontier containing the goal node if all previous nodes have been visited
        if (path.size() == pointSet.size()-1) {
            if (!path.contains(goal)) {
                DistancePoint goal_dp = new DistancePoint(goal);
                goal_dp.predecessor = current;
                goal_dp.tourLength = g(current, goal_dp);
                goal_dp.heuristicDistance = f(current, goal_dp, goal);
                frontier.add(goal_dp);
                return;
            }
        }

        for (DistancePoint next : pointSet) {
            if (!next.point.equals(goal) && !path.contains(next.point)) {
                if (frontier.contains(next)) {
                    // Update point if costs using current route is lower than previous route to this point
                    double newDistance = f(current, next, goal);
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
                    next.heuristicDistance = f(current, next, goal);
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
