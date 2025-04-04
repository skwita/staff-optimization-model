package com.skwita.optimizationmodel;

import com.skwita.optimizationmodel.model.DataForm;
import com.skwita.optimizationmodel.model.DataRow;
import com.skwita.optimizationmodel.util.TeamGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamGeneratorTests {
    private TeamGenerator teamGenerator;
    private DataForm mockDataForm;
    private List<DataRow> mockRows;

    @BeforeEach
    void setUp() {
        mockDataForm = mock(DataForm.class);

        DataRow row1 = mock(DataRow.class);
        when(row1.getStageCode()).thenReturn("A");
        when(row1.getPreviousStage()).thenReturn("");
        when(row1.getResponsibleRole()).thenReturn("analyst");
        when(row1.getLaborCosts()).thenReturn("10");

        DataRow row2 = mock(DataRow.class);
        when(row2.getStageCode()).thenReturn("B");
        when(row2.getPreviousStage()).thenReturn("A");
        when(row2.getResponsibleRole()).thenReturn("developer");
        when(row2.getLaborCosts()).thenReturn("20");

        DataRow row3 = mock(DataRow.class);
        when(row3.getStageCode()).thenReturn("C");
        when(row3.getPreviousStage()).thenReturn("A");
        when(row3.getResponsibleRole()).thenReturn("tester");
        when(row3.getLaborCosts()).thenReturn("20");

        DataRow row4 = mock(DataRow.class);
        when(row4.getStageCode()).thenReturn("D");
        when(row4.getPreviousStage()).thenReturn("BC");
        when(row4.getResponsibleRole()).thenReturn("tester");
        when(row4.getLaborCosts()).thenReturn("10");

        mockRows = List.of(row1, row2, row3, row4);
        when(mockDataForm.getRows()).thenReturn(mockRows);

        teamGenerator = new TeamGenerator(mockDataForm);
    }

    @Test
    void testGenerateAllResultsSizeAndStructure() {
        int[] maxAnalysts = {1, 2, 2};
        int[] maxDevelopers = {1, 2, 2};
        int[] maxTesters = {1, 2, 2};
        int numIterations = 64;

        List<List<Double>> results = teamGenerator.generateAll(maxAnalysts, maxDevelopers, maxTesters, numIterations, false);
        
        assertEquals(numIterations, results.size());

        maxAnalysts = new int[]{100, 100, 100};
        maxDevelopers = new int[]{100, 100, 100};
        maxTesters = new int[]{100, 100, 100};
        numIterations = 5;

        results = teamGenerator.generateAll(maxAnalysts, maxDevelopers, maxTesters, numIterations, false);
        
        assertEquals(numIterations, results.size());

        maxAnalysts = new int[]{1, 2, 0};
        maxDevelopers = new int[]{1, 0, 2};
        maxTesters = new int[]{0, 2, 3};
        numIterations = 35;

        results = teamGenerator.generateAll(maxAnalysts, maxDevelopers, maxTesters, numIterations, false);
        
        assertEquals(numIterations, results.size());
    }

    @Test
    void testGeneratedTeamsAreUniqueAndValid() {
        int[] maxAnalysts = {1, 2, 4};
        int[] maxDevelopers = {1, 3, 2};
        int[] maxTesters = {3, 2, 3};
        int numIterations = 20;

        List<List<Double>> results = teamGenerator.generateAll(maxAnalysts, maxDevelopers, maxTesters, numIterations, false);

        Set<List<Double>> seen = new HashSet<>();
        for (List<Double> result : results) {
            List<Double> team = result.subList(3, 12);
            assertFalse(seen.contains(team));
            seen.add(team);

            assertTrue(team.get(0) + team.get(1) + team.get(2) > 0);
            assertTrue(team.get(3) + team.get(4) + team.get(5) > 0);
            assertTrue(team.get(6) + team.get(7) + team.get(8) > 0);
        }
    }

    @Test
    void testFlippedCostAndTime() {
        int[] maxAnalysts = {0, 0, 1};
        int[] maxDevelopers = {0, 0, 1};
        int[] maxTesters = {0, 0, 1};
        int numIterations = 1;

        List<List<Double>> normal = teamGenerator.generateAll(maxAnalysts, maxDevelopers, maxTesters, numIterations, false);
        List<List<Double>> flipped = teamGenerator.generateAll(maxAnalysts, maxDevelopers, maxTesters, numIterations, true);

        double normalCost = normal.get(0).get(1);
        double normalTime = normal.get(0).get(2);
        double flippedCost = flipped.get(0).get(1);
        double flippedTime = flipped.get(0).get(2);

        assertEquals(1 / normalCost, flippedCost, 1e-6);
        assertEquals(1 / normalTime, flippedTime, 1e-6);
    }

    @Test
    void testGenerateAllCalculatesTimeAndCostCorrectly() {
        int[] maxAnalysts = {1, 0, 0};
        int[] maxDevelopers = {0, 0, 1};
        int[] maxTesters = {1, 0, 0};
        int iterations = 1;
        boolean isFlipped = false;

        List<List<Double>> result = teamGenerator.generateAll(
                maxAnalysts, maxDevelopers, maxTesters, iterations, isFlipped
        );

        assertEquals(1, result.size());
        List<Double> team = result.get(0);

        double time = team.get(2);
        double cost = team.get(1);

        double expectedTime = 10 / (1 * 0.75) + 20 / (1 * 0.75) + 10 / (1 * 0.75);
        double expectedCost = (1 * 0.5 * (10 / (1 * 0.75))) + (1 * 1.5 * (20 / (1 * 1.2))) + (1 * 0.5 * (30 / (1 * 0.75)));

        assertEquals(expectedTime, time, 1e-6);
        assertEquals(expectedCost, cost, 1e-6);

        maxAnalysts = new int[]{1, 0, 0};
        maxDevelopers = new int[]{0, 1, 0};
        maxTesters = new int[]{0, 1, 0};
        iterations = 1;
        isFlipped = false;

        result = teamGenerator.generateAll(
                maxAnalysts, maxDevelopers, maxTesters, iterations, isFlipped
        );

        assertEquals(1, result.size());
        team = result.get(0);

        time = team.get(2);
        cost = team.get(1);

        expectedTime = 10 / (1 * 0.75) + 20 / (1 * 1) + 10 / (1 * 1);
        expectedCost = (1 * 0.5 * (10 / (1 * 0.75))) + (1 * 1 * (20 / (1 * 1))) + (1 * 1 * (30 / (1 * 1)));

        assertEquals(expectedTime, time, 1e-6);
        assertEquals(expectedCost, cost, 1e-6);
    }
}
