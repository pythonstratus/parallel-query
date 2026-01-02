package com.example.tviewcaserelated;

import com.example.tviewcaserelated.config.DatabaseConfig;
import com.example.tviewcaserelated.mapper.ResultSetMapper;
import com.example.tviewcaserelated.model.CaseRelatedData;
import com.example.tviewcaserelated.model.ExecutionSummary;
import com.example.tviewcaserelated.model.QueryResult;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Tviewcaserelated Executor components.
 */
@DisplayName("Tviewcaserelated Executor Tests")
class TviewcaserelatedTest {
    
    @Nested
    @DisplayName("CaseRelatedData Model Tests")
    class CaseRelatedDataTests {
        
        @Test
        @DisplayName("Should create empty CaseRelatedData")
        void testEmptyConstruction() {
            CaseRelatedData data = new CaseRelatedData();
            assertNotNull(data);
            assertNull(data.getTinsid());
            assertNull(data.getTin());
        }
        
        @Test
        @DisplayName("Should set and get all identity fields")
        void testIdentityFields() {
            CaseRelatedData data = new CaseRelatedData();
            
            data.setTinsid(12345L);
            data.setTin("123456789");
            data.setRoid("TESTROID");
            data.setSeid("TESTSEID");
            
            assertEquals(12345L, data.getTinsid());
            assertEquals("123456789", data.getTin());
            assertEquals("TESTROID", data.getRoid());
            assertEquals("TESTSEID", data.getSeid());
        }
        
        @Test
        @DisplayName("Should set and get financial fields")
        void testFinancialFields() {
            CaseRelatedData data = new CaseRelatedData();
            
            data.setTotassd(new BigDecimal("15000.50"));
            data.setBal941(new BigDecimal("5000.25"));
            data.setAgiAmt(new BigDecimal("75000.00"));
            
            assertEquals(new BigDecimal("15000.50"), data.getTotassd());
            assertEquals(new BigDecimal("5000.25"), data.getBal941());
            assertEquals(new BigDecimal("75000.00"), data.getAgiAmt());
        }
        
        @Test
        @DisplayName("Should mask TIN in toString")
        void testToStringMasksTin() {
            CaseRelatedData data = new CaseRelatedData();
            data.setTin("123456789");
            data.setTinsid(1L);
            
            String str = data.toString();
            assertFalse(str.contains("123456789"), "TIN should be masked");
            assertTrue(str.contains("6789"), "Last 4 digits should be visible");
        }
        
        @Test
        @DisplayName("Should implement equals and hashCode")
        void testEqualsAndHashCode() {
            CaseRelatedData data1 = new CaseRelatedData();
            data1.setTinsid(1L);
            data1.setTin("123456789");
            
            CaseRelatedData data2 = new CaseRelatedData();
            data2.setTinsid(1L);
            data2.setTin("123456789");
            
            assertEquals(data1, data2);
            assertEquals(data1.hashCode(), data2.hashCode());
        }
    }
    
    @Nested
    @DisplayName("QueryResult Tests")
    class QueryResultTests {
        
        @Test
        @DisplayName("Should calculate records per second")
        void testRecordsPerSecond() {
            List<CaseRelatedData> data = Arrays.asList(
                    new CaseRelatedData(),
                    new CaseRelatedData(),
                    new CaseRelatedData()
            );
            
            QueryResult result = new QueryResult(4, data, 3, 1000); // 3 records in 1000ms
            
            assertEquals(3.0, result.getRecordsPerSecond(), 0.001);
        }
        
        @Test
        @DisplayName("Should handle zero execution time")
        void testZeroExecutionTime() {
            QueryResult result = new QueryResult(4, List.of(), 0, 0);
            assertEquals(0.0, result.getRecordsPerSecond());
        }
        
        @Test
        @DisplayName("Should store thread name")
        void testThreadName() {
            QueryResult result = new QueryResult(5, List.of(), 0, 100);
            assertNotNull(result.getThreadName());
        }
    }
    
    @Nested
    @DisplayName("ExecutionSummary Tests")
    class ExecutionSummaryTests {
        
        @Test
        @DisplayName("Should aggregate results correctly")
        void testAggregation() {
            List<QueryResult> results = Arrays.asList(
                    new QueryResult(4, createMockData(100), 100, 500),
                    new QueryResult(5, createMockData(200), 200, 750),
                    new QueryResult(7, createMockData(150), 150, 600)
            );
            
            ExecutionSummary summary = new ExecutionSummary(results, 1000, 0);
            
            assertEquals(450, summary.getTotalRecords());
            assertEquals(1000, summary.getTotalTimeMs());
            assertEquals(3, summary.getSuccessfulQueries());
            assertEquals(0, summary.getFailedQueries());
        }
        
        @Test
        @DisplayName("Should calculate overall throughput")
        void testThroughput() {
            List<QueryResult> results = List.of(
                    new QueryResult(4, createMockData(1000), 1000, 500)
            );
            
            ExecutionSummary summary = new ExecutionSummary(results, 1000, 0);
            
            assertEquals(1000.0, summary.getOverallRecordsPerSecond(), 0.001);
        }
        
        private List<CaseRelatedData> createMockData(int count) {
            CaseRelatedData[] data = new CaseRelatedData[count];
            for (int i = 0; i < count; i++) {
                data[i] = new CaseRelatedData();
            }
            return Arrays.asList(data);
        }
    }
    
    @Nested
    @DisplayName("DatabaseConfig Tests")
    class DatabaseConfigTests {
        
        @Test
        @DisplayName("Should load configuration")
        void testConfigurationLoading() {
            assertTrue(DatabaseConfig.isInitialized());
        }
        
        @Test
        @DisplayName("Should parse grades from config")
        void testGradesParsing() {
            int[] grades = DatabaseConfig.getGrades();
            assertNotNull(grades);
            assertTrue(grades.length > 0);
        }
        
        @Test
        @DisplayName("Should return default for missing property")
        void testDefaultProperty() {
            String value = DatabaseConfig.getProperty("nonexistent.property", "default");
            assertEquals("default", value);
        }
        
        @Test
        @DisplayName("Should return default for missing int property")
        void testDefaultIntProperty() {
            int value = DatabaseConfig.getIntProperty("nonexistent.int", 42);
            assertEquals(42, value);
        }
    }
    
    @Nested
    @DisplayName("ResultSetMapper Tests")
    class ResultSetMapperTests {
        
        @Test
        @DisplayName("Should instantiate mapper")
        void testMapperCreation() {
            ResultSetMapper mapper = new ResultSetMapper();
            assertNotNull(mapper);
        }
    }
}
