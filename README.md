# Tviewcaserelated Parallel Query Executor

A high-performance Java application for parallel execution of the optimized `Tviewcaserelated.sql` query against Oracle databases.

## Features

- **Parallel Execution**: Queries for different grade values run concurrently
- **Connection Pooling**: HikariCP for efficient database connection management
- **Configurable**: All settings via `application.properties`
- **Comprehensive Logging**: SLF4J-based logging with configurable levels
- **Metrics**: Detailed execution timing and throughput statistics
- **Clean Architecture**: Separation of concerns with config, model, mapper, executor packages

## Requirements

- **JDK 17** or higher
- **Maven 3.6+**
- **Oracle Database** access
- Network connectivity to database server

## Project Structure

```
tviewcaserelated-executor/
├── pom.xml                                    # Maven build configuration
├── README.md                                  # This file
├── src/
│   ├── main/
│   │   ├── java/com/example/tviewcaserelated/
│   │   │   ├── App.java                       # Main entry point
│   │   │   ├── config/
│   │   │   │   └── DatabaseConfig.java        # HikariCP configuration
│   │   │   ├── executor/
│   │   │   │   └── CaseRelatedQueryExecutor.java  # Parallel executor
│   │   │   ├── mapper/
│   │   │   │   └── ResultSetMapper.java       # ResultSet to POJO mapper
│   │   │   ├── model/
│   │   │   │   ├── CaseRelatedData.java       # 97-column data model
│   │   │   │   ├── QueryResult.java           # Result with metadata
│   │   │   │   └── ExecutionSummary.java      # Aggregated statistics
│   │   │   └── sql/
│   │   │       └── SqlQueries.java            # SQL query constants
│   │   └── resources/
│   │       ├── application.properties         # Configuration file
│   │       └── simplelogger.properties        # Logging configuration
│   └── test/
│       └── java/com/example/tviewcaserelated/
│           └── TviewcaserelatedTest.java      # Unit tests
```

## Quick Start

### 1. Extract the Archive

```bash
tar -xvf tviewcaserelated-executor.tar
cd tviewcaserelated-executor
```

### 2. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
# Database Connection
db.url=jdbc:oracle:thin:@//your-host:1521/YOUR_SERVICE
db.username=your_username
db.password=your_password

# Execution Settings
executor.thread-pool-size=4
executor.fetch-size=1000
query.grades=4,5,7,11,12,13
```

### 3. Build the Project

```bash
mvn clean package
```

### 4. Run the Application

**Option A: Using Maven**
```bash
mvn exec:java
```

**Option B: Using the JAR**
```bash
java -jar target/tviewcaserelated-executor-1.0.0.jar
```

## Expected Output

```
╔═══════════════════════════════════════════════════════════════╗
║     Tviewcaserelated Parallel Query Executor v1.0.0           ║
║     Oracle Database Performance Testing Tool                   ║
╚═══════════════════════════════════════════════════════════════╝

═════════════════════════════════════════════════════════════════
  CONFIGURATION
═════════════════════════════════════════════════════════════════
  Database URL:           jdbc:oracle:thin:@//host:1521/service
  Database User:          username
  Connection Pool Size:   10
  Thread Pool Size:       4
  Fetch Size:             1,000
  Grades to Process:      4,5,7,11,12,13

═════════════════════════════════════════════════════════════════
  DATABASE CONNECTION TEST
═════════════════════════════════════════════════════════════════
  Status:                 ✓ CONNECTED
  Database:               Oracle Database 19c Enterprise Edition
  JDBC Driver:            Oracle JDBC driver 23.3.0.23.09
  Pool Stats:             Pool[active=1, idle=4, waiting=0, total=5]

═════════════════════════════════════════════════════════════════
  EXECUTING PARALLEL QUERIES
═════════════════════════════════════════════════════════════════
  Grade  4:   15,234 records in  2,341 ms (thread: QueryExecutor-1)
  Grade  5:   23,456 records in  3,567 ms (thread: QueryExecutor-2)
  Grade  7:   12,345 records in  1,890 ms (thread: QueryExecutor-3)
  Grade 11:   45,678 records in  5,123 ms (thread: QueryExecutor-4)
  Grade 12:   34,567 records in  4,567 ms (thread: QueryExecutor-1)
  Grade 13:   28,901 records in  3,890 ms (thread: QueryExecutor-2)

═════════════════════════════════════════════════════════════════
  SAMPLE RESULTS (first 10)
═════════════════════════════════════════════════════════════════
  TINSID       TIN          GRADE  STATUS   CASECODE        TOTASSD
  ---------------------------------------------------------------
  12345678     ***-**-1234  4      O        201              15,234.56
  ...

═════════════════════════════════════════════════════════════════
  SUMMARY STATISTICS
═════════════════════════════════════════════════════════════════
  Total Records:                   160,181

  Records by Status:
    O:                             95,234
    C:                             64,947

  Records by Grade:
    Grade 4:                       15,234
    Grade 5:                       23,456
    ...

  Financial Summary:
    Total Assessed:                $ 1,234,567,890.00
    Total BAL_941:                 $   234,567,890.00
```

## Configuration Reference

| Property | Description | Default |
|----------|-------------|---------|
| `db.url` | Oracle JDBC URL | `jdbc:oracle:thin:@//localhost:1521/ORCL` |
| `db.username` | Database username | - |
| `db.password` | Database password | - |
| `db.pool.size` | Max connections in pool | `10` |
| `db.pool.min-idle` | Min idle connections | `5` |
| `db.pool.max-lifetime` | Max connection lifetime (ms) | `1800000` |
| `db.pool.connection-timeout` | Connection timeout (ms) | `30000` |
| `executor.thread-pool-size` | Parallel execution threads | `4` |
| `executor.fetch-size` | JDBC fetch size | `1000` |
| `executor.query-timeout` | Query timeout (seconds) | `300` |
| `query.grades` | Grades to process (comma-separated) | `4,5,7,11,12,13` |
| `output.sample-size` | Sample records to display | `10` |

## Performance Tuning

### Thread Pool Size
- Set to match CPU cores for CPU-bound workloads
- Consider database connection limits
- Recommendation: `4-8` for most systems

### Fetch Size
- Higher values = fewer network round trips
- Higher values = more memory usage
- Recommendation: `1000-5000` for large result sets

### Connection Pool
- Should be >= thread pool size
- Add 2-3 extra for overhead
- Recommendation: `thread-pool-size + 3`

## SQL Query Optimizations

The embedded query includes these optimizations:

1. **CTEs with MATERIALIZE hints** - Pre-filter data early
2. **Scalar subquery → JOIN conversion** - Better execution plans
3. **EXISTS → LEFT JOIN conversion** - Avoid correlated subqueries
4. **Explicit table aliases** - Avoid ORA-00918 ambiguity errors
5. **FIRST_ROWS(500) hint** - Optimize for pagination

## Troubleshooting

### ORA-12505: TNS listener does not currently know of SID
- Check service name vs SID in JDBC URL
- Try: `jdbc:oracle:thin:@//host:1521/SERVICE_NAME`

### Connection Timeout
- Verify network connectivity: `telnet host 1521`
- Check firewall rules
- Increase `db.pool.connection-timeout`

### Out of Memory
- Reduce `executor.fetch-size`
- Reduce `executor.thread-pool-size`
- Increase JVM heap: `java -Xmx4g -jar ...`

### Query Timeout
- Increase `executor.query-timeout`
- Check database performance
- Review execution plan in TOAD

## Running Tests

```bash
mvn test
```

## Building Distribution

```bash
# Create fat JAR with all dependencies
mvn clean package

# The JAR will be at:
# target/tviewcaserelated-executor-1.0.0.jar
```

## License

Internal use only.
