---

## Problems to Fix

### 1. **You need proper executor shutdown** ✅

In your `ExecutorConfig` (Image 1), you create executors but I don't see a shutdown hook.

Add this to your config:

```java
@Configuration
public class ExecutorConfig {
    
    public static final int TIMEVIEW_THREADPOOLSIZE = 4;
    public static final int TIMEVIEW_FETCH_SIZE = 1000;
    public static final int TIMEVIEW_TIMEOUT_SECONDS = 400;
    
    @Bean(name = "entityServiceExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(TIMEVIEW_THREADPOOLSIZE);
        threadPoolTaskExecutor.setMaxPoolSize(TIMEVIEW_THREADPOOLSIZE);
        threadPoolTaskExecutor.setQueueCapacity(25);
        threadPoolTaskExecutor.setThreadNamePrefix("EntityService-");
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);  // ADD THIS
        threadPoolTaskExecutor.setAwaitTerminationSeconds(60);              // ADD THIS
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
    
    @Bean(name = "TimeViewServiceExecutor")
    public ExecutorService timeViewServiceExecutor() {
        return Executors.newFixedThreadPool(TIMEVIEW_THREADPOOLSIZE, r -> {
            Thread t = new Thread(r);
            t.setName("EntityTimeViewQueryExecutorAsync-" + t.getId());
            t.setDaemon(true);  // ADD THIS - allows JVM to exit
            return t;
        });
    }
    
    // ADD THIS - Cleanup on shutdown
    @PreDestroy
    public void shutdown() {
        // Shutdown TimeViewServiceExecutor
        ExecutorService executor = timeViewServiceExecutor();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

---

### 2. **Don't load everything into memory for caching**

Your current approach:
```java
list.addAll(queryTimeViewData(caseSql, parameters, true));  // ALL rows into memory
```

This loads **millions of rows** into a `List` → OOM.

**Options:**

**Option A: Don't cache the full dataset**
```java
// Cache only counts or summaries, not full data
```

**Option B: Use pagination for cache warming**
```java
// Load and cache in chunks
for (int offset = 0; offset < totalCount; offset += 10000) {
    List<TimeView> chunk = queryWithPagination(grade, offset, 10000);
    cache.put("timeview-grade-" + grade + "-offset-" + offset, chunk);
}
```

**Option C: Use off-heap cache (Redis/Hazelcast)**
```java
// Don't store in JVM memory
redisTemplate.opsForList().rightPushAll("timeview:" + grade, chunk);
```

---

### 3. **Reduce fetch size if OOM persists**

```java
public static final int TIMEVIEW_FETCH_SIZE = 500;  // Reduce from 1000
```

---

### 4. **Add memory limits per query**

```java
private List<TimeView> queryTimeViewData(String sql, int grade, boolean isInTime) {
    
    final int MAX_ROWS = 100000;  // Prevent OOM
    
    return jdbcTemplate.getJdbcTemplate().query(
        sql,
        ps -> {
            ps.setFetchSize(500);
            ps.setMaxRows(MAX_ROWS);  // ADD THIS - hard limit
            ps.setInt(1, grade);
        },
        (rs, rowNum) -> mapRowToTimeView(rs, rowNum, isInTime)
    );
}
```

---

## Quick Summary

| Fix | Why |
|-----|-----|
| Add `@PreDestroy` shutdown | Clean up executor threads |
| Set `setDaemon(true)` | Allow JVM to exit if threads hang |
| Add `setMaxRows()` | Prevent OOM from huge result sets |
| Reduce fetch size to 500 | Less memory per batch |
| Consider pagination | Don't load millions of rows at once |

---

**What's your cache strategy?** Are you trying to cache all rows for all grades at startup? If so, that's the root cause of OOM - we need to rethink that approach.
