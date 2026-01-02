package com.example.tviewcaserelated;

import com.example.tviewcaserelated.config.DatabaseConfig;
import com.example.tviewcaserelated.executor.CaseRelatedQueryExecutor;
import com.example.tviewcaserelated.model.CaseRelatedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main application entry point for Tviewcaserelated Parallel Query Executor.
 * 
 * <p>Usage:</p>
 * <pre>
 *   mvn exec:java
 *   java -jar tviewcaserelated-executor-1.0.0.jar
 * </pre>
 */
public class App {
    
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    
    private static final String BANNER = """
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║     Tviewcaserelated Parallel Query Executor v1.0.0           ║
            ║     Oracle Database Performance Testing Tool                   ║
            ╚═══════════════════════════════════════════════════════════════╝
            """;
    
    private static final String SEPARATOR = "═".repeat(65);
    
    public static void main(String[] args) {
        System.out.println(BANNER);
        
        int exitCode = 0;
        
        try {
            // Print configuration
            printConfiguration();
            
            // Test database connection
            if (!testConnection()) {
                System.err.println("Failed to connect to database. Please check configuration.");
                System.exit(1);
            }
            
            // Run parallel queries
            System.out.println("\n" + SEPARATOR);
            System.out.println("  EXECUTING PARALLEL QUERIES");
            System.out.println(SEPARATOR);
            
            CaseRelatedQueryExecutor executor = new CaseRelatedQueryExecutor();
            List<CaseRelatedData> results = executor.executeParallel();
            
            // Print results
            printSampleResults(results);
            printSummaryStatistics(results);
            
        } catch (Exception e) {
            exitCode = 1;
            System.err.println("\n*** EXECUTION ERROR ***");
            System.err.println("Message: " + e.getMessage());
            logger.error("Execution failed", e);
        } finally {
            // Clean up
            System.out.println("\n" + SEPARATOR);
            System.out.println("  CLEANUP");
            System.out.println(SEPARATOR);
            DatabaseConfig.shutdown();
            System.out.println("Application finished with exit code: " + exitCode);
        }
        
        System.exit(exitCode);
    }
    
    /**
     * Print current configuration settings.
     */
    private static void printConfiguration() {
        System.out.println(SEPARATOR);
        System.out.println("  CONFIGURATION");
        System.out.println(SEPARATOR);
        
        String dbUrl = DatabaseConfig.getProperty("db.url", "not configured");
        String dbUser = DatabaseConfig.getProperty("db.username", "not configured");
        int threadPool = DatabaseConfig.getIntProperty("executor.thread-pool-size", 4);
        int fetchSize = DatabaseConfig.getIntProperty("executor.fetch-size", 1000);
        int poolSize = DatabaseConfig.getIntProperty("db.pool.size", 10);
        String grades = DatabaseConfig.getProperty("query.grades", "4,5,7,11,12,13");
        
        System.out.printf("  %-25s %s%n", "Database URL:", maskPassword(dbUrl));
        System.out.printf("  %-25s %s%n", "Database User:", dbUser);
        System.out.printf("  %-25s %d%n", "Connection Pool Size:", poolSize);
        System.out.printf("  %-25s %d%n", "Thread Pool Size:", threadPool);
        System.out.printf("  %-25s %,d%n", "Fetch Size:", fetchSize);
        System.out.printf("  %-25s %s%n", "Grades to Process:", grades);
    }
    
    /**
     * Test database connectivity and print database info.
     */
    private static boolean testConnection() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("  DATABASE CONNECTION TEST");
        System.out.println(SEPARATOR);
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            
            System.out.printf("  %-25s %s%n", "Status:", "✓ CONNECTED");
            System.out.printf("  %-25s %s %s%n", "Database:", 
                    meta.getDatabaseProductName(), 
                    meta.getDatabaseProductVersion());
            System.out.printf("  %-25s %s %s%n", "JDBC Driver:", 
                    meta.getDriverName(), 
                    meta.getDriverVersion());
            System.out.printf("  %-25s %s%n", "Pool Stats:", 
                    DatabaseConfig.getPoolStats());
            
            logger.info("Database connection successful");
            return true;
            
        } catch (Exception e) {
            System.out.printf("  %-25s %s%n", "Status:", "✗ FAILED");
            System.out.printf("  %-25s %s%n", "Error:", e.getMessage());
            logger.error("Database connection failed", e);
            return false;
        }
    }
    
    /**
     * Print sample results in formatted table.
     */
    private static void printSampleResults(List<CaseRelatedData> results) {
        int sampleSize = DatabaseConfig.getIntProperty("output.sample-size", 10);
        
        System.out.println("\n" + SEPARATOR);
        System.out.println("  SAMPLE RESULTS (first " + sampleSize + ")");
        System.out.println(SEPARATOR);
        
        if (results.isEmpty()) {
            System.out.println("  No results returned.");
            return;
        }
        
        // Print header
        System.out.printf("  %-12s %-12s %-6s %-8s %-10s %15s%n",
                "TINSID", "TIN", "GRADE", "STATUS", "CASECODE", "TOTASSD");
        System.out.println("  " + "-".repeat(63));
        
        // Print rows
        int count = 0;
        for (CaseRelatedData row : results) {
            if (count++ >= sampleSize) break;
            
            System.out.printf("  %-12s %-12s %-6s %-8s %-10s %,15.2f%n",
                    row.getTinsid() != null ? row.getTinsid() : "N/A",
                    maskTin(row.getTin()),
                    row.getCGrade() != null ? row.getCGrade() : "N/A",
                    row.getStatus() != null ? row.getStatus() : "N/A",
                    row.getCasecode() != null ? row.getCasecode() : "N/A",
                    row.getTotassd() != null ? row.getTotassd() : BigDecimal.ZERO);
        }
        
        if (results.size() > sampleSize) {
            System.out.printf("%n  ... and %,d more records%n", results.size() - sampleSize);
        }
    }
    
    /**
     * Print summary statistics.
     */
    private static void printSummaryStatistics(List<CaseRelatedData> results) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("  SUMMARY STATISTICS");
        System.out.println(SEPARATOR);
        
        // Total records
        System.out.printf("  %-30s %,d%n", "Total Records:", results.size());
        
        // Count by status
        Map<String, Long> statusCounts = results.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getStatus() != null ? r.getStatus() : "UNKNOWN",
                        Collectors.counting()));
        
        System.out.println("\n  Records by Status:");
        statusCounts.forEach((status, count) -> 
                System.out.printf("    %-26s %,d%n", status + ":", count));
        
        // Count by grade
        Map<Integer, Long> gradeCounts = results.stream()
                .filter(r -> r.getCGrade() != null)
                .collect(Collectors.groupingBy(
                        CaseRelatedData::getCGrade,
                        Collectors.counting()));
        
        System.out.println("\n  Records by Grade:");
        gradeCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("    Grade %-21d %,d%n", e.getKey(), e.getValue()));
        
        // Financial summary
        BigDecimal totalAssessed = results.stream()
                .map(CaseRelatedData::getTotassd)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalBal941 = results.stream()
                .map(CaseRelatedData::getBal941)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("\n  Financial Summary:");
        System.out.printf("    %-26s $%,15.2f%n", "Total Assessed:", totalAssessed);
        System.out.printf("    %-26s $%,15.2f%n", "Total BAL_941:", totalBal941);
    }
    
    /**
     * Mask TIN for display (show only last 4 digits).
     */
    private static String maskTin(String tin) {
        if (tin == null || tin.length() < 4) {
            return "***-**-****";
        }
        return "***-**-" + tin.substring(tin.length() - 4);
    }
    
    /**
     * Mask password in JDBC URL if present.
     */
    private static String maskPassword(String url) {
        if (url == null) return "null";
        return url.replaceAll("password=[^&;]*", "password=****");
    }
}
