package com.example.tviewcaserelated;

import com.example.tviewcaserelated.config.DatabaseConfig;
import com.example.tviewcaserelated.executor.LimitedQueryExecutor;
import com.example.tviewcaserelated.model.CaseRelatedData;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

/**
 * SQL Query Test Tool
 * 
 * Tests the Tviewcaserelated query by fetching limited rows per grade.
 * Use this to verify query correctness before deploying to React UI.
 * 
 * Default: 100 rows per grade (600 total for 6 grades)
 */
public class App {
    
    // Change this to fetch more/fewer rows per grade
    private static final int ROWS_PER_GRADE = 100;
    
    public static void main(String[] args) {
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║     Tviewcaserelated Query Tester                             ║
            ║     Limited Mode - For Testing Only                           ║
            ╚═══════════════════════════════════════════════════════════════╝
            """);
        
        int exitCode = 0;
        
        try {
            // Test connection
            if (!testConnection()) {
                System.exit(1);
            }
            
            // Run limited query
            LimitedQueryExecutor executor = new LimitedQueryExecutor(ROWS_PER_GRADE);
            List<CaseRelatedData> results = executor.executeParallel();
            
            // Show results
            printSampleResults(results);
            printStats(results);
            
            System.out.println("\n✓ Query executed successfully - safe to use in React UI");
            
        } catch (Exception e) {
            exitCode = 1;
            System.err.println("\n✗ ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.shutdown();
        }
        
        System.exit(exitCode);
    }
    
    private static boolean testConnection() {
        System.out.println("Testing connection...");
        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("  ✓ Connected to " + meta.getDatabaseProductName());
            return true;
        } catch (Exception e) {
            System.err.println("  ✗ Connection failed: " + e.getMessage());
            return false;
        }
    }
    
    private static void printSampleResults(List<CaseRelatedData> results) {
        System.out.println("\n--- Sample Results (first 10) ---");
        System.out.printf("%-10s %-12s %-6s %-6s %-8s %12s%n",
                "TINSID", "TIN", "GRADE", "STATUS", "CASECODE", "TOTASSD");
        System.out.println("-".repeat(60));
        
        int count = 0;
        for (CaseRelatedData r : results) {
            if (count++ >= 10) break;
            System.out.printf("%-10s %-12s %-6s %-6s %-8s %,12.2f%n",
                    r.getTinsid(),
                    maskTin(r.getTin()),
                    r.getCGrade(),
                    r.getStatus(),
                    r.getCasecode(),
                    r.getTotassd() != null ? r.getTotassd() : BigDecimal.ZERO);
        }
    }
    
    private static void printStats(List<CaseRelatedData> results) {
        System.out.println("\n--- Statistics ---");
        System.out.println("Total rows fetched: " + results.size());
        
        long open = results.stream().filter(r -> "O".equals(r.getStatus())).count();
        long closed = results.stream().filter(r -> "C".equals(r.getStatus())).count();
        System.out.println("Open: " + open + ", Closed: " + closed);
        
        BigDecimal total = results.stream()
                .map(CaseRelatedData::getTotassd)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.printf("Total Assessed (sample): $%,.2f%n", total);
    }
    
    private static String maskTin(String tin) {
        if (tin == null || tin.length() < 4) return "****";
        return "***-**-" + tin.substring(tin.length() - 4);
    }
}
