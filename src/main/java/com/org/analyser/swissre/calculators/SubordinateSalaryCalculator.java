package com.org.analyser.swissre.calculators;

import com.org.analyser.swissre.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SubordinateSalaryCalculator {

    private static final Logger log = LoggerFactory.getLogger(SubordinateSalaryCalculator.class);

    public Map<String, BigDecimal> calculate(List<Employee> employees) {

        Map<String, List<Employee>> employeesByManager =
                employees.stream()
                        .filter(emp -> emp.getManagerId() != null)
                        .collect(Collectors.groupingBy(Employee::getManagerId));

        Map<String, BigDecimal> avgSalaryMap = new HashMap<>();

        employeesByManager.forEach((managerId, subordinates) -> {

            BigDecimal totalSalary = subordinates.stream()
                    .map(Employee::getSalary)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal avg = totalSalary.divide(
                    BigDecimal.valueOf(subordinates.size()),
                    2,
                    RoundingMode.HALF_UP);

            avgSalaryMap.put(managerId, avg);
        });

        log.debug("Computed average subordinate salaries: {}", avgSalaryMap);
        return avgSalaryMap;
    }
}