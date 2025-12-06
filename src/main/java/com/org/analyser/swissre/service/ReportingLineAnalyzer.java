package com.org.analyser.swissre.service;

import com.org.analyser.swissre.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ReportingLineAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(ReportingLineAnalyzer.class);

    public Map<String, Integer> findEmployeesWithLongReportingLines(List<Employee> employees) {

        Map<String, Employee> empMap =
                employees.stream().collect(Collectors.toMap(Employee::getId, e -> e));

        Map<String, Integer> longReportingLines = new HashMap<>();

        for (Employee emp : employees) {
            int depth = 0;
            String managerId = emp.getManagerId();

            while (managerId != null && empMap.containsKey(managerId)) {
                depth++;
                managerId = empMap.get(managerId).getManagerId();

                if (depth > 4) {
                    longReportingLines.put(emp.getId(), depth-4);
                    break;
                }
            }
        }

        log.info("Employees with long reporting lines (>4): {}", longReportingLines);
        return longReportingLines;
    }
}
