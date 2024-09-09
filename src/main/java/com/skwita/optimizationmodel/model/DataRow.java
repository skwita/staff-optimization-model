package com.skwita.optimizationmodel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DataRow {
    private String stageCode;
    private String stageName;
    private String responsibleRole;
    private String previousStage;
    private String laborCosts;
}
