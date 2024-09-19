package com.skwita.optimizationmodel.util;

import java.util.List;

public class AreaFinder {
    private AreaFinder() {}

    public static double getArea(List<List<Double>> points, List<Double> firstPoint100, List<Double> lastPoint100) {
        double area = 0.0;

        int costColumn = 1; 
        int timeColumn = 2;
        
        for (int i = 1; i < points.size(); i++) {
            List<Double> p1 = points.get(i - 1);
            List<Double> p2 = points.get(i);

            double t1 = p1.get(timeColumn);
            double c1 = p1.get(costColumn);
            double t2 = p2.get(timeColumn);
            double c2 = p2.get(costColumn);

            area += 0.5 * (c1 + c2) * Math.abs(t1 - t2);
        }

        if (!firstPoint100.get(costColumn).equals(points.get(0).get(costColumn))) {
            area += 0.5 * (firstPoint100.get(costColumn) + points.get(0).get(costColumn)) * Math.abs(firstPoint100.get(timeColumn) - points.get(0).get(timeColumn));
        }

        if (!lastPoint100.get(costColumn).equals(points.get(points.size() - 1).get(costColumn))) {
            area += 0.5 * (lastPoint100.get(costColumn) + points.get(points.size() - 1).get(costColumn)) * Math.abs(lastPoint100.get(timeColumn) - points.get(points.size() - 1).get(timeColumn));
        }

        return area;
    }
}
