package CircleTSP.gui;

import CircleTSP.entities.Point;

/**
 * Created by Erik Heller on 02.10.2019.
 */
class GraphDrawTest {

    public static void main(String[] args) {
        GraphDraw frame = new GraphDraw("CircleTSP", 500, 500);

        Point[] points = new Point[] {
                new Point("N", new double[] {0, 1}),
                new Point("NE", new double[] {1, 1}),
                new Point("E", new double[] {1, 0}),
                new Point("SE", new double[] {1, -1}),
                new Point("S", new double[] {0, -1}),
                new Point("SW", new double[] {-1, -1}),
                new Point("W", new double[] {-1, 0}),
                new Point("NW", new double[] {-1, 1}),
                new Point("C", new double[] {0, 0})
        };

        for (int i = 0; i < points.length; i++) {
            Point p1 = points[i];
            Point p2;
            if (i+1 == points.length)
                p2 = points[0];
            else
                p2 = points[i+1];
            frame.addNode(p1);
            frame.addEdge(p1, p2);
        }
    }

}