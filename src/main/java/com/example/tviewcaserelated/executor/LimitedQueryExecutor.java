package com.example.tviewcaserelated.executor;

import com.example.tviewcaserelated.config.DatabaseConfig;
import com.example.tviewcaserelated.mapper.ResultSetMapper;
import com.example.tviewcaserelated.model.CaseRelatedData;
import com.example.tviewcaserelated.sql.SqlQueries;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Limited Query Executor - For TESTING purposes only.
 * Fetches a limited number of rows per grade to validate query correctness.
 * 
 * Use this to:
 * - Verify SQL has no errors (ORA-xxxxx)
 * - See sample data
 * - Test without loading millions of rows
 */
public class LimitedQueryExecutor {
    
    private final int threadPoolSize;
    private final int rowLimitPerGrade;
    private final DataSource dataSource;
    private final ResultSetMapper mapper;
    
    /**
     * Create executor with default 100 rows per grade limit.
     */
    public LimitedQueryExecutor() {
        this(100);
    }
    
    /**
     * Create executor with specified row limit per grade.
     * 
     * @param rowLimitPerGrade Max rows to fetch per grade (e.g., 100)
     */
    public LimitedQueryExecutor(int rowLimitPerGrade) {
        this.threadPoolSize = DatabaseConfig.getIntProperty("executor.thread-pool-size", 4);
        this.rowLimitPerGrade = rowLimitPerGrade;
        this.dataSource = DatabaseConfig.getDataSource();
        this.mapper = new ResultSetMapper();
    }
    
    /**
     * Execute queries with row limit per grade.
     */
    public List<CaseRelatedData> executeParallel() throws Exception {
        int[] grades = DatabaseConfig.getGrades();
        
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        List<Future<GradeResult>> futures = new ArrayList<>();
        
        System.out.println("\n=== LIMITED MODE (Testing) ===");
        System.out.println("Max rows per grade: " + rowLimitPerGrade);
        System.out.println("Grades: " + java.util.Arrays.toString(grades));
        System.out.println();
        
        long startTime = System.currentTimeMillis();
        
        for (int grade : grades) {
            futures.add(executor.submit(() -> executeForGrade(grade)));
        }
        
        // Collect results
        List<CaseRelatedData> allResults = new ArrayList<>();
        
        for (Future<GradeResult> future : futures) {
            try {
                GradeResult result = future.get(5, TimeUnit.MINUTES);
                allResults.addAll(result.rows);
                System.out.printf("  Grade %2d: %,d rows fetched in %,d ms%n",
                        result.grade, result.rows.size(), result.timeMs);
            } catch (Exception e) {
                System.err.println("  Error: " + e.getMessage());
            }
        }
        
        executor.shutdown();
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("%nTotal: %,d rows in %,d ms%n", allResults.size(), totalTime);
        
        return allResults;
    }
    
    /**
     * Execute for single grade with row limit.
     */
    private GradeResult executeForGrade(int grade) throws SQLException {
        List<CaseRelatedData> rows = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SqlQueries.TVIEWCASERELATED_QUERY)) {
            
            stmt.setFetchSize(Math.min(rowLimitPerGrade, 500));
            stmt.setInt(1, grade);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next() && count < rowLimitPerGrade) {
                    rows.add(mapper.mapRow(rs));
                    count++;
                }
            }
        }
        
        long timeMs = System.currentTimeMillis() - startTime;
        return new GradeResult(grade, rows, timeMs);
    }
    
    private static class GradeResult {
        final int grade;
        final List<CaseRelatedData> rows;
        final long timeMs;
        
        GradeResult(int grade, List<CaseRelatedData> rows, long timeMs) {
            this.grade = grade;
            this.rows = rows;
            this.timeMs = timeMs;
        }
    }
}
