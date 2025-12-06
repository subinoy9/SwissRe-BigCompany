package org.bigcompany.controller;

import org.bigcompany.util.CsvReader;
import org.bigcompany.model.Employee;
import org.bigcompany.service.OrgAnalyzerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.nio.file.NoSuchFileException;
import java.io.FileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrgStructureController {

    private static final Logger log = LoggerFactory.getLogger(OrgStructureController.class);

    private OrgAnalyzerService orgService;
    private final CsvReader csvReader;

    public OrgStructureController(CsvReader csvReader) {
        this.csvReader = csvReader;
    }

    private OrgAnalyzerService initializeDefaultService() throws NoSuchFileException {
        try {
            List<Employee> employees = csvReader.read(null); // internally loads default file
            return new OrgAnalyzerService(employees);
        } catch (Exception e) {
            log.error("Failed to load default CSV", e);
            throw new NoSuchFileException("Default CSV loading failed: " + e.getMessage());
        }
    }

    @PostMapping("/import")
    public Map<String, Object> importCsv(@RequestParam(required = false) String file) {
        try {
            String msg;

            if (file == null || file.isBlank()) {
                this.orgService = initializeDefaultService();
                msg = "Default CSV loaded successfully.";
            } else {
                List<Employee> employees = csvReader.read(file);
                this.orgService = new OrgAnalyzerService(employees);
                msg = "CSV loaded from path: " + file;
            }

            logAnalysisResults(orgService);
            return Map.of("status", msg);

        } catch (Exception ex) {

            // Detect File Not Found
            if (ex instanceof FileNotFoundException ||
                    (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found"))) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "CSV file not found at path: " + file
                );
            }

            log.error("CSV load error", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "CSV load failed: " + ex.getMessage()
            );
        }
    }



    private void logAnalysisResults(OrgAnalyzerService svc) {
        log.info("--- UNDERPAID MANAGERS ---");
        Map<String, ?> underpaid = svc.managersUnderpaid();
        log.info(underpaid.isEmpty() ? "No underpaid managers" : underpaid.toString());

        log.info("--- OVERPAID MANAGERS ---");
        Map<String, ?> overpaid = svc.managersOverpaid();
        log.info(overpaid.isEmpty() ? "No overpaid managers" : overpaid.toString());

        log.info("--- LONG REPORTING LINES (>4) ---");
        Map<String, ?> longLines = svc.employeesWithTooLongReportingLines();
        log.info(longLines.isEmpty() ? "No long reporting lines" : longLines.toString());
    }

    @GetMapping("/managers/underpaid")
    public Map<String, Object> getUnderpaidManagers() {
        var underpaid = orgService.managersUnderpaid();

        var result = underpaid.entrySet().stream()
                .map(e -> Map.of(
                        "employeeId", e.getKey(),
                        "underpaidBy", e.getValue()
                ))
                .toList();

        return Map.of("underpaidManagers", result);
    }

    @GetMapping("/managers/overpaid")
    public Map<String, Object> getOverpaidManagers() {
        var overpaid = orgService.managersOverpaid();

        var result = overpaid.entrySet().stream()
                .map(e -> Map.of(
                        "employeeId", e.getKey(),
                        "overpaidBy", e.getValue()
                ))
                .toList();

        return Map.of("overpaidManagers", result);
    }

    @GetMapping("/employees/long-reporting-lines")
    public Map<String, Object> fetchLongReportingLines() {
        Map<String, Integer> tooLong = orgService.employeesWithTooLongReportingLines();

        var result = tooLong.entrySet().stream()
                .map(e -> Map.of(
                        "employeeId", e.getKey(),
                        "reportingLineLength", e.getValue()
                ))
                .toList();

        return Map.of("longReportingLines", result);
    }
}
