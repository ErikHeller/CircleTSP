package CircleTSP.util;

import CircleTSP.entities.Point;
import CircleTSP.entities.Tour;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TSPLIB {
    public static HashMap<String, Point> readPoints(String fileName) throws IOException {
        HashMap<String, Point> P = new HashMap<>();

        File file = new File(fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;

            // Check header
            boolean[] supportedFeatures = {false, false, false};
            while ((s = br.readLine()) != null) {
                if (s.trim().equals("EOF"))
                    break;
                if (s.contains("EDGE_WEIGHT_TYPE")) {
                    String type = s.split(":")[1].trim();
                    supportedFeatures[1] = type.equals("EUC_2D");
                }
                else if (s.contains("TYPE")){
                    String type = s.split(":")[1].trim();
                    supportedFeatures[0] = type.equals("TSP");
                }
                else if (s.contains("NODE_COORD_SECTION")) {
                    supportedFeatures[2] = true;
                    break;
                }
            }
            for (boolean supported:supportedFeatures) {
                if (!supported)
                    throw new IllegalArgumentException("Unsupported file format!");
            }

            // Read points
            while ((s = br.readLine()) != null) {
                if (s.trim().equals("EOF"))
                    break;
                String[] point = s.trim().split("\\s+");
                String id = point[0].trim();
                double x = Double.parseDouble(point[1]);
                double y = Double.parseDouble(point[2]);
                P.put(id, new Point(id, new double[] {x,y}));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return P;
    }

    public static Tour readOpt(String optFile, HashMap<String, Point> points) throws IOException {
        Tour tour = new Tour();

        File file = new File(optFile);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            // Check header
            boolean[] supportedFeatures = {false, false};
            while ((s = br.readLine()) != null) {
                if (s.trim().equals("EOF"))
                    break;
                if (s.contains("TYPE")){
                    String type = s.split(":")[1].trim();
                    supportedFeatures[0] = type.equals("TOUR");
                }
                else if (s.contains("TOUR_SECTION")) {
                    supportedFeatures[1] = true;
                    break;
                }
            }
            for (boolean supported:supportedFeatures) {
                if (!supported)
                    throw new IllegalArgumentException("Unsupported file format!");
            }

            // Read tour
            while ((s = br.readLine()) != null) {
                if (s.trim().equals("EOF"))
                    return tour;
                String[] ids = s.trim().split("\\s+");
                for (String id : ids) {
                    id = id.trim();
                    if (!id.equals("-1"))
                        if (!id.equals(""))
                            if (points.get(id) != null)
                                tour.add(points.get(id));
                            else
                                throw new IllegalArgumentException("Points in file do not match previously read points!");
                    else
                        return tour;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return tour;
    }
}
