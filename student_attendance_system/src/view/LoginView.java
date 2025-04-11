package view;

import controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class LoginView {
    private Stage primaryStage;
    
    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void show() {
        // Main layout container using BorderPane
        BorderPane borderPane = new BorderPane();
        
        // LEFT SIDE - White panel with image
        StackPane leftPanel = new StackPane();
        leftPanel.setStyle("-fx-background-color: #FFFFFF;");
        leftPanel.setPrefWidth(500);
        
        // Load image from resources
        try {
        	Image presentationImage = new Image(getClass().getResourceAsStream("/pic.png"));

            ImageView imageView = new ImageView(presentationImage);
            imageView.setFitWidth(450);
            imageView.setPreserveRatio(true);
            leftPanel.getChildren().add(imageView);
        } catch (Exception e) {
            System.err.println("Failed to load image: " + e.getMessage());
        }
        
        // RIGHT SIDE - Blue Login Panel
        VBox rightPanel = new VBox();
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(40, 30, 40, 30));
        rightPanel.setSpacing(20);
        rightPanel.setStyle("-fx-background-color: #3A57A6;"); // Royal blue color
        rightPanel.setPrefWidth(400);
        
        // Welcome Back Text
        Label welcomeLabel = new Label("Welcome Back");
        welcomeLabel.setFont(Font.font("Palatino", FontWeight.BOLD, 36));
        welcomeLabel.setTextFill(Color.WHITE);
        welcomeLabel.setTextAlignment(TextAlignment.CENTER);
        
        Label loginLabel = new Label("LOGIN");
        loginLabel.setFont(Font.font("Palatino", FontWeight.MEDIUM, 26));
        loginLabel.setTextFill(Color.WHITE);
        loginLabel.setTextAlignment(TextAlignment.CENTER);
        loginLabel.setPadding(new Insets(0, 0, 20, 0));
        
        // Email field
        HBox emailBox = new HBox(10);
        emailBox.setAlignment(Pos.CENTER_LEFT);
        emailBox.setMaxWidth(350);
        
        Label emailLabel = new Label("Email:");
        emailLabel.setTextFill(Color.WHITE);
        emailLabel.setFont(Font.font("Arial", 14));
        emailLabel.setPrefWidth(80);
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefHeight(35);
        emailField.setPrefWidth(270);
        
        emailBox.getChildren().addAll(emailLabel, emailField);
        
        // Password field
        HBox passwordBox = new HBox(10);
        passwordBox.setAlignment(Pos.CENTER_LEFT);
        passwordBox.setMaxWidth(350);
        
        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.WHITE);
        passwordLabel.setFont(Font.font("Arial", 14));
        passwordLabel.setPrefWidth(80);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(35);
        passwordField.setPrefWidth(270);
        
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        
        // Forgot Password Link
        Hyperlink forgotPasswordLink = new Hyperlink("Forgot Password?");
        forgotPasswordLink.setTextFill(Color.WHITE);
        forgotPasswordLink.setStyle("-fx-border-color: transparent;");
        HBox forgotPasswordBox = new HBox(forgotPasswordLink);
        forgotPasswordBox.setAlignment(Pos.CENTER_RIGHT);
        forgotPasswordBox.setPadding(new Insets(-10, 0, 10, 0));
        forgotPasswordBox.setMaxWidth(350);
        
        // Login Button
        Button loginButton = new Button("LOGIN");
        loginButton.setStyle("-fx-background-color: #78C4E8; " +
                "-fx-text-fill: #333333; " +
                "-fx-background-radius: 30; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16px;");
        loginButton.setPrefHeight(45);
        loginButton.setPrefWidth(200);
        
        // Register Link
        HBox registerBox = new HBox(10);
        registerBox.setAlignment(Pos.CENTER);
        
        Label noAccountLabel = new Label("Don't have an account yet?");
        noAccountLabel.setTextFill(Color.WHITE);
        
        Hyperlink registerLink = new Hyperlink("Register Here!");
        registerLink.setTextFill(Color.LIGHTBLUE);
        registerLink.setStyle("-fx-border-color: transparent;");
        
        registerBox.getChildren().addAll(noAccountLabel, registerLink);
        
        // Login Controller
        LoginController loginController = new LoginController(emailField, passwordField, primaryStage);
        loginButton.setOnAction(e -> loginController.handleLogin());
        registerLink.setOnAction(e -> new RegisterView(primaryStage).show());
        forgotPasswordLink.setOnAction(e -> System.out.println("Forgot password clicked"));
        
        // Add elements to right panel
        rightPanel.getChildren().addAll(welcomeLabel, loginLabel, emailBox, passwordBox, forgotPasswordBox, loginButton, registerBox);
        
        // Add panels to BorderPane
        borderPane.setLeft(leftPanel);
        borderPane.setRight(rightPanel);
        
        // Set Scene and show
        Scene scene = new Scene(borderPane, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Attendance System - Login");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
