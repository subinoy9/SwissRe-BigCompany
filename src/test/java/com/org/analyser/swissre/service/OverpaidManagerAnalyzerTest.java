package com.org.analyser.swissre.service;

import com.org.analyser.swissre.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OverpaidManagerAnalyzerTest {

    private OverpaidManagerAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        SubordinateSalaryCalculator calculator = new SubordinateSalaryCalculator() {
            @Override
            public Map<String, BigDecimal> calculate(List<Employee> employees) {
                // return controlled average salary per manager
                return Map.of(
                        "1", new BigDecimal("50000"),
                        "2", new BigDecimal("30000")
                );
            }
        };

        analyzer = new OverpaidManagerAnalyzer(calculator);
    }

    @Test
    void testFindOverpaidManagers() {
        List<Employee> employees = List.of(
                new Employee("1", "John", "Doe", new BigDecimal("200000"), null),
                new Employee("2", "Alice", "Smith", new BigDecimal("40000"), "1"),
                new Employee("3", "Bob", "Ronstad", new BigDecimal("50000"), "1")
        );

        Map<String, BigDecimal> overpaid = analyzer.findOverpaidManagers(employees);

        assertNotNull(overpaid);
        assertEquals(1, overpaid.size());
        assertTrue(overpaid.containsKey("1"));
        assertEquals(new BigDecimal("125000.0"), overpaid.get("1"));
    }

    @Test
    void testNoOverpaidManagers() {
        List<Employee> employees = List.of(
                new Employee("1", "John", "Doe", new BigDecimal("75000"), null),
                new Employee("2", "Alice", "Smith", new BigDecimal("40000"), "1")
        );

        Map<String, BigDecimal> overpaid = analyzer.findOverpaidManagers(employees);

        assertTrue(overpaid.isEmpty(), "No managers should be overpaid");
    }
}
