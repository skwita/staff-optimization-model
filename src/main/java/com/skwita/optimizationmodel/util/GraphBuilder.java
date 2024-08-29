package com.skwita.optimizationmodel.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skwita.optimizationmodel.model.*;

public class GraphBuilder {
    private GraphBuilder(){}
    public static String graphToJson(DataForm dataForm) {
        char stageCode = 'A';
        for (DataRow row : dataForm.getRows()) {
            row.setStageCode(Character.toString(stageCode++));
        }

        Graph graph = build(dataForm);

        String jsonStr = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonStr = objectMapper.writeValueAsString(graph);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    public static Graph build(DataForm dataForm) {
        Graph graph = new Graph();
        Node[] nodes = new Node[dataForm.size()];
        for (int i = 0; i < nodes.length; i++) {
            Node node = new Node();
            node.setCode(dataForm.getRows().get(i).getStageCode());
            String predecessorsString = dataForm.getRows().get(i).getPreviousStage();
            String[] predecessors = new String[predecessorsString.length()];
            for (int j = 0; j < predecessors.length; j++) {
                predecessors[j] = Character.toString(predecessorsString.toCharArray()[j]);
            }
            node.setPredecessors(predecessors);

            nodes[i] = node;
        }
        graph.setGraphArray(nodes);
        return graph;
    }
}
