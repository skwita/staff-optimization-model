package com.skwita.optimizationmodel.util;

import java.util.ArrayList;
import java.util.List;


public class ParetoFinder {
    public List<List<Double>> getCustomPoints(List<List<Double>> allPoints) {
        return calculatePareto(allPoints.stream()
                                        .sorted((a, b) -> a.get(4).compareTo(b.get(4)))
                                        .toList(), 5);
    }

    private List<List<Double>> calculatePareto(List<List<Double>> allPoints, int costColumn) {
        List<List<Double>> result = new ArrayList<>();
        result.add(allPoints.get(0));
        for (int i = 0; i < allPoints.size(); i++) {
            if (result.get(result.size() - 1).get(costColumn) > allPoints.get(i).get(costColumn)) {
                result.add(allPoints.get(i));
            }    
        }
        return result;
    }
}
