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
import dao.AttendanceDAO;
import dao.UserDAO;
import dao.SubjectDAO;
import model.Attendance;
import model.Subject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentView {
    private Stage primaryStage;
    private User student;
    private TableView<Attendance> attendanceTable;
    private ComboBox<String> subjectComboBox;
    private DatePicker datePicker;
    private TextArea leaveReasonArea;
    private Label attendancePercentageLabel;
    private Map<String, Integer> subjectMap = new HashMap<>();

    public StudentView(Stage primaryStage, User student) {
        this.primaryStage = primaryStage;
        this.student = student;
    }

    public void show() {
        TabPane tabPane = new TabPane();

        Tab profileTab = new Tab("Profile");
        profileTab.setClosable(false);
        profileTab.setContent(createProfileTab());

        Tab attendanceTab = new Tab("Attendance");
        attendanceTab.setClosable(false);
        attendanceTab.setContent(createAttendanceTab());

        tabPane.getTabs().addAll(profileTab, attendanceTab);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #FF4444; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            loginView.show();
        });

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);
        root.setBottom(logoutButton);
        BorderPane.setAlignment(logoutButton, Pos.CENTER_RIGHT);
        BorderPane.setMargin(logoutButton, new Insets(10));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Dashboard - " + student.getFullName());
        primaryStage.show();
    }

    private VBox createProfileTab() {
        VBox profileBox = new VBox(15);
        profileBox.setPadding(new Insets(20));
        profileBox.setStyle("-fx-background-color: #F5F5F5;");

        Label titleLabel = new Label("Student Profile");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#25447D"));

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(10));

        addReadOnlyField(infoGrid, "Full Name:", student.getFullName(), 0);
        addReadOnlyField(infoGrid, "College Email:", student.getCollegeEmail(), 1);
        addReadOnlyField(infoGrid, "Date of Birth:", student.getDateOfBirth(), 2);
        addReadOnlyField(infoGrid, "Address:", student.getAddress(), 3);
        addReadOnlyField(infoGrid, "Role:", student.getRole(), 4);

        TextField personalEmailField = new TextField(student.getPersonalEmail());
        TextField phoneNumberField = new TextField(student.getPhoneNumber());

        infoGrid.add(new Label("Personal Email:"), 0, 5);
        infoGrid.add(personalEmailField, 1, 5);
        infoGrid.add(new Label("Phone Number:"), 0, 6);
        infoGrid.add(phoneNumberField, 1, 6);

        PasswordField currentPasswordField = new PasswordField();
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        infoGrid.add(new Label("Current Password:"), 0, 7);
        infoGrid.add(currentPasswordField, 1, 7);
        infoGrid.add(new Label("New Password:"), 0, 8);
        infoGrid.add(newPasswordField, 1, 8);
        infoGrid.add(new Label("Confirm Password:"), 0, 9);
        infoGrid.add(confirmPasswordField, 1, 9);

        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            if (!newPasswordField.getText().isEmpty() &&
                !newPasswordField.getText().equals(confirmPasswordField.getText())) {
                showAlert("Error", "New passwords don't match!");
                return;
            }

            if (!newPasswordField.getText().isEmpty() &&
                !currentPasswordField.getText().equals(student.getPassword())) {
                showAlert("Error", "Current password is incorrect!");
                return;
            }

            student.setPersonalEmail(personalEmailField.getText());
            student.setPhoneNumber(phoneNumberField.getText());

            if (!newPasswordField.getText().isEmpty()) {
                student.setPassword(newPasswordField.getText());
            }

            UserDAO userDAO = new UserDAO();
            if (userDAO.updateUser(student)) {
                showAlert("Success", "Profile updated successfully!");
            } else {
                showAlert("Error", "Failed to update profile. Please try again.");
            }
        });

        profileBox.getChildren().addAll(titleLabel, infoGrid, saveButton);
        return profileBox;
    }

    private VBox createAttendanceTab() {
        VBox attendanceBox = new VBox(15);
        attendanceBox.setPadding(new Insets(20));
        attendanceBox.setStyle("-fx-background-color: #F5F5F5;");

        Label titleLabel = new Label("Attendance Records");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#25447D"));

        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        subjectComboBox = new ComboBox<>();
        subjectComboBox.setPromptText("Select Subject");
        subjectComboBox.setOnAction(e -> loadAttendancePercentage());
        loadSubjectsForStudent();

        datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
        datePicker.setValue(LocalDate.now());

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;");
        searchButton.setOnAction(e -> loadAttendanceData());

        filterBox.getChildren().addAll(
            new Label("Subject:"), subjectComboBox,
            new Label("Date:"), datePicker,
            searchButton
        );

        attendancePercentageLabel = new Label("Attendance Percentage: --%");
        attendancePercentageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        attendancePercentageLabel.setTextFill(Color.DARKSLATEBLUE);

        leaveReasonArea = new TextArea();
        leaveReasonArea.setPromptText("Enter reason for leave...");
        leaveReasonArea.setPrefRowCount(3);

        Button markLeaveButton = new Button("Mark Leave");
        markLeaveButton.setStyle("-fx-background-color: #44679F; -fx-text-fill: white;");
        markLeaveButton.setOnAction(e -> markLeave());

        attendanceTable = new TableView<>();
        setupAttendanceTable();

        attendanceBox.getChildren().addAll(titleLabel, filterBox, attendancePercentageLabel, attendanceTable,
                                           new Label("Leave Reason:"), leaveReasonArea, markLeaveButton);
        return attendanceBox;
    }

    private void setupAttendanceTable() {
        TableColumn<Attendance, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        TableColumn<Attendance, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());

        TableColumn<Attendance, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        TableColumn<Attendance, String> approvalCol = new TableColumn<>("Approval");
        approvalCol.setCellValueFactory(cellData ->
            cellData.getValue().isApprovedProperty().get() ?
                new javafx.beans.property.SimpleStringProperty("Approved") :
                new javafx.beans.property.SimpleStringProperty("Pending")
        );

        attendanceTable.getColumns().addAll(dateCol, subjectCol, statusCol, approvalCol);
        attendanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadAttendanceData() {
        String subject = subjectComboBox.getValue();
        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : null;

        AttendanceDAO attendanceDAO = new AttendanceDAO();
        List<Attendance> records = attendanceDAO.getFilteredAttendance(
            student.getFullName(), subject, date);

        attendanceTable.getItems().setAll(records);
    }

    private void markLeave() {
        String subjectName = subjectComboBox.getValue();
        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : null;
        String reason = leaveReasonArea.getText().trim();

        if (subjectName == null || date == null || reason.isEmpty()) {
            showAlert("Error", "Please select subject, date, and provide a reason for leave.");
            return;
        }

        AttendanceDAO attendanceDAO = new AttendanceDAO();
        List<Attendance> existing = attendanceDAO.getFilteredAttendance(
            student.getFullName(), subjectName, date);

        if (!existing.isEmpty()) {
            showAlert("Info", "Attendance already marked for " + subjectName + " on " + date);
            return;
        }

        Integer subjectId = subjectMap.get(subjectName);
        if (subjectId == null) {
            showAlert("Error", "Subject not found");
            return;
        }

        Attendance attendance = new Attendance();
        attendance.setStudentId(student.getUserId());
        attendance.setSubjectId(subjectId);
        attendance.setSubject(subjectName);
        attendance.setDate(date);
        attendance.setStatus("leave");  // Set status to 'leave'
        attendance.setLeaveReason(reason);  // Set the reason for leave in the leave_reason field
        attendance.setApproved(false);

        try {
            attendanceDAO.markAttendance(attendance);
            showAlert("Success", "Leave marked for " + subjectName + " on " + date + " (Pending Approval)");
            leaveReasonArea.clear();
            loadAttendanceData();
            loadAttendancePercentage();
        } catch (Exception e) {
            showAlert("Error", "Failed to mark leave: " + e.getMessage());
        }
    }

    private void loadSubjectsForStudent() {
        SubjectDAO subjectDAO = new SubjectDAO();
        List<Subject> subjects = subjectDAO.getSubjectsByStudentId(student.getUserId());
        for (Subject subject : subjects) {
            subjectComboBox.getItems().add(subject.getSubjectName());
            subjectMap.put(subject.getSubjectName(), subject.getSubjectId());
        }
    }

    private void loadAttendancePercentage() {
        String subjectName = subjectComboBox.getValue();
        if (subjectName == null) return;

        AttendanceDAO attendanceDAO = new AttendanceDAO();
        Integer subjectId = subjectMap.get(subjectName);
        double percentage = attendanceDAO.getAttendancePercentage(student.getUserId(), subjectId);

        attendancePercentageLabel.setText("Attendance Percentage: " + String.format("%.2f", percentage) + "%");
    }

    private void addReadOnlyField(GridPane grid, String labelText, String value, int row) {
        grid.add(new Label(labelText), 0, row);
        TextField field = new TextField(value);
        field.setEditable(false);
        grid.add(field, 1, row);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
