package Core.Entities;

import java.time.LocalDate;

public class Member extends User {
    private LocalDate registrationDate;
    private boolean active;

    public Member(String userId, String fullName, String phone, LocalDate dateOfBirth, LocalDate registrationDate) {
        super(userId, fullName, phone, dateOfBirth);
        setRegistrationDate(registrationDate);
        this.active = true;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = (registrationDate != null) ? registrationDate : LocalDate.now();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getStatusText() {
        return active ? "Đang hoạt động" : "Bị khóa";
    }

    public void toggleStatus() {
        this.active = !this.active;
    }

    @Override
    public String toString() {
        String regDate = (registrationDate != null) ? registrationDate.format(DATE_FORMAT) : "N/A";
        return String.format("%s | Ngày ĐK: %-10s | Trạng thái: %s", super.toString(), regDate, getStatusText());
    }
}
