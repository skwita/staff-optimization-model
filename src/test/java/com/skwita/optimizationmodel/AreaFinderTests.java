package com.skwita.optimizationmodel;

import com.skwita.optimizationmodel.util.AreaFinder;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;


class AreaFinderTests {
    @Test
    void testBasicAreaBetweenPoints() {
        List<List<Double>> points = List.of(
                List.of(0.0, 10.0, 1.0),
                List.of(1.0, 20.0, 2.0),
                List.of(2.0, 30.0, 3.0)
        );

        List<Double> firstPoint100 = List.of(999.0, 10.0, 1.0);
        List<Double> lastPoint100 = List.of(999.0, 30.0, 3.0);

        double area = AreaFinder.getArea(points, firstPoint100, lastPoint100);

        // Trapezoid rule:
        // (1-0)*(10+20)/2 = 15
        // (2-1)*(20+30)/2 = 25
        // Total = 40
        assertEquals(40.0, area, 1e-6);
    }

    @Test
    void testAreaWithDifferentFirstLastPoint100() {
        List<List<Double>> points = List.of(
                List.of(0.0, 15.0, 1.0),
                List.of(1.0, 25.0, 2.0)
        );

        List<Double> firstPoint100 = List.of(999.0, 10.0, 0.5);
        List<Double> lastPoint100 = List.of(999.0, 30.0, 2.5);

        double area = AreaFinder.getArea(points, firstPoint100, lastPoint100);

        // Main area: (1-0)*(15+25)/2 = 20.0
        // FirstPoint100 to first: (1.0 - 0.5)*(15+10)/2 = 0.5 * 25 / 2 = 6.25
        // Last to LastPoint100: (2.5 - 2.0)*(25+30)/2 = 0.5 * 55 / 2 = 13.75
        // Total = 20 + 6.25 + 13.75 = 40.0
        assertEquals(40.0, area, 1e-6);
    }

    @Test
    void testSinglePointNoArea() {
        List<List<Double>> points = List.of(
                List.of(0.0, 10.0, 1.0)
        );

        List<Double> firstPoint100 = List.of(999.0, 10.0, 1.0);
        List<Double> lastPoint100 = List.of(999.0, 10.0, 1.0);

        double area = AreaFinder.getArea(points, firstPoint100, lastPoint100);

        assertEquals(0.0, area, 1e-6);
    }

    @Test
    void testEmptyListNoArea() {
        List<List<Double>> points = List.of();

        List<Double> firstPoint100 = List.of(999.0, 10.0, 1.0);
        List<Double> lastPoint100 = List.of(999.0, 30.0, 3.0);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            AreaFinder.getArea(points, firstPoint100, lastPoint100);
        });
    }
}
