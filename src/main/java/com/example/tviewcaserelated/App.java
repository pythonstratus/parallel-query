package com.example.tviewcaserelated;

import com.example.tviewcaserelated.config.DatabaseConfig;
import com.example.tviewcaserelated.executor.StreamingQueryExecutor;
import com.example.tviewcaserelated.executor.StreamingQueryExecutor.StreamingResult;
import com.example.tviewcaserelated.model.CaseRelatedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * Main application - STREAMING MODE for large datasets.
 * 
 * This version processes rows one at a time without storing them all in memory.
 * Use this for queries that return millions of rows.
 * 
 * Usage:
 *   mvn exec:java -Dexec.args="--streaming"
 *   java -Xmx512m -jar tviewcaserelated-executor-1.0.0.jar
 */
public class App {
    
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    
    private static final String BANNER = """
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║     Tviewcaserelated Parallel Query Executor v1.1.0           ║
            ║     STREAMING MODE - Memory Efficient                          ║
            ╚═══════════════════════════════════════════════════════════════╝
            """;
    
    private static final String SEPARATOR = "═".repeat(65);
    
    public static void main(String[] args) {
        System.out.println(BANNER);
        
        // Print JVM memory info
        printMemoryInfo();
        
        int exitCode = 0;
        
        try {
            // Print configuration
            printConfiguration();
            
            // Test database connection
            if (!testConnection()) {
                System.err.println("Failed to connect to database.");
                System.exit(1);
            }
            
            // Run STREAMING queries - memory efficient
            System.out.println("\n" + SEPARATOR);
            System.out.println("  EXECUTING STREAMING QUERIES");
            System.out.println(SEPARATOR);
            
            StreamingQueryExecutor executor = new StreamingQueryExecutor();
            
            // Only keep 10 sample rows in memory, but count everything
            StreamingResult result = executor.executeWithSample(10);
            
            // Print results
            printSampleResults(result);
            printSummaryStatistics(result);
            
            // Print final memory usage
            printMemoryInfo();
            
        } catch (Exception e) {
            exitCode = 1;
            System.err.println("\n*** EXECUTION ERROR ***");
            System.err.println("Message: " + e.getMessage());
            logger.error("Execution failed", e);
            e.printStackTrace();
        } finally {
            System.out.println("\n" + SEPARATOR);
            System.out.println("  CLEANUP");
            System.out.println(SEPARATOR);
            DatabaseConfig.shutdown();
            System.out.println("Exit code: " + exitCode);
        }
        
        System.exit(exitCode);
    }
    
    private static void printMemoryInfo() {
        Runtime rt = Runtime.getRuntime();
        long totalMB = rt.totalMemory() / (1024 * 1024);
        long freeMB = rt.freeMemory() / (1024 * 1024);
        long usedMB = totalMB - freeMB;
        long maxMB = rt.maxMemory() / (1024 * 1024);
        
        System.out.println("\n--- JVM Memory ---");
        System.out.printf("  Used: %,d MB / Max: %,d MB (%.1f%%)%n", 
                usedMB, maxMB, (usedMB * 100.0) / maxMB);
    }
    
    private static void printConfiguration() {
        System.out.println(SEPARATOR);
        System.out.println("  CONFIGURATION");
        System.out.println(SEPARATOR);
        
        String dbUrl = DatabaseConfig.getProperty("db.url", "not configured");
        String dbUser = DatabaseConfig.getProperty("db.username", "not configured");
        int threadPool = DatabaseConfig.getIntProperty("executor.thread-pool-size", 4);
        int fetchSize = DatabaseConfig.getIntProperty("executor.fetch-size", 500);
        String grades = DatabaseConfig.getProperty("query.grades", "4,5,7,11,12,13");
        
        System.out.printf("  %-25s %s%n", "Database URL:", maskUrl(dbUrl));
        System.out.printf("  %-25s %s%n", "Database User:", dbUser);
        System.out.printf("  %-25s %d%n", "Thread Pool Size:", threadPool);
        System.out.printf("  %-25s %,d%n", "Fetch Size:", fetchSize);
        System.out.printf("  %-25s %s%n", "Grades:", grades);
        System.out.printf("  %-25s %s%n", "Mode:", "STREAMING (memory efficient)");
    }
    
    private static boolean testConnection() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("  DATABASE CONNECTION TEST");
        System.out.println(SEPARATOR);
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.printf("  %-25s %s%n", "Status:", "✓ CONNECTED");
            System.out.printf("  %-25s %s%n", "Database:", meta.getDatabaseProductName());
            return true;
        } catch (Exception e) {
            System.out.printf("  %-25s %s%n", "Status:", "✗ FAILED");
            System.out.printf("  %-25s %s%n", "Error:", e.getMessage());
            return false;
        }
    }
    
    private static void printSampleResults(StreamingResult result) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("  SAMPLE RESULTS (first " + result.getSample().size() + ")");
        System.out.println(SEPARATOR);
        
        if (result.getSample().isEmpty()) {
            System.out.println("  No results.");
            return;
        }
        
        System.out.printf("  %-12s %-12s %-6s %-8s %-10s %15s%n",
                "TINSID", "TIN", "GRADE", "STATUS", "CASECODE", "TOTASSD");
        System.out.println("  " + "-".repeat(63));
        
        for (CaseRelatedData row : result.getSample()) {
            System.out.printf("  %-12s %-12s %-6s %-8s %-10s %,15.2f%n",
                    row.getTinsid() != null ? row.getTinsid() : "N/A",
                    maskTin(row.getTin()),
                    row.getCGrade() != null ? row.getCGrade() : "N/A",
                    row.getStatus() != null ? row.getStatus() : "N/A",
                    row.getCasecode() != null ? row.getCasecode() : "N/A",
                    row.getTotassd() != null ? row.getTotassd() : BigDecimal.ZERO);
        }
    }
    
    private static void printSummaryStatistics(StreamingResult result) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("  SUMMARY STATISTICS");
        System.out.println(SEPARATOR);
        
        System.out.printf("  %-30s %,d%n", "Total Records:", result.getTotalCount());
        System.out.printf("  %-30s %,d%n", "Open (O):", result.getOpenCount());
        System.out.printf("  %-30s %,d%n", "Closed (C):", result.getClosedCount());
        System.out.printf("  %-30s $%,.2f%n", "Total Assessed:", result.getTotalAssessed());
    }
    
    private static String maskTin(String tin) {
        if (tin == null || tin.length() < 4) return "***-**-****";
        return "***-**-" + tin.substring(tin.length() - 4);
    }
    
    private static String maskUrl(String url) {
        if (url == null) return "null";
        // Just show host portion
        return url.length() > 50 ? url.substring(0, 50) + "..." : url;
    }
}
