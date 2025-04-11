package dao;

import database.DatabaseConnection;
import model.User;
import model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectAssignmentDAO {

    // Assign subject to student using subject name
	public boolean assignSubjectToStudent(int studentId, int subjectId) {
	    String checkQuery = "SELECT COUNT(*) FROM StudentSubjects WHERE student_id = ? AND subject_id = ?";
	    String insertQuery = "INSERT INTO StudentSubjects (student_id, subject_id) VALUES (?, ?)";

	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

	        checkStmt.setInt(1, studentId);
	        checkStmt.setInt(2, subjectId);
	        ResultSet rs = checkStmt.executeQuery();

	        if (rs.next() && rs.getInt(1) > 0) {
	            // Already exists
	            return false;
	        }

	        // Safe to insert
	        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
	            insertStmt.setInt(1, studentId);
	            insertStmt.setInt(2, subjectId);
	            insertStmt.executeUpdate();
	            return true;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return false;
	}


    // Assign subject to instructor using subject name
    public boolean assignSubjectToInstructor(int instructorId, String subjectName) {
        int subjectId = getSubjectIdByName(subjectName);
        if (subjectId == -1) return false;

        String query = "INSERT INTO InstructorSubjects (instructor_id, subject_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, instructorId);
            stmt.setInt(2, subjectId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all students
    public List<User> getAllStudents() {
        List<User> students = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE role = 'student'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setCollegeEmail(rs.getString("college_email"));
                students.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // Get all instructors
    public List<User> getAllInstructors() {
        List<User> instructors = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE role = 'instructor'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setCollegeEmail(rs.getString("college_email"));
                instructors.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructors;
    }

    // Get all subject names
    public List<String> getAllSubjectNames() {
        List<String> subjectNames = new ArrayList<>();
        String query = "SELECT subject_name FROM Subjects";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                subjectNames.add(rs.getString("subject_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjectNames;
    }

    // Get subject ID by name
    public int getSubjectIdByName(String subjectName) {
        String query = "SELECT subject_id FROM Subjects WHERE subject_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, subjectName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("subject_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }

    // Optional: Get all subjects as Subject objects
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        String query = "SELECT * FROM Subjects";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setSubjectId(rs.getInt("subject_id"));
                subject.setSubjectName(rs.getString("subject_name"));
                subjects.add(subject);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }
    public boolean assignSubjectToStudent(int studentId, String subjectName) {
        int subjectId = getSubjectIdByName(subjectName);
        if (subjectId == -1) return false;

        return assignSubjectToStudent(studentId, subjectId);
    }

}
