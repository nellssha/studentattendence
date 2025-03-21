package dao;

import model.Attendance;
import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    // Method to get attendance by student ID
    public List<Attendance> getAttendanceByStudentId(int studentId) {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM Attendance WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setAttendanceId(rs.getInt("attendance_id"));
                attendance.setStudentId(rs.getInt("student_id"));
                attendance.setSubject(rs.getString("subject"));
                attendance.setDate(rs.getString("date"));
                attendance.setStatus(rs.getString("status"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceList;
    }

    // Method to get all attendance records
    public List<Attendance> getAllAttendance() {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM Attendance";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setAttendanceId(rs.getInt("attendance_id"));
                attendance.setStudentId(rs.getInt("student_id"));
                attendance.setSubject(rs.getString("subject"));
                attendance.setDate(rs.getString("date"));
                attendance.setStatus(rs.getString("status"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceList;
    }

    // Method to mark attendance
    public void markAttendance(Attendance attendance) {
        String query = "INSERT INTO Attendance (student_id, subject, date, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, attendance.getStudentId());
            stmt.setString(2, attendance.getSubject());
            stmt.setString(3, attendance.getDate());
            stmt.setString(4, attendance.getStatus());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to filter attendance records
    public List<Attendance> getFilteredAttendance(String studentName, String subject, String date) {
        List<Attendance> attendanceList = new ArrayList<>();
        StringBuilder query = new StringBuilder( "SELECT a.attendance_id, a.student_id, a.subject, a.date, a.status, u.full_name " +
                "FROM Attendance a " +
                "JOIN Users u ON a.student_id = u.user_id WHERE 1=1"
            );

        if (studentName != null && !studentName.isEmpty()) {
            query.append(" AND u.full_name LIKE ?");
        }
        if (subject != null && !subject.isEmpty()) {
            query.append(" AND a.subject = ?");
        }
        if (date != null && !date.isEmpty()) {
            query.append(" AND a.date = ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;

            if (studentName != null && !studentName.isEmpty()) {
                stmt.setString(paramIndex++, "%" + studentName + "%");
            }
            if (subject != null && !subject.isEmpty()) {
                stmt.setString(paramIndex++, subject);
            }
            if (date != null && !date.isEmpty()) {
                stmt.setString(paramIndex++, date);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setAttendanceId(rs.getInt("attendance_id"));
                attendance.setStudentId(rs.getInt("student_id"));
                attendance.setSubject(rs.getString("subject"));
                attendance.setDate(rs.getString("date"));
                attendance.setStatus(rs.getString("status"));
                attendance.setStudentName(rs.getString("full_name"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceList;
    }
}
