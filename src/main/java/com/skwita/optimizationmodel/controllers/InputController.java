package com.skwita.optimizationmodel.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.skwita.optimizationmodel.model.DataForm;
import com.skwita.optimizationmodel.model.DataRow;
import com.skwita.optimizationmodel.util.AreaFinder;
import com.skwita.optimizationmodel.util.GraphBuilder;
import com.skwita.optimizationmodel.util.ParetoFinder;
import com.skwita.optimizationmodel.util.TeamGenerator;

import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class InputController {
    private static final String DATA_PARETO = "stepPareto100";
    private static final String DATA_ALL = "stepAll100";
    private static final String DATA_AREA = "area";
    private static final String DATA_AREA_DIFF = "areaDiff";
    //input form
    @GetMapping("/")
    public String showInputStagesForm(Model model) {
        DataForm dataForm = new DataForm();
        dataForm.setRows(new ArrayList<>());
        dataForm.getRows().add(new DataRow());
        model.addAttribute("dataForm", dataForm);
        return "input_stages_form";
    }
    
    //resulting page with plots
    @PostMapping("/submitData")
    public String submitData(DataForm dataForm, Model model) {
        
        // Building graph
        String json = GraphBuilder.graphToJson(dataForm);
        model.addAttribute("graphJson", json.substring(14, json.length()-1));

        int[] analysts = new int[]{dataForm.getAnalystsJunior(),
                                   dataForm.getAnalystsMiddle(),
                                   dataForm.getAnalystsSenior()};

        int[] developers = new int[]{dataForm.getDevelopersJunior(),
                                     dataForm.getDevelopersMiddle(),
                                     dataForm.getDevelopersSenior()};

        int[] testers = new int[]{dataForm.getTestersJunior(),
                                  dataForm.getTestersMiddle(),
                                  dataForm.getTestersSenior()};

        ParetoFinder paretoFinder = new ParetoFinder();
        TeamGenerator teamGenerator = new TeamGenerator(dataForm);

        // int maxIterations = 1;
        // for (int i = 0; i < analysts.length; i++) {
        //     if (analysts[i] > 0) maxIterations *= analysts[i];
        // }
        // for (int i = 0; i < developers.length; i++) {
        //     if (developers[i] > 0) maxIterations *= developers[i];
        // }
        // for (int i = 0; i < testers.length; i++) {
        //     if (testers[i] > 0) maxIterations *= testers[i];
        // }

        // maxIterations += analysts[0] * analysts[1] + analysts[1] * analysts[2] + analysts[0] * analysts[2] + analysts[0] + analysts[1] + analysts[2]; 
        // maxIterations += developers[0] * developers[1] + developers[1] * developers[2] + developers[0] * developers[2] + developers[0] + developers[1] + developers[2]; 
        // maxIterations += testers[0] * testers[1] + testers[1] * testers[2] + testers[0] * testers[2] + testers[0] + testers[1] + testers[2]; 

        int a = (analysts[0] + 1) * (analysts[1] + 1) * (analysts[2] + 1);
        int d = (developers[0] + 1) * (developers[1] + 1) * (developers[2] + 1);
        int t = (testers[0] + 1) * (testers[1] + 1) * (testers[2] + 1);
        int maxIterations = a * d * t - (a * d + a * t + d * t - a - d - t + 1);

        // Calculate all points if it is less than 500,000
        if (maxIterations < 500000) {
            List<List<Double>> teams = teamGenerator.generateAll(analysts, developers, testers, maxIterations, false);
            List<List<Double>> optimalTeams = paretoFinder.getCustomPoints(teams, false);
            model.addAttribute(DATA_PARETO, optimalTeams);
            model.addAttribute(DATA_ALL, teams);
            return "output_full";

        // Calculate a fraction of all points until the area difference is less than 10%
        } else {
            int base = 2;
            int exp = 1;
            List<Double> areas = new ArrayList<>();
            List<Double> areasDiff = new ArrayList<>();

            double maxMonteCarlo = Math.pow(base, 22.0 - exp);
            List<List<Double>> finalTeams = new ArrayList<>();
            List<List<Double>> finalOptimalTeams = new ArrayList<>();
            double area = parallelMonteCarlo(teamGenerator, paretoFinder, analysts, developers, testers, (int) maxMonteCarlo, exp, finalTeams, finalOptimalTeams);
            areas.add(area);
            exp++;

            for (int i = 0; i < maxIterations; i++) {
                maxMonteCarlo = Math.pow(base, 22.0 - exp);
                area = parallelMonteCarlo(teamGenerator, paretoFinder, analysts, developers, testers, (int) maxMonteCarlo, exp, finalTeams, finalOptimalTeams);
                areas.add(area);
                exp++;

                System.out.println(area);

                if (areas.size() > 1 && areas.get(areas.size() - 1) / areas.get(areas.size() - 2) < 1.05) {
                    List<List<Double>> teams = teamGenerator.generateAll(analysts, developers, testers, (int) Math.pow(2, exp), true);
                    List<List<Double>> optimalTeams = paretoFinder.getCustomPoints(teams, true);
                    //model.addAttribute(DATA_ALL, teams);
                    model.addAttribute(DATA_ALL, optimalTeams);
                    model.addAttribute(DATA_PARETO, optimalTeams);
                    model.addAttribute(DATA_AREA, areas);

                    for (int j = 0; j < areas.size() - 1; j++) {
                        areasDiff.add((areas.get(j + 1) / areas.get(j)) - 1);
                    }
                    model.addAttribute(DATA_AREA_DIFF, areasDiff);
                    return "output";
                }
            }
            return "error";
        }
    }

    private double parallelMonteCarlo(TeamGenerator teamGenerator, ParetoFinder paretoFinder, int[] analysts, int[] developers, int[] testers, int maxMonteCarlo, int exp, List<List<Double>> finalTeams, List<List<Double>> finalOptimalTeams) {
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        List<CompletableFuture<Double>> futures = new ArrayList<>();

        for (int i = 0; i < maxMonteCarlo; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                List<List<Double>> teams = teamGenerator.generateAll(analysts, developers, testers, (int) Math.pow(2, exp), true);
                List<List<Double>> optimalTeams = paretoFinder.getCustomPoints(teams, true);
                
                List<Double> firstPoint = List.of(0.0, 0.0, optimalTeams.get(0).get(2));
                List<Double> lastPoint = List.of(0.0, optimalTeams.get(optimalTeams.size() - 1).get(1), 0.0);
                return AreaFinder.getArea(optimalTeams, firstPoint, lastPoint) / maxMonteCarlo;
            }, pool));
        }

        return futures.stream().mapToDouble(CompletableFuture::join).sum();
    }
}
