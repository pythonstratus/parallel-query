package com.example.tviewcaserelated.executor;

import com.example.tviewcaserelated.config.DatabaseConfig;
import com.example.tviewcaserelated.mapper.ResultSetMapper;
import com.example.tviewcaserelated.model.CaseRelatedData;
import com.example.tviewcaserelated.sql.SqlQueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Memory-efficient streaming query executor.
 * Processes results row-by-row without loading everything into memory.
 * 
 * USE THIS instead of CaseRelatedQueryExecutor for large result sets.
 */
public class StreamingQueryExecutor {
    
    private static final Logger logger = LoggerFactory.getLogger(StreamingQueryExecutor.class);
    
    private final int threadPoolSize;
    private final int fetchSize;
    private final int queryTimeoutSeconds;
    private final DataSource dataSource;
    private final ResultSetMapper mapper;
    
    public StreamingQueryExecutor() {
        this.threadPoolSize = DatabaseConfig.getIntProperty("executor.thread-pool-size", 4);
        this.fetchSize = DatabaseConfig.getIntProperty("executor.fetch-size", 500); // Smaller default
        this.queryTimeoutSeconds = DatabaseConfig.getIntProperty("executor.query-timeout", 600);
        this.dataSource = DatabaseConfig.getDataSource();
        this.mapper = new ResultSetMapper();
    }
    
    /**
     * Execute queries and process each row with a callback.
     * Does NOT store results in memory - processes and discards.
     * 
     * @param rowProcessor Callback to process each row
     * @return Total number of rows processed
     */
    public long executeParallelStreaming(Consumer<CaseRelatedData> rowProcessor) throws Exception {
        int[] grades = DatabaseConfig.getGrades();
        
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        AtomicLong totalRows = new AtomicLong(0);
        AtomicInteger completedGrades = new AtomicInteger(0);
        
        System.out.println("\n=== STREAMING MODE (Memory Efficient) ===");
        System.out.println("Processing grades: " + java.util.Arrays.toString(grades));
        System.out.println("Fetch size: " + fetchSize);
        System.out.println("Threads: " + threadPoolSize);
        System.out.println();
        
        long startTime = System.currentTimeMillis();
        
        // Submit streaming tasks
        CompletableFuture<?>[] futures = new CompletableFuture[grades.length];
        for (int i = 0; i < grades.length; i++) {
            final int grade = grades[i];
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    long count = streamForGrade(grade, rowProcessor);
                    totalRows.addAndGet(count);
                    int done = completedGrades.incrementAndGet();
                    System.out.printf("  [%d/%d] Grade %2d: %,d rows processed%n", 
                            done, grades.length, grade, count);
                } catch (SQLException e) {
                    logger.error("Error processing grade {}: {}", grade, e.getMessage());
                }
            }, executor);
        }
        
        // Wait for all to complete
        CompletableFuture.allOf(futures).join();
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("%n=== COMPLETE ===%n");
        System.out.printf("Total rows: %,d in %,d ms (%.0f rows/sec)%n", 
                totalRows.get(), totalTime, (totalRows.get() * 1000.0) / totalTime);
        
        return totalRows.get();
    }
    
    /**
     * Execute for single grade with streaming.
     * Returns count only, doesn't store data.
     */
    private long streamForGrade(int grade, Consumer<CaseRelatedData> rowProcessor) throws SQLException {
        long count = 0;
        
        try (Connection conn = dataSource.getConnection()) {
            // CRITICAL: Set these for streaming
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(
                    SqlQueries.TVIEWCASERELATED_QUERY,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY)) {
                
                stmt.setFetchSize(fetchSize);
                stmt.setQueryTimeout(queryTimeoutSeconds);
                stmt.setInt(1, grade);
                
                // Use streaming ResultSet
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        CaseRelatedData data = mapper.mapRow(rs);
                        if (rowProcessor != null) {
                            rowProcessor.accept(data);
                        }
                        count++;
                        
                        // Log progress every 10000 rows
                        if (count % 10000 == 0) {
                            logger.debug("Grade {}: processed {} rows...", grade, count);
                        }
                    }
                }
            }
        }
        
        return count;
    }
    
    /**
     * Just count rows without processing - minimal memory usage.
     */
    public long executeCountOnly() throws Exception {
        return executeParallelStreaming(null);
    }
    
    /**
     * Execute with a sample collector - only keeps first N rows.
     */
    public StreamingResult executeWithSample(int sampleSize) throws Exception {
        StreamingResult result = new StreamingResult(sampleSize);
        
        executeParallelStreaming(data -> {
            result.incrementCount();
            result.addToSampleIfRoom(data);
            result.addToTotals(data);
        });
        
        return result;
    }
    
    /**
     * Result holder that only keeps a sample in memory.
     */
    public static class StreamingResult {
        private final int maxSampleSize;
        private final java.util.List<CaseRelatedData> sample;
        private final AtomicLong totalCount = new AtomicLong(0);
        private final AtomicLong openCount = new AtomicLong(0);
        private final AtomicLong closedCount = new AtomicLong(0);
        private final java.util.concurrent.atomic.DoubleAdder totalAssessed = new java.util.concurrent.atomic.DoubleAdder();
        
        public StreamingResult(int maxSampleSize) {
            this.maxSampleSize = maxSampleSize;
            this.sample = java.util.Collections.synchronizedList(new java.util.ArrayList<>(maxSampleSize));
        }
        
        synchronized void addToSampleIfRoom(CaseRelatedData data) {
            if (sample.size() < maxSampleSize) {
                sample.add(data);
            }
        }
        
        void incrementCount() {
            totalCount.incrementAndGet();
        }
        
        void addToTotals(CaseRelatedData data) {
            if ("O".equals(data.getStatus())) {
                openCount.incrementAndGet();
            } else if ("C".equals(data.getStatus())) {
                closedCount.incrementAndGet();
            }
            if (data.getTotassd() != null) {
                totalAssessed.add(data.getTotassd().doubleValue());
            }
        }
        
        public java.util.List<CaseRelatedData> getSample() { return sample; }
        public long getTotalCount() { return totalCount.get(); }
        public long getOpenCount() { return openCount.get(); }
        public long getClosedCount() { return closedCount.get(); }
        public double getTotalAssessed() { return totalAssessed.sum(); }
    }
}
