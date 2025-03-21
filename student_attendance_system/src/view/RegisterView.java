package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import controller.RegisterController;

public class RegisterView {
    private Stage primaryStage;

    public RegisterView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #FFFFFF;"); // White background

        // Title
        Label titleLabel = new Label("Register");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#25447D"));
        grid.add(titleLabel, 0, 0, 2, 1);

        // Full Name Field
        Label nameLabel = new Label("Full Name:");
        nameLabel.setTextFill(Color.web("#25447D"));
        grid.add(nameLabel, 0, 1);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        grid.add(nameField, 1, 1);

        // Date of Birth Field
        Label dobLabel = new Label("Date of Birth:");
        dobLabel.setTextFill(Color.web("#25447D"));
        grid.add(dobLabel, 0, 2);

        TextField dobField = new TextField();
        dobField.setPromptText("YYYY/MM/DD");
        grid.add(dobField, 1, 2);

        // Personal Email Field
        Label personalEmailLabel = new Label("Personal Email:");
        personalEmailLabel.setTextFill(Color.web("#25447D"));
        grid.add(personalEmailLabel, 0, 3);

        TextField personalEmailField = new TextField();
        personalEmailField.setPromptText("Enter your personal email");
        grid.add(personalEmailField, 1, 3);

        // Phone Number Field
        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setTextFill(Color.web("#25447D"));
        grid.add(phoneLabel, 0, 4);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter your phone number");
        grid.add(phoneField, 1, 4);

        // College Email Field
        Label emailLabel = new Label("College Email:");
        emailLabel.setTextFill(Color.web("#25447D"));
        grid.add(emailLabel, 0, 5);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your college email");
        grid.add(emailField, 1, 5);

        // Password Field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.web("#25447D"));
        grid.add(passwordLabel, 0, 6);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        grid.add(passwordField, 1, 6);

        // Role Selection
        Label roleLabel = new Label("Role:");
        roleLabel.setTextFill(Color.web("#25447D"));
        grid.add(roleLabel, 0, 7);

        // Role Selection Buttons
        Button studentButton = new Button("Student");
        studentButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;");

        Button instructorButton = new Button("Instructor");
        instructorButton.setStyle("-fx-background-color: #44679F; -fx-text-fill: white;");

        // Use HBox to align buttons properly
        HBox roleButtons = new HBox(10, studentButton, instructorButton);
        roleButtons.setAlignment(Pos.CENTER);
        grid.add(roleButtons, 1, 7, 2, 1); // Span across 2 columns

        // Back to Login Button
        Button backButton = new Button("Back to Login");
        backButton.setStyle("-fx-background-color: #44679F; -fx-text-fill: white;");
        grid.add(backButton, 1, 8);

        // Attach Controller
        new RegisterController(nameField, emailField, passwordField, studentButton, instructorButton, primaryStage, dobField, personalEmailField, phoneField);

        // Back to Login View
        backButton.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            loginView.show();
        });

        // Set Scene
        Scene scene = new Scene(grid, 450, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
