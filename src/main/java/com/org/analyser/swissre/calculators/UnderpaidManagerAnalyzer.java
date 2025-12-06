package com.org.analyser.swissre.calculators;

import com.org.analyser.swissre.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class UnderpaidManagerAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(UnderpaidManagerAnalyzer.class);

    private final SubordinateSalaryCalculator calculator;

    public UnderpaidManagerAnalyzer(SubordinateSalaryCalculator calculator) {
        this.calculator = calculator;
    }

    public Map<String, BigDecimal> findUnderpaidManagers(List<Employee> employees) {

        Map<String, BigDecimal> avgMap = calculator.calculate(employees);
        Map<String, BigDecimal> underpaid = new HashMap<>();

        for (Employee emp : employees) {
            if (!avgMap.containsKey(emp.getId())) continue;

            BigDecimal requiredMinimum = avgMap.get(emp.getId()).multiply(BigDecimal.valueOf(1.2));

            if (emp.getSalary().compareTo(requiredMinimum) < 0) {
                underpaid.put(emp.getId(), requiredMinimum.subtract(emp.getSalary()));
            }
        }

        log.info("Underpaid managers: {}", underpaid);
        return underpaid;
    }
}