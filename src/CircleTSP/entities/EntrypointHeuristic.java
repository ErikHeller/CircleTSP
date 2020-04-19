package CircleTSP.entities;

public interface EntrypointHeuristic {

    Tuple<Point, Point> findEntryPoints(Tour localTour, Point localCenter,
                                        Tour globalTour, Point globalCenter);
}
