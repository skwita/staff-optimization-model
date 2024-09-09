package com.skwita.optimizationmodel.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DataForm {
    private List<DataRow> rows;
    private int developers;
    private int testers;
    private int analysts;
    private int iterations;

    public int size() {
        return rows.size();
    }
}
