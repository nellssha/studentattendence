package dao;

import database.DatabaseConnection;
import model.Attendance;
import model.User;
import utils.EmailSender;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public List<Attendance> getAllAttendance() {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT a.*, u.full_name as student_name, s.subject_name FROM Attendance a " +
                       "JOIN Users u ON a.student_id = u.user_id " +
                       "JOIN Subjects s ON a.subject_id = s.subject_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                attendanceList.add(mapResultSetToAttendance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceList;
    }

    public List<Attendance> getFilteredAttendance(String studentIdentifier, String subjectName, String date) {
        List<Attendance> attendanceList = new ArrayList<>();
        StringBuilder query = new StringBuilder(
            "SELECT a.*, u.full_name as student_name, s.subject_name FROM Attendance a " +
            "JOIN Users u ON a.student_id = u.user_id " +
            "JOIN Subjects s ON a.subject_id = s.subject_id WHERE 1=1"
        );
        List<Object> parameters = new ArrayList<>();

        if (studentIdentifier != null && !studentIdentifier.isEmpty()) {
            try {
                int studentId = Integer.parseInt(studentIdentifier);
                query.append(" AND a.student_id = ?");
                parameters.add(studentId);
            } catch (NumberFormatException e) {
                query.append(" AND u.full_name LIKE ?");
                parameters.add("%" + studentIdentifier + "%");
            }
        }

        if (subjectName != null && !subjectName.isEmpty()) {
            query.append(" AND s.subject_name = ?");
            parameters.add(subjectName);
        }

        if (date != null && !date.isEmpty()) {
            query.append(" AND a.date = ?");
            parameters.add(date);
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                attendanceList.add(mapResultSetToAttendance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceList;
    }

    public boolean markAttendance(Attendance attendance) {
        String query = "INSERT INTO Attendance (student_id, subject_id, date, status, approved, leave_reason) " +
                       "VALUES (?, ?, ?, ?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE status = VALUES(status), approved = VALUES(approved), leave_reason = VALUES(leave_reason)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, attendance.getStudentId());
            stmt.setInt(2, attendance.getSubjectId());
            stmt.setString(3, attendance.getDate());
            stmt.setString(4, attendance.getStatus());

            if (attendance.isApproved() != null) {
                stmt.setBoolean(5, attendance.isApproved());
            } else {
                stmt.setNull(5, Types.BOOLEAN);
            }

            if (attendance.getLeaveReason() != null) {
                stmt.setString(6, attendance.getLeaveReason());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateOrCreateAttendance(Attendance attendance) {
        return markAttendance(attendance);
    }

    public boolean approveLeave(int attendanceId) {
        String query = "UPDATE Attendance SET approved = TRUE " +
                       "WHERE attendance_id = ? AND status = 'Leave' AND (approved IS NULL OR approved = FALSE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, attendanceId);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                // ✅ Send email notification after successful approval
                String studentEmail = getStudentEmailByAttendanceId(attendanceId);
                if (studentEmail != null) {
                    String subject = "Leave Request Approved";
                    String message = "Dear Student,\n\nYour leave request has been approved.\n\nRegards,\nAdmin";
                    utils.EmailSender.sendEmail(studentEmail, subject, message);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean rejectLeave(int attendanceId) {
        String query = "UPDATE Attendance SET approved = FALSE " +
                       "WHERE attendance_id = ? AND status = 'Leave' AND (approved IS NULL OR approved = TRUE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, attendanceId);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                // ✅ Send email notification after rejection
                String studentEmail = getStudentEmailByAttendanceId(attendanceId);
                if (studentEmail != null) {
                    String subject = "Leave Request Rejected";
                    String message = "Dear Student,\n\nYour leave request has been rejected.\n\nRegards,\nAdmin";
                    utils.EmailSender.sendEmail(studentEmail, subject, message);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }



    public List<Attendance> getAttendanceForSubject(String subjectName, String date) {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT a.*, u.full_name as student_name, s.subject_name FROM Attendance a " +
                       "JOIN Users u ON a.student_id = u.user_id " +
                       "JOIN Subjects s ON a.subject_id = s.subject_id " +
                       "WHERE s.subject_name = ? AND a.date = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, subjectName);
            stmt.setString(2, date);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                attendanceList.add(mapResultSetToAttendance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceList;
    }

    public List<User> getStudentsForSubject(String subjectName) {
        List<User> students = new ArrayList<>();
        String query = "SELECT u.* FROM Users u " +
                       "JOIN StudentSubjects ss ON u.user_id = ss.student_id " +
                       "JOIN Subjects s ON ss.subject_id = s.subject_id " +
                       "WHERE s.subject_name = ? AND u.role = 'Student'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, subjectName);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User student = new User();
                student.setUserId(rs.getInt("user_id"));
                student.setFullName(rs.getString("full_name"));
                student.setCollegeEmail(rs.getString("college_email"));
                student.setPersonalEmail(rs.getString("personal_email"));
                student.setPhoneNumber(rs.getString("phone_number"));
                student.setRole(rs.getString("role"));
                student.setPassword(rs.getString("password"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public List<String> getInstructorSubjects(int instructorId) {
        List<String> subjects = new ArrayList<>();
        String query = "SELECT s.subject_name FROM Subjects s " +
                       "JOIN InstructorSubjects isub ON s.subject_id = isub.subject_id " +
                       "WHERE isub.instructor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, instructorId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subjects.add(rs.getString("subject_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subjects;
    }

    public List<String> getAttendancePercentages() {
        List<String> result = new ArrayList<>();
        String query = "SELECT a.student_id, a.subject_id, COUNT(*) AS total_classes, " +
                       "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS presents, " +
                       "ROUND(SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS percentage " +
                       "FROM Attendance a " +
                       "WHERE a.status = 'Present' OR (a.status = 'Leave' AND a.approved = TRUE) " +
                       "GROUP BY a.student_id, a.subject_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                int subjectId = rs.getInt("subject_id");
                int total = rs.getInt("total_classes");
                int present = rs.getInt("presents");
                double percent = rs.getDouble("percentage");
                result.add("Student: " + studentId + ", Subject ID: " + subjectId + ", Present: " + present + "/" + total + ", " + percent + "%");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public double getAttendancePercentage(int studentId, Integer subjectId) {
        StringBuilder query = new StringBuilder(
            "SELECT COUNT(*) AS total_classes, " +
            "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS present_count " +
            "FROM Attendance WHERE student_id = ?"
        );

        List<Object> parameters = new ArrayList<>();
        parameters.add(studentId);

        if (subjectId != null) {
            query.append(" AND subject_id = ?");
            parameters.add(subjectId);
        }

        query.append(" AND (status = 'Present' OR (status = 'Leave' AND approved = TRUE))");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total_classes");
                int present = rs.getInt("present_count");

                if (total == 0) return 0.0;

                return (present * 100.0) / total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public boolean isAttendanceSaved(String date, int subjectId) {
        String query = "SELECT 1 FROM Attendance WHERE date = ? AND subject_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, date);
            stmt.setInt(2, subjectId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAttendanceSaved(String date, String subjectName) {
        int subjectId = getSubjectIdByName(subjectName);
        if (subjectId == -1) return false;
        return isAttendanceSaved(date, subjectId);
    }

    // ✅ PUBLIC: Now accessible outside this class
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
        return -1;
    }

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(rs.getInt("attendance_id"));
        attendance.setStudentId(rs.getInt("student_id"));
        attendance.setStudentName(rs.getString("student_name"));
        attendance.setSubjectId(rs.getInt("subject_id"));
        attendance.setSubjectName(rs.getString("subject_name"));
        attendance.setDate(rs.getString("date"));
        attendance.setStatus(rs.getString("status"));

        Object approvedValue = rs.getObject("approved");
        attendance.setApproved(approvedValue != null ? rs.getBoolean("approved") : null);

        String leaveReason = rs.getString("leave_reason");
        attendance.setLeaveReason(leaveReason != null ? leaveReason : "");

        return attendance;
    }
    public int getEditCount(String subjectName, String date) {
        int count = 0;
        String sql = "SELECT MAX(edit_count) FROM attendance a " +
                     "JOIN subjects s ON a.subject_id = s.subject_id " +
                     "WHERE s.subject_name = ? AND a.date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subjectName);
            stmt.setString(2, date);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public int getEditCount(int studentId, int subjectId, String date) {
        int count = 0;
        String sql = "SELECT edit_count FROM attendance WHERE student_id = ? AND subject_id = ? AND date = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);
            stmt.setString(3, date);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("edit_count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }
    public String getStudentEmailByAttendanceId(int attendanceId) {
        String email = null;
        String query = "SELECT u.personal_email FROM Attendance a JOIN Users u ON a.student_id = u.user_id WHERE a.attendance_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, attendanceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                email = rs.getString("personal_email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }


}