package CircleTSP.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Statistics {

    // https://math.stackexchange.com/questions/2148877/iterative-calculation-of-mean-and-standard-deviation
    // https://math.stackexchange.com/questions/374881/recursive-formula-for-variance

    private static BigDecimal avg(double[] x) {
        BigDecimal sum = new BigDecimal(0.0);
        for (double i : x) {
            BigDecimal big = new BigDecimal(i);
            sum = sum.add(big);
        }
        return sum.divide(new BigDecimal(x.length));
    }

    private static BigDecimal var(double[] x) {
        BigDecimal sum = new BigDecimal(0.0);
        BigDecimal avg = avg(x);
        for (double i : x) {
            BigDecimal big = new BigDecimal(i);
            sum = sum.add(big.subtract(avg).pow(2));
        }
        return sum.divide(new BigDecimal(x.length));
    }

    public static double average(double[] x) {
        BigDecimal avg = avg(x);
        return avg.doubleValue();
    }

    public static double variance(double[] x) {
        return var(x).doubleValue();
    }

    public static double standardDeviation(double[] x) {
        return var(x).sqrt(new MathContext(3)).doubleValue();
    }
}
