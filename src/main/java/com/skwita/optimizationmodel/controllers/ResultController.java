package com.skwita.optimizationmodel.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.skwita.optimizationmodel.util.TableReader;


@Controller
public class ResultController {

    @GetMapping("/import")
    public String importFile() {
        return "import_table";
    }

    @PostMapping("/graphs")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        TableReader reader = new TableReader(file);

        List<List<Double>> dataAll = reader.readAll();
        List<List<Double>> dataParetoFull = reader.readParetoFull();

		model.addAttribute("dataPointsAll", dataAll.toString());
        model.addAttribute("dataPointsParetoFull", dataParetoFull.toString());

		return "plots";
	}

    @GetMapping("/error")
    public String getMethodName(@RequestParam String param) {
        return "ERROR";
    }
    
}
