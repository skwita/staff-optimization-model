package com.skwita.optimizationmodel;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.skwita.optimizationmodel.model.DataForm;
import com.skwita.optimizationmodel.model.DataRow;
import com.skwita.optimizationmodel.util.GraphBuilder;

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
    public String submitData(DataForm dataForm) {
        return GraphBuilder.graphToJson(dataForm);
    }
}
