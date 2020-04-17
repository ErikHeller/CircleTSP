package CircleTSP.benchmark;

import CircleTSP.algo.*;
import CircleTSP.entities.*;
import CircleTSP.util.Distance;
import CircleTSP.util.ParameterLearner;
import CircleTSP.util.Statistics;
import CircleTSP.util.TSPLIB;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Erik Heller on 28.10.2019.
 */
public class Runner {

    private JSONObject performBenchmark(final Collection<Point> points, final TSPSolver solver, final int numIterations,
                                        final int warmUp) {
        double[] timeUsed = new double[numIterations];
        double[] tourlengths = new double[numIterations];
        double learningTime = 0;
        int numClusters = 0;

        JSONObject solverJSON = new JSONObject();
        JSONObject parameters = new JSONObject();

        solverJSON.put("name", solver.getClass().getSimpleName());

        if (solver instanceof TSPClusterSolver) {
            int minPts = 4;
            TSPClusterSolver clusterSolver = (TSPClusterSolver) solver;
            double time_learn1 = System.nanoTime();
            double epsilon = ParameterLearner.learnEpsilon2(points, minPts, clusterSolver);
            double time_learn2 = System.nanoTime();
            learningTime = (double)(time_learn2 - time_learn1) / 1000000.0;

            parameters.put("minPts", minPts);
            parameters.put("epsilon", epsilon);

            DBSCAN clusterer = new DBSCAN(points, 4, epsilon);
            List<Cluster> clusters = clusterer.getClusters();
            numClusters = clusters.size();

            Tour tour;
            for (int j = 0 - warmUp; j < numIterations; j++) {
                long time1 = System.nanoTime();
                tour = clusterSolver.calculateTour(points, 4, epsilon);
                long time2 = System.nanoTime();

                if (j >= 0) {
                    timeUsed[j] = (double)(time2 - time1) / 1000000.0;
                    tourlengths[j] = Distance.calculateTourLength(tour);
                }
            }
        }
        else if (solver instanceof CircleTSP) {
            Tour tour;
            for (int j = 0 - warmUp; j < numIterations; j++) {
                long time1 = System.nanoTime();
                tour = CircleTSP.calculateTour(points);
                long time2 = System.nanoTime();

                if (j >= 0) {
                    timeUsed[j] = (double)(time2 - time1) / 1000000.0;
                    tourlengths[j] = Distance.calculateTourLength(tour);
                }
            }
        }
        else {
            throw new IllegalArgumentException("Solver is not a valid TSPSolver!");
        }

        solverJSON.put("parameters", parameters);

        JSONObject benchmark = new JSONObject();
        benchmark.put("numClusters", numClusters);

        Map<String, Double> runtime = new LinkedHashMap<>();
        runtime.put("average", Statistics.average(timeUsed));
        runtime.put("variance", Statistics.variance(timeUsed));
        runtime.put("learning", learningTime);
        benchmark.put("runtime", runtime);

        Map<String, Double> costs = new LinkedHashMap<>();
        costs.put("average", Statistics.average(tourlengths));
        costs.put("variance", Statistics.variance(tourlengths));
        benchmark.put("costs", costs);

        JSONObject result = new JSONObject();
        result.put("solver", solverJSON);
        result.put("benchmark", benchmark);

        return result;
    }

    private JSONObject writeResults(final String experiment, final String dataset,
                                    final int numPoints, final int numInstances,
                                    final int numIterations, final JSONObject benchmark) {
        JSONObject instanceResult = new JSONObject();
        instanceResult.put("experiment", experiment);
        instanceResult.put("timestamp", System.currentTimeMillis());

        JSONObject settings = new JSONObject();
        settings.put("dataset", dataset);
        settings.put("numPoints", numPoints);
        settings.put("numInstances", numInstances);
        settings.put("numIterations", numIterations);
        instanceResult.put("settings", settings);

        instanceResult.put("solver", benchmark.get("solver"));
        instanceResult.put("benchmark", benchmark.get("benchmark"));

        return instanceResult;
    }

    // TODO: Create more experiments (Parameter randomization, deviation, rotation, stretching)

    public List<String> circleBenchmarks(final int numIterations, final int numInstances, final int warmUp,
                                         final double[] experiments, final int numPoints)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String experimentName = "circleBenchmarks";
        TSPSolver[] solvers = new TSPSolver[] {
                new CircleTSP(),
                new ClusteredCircleTSP(),
                new PathCircleTSP()
        };
        List<String> results = new ArrayList<>();

        int numExperiments = experiments.length;

        int countSolver = 0;
        int maxSolvers = solvers.length;
        for (TSPSolver solver : solvers) {
            for (int i = 0; i < numExperiments; i++) {
                final double deviation = experiments[i];

                    for (int j = 0; j < numInstances; j++) {

                        int maxBenchmark = maxSolvers * numExperiments * numInstances;
                        int currentBenchmark = j + i*(numInstances) + countSolver*(numInstances*numExperiments) + 1;
                        System.out.print("Running benchmark " + currentBenchmark + " of " + maxBenchmark + "\r");

                        Collection<Point> points = ProblemGenerator.randomCircle(numPoints, deviation);
                        JSONObject benchmark = performBenchmark(points, solver, numIterations, warmUp);

                        JSONObject result = writeResults(experimentName, "Dev" + deviation, numPoints,
                                numInstances, numIterations, benchmark);

                        results.add(result.toJSONString());
                    }
            }
            countSolver++;
        }
        return results;
    }

    public List<String> randomBenchmarks(final int numIterations, final int numInstances, final int warmUp,
                                         final int[] experiments)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String experimentName = "randomBenchmarks";
        TSPSolver[] solvers = new TSPSolver[] {
                new CircleTSP(),
                new ClusteredCircleTSP(),
                new PathCircleTSP()
        };
        List<String> results = new ArrayList<>();

        int numExperiments = experiments.length;

        int countSolver = 0;
        int maxSolvers = solvers.length;
        for (TSPSolver solver : solvers) {
            for (int i = 0; i < numExperiments; i++) {
                final int numPoints = experiments[i];

                Map<String, Method> generators = new HashMap<>();
                generators.put("4UC", ProblemGenerator.class.getMethod(
                        "fourUniformClusters", int.class));
                generators.put("4CC", ProblemGenerator.class.getMethod(
                        "fourCircleClusters", int.class));
                generators.put("4LC", ProblemGenerator.class.getMethod(
                        "fourLinearClusters", int.class));
                generators.put("RU", ProblemGenerator.class.getMethod(
                        "randomUniform", int.class));
                generators.put("RC", ProblemGenerator.class.getMethod(
                        "randomCircle", int.class));
                generators.put("RL", ProblemGenerator.class.getMethod(
                        "randomLinear", int.class));

                int countGenerator = 0;
                int maxGenerators = generators.size();
                for (String id : generators.keySet()) {
                    for (int j = 0; j < numInstances; j++) {

                        int maxBenchmark = maxSolvers * numExperiments * maxGenerators * numInstances;
                        int currentBenchmark = j + countGenerator*numInstances
                                + i*(numInstances*maxGenerators) +
                                countSolver*(numInstances*maxGenerators*numExperiments) + 1;
                        System.out.print("Running benchmark " + currentBenchmark + " of " + maxBenchmark + "\r");

                        Collection<Point> points = (Collection<Point>) generators.get(id).invoke(null, numPoints);
                        JSONObject benchmark = performBenchmark(points, solver, numIterations, warmUp);

                        JSONObject result = writeResults(experimentName, id, numPoints,
                                numInstances, numIterations, benchmark);

                        results.add(result.toJSONString());
                    }
                    countGenerator++;
                }
            }
            countSolver++;
        }
        return results;
    }

    public List<String> tsplibBenchmarks(final int numIterations, final int warmUp, final String folder) throws IOException {
        // Find files

        Set<String> benchmarkFiles = new TreeSet<>();

        try (Stream<Path> walk = Files.walk(Paths.get(folder))) {
            List<String> files = walk.filter(Files::isRegularFile)
                    .map(Path::toString).collect(Collectors.toList());

            for (String filepath:files) {
                File file = new File(filepath);
                String filename = file.getName().split("\\.")[0];
                File optFile = new File(folder +"/tour/" + filename + ".opt.tour");
                if (optFile.isFile()) {
                    benchmarkFiles.add(filename);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Perform benchmarks
        String experimentName = "TSPLIB";
        TSPSolver[] solvers = new TSPSolver[] {
                new CircleTSP(),
                new ClusteredCircleTSP(),
                new PathCircleTSP()
        };
        List<String> results = new ArrayList<>();

        int maxBenchmark = solvers.length * benchmarkFiles.size();
        int currentBenchmark = 1;
        for (String filename:benchmarkFiles) {
            String pointsFile = folder + "/" + filename + ".tsp";
            String tourFile = folder + "/tour/" + filename + ".opt.tour";

            HashMap<String, Point> pointMap;
            try {
                pointMap = TSPLIB.readPoints(pointsFile);
            } catch (IllegalArgumentException e) {
                currentBenchmark += solvers.length;
                continue;
            }

            for (TSPSolver solver : solvers) {
                System.out.print("Running benchmark " + currentBenchmark + " of " + maxBenchmark + "\r");

                Collection<Point> points = pointMap.values();
                JSONObject benchmark = performBenchmark(points, solver, numIterations, warmUp);

                JSONObject result = writeResults(experimentName, filename,
                        pointMap.size(),1, numIterations, benchmark);

                // Get optimal results
                Tour optimalTour = TSPLIB.readOpt(tourFile, pointMap);
                double optimalCosts = 0;
                try {
                    optimalCosts = Distance.calculateTourLength(optimalTour);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                double averageCosts = (double)((LinkedHashMap)((JSONObject)benchmark.get("benchmark"))
                        .get("costs")).get("average");
                double ratio = averageCosts / optimalCosts;

                JSONObject optimal = new JSONObject();
                optimal.put("costs", optimalCosts);
                optimal.put("ratio", ratio);
                result.put("optimal", optimal);

                results.add(result.toJSONString());

                currentBenchmark++;
            }
        }

        return results;
    }

    private static void writeFile(final String filename, List<String> results) {
        File file = new File(filename);
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(file));
            for (String result : results) {
                br.write("{ \"index\" : { \"_index\" : \"benchmarks\"} }\n");
                String s = result + System.getProperty("line.separator");
                br.write(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException {

        int[] randomLow = new int[]{4, 8, 16, 32, 64, 128, 256, 512, 1024};
        int[] randomMid = new int[]{2048, 4096, 8192, 16384, 32768};
        int[] randomHigh = new int[]{65536, 131072};
        // int[] randomVeryHigh = new int[]{262144, 524288, 1048576};
        double[] circleDeviations = new double[]{0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

        Runner runner = new Runner();

        System.out.println("=== Circle Benchmarks ===\n");
        List<String> circleResults = runner.circleBenchmarks(1024, 256, 10, circleDeviations, 512);
        writeFile("circleBenchmarks.ndjson", circleResults);
        System.out.println("Done!\n");

        System.out.println("=== Random Benchmarks (Low) ===\n");
        List<String> randomResultsLow = runner.randomBenchmarks(1024, 128, 10, randomLow);
        writeFile("randomBenchmarks_low.ndjson", randomResultsLow);
        System.out.println("Done!\n");

        System.out.println("=== Random Benchmarks (Mid) ===\n");
        List<String> randomResultsMid = runner.randomBenchmarks(64, 8, 10, randomMid);
        writeFile("randomBenchmarks_mid.ndjson", randomResultsMid);
        System.out.println("Done!\n");

        System.out.println("=== Random Benchmarks (High) ===\n");
        List<String> randomResultsHigh = runner.randomBenchmarks(4, 1, 4, randomHigh);
        writeFile("randomBenchmarks_high.ndjson", randomResultsHigh);
        System.out.println("Done!\n");

        System.out.println("=== TSPLIB Benchmarks ===\n");
        List<String> tsplibResults = runner.tsplibBenchmarks(2048, 10, args[0]);
        writeFile("tsplibBenchmarks.ndjson", tsplibResults);
        System.out.println("Done!\n");

    }
}
