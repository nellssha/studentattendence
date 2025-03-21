package view;

import controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {
    private Stage primaryStage;

    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        // Creating the grid layout for the login view
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #FFFFFF;"); // White background

        // Title Label
        Label titleLabel = new Label("Login");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#25447D")); // Dark blue text
        grid.add(titleLabel, 0, 0, 2, 1);

        // Email Field
        Label emailLabel = new Label("Email:");
        emailLabel.setTextFill(Color.web("#25447D")); // Dark blue text
        grid.add(emailLabel, 0, 1);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        grid.add(emailField, 1, 1);

        // Password Field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.web("#25447D")); // Dark blue text
        grid.add(passwordLabel, 0, 2);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        grid.add(passwordField, 1, 2);

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;"); // Dark blue button
        grid.add(loginButton, 1, 3);

        // Register Button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #44679F; -fx-text-fill: white;"); // Light blue button
        grid.add(registerButton, 1, 4);

        // Create LoginController and pass the required fields
        LoginController loginController = new LoginController(emailField, passwordField, primaryStage);

        // Handle Login Button Action
        loginButton.setOnAction(e -> loginController.handleLogin());

        // Handle Register Button Action
        registerButton.setOnAction(e -> {
            // Check if RegisterView exists and is properly set up
            RegisterView registerView = new RegisterView(primaryStage);
            registerView.show(); // Opens the registration window
        });

        // Set Scene and show
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
