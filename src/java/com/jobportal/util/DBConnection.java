package com.jobportal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBConnection {

    // Defaults keep local development simple, while env vars enable cloud deployment.
    private static final String DEFAULT_DB_URL = "jdbc:mysql://127.0.0.1:3306/job_portal?allowPublicKeyRetrieval=true&sslMode=DISABLED&serverTimezone=UTC";
    private static final String DEFAULT_DB_USERNAME = "root";
    private static final String DEFAULT_DB_PASSWORD = "root";

    static {
        try {
            // Load MySQL JDBC driver once when class is initialized.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("MySQL JDBC driver not found.", ex);
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String dbUrl = getEnvOrDefault("DB_URL", DEFAULT_DB_URL);
        String dbUsername = getEnvOrDefault("DB_USERNAME", DEFAULT_DB_USERNAME);
        String dbPassword = getEnvOrDefault("DB_PASSWORD", DEFAULT_DB_PASSWORD);
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    private static String getEnvOrDefault(String envKey, String defaultValue) {
        String value = System.getenv(envKey);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    public static boolean insertApplication(
            String name,
            String email,
            String jobRole,
            String skills
    ) throws SQLException {
        try (Connection connection = getConnection()) {
            boolean statusColumnExists = hasStatusColumn(connection);
            String sql;
            if (statusColumnExists) {
                sql = "INSERT INTO applications "
                        + "(name, email, job_role, skills, status) "
                        + "VALUES (?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO applications "
                        + "(name, email, job_role, skills) "
                        + "VALUES (?, ?, ?, ?)";
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, jobRole);
                statement.setString(4, skills);
                if (statusColumnExists) {
                    statement.setString(5, "Pending");
                }
                return statement.executeUpdate() > 0;
            }
        }
    }

    public static List<Map<String, String>> searchApplications(String jobRole) throws SQLException {
        List<Map<String, String>> records = new ArrayList<>();
        String sql = "SELECT id, name, email, job_role, skills "
                + "FROM applications WHERE job_role = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, jobRole);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Keep result shape simple so JSP can render it directly.
                    Map<String, String> row = new HashMap<>();
                    row.put("id", String.valueOf(resultSet.getInt("id")));
                    row.put("name", resultSet.getString("name"));
                    row.put("email", resultSet.getString("email"));
                    row.put("job_role", resultSet.getString("job_role"));
                    row.put("skills", resultSet.getString("skills"));
                    records.add(row);
                }
            }
        }

        return records;
    }

    public static int getApplicationCount() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM applications";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        }
        return 0;
    }

    public static List<Map<String, String>> getApplicationStatus(String email, Integer applicationId)
            throws SQLException {
        List<Map<String, String>> records = new ArrayList<>();
        try (Connection connection = getConnection()) {
            boolean statusColumnExists = hasStatusColumn(connection);
            StringBuilder sql = new StringBuilder();
            if (statusColumnExists) {
                sql.append("SELECT id, name, email, job_role, status FROM applications WHERE 1=1");
            } else {
                sql.append("SELECT id, name, email, job_role FROM applications WHERE 1=1");
            }

            if (email != null && !email.trim().isEmpty()) {
                sql.append(" AND email = ?");
            }
            if (applicationId != null) {
                sql.append(" AND id = ?");
            }
            sql.append(" ORDER BY id DESC");

            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                int parameterIndex = 1;
                if (email != null && !email.trim().isEmpty()) {
                    statement.setString(parameterIndex++, email.trim());
                }
                if (applicationId != null) {
                    statement.setInt(parameterIndex, applicationId);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Map<String, String> row = new HashMap<>();
                        row.put("id", String.valueOf(resultSet.getInt("id")));
                        row.put("name", resultSet.getString("name"));
                        row.put("email", resultSet.getString("email"));
                        row.put("job_role", resultSet.getString("job_role"));
                        row.put("status", statusColumnExists
                                ? resultSet.getString("status")
                                : "Pending");
                        records.add(row);
                    }
                }
            }
        }

        return records;
    }

    private static boolean hasStatusColumn(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM information_schema.columns "
                + "WHERE table_schema = DATABASE() AND table_name = 'applications' AND column_name = 'status'";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("total") > 0;
            }
        }
        return false;
    }
}
