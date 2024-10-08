package CircleTSP.algo.estimators;

import CircleTSP.entities.Point;

import java.util.Collection;

public interface CenterpointEstimator {

    Point estimateCenter(Collection<Point> points);
}
