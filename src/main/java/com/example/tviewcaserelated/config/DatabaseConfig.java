package com.example.tviewcaserelated.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Database configuration using HikariCP connection pool.
 * Provides optimized connection management for parallel query execution.
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Connection pooling with HikariCP</li>
 *   <li>Configurable via application.properties</li>
 *   <li>Oracle-specific optimizations</li>
 *   <li>Thread-safe singleton pattern</li>
 * </ul>
 */
public class DatabaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    
    private static volatile HikariDataSource dataSource;
    private static final Properties properties = new Properties();
    private static boolean initialized = false;
    
    // Static initializer to load properties
    static {
        loadProperties();
    }
    
    private DatabaseConfig() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Load configuration from application.properties file.
     */
    private static void loadProperties() {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                initialized = true;
                logger.info("Configuration loaded from application.properties");
            } else {
                logger.warn("application.properties not found, using defaults");
            }
        } catch (IOException e) {
            logger.error("Error loading properties: {}", e.getMessage());
        }
    }
    
    /**
     * Get the configured DataSource (creates if not exists).
     * Uses double-checked locking for thread safety.
     * 
     * @return HikariDataSource configured for Oracle
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DatabaseConfig.class) {
                if (dataSource == null) {
                    dataSource = createDataSource();
                }
            }
        }
        return dataSource;
    }
    
    /**
     * Create and configure HikariCP DataSource.
     */
    private static HikariDataSource createDataSource() {
        logger.info("Initializing HikariCP connection pool...");
        
        HikariConfig config = new HikariConfig();
        
        // Basic connection settings
        config.setJdbcUrl(getProperty("db.url", "jdbc:oracle:thin:@//localhost:1521/ORCL"));
        config.setUsername(getProperty("db.username", ""));
        config.setPassword(getProperty("db.password", ""));
        
        // Pool sizing
        config.setMaximumPoolSize(getIntProperty("db.pool.size", 10));
        config.setMinimumIdle(getIntProperty("db.pool.min-idle", 5));
        
        // Timeouts
        config.setMaxLifetime(getLongProperty("db.pool.max-lifetime", 1800000));
        config.setConnectionTimeout(getLongProperty("db.pool.connection-timeout", 30000));
        config.setIdleTimeout(getLongProperty("db.pool.idle-timeout", 600000));
        
        // Oracle-specific optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        
        // Oracle implicit statement caching
        config.addDataSourceProperty("oracle.jdbc.implicitStatementCacheSize", "100");
        
        // Pool naming for monitoring
        config.setPoolName("TviewcaserelatedPool");
        
        // Validation
        config.setConnectionTestQuery("SELECT 1 FROM DUAL");
        
        logger.info("Connection pool configured: URL={}, MaxPool={}, MinIdle={}", 
            config.getJdbcUrl(), config.getMaximumPoolSize(), config.getMinimumIdle());
        
        return new HikariDataSource(config);
    }
    
    /**
     * Get string property with default value.
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get integer property with default value.
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer for {}: {}, using default: {}", key, value, defaultValue);
            }
        }
        return defaultValue;
    }
    
    /**
     * Get long property with default value.
     */
    public static long getLongProperty(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value != null && !value.isEmpty()) {
            try {
                return Long.parseLong(value.trim());
            } catch (NumberFormatException e) {
                logger.warn("Invalid long for {}: {}, using default: {}", key, value, defaultValue);
            }
        }
        return defaultValue;
    }
    
    /**
     * Get boolean property with default value.
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null && !value.isEmpty()) {
            return Boolean.parseBoolean(value.trim());
        }
        return defaultValue;
    }
    
    /**
     * Parse grade values from configuration.
     * 
     * @return array of grade integers
     */
    public static int[] getGrades() {
        String gradesStr = properties.getProperty("query.grades", "4,5,7,11,12,13");
        String[] parts = gradesStr.split(",");
        int[] grades = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            grades[i] = Integer.parseInt(parts[i].trim());
        }
        logger.debug("Configured grades: {}", gradesStr);
        return grades;
    }
    
    /**
     * Check if configuration is loaded successfully.
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Get pool statistics for monitoring.
     */
    public static String getPoolStats() {
        if (dataSource != null) {
            return String.format("Pool[active=%d, idle=%d, waiting=%d, total=%d]",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection(),
                dataSource.getHikariPoolMXBean().getTotalConnections());
        }
        return "Pool not initialized";
    }
    
    /**
     * Gracefully shutdown the connection pool.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Shutting down connection pool...");
            logger.info("Final pool stats: {}", getPoolStats());
            dataSource.close();
            logger.info("Connection pool closed successfully");
        }
    }
}
