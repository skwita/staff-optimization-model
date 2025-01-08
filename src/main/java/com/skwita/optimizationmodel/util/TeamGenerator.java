package com.skwita.optimizationmodel.util;

import java.util.*;
import java.util.stream.Collectors;

import com.skwita.optimizationmodel.model.DataForm;
import com.skwita.optimizationmodel.model.DataRow;

public class TeamGenerator {
    private DataForm dataForm;
    private Random random = new Random();

    public TeamGenerator(DataForm graphTable) {
        this.dataForm = graphTable;
    }
    
    public List<List<Double>> generateAll(int[] maxAnalysts, int[] maxDevelopers, int[] maxTesters, int numIterations, boolean isFlipped) {
        List<List<Double>> results = new ArrayList<>();
        Set<List<Integer>> uniqueTeams = new HashSet<>();

        int iterationCounter = 0;
        while (iterationCounter < numIterations) { 
            List<Double> currentTeam = new ArrayList<>();

            List<Integer> tempTeam = List.of(random.nextInt(maxAnalysts[0] + 1),
                                             random.nextInt(maxAnalysts[1] + 1),
                                             random.nextInt(maxAnalysts[2] + 1),
                                             random.nextInt(maxDevelopers[0] + 1),
                                             random.nextInt(maxDevelopers[1] + 1),
                                             random.nextInt(maxDevelopers[2] + 1),
                                             random.nextInt(maxTesters[0] + 1),
                                             random.nextInt(maxTesters[1] + 1),
                                             random.nextInt(maxTesters[2] + 1));
            boolean isValidTeam = !((tempTeam.get(0) == 0 && tempTeam.get(1) == 0 && tempTeam.get(2) == 0) ||
                                    (tempTeam.get(3) == 0 && tempTeam.get(4) == 0 && tempTeam.get(5) == 0) ||
                                    (tempTeam.get(6) == 0 && tempTeam.get(7) == 0 && tempTeam.get(8) == 0));
            if (uniqueTeams.contains(tempTeam) || !isValidTeam) {
                continue;
            } else {
                uniqueTeams.add(tempTeam);
                iterationCounter++;
            }

            currentTeam.add(Double.valueOf(iterationCounter));

            double time = calculateTime(tempTeam.subList(0, 3), tempTeam.subList(3, 6), tempTeam.subList(6, 9));
            if (isFlipped) {
                currentTeam.add(1 / (calculateCost(tempTeam.subList(0, 3), tempTeam.subList(3, 6), tempTeam.subList(6, 9))));
            } else {
                currentTeam.add(calculateCost(tempTeam.subList(0, 3), tempTeam.subList(3, 6), tempTeam.subList(6, 9)));
            }

            if (isFlipped) {
                currentTeam.add(1 / time);
            } else {
                currentTeam.add(time);
            }

            currentTeam.addAll(tempTeam.stream()
                                       .map(Integer::doubleValue)
                                       .collect(Collectors.toList()));

            results.add(currentTeam);
        }

        return results;
    }

    private double calculateCost(List<Integer> numAnalysts, List<Integer> numDevelopers, List<Integer> numTesters) {
        Map<String, List<Integer>> roleCapacity = new HashMap<>();
        roleCapacity.put("analyst", numAnalysts);
        roleCapacity.put("developer", numDevelopers);
        roleCapacity.put("tester", numTesters);
        
        double cost = 0.0;

        for (DataRow row : dataForm.getRows()) {
            List<Integer> availableWorkers = roleCapacity.getOrDefault(row.getResponsibleRole(), List.of(1, 1, 1));
            double timeToComplete = Double.parseDouble(row.getLaborCosts()) / ((availableWorkers.get(0) * 0.75 + (availableWorkers.get(1) * 1 + (availableWorkers.get(2) * 1.2))) / 
                                                                               (1 + 0.05 * (availableWorkers.get(0) + (availableWorkers.get(1) + (availableWorkers.get(2)))) * 
                                                                               ((availableWorkers.get(0) + (availableWorkers.get(1) + (availableWorkers.get(2)))) - 1) / 2));

            if (row.getResponsibleRole().equals("developer")) {
                cost += timeToComplete * 0.5 * numDevelopers.get(0) + 
                        timeToComplete * 1.0 * numDevelopers.get(1) + 
                        timeToComplete * 1.5 * numDevelopers.get(2);
            } else if (row.getResponsibleRole().equals("tester")){
                cost += timeToComplete * 0.5 * numTesters.get(0) + 
                        timeToComplete * 1.0 * numTesters.get(1) + 
                        timeToComplete * 1.5 * numTesters.get(2);
            } else {
                cost += timeToComplete * 0.5 * numAnalysts.get(0) + 
                        timeToComplete * 1.0 * numAnalysts.get(1) + 
                        timeToComplete * 1.5 * numAnalysts.get(2);
            }
        }
        
        return cost;
    }

    private double calculateTime(List<Integer> numAnalysts, List<Integer> numDevelopers, List<Integer> numTesters) {
        Map<String, List<Integer>> workers = new HashMap<>();
        workers.put("analyst", numAnalysts);
        workers.put("developer", numDevelopers);
        workers.put("tester", numTesters);
        return findCriticalPath(dataForm.getRows(), workers);
    }

    private double findCriticalPath(List<DataRow> rows, Map<String, List<Integer>> roleCapacity) {
        Map<String, Double> stageDurations = new HashMap<>();
        Map<String, List<String>> adjList = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        
        for (DataRow row : rows) {
            adjList.putIfAbsent(row.getStageCode(), new ArrayList<>());
            inDegree.putIfAbsent(row.getStageCode(), 0);
            
            List<Integer> availableWorkers = roleCapacity.getOrDefault(row.getResponsibleRole(), List.of(1, 1, 1));
            // double timeToComplete = Double.parseDouble(row.getLaborCosts()) / ((availableWorkers.get(0) * 0.75 / 
            //                                                                    (1 + 0.05 * availableWorkers.get(0) * (availableWorkers.get(0) - 1) / 2)) + 
            //                                                                    (availableWorkers.get(1) * 1 / 
            //                                                                    (1 + 0.05 * availableWorkers.get(1) * (availableWorkers.get(1) - 1) / 2)) + 
            //                                                                    (availableWorkers.get(2) * 1.2 / 
            //                                                                    (1 + 0.05 * availableWorkers.get(2) * (availableWorkers.get(2) - 1) / 2)));

            double timeToComplete = Double.parseDouble(row.getLaborCosts()) / (((availableWorkers.get(0) * 0.75 + (availableWorkers.get(1) * 1 + (availableWorkers.get(2) * 1.2))) / 
                                                                               (1 + 0.05 * (availableWorkers.get(0) + (availableWorkers.get(1) + (availableWorkers.get(2)))) * 
                                                                               ((availableWorkers.get(0) + (availableWorkers.get(1) + (availableWorkers.get(2)))) - 1) / 2)));
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
