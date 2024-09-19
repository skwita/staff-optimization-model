package com.skwita.optimizationmodel.util;

import java.util.ArrayList;
import java.util.List;


public class ParetoFinder {
    public List<List<Double>> getCustomPoints(List<List<Double>> allPoints, boolean isFlipped) {
        int costColumn = 1; 
        int timeColumn = 2;
        
        if (isFlipped) {
            return calculateParetoMax(allPoints.stream()
                                        .sorted((a, b) -> a.get(timeColumn).compareTo(b.get(timeColumn)))
                                        .toList(), costColumn);
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
        result.add(allPoints.get(0));
        double maxCost = allPoints.get(0).get(costColumn);
        for (int i = 0; i < allPoints.size(); i++) {
            if (allPoints.get(i).get(costColumn) > maxCost) {
                result.add(allPoints.get(i));
                maxCost = allPoints.get(i).get(costColumn);
            }    
        }
        return result;
    }
}
