package com.org.analyser.swissre.service;

import com.org.analyser.swissre.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UnderpaidManagerAnalyzerTest {

    private UnderpaidManagerAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        SubordinateSalaryCalculator calculator = new SubordinateSalaryCalculator();
        analyzer = new UnderpaidManagerAnalyzer(calculator);
    }

    private List<Employee> sampleEmployeesUnderpaid() {
        return List.of(
                // CEO
                new Employee("100", "CEO", "Smith", new BigDecimal("200000"), null),
                // Managers under CEO (set salaries below 1.2 × avg subordinates)
                new Employee("101", "Alice", "Johnson", new BigDecimal("19000"), "100"), // underpaid
                new Employee("102", "Bob", "Brown", new BigDecimal("17000"), "100"),     // underpaid
                // Subordinates under Alice
                new Employee("103", "Charlie", "Davis", new BigDecimal("10000"), "101"),
                new Employee("104", "Diana", "Miller", new BigDecimal("10000"), "101"),
                // Subordinates under Bob
                new Employee("105", "Evan", "Wilson", new BigDecimal("15000"), "102"),
                new Employee("106", "Fiona", "Taylor", new BigDecimal("15000"), "102")
        );
    }

    private List<Employee> sampleEmployeesNoUnderpaid() {
        return List.of(
                // CEO
                new Employee("100", "CEO", "Smith", new BigDecimal("200000"), null),
                // Managers under CEO (set salaries above 1.2 × avg subordinates)
                new Employee("101", "Alice", "Johnson", new BigDecimal("25000"), "100"),
                new Employee("102", "Bob", "Brown", new BigDecimal("35000"), "100"),
                // Subordinates under Alice
                new Employee("103", "Charlie", "Davis", new BigDecimal("10000"), "101"),
                new Employee("104", "Diana", "Miller", new BigDecimal("10000"), "101"),
                // Subordinates under Bob
                new Employee("105", "Evan", "Wilson", new BigDecimal("15000"), "102"),
                new Employee("106", "Fiona", "Taylor", new BigDecimal("15000"), "102")
        );
    }

    @Test
    public void testFindUnderpaidManagers_NonEmpty() {
        List<Employee> employees = sampleEmployeesUnderpaid();

        Map<String, BigDecimal> underpaid = analyzer.findUnderpaidManagers(employees);

        assertFalse(underpaid.isEmpty(), "Expected some underpaid managers");
        assertTrue(underpaid.containsKey("102"), "Bob should be underpaid");
        assertEquals(new BigDecimal("1000.00"), underpaid.get("102").setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void testFindUnderpaidManagers_Empty() {
        List<Employee> employees = sampleEmployeesNoUnderpaid();

        Map<String, BigDecimal> underpaid = analyzer.findUnderpaidManagers(employees);

        assertTrue(underpaid.isEmpty(), "Expected no underpaid managers");
    }
}
