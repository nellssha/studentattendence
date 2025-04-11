package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import controller.RegisterController;

public class RegisterView {
    private Stage primaryStage;
    private PasswordField passwordField;
    private TextField visiblePasswordField;
    private PasswordField confirmPasswordField;
    private TextField visibleConfirmPasswordField;

    public RegisterView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        // Main layout container using BorderPane
        BorderPane borderPane = new BorderPane();

        // LEFT SIDE - Smaller image panel
        StackPane leftPanel = new StackPane();
        leftPanel.setStyle("-fx-background-color: #FFFFFF;");
        leftPanel.setPrefWidth(300); // Reduced width for image panel

        try {
            // Load image from the resources directory
            Image registrationImage = new Image(getClass().getResource("/registration .png").toString());

            ImageView imageView = new ImageView(registrationImage);
            imageView.setFitWidth(250);  // Smaller image
            imageView.setPreserveRatio(true);

            VBox imageContainer = new VBox(imageView);
            imageContainer.setAlignment(Pos.CENTER);
            imageContainer.setPadding(new Insets(0, 0, 20, 0));

            leftPanel.getChildren().add(imageContainer);
        } catch (Exception e) {
            System.err.println("Failed to load image: " + e.getMessage());
        }

        // RIGHT SIDE - Registration Panel with smaller text
        VBox rightPanel = new VBox();
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(20, 40, 20, 40)); // Reduced padding
        rightPanel.setSpacing(12); // Reduced spacing
        rightPanel.setStyle("-fx-background-color: #3A57A6;");
        rightPanel.setPrefWidth(500); // Adjusted width

        // Welcome Text - smaller
        Label welcomeLabel = new Label("Join Us Today");
        welcomeLabel.setFont(Font.font("Palatino", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.WHITE);
        welcomeLabel.setTextAlignment(TextAlignment.CENTER);

        // REGISTER Text - smaller
        Label registerTitleLabel = new Label("REGISTER");
        registerTitleLabel.setFont(Font.font("Palatino", FontWeight.MEDIUM, 20));
        registerTitleLabel.setTextFill(Color.WHITE);
        registerTitleLabel.setTextAlignment(TextAlignment.CENTER);
        registerTitleLabel.setPadding(new Insets(0, 0, 15, 0));

        // Form fields with smaller text
        HBox nameBox = createFormField("Full Name:", "Enter your full name");
        HBox dobBox = createFormField("Date of Birth:", "YYYY/MM/DD");
        HBox personalEmailBox = createFormField("Personal Email:", "Enter your personal email");
        HBox phoneBox = createFormField("Phone Number:", "Enter your phone number");
        HBox collegeEmailBox = createFormField("College Email:", "Enter your college email");

        // Password fields with smaller text
        HBox passwordBox = createPasswordField("Password:", "Enter your password", true);
        HBox confirmPasswordBox = createPasswordField("Confirm Password:", "Confirm your password", false);

        // Show/Hide Password CheckBox - smaller
        CheckBox showPasswordCheckBox = new CheckBox("Show Password");
        showPasswordCheckBox.setTextFill(Color.WHITE);
        showPasswordCheckBox.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        HBox showPasswordBox = new HBox(showPasswordCheckBox);
        showPasswordBox.setAlignment(Pos.CENTER_RIGHT);
        showPasswordBox.setMaxWidth(400);

        // Add event handler for show password checkbox
        showPasswordCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                // Show passwords
                passwordField.setManaged(false);
                passwordField.setVisible(false);
                visiblePasswordField.setManaged(true);
                visiblePasswordField.setVisible(true);

                confirmPasswordField.setManaged(false);
                confirmPasswordField.setVisible(false);
                visibleConfirmPasswordField.setManaged(true);
                visibleConfirmPasswordField.setVisible(true);
            } else {
                // Hide passwords
                visiblePasswordField.setManaged(false);
                visiblePasswordField.setVisible(false);
                passwordField.setManaged(true);
                passwordField.setVisible(true);

                visibleConfirmPasswordField.setManaged(false);
                visibleConfirmPasswordField.setVisible(false);
                confirmPasswordField.setManaged(true);
                confirmPasswordField.setVisible(true);
            }
        });

        // Role Selection - changed to checkboxes
        Label roleLabel = new Label("Select Your Role:");
        roleLabel.setTextFill(Color.WHITE);
        roleLabel.setFont(Font.font("Arial", 12));
        roleLabel.setTextAlignment(TextAlignment.CENTER);

        CheckBox studentCheckBox = new CheckBox("Student");
        styleCheckBox(studentCheckBox);

        CheckBox instructorCheckBox = new CheckBox("Instructor");
        styleCheckBox(instructorCheckBox);

        HBox roleCheckBoxes = new HBox(15, studentCheckBox, instructorCheckBox);
        roleCheckBoxes.setAlignment(Pos.CENTER);

        // Register Button - smaller
        Button registerButton = new Button("REGISTER");
        registerButton.setStyle("-fx-background-color: #78C4E8; " +
                "-fx-text-fill: #333333; " +
                "-fx-background-radius: 30; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px;");
        registerButton.setPrefHeight(40);
        registerButton.setPrefWidth(200);

        // Already have an account section - smaller
        HBox loginBox = new HBox(8);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(10, 0, 0, 0));

        Label haveAccountLabel = new Label("Already have an account?");
        haveAccountLabel.setTextFill(Color.WHITE);
        haveAccountLabel.setStyle("-fx-font-size: 12px;");

        Hyperlink loginLink = new Hyperlink("Login Here!");
        loginLink.setTextFill(Color.LIGHTBLUE);
        loginLink.setStyle("-fx-border-color: transparent; -fx-font-size: 12px; -fx-underline: true;");

        loginBox.getChildren().addAll(haveAccountLabel, loginLink);

        // Controller and navigation
        RegisterController registerController = new RegisterController(
                (TextField) nameBox.getChildren().get(1),
                (TextField) collegeEmailBox.getChildren().get(1),
                passwordField,
                studentCheckBox, instructorCheckBox,
                primaryStage,
                (TextField) dobBox.getChildren().get(1),
                (TextField) personalEmailBox.getChildren().get(1),
                (TextField) phoneBox.getChildren().get(1),
                confirmPasswordField,
                registerButton);

        loginLink.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            loginView.show();
        });

        // Add all elements to the right panel
        rightPanel.getChildren().addAll(
                welcomeLabel,
                registerTitleLabel,
                nameBox,
                dobBox,
                personalEmailBox,
                phoneBox,
                collegeEmailBox,
                passwordBox,
                confirmPasswordBox,
                showPasswordBox,
                roleLabel,
                roleCheckBoxes,
                registerButton,
                loginBox
        );

        // Add panels to BorderPane
        borderPane.setLeft(leftPanel);
        borderPane.setRight(rightPanel);

        // Set scene size
        Scene scene = new Scene(borderPane, 800, 600); // Smaller window
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Attendance System - Registration");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private HBox createFormField(String labelText, String promptText) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(400);

        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", 12));
        label.setPrefWidth(120);

        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle("-fx-background-color: white; " +
                "-fx-padding: 6 8 6 8; " + // Reduced padding
                "-fx-font-size: 12px;");   // Smaller font
        field.setPrefHeight(30);           // Smaller height
        field.setPrefWidth(220);           // Narrower field

        box.getChildren().addAll(label, field);
        return box;
    }

    private HBox createPasswordField(String labelText, String promptText, boolean isPasswordField) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(400);

        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", 12));
        label.setPrefWidth(120);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setStyle("-fx-background-color: white; " +
                "-fx-padding: 6 8 6 8; " +
                "-fx-font-size: 12px;");
        passwordField.setPrefHeight(30);
        passwordField.setPrefWidth(220);

        TextField visibleField = new TextField();
        visibleField.setPromptText(promptText);
        visibleField.setStyle("-fx-background-color: white; " +
                "-fx-padding: 6 8 6 8; " +
                "-fx-font-size: 12px;");
        visibleField.setPrefHeight(30);
        visibleField.setPrefWidth(220);
        visibleField.setManaged(false);
        visibleField.setVisible(false);

        StackPane stackPane = new StackPane(passwordField, visibleField);
        box.getChildren().addAll(label, stackPane);

        passwordField.textProperty().bindBidirectional(visibleField.textProperty());

        if (isPasswordField) {
            this.passwordField = passwordField;
            this.visiblePasswordField = visibleField;
        } else {
            this.confirmPasswordField = passwordField;
            this.visibleConfirmPasswordField = visibleField;
        }

        return box;
    }

    private void styleCheckBox(CheckBox checkBox) {
        checkBox.setTextFill(Color.WHITE);
        checkBox.setStyle("-fx-font-size: 12px;");
    }
}
