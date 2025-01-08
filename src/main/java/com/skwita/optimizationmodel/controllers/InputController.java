package com.skwita.optimizationmodel.controllers;

import java.util.ArrayList;
import java.util.List;

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

        // Preparation for Pareto calculation
        ParetoFinder paretoFinder = new ParetoFinder();
        TeamGenerator teamGenerator = new TeamGenerator(dataForm);
        int maxIterations = 1;
        for (int i = 0; i < analysts.length; i++) {
            if (analysts[i] > 0) {
                maxIterations *= analysts[i];
            }
        }
        for (int i = 0; i < analysts.length; i++) {
            if (developers[i] > 0) {
                maxIterations *= developers[i];
            }
        }
        for (int i = 0; i < analysts.length; i++) {
            if (testers[i] > 0) {
                maxIterations *= testers[i];
            }
        }

        // Calculate all points if it is less than 500,000
        if (maxIterations < 500000) {
        // if (true) {
            List<List<Double>> teams = teamGenerator.generateAll(analysts, developers, testers, maxIterations, false);
            List<List<Double>> optimalTeams = paretoFinder.getCustomPoints(teams, false);
            // model.addAttribute(DATA_PARETO, optimalTeams);
            model.addAttribute(DATA_PARETO, optimalTeams);
            model.addAttribute(DATA_ALL, teams);
            return "output_full";

        // Calculate a fraction of all points until the area difference is less than 10%
        } else {
            int base = 2;
            int exp = 1;
            List<List<Double>> teams = new ArrayList<>();
            List<List<Double>> optimalTeams = new ArrayList<>();
            List<Double> areas = new ArrayList<>();
            List<Double> areasDiff = new ArrayList<>();
            double area = 0.0;
            // double maxMonteCarlo = Math.sqrt(Math.pow(base, Math.log(maxIterations)/Math.log(2) - 5 - exp));
            double maxMonteCarlo = Math.pow(base, 22.0 - exp); // for testing


            //calculating first iteration average area
            for (int i = 0; i < maxMonteCarlo; i++) {
                
                teams = teamGenerator.generateAll(analysts, developers, testers, (int)Math.pow(base, exp), true);
                optimalTeams = paretoFinder.getCustomPoints(teams, true);
                areas = new ArrayList<>();

                List<Double> firstPoint = List.of(0.0, 0.0, optimalTeams.get(0).get(2));
                List<Double> lastPoint  = List.of(0.0, optimalTeams.get(optimalTeams.size()-1).get(1), 0.0);
                area += AreaFinder.getArea(optimalTeams, firstPoint, lastPoint) / maxMonteCarlo;
            }
            exp++;

            areas.add(area);

            for (int i = 0; i < maxIterations; i++) {
                area = 0.0;
                maxMonteCarlo = Math.pow(base, 22.0 - exp);
                for (int j = 0; j < maxMonteCarlo; j++) {
                    
                    teams = teamGenerator.generateAll(analysts, developers, testers, (int)Math.pow(base, exp), true);
                    optimalTeams = paretoFinder.getCustomPoints(teams, true);
                
                    List<Double> firstPoint = List.of(0.0, 0.0, optimalTeams.get(0).get(2));
                    List<Double> lastPoint  = List.of(0.0, optimalTeams.get(optimalTeams.size()-1).get(1), 0.0);
                    area += AreaFinder.getArea(optimalTeams, firstPoint, lastPoint) / maxMonteCarlo;
                }
                exp++;
                areas.add(area);

                System.out.println(teams.size());
                System.out.println(optimalTeams.size()); 
                System.out.println(area);

                if (areas.get(areas.size() - 1) / areas.get(areas.size() - 2) < 1.05) {
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
}
