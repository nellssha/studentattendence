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
import model.User;
import dao.AttendanceDAO;
import dao.SubjectAssignmentDAO;
import dao.SubjectDAO;
import javafx.beans.property.SimpleStringProperty;

import java.util.*;
import java.util.stream.Collectors;

public class AdminView extends Application {

    private TableView<Attendance> attendanceTable;
    private TextField studentNameField;
    private ComboBox<String> subjectComboBox;
    private DatePicker datePicker;
    private ObservableList<Attendance> attendanceData;
    private Stage primaryStage;

    private ComboBox<User> studentComboBox;
    private ComboBox<User> instructorComboBox;
    private ComboBox<String> assignmentSubjectComboBox;
    private TextArea resultArea;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        BorderPane root = new BorderPane();

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color: #25447D;");
        topBar.setAlignment(Pos.CENTER_RIGHT);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setOnAction(e -> logout());

        topBar.getChildren().add(logoutButton);
        root.setTop(topBar);

        TabPane tabPane = new TabPane();

        Tab attendanceTab = new Tab("Attendance Management");
        attendanceTab.setClosable(false);
        attendanceTab.setContent(createAttendanceTabContent());

        Tab subjectAssignmentTab = new Tab("Subject Assignment");
        subjectAssignmentTab.setClosable(false);
        subjectAssignmentTab.setContent(createSubjectAssignmentTabContent());

        Tab leaveApprovalTab = new Tab("Leave Approvals");
        leaveApprovalTab.setClosable(false);
        leaveApprovalTab.setContent(createLeaveApprovalTab());

        tabPane.getTabs().addAll(attendanceTab, subjectAssignmentTab, leaveApprovalTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.show();
    }

    private void logout() {
        primaryStage.close();
        Stage loginStage = new Stage();
        LoginView loginView = new LoginView(loginStage);
        loginView.show();
    }

    private VBox createLeaveApprovalTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F5F5F5;");

        Label header = new Label("Pending Leave Requests");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TableView<Attendance> leaveTable = new TableView<>();
        leaveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Attendance, String> nameCol = new TableColumn<>("Student");
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStudentName()));

        TableColumn<Attendance, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subjectName"));

        TableColumn<Attendance, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Attendance, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Attendance, String> reasonCol = new TableColumn<>("Leave Reason");
        reasonCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLeaveReason()));

        TableColumn<Attendance, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<Attendance, Void>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox actionBox = new HBox(10, approveBtn, rejectBtn);

            {
                approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                approveBtn.setOnAction(e -> {
                    Attendance att = getTableView().getItems().get(getIndex());
                    boolean approved = attendanceDAO.approveLeave(att.getAttendanceId());
                    if (approved) {
                        att.setStatus("Approved"); // Update status to Approved
                        getTableView().getItems().set(getIndex(), att); // Update the row
                        approveBtn.setDisable(true);
                        rejectBtn.setDisable(true);
                        showAlert("Success", "Leave approved for " + att.getStudentName());
                    } else {
                        showAlert("Error", "Failed to approve leave.");
                    }
                });

                rejectBtn.setOnAction(e -> {
                    Attendance att = getTableView().getItems().get(getIndex());
                    boolean rejected = attendanceDAO.rejectLeave(att.getAttendanceId());
                    if (rejected) {
                        att.setStatus("Rejected"); // Update status to Rejected
                        getTableView().getItems().set(getIndex(), att); // Update the row
                        approveBtn.setDisable(true);
                        rejectBtn.setDisable(true);
                        showAlert("Success", "Leave rejected for " + att.getStudentName());
                    } else {
                        showAlert("Error", "Failed to reject leave.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && getIndex() >= 0) {
                    Attendance att = getTableView().getItems().get(getIndex());
                    if ("Approved".equals(att.getStatus()) || "Rejected".equals(att.getStatus())) {
                        approveBtn.setDisable(true);
                        rejectBtn.setDisable(true);
                    } else {
                        approveBtn.setDisable(false);
                        rejectBtn.setDisable(false);
                    }
                    setGraphic(actionBox);
                } else {
                    setGraphic(null);
                }
            }
        });

        leaveTable.getColumns().addAll(nameCol, subjectCol, dateCol, statusCol, reasonCol, actionCol);

        List<Attendance> pendingLeaves = attendanceDAO.getFilteredAttendance(null, null, null)
            .stream()
            .filter(att -> "Leave".equalsIgnoreCase(att.getStatus()))
            .filter(att -> !Boolean.TRUE.equals(att.isApproved()))
            .collect(Collectors.collectingAndThen(
                Collectors.toMap(
                    att -> att.getStudentId() + "_" + att.getDate() + "_" + att.getSubjectName(),
                    att -> att,
                    (existing, replacement) -> existing
                ),
                m -> new ArrayList<>(m.values())
            ));

        leaveTable.setItems(FXCollections.observableArrayList(pendingLeaves));

        layout.getChildren().addAll(header, leaveTable);
        return layout;
    }


    private VBox createAttendanceTabContent() {
        VBox attendanceTabContent = new VBox(15);
        attendanceTabContent.setPadding(new Insets(20));
        attendanceTabContent.setStyle("-fx-background-color: #F5F5F5;");

        Label headerLabel = new Label("Attendance Management");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.web("#25447D"));

        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        studentNameField = new TextField();
        studentNameField.setPromptText("Student Name");

        subjectComboBox = new ComboBox<>();
        subjectComboBox.setPromptText("Select Subject");
        loadSubjects(subjectComboBox);

        datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;");
        searchButton.setOnAction(e -> filterAttendance());

        filterBox.getChildren().addAll(new Label("Filters:"), studentNameField, subjectComboBox, datePicker, searchButton);

        attendanceTable = new TableView<>();
        setupTableColumns();
        loadAttendanceData();

        attendanceTabContent.getChildren().addAll(headerLabel, filterBox, attendanceTable);
        return attendanceTabContent;
    }

    private VBox createSubjectAssignmentTabContent() {
        VBox subjectAssignmentContent = new VBox(15);
        subjectAssignmentContent.setPadding(new Insets(20));
        subjectAssignmentContent.setStyle("-fx-background-color: #F5F5F5;");

        Label headerLabel = new Label("Subject Assignment");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.web("#25447D"));

        SubjectAssignmentDAO subjectAssignmentDAO = new SubjectAssignmentDAO();

        Label studentLabel = new Label("Assign Subject to Student:");
        studentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        studentComboBox = new ComboBox<>();
        studentComboBox.setPromptText("Select Student");
        studentComboBox.setItems(FXCollections.observableArrayList(subjectAssignmentDAO.getAllStudents()));
        setupUserComboBox(studentComboBox);

        Label instructorLabel = new Label("Assign Subject to Instructor:");
        instructorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        instructorComboBox = new ComboBox<>();
        instructorComboBox.setPromptText("Select Instructor");
        instructorComboBox.setItems(FXCollections.observableArrayList(subjectAssignmentDAO.getAllInstructors()));
        setupUserComboBox(instructorComboBox);

        Label subjectLabel = new Label("Subject:");
        assignmentSubjectComboBox = new ComboBox<>();
        assignmentSubjectComboBox.setPromptText("Select Subject");
        loadSubjects(assignmentSubjectComboBox);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button assignToStudentBtn = new Button("Assign to Student");
        assignToStudentBtn.setStyle("-fx-background-color: #25447D; -fx-text-fill: white;");
        assignToStudentBtn.setOnAction(e -> assignSubjectToStudent());

        Button assignToInstructorBtn = new Button("Assign to Instructor");
        assignToInstructorBtn.setStyle("-fx-background-color: #44679F; -fx-text-fill: white;");
        assignToInstructorBtn.setOnAction(e -> assignSubjectToInstructor());

        buttonBox.getChildren().addAll(assignToStudentBtn, assignToInstructorBtn);

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(100);

        VBox studentBox = new VBox(5, studentLabel, studentComboBox);
        VBox instructorBox = new VBox(5, instructorLabel, instructorComboBox);
        HBox selectionBox = new HBox(20, studentBox, instructorBox);

        VBox subjectBox = new VBox(5, subjectLabel, assignmentSubjectComboBox, buttonBox);

        subjectAssignmentContent.getChildren().addAll(headerLabel, selectionBox, subjectBox, resultArea);
        return subjectAssignmentContent;
    }

    private void setupTableColumns() {
        TableColumn<Attendance, String> studentColumn = new TableColumn<>("Student Name");
        studentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentName()));

        TableColumn<Attendance, String> subjectColumn = new TableColumn<>("Subject");
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));

        TableColumn<Attendance, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Attendance, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Attendance, String> percentageColumn = new TableColumn<>("Attendance %");
        percentageColumn.setCellValueFactory(cell -> {
            int studentId = cell.getValue().getStudentId();
            int subjectId = cell.getValue().getSubjectId();
            if (subjectId == 0) {
                subjectId = subjectDAO.getSubjectIdByName(cell.getValue().getSubjectName());
            }
            double percent = attendanceDAO.getAttendancePercentage(studentId, subjectId);
            return new SimpleStringProperty(String.format("%.2f%%", percent));
        });

        attendanceTable.getColumns().addAll(studentColumn, subjectColumn, dateColumn, statusColumn, percentageColumn);
    }

    private void loadAttendanceData() {
        List<Attendance> records = attendanceDAO.getAllAttendance();

        List<Attendance> uniqueRecords = records.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toMap(
                    att -> att.getStudentId() + "_" + att.getSubjectId() + "_" + att.getDate(),
                    att -> att,
                    (existing, replacement) -> existing
                ),
                m -> new ArrayList<>(m.values())
            ));

        attendanceData = FXCollections.observableArrayList(uniqueRecords);

        attendanceTable.setItems(attendanceData); 
    }


    private void filterAttendance() {
        String studentName = studentNameField.getText().trim();
        String subject = subjectComboBox.getValue();
        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : null;

        List<Attendance> filteredRecords = attendanceDAO.getFilteredAttendance(studentName, subject, date);

        List<Attendance> uniqueFiltered = filteredRecords.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toMap(
                    att -> att.getStudentId() + "_" + att.getSubjectId() + "_" + att.getDate(),
                    att -> att,
                    (existing, replacement) -> existing
                ),
                m -> new ArrayList<>(m.values())
            ));

        attendanceData.setAll(uniqueFiltered);

        attendanceTable.setItems(attendanceData); 
    }


    private void loadSubjects(ComboBox<String> comboBox) {
        List<String> subjects = subjectDAO.getAllSubjectNames();
        comboBox.getItems().setAll(subjects);
    }

    private void setupUserComboBox(ComboBox<User> comboBox) {
        comboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName() + " (" + item.getCollegeEmail() + ")");
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName() + " (" + item.getCollegeEmail() + ")");
            }
        });
    }

    private void assignSubjectToStudent() {
        User selectedStudent = studentComboBox.getValue();
        String selectedSubject = assignmentSubjectComboBox.getValue();

        if (selectedStudent == null || selectedSubject == null) {
            resultArea.setText("Please select both a student and a subject.");
            return;
        }

        SubjectAssignmentDAO dao = new SubjectAssignmentDAO();
        boolean success = dao.assignSubjectToStudent(selectedStudent.getUserId(), selectedSubject);
        resultArea.setText(success ? "Successfully assigned " + selectedSubject + " to student " + selectedStudent.getFullName() : "Failed to assign subject. Please try again.");
    }

    private void assignSubjectToInstructor() {
        User selectedInstructor = instructorComboBox.getValue();
        String selectedSubject = assignmentSubjectComboBox.getValue();

        if (selectedInstructor == null || selectedSubject == null) {
            resultArea.setText("Please select both an instructor and a subject.");
            return;
        }

        SubjectAssignmentDAO dao = new SubjectAssignmentDAO();
        boolean success = dao.assignSubjectToInstructor(selectedInstructor.getUserId(), selectedSubject);
        resultArea.setText(success ? "Successfully assigned " + selectedSubject + " to instructor " + selectedInstructor.getFullName() : "Failed to assign subject. Please try again.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
