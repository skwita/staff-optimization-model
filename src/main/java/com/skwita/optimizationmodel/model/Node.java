package com.skwita.optimizationmodel.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Node {
    private String code;
    private String[] predecessors;
}
