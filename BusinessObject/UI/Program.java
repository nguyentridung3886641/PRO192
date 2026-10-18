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
import java.util.stream.Collectors;

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
            int choice = DataInput.getIntegerNumber(">> Select subsystem [0-6]: ");
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
                    System.out.println("\nPress Enter to continue...");
                    DataInput.getString("");
                    break;
                case 5:
                    exportSystemReportToFile(bookDAO, memberDAO, borrowDAO);
                    System.out.println("\nPress Enter to continue...");
                    DataInput.getString("");
                    break;
                case 6:
                    saveAllData(bookDAO, memberDAO, borrowDAO);
                    System.out.println("\nPress Enter to continue...");
                    DataInput.getString("");
                    break;
                case 0:
                    saveAllData(bookDAO, memberDAO, borrowDAO);
                    System.out.println("==========================================================================");
                    System.out.println("   Thank you for using the Library Management System! See you again!     ");
                    System.out.println("==========================================================================");
                    running = false;
                    break;
                default:
                    System.out.println(">> Invalid choice! Please select from 0 to 6.");
                    break;
            }
        }
    }

    /**
     * [LAMBDA & STREAM API]: Refactored to utilize Menu.print()
     * Parses pipe-delimited menu options through an Arrays.stream pipeline.
     */
    private static void printMainBanner() {
        System.out.println("\n" + "=".repeat(74));
        System.out.println("                      LIBRARY MANAGEMENT SYSTEM                           ");
        System.out.println("                   PRO192 PROJECT - FALL SEMESTER 2026                    ");
        System.out.println("=".repeat(74));
        Menu.print(
            "  1. Book & Inventory Management    (Polymorphism, Rating, Reserve)|" +
            "  2. Library Member Management      (Tiers VIP/GOLD/SILVER, Points)|" +
            "  3. Borrow & Return Transactions   (Extension, Damage Fee, Receipt)|" +
            "  4. System Summary & Visual Charts (Dashboard & ASCII Bar Charts)|" +
            "  5. Export Summary Report to File  (Library_Summary_Report.txt)|" +
            "  6. Backup All Data to Files       (Books / Members / BorrowRecords)|" +
            "  0. Save Data & Exit Program"
        );
        System.out.println("=".repeat(74));
    }

    private static void displaySystemDashboard(IBookDAO bookDAO, IMemberDAO memberDAO, IBorrowDAO borrowDAO) {
        List<Book> books = bookDAO.getBookList();

        System.out.println("==========================================================================");
        System.out.println("                      SYSTEM DASHBOARD SUMMARY                            ");
        System.out.println("==========================================================================");
        System.out.printf("  [1. BOOK INVENTORY]   Total Book Titles: %d titles (%d copies in stock)\n",
                bookDAO.getTotalBooksCount(), bookDAO.getTotalQuantity());
        System.out.printf("                        Total Inventory Asset Value: %,.0f VND\n",
                bookDAO.calculateTotalInventoryValue());
        System.out.printf("  [2. MEMBERS]          Total Registered Members: %d (VIP: %d | GOLD: %d | SILVER: %d)\n",
                memberDAO.getAll().size(),
                memberDAO.filterByTier("VIP").size(),
                memberDAO.filterByTier("GOLD").size(),
                memberDAO.filterByTier("SILVER").size());
        System.out.printf("  [3. TRANSACTIONS]     Total Borrow Records: %d (Borrowing: %d | Overdue: %d)\n",
                borrowDAO.getAll().size(),
                borrowDAO.getActiveBorrowRecords().size(),
                borrowDAO.getOverdueRecords().size());
        System.out.printf("                        Borrow Fees: %,.0f VND | Fines: %,.0f VND | Damage Fees: %,.0f VND\n",
                borrowDAO.calculateTotalBorrowFeeRevenue(),
                borrowDAO.calculateTotalFineRevenue(),
                borrowDAO.calculateTotalCompensationRevenue());
        System.out.println("==========================================================================");

        // [LAMBDA & STREAM API]: Tính năng 8 - Vẽ biểu đồ ASCII tồn kho trên Dashboard
        List<String> labels = books.stream()
                .map(b -> "[" + b.getBookId() + "] " + b.getTitle())
                .collect(Collectors.toList());
        List<Integer> values = books.stream()
                .map(Book::getQuantity)
                .collect(Collectors.toList());

        Menu.printAsciiBarChart("CURRENT BOOK INVENTORY", labels, values, "copies");
    }

    /**
     * Tính năng 9: Xuất báo cáo tổng hợp toàn thư viện ra file Library_Summary_Report.txt
     */
    private static void exportSystemReportToFile(IBookDAO bookDAO, IMemberDAO memberDAO, IBorrowDAO borrowDAO) {
        String reportFile = "Library_Summary_Report.txt";
        List<String> lines = new ArrayList<>();
        List<Member> members = memberDAO.getAll();

        lines.add("==========================================================================");
        lines.add("              LIBRARY MANAGEMENT SYSTEM - SUMMARY REPORT                  ");
        lines.add("              Report Date: " + LocalDate.now().format(Core.Entities.User.DATE_FORMAT));
        lines.add("==========================================================================");
        lines.add("1. BOOK INVENTORY STATISTICS:");
        lines.add("   - Total Book Titles          : " + bookDAO.getTotalBooksCount() + " titles");
        lines.add("   - Total Copies in Stock      : " + bookDAO.getTotalQuantity() + " copies");
        lines.add(String.format("   - Total Inventory Asset Value: %,.0f VND", bookDAO.calculateTotalInventoryValue()));
        lines.add("--------------------------------------------------------------------------");
        lines.add("2. MEMBER & MEMBERSHIP TIER STATISTICS:");
        lines.add("   - Total Registered Members   : " + members.size() + " members");
        lines.add("   - VIP Tier (-25% borrow fee) : " + memberDAO.filterByTier("VIP").size() + " members");
        lines.add("   - GOLD Tier (-10% borrow fee): " + memberDAO.filterByTier("GOLD").size() + " members");
        lines.add("   - SILVER Tier (0% discount)  : " + memberDAO.filterByTier("SILVER").size() + " members");
        lines.add("--------------------------------------------------------------------------");
        lines.add("3. BORROW - RETURN TRANSACTIONS & FINANCIALS:");
        lines.add("   - Total Borrow Records       : " + borrowDAO.getAll().size() + " records");
        lines.add("   - Currently Borrowing Records: " + borrowDAO.getActiveBorrowRecords().size() + " records");
        lines.add("   - Overdue Records            : " + borrowDAO.getOverdueRecords().size() + " records");
        lines.add(String.format("   - Borrow Fee Revenue         : %,.0f VND", borrowDAO.calculateTotalBorrowFeeRevenue()));
        lines.add(String.format("   - Total Overdue Fines        : %,.0f VND", borrowDAO.calculateTotalFineRevenue()));
        lines.add(String.format("   - Book Loss/Damage Fee       : %,.0f VND", borrowDAO.calculateTotalCompensationRevenue()));
        lines.add("==========================================================================");

        try {
            FileManager.writeLines(reportFile, lines);
            System.out.println(">> Successfully exported System Summary Report to: " + reportFile + "!");
        } catch (Exception e) {
            System.out.println(">> Error exporting report file: " + e.getMessage());
        }
    }

    private static void saveAllData(IBookDAO bookDAO, IMemberDAO memberDAO, IBorrowDAO borrowDAO) {
        boolean bOk = bookDAO.saveToFile();
        boolean mOk = memberDAO.saveToFile();
        boolean rOk = borrowDAO.saveToFile();
        if (bOk && mOk && rOk) {
            System.out.println(">> Successfully synchronized all data to Books.txt, Members.txt, BorrowRecords.txt!");
        } else {
            System.out.println(">> An error occurred while saving some data files!");
        }
    }
}
