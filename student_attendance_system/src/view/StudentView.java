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

public class StudentView {
    private Stage primaryStage;
    private User student;

    public StudentView(Stage primaryStage, User student) {
        this.primaryStage = primaryStage;
        this.student = student;
    }

    public void show() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #FFFFFF;"); // White background

        // Title
        Label titleLabel = new Label("Student Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#25447D")); // Dark blue text
        grid.add(titleLabel, 0, 0, 2, 1);

        // Welcome Message
        Label welcomeLabel = new Label("Welcome, " + student.getFullName());
        welcomeLabel.setTextFill(Color.web("#25447D")); // Dark blue text
        grid.add(welcomeLabel, 0, 1, 2, 1);

        // View Attendance Button
        Button viewAttendanceButton = new Button("View Attendance");
        viewAttendanceButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;"); // Dark blue button
        grid.add(viewAttendanceButton, 0, 2);

        // Mark Leave Button
        Button markLeaveButton = new Button("Mark Leave");
        markLeaveButton.setStyle("-fx-background-color: #44679F; -fx-text-fill: white;"); // Light blue button
        grid.add(markLeaveButton, 1, 2);

        // Attendance Display Area
        TextArea attendanceArea = new TextArea();
        attendanceArea.setEditable(false);
        attendanceArea.setPrefSize(300, 150);
        grid.add(attendanceArea, 0, 3, 2, 1);

        // Event Handlers
        viewAttendanceButton.setOnAction(e -> {
            AttendanceDAO attendanceDAO = new AttendanceDAO();
            List<Attendance> attendanceList = attendanceDAO.getAttendanceByStudentId(student.getUserId());
            StringBuilder attendanceDetails = new StringBuilder();
            for (Attendance attendance : attendanceList) {
                attendanceDetails.append("Subject: ").append(attendance.getSubject())
                        .append(", Date: ").append(attendance.getDate())
                        .append(", Status: ").append(attendance.getStatus()).append("\n");
            }
            attendanceArea.setText(attendanceDetails.toString());
        });

        markLeaveButton.setOnAction(e -> {
            Attendance attendance = new Attendance();
            attendance.setStudentId(student.getUserId());
            attendance.setSubject("Math"); // Example subject
            attendance.setDate("2023-10-15"); // Example date
            attendance.setStatus("Leave");
            AttendanceDAO attendanceDAO = new AttendanceDAO();
            attendanceDAO.markAttendance(attendance);
            attendanceArea.setText("Leave marked successfully!");
        });

        // Set Scene
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
