package CircleTSP.gui;

import CircleTSP.entities.Point;
import CircleTSP.entities.Tour;

import java.util.Iterator;

public class Display {
    public static void displayResults(Tour tour, Point centerPoint,
                                      double tourLength, double timeUsed,
                                      boolean gui) {
        int size = 500;
        GraphDraw frame = null;

        // gui = gui && (tour.size() < 25);

        System.out.println("Path taken:");
        System.out.println(tour.toString());

        if (gui) {
            frame = new GraphDraw("CircleTSP", size, size+300);

            Iterator<Point> it = tour.iterator();
            Point currentPoint = it.next();
            Point nextPoint;
            frame.addNode(currentPoint);

            while (it.hasNext()) {
                nextPoint = it.next();
                frame.addNode(nextPoint);
                frame.addEdge(currentPoint, nextPoint);
                currentPoint = nextPoint;
            }
            nextPoint = tour.get(0);
            frame.addEdge(currentPoint, nextPoint);
            frame.addNode(centerPoint);
        }

        System.out.println("\nTour length: " + tourLength);
        System.out.println("Time used: " + timeUsed + "ms\n");
    }
}
