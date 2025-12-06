package com.org.analyser.swissre.api;

import com.org.analyser.swissre.lib.CsvReader;
import com.org.analyser.swissre.model.Employee;

import com.org.analyser.swissre.service.UnderpaidManagerAnalyzer;
import com.org.analyser.swissre.service.OverpaidManagerAnalyzer;
import com.org.analyser.swissre.service.ReportingLineAnalyzer;
import com.org.analyser.swissre.service.SubordinateSalaryCalculator;
import com.org.analyser.swissre.dto.FilePathRequestDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrgStructureApi {

    private static final Logger log = LoggerFactory.getLogger(OrgStructureApi.class);

    private final CsvReader csvReader;

    private List<Employee> employees;

    // analyzers
    private SubordinateSalaryCalculator salaryCalc;
    private UnderpaidManagerAnalyzer underpaidAnalyzer;
    private OverpaidManagerAnalyzer overpaidAnalyzer;
    private ReportingLineAnalyzer reportingAnalyzer;

    public OrgStructureApi(CsvReader csvReader) {
        this.csvReader = csvReader;
    }

    private void initializeAnalyzers(List<Employee> employees) {
        this.employees = employees;

        salaryCalc = new SubordinateSalaryCalculator();

        underpaidAnalyzer = new UnderpaidManagerAnalyzer(salaryCalc);
        overpaidAnalyzer = new OverpaidManagerAnalyzer(salaryCalc);
        reportingAnalyzer = new ReportingLineAnalyzer();
    }

    // ---------------------------------------
    //             IMPORT CSV
    // ---------------------------------------

    @PostMapping("/import")
    public Map<String, Object> importCsv(@RequestBody(required = false) FilePathRequestDto request) {

        try {
            String msg;

            String file = (request != null) ? request.getPath() : null;

            List<Employee> employeesLoaded =
                    (file == null || file.isBlank())
                            ? csvReader.read(null)
                            : csvReader.read(file);

            initializeAnalyzers(employeesLoaded);

            msg = (file == null || file.isBlank())
                    ? "Default CSV loaded successfully."
                    : "CSV loaded from path: " + file;

            logAnalysisResults();

            return Map.of("status", msg);

        } catch (Exception ex) {

            if (ex instanceof FileNotFoundException ||
                    (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found"))) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "CSV file not found at path: " + ((request != null) ? request.getPath() : null)
                );
            }

            log.error("CSV load error", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "CSV load failed: " + ex.getMessage()
            );
        }
    }

    // ---------------------------------------
    //            INTERNAL LOGGING
    // ---------------------------------------

    private void logAnalysisResults() {

        log.info("--- UNDERPAID MANAGERS ---");
        var underpaid = underpaidAnalyzer.findUnderpaidManagers(employees);
        log.info(underpaid.isEmpty() ? "No underpaid managers" : underpaid.toString());

        log.info("--- OVERPAID MANAGERS ---");
        var overpaid = overpaidAnalyzer.findOverpaidManagers(employees);
        log.info(overpaid.isEmpty() ? "No overpaid managers" : overpaid.toString());

        log.info("--- LONG REPORTING LINES (>4) ---");
        var longLines = reportingAnalyzer.findEmployeesWithLongReportingLines(employees);
        log.info(longLines.isEmpty() ? "No long reporting lines" : longLines.toString());
    }

    // ---------------------------------------
    //               ENDPOINTS
    // ---------------------------------------

    @GetMapping("/managers/underpaid")
    public Map<String, Object> getUnderpaidManagers() {

        var underpaid = underpaidAnalyzer.findUnderpaidManagers(employees);

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

        var overpaid = overpaidAnalyzer.findOverpaidManagers(employees);

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

        var tooLong = reportingAnalyzer.findEmployeesWithLongReportingLines(employees);

        var result = tooLong.entrySet().stream()
                .map(e -> Map.of(
                        "employeeId", e.getKey(),
                        "reportingLineLength", e.getValue()
                ))
                .toList();

        return Map.of("longReportingLines", result);
    }
}
