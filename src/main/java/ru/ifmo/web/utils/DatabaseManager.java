package ru.ifmo.web.utils;

import ru.ifmo.web.models.Result;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static final String TABLE_NAME = "lab3_results";

    private static final String DATASOURCE_JNDI = "java:jboss/datasources/ExampleDS";


    private static DataSource getDataSource() throws SQLException {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(DATASOURCE_JNDI);
            LOGGER.log(Level.INFO, "DataSource obtained successfully: {0}", DATASOURCE_JNDI);
            return ds;
        } catch (NamingException e) {
            LOGGER.log(Level.SEVERE, "DataSource lookup failed: " + DATASOURCE_JNDI, e);
            throw new SQLException("DataSource lookup failed", e);
        }
    }


    private static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static boolean saveResult(Result result) {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (x, y, r, hit, created_at, execution_time, user_session) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, result.getX());
            stmt.setDouble(2, result.getY());
            stmt.setDouble(3, result.getR());
            stmt.setBoolean(4, result.isHit());
            stmt.setTimestamp(5, Timestamp.valueOf(result.getCurrentTime()));
            stmt.setLong(6, result.getExecutionTime());
            stmt.setString(7, result.getUserSession());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        result.setId(generatedKeys.getLong(1));
                    }
                }
                LOGGER.log(Level.INFO, "Result saved successfully: {0}", result);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving result to database", e);
            e.printStackTrace();
        }

        return false;
    }

    public static List<Result> getResultsBySession(String sessionId) {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME +
                " WHERE user_session = ? ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Result result = extractResultFromResultSet(rs);
                    results.add(result);
                }
            }

            LOGGER.log(Level.INFO, "Retrieved {0} results for session {1}",
                    new Object[]{results.size(), sessionId});

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving results from database", e);
            e.printStackTrace();
        }

        return results;
    }

    public static List<Result> getAllResults() {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY created_at DESC LIMIT 1000";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Result result = extractResultFromResultSet(rs);
                results.add(result);
            }

            LOGGER.log(Level.INFO, "Retrieved {0} total results", results.size());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all results from database", e);
            e.printStackTrace();
        }

        return results;
    }

    public static boolean clearResultsBySession(String sessionId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE user_session = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId);
            int deletedRows = stmt.executeUpdate();

            LOGGER.log(Level.INFO, "Deleted {0} results for session {1}",
                    new Object[]{deletedRows, sessionId});
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing results from database", e);
            e.printStackTrace();
        }

        return false;
    }

    private static Result extractResultFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        double x = rs.getDouble("x");
        double y = rs.getDouble("y");
        double r = rs.getDouble("r");
        boolean hit = rs.getBoolean("hit");
        LocalDateTime currentTime = rs.getTimestamp("created_at").toLocalDateTime();
        long executionTime = rs.getLong("execution_time");
        String userSession = rs.getString("user_session");

        return new Result(id, x, y, r, hit, currentTime, executionTime, userSession);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            LOGGER.log(Level.INFO, "Database connection successful");
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection failed", e);
            e.printStackTrace();
            return false;
        }
    }
}
