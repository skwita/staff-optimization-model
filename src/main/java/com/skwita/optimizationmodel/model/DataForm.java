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

    private int developersJunior;
    private int developersMiddle;
    private int developersSenior;

    private int testersJunior;
    private int testersMiddle;
    private int testersSenior;

    private int analystsJunior;
    private int analystsMiddle;
    private int analystsSenior;

    public int size() {
        return rows.size();
    }
}
