package com.skwita.optimizationmodel.util;

import java.util.List;

public class AreaFinder {
    private AreaFinder() {}

    public static double getArea(List<List<Double>> points, List<Double> firstPoint100, List<Double> lastPoint100) {
        double area = 0.0;
        
        for (int i = 1; i < points.size(); i++) {
            List<Double> p1 = points.get(i - 1);
            List<Double> p2 = points.get(i);

            double t1 = p1.get(5);
            double c1 = p1.get(4);
            double t2 = p2.get(5);
            double c2 = p2.get(4);

            area += 0.5 * (c1 + c2) * Math.abs(t1 - t2);
        }

        if (!firstPoint100.get(4).equals(points.get(0).get(4))) {
            area += 0.5 * (firstPoint100.get(4) + points.get(0).get(4)) * Math.abs(firstPoint100.get(5) - points.get(0).get(5));
        }

        if (!lastPoint100.get(4).equals(points.get(points.size() - 1).get(4))) {
            area += 0.5 * (lastPoint100.get(4) + points.get(points.size() - 1).get(4)) * Math.abs(lastPoint100.get(5) - points.get(points.size() - 1).get(5));
        }

        return area;
    }
}
