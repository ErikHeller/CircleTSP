package CircleTSP.entities;

import CircleTSP.algo.CircleTSP;
import CircleTSP.entities.Point;
import CircleTSP.entities.TSPSolver;
import CircleTSP.entities.Tour;

import java.util.Collection;

public interface TSPClusterSolver extends TSPSolver {

    public Tour calculateTour(Collection<Point> pointSet, int minPts, double epsilon);
}
