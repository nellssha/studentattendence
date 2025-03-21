package view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Attendance;
import dao.AttendanceDAO;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class AdminView extends Application {

    private TableView<Attendance> attendanceTable;
    private TextField studentNameField;
    private ComboBox<String> subjectComboBox;
    private DatePicker datePicker;
    private ObservableList<Attendance> attendanceData;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F5F5F5;");

        Label headerLabel = new Label("Admin Dashboard - View Attendance");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.web("#25447D"));

        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        studentNameField = new TextField();
        studentNameField.setPromptText("Student Name");

        subjectComboBox = new ComboBox<>();
        subjectComboBox.setPromptText("Select Subject");
        loadSubjects();

        datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;");
        searchButton.setOnAction(e -> filterAttendance());

        filterBox.getChildren().addAll(new Label("Filters:"), studentNameField, subjectComboBox, datePicker, searchButton);

        attendanceTable = new TableView<>();
        setupTableColumns();

        loadAttendanceData();

        root.getChildren().addAll(headerLabel, filterBox, attendanceTable);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard - View Attendance");
        primaryStage.show();
    }

    private void setupTableColumns() {
        // Student Name Column
        TableColumn<Attendance, String> studentColumn = new TableColumn<>("Student Name");
        studentColumn.setCellValueFactory(cellData -> {
            Attendance attendance = cellData.getValue();
            return new SimpleStringProperty(attendance.getStudentName()); // Wrap the student name in SimpleStringProperty
        });

        // Subject Column
        TableColumn<Attendance, String> subjectColumn = new TableColumn<>("Subject");
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));

        // Date Column
        TableColumn<Attendance, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Status Column
        TableColumn<Attendance, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add columns to table
        attendanceTable.getColumns().addAll(studentColumn, subjectColumn, dateColumn, statusColumn);
    }

    private void loadAttendanceData() {
        AttendanceDAO attendanceDAO = new AttendanceDAO();
        List<Attendance> records = attendanceDAO.getAllAttendance();
        attendanceData = FXCollections.observableArrayList(records);
        attendanceTable.setItems(attendanceData);
    }

    private void filterAttendance() {
        String studentName = studentNameField.getText().trim();
        String subject = subjectComboBox.getValue();
        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : null;

        AttendanceDAO attendanceDAO = new AttendanceDAO();
        List<Attendance> filteredRecords = attendanceDAO.getFilteredAttendance(studentName, subject, date);
        attendanceData.setAll(filteredRecords);
    }

    private void loadSubjects() {
        subjectComboBox.getItems().addAll("Math", "Science", "History", "English");
    }

    public static void main(String[] args) {
        launch(args); // Calls JavaFX launch method to start the application
    }
}
