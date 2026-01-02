package com.example.tviewcaserelated.executor;

import com.example.tviewcaserelated.config.DatabaseConfig;
import com.example.tviewcaserelated.mapper.ResultSetMapper;
import com.example.tviewcaserelated.model.CaseRelatedData;
import com.example.tviewcaserelated.model.ExecutionSummary;
import com.example.tviewcaserelated.model.QueryResult;
import com.example.tviewcaserelated.sql.SqlQueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Parallel query executor for Tviewcaserelated.sql.
 * Executes queries for different grade values concurrently using a thread pool.
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Parallel execution by grade value</li>
 *   <li>Configurable thread pool and fetch size</li>
 *   <li>Connection pooling via HikariCP</li>
 *   <li>Detailed execution metrics</li>
 *   <li>Timeout handling for long-running queries</li>
 * </ul>
 */
public class CaseRelatedQueryExecutor {
    
    private static final Logger logger = LoggerFactory.getLogger(CaseRelatedQueryExecutor.class);
    
    private final int threadPoolSize;
    private final int fetchSize;
    private final int queryTimeoutSeconds;
    private final DataSource dataSource;
    private final ResultSetMapper mapper;
    
    /**
     * Create executor with default configuration from properties.
     */
    public CaseRelatedQueryExecutor() {
        this.threadPoolSize = DatabaseConfig.getIntProperty("executor.thread-pool-size", 4);
        this.fetchSize = DatabaseConfig.getIntProperty("executor.fetch-size", 1000);
        this.queryTimeoutSeconds = DatabaseConfig.getIntProperty("executor.query-timeout", 300);
        this.dataSource = DatabaseConfig.getDataSource();
        this.mapper = new ResultSetMapper();
        
        logger.info("Executor initialized: threads={}, fetchSize={}, timeout={}s",
                threadPoolSize, fetchSize, queryTimeoutSeconds);
    }
    
    /**
     * Create executor with custom configuration.
     */
    public CaseRelatedQueryExecutor(int threadPoolSize, int fetchSize, int queryTimeoutSeconds) {
        this.threadPoolSize = threadPoolSize;
        this.fetchSize = fetchSize;
        this.queryTimeoutSeconds = queryTimeoutSeconds;
        this.dataSource = DatabaseConfig.getDataSource();
        this.mapper = new ResultSetMapper();
    }
    
    /**
     * Execute queries in parallel for all configured grades.
     *
     * @return List of all results from all grades
     * @throws Exception if execution fails
     */
    public List<CaseRelatedData> executeParallel() throws Exception {
        int[] grades = DatabaseConfig.getGrades();
        return executeParallel(grades);
    }
    
    /**
     * Execute queries in parallel for specified grades.
     *
     * @param grades Array of grade values to query
     * @return List of all results from all grades
     * @throws Exception if execution fails
     */
    public List<CaseRelatedData> executeParallel(int[] grades) throws Exception {
        logger.info("Starting parallel execution with {} threads for grades: {}",
                threadPoolSize, Arrays.toString(grades));
        
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize,
                r -> {
                    Thread t = new Thread(r);
                    t.setName("QueryExecutor-" + t.getId());
                    return t;
                });
        
        List<Future<QueryResult>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        // Submit tasks for each grade
        for (int grade : grades) {
            futures.add(executor.submit(() -> executeForGrade(grade)));
        }
        
        // Collect results
        List<CaseRelatedData> allResults = new ArrayList<>();
        List<QueryResult> queryResults = new ArrayList<>();
        int failedQueries = 0;
        
        for (Future<QueryResult> future : futures) {
            try {
                QueryResult result = future.get(queryTimeoutSeconds + 60, TimeUnit.SECONDS);
                allResults.addAll(result.getData());
                queryResults.add(result);
                
                logger.info("Grade {:2d}: {:>8,d} records in {:>6,d} ms (thread: {})",
                        result.getGrade(), result.getRecordCount(),
                        result.getExecutionTimeMs(), result.getThreadName());
                        
            } catch (TimeoutException e) {
                failedQueries++;
                logger.error("Query timed out after {} seconds", queryTimeoutSeconds + 60);
            } catch (ExecutionException e) {
                failedQueries++;
                logger.error("Query execution failed: {}", e.getCause().getMessage());
                if (logger.isDebugEnabled()) {
                    logger.debug("Stack trace:", e.getCause());
                }
            } catch (InterruptedException e) {
                failedQueries++;
                Thread.currentThread().interrupt();
                logger.error("Query interrupted");
            }
        }
        
        // Shutdown executor
        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            logger.warn("Executor did not terminate gracefully, forcing shutdown");
            executor.shutdownNow();
        }
        
        // Log summary
        long totalTime = System.currentTimeMillis() - startTime;
        ExecutionSummary summary = new ExecutionSummary(queryResults, totalTime, failedQueries);
        
        logger.info("Execution complete: {}", summary);
        logger.info("Pool stats: {}", DatabaseConfig.getPoolStats());
        
        return allResults;
    }
    
    /**
     * Execute query for a single grade value.
     * This method is called in parallel from the thread pool.
     *
     * @param grade The grade value to filter by
     * @return QueryResult containing data and metrics
     * @throws SQLException if database error occurs
     */
    private QueryResult executeForGrade(int grade) throws SQLException {
        List<CaseRelatedData> results = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        logger.debug("Starting query for grade {} on thread {}",
                grade, Thread.currentThread().getName());
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SqlQueries.TVIEWCASERELATED_QUERY)) {
            
            // Configure statement
            stmt.setFetchSize(fetchSize);
            stmt.setQueryTimeout(queryTimeoutSeconds);
            stmt.setInt(1, grade);
            
            // Execute and process results
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.mapRow(rs));
                }
            }
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.debug("Completed query for grade {}: {} records in {} ms",
                grade, results.size(), executionTime);
        
        return new QueryResult(grade, results, results.size(), executionTime);
    }
    
    /**
     * Execute query for a single grade (non-parallel, for testing).
     *
     * @param grade The grade value to query
     * @return List of results for that grade
     * @throws SQLException if database error occurs
     */
    public List<CaseRelatedData> executeSingle(int grade) throws SQLException {
        QueryResult result = executeForGrade(grade);
        return result.getData();
    }
    
    /**
     * Get count of records for a grade without fetching all data.
     *
     * @param grade The grade to count
     * @return Number of records
     * @throws SQLException if database error occurs
     */
    public int getCountForGrade(int grade) throws SQLException {
        String countQuery = "SELECT COUNT(*) FROM ENT WHERE grade = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(countQuery)) {
            
            stmt.setInt(1, grade);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
