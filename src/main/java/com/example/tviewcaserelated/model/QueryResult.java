package com.example.tviewcaserelated.model;

import java.util.List;

/**
 * Wrapper class for query results with execution metadata.
 * Used to track per-grade execution statistics.
 */
public class QueryResult {
    
    private final int grade;
    private final List<CaseRelatedData> data;
    private final int recordCount;
    private final long executionTimeMs;
    private final String threadName;
    
    /**
     * Create a new QueryResult.
     *
     * @param grade The grade value that was queried
     * @param data The list of results
     * @param recordCount Number of records returned
     * @param executionTimeMs Execution time in milliseconds
     */
    public QueryResult(int grade, List<CaseRelatedData> data, int recordCount, long executionTimeMs) {
        this.grade = grade;
        this.data = data;
        this.recordCount = recordCount;
        this.executionTimeMs = executionTimeMs;
        this.threadName = Thread.currentThread().getName();
    }

    public int getGrade() {
        return grade;
    }

    public List<CaseRelatedData> getData() {
        return data;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public String getThreadName() {
        return threadName;
    }
    
    /**
     * Calculate records per second throughput.
     */
    public double getRecordsPerSecond() {
        if (executionTimeMs == 0) return 0;
        return (recordCount * 1000.0) / executionTimeMs;
    }

    @Override
    public String toString() {
        return String.format("QueryResult{grade=%d, records=%,d, time=%,dms, thread=%s}",
                grade, recordCount, executionTimeMs, threadName);
    }
}
