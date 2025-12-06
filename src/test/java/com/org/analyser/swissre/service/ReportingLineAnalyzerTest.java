package com.org.analyser.swissre.service;

import com.org.analyser.swissre.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReportingLineAnalyzerTest {

    private ReportingLineAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new ReportingLineAnalyzer();
    }

    private List<Employee> sampleEmployees() {
        return List.of(
                new Employee("100", "Emma", "Stone", null, null),
                new Employee("101", "Liam", "Grant", null, "100"),
                new Employee("102", "Olivia", "Turner", null, "101"),
                new Employee("103", "Noah", "Blake", null, "102"),
                new Employee("104", "Ava", "Shields", null, "103"),
                new Employee("105", "Isla", "Rowe", null, "104"),
                new Employee("106", "Ethan", "Cross", null, "105"),
                new Employee("200", "Mason", "Reed", null, "101"),
                new Employee("201", "Sophia", "Hayes", null, "101")
        );
    }

    @Test
    void testFindEmployeesWithLongReportingLines() {
        List<Employee> employees = sampleEmployees();
        Map<String, Integer> result = analyzer.findEmployeesWithLongReportingLines(employees);

        assertNotNull(result);
        assertTrue(result.containsKey("105")); // Depth 5 → 1 over
        assertTrue(result.containsKey("106")); // Depth 6 → 2 over

        assertEquals(1, result.get("105"));
        assertEquals(1, result.get("106"));

        // Employees with shorter lines should not appear
        assertFalse(result.containsKey("100"));
        assertFalse(result.containsKey("101"));
        assertFalse(result.containsKey("102"));
        assertFalse(result.containsKey("103"));
        assertFalse(result.containsKey("104"));
        assertFalse(result.containsKey("200"));
        assertFalse(result.containsKey("201"));
    }

    @Test
    void testNoLongReportingLines() {
        List<Employee> employees = List.of(
                new Employee("1", "Alice", "Smith", null, null),
                new Employee("2", "Bob", "Jones", null, "1"),
                new Employee("3", "Carol", "White", null, "2")
        );

        Map<String, Integer> result = analyzer.findEmployeesWithLongReportingLines(employees);

        assertTrue(result.isEmpty(), "No employees should have long reporting lines");
    }
}
