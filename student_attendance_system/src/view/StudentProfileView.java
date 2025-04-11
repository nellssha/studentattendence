package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.User;
import dao.UserDAO;

public class StudentProfileView {
    private Stage profileStage;
    private User student;
    private Stage primaryStage;
    private TabPane tabPane;

    public StudentProfileView(Stage primaryStage, User student) {
        this.primaryStage = primaryStage;
        this.student = student;
        this.profileStage = new Stage();
    }

    public void show() {
        // Create main tab pane
        tabPane = new TabPane();
        
        // Create profile tab
        Tab profileTab = new Tab("My Profile");
        profileTab.setContent(createProfileTabContent());
        profileTab.setClosable(false);
        
        // Create password change tab
        Tab passwordTab = new Tab("Change Password");
        passwordTab.setContent(createPasswordTabContent());
        passwordTab.setClosable(false);
        
        tabPane.getTabs().addAll(profileTab, passwordTab);
        
        // Create main scene
        Scene scene = new Scene(tabPane, 500, 400);
        profileStage.setScene(scene);
        profileStage.setTitle("Student Profile - " + student.getFullName());
        profileStage.show();
    }

    private VBox createProfileTabContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        // Title
        Label titleLabel = new Label("Personal Information");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#25447D"));

        // Information Grid
        GridPane infoGrid = new GridPane();
        infoGrid.setAlignment(Pos.CENTER);
        infoGrid.setHgap(10);
        infoGrid.setVgap(10);
        
        // Full Name
        addInfoRow(infoGrid, "Full Name:", student.getFullName(), 0);
        
        // College Email
        addInfoRow(infoGrid, "College Email:", student.getCollegeEmail(), 1);
        
        // Date of Birth
        TextField dobField = addEditableRow(infoGrid, "Date of Birth:", student.getDateOfBirth(), 2);
        
        // Personal Email
        TextField personalEmailField = addEditableRow(infoGrid, "Personal Email:", student.getPersonalEmail(), 3);
        
        // Phone Number
        TextField phoneField = addEditableRow(infoGrid, "Phone Number:", student.getPhoneNumber(), 4);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;");
        saveButton.setOnAction(e -> saveProfileChanges(
            dobField.getText(),
            personalEmailField.getText(),
            phoneField.getText()
        ));
        
        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #44679F; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            profileStage.close();
            primaryStage.show();
        });
        
        buttonBox.getChildren().addAll(saveButton, backButton);

        content.getChildren().addAll(titleLabel, infoGrid, buttonBox);
        return content;
    }

    private VBox createPasswordTabContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        // Title
        Label titleLabel = new Label("Change Password");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#25447D"));

        // Password Grid
        GridPane passwordGrid = new GridPane();
        passwordGrid.setAlignment(Pos.CENTER);
        passwordGrid.setHgap(10);
        passwordGrid.setVgap(10);
        
        // Current Password
        PasswordField currentPasswordField = new PasswordField();
        addPasswordRow(passwordGrid, "Current Password:", currentPasswordField, 0);
        
        // New Password
        PasswordField newPasswordField = new PasswordField();
        addPasswordRow(passwordGrid, "New Password:", newPasswordField, 1);
        
        // Confirm Password
        PasswordField confirmPasswordField = new PasswordField();
        addPasswordRow(passwordGrid, "Confirm Password:", confirmPasswordField, 2);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button saveButton = new Button("Save Password");
        saveButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;");
        saveButton.setOnAction(e -> changePassword(
            currentPasswordField.getText(),
            newPasswordField.getText(),
            confirmPasswordField.getText()
        ));
        
        Button backButton = new Button("Back to Profile");
        backButton.setStyle("-fx-background-color: #44679F; -fx-text-fill: white;");
        backButton.setOnAction(e -> tabPane.getSelectionModel().select(0));
        
        buttonBox.getChildren().addAll(saveButton, backButton);

        content.getChildren().addAll(titleLabel, passwordGrid, buttonBox);
        return content;
    }

    private void addInfoRow(GridPane grid, String labelText, String value, int row) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label valueLabel = new Label(value);
        grid.add(label, 0, row);
        grid.add(valueLabel, 1, row);
    }

    private TextField addEditableRow(GridPane grid, String labelText, String value, int row) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        TextField field = new TextField(value);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
        return field;
    }

    private void addPasswordRow(GridPane grid, String labelText, PasswordField field, int row) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    private void saveProfileChanges(String dob, String personalEmail, String phone) {
        student.setDateOfBirth(dob);
        student.setPersonalEmail(personalEmail);
        student.setPhoneNumber(phone);

        UserDAO userDAO = new UserDAO();
        if (userDAO.updateUser(student)) {
            showAlert("Success", "Profile updated successfully!");
        } else {
            showAlert("Error", "Failed to update profile");
        }
    }

    private void changePassword(String currentPassword, String newPassword, String confirmPassword) {
        if (!currentPassword.equals(student.getPassword())) {
            showAlert("Error", "Current password is incorrect");
            return;
        }
        if (newPassword.isEmpty()) {
            showAlert("Error", "New password cannot be empty");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "New passwords don't match");
            return;
        }
        if (newPassword.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters");
            return;
        }

        student.setPassword(newPassword);
        UserDAO userDAO = new UserDAO();
        if (userDAO.updateUser(student)) {
            showAlert("Success", "Password changed successfully!");
            tabPane.getSelectionModel().select(0); // Switch back to profile tab
        } else {
            showAlert("Error", "Failed to change password");
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
