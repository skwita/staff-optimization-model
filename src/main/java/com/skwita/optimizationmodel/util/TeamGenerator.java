package com.skwita.optimizationmodel.util;

import java.util.*;

import com.skwita.optimizationmodel.model.DataForm;
import com.skwita.optimizationmodel.model.DataRow;

public class TeamGenerator {
    private DataForm dataForm;
    private Random random = new Random();

    public TeamGenerator(DataForm graphTable) {
        this.dataForm = graphTable;
    }
    
    public List<List<Double>> generateAll(int maxDevelopers, int maxTesters, int maxAnalysts, int numIterations) {
        List<List<Double>> results = new ArrayList<>();
        Set<List<Integer>> uniqueTeams = new HashSet<>();

        if (numIterations >= (maxTesters * maxAnalysts * maxDevelopers)) {
            numIterations = maxTesters * maxAnalysts * maxDevelopers;
        }
        int iterationCounter = 0;
        while (iterationCounter < numIterations) { 
            List<Double> currentTeam = new ArrayList<>();

            int numDevelopers = random.nextInt(maxDevelopers) + 1;
            int numTesters = random.nextInt(maxTesters) + 1;
            int numAnalysts = random.nextInt(maxAnalysts) + 1;

            if (uniqueTeams.contains(List.of(numDevelopers, numTesters, numAnalysts))) {
                continue;
            } else {
                uniqueTeams.add(List.of(numDevelopers, numTesters, numAnalysts));
                iterationCounter++;
            }

            currentTeam.add(Double.valueOf(numAnalysts));
            currentTeam.add(Double.valueOf(numDevelopers));
            currentTeam.add(Double.valueOf(numTesters));
            double time = calculateTime(numDevelopers, numTesters, numAnalysts);
            currentTeam.add(calculateCost(numDevelopers, numTesters, numAnalysts, time));
            currentTeam.add(time);

            results.add(currentTeam);
        }

        return results;
    }

    private double calculateCost(int numDevelopers, int numTesters, int numAnalysts, double time) {
        Map<String, Integer> roleCapacity = new HashMap<>();
        roleCapacity.put("developer", numDevelopers);
        roleCapacity.put("tester", numTesters);
        roleCapacity.put("analyst", numAnalysts);
        
        double cost = 0.0;

        for (DataRow row : dataForm.getRows()) {
            int availableWorkers = roleCapacity.getOrDefault(row.getResponsibleRole(), 1);
            double timeToComplete = Double.parseDouble(row.getLaborCosts()) / (availableWorkers / (1 + 0.05 * availableWorkers * (availableWorkers - 1) / 2));

            if (row.getResponsibleRole().equals("developer")) {
                cost += timeToComplete * 1 * numDevelopers;
            } else if (row.getResponsibleRole().equals("tester")){
                cost += timeToComplete * 1 * numTesters;
            } else {
                cost += timeToComplete * 1 * numAnalysts;
            }
        }
        
        return cost;
    }

    private double calculateTime(int numDevelopers, int numTesters, int numAnalysts) {
        Map<String, Integer> workers = new HashMap<>();
        workers.put("developer", numDevelopers);
        workers.put("tester", numTesters);
        workers.put("analyst", numAnalysts);
        return findCriticalPath(dataForm.getRows(), workers);
    }

    private double findCriticalPath(List<DataRow> rows, Map<String, Integer> roleCapacity) {
        Map<String, Double> stageDurations = new HashMap<>();
        Map<String, List<String>> adjList = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        
        for (DataRow row : rows) {
            adjList.putIfAbsent(row.getStageCode(), new ArrayList<>());
            inDegree.putIfAbsent(row.getStageCode(), 0);
            
            int availableWorkers = roleCapacity.getOrDefault(row.getResponsibleRole(), 1);
            double timeToComplete = Double.parseDouble(row.getLaborCosts()) / (availableWorkers / (1 + 0.05 * availableWorkers * (availableWorkers - 1) / 2));
            stageDurations.put(row.getStageCode(), timeToComplete);
            
            for (char ch : row.getPreviousStage().toCharArray()) {
                String prevStage = String.valueOf(ch);
                adjList.putIfAbsent(prevStage, new ArrayList<>());
                adjList.get(prevStage).add(row.getStageCode());
                inDegree.put(row.getStageCode(), inDegree.getOrDefault(row.getStageCode(), 0) + 1);
            }
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, Double> earliestCompletion = new HashMap<>();

        for (String stage : adjList.keySet()) {
            if (inDegree.getOrDefault(stage, 0) == 0) {
                queue.add(stage);
                earliestCompletion.put(stage, stageDurations.get(stage));
            }
        }

        double longestPath = 0;

        while (!queue.isEmpty()) {
            String stage = queue.poll();
            double completionTime = earliestCompletion.get(stage);
            longestPath = Math.max(longestPath, completionTime);

            for (String neighbor : adjList.get(stage)) {
                double newCompletionTime = completionTime + stageDurations.get(neighbor);
                earliestCompletion.put(neighbor, Math.max(earliestCompletion.getOrDefault(neighbor, 0.0), newCompletionTime));

                int newInDegree = inDegree.get(neighbor) - 1;
                inDegree.put(neighbor, newInDegree);
                if (newInDegree == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return longestPath;
    }   
}
