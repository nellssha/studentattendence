package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "nelshashrestha_620320"; // Replace with your MySQL password
    private static final String DATABASE_NAME = "Attendance_System"; // Specify database name here

    public static Connection getConnection() throws SQLException {
        System.out.println("Attempting to connect to the database...");

        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connected successfully!");

                // Create the database and tables
                createDatabaseAndTables(conn);
                return conn;
            } else {
                System.err.println("Failed to establish a database connection.");
                throw new SQLException("Unable to establish a database connection.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            throw new SQLException("Unable to connect to the database", e);
        }
    }

    private static void createDatabaseAndTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Create the database if it doesn't exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME + ";");
            stmt.executeUpdate("USE " + DATABASE_NAME + ";");

            // Create Users table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Users (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "full_name VARCHAR(100)," +
                    "date_of_birth DATE," +
                    "address VARCHAR(255)," +
                    "personal_email VARCHAR(100)," +
                    "college_email VARCHAR(100)," +
                    "phone_number VARCHAR(15)," +
                    "password VARCHAR(100)," +
                    "role ENUM('student', 'instructor', 'admin'));");

            // Create Subjects table with UNIQUE subject_name
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Subjects (" +
                    "subject_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "subject_name VARCHAR(100) UNIQUE);");

            // Create StudentSubjects table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS StudentSubjects (" +
                    "student_id INT NOT NULL," +
                    "subject_id INT NOT NULL," +
                    "PRIMARY KEY (student_id, subject_id)," +
                    "FOREIGN KEY (student_id) REFERENCES Users(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (subject_id) REFERENCES Subjects(subject_id) ON DELETE CASCADE);");

            // Create InstructorSubjects table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS InstructorSubjects (" +
                    "instructor_id INT NOT NULL," +
                    "subject_id INT NOT NULL," +
                    "PRIMARY KEY (instructor_id, subject_id)," +
                    "FOREIGN KEY (instructor_id) REFERENCES Users(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (subject_id) REFERENCES Subjects(subject_id) ON DELETE CASCADE);");

            // Create Attendance table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Attendance (" +
                    "attendance_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "student_id INT NOT NULL," +
                    "subject_id INT NOT NULL," +
                    "date DATE," +
                    "status ENUM('present', 'absent', 'leave')," +
                    "approved TINYINT(1)," +
                    "leave_reason VARCHAR(255)," +
                    "edit_count INT DEFAULT 0," +
                    "UNIQUE (student_id, subject_id, date)," +
                    "FOREIGN KEY (student_id) REFERENCES Users(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (subject_id) REFERENCES Subjects(subject_id) ON DELETE CASCADE);");

            // Insert Admin User
            stmt.executeUpdate("INSERT INTO Users (full_name, date_of_birth, address, personal_email, college_email, phone_number, password, role) VALUES " +
                    "('Admin User', '1990-01-01', '123 Admin Street', 'admin@gmail.com', 'admin@college.edu', '9777777777', 'admin123', 'admin') " +
                    "ON DUPLICATE KEY UPDATE user_id=user_id;");

            // Insert Default Subjects with trimmed and unique names
            stmt.executeUpdate("INSERT INTO Subjects (subject_name) VALUES " +
                    "('Math'), ('Physics'), ('Chemistry'), ('Computer'), ('English') " +
                    "ON DUPLICATE KEY UPDATE subject_id=subject_id;");

            // Insert Default Users
            stmt.executeUpdate("INSERT INTO Users (user_id, full_name, date_of_birth, address, personal_email, college_email, phone_number, password, role) VALUES " +
                    "(2, 'Nelsha Shrestha', '2005-07-03', '', 'nelshashrestha123@gmail.com', 'nelsha.shrestha@patancollege.edu.np', '9745653567', '123456', 'student'), " +
                    "(3, 'Ritika Maharjan', '1995-02-01', '', 'ritikamhrjn127@gmail.com', 'ritika.maharjan@patancollege.edu.np', '9841953785', 'ritika', 'instructor'), " +
                    "(4, 'Ava Gurung', '1996-09-08', '', 'tnssssd@gmail.com', 'ava@college.edu', '9851089330', 'avagurung', 'instructor'), " +
                    "(5, 'Lily Shrestha', '2006-09-03', '', 'lilylilithshrestha@gmail.com', 'lily@college.edu', '9843132433', 'lilyshrestha', 'student'), " +
                    "(6, 'Bigyan Shrestha', '1994-02-03', '', 'bigyan18shrestha@gmail.com', 'bigyan@college.edu', '9860850006', 'bigyan', 'student'), " +
                    "(7, 'Lenin Maharjan', '2000-04-05', '', 'leninmaharjan123@gmail.com', 'lenin.maharjan@patancollege.edu.np', '9876543219', 'lenin123', 'instructor') " +
                    "ON DUPLICATE KEY UPDATE user_id=user_id;");

            System.out.println("Database and tables created/initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Error executing SQL script: " + e.getMessage());
            throw new SQLException("Unable to execute SQL script", e);
        }
    }
}
