
/**
 *
 * @author alika
 */
package com.eduvo.uniconnectchat.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

public class UserDB {
    private static BasicDataSource dataSource;
    
    static {
        try {
            // Manually load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
        
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // Disable SSL and add connection parameters
        dataSource.setUrl("jdbc:mysql://localhost:3306/uniconnect?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("Lovetennis@16");
        dataSource.setMinIdle(5);
        dataSource.setMaxTotal(20);
        
        // Optional: Add connection validation
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(true);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static void testConnection() {
        System.out.println("=== DATABASE CONNECTION TEST ===");
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connection successful!");
                System.out.println("Database: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Version: " + conn.getMetaData().getDatabaseProductVersion());
                
                // Test if table exists
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet tables = meta.getTables(null, null, "users", new String[]{"TABLE"});
                if (tables.next()) {
                    System.out.println("Users table exists");
                } else {
                    System.out.println("Users table NOT found");
                }
            } else {
                System.out.println("Connection is null");
            }
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}