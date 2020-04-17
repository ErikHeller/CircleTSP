package CircleTSP.algo;

import CircleTSP.entities.Cluster;
import CircleTSP.util.Distance;
import CircleTSP.entities.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DBSCAN {

    private int minPts;
    private double epsilon;

    private Collection<Point> setOfPoints;

    private Set<Point> unclassified;
    private Set<Point> noise;
    private ArrayList<Cluster> clusters;

    public DBSCAN(Collection<Point> setOfPoints, int minPts, double epsilon) {
        this.setOfPoints = setOfPoints;
        this.minPts = minPts;
        this.epsilon = epsilon;

        unclassified = new HashSet<>(setOfPoints);
        noise = new HashSet<>();
    }

    // TODO: Use R*-Tree for making this query more efficient
    private Set<Point> regionQuery(Point p, double epsilon) {
        Set<Point> N = new HashSet<>();
        for (Point neighbor : setOfPoints){
            double distance = Distance.euclidianDistance(p.getCoordinates(), neighbor.getCoordinates());
            if (distance <= epsilon)
                N.add(neighbor);
        }
        return N;
    }

    /**
     * Add a point to a cluster with clusterID. Create new cluster if this cluster does not exist.
     */
    private void addToCluster(int clusterID, Point point) {
        unclassified.remove(point);
        noise.remove(point);

        try {
            clusters.get(clusterID).add(point);
        } catch (IndexOutOfBoundsException e) {
            HashSet<Point> pointSet = new HashSet<>();
            pointSet.add(point);
            clusters.add(clusterID, new Cluster(epsilon, minPts, pointSet));
        }
    }

    private boolean expandCluster(Point point, int clusterID) {
        Set<Point> seeds = regionQuery(point, epsilon);
        if (seeds.size() < minPts) {
            noise.add(point);
            unclassified.remove(point);
            return false;
        }
        else {
            addToCluster(clusterID, point);
            seeds.remove(point);

            while (!seeds.isEmpty()) {
                Point currentP = seeds.iterator().next();
                Set<Point> result = regionQuery(currentP, epsilon);

                if (result.size() >= minPts) {
                    for (Point resultP : result) {
                        if (unclassified.contains(resultP) || noise.contains(resultP)) {
                            if (unclassified.contains(resultP))
                                seeds.add(resultP);
                            addToCluster(clusterID, resultP);
                        }
                    }
                }
                seeds.remove(currentP);
            }
            return true;
        }
    }

    private ArrayList<Cluster> Run() {
        clusters = new ArrayList<>();
        int clusterID = 0;

        for (Point point : setOfPoints) {
            if (unclassified.contains(point)) {
                if (expandCluster(point, clusterID)) {
                    clusterID++;
                }
            }
        }
        return clusters;
    }


    public ArrayList<Cluster> getClusters() {
        if (clusters == null){
            clusters = Run();
        }
        return clusters;
    }

    // Reference: A Density-Based Algorithm for Discovering Clusters in Large Spatial Databases with Noise
    // - Martin Ester, Hans-Peter Kriegel, Jörg Sander, Xiaowei Xu

}
