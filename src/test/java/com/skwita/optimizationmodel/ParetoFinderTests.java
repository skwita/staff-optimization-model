package com.skwita.optimizationmodel;

import com.skwita.optimizationmodel.util.ParetoFinder;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParetoFinderTests {

    private final ParetoFinder paretoFinder = new ParetoFinder();

    @Test
    void testParetoMinSimple() {
        List<List<Double>> points = List.of(
                List.of(0.0, 10.0, 1.0),
                List.of(1.0, 8.0, 2.0),
                List.of(2.0, 7.0, 3.0),
                List.of(3.0, 9.0, 4.0) // not on pareto front
        );

        List<List<Double>> pareto = paretoFinder.getCustomPoints(points, false);

        assertEquals(3, pareto.size());
        assertTrue(pareto.contains(List.of(0.0, 10.0, 1.0)));
        assertTrue(pareto.contains(List.of(1.0, 8.0, 2.0)));
        assertTrue(pareto.contains(List.of(2.0, 7.0, 3.0)));
    }

    @Test
    void testFlippedParetoStrictMax() {
        List<List<Double>> points = List.of(
                List.of(0.0, 5.0, 1.0),  // A
                List.of(1.0, 4.7, 2.0),  // B
                List.of(2.0, 4.0, 3.0),  // C
                List.of(3.0, 3.0, 4.0),  // D
                List.of(4.0, 1.0, 5.0)   // E
        );

        List<List<Double>> pareto = paretoFinder.getCustomPoints(points, true);

        // Expected points are C, B, A because D is under the convex hull (strict max)
        assertTrue(pareto.contains(List.of(0.0, 5.0, 1.0))); // A
        assertTrue(pareto.contains(List.of(1.0, 4.7, 2.0))); // B
        assertTrue(pareto.contains(List.of(2.0, 4.0, 3.0))); // C
        assertTrue(pareto.contains(List.of(3.0, 3.0, 4.0))); // D
        assertFalse(pareto.contains(List.of(4.0, 5.5, 5.0))); // E - start/end always included
    }

    @Test
    void testFlatCostValues() {
        List<List<Double>> points = List.of(
                List.of(0.0, 5.0, 1.0),
                List.of(1.0, 5.0, 2.0),
                List.of(2.0, 5.0, 3.0)
        );

        List<List<Double>> pareto = paretoFinder.getCustomPoints(points, false);

        assertEquals(1, pareto.size()); // Только первая, остальные не дают выигрыш по cost
    }

    @Test
    void testEmptyInput() {
        List<List<Double>> points = List.of();

        assertThrows(IndexOutOfBoundsException.class, () -> {
            paretoFinder.getCustomPoints(points, false);
        });
    }
}
