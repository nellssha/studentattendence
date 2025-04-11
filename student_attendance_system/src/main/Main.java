package main;

import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView; // Import the LoginView class

public class Main extends Application {
    public static void main(String[] args) {
        // Launches the JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Set the title of the main window
        primaryStage.setTitle("Student Attendance System");

        // Create an instance of the LoginView and pass the primaryStage to it
        LoginView loginView = new LoginView(primaryStage);

        // Show the login view
        loginView.show();
    }
}
