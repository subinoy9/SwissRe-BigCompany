package org.bigcompany.util;

import org.bigcompany.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvReader {

    private static final Logger log = LoggerFactory.getLogger(CsvReader.class);

    /**
     * Reads CSV from classpath (default) or from a client-provided file path.
     */
    public List<Employee> read(String filePath) {
        try {
            if (filePath == null || filePath.isBlank()) {
                log.info("Loading default employees.csv from resources...");
                return readFromResource("employees.csv");
            } else {
                log.info("Loading CSV from provided path: {}", filePath);
                return readFromDisk(filePath);
            }
        } catch (Exception e) {
            log.error("Failed to read CSV: {}", e.getMessage(), e);
            throw new RuntimeException("CSV read failure: " + e.getMessage(), e);
        }
    }

    /**
     * Reads a CSV file from an absolute disk path.
     */
    private List<Employee> readFromDisk(String path) throws IOException {
        File file = new File(path);

        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("CSV file not found at path: " + path);
        }

        log.debug("Reading CSV from disk file: {}", path);
        return parse(Files.newBufferedReader(file.toPath()));
    }

    /**
     * Reads a CSV file packaged in the application's resources.
     */
    private List<Employee> readFromResource(String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource(filename);

        if (!resource.exists()) {
            throw new FileNotFoundException("Default resource '" + filename + "' is missing.");
        }

        log.debug("Reading CSV from classpath resource: {}", filename);
        return parse(new BufferedReader(new InputStreamReader(resource.getInputStream())));
    }

    /**
     * Parses CSV file into a list of Employee objects.
     */
    private List<Employee> parse(BufferedReader reader) throws IOException {
        List<Employee> employees = new ArrayList<>();

        String line;

        // Skip header line
        reader.readLine();

        while ((line = reader.readLine()) != null) {

            if (line.isBlank()) continue;

            String[] cols = line.split(",");

            if (cols.length < 4) {
                log.warn("Skipping invalid CSV row: {}", line);
                continue;
            }

            String id = cols[0].trim();
            String firstName = cols[1].trim();
            String lastName = cols[2].trim();
            BigDecimal salary = new BigDecimal(cols[3].trim());

            String managerId = (cols.length > 4 && !cols[4].isBlank())
                    ? cols[4].trim()
                    : null;

            employees.add(new Employee(id, firstName, lastName, salary, managerId));
        }

        log.info("Parsed {} employees from CSV", employees.size());
        return employees;
    }
}
