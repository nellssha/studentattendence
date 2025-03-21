package javafx_application;

import javafx.stage.Stage;

public abstract class Application {
    /**
     * The main entry point for JavaFX applications.
     *
     * @param primaryStage the primary stage for the application.
     */
    public abstract void start(Stage primaryStage);

    /**
     * Called before the application is started. Can be overridden to perform initialization.
     */
    public void init() throws Exception {
    }

    /**
     * Called when the application is about to stop. Can be overridden to clean up resources.
     */
    public void stop() throws Exception {
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args the command-line arguments.
     */
    public static void launch(String... args) {
        // Internal JavaFX logic to start the application
    }
}
