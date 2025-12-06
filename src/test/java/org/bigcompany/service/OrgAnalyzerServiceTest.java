package org.bigcompany.service;

import org.bigcompany.model.Employee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OrgAnalyzerServiceTest {

    private List<Employee> sampleEmployees() {
        return List.of(
                new Employee("E01", "Arjun", "Mehta", new BigDecimal("60000"), null),
                new Employee("E11", "Priya", "Nair", new BigDecimal("145000"), "E01"),
                new Employee("E12", "Kabir", "Das", new BigDecimal("47000"), "E01"),
                new Employee("E21", "Neha", "Rastogi", new BigDecimal("50000"), "E11"),
                new Employee("E22", "Rohan", "Kulkarni", new BigDecimal("34000"), "E21"),
                new Employee("E23", "Sameer", "Iqbal", new BigDecimal("34000"), "E22"),
                new Employee("E24","Lata","Sharma",new BigDecimal("20000"),"E23")
        );
    }

    @Test
    public void underpaidManagersShouldBeIdentified() {
        OrgAnalyzerService svc = new OrgAnalyzerService(sampleEmployees());

        Map<String, BigDecimal> underpaid = svc.managersUnderpaid();

        // Expected: E01 and E22 are below their required 20% threshold
        assertTrue(underpaid.containsKey("E01"));
        assertTrue(underpaid.containsKey("E22"));

        assertEquals(new BigDecimal("55200.000"), underpaid.get("E01"));
        assertEquals(new BigDecimal("6800.000"), underpaid.get("E22"));
    }

    @Test
    public void overpaidManagersShouldBeReported() {
        OrgAnalyzerService svc = new OrgAnalyzerService(sampleEmployees());

        Map<String, BigDecimal> overpaid = svc.managersOverpaid();

        // Expected: E11 and E23 are above the 50% limit
        assertTrue(overpaid.containsKey("E11"));
        assertTrue(overpaid.containsKey("E23"));

        assertEquals(new BigDecimal("70000.000"), overpaid.get("E11"));
        assertEquals(new BigDecimal("4000.000"), overpaid.get("E23"));
    }

    @Test
    public void reportingChainsLongerThanAllowedShouldBeDetected() {
        OrgAnalyzerService svc = new OrgAnalyzerService(sampleEmployees());

        Map<String, Integer> chain = svc.employeesWithTooLongReportingLines();

        assertNotNull(chain);
        assertTrue(chain.containsKey("E24"));
        assertEquals(5, chain.get("E24"));
    }
}
