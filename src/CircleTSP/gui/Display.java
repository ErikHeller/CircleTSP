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

        if (gui)
            frame = new GraphDraw("CircleTSP", size, size);

        Iterator<Point> it = tour.iterator();
        Point currentPoint = it.next();
        Point nextPoint;
        if (gui)
            frame.addNode(currentPoint);

        while (it.hasNext()) {
            nextPoint = it.next();

            if (gui) {
                frame.addNode(nextPoint);
                frame.addEdge(currentPoint, nextPoint);
            }
            System.out.print(currentPoint.getId() + " ");
            currentPoint = nextPoint;
        }
        nextPoint = tour.get(0);
        if (gui)
            frame.addEdge(currentPoint, nextPoint);
        System.out.print(currentPoint.getId() + " ");

        if (gui) {
            frame.addNode(centerPoint);
        }

        System.out.println("\nTour length: " + tourLength);
        System.out.println("Time used: " + timeUsed + "ms\n");
    }
}
