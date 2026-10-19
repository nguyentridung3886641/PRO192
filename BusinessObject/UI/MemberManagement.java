package BusinessObject.UI;

import Core.Entities.Member;
import Core.Entities.User;
import DataObjects.DAO.IMemberDAO;
import DataObjects.DAO.MemberDAO;
import Utilities.DataInput;
import Utilities.DataValidation;
import Utilities.Menu;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện điều khiển phân hệ Quản lý Độc giả & Người dùng (Member Management UI).
 * Tích hợp Tính năng 1 (Hạng thẻ & Điểm uy tín) và Tính năng 8 (Biểu đồ ASCII).
 * 
 * PRO192 - Phân hệ Độc giả & Người dùng (Member 1)
 */
public class MemberManagement {

    private final IMemberDAO memberDAO;

    public MemberManagement() {
        this.memberDAO = new MemberDAO();
    }

    public MemberManagement(IMemberDAO memberDAO) {
        this.memberDAO = (memberDAO != null) ? memberDAO : new MemberDAO();
    }

    public void processMenuForMember() {
        boolean running = true;
        while (running) {
            printMenuHeader();
            int choice = DataInput.getIntegerNumber(">> Enter your choice [0-9]: ");
            System.out.println();
            switch (choice) {
                case 1:
                    displayMembers(memberDAO.getAll(), "ALL LIBRARY MEMBERS LIST");
                    break;
                case 2:
                    addMember();
                    break;
                case 3:
                    updateMember();
                    break;
                case 4:
                    deleteMember();
                    break;
                case 5:
                    searchMemberSubMenu();
                    break;
                case 6:
                    sortMemberSubMenu();
                    break;
                case 7:
                    displayMembershipTierAnalytics();
                    break;
                case 8:
                    if (memberDAO.saveToFile()) {
                        System.out.println(">> Successfully saved member list to Members.txt!");
                    } else {
                        System.out.println(">> Failed to save member list!");
                    }
                    break;
                case 9:
                    memberDAO.loadFromFile();
                    System.out.println(">> Changes discarded from RAM. Returning to Main Menu!");
                    running = false;
                    break;
                case 0:
                    memberDAO.saveToFile();
                    System.out.println(">> Member data saved. Returning to Main Menu!");
                    running = false;
                    break;
                default:
                    System.out.println(">> Invalid choice! Please select from 0 to 9.");
                    break;
            }
            if (running) {
                System.out.println("\nPress Enter to continue...");
                DataInput.getString("");
            }
        }
    }

    private void printMenuHeader() {
        System.out.println("\n" + Menu.repeat("=", 106));
        System.out.println("                         MEMBER & MEMBERSHIP TIER MANAGEMENT SYSTEM                                       ");
        System.out.println(Menu.repeat("=", 106));
        Menu.print(
            "  1. Display all library members (Includes Tier, Reputation Points & Borrow Limits)|" +
            "  2. Add new member|" +
            "  3. Update member information (Includes Status Active / Locked)|" +
            "  4. Delete member by ID|" +
            "  5. Search & filter members (By ID / Name / Phone / Membership Tier VIP-GOLD-SILVER)|" +
            "  6. Sort member list (By Reputation Points / Full Name / Member ID)|" +
            "  7. Membership Tier Distribution Chart (Console ASCII Bar Chart)|" +
            "  8. Save member list to file (Members.txt)|" +
            "  9. Exit without saving (discard changes on RAM)|" +
            "  0. Save & return to Main Menu"
        );
        System.out.println(Menu.repeat("=", 106));
    }

    public void displayMembers(List<Member> members, String title) {
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        System.out.println("  " + title + " (Total: " + members.size() + " members)");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        if (members.isEmpty()) {
            System.out.println("  >> No members found in the list!");
            System.out.println("--------------------------------------------------------------------------------------------------------------------");
            return;
        }
        System.out.printf("| %-6s | %-22s | %-11s | %-10s | %-8s | %-8s | %-8s | %-8s | %-15s |\n",
                "ID", "Full Name", "Phone", "DOB", "Points", "Tier", "Discount", "Limit", "Status");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        // [LAMBDA & STREAM API]: Duyệt và hiển thị danh sách độc giả bằng forEach Lambda
        members.forEach(m -> {
            String dob = (m.getDateOfBirth() != null) ? m.getDateOfBirth().format(User.DATE_FORMAT) : "N/A";
            String discountStr = String.format("-%.0f%%", m.getDiscountRate() * 100);
            String limitStr = m.getMaxBorrowLimit() + " books";
            System.out.printf("| %-6s | %-22s | %-11s | %-10s | %5d pt | %-8s | %-8s | %-8s | %-15s |\n",
                m.getUserId(), m.getFullName(), m.getPhone(), dob,
                m.getReputationPoints(), m.getMembershipTier(), discountStr, limitStr, m.getStatusText());
        });
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
    }

    private void addMember() {
        System.out.println("--- ADD NEW MEMBER ---");
        String userId;
        while (true) {
            userId = DataInput.getNonEmptyString("Enter Member ID (e.g. M006): ").toUpperCase();
            if (!DataValidation.isValidId(userId, "M")) {
                System.out.println(">> Error: Member ID must start with 'M' followed by at least 3 digits (e.g. M001)!");
                continue;
            }
            if (memberDAO.findById(userId) != null) {
                System.out.println(">> Error: Member ID " + userId + " already exists! Please enter another ID.");
            } else {
                break;
            }
        }

        String fullName = DataInput.getNonEmptyString("Enter full name: ");
        String phone;
        while (true) {
            phone = DataInput.getNonEmptyString("Enter phone number (9-11 digits): ");
            if (phone.matches("\\d{9,11}")) {
                break;
            }
            System.out.println(">> Error: Phone number must contain 9 to 11 digits!");
        }

        LocalDate dob;
        while (true) {
            dob = DataInput.getDate("Enter date of birth");
            if (dob != null && !dob.isAfter(LocalDate.now())) {
                break;
            }
            System.out.println(">> Error: Please enter a valid date of birth (not in the future)!");
        }

        int initialPoints = DataInput.getIntegerNumber("Enter initial reputation points [0-150] (Default 50): ", 0, 150);
        LocalDate registrationDate = LocalDate.now();
        try {
            Member member = new Member(userId, fullName, phone, dob, registrationDate, true, initialPoints);
            if (memberDAO.add(member)) {
                System.out.println(">> Successfully added new member! Tier: " + member.getMembershipTier()
                        + " (Borrow limit: " + member.getMaxBorrowLimit() + " books)");
            } else {
                System.out.println(">> Failed to add member!");
            }
        } catch (Exception e) {
            System.out.println(">> Error: " + e.getMessage());
        }
    }

    private void updateMember() {
        System.out.println("--- UPDATE MEMBER INFORMATION ---");
        String userId = DataInput.getNonEmptyString("Enter Member ID to update: ");
        Member member = memberDAO.findById(userId);
        if (member == null) {
            System.out.println(">> No member found with ID: " + userId);
            return;
        }
        System.out.println("Current information: " + member);
        System.out.println("(Press Enter to keep current value)");

        String newName = DataInput.getString("New full name [" + member.getFullName() + "]: ");
        String newPhone = DataInput.getString("New phone number [" + member.getPhone() + "]: ");
        if (!newPhone.isEmpty() && !newPhone.matches("\\d{9,11}")) {
            System.out.println(">> Invalid phone number, keeping existing one.");
            newPhone = "";
        }
        LocalDate newDob = DataInput.getDate("New date of birth");

        System.out.println("Update member card status? (1: Active, 2: Locked, Enter to keep current [" + member.getStatusText() + "])");
        String statusChoice = DataInput.getString(">> Your choice: ");
        if ("1".equals(statusChoice.trim())) {
            member.setActive(true);
            if (member.getReputationPoints() < 0) {
                member.setReputationPoints(50);
            }
            System.out.println(">> Card status set to Active.");
        } else if ("2".equals(statusChoice.trim())) {
            member.setActive(false);
            System.out.println(">> Card status set to Locked.");
        }

        try {
            if (memberDAO.update(member, newName, newPhone, newDob)) {
                System.out.println(">> Member information updated successfully!");
            }
        } catch (Exception e) {
            System.out.println(">> Update error: " + e.getMessage());
        }
    }

    private void deleteMember() {
        System.out.println("--- DELETE MEMBER ---");
        String userId = DataInput.getNonEmptyString("Enter Member ID to delete: ");
        Member member = memberDAO.findById(userId);
        if (member == null) {
            System.out.println(">> No member found with ID: " + userId);
            return;
        }
        System.out.println("Member information: " + member);
        if (DataInput.getBoolean("Are you sure you want to delete this member?")) {
            if (memberDAO.delete(member)) {
                System.out.println(">> Member deleted successfully!");
            }
        } else {
            System.out.println(">> Delete operation cancelled.");
        }
    }

    private void searchMemberSubMenu() {
        System.out.println("1. Find by ID | 2. Search by Name | 3. Search by Phone | 4. Filter by Tier (VIP/GOLD/SILVER)");
        int choice = DataInput.getIntegerNumber("Choose search option [1-4]: ");
        switch (choice) {
            case 1:
                String id = DataInput.getNonEmptyString("Enter Member ID: ");
                Member m = memberDAO.findById(id);
                if (m != null) {
                    System.out.println(">> Result: " + m);
                } else {
                    System.out.println(">> No member found with ID: " + id);
                }
                break;
            case 2:
                String kw = DataInput.getNonEmptyString("Enter name keyword: ");
                displayMembers(memberDAO.searchByName(kw), "SEARCH RESULTS BY NAME: " + kw);
                break;
            case 3:
                String phone = DataInput.getNonEmptyString("Enter phone number to search: ");
                displayMembers(memberDAO.searchByPhone(phone), "SEARCH RESULTS BY PHONE: " + phone);
                break;
            case 4:
                String tier = DataInput.getNonEmptyString("Enter tier to filter (VIP / GOLD / SILVER): ").toUpperCase();
                displayMembers(memberDAO.filterByTier(tier), "MEMBERS LIST BY TIER: " + tier);
                break;
            default:
                System.out.println(">> Invalid choice!");
                break;
        }
    }

    private void sortMemberSubMenu() {
        System.out.println("1. Sort by Reputation Points (High -> Low) | 2. Sort by Full Name (A -> Z) | 3. Sort by Member ID");
        int choice = DataInput.getIntegerNumber("Choose sort option [1-3]: ");
        if (choice == 1) {
            displayMembers(memberDAO.sortByReputationDescending(), "MEMBER RANKING BY REPUTATION POINTS (HIGH -> LOW)");
        } else if (choice == 2) {
            displayMembers(memberDAO.sortByNameAscending(), "MEMBERS SORTED BY FULL NAME (A - Z)");
        } else if (choice == 3) {
            displayMembers(memberDAO.sortByIdAscending(), "MEMBERS SORTED BY MEMBER ID (A - Z)");
        } else {
            System.out.println(">> Invalid choice!");
        }
    }

    /**
     * [LAMBDA & STREAM API]: Thống kê phân bố hạng thẻ và tình trạng hoạt động bằng Stream filter và count.
     * 
     * So sánh:
     * - Cách cũ: Khởi tạo 4 biến đếm rồi dùng vòng for lặp và nhiều câu lệnh if-else lồng nhau.
     * - Lambda/Stream: Mỗi hạng thẻ được tính độc lập bằng `all.stream().filter(...).count()`,
     *   loại bỏ logic kiểm tra if-else phức tạp và biến đếm đột biến (state mutation).
     */
    private void displayMembershipTierAnalytics() {
        List<Member> all = memberDAO.getAll();
        long vipCount = all.stream().filter(m -> "VIP".equalsIgnoreCase(m.getMembershipTier())).count();
        long goldCount = all.stream().filter(m -> "GOLD".equalsIgnoreCase(m.getMembershipTier())).count();
        long silverCount = all.stream().filter(m -> "SILVER".equalsIgnoreCase(m.getMembershipTier())).count();
        long lockedCount = all.stream().filter(m -> !m.isActive()).count();

        List<String> labels = new ArrayList<>();
        labels.add("VIP Tier (>=100 pts, -25%)");
        labels.add("GOLD Tier (60-99 pts, -10%)");
        labels.add("SILVER Tier (<60 pts, 0%)");
        labels.add("Locked Member Cards");

        List<Integer> values = new ArrayList<>();
        values.add((int) vipCount);
        values.add((int) goldCount);
        values.add((int) silverCount);
        values.add((int) lockedCount);

        Menu.printAsciiBarChart("LIBRARY MEMBERSHIP TIER DISTRIBUTION", labels, values, "members");
    }
}
