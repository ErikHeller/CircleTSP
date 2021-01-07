package CircleTSP.algo.estimators;

import CircleTSP.entities.Point;
import CircleTSP.entities.Tour;
import CircleTSP.entities.Tuple;

public interface EntrypointHeuristic {

    Tuple<Point, Point> findEntryPoints(Tour localTour, Point localCenter,
                                        Tour globalTour, Point globalCenter);
}
