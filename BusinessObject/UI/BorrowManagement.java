package BusinessObject.UI;

import Core.Entities.Book;
import Core.Entities.BorrowRecord;
import Core.Entities.Member;
import DataObjects.DAO.BorrowDAO;
import DataObjects.DAO.IBookDAO;
import DataObjects.DAO.IBorrowDAO;
import DataObjects.DAO.IMemberDAO;
import Utilities.DataInput;
import Utilities.DataValidation;
import Utilities.Menu;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Giao diện điều khiển phân hệ Quản lý Giao dịch Mượn - Trả sách (Borrow Management UI).
 * Tích hợp đầy đủ các Tính năng 1, 5, 6, 7, 8, 9 (Viết bằng vòng lặp for và if-else truyền thống).
 * 
 * PRO192 - Phân hệ Giao dịch Mượn - Trả (Member 3)
 */
public class BorrowManagement {

    private final IBorrowDAO borrowDAO;
    private final IMemberDAO memberDAO;
    private final IBookDAO bookDAO;

    public BorrowManagement(IBorrowDAO borrowDAO, IMemberDAO memberDAO, IBookDAO bookDAO) {
        this.memberDAO = memberDAO;
        this.bookDAO = bookDAO;
        this.borrowDAO = (borrowDAO != null) ? borrowDAO : new BorrowDAO(memberDAO, bookDAO);
    }

    public void processMenuForBorrow() {
        boolean running = true;
        while (running) {
            printMenuHeader();
            int choice = DataInput.getIntegerNumber(">> Enter your choice [0-10]: ");
            System.out.println();
            switch (choice) {
                case 1:
                    displayBorrowRecords(borrowDAO.getAll(), "ALL BORROW & RETURN RECORDS");
                    break;
                case 2:
                    createNewBorrowRecord();
                    break;
                case 3:
                    processReturnBooks();
                    break;
                case 4:
                    extendLoanPeriod();
                    break;
                case 5:
                    searchBorrowSubMenu();
                    break;
                case 6:
                    displayBorrowRecords(borrowDAO.getOverdueRecords(), "OVERDUE BORROW RECORDS");
                    break;
                case 7:
                    displayBorrowStatistics();
                    break;
                case 8:
                    exportReceiptMenu();
                    break;
                case 9:
                    deleteBorrowRecord();
                    break;
                case 10:
                    if (borrowDAO.saveToFile()) {
                        System.out.println(">> Successfully saved borrow records to BorrowRecords.txt!");
                    }
                    break;
                case 0:
                    borrowDAO.saveToFile();
                    System.out.println(">> Borrow records saved. Returning to Main Menu!");
                    running = false;
                    break;
                default:
                    System.out.println(">> Invalid choice! Please select from 0 to 10.");
                    break;
            }
            if (running) {
                System.out.println("\nPress Enter to continue...");
                DataInput.getString("");
            }
        }
    }

    private void printMenuHeader() {
        System.out.println("\n==========================================================================================================");
        System.out.println("                      BORROW & RETURN TRANSACTIONS MANAGEMENT SYSTEM                                      ");
        System.out.println("==========================================================================================================");
        System.out.println("  1. Display all borrow & return records (Includes Tier Discount, Fines & Damage Fees)");
        System.out.println("  2. Create new borrow record (Applies Tier limits VIP/GOLD/SILVER & Book Reservation)");
        System.out.println("  3. Process book return, report damage/loss (150%), update reputation points & export receipt");
        System.out.println("  4. Extend loan period (+7 days for non-overdue records - Feature 5)");
        System.out.println("  5. Search borrow records (By Record ID / Member ID / Active Records)");
        System.out.println("  6. Filter OVERDUE records");
        System.out.println("  7. Borrow statistics report & Top borrowed books ASCII chart (Feature 8)");
        System.out.println("  8. Export transaction receipt to file Receipt_<RecordID>.txt (Feature 9)");
        System.out.println("  9. Delete borrow record by ID");
        System.out.println(" 10. Save borrow records to file (BorrowRecords.txt)");
        System.out.println("  0. Save & return to Main Menu");
        System.out.println("==========================================================================================================");
    }

    public void displayBorrowRecords(List<BorrowRecord> records, String title) {
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        System.out.println("  " + title + " (Total: " + records.size() + " records)");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        if (records.isEmpty()) {
            System.out.println("  >> No borrow records found in the list!");
            System.out.println("--------------------------------------------------------------------------------------------------------------------");
            return;
        }
        System.out.printf("| %-6s | %-18s | %-6s | %-4s | %-10s | %-10s | %-10s | %-18s | %-10s | %-11s |\n",
                "ID", "Member Name", "Tier", "Qty", "BorrowDate", "Due Date", "Ret. Date", "Status", "Fine", "Total Pay");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        // [LAMBDA & STREAM API]: Hiển thị danh sách phiếu mượn bằng forEach Lambda
        records.forEach(r -> {
            String bDate = (r.getBorrowDate() != null) ? r.getBorrowDate().format(BorrowRecord.DATE_FORMAT) : "N/A";
            String dDate = (r.getDueDate() != null) ? r.getDueDate().format(BorrowRecord.DATE_FORMAT) : "N/A";
            String rDate = (r.getReturnDate() != null) ? r.getReturnDate().format(BorrowRecord.DATE_FORMAT) : "Not returned";
            System.out.printf("| %-6s | %-18s | %-6s | %-4d | %-10s | %-10s | %-10s | %-18s | %,10.0f | %,11.0f |\n",
                    r.getRecordId(), r.getMember().getFullName(), r.getMember().getMembershipTier(),
                    r.getBorrowedBooks().size(), bDate, dDate, rDate,
                    r.getStatusText(), r.calculateFine(), r.calculateFinalPayment());
        });
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
    }

    private void createNewBorrowRecord() {
        System.out.println("--- CREATE NEW BORROW RECORD ---");
        String recordId;
        while (true) {
            recordId = DataInput.getNonEmptyString("Enter borrow record ID (e.g. BR004): ").toUpperCase();
            if (!DataValidation.isValidId(recordId, "BR")) {
                System.out.println(">> Error: Borrow record ID must be in format BRxxx (e.g. BR001)!");
                continue;
            }
            if (borrowDAO.findById(recordId) != null) {
                System.out.println(">> Error: Borrow record ID already exists!");
            } else {
                break;
            }
        }

        String memberId = DataInput.getNonEmptyString("Enter member ID borrowing books (e.g. M001): ").toUpperCase();
        Member member = memberDAO.findById(memberId);
        if (member == null) {
            System.out.println(">> Error: Member with ID " + memberId + " not found!");
            return;
        }
        if (!member.isActive()) {
            System.out.println(">> Error: Member " + member.getFullName() + "'s card is currently LOCKED (Reputation points: "
                    + member.getReputationPoints() + " pts), cannot borrow books!");
            return;
        }

        int maxLimit = member.getMaxBorrowLimit();
        int currentBorrowed = borrowDAO.countActiveBorrowedBooksByMember(memberId);
        int remainingQuota = maxLimit - currentBorrowed;
        if (remainingQuota <= 0) {
            System.out.println(">> Error: Member tier " + member.getMembershipTier() + " is currently borrowing "
                    + currentBorrowed + " books (Reached maximum quota of " + maxLimit + " books)!");
            return;
        }
        System.out.printf(">> Member: %s | Tier: %s (Discount -%.0f%%) | Remaining quota: %d book(s).\n",
                member.getFullName(), member.getMembershipTier(), member.getDiscountRate() * 100, remainingQuota);

        int numBooks = DataInput.getIntegerNumber("Enter number of book titles to borrow [1-" + remainingQuota + "]: ", 1, remainingQuota);
        List<Book> selectedBooks = new ArrayList<>();

        // [LAMBDA & STREAM API]: Duyệt từng lượt nhập sách bằng IntStream.rangeClosed
        IntStream.rangeClosed(1, numBooks).forEach(i -> {
            while (true) {
                String bookId = DataInput.getNonEmptyString("  Enter book ID for item " + i + " (e.g. B001): ").toUpperCase();
                Book book = bookDAO.findBookById(bookId);
                if (book == null) {
                    System.out.println("  >> Book with ID " + bookId + " not found!");
                    continue;
                }
                if (book.getQuantity() <= 0) {
                    System.out.println("  >> Book '" + book.getTitle() + "' is currently OUT OF STOCK (Qty = 0)!");
                    // Tính năng 7: Cho phép đặt trước ngay khi hết sách
                    if (DataInput.getBoolean("  >> Would you like to RESERVE this book?")) {
                        if (bookDAO.reserveBook(bookId, memberId)) {
                            System.out.println("  >> Successfully placed member " + memberId + " in the priority queue for '" + book.getTitle() + "'!");
                        } else {
                            System.out.println("  >> Member is already in the reservation queue for this book!");
                        }
                    }
                    continue;
                }
                // [LAMBDA & STREAM API]: Kiểm tra sách đã được chọn chưa bằng anyMatch
                boolean alreadySelected = selectedBooks.stream()
                        .anyMatch(b -> b.getBookId().equalsIgnoreCase(bookId));
                if (alreadySelected) {
                    System.out.println("  >> This book is already in the borrow list!");
                    continue;
                }
                selectedBooks.add(book);
                System.out.println("  >> Selected: " + book.getTitle() + " (Remaining in stock: " + book.getQuantity() + ")");
                break;
            }
        });

        int borrowDays = DataInput.getIntegerNumber("Enter borrow period in days [1-30] (Default 14 days): ", 1, 30);
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(borrowDays);

        BorrowRecord newRecord = new BorrowRecord(recordId, member, selectedBooks, borrowDate, dueDate);
        if (borrowDAO.createBorrowRecord(newRecord)) {
            System.out.println("\n>> BORROW RECORD CREATED SUCCESSFULLY!");
            System.out.println(newRecord);
        } else {
            System.out.println(">> Failed to create borrow record!");
        }
    }

    /**
     * Features 1, 6, 7, 9: Return books, damage inspection, reputation points update & receipt generation.
     */
    private void processReturnBooks() {
        System.out.println("--- PROCESS BOOK RETURN, DAMAGE INSPECTION & RECEIPT GENERATION ---");
        String recordId = DataInput.getNonEmptyString("Enter borrow record ID to return (e.g. BR002): ").toUpperCase();
        BorrowRecord record = borrowDAO.findById(recordId);
        if (record == null) {
            System.out.println(">> No borrow record found with ID: " + recordId);
            return;
        }
        if (record.isReturned()) {
            System.out.println(">> This borrow record was already returned on "
                    + record.getReturnDate().format(BorrowRecord.DATE_FORMAT) + "!");
            return;
        }

        System.out.println("Current borrow record information:");
        System.out.println(record);

        LocalDate returnDate = DataInput.getDate("Enter actual return date (Press Enter for today)");
        if (returnDate == null) {
            returnDate = LocalDate.now();
        }
        if (returnDate.isBefore(record.getBorrowDate())) {
            System.out.println(">> Error: Return date cannot be before borrow date ("
                    + record.getBorrowDate().format(BorrowRecord.DATE_FORMAT) + ")!");
            return;
        }

        // Tính năng 6: Kiểm tra xem có cuốn sách nào bị làm mất hoặc hư hỏng không
        List<String> damagedBookIds = new ArrayList<>();
        if (DataInput.getBoolean("Are any books LOST or SEVERELY DAMAGED?")) {
            record.getBorrowedBooks().forEach(b -> {
                boolean isLost = DataInput.getBoolean("  - Is book [" + b.getBookId() + "] '" + b.getTitle() + "' lost/damaged?");
                if (isLost) {
                    damagedBookIds.add(b.getBookId());
                    System.out.printf("    -> 150%% Compensation Fee: %,.0f VND\n", b.calculatePrice() * 1.5);
                }
            });
        }

        if (borrowDAO.returnBooksWithDamageCheck(recordId, returnDate, damagedBookIds)) {
            Member m = record.getMember();
            System.out.println("\n==================== BOOK RETURN RECEIPT ====================");
            System.out.println(record);
            double rawFee = record.calculateTotalBorrowFee();
            double discountedFee = record.calculateDiscountedBorrowFee();
            double fine = record.calculateFine();
            double comp = record.getCompensationFee();
            System.out.printf("  + Base Borrow Fee (Polymorphic)     : %,14.0f VND\n", rawFee);
            System.out.printf("  + Tier Discount %-6s (-%2.0f%%)       : -%,13.0f VND\n",
                    m.getMembershipTier(), m.getDiscountRate() * 100, rawFee - discountedFee);
            System.out.printf("  + Fee After Discount                : %,14.0f VND\n", discountedFee);
            System.out.printf("  + Overdue Fine (%2d days)            : %,14.0f VND\n", record.getOverdueDays(), fine);
            System.out.printf("  + Book Damage/Loss Fee (150%%)       : %,14.0f VND\n", comp);
            System.out.println("---------------------------------------------------------------");
            System.out.printf("  => FINAL TOTAL PAYMENT              : %,14.0f VND\n", record.calculateFinalPayment());
            System.out.printf("  * Member's New Reputation Points    : %d pts (Tier: %s)\n",
                    m.getReputationPoints(), m.getMembershipTier());
            System.out.println("===============================================================");

            // Tính năng 9: Tự động xuất biên lai ra file Receipt_<MãPhiếu>.txt
            if (borrowDAO.exportReceiptToFile(recordId)) {
                System.out.println(">> Receipt automatically exported to file: Receipt_" + recordId + ".txt!");
            }
        }
    }

    /**
     * Feature 5: Extend loan period (+7 days).
     */
    private void extendLoanPeriod() {
        System.out.println("--- EXTEND LOAN PERIOD (FEATURE 5) ---");
        String recordId = DataInput.getNonEmptyString("Enter borrow record ID to extend (e.g. BR001): ").toUpperCase();
        BorrowRecord record = borrowDAO.findById(recordId);
        if (record == null) {
            System.out.println(">> No borrow record found with ID: " + recordId);
            return;
        }
        if (record.isReturned()) {
            System.out.println(">> Error: This record has already been returned, cannot extend!");
            return;
        }
        if (record.isOverdue()) {
            System.out.println(">> Error: This record is OVERDUE, cannot extend! Please return books.");
            return;
        }
        if (record.getExtensionCount() >= BorrowRecord.MAX_EXTENSIONS) {
            System.out.println(">> Error: Maximum extension limit reached (Max " + BorrowRecord.MAX_EXTENSIONS + " time(s))!");
            return;
        }

        int extraDays = DataInput.getIntegerNumber("Enter extra days to extend [1-7]: ", 1, 7);
        if (borrowDAO.extendBorrowRecord(recordId, extraDays)) {
            System.out.println(">> Successfully extended loan by " + extraDays + " days! New due date is: "
                    + record.getDueDate().format(BorrowRecord.DATE_FORMAT));
        } else {
            System.out.println(">> Failed to extend borrow record!");
        }
    }

    private void exportReceiptMenu() {
        System.out.println("--- EXPORT TRANSACTION RECEIPT TO FILE (FEATURE 9) ---");
        String recordId = DataInput.getNonEmptyString("Enter borrow record ID to export (e.g. BR001): ").toUpperCase();
        if (borrowDAO.exportReceiptToFile(recordId)) {
            System.out.println(">> Successfully exported receipt to file: Receipt_" + recordId + ".txt!");
        } else {
            System.out.println(">> Record not found or error writing file!");
        }
    }

    private void searchBorrowSubMenu() {
        System.out.println("1. Find by Record ID | 2. Find by Member ID | 3. View Active Borrow Records");
        int choice = DataInput.getIntegerNumber("Choose option [1-3]: ");
        switch (choice) {
            case 1:
                String rId = DataInput.getNonEmptyString("Enter borrow record ID: ");
                BorrowRecord r = borrowDAO.findById(rId);
                if (r != null) {
                    System.out.println("\n" + r);
                } else {
                    System.out.println(">> No borrow record found with ID: " + rId);
                }
                break;
            case 2:
                String mId = DataInput.getNonEmptyString("Enter Member ID: ");
                displayBorrowRecords(borrowDAO.findByMemberId(mId), "BORROW HISTORY OF MEMBER: " + mId.toUpperCase());
                break;
            case 3:
                displayBorrowRecords(borrowDAO.getActiveBorrowRecords(), "ACTIVE UNRETURNED BORROW RECORDS");
                break;
            default:
                System.out.println(">> Invalid choice!");
                break;
        }
    }

    private void displayBorrowStatistics() {
        List<BorrowRecord> all = borrowDAO.getAll();
        List<BorrowRecord> active = borrowDAO.getActiveBorrowRecords();
        List<BorrowRecord> overdue = borrowDAO.getOverdueRecords();

        System.out.println("==========================================================================");
        System.out.println("                 BORROW & RETURN TRANSACTIONS REPORT                      ");
        System.out.println("==========================================================================");
        System.out.printf("  - Total Borrow Records Created       : %d records\n", all.size());
        System.out.printf("  - Active Records (Not Returned)      : %d records\n", active.size());
        System.out.printf("  - Completed Records (Returned)       : %d records\n", all.size() - active.size());
        System.out.printf("  - Overdue Records                    : %d records\n", overdue.size());
        System.out.printf("  - Total Borrow Fee Revenue (Discount): %,.0f VND\n", borrowDAO.calculateTotalBorrowFeeRevenue());
        System.out.printf("  - Total Overdue Fine Revenue         : %,.0f VND\n", borrowDAO.calculateTotalFineRevenue());
        System.out.printf("  - Total Book Damage/Loss Fees        : %,.0f VND\n", borrowDAO.calculateTotalCompensationRevenue());
        System.out.println("==========================================================================");

        // [LAMBDA & STREAM API]: Tính năng 8 - Thống kê số lượt mượn từng cuốn sách
        // 
        // So sánh:
        // - Cách cũ: Dùng vòng lặp for lồng nhau 2 cấp (phiếu -> sách) kết hợp Map get/put thủ công.
        // - Lambda/Stream:
        //   1. all.stream(): Tạo luồng từ danh sách toàn bộ phiếu mượn
        //   2. flatMap(r -> r.getBorrowedBooks().stream()): "Làm phẳng" danh sách sách của từng phiếu thành luồng phẳng
        //   3. collect(Collectors.groupingBy(Book::getBookId, Collectors.counting())): Tự động phân nhóm theo ID và đếm số lần xuất hiện
        Map<String, Long> bookBorrowCounts = all.stream()
                .flatMap(r -> r.getBorrowedBooks().stream())
                .collect(Collectors.groupingBy(Book::getBookId, Collectors.counting()));

        // [LAMBDA & STREAM API]: Ánh xạ nhãn và giá trị biểu đồ từ Map bằng Stream API
        List<String> chartLabels = bookBorrowCounts.keySet().stream()
                .map(id -> {
                    Book b = bookDAO.findBookById(id);
                    return "[" + id + "] " + (b != null ? b.getTitle() : id);
                })
                .collect(Collectors.toList());

        List<Integer> chartValues = bookBorrowCounts.values().stream()
                .map(Long::intValue)
                .collect(Collectors.toList());

        // Tính năng 8: Vẽ biểu đồ cột ASCII cho các sách được mượn
        Menu.printAsciiBarChart("BORROW COUNT BY BOOK TITLE", chartLabels, chartValues, "loans");
    }

    private void deleteBorrowRecord() {
        String recordId = DataInput.getNonEmptyString("Enter borrow record ID to delete: ");
        BorrowRecord r = borrowDAO.findById(recordId);
        if (r == null) {
            System.out.println(">> Record not found: " + recordId);
            return;
        }
        System.out.println(r);
        if (DataInput.getBoolean("Are you sure you want to delete this borrow record?")) {
            if (borrowDAO.deleteBorrowRecord(recordId)) {
                System.out.println(">> Borrow record deleted successfully!");
            }
        }
    }
}
