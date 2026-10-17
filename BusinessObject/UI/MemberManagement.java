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
            int choice = DataInput.getIntegerNumber(">> Nhập lựa chọn của bạn [0-9]: ");
            System.out.println();
            switch (choice) {
                case 1:
                    displayMembers(memberDAO.getAll(), "DANH SÁCH TOÀN BỘ ĐỘC GIẢ THƯ VIỆN");
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
                    toggleMemberStatus();
                    break;
                case 7:
                    sortMemberSubMenu();
                    break;
                case 8:
                    displayMembershipTierAnalytics();
                    break;
                case 9:
                    if (memberDAO.saveToFile()) {
                        System.out.println(">> Đã lưu danh sách độc giả ra file Members.txt thành công!");
                    }
                    break;
                case 0:
                    memberDAO.saveToFile();
                    System.out.println(">> Đã lưu dữ liệu độc giả và quay lại Menu chính!");
                    running = false;
                    break;
                default:
                    System.out.println(">> Lựa chọn không hợp lệ! Vui lòng chọn từ 0 đến 9.");
                    break;
            }
            if (running) {
                System.out.println("\nNhấn Enter để tiếp tục...");
                DataInput.getString("");
            }
        }
    }

    private void printMenuHeader() {
        System.out.println("\n==========================================================================================================");
        System.out.println("                       PHÂN HỆ QUẢN LÝ ĐỘC GIẢ & HẠNG THẺ (MEMBER MANAGEMENT)                             ");
        System.out.println("==========================================================================================================");
        System.out.println("  1. Hiển thị danh sách toàn bộ độc giả (Kèm Hạng thẻ, Điểm uy tín & Mức ưu đãi)");
        System.out.println("  2. Thêm mới độc giả");
        System.out.println("  3. Cập nhật thông tin độc giả");
        System.out.println("  4. Xóa độc giả theo mã ID");
        System.out.println("  5. Tìm kiếm & Lọc độc giả (Theo Mã / Họ Tên / SĐT / Hạng Thẻ VIP-GOLD-SILVER)");
        System.out.println("  6. Khóa / Mở khóa thẻ độc giả (Active / Locked)");
        System.out.println("  7. Sắp xếp danh sách độc giả (Theo Điểm Uy Tín / Họ Tên / Mã ID)");
        System.out.println("  8. Biểu đồ thống kê phân bố Hạng Thẻ Độc Giả (ASCII Bar Chart)");
        System.out.println("  9. Lưu danh sách độc giả ra file (Members.txt)");
        System.out.println("  0. Lưu & Quay lại Menu chính");
        System.out.println("==========================================================================================================");
    }

    public void displayMembers(List<Member> members, String title) {
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        System.out.println("  " + title + " (Tổng số: " + members.size() + " độc giả)");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        if (members.isEmpty()) {
            System.out.println("  >> Không tìm thấy độc giả nào trong danh sách!");
            System.out.println("--------------------------------------------------------------------------------------------------------------------");
            return;
        }
        System.out.printf("| %-6s | %-22s | %-11s | %-10s | %-7s | %-8s | %-8s | %-8s | %-15s |\n",
                "Mã ĐG", "Họ và Tên", "Số ĐT", "Ngày Sinh", "Điểm UT", "Hạng Thẻ", "Ưu Đãi", "Hạn Mức", "Trạng Thái");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        // [LAMBDA & STREAM API]: Duyệt và hiển thị danh sách độc giả bằng forEach Lambda
        members.forEach(m -> {
            String dob = (m.getDateOfBirth() != null) ? m.getDateOfBirth().format(User.DATE_FORMAT) : "N/A";
            String discountStr = String.format("-%.0f%%", m.getDiscountRate() * 100);
            String limitStr = m.getMaxBorrowLimit() + " cuốn";
            System.out.printf("| %-6s | %-22s | %-11s | %-10s | %5d đ | %-8s | %-8s | %-8s | %-15s |\n",
                    m.getUserId(), m.getFullName(), m.getPhone(), dob,
                    m.getReputationPoints(), m.getMembershipTier(), discountStr, limitStr, m.getStatusText());
        });
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
    }

    private void addMember() {
        System.out.println("--- THÊM ĐỘC GIẢ MỚI ---");
        String userId;
        while (true) {
            userId = DataInput.getNonEmptyString("Nhập mã độc giả (VD: M006): ").toUpperCase();
            if (!DataValidation.isValidId(userId, "M")) {
                System.out.println(">> Lỗi: Mã độc giả phải bắt đầu bằng 'M' theo sau ít nhất 3 chữ số (VD: M001)!");
                continue;
            }
            if (memberDAO.findById(userId) != null) {
                System.out.println(">> Lỗi: Mã độc giả " + userId + " đã tồn tại! Vui lòng nhập mã khác.");
            } else {
                break;
            }
        }

        String fullName = DataInput.getNonEmptyString("Nhập họ và tên: ");
        String phone;
        while (true) {
            phone = DataInput.getNonEmptyString("Nhập số điện thoại (9-11 chữ số): ");
            if (phone.matches("\\d{9,11}")) {
                break;
            }
            System.out.println(">> Lỗi: Số điện thoại phải từ 9 đến 11 chữ số!");
        }

        LocalDate dob;
        while (true) {
            dob = DataInput.getDate("Nhập ngày sinh");
            if (dob != null && !dob.isAfter(LocalDate.now())) {
                break;
            }
            System.out.println(">> Lỗi: Vui lòng nhập ngày sinh hợp lệ (không ở tương lai)!");
        }

        int initialPoints = DataInput.getIntegerNumber("Nhập điểm uy tín ban đầu [0-150] (Mặc định 50): ", 0, 150);
        LocalDate registrationDate = LocalDate.now();
        try {
            Member member = new Member(userId, fullName, phone, dob, registrationDate, true, initialPoints);
            if (memberDAO.add(member)) {
                memberDAO.saveToFile();
                System.out.println(">> Thêm độc giả mới thành công! Hạng thẻ: " + member.getMembershipTier()
                        + " (Hạn mức mượn: " + member.getMaxBorrowLimit() + " cuốn)");
            } else {
                System.out.println(">> Không thể thêm độc giả!");
            }
        } catch (Exception e) {
            System.out.println(">> Lỗi: " + e.getMessage());
        }
    }

    private void updateMember() {
        System.out.println("--- CẬP NHẬT THÔNG TIN ĐỘC GIẢ ---");
        String userId = DataInput.getNonEmptyString("Nhập mã độc giả cần cập nhật: ");
        Member member = memberDAO.findById(userId);
        if (member == null) {
            System.out.println(">> Không tìm thấy độc giả có mã: " + userId);
            return;
        }
        System.out.println("Thông tin hiện tại: " + member);
        System.out.println("(Nhấn Enter để giữ nguyên giá trị cũ)");

        String newName = DataInput.getString("Họ tên mới [" + member.getFullName() + "]: ");
        String newPhone = DataInput.getString("Số điện thoại mới [" + member.getPhone() + "]: ");
        if (!newPhone.isEmpty() && !newPhone.matches("\\d{9,11}")) {
            System.out.println(">> Số điện thoại không hợp lệ, giữ nguyên số điện thoại cũ.");
            newPhone = "";
        }
        LocalDate newDob = DataInput.getDate("Ngày sinh mới");

        try {
            if (memberDAO.update(member, newName, newPhone, newDob)) {
                memberDAO.saveToFile();
                System.out.println(">> Cập nhật thông tin độc giả thành công!");
            }
        } catch (Exception e) {
            System.out.println(">> Lỗi cập nhật: " + e.getMessage());
        }
    }

    private void deleteMember() {
        System.out.println("--- XÓA ĐỘC GIẢ ---");
        String userId = DataInput.getNonEmptyString("Nhập mã độc giả cần xóa: ");
        Member member = memberDAO.findById(userId);
        if (member == null) {
            System.out.println(">> Không tìm thấy độc giả có mã: " + userId);
            return;
        }
        System.out.println("Thông tin độc giả: " + member);
        if (DataInput.getBoolean("Bạn có chắc chắn muốn xóa độc giả này không?")) {
            if (memberDAO.delete(member)) {
                memberDAO.saveToFile();
                System.out.println(">> Đã xóa độc giả thành công!");
            }
        } else {
            System.out.println(">> Đã hủy thao tác xóa.");
        }
    }

    private void toggleMemberStatus() {
        System.out.println("--- KHÓA / MỞ KHÓA THẺ ĐỘC GIẢ ---");
        String userId = DataInput.getNonEmptyString("Nhập mã độc giả: ");
        Member member = memberDAO.findById(userId);
        if (member == null) {
            System.out.println(">> Không tìm thấy độc giả có mã: " + userId);
            return;
        }
        member.toggleStatus();
        if (member.isActive() && member.getReputationPoints() < 0) {
            member.setReputationPoints(50); // Khôi phục điểm mặc định khi mở khóa thẻ
        }
        memberDAO.saveToFile();
        System.out.println(">> Đã chuyển trạng thái của độc giả " + member.getFullName() + " sang: " + member.getStatusText());
    }

    private void searchMemberSubMenu() {
        System.out.println("1. Tìm theo Mã Độc Giả | 2. Tìm theo Họ Tên | 3. Tìm theo SĐT | 4. Lọc theo Hạng Thẻ (VIP/GOLD/SILVER)");
        int choice = DataInput.getIntegerNumber("Chọn tiêu chí tìm kiếm [1-4]: ");
        switch (choice) {
            case 1:
                String id = DataInput.getNonEmptyString("Nhập mã độc giả: ");
                Member m = memberDAO.findById(id);
                if (m != null) {
                    System.out.println(">> Kết quả: " + m);
                } else {
                    System.out.println(">> Không tìm thấy độc giả có mã: " + id);
                }
                break;
            case 2:
                String kw = DataInput.getNonEmptyString("Nhập từ khóa họ tên: ");
                displayMembers(memberDAO.searchByName(kw), "KẾT QUẢ TÌM KIẾM THEO TÊN: " + kw);
                break;
            case 3:
                String phone = DataInput.getNonEmptyString("Nhập số điện thoại cần tìm: ");
                displayMembers(memberDAO.searchByPhone(phone), "KẾT QUẢ TÌM KIẾM THEO SĐT: " + phone);
                break;
            case 4:
                String tier = DataInput.getNonEmptyString("Nhập hạng thẻ cần lọc (VIP / GOLD / SILVER): ").toUpperCase();
                displayMembers(memberDAO.filterByTier(tier), "DANH SÁCH ĐỘC GIẢ HẠNG THẺ: " + tier);
                break;
            default:
                System.out.println(">> Lựa chọn không hợp lệ!");
                break;
        }
    }

    private void sortMemberSubMenu() {
        System.out.println("1. Sắp xếp theo Điểm Uy Tín (Cao -> Thấp) | 2. Sắp xếp theo Họ Tên (A -> Z) | 3. Sắp xếp theo Mã ID");
        int choice = DataInput.getIntegerNumber("Chọn kiểu sắp xếp [1-3]: ");
        if (choice == 1) {
            displayMembers(memberDAO.sortByReputationDescending(), "BẢNG XẾP HẠNG ĐỘC GIẢ THEO ĐIỂM UY TÍN (CAO -> THẤP)");
        } else if (choice == 2) {
            displayMembers(memberDAO.sortByNameAscending(), "DANH SÁCH ĐỘC GIẢ SẮP XẾP THEO TÊN (A-Z)");
        } else if (choice == 3) {
            displayMembers(memberDAO.sortByIdAscending(), "DANH SÁCH ĐỘC GIẢ SẮP XẾP THEO MÃ ID");
        } else {
            System.out.println(">> Lựa chọn không hợp lệ!");
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
        labels.add("Hạng VIP (>=100đ, -25%)");
        labels.add("Hạng GOLD (60-99đ, -10%)");
        labels.add("Hạng SILVER (<60đ, 0%)");
        labels.add("Thẻ Đang Bị Khóa");

        List<Integer> values = new ArrayList<>();
        values.add((int) vipCount);
        values.add((int) goldCount);
        values.add((int) silverCount);
        values.add((int) lockedCount);

        Menu.printAsciiBarChart("PHÂN BỐ HẠNG THẺ ĐỘC GIẢ THƯ VIỆN", labels, values, "độc giả");
    }
}
