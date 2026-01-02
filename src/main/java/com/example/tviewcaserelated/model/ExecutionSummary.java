package com.example.tviewcaserelated.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aggregated execution summary with statistics.
 */
public class ExecutionSummary {
    
    private final int totalRecords;
    private final long totalTimeMs;
    private final int successfulQueries;
    private final int failedQueries;
    private final Map<Integer, QueryResult> resultsByGrade;
    
    public ExecutionSummary(List<QueryResult> results, long totalTimeMs, int failedQueries) {
        this.totalRecords = results.stream().mapToInt(QueryResult::getRecordCount).sum();
        this.totalTimeMs = totalTimeMs;
        this.successfulQueries = results.size();
        this.failedQueries = failedQueries;
        this.resultsByGrade = results.stream()
                .collect(Collectors.toMap(QueryResult::getGrade, r -> r));
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public long getTotalTimeMs() {
        return totalTimeMs;
    }

    public int getSuccessfulQueries() {
        return successfulQueries;
    }

    public int getFailedQueries() {
        return failedQueries;
    }

    public Map<Integer, QueryResult> getResultsByGrade() {
        return resultsByGrade;
    }
    
    public double getOverallRecordsPerSecond() {
        if (totalTimeMs == 0) return 0;
        return (totalRecords * 1000.0) / totalTimeMs;
    }
    
    /**
     * Calculate total assessed amount from all results.
     */
    public BigDecimal calculateTotalAssessed(List<CaseRelatedData> allData) {
        return allData.stream()
                .map(CaseRelatedData::getTotassd)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Count records by status.
     */
    public Map<String, Long> countByStatus(List<CaseRelatedData> allData) {
        return allData.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getStatus() != null ? d.getStatus() : "UNKNOWN",
                        Collectors.counting()));
    }

    @Override
    public String toString() {
        return String.format(
                "ExecutionSummary{totalRecords=%,d, totalTime=%,dms, throughput=%.2f rec/sec, " +
                "successful=%d, failed=%d}",
                totalRecords, totalTimeMs, getOverallRecordsPerSecond(),
                successfulQueries, failedQueries);
    }
}
