I can see the issue now. Looking at your code:

## The Problem

You have **two separate thread pools** that are **both modifying the same `jdbcTemplate` fetch size**:

**Image 2, lines 197-198:**
```java
jdbcTemplate.getJdbcTemplate().setFetchSize(ExecutorConfig.TIMEVIEW_FETCH_SIZE);
```

**Image 3, lines 209-210 (in finally block):**
```java
jdbcTemplate.getJdbcTemplate().setFetchSize(originalFetchSize);
```

When multiple threads run concurrently:
1. Thread A sets fetch size to 1000
2. Thread B sets fetch size to 1000
3. Thread A finishes, resets fetch size to original
4. **Thread B is still running but fetch size changed** → cursor/object invalidated
5. **ORA-08103: object no longer exists**

---

## The Fix

**Don't share or modify `jdbcTemplate` settings across threads.** Instead, use a connection-level fetch size.

### Option 1: Set fetch size per query (Recommended)

```java
private List<TimeView> queryTimeViewData(String sqlFile, 
        SqlParameterSource parameters, boolean isInTime) {
    
    return jdbcTemplate.query(
        sqlFile, 
        parameters,
        (PreparedStatement ps) -> {
            ps.setFetchSize(ExecutorConfig.TIMEVIEW_FETCH_SIZE);  // Per-statement, not global
        },
        new RowMapper<TimeView>() {
            @Override
            public TimeView mapRow(ResultSet rs, int rowNum) throws SQLException {
                // your mapping code
            }
        }
    );
}
```

### Option 2: Use PreparedStatementCreator

```java
private List<TimeView> queryTimeViewData(String sql, int grade, boolean isInTime) {
    
    return jdbcTemplate.getJdbcTemplate().query(
        con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setFetchSize(1000);  // Set on THIS statement only
            ps.setInt(1, grade);
            return ps;
        },
        (rs, rowNum) -> {
            // your row mapper
        }
    );
}
```

### Option 3: Remove the global fetch size modification entirely

```java
// DELETE these lines:
// jdbcTemplate.getJdbcTemplate().setFetchSize(ExecutorConfig.TIMEVIEW_FETCH_SIZE);

// DELETE the finally block that resets it:
// jdbcTemplate.getJdbcTemplate().setFetchSize(originalFetchSize);
```

---

## Summary

| Problem | `setFetchSize` on shared `jdbcTemplate` across threads |
|---------|--------------------------------------------------------|
| Cause | One thread resets fetch size while another is still reading |
| Fix | Set fetch size per `PreparedStatement`, not globally |

Would you like me to write the corrected version of your `TimeViewService` class?
