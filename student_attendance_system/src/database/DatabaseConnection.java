package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/AttendanceSystem"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "nelshashrestha_620320"; 
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Unable to connect to the database", e);
        }
    }
}
