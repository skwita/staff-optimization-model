package com.skwita.optimizationmodel.util;

import java.util.ArrayList;
import java.util.List;


public class ParetoFinder {
    public List<List<Double>> getCustomPoints(List<List<Double>> allPoints, boolean isFlipped) {
        int costColumn = 1; 
        int timeColumn = 2;
        
        if (isFlipped) {
            return calculateParetoStrictMax(calculateParetoStrictMax(allPoints.stream()
                                        .sorted((a, b) -> a.get(timeColumn).compareTo(b.get(timeColumn)))
                                        .toList(), costColumn, timeColumn), costColumn, timeColumn);
        }
        return calculateParetoMin(allPoints.stream()
                                        .sorted((a, b) -> a.get(timeColumn).compareTo(b.get(timeColumn)))
                                        .toList(), costColumn);
    }

    private List<List<Double>> calculateParetoMin(List<List<Double>> allPoints, int costColumn) {
        List<List<Double>> result = new ArrayList<>();
        result.add(allPoints.get(0));
        for (int i = 0; i < allPoints.size(); i++) {
            if (result.get(result.size() - 1).get(costColumn) > allPoints.get(i).get(costColumn)) {
                result.add(allPoints.get(i));
            }    
        }
        return result;
    }

    private List<List<Double>> calculateParetoMax(List<List<Double>> allPoints, int costColumn) {
        List<List<Double>> result = new ArrayList<>();
        result.add(allPoints.get(allPoints.size() - 1));
        for (int i = allPoints.size() - 1; i >= 0; i--) {
            if (result.get(result.size() - 1).get(costColumn) < allPoints.get(i).get(costColumn)) {
                result.add(allPoints.get(i));
            }    
        }
        return result;
    }

    private List<List<Double>> calculateParetoStrictMax(List<List<Double>> allPointsPareto, int costColumn, int timeColumn) {
        List<List<Double>> allPoints = calculateParetoMax(allPointsPareto, costColumn);
        List<List<Double>> result = new ArrayList<>();
        List<Double> currentPoint = allPoints.get(allPoints.size() - 1);
        result.add(currentPoint);

        for (int i = allPoints.size() - 2; i > 0; i--) {
            if (isPointHigher(currentPoint, allPoints.get(i - 1), allPoints.get(i), costColumn, timeColumn)) {
                currentPoint = allPoints.get(i);
                result.add(currentPoint);
            }
        }

        result.add(allPoints.get(0));

        return result;
    }

    private boolean isPointHigher(List<Double> p1, List<Double> p2, List<Double> pm, int costColumn, int timeColumn) {
        return ((p2.get(timeColumn) - p1.get(timeColumn)) * (pm.get(costColumn) - p1.get(costColumn)) 
              - (p2.get(costColumn) - p1.get(costColumn)) * (pm.get(timeColumn) - p1.get(timeColumn))) > 0;
    }
}
