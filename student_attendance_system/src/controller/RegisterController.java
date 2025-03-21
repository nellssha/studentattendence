package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import dao.UserDAO;
import view.LoginView;

public class RegisterController {
    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;
    private Button studentButton;
    private Button instructorButton;
    private Stage primaryStage;
    private TextField dobField;
    private TextField personalEmailField;
    private TextField phoneField;

    public RegisterController(TextField nameField, TextField emailField, PasswordField passwordField,
                             Button studentButton, Button instructorButton, Stage primaryStage,
                             TextField dobField, TextField personalEmailField, TextField phoneField) {
        this.nameField = nameField;
        this.emailField = emailField;
        this.passwordField = passwordField;
        this.studentButton = studentButton;
        this.instructorButton = instructorButton;
        this.primaryStage = primaryStage;
        this.dobField = dobField;
        this.personalEmailField = personalEmailField;
        this.phoneField = phoneField;

        // Set event handlers for buttons
        studentButton.setOnAction(e -> handleRegister("student"));
        instructorButton.setOnAction(e -> handleRegister("instructor"));
    }

    private void handleRegister(String role) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String dob = dobField.getText().trim();
        String personalEmail = personalEmailField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validate input fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || dob.isEmpty() || personalEmail.isEmpty() || phone.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$") || !personalEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Error", "Invalid email format.");
            return;
        }

        // Validate password length
        if (password.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters long.");
            return;
        }

        // Create a new User object
        User newUser = new User();
        newUser.setFullName(name);
        newUser.setCollegeEmail(email);
        newUser.setPassword(password);
        newUser.setRole(role);
        newUser.setDateOfBirth(dob);
        newUser.setPersonalEmail(personalEmail);
        newUser.setPhoneNumber(phone);

        // Add the user to the database
        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.addUser(newUser);

        if (success) {
            showAlert("Success", "Registration successful!");
            // Redirect to the login view
            LoginView loginView = new LoginView(primaryStage);
            loginView.show();
        } else {
            showAlert("Error", "Registration failed. Please try again.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
