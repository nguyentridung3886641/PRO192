package Core.Entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class User {
    protected String userId;
    protected String fullName;
    protected String phone;
    protected LocalDate dateOfBirth;
    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
            throw new IllegalArgumentException("Lỗi: Mã người dùng không được để trống!");
        }
        this.userId = userId.trim();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Họ tên không được để trống!");
        }
        this.fullName = fullName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || !phone.matches("\\d{9,11}")) {
            throw new IllegalArgumentException("Lỗi: Số điện thoại phải gồm 9 - 11 chữ số!");
        }
        this.phone = phone.trim();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Lỗi: Ngày sinh không thể ở tương lai!");
        }
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        String dob = (dateOfBirth != null) ? dateOfBirth.format(DATE_FORMAT) : "N/A";
        return String.format("ID: %-8s | Tên: %-18s | SĐT: %-11s | Ngày sinh: %s", userId, fullName, phone, dob);
    }
}
