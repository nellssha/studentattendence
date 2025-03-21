package dao;

import model.User;
import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Retrieves a user from the database by their email.
     *
     * @param email The email of the user to retrieve.
     * @return The User object if found, otherwise null.
     */
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM Users WHERE college_email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setDateOfBirth(rs.getString("date_of_birth"));
                user.setAddress(rs.getString("address"));
                user.setPersonalEmail(rs.getString("personal_email"));
                user.setCollegeEmail(rs.getString("college_email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no user is found
    }

    /**
     * Adds a new user to the database.
     *
     * @param user The User object to add.
     * @return true if the user was added successfully, otherwise false.
     */
    public boolean addUser(User user) {
        String query = "INSERT INTO Users (full_name, date_of_birth, address, personal_email, college_email, phone_number, password, role) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getDateOfBirth());
            stmt.setString(3, user.getAddress());
            stmt.setString(4, user.getPersonalEmail());
            stmt.setString(5, user.getCollegeEmail());
            stmt.setString(6, user.getPhoneNumber());
            stmt.setString(7, user.getPassword());
            stmt.setString(8, user.getRole());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was affected
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurred
        }
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user The User object to update.
     * @return true if the user was updated successfully, otherwise false.
     */
    public boolean updateUser(User user) {
        String query = "UPDATE Users SET full_name = ?, date_of_birth = ?, address = ?, personal_email = ?, " +
                       "college_email = ?, phone_number = ?, password = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getDateOfBirth());
            stmt.setString(3, user.getAddress());
            stmt.setString(4, user.getPersonalEmail());
            stmt.setString(5, user.getCollegeEmail());
            stmt.setString(6, user.getPhoneNumber());
            stmt.setString(7, user.getPassword());
            stmt.setString(8, user.getRole());
            stmt.setInt(9, user.getUserId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was affected
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurred
        }
    }

    /**
     * Deletes a user from the database by their user ID.
     *
     * @param userId The ID of the user to delete.
     * @return true if the user was deleted successfully, otherwise false.
     */
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM Users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was affected
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurred
        }
    }
}