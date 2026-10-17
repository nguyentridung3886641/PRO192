package Core.Entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class User {
    protected String userId;
    protected String fullName;
    protected String phone;
    protected LocalDate dateOfBirth;
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public User(String userId, String fullName, String phone, LocalDate dateOfBirth) {
        setUserId(userId);
        setFullName(fullName);
        setPhone(phone);
        setDateOfBirth(dateOfBirth);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: User ID cannot be empty!");
        }
        this.userId = userId.trim();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Full name cannot be empty!");
        }
        this.fullName = fullName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || !phone.trim().matches("\\d{9,11}")) {
            throw new IllegalArgumentException("Error: Phone number must contain 9 - 11 digits!");
        }
        this.phone = phone.trim();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Error: Date of birth cannot be in the future!");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public abstract String getRole();

    @Override
    public String toString() {
        String dob = (dateOfBirth != null) ? dateOfBirth.format(DATE_FORMAT) : "N/A";
        return String.format("ID: %-8s | Name: %-20s | Phone: %-11s | DOB: %s", userId, fullName, phone, dob);
    }
}
