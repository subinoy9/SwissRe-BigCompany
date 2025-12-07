package com.org.analyser.swissre.service;

import com.org.analyser.swissre.model.Employee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SubordinateSalaryCalculatorTest {

    private List<Employee> sampleEmployees() {
        return List.of(
                new Employee("100", "Emma", "Stone", new BigDecimal("150000"), null),
                new Employee("101", "Liam", "Grant", new BigDecimal("90000"), "100"),
                new Employee("102", "Olivia", "Turner", new BigDecimal("85000"), "101"),
                new Employee("103", "Noah", "Blake", new BigDecimal("40000"), "102"),
                new Employee("104", "Ava", "Shields", new BigDecimal("38000"), "103"),
                new Employee("105", "Isla", "Rowe", new BigDecimal("36000"), "104"),
                new Employee("106", "Ethan", "Cross", new BigDecimal("34000"), "105"),
                new Employee("200", "Mason", "Reed", new BigDecimal("60000"), "101"),
                new Employee("201", "Sophia", "Hayes", new BigDecimal("150000"), "101"),
                new Employee("300", "Lucas", "Perry", new BigDecimal("40000"), "200"),
                new Employee("301", "Chloe", "Irwin", new BigDecimal("55000"), "200"),
                new Employee("400", "Henry", "Walsh", new BigDecimal("30000"), "300")
        );
    }

    @Test
    public void testCalculateAverageSalaries() {
        SubordinateSalaryCalculator calculator = new SubordinateSalaryCalculator();

        Map<String, BigDecimal> avgMap = calculator.calculate(sampleEmployees());

        assertNotNull(avgMap);

        assertEquals(new BigDecimal("90000.00"), avgMap.get("100"));

        assertEquals(new BigDecimal("98333.33"), avgMap.get("101"));

        assertEquals(new BigDecimal("47500.00"), avgMap.get("200"));

        assertEquals(new BigDecimal("40000.00"), avgMap.get("102"));

        assertEquals(new BigDecimal("38000.00"), avgMap.get("103"));

        assertEquals(new BigDecimal("36000.00"), avgMap.get("104"));

        assertEquals(new BigDecimal("34000.00"), avgMap.get("105"));

        assertEquals(new BigDecimal("30000.00"), avgMap.get("300"));

        // Managers with no direct subordinates should not appear
        assertFalse(avgMap.containsKey("106"));
        assertFalse(avgMap.containsKey("201"));
        assertFalse(avgMap.containsKey("400"));
    }
}
