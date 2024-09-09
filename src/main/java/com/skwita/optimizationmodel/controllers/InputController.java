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
import com.skwita.optimizationmodel.util.TableReader;
import com.skwita.optimizationmodel.util.TeamGenerator;

import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class InputController {
    @GetMapping("/")
    public String showInputStagesForm(Model model) {
        DataForm dataForm = new DataForm();
        dataForm.setRows(new ArrayList<>());
        dataForm.getRows().add(new DataRow());
        model.addAttribute("dataForm", dataForm);
        return "input_stages_form";
    }
    
    @PostMapping("/submitData")
    public String submitData(DataForm dataForm, Model model) {
        String json = GraphBuilder.graphToJson(dataForm);
        model.addAttribute("graphJson", json.substring(14, json.length()-1));

        TableReader tableReader = new TableReader();
        TeamGenerator teamGenerator = new TeamGenerator(dataForm);
        List<Integer> iterationSteps = List.of((int) Math.round(dataForm.getIterations() * 0.1),
                                               (int) Math.round(dataForm.getIterations() * 0.2),
                                               (int) Math.round(dataForm.getIterations() * 0.3),
                                               (int) Math.round(dataForm.getIterations() * 0.4),
                                               (int) Math.round(dataForm.getIterations() * 0.5),
                                               (int) Math.round(dataForm.getIterations() * 0.6),
                                               (int) Math.round(dataForm.getIterations() * 0.7),
                                               (int) Math.round(dataForm.getIterations() * 0.8),
                                               (int) Math.round(dataForm.getIterations() * 0.9),
                                               (int) Math.round(dataForm.getIterations() * 1.0));
                                               
        
        List<List<Double>> teams100 = teamGenerator.generateAll(dataForm.getDevelopers(), dataForm.getTesters(), dataForm.getAnalysts(), iterationSteps.get(9));
        for (int j = 0; j < teams100.size(); j++) {
            List<Double> temp = new ArrayList<>();
            temp.add((double) j);
            temp.addAll(teams100.get(j));
            teams100.set(j, temp);
        }

        List<List<Double>> optimalTeams100 = tableReader.getCustomPoints(teams100);

        double[] avgArea = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

        avgArea[9] = AreaFinder.getArea(optimalTeams100, optimalTeams100.get(0), optimalTeams100.get(optimalTeams100.size() - 1));

        model.addAttribute("stepPareto100", optimalTeams100);
        model.addAttribute("stepAll100", teams100);
        model.addAttribute("area100", avgArea[9]);

        int maxIterationsForAvgArea = 1000;

        for (int k = 0; k < maxIterationsForAvgArea; k++) {
            for (int i = 0; i < iterationSteps.size() - 1; i++) {
                List<List<Double>> teams = teamGenerator.generateAll(dataForm.getDevelopers(), dataForm.getTesters(), dataForm.getAnalysts(), iterationSteps.get(i));
                for (int j = 0; j < teams.size(); j++) {
                    List<Double> temp = new ArrayList<>();
                    temp.add((double) j);
                    temp.addAll(teams.get(j));
                    teams.set(j, temp);
                }
                avgArea[i] += AreaFinder.getArea(tableReader.getCustomPoints(teams), optimalTeams100.get(0), optimalTeams100.get(optimalTeams100.size() - 1)) / maxIterationsForAvgArea;
                if (k == maxIterationsForAvgArea - 1) {
                    model.addAttribute("stepPareto" + (i + 1) * 10, tableReader.getCustomPoints(teams));
                    model.addAttribute("stepAll" + (i + 1) * 10, teams);
                    model.addAttribute("area" + (i + 1) * 10, AreaFinder.getArea(tableReader.getCustomPoints(teams), optimalTeams100.get(0), optimalTeams100.get(optimalTeams100.size() - 1)));
                }
            }
        }

        model.addAttribute("avgArea", avgArea);

        return "output";
    }
}
