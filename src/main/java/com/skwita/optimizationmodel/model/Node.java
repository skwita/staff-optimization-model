package com.skwita.optimizationmodel.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {
    private String code;
    private String[] predecessors;
}
