import java.time.LocalDate;

public class Member extends User {

    private LocalDate registrationDate;
    private boolean active;

    public Member(
            String userId,
            String fullName,
            String phone,
            LocalDate dateOfBirth,
            LocalDate registrationDate) {

        super(userId, fullName, phone, dateOfBirth);

        this.registrationDate = registrationDate;
        this.active = true;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public String getActive() {
        if (active) {
            return "active";
        } else {
            return "inactive";
        }
    }

    public void setRegistrationDate(LocalDate newRegistrationDate) {
        this.registrationDate = newRegistrationDate;
    }

    public void toggleStatus() {
        this.active = !active;
    }

    public void setActive(boolean newStatus) {
        this.active = newStatus;
    }
}
