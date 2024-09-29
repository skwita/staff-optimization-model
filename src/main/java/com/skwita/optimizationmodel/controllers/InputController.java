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

        // Preparation for Pareto calculation
        ParetoFinder paretoFinder = new ParetoFinder();
        TeamGenerator teamGenerator = new TeamGenerator(dataForm);
        int maxIterations = dataForm.getDevelopers() * dataForm.getAnalysts() * dataForm.getTesters();

        // Calculate all points if it is less than 500,000
        if (maxIterations < 500000) {
        //if (true) {
            List<List<Double>> teams = teamGenerator.generateAll(dataForm.getDevelopers(), dataForm.getTesters(), dataForm.getAnalysts(), maxIterations, false);
            List<List<Double>> optimalTeams = paretoFinder.getCustomPoints(teams, false);
            // model.addAttribute(DATA_PARETO, optimalTeams);
            model.addAttribute(DATA_ALL, teams);
            model.addAttribute(DATA_ALL, optimalTeams);
            return "output_full";

        // Calculate a fraction of all points until the area difference is less than 10%
        } else {
            int base = 2;
            int exp = 1;
            List<List<Double>> teams = new ArrayList<>();
            List<List<Double>> optimalTeams = new ArrayList<>();
            List<Double> areas = new ArrayList<>();
            double area = 0.0;
            
            //calculating first iteration average area
            area = 0.0;
            for (int i = 0; i < Math.pow(base, 20.0 - exp); i++) {
                teams = teamGenerator.generateAll(dataForm.getDevelopers(), dataForm.getTesters(), dataForm.getAnalysts(), (int)Math.pow(base, exp), true);
                optimalTeams = paretoFinder.getCustomPoints(teams, true);
                areas = new ArrayList<>();

                List<Double> firstPoint = List.of(0.0, 0.0, optimalTeams.get(0).get(2));
                List<Double> lastPoint  = List.of(0.0, optimalTeams.get(optimalTeams.size()-1).get(1), 0.0);
                area += AreaFinder.getArea(optimalTeams, firstPoint, lastPoint) / Math.pow(base, 20.0 - exp);
            }
            exp++;

            areas.add(area);

            for (int i = 0; i < maxIterations; i++) {
                area = 0.0;
                double maxMonteCarlo = Math.pow(base, 20.0 - exp) > 4 ? Math.pow(base, 20.0 - exp) : 4.0;
                for (int j = 0; j < maxMonteCarlo; j++) {
                    teams = teamGenerator.generateAll(dataForm.getDevelopers(), dataForm.getTesters(), dataForm.getAnalysts(), (int)Math.pow(base, exp), true);
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
                //System.out.println(areas.get(areas.size() - 1));
                if (areas.get(areas.size() - 1) / areas.get(areas.size() - 2) < 1.05) {
                //if (Math.pow(2,i) >= maxIterations-10) {
                    System.out.println(i);
                    System.out.println(areas.get(areas.size() - 1) / areas.get(areas.size() - 2));
                    //model.addAttribute(DATA_ALL, teams);
                    model.addAttribute(DATA_ALL, optimalTeams);
                    model.addAttribute(DATA_PARETO, optimalTeams);
                    model.addAttribute(DATA_AREA, areas);
                    return "output";
                }
                
            }
            return "error";
        } 
    }
}
