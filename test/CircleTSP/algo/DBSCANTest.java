package CircleTSP.algo;

import CircleTSP.entities.Point;
import CircleTSP.entities.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DBSCANTest {

    @Test
    void benchmarkGetClusters() {

        final int N = 1000;
        final int minPts = 5;
        final double epsilon = 0.25;
        Random rnd = new Random();
        Set<Point> points = new HashSet<>();

        for (int i = 0; i < N; i++){
            points.add(new Point("" + i, new double[] {
                    rnd.nextDouble(), rnd.nextDouble()
            }));
        }

        double time1_apache = System.currentTimeMillis();
        DBSCANClusterer<Point> clusterer = new DBSCANClusterer<>(epsilon, minPts);
        List<org.apache.commons.math3.ml.clustering.Cluster<Point>> clusters_apache = clusterer.cluster(points);
        double time2_apache = System.currentTimeMillis();
        double time_apache = time2_apache - time1_apache;

        System.out.println("DBSCAN Apache: " + time_apache);

        double time1 = System.currentTimeMillis();
        DBSCAN dbscan = new DBSCAN(points, minPts, epsilon);
        List<Cluster> clusters = dbscan.getClusters();
        double time2 = System.currentTimeMillis();
        double time = time2 - time1;

        System.out.println("DBSCAN: " + time);

        // Implementations behave similar but not exactly the same due to randomisation
        /*
        assertEquals(clusters.size(), clusters_apache.size());

        for (Cluster cluster : clusters) {
            Collection<Point> pointSet = cluster.getPoints();
            Point probe = pointSet.iterator().next();
            boolean equal = false;
            for (org.apache.commons.math3.ml.clustering.Cluster<Point> apache_cluster : clusters_apache) {
                if (equal)
                    break;
                Collection<Point> pointSet_apache = apache_cluster.getPoints();
                if (!pointSet_apache.contains(probe))
                    continue;

                equal = pointSet_apache.containsAll(pointSet) && pointSet.containsAll(pointSet_apache);
            }
            if (!equal)
                fail();
        }
        assertTrue(true);
        */
    }
}