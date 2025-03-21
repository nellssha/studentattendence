package controller;

import javafx.scene.control.Alert; // Import for Alert
import javafx.scene.control.PasswordField; // Import for PasswordField
import javafx.scene.control.TextField; // Import for TextField
import javafx.stage.Stage; // Import for Stage
import model.User; // Import for User class
import dao.UserDAO; // Import for UserDAO
import view.StudentView; // Import for StudentView
import view.InstructorView; // Import for InstructorView
import view.AdminView; // Import for AdminView

public class LoginController {
    private TextField emailField;
    private PasswordField passwordField;
    private Stage primaryStage;

    public LoginController(TextField emailField, PasswordField passwordField, Stage primaryStage) {
        this.emailField = emailField;
        this.passwordField = passwordField;
        this.primaryStage = primaryStage;
    }

    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            switch (user.getRole().toLowerCase()) { // Ensure role is lowercase
                case "student":
                    StudentView studentView = new StudentView(primaryStage, user);
                    studentView.show();
                    break;
                case "instructor":
                    InstructorView instructorView = new InstructorView(primaryStage, user);
                    instructorView.show();
                    break;
                case "admin":
                    AdminView adminView = new AdminView();
                    adminView.start(primaryStage); // ✅ Corrected
                    break;
                default:
                    // Handle unknown role
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Unknown user role.");
                    alert.showAndWait();
                    break;
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid email or password.");
            alert.showAndWait();
        }
    }
}