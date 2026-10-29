package Core.Entities;

// Milestone 2 - Tran Ly Viet Tung - Member Entity
import java.time.LocalDate;

public class Member extends User {
    private LocalDate registrationDate;
    private boolean active;
    private int reputationPoints;

    public Member(String userId, String fullName, String phone,
                  LocalDate dateOfBirth, LocalDate registrationDate, boolean active, int reputationPoints) {
        super(userId, fullName, phone, dateOfBirth);
        setRegistrationDate(registrationDate);
        this.active = active;
        setReputationPoints(reputationPoints);
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        if (registrationDate != null) {
            this.registrationDate = registrationDate;
        } else {
            this.registrationDate = LocalDate.now();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getReputationPoints() {
        return reputationPoints;
    }

    public void setReputationPoints(int reputationPoints) {
        this.reputationPoints = reputationPoints;
        if (this.reputationPoints < 0) {
            this.active = false;
        }
    }

    public void addReputationPoints(int delta) {
        setReputationPoints(this.reputationPoints + delta);
    }

    public String getMembershipTier() {
        if (reputationPoints >= 100) {
            return "VIP";
        } else if (reputationPoints >= 60) {
            return "GOLD";
        } else {
            return "SILVER";
        }
    }

    public int getMaxBorrowLimit() {
        String tier = getMembershipTier();
        if (tier.equals("VIP")) {
            return 10;
        } else if (tier.equals("GOLD")) {
            return 7;
        } else {
            return 5;
        }
    }

    public double getDiscountRate() {
        String tier = getMembershipTier();
        if (tier.equals("VIP")) {
            return 0.25;
        } else if (tier.equals("GOLD")) {
            return 0.10;
        } else {
            return 0.0;
        }
    }

    public String getStatusText() {
        if (active) {
            return "Active";
        } else {
            return "Locked";
        }
    }

    public String toFileString() {
        String dob = (dateOfBirth != null) ? dateOfBirth.format(DATE_FORMAT) : "01/01/2000";
        String reg = (registrationDate != null) ? registrationDate.format(DATE_FORMAT) : LocalDate.now().format(DATE_FORMAT);
        return String.format("%s|%s|%s|%s|%s|%s|%d",
                userId, fullName, phone, dob, reg, active ? "ACTIVE" : "LOCKED", reputationPoints);
    }

    @Override
    public String toString() {
        String regDate = (registrationDate != null) ? registrationDate.format(DATE_FORMAT) : "N/A";
        return String.format("%s | Reg Date: %-10s | Tier: %-6s (%3d pts) | Status: %s",
                super.toString(), regDate, getMembershipTier(), reputationPoints, getStatusText());
    }
}
