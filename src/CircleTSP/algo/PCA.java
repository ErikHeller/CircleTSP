package CircleTSP.algo;

import CircleTSP.entities.Point;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.Collection;

public class PCA {

    private EigenDecomposition ed;

    public PCA(Collection<Point> points) {
        ed = calculateEigenvalues(points);
        if (ed.hasComplexEigenvalues())
            throw new ArithmeticException();
    }

    /**
     * Gets the i-th principal component (eigenvector) of the PCA.
     * The principal components are sorted by their eigenvalues which correspond to the variance the points have on the
     * axis of each principal component.
     * @param i Determines the prinicpal component you want to get, starting from 0.
     * @return i-th prinicpal component (eigenvector) of the PCA.
     */
    public RealVector getEigenvector(int i) {
        return ed.getEigenvector(i);
    }

    /**
     * Gets the corresponding eigenvalue of the i-th principal component.
     * @param i Determines the prinicpal component you want to get the eigenvalue from, starting from 0.
     * @return Eigenvalue of the i-th principal component.
     */
    public double getEigenvalue(int i) {
        return ed.getRealEigenvalue(i);
    }


    /**
     * Utility function that projects a vector onto a single given principal component.
     * This function can also be used for vectors that are not necessarily calculated principal components.
     * @param v Vector to be projected on another vector.
     * @param pc Vector (preferably a principal component) on which the vector v is to be projected.
     * @return Value the vector v has on the projection to pc.
     */
    public static double getProjection(RealVector v, RealVector pc) {
        return v.dotProduct(pc) / pc.dotProduct(pc);
    }

    /**
     * Get the eigenvectors (= principal components) and eigenvalues of the PCA.
     * @return Eigenvalue decomposition of the points that have been used for PCA.
     */
    public EigenDecomposition getEigenDecomposition() {
        return this.ed;
    }

    /**
     * Performs a principal component analysis (PCA) by calculating a eigenvalue decomposition of the covariance matrix
     * of the given points.
     * @param points Points to perform the PCA on.
     * @return An eigenvalue decomposition from which the principal components (the eigenvectors) and their respective
     * eigenvalues can be extracted.
     */
    private static EigenDecomposition calculateEigenvalues(Collection<Point> points) {
        // https://stackoverflow.com/questions/10604507/pca-implementation-in-java
        // Power iteration would be a faster PCA method

        // Get mean average for normalization
        Point mean = CircleTSP.averageCenter(points);

        int numPoints = points.size();
        double[][] pointsArray = new double[numPoints][Point.getDIMENSIONS()];
        int i = 0;
        for (Point p : points) {
            // Normalize point
            double[] normalizedPoint = new double[Point.getDIMENSIONS()];
            for (int j = 0; j < normalizedPoint.length; j++) {
                normalizedPoint[j] = p.getCoordinates()[j] - mean.getCoordinates()[j];
            }

            pointsArray[i] = normalizedPoint;
            i++;
        }
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(pointsArray);

        // Calculate covariance matrix of points, then find eigenvectors
        // See https://stats.stackexchange.com/questions/2691/making-sense-of-principal-component-analysis-eigenvectors-eigenvalues
        Covariance covariance = new Covariance(realMatrix);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        return new EigenDecomposition(covarianceMatrix);
    }
}
