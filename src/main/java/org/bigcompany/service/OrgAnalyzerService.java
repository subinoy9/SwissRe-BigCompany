package org.bigcompany.service;

import org.bigcompany.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrgAnalyzerService {

    private static final Logger log = LoggerFactory.getLogger(OrgAnalyzerService.class);

    private final List<Employee> employeeList;

    public OrgAnalyzerService(List<Employee> employees) {
        this.employeeList = employees;
    }

    /**
     * Computes average salary of subordinates for each manager.
     */
    private Map<String, BigDecimal> calculateAvgSubordinateSalary() {

        // Group employees by manager ID
        Map<String, List<Employee>> employeesByManager =
                employeeList.stream()
                        .filter(emp -> emp.getManagerId() != null)
                        .collect(Collectors.groupingBy(Employee::getManagerId));

        Map<String, BigDecimal> avgSalaryMap = new HashMap<>();

        employeesByManager.forEach((managerId, subordinates) -> {
            BigDecimal totalSalary = subordinates.stream()
                    .map(Employee::getSalary)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal avgSalary = totalSalary.divide(
                    BigDecimal.valueOf(subordinates.size()),
                    2,
                    RoundingMode.HALF_UP
            );

            avgSalaryMap.put(managerId, avgSalary);
        });

        log.debug("Computed average subordinate salaries: {}", avgSalaryMap);
        return avgSalaryMap;
    }

    /**
     * Managers earning less than 20% above subordinate average.
     */
    public Map<String, BigDecimal> managersUnderpaid() {

        Map<String, BigDecimal> avgMap = calculateAvgSubordinateSalary();
        Map<String, BigDecimal> underpaidManagers = new HashMap<>();

        for (Employee emp : employeeList) {
            if (!avgMap.containsKey(emp.getId())) continue;

            BigDecimal requiredMinimum = avgMap.get(emp.getId()).multiply(BigDecimal.valueOf(1.2));

            if (emp.getSalary().compareTo(requiredMinimum) < 0) {
                underpaidManagers.put(emp.getId(), requiredMinimum.subtract(emp.getSalary()));
            }
        }

        log.info("Underpaid managers: {}", underpaidManagers);
        return underpaidManagers;
    }

    /**
     * Managers earning more than 50% above subordinate average.
     */
    public Map<String, BigDecimal> managersOverpaid() {

        Map<String, BigDecimal> avgMap = calculateAvgSubordinateSalary();
        Map<String, BigDecimal> overpaidManagers = new HashMap<>();

        for (Employee emp : employeeList) {
            if (!avgMap.containsKey(emp.getId())) continue;

            BigDecimal maxAllowed = avgMap.get(emp.getId()).multiply(BigDecimal.valueOf(1.5));

            if (emp.getSalary().compareTo(maxAllowed) > 0) {
                overpaidManagers.put(emp.getId(), emp.getSalary().subtract(maxAllowed));
            }
        }

        log.info("Overpaid managers: {}", overpaidManagers);
        return overpaidManagers;
    }

    /**
     * Employees with reporting lines longer than 4 levels.
     */
    public Map<String, Integer> employeesWithTooLongReportingLines() {

        Map<String, Employee> empMap =
                employeeList.stream().collect(Collectors.toMap(Employee::getId, e -> e));

        Map<String, Integer> longReportingLines = new HashMap<>();

        for (Employee emp : employeeList) {

            int depth = 0;
            String managerId = emp.getManagerId();

            while (managerId != null && empMap.containsKey(managerId)) {
                depth++;
                managerId = empMap.get(managerId).getManagerId();

                if (depth > 4) {
                    longReportingLines.put(emp.getId(), depth);
                    break;
                }
            }
        }

        log.info("Employees with long reporting lines (>4): {}", longReportingLines);
        return longReportingLines;
    }
}
