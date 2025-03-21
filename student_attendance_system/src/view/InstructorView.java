package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.User; // Import User class
import dao.AttendanceDAO; // Import AttendanceDAO class
import model.Attendance; // Import Attendance class
import java.util.List; // Import List interface

public class InstructorView {
    private Stage primaryStage;
    private User instructor;

    public InstructorView(Stage primaryStage, User instructor) {
        this.primaryStage = primaryStage;
        this.instructor = instructor;
    }

    public void show() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #FFFFFF;"); // White background

        // Title
        Label titleLabel = new Label("Instructor Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#25447D")); // Dark blue text
        grid.add(titleLabel, 0, 0, 2, 1);

        // Welcome Message
        Label welcomeLabel = new Label("Welcome, " + instructor.getFullName());
        welcomeLabel.setTextFill(Color.web("#25447D")); // Dark blue text
        grid.add(welcomeLabel, 0, 1, 2, 1);

        // View Attendance Button
        Button viewAttendanceButton = new Button("View Attendance");
        viewAttendanceButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;"); // Dark blue button
        grid.add(viewAttendanceButton, 0, 2);

        // Attendance Display Area
        TextArea attendanceArea = new TextArea();
        attendanceArea.setEditable(false);
        attendanceArea.setPrefSize(300, 150);
        grid.add(attendanceArea, 0, 3, 2, 1);

        // Event Handlers
        viewAttendanceButton.setOnAction(e -> {
            // Assuming the instructor is assigned a specific student ID.
            // Replace 1 with actual student ID if needed.
            AttendanceDAO attendanceDAO = new AttendanceDAO();
            List<Attendance> attendanceList = attendanceDAO.getAttendanceByStudentId(1); // Example student ID
            StringBuilder attendanceDetails = new StringBuilder();
            for (Attendance attendance : attendanceList) {
                attendanceDetails.append("Subject: ").append(attendance.getSubject())
                        .append(", Date: ").append(attendance.getDate())
                        .append(", Status: ").append(attendance.getStatus()).append("\n");
            }
            attendanceArea.setText(attendanceDetails.toString());
        });

        // Set Scene
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
