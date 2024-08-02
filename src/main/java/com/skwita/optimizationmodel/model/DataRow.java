package com.skwita.optimizationmodel.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DataRow {
    private String stageCode;
    private String stageName;
    private String responsibleRole;
    private String previousStage;
    private String laborCosts;
}
