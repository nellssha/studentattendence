package model;

import javafx.beans.property.*;

public class Attendance {
    private IntegerProperty attendanceId = new SimpleIntegerProperty();
    private IntegerProperty studentId = new SimpleIntegerProperty();
    private IntegerProperty subjectId = new SimpleIntegerProperty();
    private IntegerProperty editCount = new SimpleIntegerProperty(0);
    private StringProperty subjectName = new SimpleStringProperty();
    private StringProperty date = new SimpleStringProperty();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty studentName = new SimpleStringProperty();
    private BooleanProperty approved = new SimpleBooleanProperty();
    private StringProperty leaveReason = new SimpleStringProperty(); // NEW
    private boolean approvedSet = false;

    public Attendance() {}

    public Attendance(int studentId, String studentName, String subjectName, String date, String status, Boolean approved) {
        this.studentId.set(studentId);
        this.studentName.set(studentName);
        this.subjectName.set(subjectName);
        this.date.set(date);
        this.status.set(status);
        setApproved(approved);
    }

    // Properties
    public IntegerProperty attendanceIdProperty() { return attendanceId; }
    public IntegerProperty studentIdProperty() { return studentId; }
    public IntegerProperty subjectIdProperty() { return subjectId; }
    public StringProperty subjectNameProperty() { return subjectName; }
    public StringProperty dateProperty() { return date; }
    public StringProperty statusProperty() { return status; }
    public StringProperty studentNameProperty() { return studentName; }
    public BooleanProperty approvedProperty() { return approved; }
    public StringProperty leaveReasonProperty() { return leaveReason; } // NEW

    // Getters
    public int getAttendanceId() { return attendanceId.get(); }
    public int getStudentId() { return studentId.get(); }
    public int getSubjectId() { return subjectId.get(); }
    public String getSubjectName() { return subjectName.get(); }
    public String getDate() { return date.get(); }
    public String getStatus() { return status.get(); }
    public String getStudentName() { return studentName.get(); }
    public String getLeaveReason() { return leaveReason.get(); } // NEW
    public int getEditCount() { return editCount.get(); }
    public IntegerProperty editCountProperty() {
        return editCount;
    }

    public Boolean isApproved() {
        return approvedSet ? approved.get() : null;
    }

    // Setters
    public void setAttendanceId(int id) { attendanceId.set(id); }
    public void setStudentId(int id) { studentId.set(id); }
    public void setSubjectId(int id) { subjectId.set(id); }
    public void setSubjectName(String name) { subjectName.set(name); }
    public void setDate(String d) { date.set(d); }
    public void setStatus(String s) { status.set(s); }
    public void setStudentName(String name) { studentName.set(name); }
    public void setLeaveReason(String reason) { leaveReason.set(reason); } // NEW
    public void setEditCount(int editCount) {
        this.editCount.set(editCount);
    }
    public void setApproved(Boolean value) {
        if (value == null) {
            approvedSet = false;
            approved.set(false);
        } else {
            approved.set(value);
            approvedSet = true;
        }
    }
    
    

    

    


    // Convenience
    public StringProperty subjectProperty() { return subjectName; }
    public BooleanProperty isApprovedProperty() { return approved; }
    public void setSubject(String subject) { this.subjectName.set(subject); }
}
