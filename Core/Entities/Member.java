package Core.Entities;

import java.time.LocalDate;

/**
 * Lớp Member đại diện cho Độc giả thư viện, kế thừa từ lớp trừu tượng User.
 * Bổ sung Tính năng 1: Hệ thống Điểm Uy Tín (Reputation Points) & Hạng Thẻ (SILVER, GOLD, VIP).
 * 
 * PRO192 - Phân hệ Độc giả & Người dùng (Member 1)
 */
public class Member extends User {
    private LocalDate registrationDate;
    private boolean active;
    private int reputationPoints; // Điểm uy tín (Mặc định: 50 điểm)

    public Member(String userId, String fullName, String phone,
                  LocalDate dateOfBirth, LocalDate registrationDate) {
        super(userId, fullName, phone, dateOfBirth);
        setRegistrationDate(registrationDate);
        this.active = true;
        this.reputationPoints = 50;
    }

    public Member(String userId, String fullName, String phone,
                  LocalDate dateOfBirth, LocalDate registrationDate, boolean active) {
        this(userId, fullName, phone, dateOfBirth, registrationDate, active, 50);
    }

    public Member(String userId, String fullName, String phone,
                  LocalDate dateOfBirth, LocalDate registrationDate, boolean active, int reputationPoints) {
        super(userId, fullName, phone, dateOfBirth);
        setRegistrationDate(registrationDate);
        this.active = active;
        setReputationPoints(reputationPoints);
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
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
        // Tự động khóa thẻ nếu điểm uy tín tụt xuống dưới 0
        if (this.reputationPoints < 0) {
            this.active = false;
        }
    }

    /**
     * Cộng hoặc trừ điểm uy tín khi độc giả trả sách đúng hạn / trễ hạn / làm mất sách.
     * Nếu điểm < 0 thì tự động khóa thẻ.
     */
    public void addReputationPoints(int delta) {
        this.reputationPoints += delta;
        if (this.reputationPoints < 0) {
            this.active = false;
        }
    }

    /**
     * Xác định Hạng thẻ dựa trên điểm uy tín (Viết bằng if-else truyền thống):
     * - >= 100 điểm: VIP
     * - 60 đến 99 điểm: GOLD
     * - < 60 điểm: SILVER
     */
    public String getMembershipTier() {
        if (reputationPoints >= 100) {
            return "VIP";
        } else if (reputationPoints >= 60) {
            return "GOLD";
        } else {
            return "SILVER";
        }
    }

    /**
     * Hạn mức số sách tối đa được mượn cùng lúc theo Hạng thẻ:
     * - VIP: 10 cuốn | GOLD: 7 cuốn | SILVER: 5 cuốn
     */
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

    /**
     * Tỉ lệ phần trăm giảm giá phí mượn sách theo Hạng thẻ:
     * - VIP: giảm 25% (0.25) | GOLD: giảm 10% (0.10) | SILVER: 0% (0.0)
     */
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
