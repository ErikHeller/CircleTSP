package CircleTSP.util;

import CircleTSP.entities.Edge;
import CircleTSP.entities.Point;

// https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
public class Intersection {

    // Given three colinear points p, q, r, the function checks if
    // point q lies on line segment 'pr'
    public static boolean onSegment(Point p, Point q, Point r)
    {
        if (q.getCoordinates()[0] <= Math.max(p.getCoordinates()[0], r.getCoordinates()[0])
                && q.getCoordinates()[0] >= Math.min(p.getCoordinates()[0], r.getCoordinates()[0]) &&
                q.getCoordinates()[1] <= Math.max(p.getCoordinates()[1], r.getCoordinates()[1])
                && q.getCoordinates()[1] >= Math.min(p.getCoordinates()[1], r.getCoordinates()[1]))
            return true;

        return false;
    }

    // To find orientation of ordered triplet (p, q, r).
    // The function returns following values
    // 0 --> p, q and r are colinear
    // 1 --> Clockwise
    // 2 --> Counterclockwise
    public static int orientation(Point p, Point q, Point r)
    {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
        // for details of below formula.
        double val = (q.getCoordinates()[1] - p.getCoordinates()[1]) * (r.getCoordinates()[0] - q.getCoordinates()[0]) -
                (q.getCoordinates()[0] - p.getCoordinates()[0]) * (r.getCoordinates()[1] - q.getCoordinates()[1]);

        if (val == 0) return 0; // colinear

        return (val > 0)? 1: 2; // clock or counterclock wise
    }

    public static boolean doIntersect(Edge e1, Edge e2) {
        return doIntersect(e1.getFirst(), e1.getSecond(), e2.getFirst(), e2.getSecond());
    }

    // The main function that returns true if line segment 'p1q1'
    // and 'p2q2' intersect.
    public static boolean doIntersect(Point p1, Point q1, Point p2, Point q2)
    {
        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        // General case
        if (o1 != o2 && o3 != o4)
            return true;

        // Special Cases
        // p1, q1 and p2 are colinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;

        // p1, q1 and q2 are colinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;

        // p2, q2 and p1 are colinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;

        // p2, q2 and q1 are colinear and q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;

        return false; // Doesn't fall in any of the above cases
    }
}
