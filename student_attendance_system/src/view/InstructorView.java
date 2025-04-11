// InstructorView.java

package view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import model.User;
import dao.AttendanceDAO;
import dao.UserDAO;
import model.Attendance;
import utils.EmailSender;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class InstructorView {
    private Stage primaryStage;
    private User instructor;
    private TableView<Attendance> attendanceTable;
    private ComboBox<String> subjectComboBox;
    private DatePicker datePicker;
    private TextField studentNameField;
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final List<Attendance> pendingAttendanceList = new ArrayList<>();
    private Button saveButton;
    private boolean isSearchMode = false;

    public InstructorView(Stage primaryStage, User instructor) {
        this.primaryStage = primaryStage;
        this.instructor = instructor;
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
        logoutButton.setOnAction(e -> new LoginView(primaryStage).show());

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);
        root.setBottom(logoutButton);
        BorderPane.setAlignment(logoutButton, Pos.CENTER_RIGHT);
        BorderPane.setMargin(logoutButton, new Insets(10));

        // window size here
        Scene scene = new Scene(root, 1200, 800);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Instructor Dashboard - " + instructor.getFullName());
        primaryStage.show();
    }

    private VBox createProfileTab() {
        VBox profileBox = new VBox(15);
        profileBox.setPadding(new Insets(20));
        profileBox.setStyle("-fx-background-color: #F5F5F5;");

        Label titleLabel = new Label("My Profile");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#25447D"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        addReadOnlyField(grid, "Full Name:", instructor.getFullName(), 0);
        addReadOnlyField(grid, "College Email:", instructor.getCollegeEmail(), 1);
        addReadOnlyField(grid, "Role:", instructor.getRole(), 2);

        TextField phoneField = new TextField(instructor.getPhoneNumber());
        grid.add(new Label("Phone Number:"), 0, 3);
        grid.add(phoneField, 1, 3);

        TextField personalEmailField = new TextField(instructor.getPersonalEmail());
        grid.add(new Label("Personal Email:"), 0, 4);
        grid.add(personalEmailField, 1, 4);

        PasswordField currentPasswordField = new PasswordField();
        grid.add(new Label("Current Password:"), 0, 5);
        grid.add(currentPasswordField, 1, 5);

        PasswordField newPasswordField = new PasswordField();
        grid.add(new Label("New Password:"), 0, 6);
        grid.add(newPasswordField, 1, 6);

        PasswordField confirmPasswordField = new PasswordField();
        grid.add(new Label("Confirm Password:"), 0, 7);
        grid.add(confirmPasswordField, 1, 7);

        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;");
        saveButton.setOnAction(e -> saveProfileChanges(phoneField, personalEmailField,
                currentPasswordField, newPasswordField, confirmPasswordField));

        profileBox.getChildren().addAll(titleLabel, grid, saveButton);
        return profileBox;
    }

    private VBox createAttendanceTab() {
        VBox attendanceBox = new VBox(15);
        attendanceBox.setPadding(new Insets(20));
        attendanceBox.setStyle("-fx-background-color: #F5F5F5;");

        Label titleLabel = new Label("Manage Attendance");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#25447D"));

        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(0, 0, 20, 0));

        subjectComboBox = new ComboBox<>();
        subjectComboBox.setPromptText("Select Subject");
        subjectComboBox.setPrefWidth(180);
        loadInstructorSubjects();

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setPromptText("Select Date");
        datePicker.setPrefWidth(140);

        studentNameField = new TextField();
        studentNameField.setPromptText("Student Name");
        studentNameField.setPrefWidth(160);

        Button loadButton = new Button("Load All");
        loadButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;");
        loadButton.setOnAction(e -> {
            isSearchMode = false;
            loadAttendanceData();
        });

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white;");
        searchButton.setOnAction(e -> {
            isSearchMode = true;
            searchAttendance();
        });

        saveButton = new Button("Save Attendance");
        saveButton.setStyle("-fx-background-color: #0077cc; -fx-text-fill: white;");
        saveButton.setOnAction(e -> saveAttendanceBatch());

        Button viewPercentageButton = new Button("View %");
        viewPercentageButton.setStyle("-fx-background-color: #6A1B9A; -fx-text-fill: white;");
        viewPercentageButton.setOnAction(e -> showAttendancePercentage());

        filterBox.getChildren().addAll(
                new Label("Subject:"), subjectComboBox,
                new Label("Date:"), datePicker,
                new Label("Student:"), studentNameField,
                loadButton, searchButton, saveButton, viewPercentageButton
        );

        attendanceTable = new TableView<>();
        setupAttendanceTable();

        attendanceBox.getChildren().addAll(titleLabel, filterBox, attendanceTable);
        return attendanceBox;
    }

    private void loadInstructorSubjects() {
        List<String> subjects = attendanceDAO.getInstructorSubjects(instructor.getUserId());
        subjectComboBox.setItems(FXCollections.observableArrayList(subjects));
    }

    private void setupAttendanceTable() {
        attendanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Attendance, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        TableColumn<Attendance, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subjectName"));

        TableColumn<Attendance, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Attendance, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Attendance, Integer> editCountCol = new TableColumn<>("Edits");
        editCountCol.setCellValueFactory(new PropertyValueFactory<>("editCount"));

        TableColumn<Attendance, Void> actionCol = new TableColumn<>("Mark Attendance");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("Present", "Absent", "Leave"));

            {
                statusCombo.setOnAction(event -> {
                    Attendance attendance = getTableView().getItems().get(getIndex());
                    if (attendance.getEditCount() >= 2) {
                        showAlert("Limit Reached", "This student's attendance has already been edited twice today.");
                        loadAttendanceData(); // reset UI
                        return;
                    }

                    attendance.setStatus(statusCombo.getValue());
                    attendance.setEditCount(attendance.getEditCount() + 1);
                    if (!pendingAttendanceList.contains(attendance)) {
                        pendingAttendanceList.add(attendance);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Attendance att = getTableView().getItems().get(getIndex());
                    if (isSearchMode || !canEdit(att)) {
                        setGraphic(null);
                    } else {
                        statusCombo.setValue(att.getStatus());
                        setGraphic(statusCombo);
                    }
                }
            }
        });

        TableColumn<Attendance, String> approvalCol = new TableColumn<>("Leave Approval");
        approvalCol.setCellValueFactory(cell -> {
            Boolean approved = cell.getValue().isApproved();
            String text = approved == null ? "Pending" : (approved ? "Approved" : "Rejected");
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        attendanceTable.getColumns().addAll(nameCol, subjectCol, dateCol, statusCol, editCountCol, actionCol, approvalCol);
    }

    private void loadAttendanceData() {
        String subject = subjectComboBox.getValue();
        LocalDate date = datePicker.getValue();

        if (subject == null || date == null) {
            showAlert("Error", "Please select both subject and date.");
            return;
        }

        List<Attendance> records = attendanceDAO.getAttendanceForSubject(subject, date.toString());
        Map<Integer, Attendance> recordMap = records.stream()
                .collect(Collectors.toMap(Attendance::getStudentId, a -> a, (a1, a2) -> a1));

        List<User> students = attendanceDAO.getStudentsForSubject(subject);
        for (User student : students) {
            recordMap.putIfAbsent(student.getUserId(), new Attendance(
                    student.getUserId(), student.getFullName(), subject, date.toString(), "Not Marked", null
            ));
        }

        for (Attendance att : recordMap.values()) {
            int subjectId = attendanceDAO.getSubjectIdByName(att.getSubjectName());
            int count = attendanceDAO.getEditCount(att.getStudentId(), subjectId, att.getDate());
            att.setEditCount(count);
        }

        attendanceTable.setItems(FXCollections.observableArrayList(recordMap.values()));
    }

    private void searchAttendance() {
        String subject = subjectComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String nameQuery = studentNameField.getText().trim().toLowerCase();

        if (subject == null || date == null) {
            showAlert("Error", "Please select subject and date");
            return;
        }

        List<Attendance> all = attendanceDAO.getAttendanceForSubject(subject, date.toString());
        Set<Integer> seen = new HashSet<>();
        List<Attendance> filtered = all.stream()
                .filter(a -> a.getStudentName().toLowerCase().contains(nameQuery))
                .filter(a -> seen.add(a.getStudentId()))
                .collect(Collectors.toList());

        attendanceTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void saveAttendanceBatch() {
        String subject = subjectComboBox.getValue();
        LocalDate date = datePicker.getValue();

        if (subject == null || date == null) {
            showAlert("Error", "Please select subject and date.");
            return;
        }

        if (attendanceDAO.isAttendanceSaved(subject, date.toString())) {
            showAlert("Info", "Attendance for today is already saved, you cannot make further changes.");
            return;
        }

        if (pendingAttendanceList.isEmpty()) {
            showAlert("Info", "No changes to save.");
            return;
        }

        for (Attendance a : pendingAttendanceList) {
            int subjectId = attendanceDAO.getSubjectIdByName(a.getSubjectName());
            a.setSubjectId(subjectId);
            attendanceDAO.updateOrCreateAttendance(a);
        }

        showAlert("Success", "All marked attendance saved and locked.");

        for (Attendance a : pendingAttendanceList) {
            if (a.getStatus().equals("Absent") || a.getStatus().equals("Leave")) {
                User student = new UserDAO().getUserById(a.getStudentId());
                if (student != null && student.getPersonalEmail() != null) {
                    String subject1 = "Attendance Notification";
                    String message = "Dear " + student.getFullName() + ",\n\n"
                            + "You have been marked as '" + a.getStatus() + "' for "
                            + a.getSubjectName() + " on " + a.getDate() + ".\n\n"
                            + "Please contact your instructor if this is incorrect.\n\n"
                            + "Regards,\nAttendance Management System";
                    EmailSender.sendEmail(student.getPersonalEmail(), subject1, message);
                }
            }
        }

        pendingAttendanceList.clear();
        loadAttendanceData();
    }

    private boolean canEdit(Attendance att) {
        LocalDate selectedDate = LocalDate.parse(att.getDate());
        if (selectedDate.isAfter(LocalDate.now())) return false;
        if (!selectedDate.equals(LocalDate.now())) return false;
        if (attendanceDAO.isAttendanceSaved(att.getSubjectName(), att.getDate())) return false;
        return att.getEditCount() < 2;
    }

    private void showAttendancePercentage() {
        String subjectName = subjectComboBox.getValue();
        if (subjectName == null) {
            showAlert("Error", "Please select a subject.");
            return;
        }

        int subjectId = attendanceDAO.getSubjectIdByName(subjectName);
        List<User> students = attendanceDAO.getStudentsForSubject(subjectName);
        StringBuilder report = new StringBuilder("Attendance Percentage for " + subjectName + ":\n\n");

        for (User s : students) {
            double percent = attendanceDAO.getAttendancePercentage(s.getUserId(), subjectId);
            report.append(s.getFullName()).append(": ").append(String.format("%.2f", percent)).append("%\n");
        }

        showAlert("Attendance Percentage", report.toString());
    }

    private void saveProfileChanges(TextField phoneField, TextField personalEmailField,
                                    PasswordField currentPassword, PasswordField newPassword,
                                    PasswordField confirmPassword) {
        if (!newPassword.getText().isEmpty()) {
            if (!newPassword.getText().equals(confirmPassword.getText())) {
                showAlert("Error", "New passwords don't match");
                return;
            }
            if (!currentPassword.getText().equals(instructor.getPassword())) {
                showAlert("Error", "Current password is incorrect");
                return;
            }
            instructor.setPassword(newPassword.getText());
        }

        instructor.setPhoneNumber(phoneField.getText());
        instructor.setPersonalEmail(personalEmailField.getText());

        if (new UserDAO().updateUser(instructor)) {
            showAlert("Success", "Profile updated successfully");
        } else {
            showAlert("Error", "Failed to update profile");
        }
    }

    private void addReadOnlyField(GridPane grid, String label, String value, int row) {
        grid.add(new Label(label), 0, row);
        TextField field = new TextField(value);
        field.setEditable(false);
        grid.add(field, 1, row);
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
