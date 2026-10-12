package BusinessObject.UI;

import Core.Entities.Book;
import Core.Entities.Member;
import DataObjects.DAO.BookDAO;
import DataObjects.DAO.BorrowDAO;
import DataObjects.DAO.FileManager;
import DataObjects.DAO.IBookDAO;
import DataObjects.DAO.IBorrowDAO;
import DataObjects.DAO.IMemberDAO;
import DataObjects.DAO.MemberDAO;
import Utilities.DataInput;
import Utilities.Menu;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Điểm khởi chạy chính của chương trình Quản lý Thư viện (Library Management System).
 * Điều phối toàn bộ 3 phân hệ: Sách (Book), Độc giả (Member), Mượn - Trả (BorrowRecord)
 * kèm Biểu đồ trực quan ASCII (Tính năng 8) và Xuất báo cáo tổng hợp (Tính năng 9).
 * 
 * PRO192 - Tầng Điều phối & Tiện ích (Member 4)
 */
public class Program {

    public static void main(String[] args) {
        IBookDAO bookDAO = new BookDAO();
        IMemberDAO memberDAO = new MemberDAO();
        IBorrowDAO borrowDAO = new BorrowDAO(memberDAO, bookDAO);

        BookManagement bookMenu = new BookManagement(bookDAO);
        MemberManagement memberMenu = new MemberManagement(memberDAO);
        BorrowManagement borrowMenu = new BorrowManagement(borrowDAO, memberDAO, bookDAO);

        boolean running = true;
        while (running) {
            printMainBanner();
            int choice = DataInput.getIntegerNumber(">> Chọn phân hệ làm việc [0-6]: ");
            System.out.println();
            switch (choice) {
                case 1:
                    bookMenu.processMenuForBook();
                    break;
                case 2:
                    memberMenu.processMenuForMember();
                    break;
                case 3:
                    borrowMenu.processMenuForBorrow();
                    break;
                case 4:
                    displaySystemDashboard(bookDAO, memberDAO, borrowDAO);
                    System.out.println("\nNhấn Enter để tiếp tục...");
                    DataInput.getString("");
                    break;
                case 5:
                    exportSystemReportToFile(bookDAO, memberDAO, borrowDAO);
                    System.out.println("\nNhấn Enter để tiếp tục...");
                    DataInput.getString("");
                    break;
                case 6:
                    saveAllData(bookDAO, memberDAO, borrowDAO);
                    System.out.println("\nNhấn Enter để tiếp tục...");
                    DataInput.getString("");
                    break;
                case 0:
                    saveAllData(bookDAO, memberDAO, borrowDAO);
                    System.out.println("==========================================================================");
                    System.out.println("   Cảm ơn bạn đã sử dụng Hệ thống Quản lý Thư viện! Hẹn gặp lại!          ");
                    System.out.println("==========================================================================");
                    running = false;
                    break;
                default:
                    System.out.println(">> Lựa chọn không hợp lệ! Vui lòng chọn từ 0 đến 6.");
                    break;
            }
        }
    }

    private static void printMainBanner() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║         HỆ THỐNG QUẢN LÝ THƯ VIỆN (LIBRARY MANAGEMENT SYSTEM)          ║");
        System.out.println("║                    ĐỒ ÁN MÔN HỌC PRO192 - FALL 2026                    ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Quản lý Kho Tài liệu & Sách       (Đa hình, Đánh giá ⭐, Đặt trước) ║");
        System.out.println("║  2. Quản lý Độc giả Thư viện          (Hạng thẻ VIP/GOLD/SILVER, Điểm) ║");
        System.out.println("║  3. Quản lý Giao dịch Mượn - Trả      (Gia hạn, Đền bù, Xuất Biên lai) ║");
        System.out.println("║  4. Bảng Tổng hợp & Biểu đồ Hệ thống  (Dashboard & ASCII Bar Charts)   ║");
        System.out.println("║  5. Xuất Báo cáo Tổng kết ra File     (Library_Summary_Report.txt)     ║");
        System.out.println("║  6. Sao lưu Toàn bộ Dữ liệu ra File   (Books / Members / BorrowRecords)║");
        System.out.println("║  0. Lưu dữ liệu & Thoát chương trình                                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════╝");
    }

    private static void displaySystemDashboard(IBookDAO bookDAO, IMemberDAO memberDAO, IBorrowDAO borrowDAO) {
        List<Book> books = bookDAO.getBookList();
        int totalQty = 0;
        for (int i = 0; i < books.size(); i++) {
            totalQty += books.get(i).getQuantity();
        }
        System.out.println("==========================================================================");
        System.out.println("                BẢNG TỔNG HỢP TOÀN HỆ THỐNG (SYSTEM DASHBOARD)            ");
        System.out.println("==========================================================================");
        System.out.printf("  [1. KHO SÁCH]     Tổng số đầu sách: %d đầu sách (%d cuốn tồn kho)\n",
                books.size(), totalQty);
        System.out.printf("                    Tổng giá trị tài sản kho sách: %,.0f VNĐ\n",
                bookDAO.calculateTotalInventoryValue());
        System.out.printf("  [2. ĐỘC GIẢ]      Tổng số thẻ độc giả: %d độc giả (VIP: %d | GOLD: %d | SILVER: %d)\n",
                memberDAO.getAll().size(),
                memberDAO.filterByTier("VIP").size(),
                memberDAO.filterByTier("GOLD").size(),
                memberDAO.filterByTier("SILVER").size());
        System.out.printf("  [3. MƯỢN - TRẢ]   Tổng số phiếu mượn: %d phiếu (Đang mượn: %d | Quá hạn: %d)\n",
                borrowDAO.getAll().size(),
                borrowDAO.getActiveBorrowRecords().size(),
                borrowDAO.getOverdueRecords().size());
        System.out.printf("                    Phí mượn: %,.0f VNĐ | Tiền phạt: %,.0f VNĐ | Đền bù: %,.0f VNĐ\n",
                borrowDAO.calculateTotalBorrowFeeRevenue(),
                borrowDAO.calculateTotalFineRevenue(),
                borrowDAO.calculateTotalCompensationRevenue());
        System.out.println("==========================================================================");

        // Tính năng 8: Vẽ biểu đồ ASCII tồn kho trên Dashboard
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            labels.add("[" + b.getBookId() + "] " + b.getTitle());
            values.add(b.getQuantity());
        }
        Menu.printAsciiBarChart("TỒN KHO SÁCH HIỆN TẠI", labels, values, "cuốn");
    }

    /**
     * Tính năng 9: Xuất báo cáo tổng hợp toàn thư viện ra file Library_Summary_Report.txt
     */
    private static void exportSystemReportToFile(IBookDAO bookDAO, IMemberDAO memberDAO, IBorrowDAO borrowDAO) {
        String reportFile = "Library_Summary_Report.txt";
        List<String> lines = new ArrayList<>();
        List<Book> books = bookDAO.getBookList();
        List<Member> members = memberDAO.getAll();

        int totalQty = 0;
        for (int i = 0; i < books.size(); i++) {
            totalQty += books.get(i).getQuantity();
        }

        lines.add("==========================================================================");
        lines.add("          BÁO CÁO TỔNG KẾT HOẠT ĐỘNG HỆ THỐNG QUẢN LÝ THƯ VIỆN            ");
        lines.add("          Ngày xuất báo cáo: " + LocalDate.now().format(Core.Entities.User.DATE_FORMAT));
        lines.add("==========================================================================");
        lines.add("1. THỐNG KÊ KHO SÁCH:");
        lines.add("   - Tổng số đầu sách           : " + books.size() + " đầu sách");
        lines.add("   - Tổng số bản sách tồn kho   : " + totalQty + " cuốn");
        lines.add(String.format("   - Tổng giá trị tài sản kho   : %,.0f VNĐ", bookDAO.calculateTotalInventoryValue()));
        lines.add("--------------------------------------------------------------------------");
        lines.add("2. THỐNG KÊ ĐỘC GIẢ & HẠNG THẺ:");
        lines.add("   - Tổng số độc giả            : " + members.size() + " độc giả");
        lines.add("   - Hạng VIP (-25% phí mượn)   : " + memberDAO.filterByTier("VIP").size() + " độc giả");
        lines.add("   - Hạng GOLD (-10% phí mượn)  : " + memberDAO.filterByTier("GOLD").size() + " độc giả");
        lines.add("   - Hạng SILVER (0% ưu đãi)    : " + memberDAO.filterByTier("SILVER").size() + " độc giả");
        lines.add("--------------------------------------------------------------------------");
        lines.add("3. THỐNG KÊ GIAO DỊCH MƯỢN - TRẢ & TÀI CHÍNH:");
        lines.add("   - Tổng số phiếu mượn         : " + borrowDAO.getAll().size() + " phiếu");
        lines.add("   - Số phiếu đang mượn         : " + borrowDAO.getActiveBorrowRecords().size() + " phiếu");
        lines.add("   - Số phiếu quá hạn (Overdue) : " + borrowDAO.getOverdueRecords().size() + " phiếu");
        lines.add(String.format("   - Doanh thu phí mượn sách    : %,.0f VNĐ", borrowDAO.calculateTotalBorrowFeeRevenue()));
        lines.add(String.format("   - Tổng tiền phạt trễ hạn     : %,.0f VNĐ", borrowDAO.calculateTotalFineRevenue()));
        lines.add(String.format("   - Tiền đền bù sách mất/hỏng  : %,.0f VNĐ", borrowDAO.calculateTotalCompensationRevenue()));
        lines.add("==========================================================================");

        try {
            FileManager.writeLines(reportFile, lines);
            System.out.println(">> Đã xuất Báo cáo Tổng kết Hệ thống ra file: " + reportFile + " thành công!");
        } catch (Exception e) {
            System.out.println(">> Lỗi khi xuất file báo cáo: " + e.getMessage());
        }
    }

    private static void saveAllData(IBookDAO bookDAO, IMemberDAO memberDAO, IBorrowDAO borrowDAO) {
        boolean bOk = bookDAO.saveToFile();
        boolean mOk = memberDAO.saveToFile();
        boolean rOk = borrowDAO.saveToFile();
        if (bOk && mOk && rOk) {
            System.out.println(">> Đã đồng bộ toàn bộ dữ liệu ra 3 file: Books.txt, Members.txt, BorrowRecords.txt!");
        } else {
            System.out.println(">> Có lỗi xảy ra khi lưu một số file dữ liệu!");
        }
    }
}
