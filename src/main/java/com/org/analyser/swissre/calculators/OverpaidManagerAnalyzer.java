package com.org.analyser.swissre.calculators;

import com.org.analyser.swissre.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class OverpaidManagerAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(OverpaidManagerAnalyzer.class);

    private final SubordinateSalaryCalculator calculator;

    public OverpaidManagerAnalyzer(SubordinateSalaryCalculator calculator) {
        this.calculator = calculator;
    }

    public Map<String, BigDecimal> findOverpaidManagers(List<Employee> employees) {

        Map<String, BigDecimal> avgMap = calculator.calculate(employees);
        Map<String, BigDecimal> overpaid = new HashMap<>();

        for (Employee emp : employees) {
            if (!avgMap.containsKey(emp.getId())) continue;

            BigDecimal maxAllowed = avgMap.get(emp.getId()).multiply(BigDecimal.valueOf(1.5));

            if (emp.getSalary().compareTo(maxAllowed) > 0) {
                overpaid.put(emp.getId(), emp.getSalary().subtract(maxAllowed));
            }
        }

        log.info("Overpaid managers: {}", overpaid);
        return overpaid;
    }
}
