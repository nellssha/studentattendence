package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    private PasswordField confirmPasswordField;
    private CheckBox studentCheckBox;
    private CheckBox instructorCheckBox;
    private Button registerButton;
    private Stage primaryStage;
    private TextField dobField;
    private TextField personalEmailField;
    private TextField phoneField;

    public RegisterController(TextField nameField, TextField emailField, PasswordField passwordField,
                             CheckBox studentCheckBox, CheckBox instructorCheckBox, Stage primaryStage,
                             TextField dobField, TextField personalEmailField, TextField phoneField, 
                             PasswordField confirmPasswordField, Button registerButton) {
        this.nameField = nameField;
        this.emailField = emailField;
        this.passwordField = passwordField;
        this.confirmPasswordField = confirmPasswordField;
        this.studentCheckBox = studentCheckBox;
        this.instructorCheckBox = instructorCheckBox;
        this.primaryStage = primaryStage;
        this.dobField = dobField;
        this.personalEmailField = personalEmailField;
        this.phoneField = phoneField;
        this.registerButton = registerButton;

        // Set event handler for register button
        registerButton.setOnAction(e -> handleRegister());
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String dob = dobField.getText().trim();
        String personalEmail = personalEmailField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validate input fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || 
            dob.isEmpty() || personalEmail.isEmpty() || phone.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        // Validate role selection
        if (!studentCheckBox.isSelected() && !instructorCheckBox.isSelected()) {
            showAlert("Error", "Please select a role (Student or Instructor).");
            return;
        }

        // Validate that only one role is selected
        if (studentCheckBox.isSelected() && instructorCheckBox.isSelected()) {
            showAlert("Error", "Please select only one role.");
            return;
        }

        // Determine selected role
        String role = studentCheckBox.isSelected() ? "student" : "instructor";

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

        // Validate that passwords match
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        // Validate date of birth format
        if (!dob.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
            showAlert("Error", "Date of birth must be in YYYY/MM/DD format.");
            return;
        }

        // Validate phone number (basic validation - can be enhanced)
        if (!phone.matches("^\\d{10}$")) {
            showAlert("Error", "Phone number must be 10 digits.");
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