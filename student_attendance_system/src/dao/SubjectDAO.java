package dao;

import database.DatabaseConnection;
import model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    // Fetch all subject names
    public List<String> getAllSubjectNames() {
        List<String> subjects = new ArrayList<>();
        String query = "SELECT subject_name FROM Subjects";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                subjects.add(rs.getString("subject_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subjects;
    }

    // ✅ NEW METHOD: Fetch subjects assigned to a specific student
    public List<Subject> getSubjectsByStudentId(int studentId) {
        List<Subject> subjects = new ArrayList<>();
        String query = """
            SELECT s.subject_id, s.subject_name
            FROM Subjects s
            INNER JOIN StudentSubjects ss ON s.subject_id = ss.subject_id
            WHERE ss.student_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Subject subject = new Subject();
                    subject.setSubjectId(rs.getInt("subject_id"));
                    subject.setSubjectName(rs.getString("subject_name"));
                    subjects.add(subject);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subjects;
    }

    // ✅ NEW METHOD: Get subject ID from name
    public int getSubjectIdByName(String subjectName) {
        String query = "SELECT subject_id FROM Subjects WHERE subject_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, subjectName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("subject_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return -1 if not found or error
        return -1;
    }
}
