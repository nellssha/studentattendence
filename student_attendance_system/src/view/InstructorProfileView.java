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

public class InstructorProfileView {
    private Stage profileStage;
    private User instructor;
    private Stage primaryStage;

    public InstructorProfileView(Stage primaryStage, User instructor) {
        this.primaryStage = primaryStage;
        this.instructor = instructor;
        this.profileStage = new Stage();
    }

    public void show() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Title
        Label titleLabel = new Label("Instructor Profile");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#25447D"));
        grid.add(titleLabel, 0, 0, 2, 1);

        // Full Name (read-only)
        Label nameLabel = new Label("Full Name:");
        TextField nameField = new TextField(instructor.getFullName());
        nameField.setEditable(false);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);

        // College Email (read-only)
        Label emailLabel = new Label("College Email:");
        TextField emailField = new TextField(instructor.getCollegeEmail());
        emailField.setEditable(false);
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);

        // Personal Email
        Label personalEmailLabel = new Label("Personal Email:");
        TextField personalEmailField = new TextField(instructor.getPersonalEmail());
        grid.add(personalEmailLabel, 0, 3);
        grid.add(personalEmailField, 1, 3);

        // Phone Number
        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField(instructor.getPhoneNumber());
        grid.add(phoneLabel, 0, 4);
        grid.add(phoneField, 1, 4);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;");
        saveButton.setOnAction(e -> saveProfileChanges(
            personalEmailField.getText(),
            phoneField.getText()
        ));

        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #44679F; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            profileStage.close();
            primaryStage.show(); // Show the main dashboard stage
        });

        buttonBox.getChildren().addAll(saveButton, backButton);
        grid.add(buttonBox, 0, 5, 2, 1);

        Scene scene = new Scene(grid, 400, 350);
        profileStage.setScene(scene);
        profileStage.setTitle("Instructor Profile - " + instructor.getFullName());
        profileStage.show();
    }

    private void saveProfileChanges(String personalEmail, String phone) {
        instructor.setPersonalEmail(personalEmail);
        instructor.setPhoneNumber(phone);

        UserDAO userDAO = new UserDAO();
        if (userDAO.updateUser(instructor)) {
            showAlert("Success", "Profile updated successfully!");
        } else {
            showAlert("Error", "Failed to update profile");
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