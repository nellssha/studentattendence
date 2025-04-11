package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a user in the system, including students, instructors, and admins.
 */
public class User {
    // Properties
    private final IntegerProperty userId = new SimpleIntegerProperty();
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty dateOfBirth = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty personalEmail = new SimpleStringProperty();
    private final StringProperty collegeEmail = new SimpleStringProperty();
    private final StringProperty phoneNumber = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();

    // Constructors
    public User() {
        // Default constructor
    }

    public User(int userId, String fullName, String dateOfBirth, String address,
                String personalEmail, String collegeEmail, String phoneNumber,
                String password, String role) {
        setUserId(userId);
        setFullName(fullName);
        setDateOfBirth(dateOfBirth);
        setAddress(address);
        setPersonalEmail(personalEmail);
        setCollegeEmail(collegeEmail);
        setPhoneNumber(phoneNumber);
        setPassword(password);
        setRole(role);
    }

    // Property getters
    public IntegerProperty userIdProperty() {
        return userId;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty dateOfBirthProperty() {
        return dateOfBirth;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public StringProperty personalEmailProperty() {
        return personalEmail;
    }

    public StringProperty collegeEmailProperty() {
        return collegeEmail;
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty roleProperty() {
        return role;
    }

    // Standard getters
    public int getUserId() {
        return userId.get();
    }

    public String getFullName() {
        return fullName.get();
    }

    public String getDateOfBirth() {
        return dateOfBirth.get();
    }

    public String getAddress() {
        return address.get();
    }

    public String getPersonalEmail() {
        return personalEmail.get();
    }

    public String getCollegeEmail() {
        return collegeEmail.get();
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public String getPassword() {
        return password.get();
    }

    public String getRole() {
        return role.get();
    }

    // Standard setters
    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth.set(dateOfBirth);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail.set(personalEmail);
    }

    public void setCollegeEmail(String collegeEmail) {
        this.collegeEmail.set(collegeEmail);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    // Utility methods
    @Override
    public String toString() {
        String name = getFullName() != null ? getFullName() : "Unknown";
        String email = getCollegeEmail() != null ? getCollegeEmail() : "No email";
        return name + " (" + email + ")";
    }

    public boolean isStudent() {
        return "student".equalsIgnoreCase(getRole());
    }

    public boolean isInstructor() {
        return "instructor".equalsIgnoreCase(getRole());
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(getRole());
    }
}
