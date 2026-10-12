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
            int choice = DataInput.getIntegerNumber(">> Nhập lựa chọn của bạn [0-10]: ");
            System.out.println();
            switch (choice) {
                case 1:
                    displayBorrowRecords(borrowDAO.getAll(), "DANH SÁCH TOÀN BỘ PHIẾU MƯỢN - TRẢ");
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
                    displayBorrowRecords(borrowDAO.getOverdueRecords(), "DANH SÁCH CÁC PHIẾU MƯỢN QUÁ HẠN (OVERDUE)");
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
                        System.out.println(">> Đã lưu lịch sử mượn - trả ra file BorrowRecords.txt thành công!");
                    }
                    break;
                case 0:
                    borrowDAO.saveToFile();
                    System.out.println(">> Đã lưu dữ liệu mượn - trả và quay lại Menu chính!");
                    running = false;
                    break;
                default:
                    System.out.println(">> Lựa chọn không hợp lệ! Vui lòng chọn từ 0 đến 10.");
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
        System.out.println("                      PHÂN HỆ QUẢN LÝ GIAO DỊCH MƯỢN - TRẢ SÁCH (BORROW MANAGEMENT)                       ");
        System.out.println("==========================================================================================================");
        System.out.println("  1. Hiển thị toàn bộ danh sách phiếu mượn - trả (Kèm Ưu đãi hạng thẻ, Phạt & Đền bù)");
        System.out.println("  2. Lập phiếu mượn sách mới (Tự động áp dụng hạn mức VIP/GOLD/SILVER & Đặt trước nếu hết sách)");
        System.out.println("  3. Xử lý trả sách, Báo mất/hỏng sách (150%), Cộng/Trừ Điểm uy tín & Xuất biên lai");
        System.out.println("  4. Gia hạn phiếu mượn sách (+7 ngày cho phiếu chưa quá hạn - Tính năng 5)");
        System.out.println("  5. Tra cứu phiếu mượn (Theo Mã Phiếu / Theo Mã Độc Giả / Phiếu Đang Mượn)");
        System.out.println("  6. Lọc danh sách các phiếu mượn QUÁ HẠN (Overdue Records)");
        System.out.println("  7. Báo cáo thống kê Mượn - Trả & Biểu đồ ASCII Top sách mượn nhiều nhất (Tính năng 8)");
        System.out.println("  8. Xuất biên lai giao dịch ra file riêng Receipt_<MãPhiếu>.txt (Tính năng 9)");
        System.out.println("  9. Xóa phiếu mượn theo mã");
        System.out.println(" 10. Lưu dữ liệu lịch sử mượn trả ra file (BorrowRecords.txt)");
        System.out.println("  0. Lưu & Quay lại Menu chính");
        System.out.println("==========================================================================================================");
    }

    public void displayBorrowRecords(List<BorrowRecord> records, String title) {
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        System.out.println("  " + title + " (Tổng số: " + records.size() + " phiếu)");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        if (records.isEmpty()) {
            System.out.println("  >> Không có phiếu mượn nào trong danh sách!");
            System.out.println("--------------------------------------------------------------------------------------------------------------------");
            return;
        }
        System.out.printf("| %-6s | %-18s | %-6s | %-4s | %-10s | %-10s | %-10s | %-18s | %-10s | %-11s |\n",
                "Mã PM", "Tên Độc Giả", "Hạng", "SL", "Ngày Mượn", "Hạn Trả", "Ngày Trả", "Trạng Thái", "Phạt Trễ", "Tổng TT");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < records.size(); i++) {
            BorrowRecord r = records.get(i);
            String bDate = (r.getBorrowDate() != null) ? r.getBorrowDate().format(BorrowRecord.DATE_FORMAT) : "N/A";
            String dDate = (r.getDueDate() != null) ? r.getDueDate().format(BorrowRecord.DATE_FORMAT) : "N/A";
            String rDate = (r.getReturnDate() != null) ? r.getReturnDate().format(BorrowRecord.DATE_FORMAT) : "Chưa trả";
            System.out.printf("| %-6s | %-18s | %-6s | %-4d | %-10s | %-10s | %-10s | %-18s | %,10.0f | %,11.0f |\n",
                    r.getRecordId(), r.getMember().getFullName(), r.getMember().getMembershipTier(),
                    r.getBorrowedBooks().size(), bDate, dDate, rDate,
                    r.getStatusText(), r.calculateFine(), r.calculateFinalPayment());
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
    }

    private void createNewBorrowRecord() {
        System.out.println("--- LẬP PHIẾU MƯỢN SÁCH MỚI ---");
        String recordId;
        while (true) {
            recordId = DataInput.getNonEmptyString("Nhập mã phiếu mượn (VD: BR004): ").toUpperCase();
            if (!DataValidation.isValidId(recordId, "BR")) {
                System.out.println(">> Lỗi: Mã phiếu mượn phải có định dạng BRxxx (VD: BR001)!");
                continue;
            }
            if (borrowDAO.findById(recordId) != null) {
                System.out.println(">> Lỗi: Mã phiếu mượn đã tồn tại!");
            } else {
                break;
            }
        }

        String memberId = DataInput.getNonEmptyString("Nhập mã độc giả mượn sách (VD: M001): ").toUpperCase();
        Member member = memberDAO.findById(memberId);
        if (member == null) {
            System.out.println(">> Lỗi: Không tìm thấy độc giả có mã " + memberId + "!");
            return;
        }
        if (!member.isActive()) {
            System.out.println(">> Lỗi: Thẻ của độc giả " + member.getFullName() + " hiện đang BỊ KHÓA (Điểm uy tín: "
                    + member.getReputationPoints() + "đ), không thể mượn sách!");
            return;
        }

        int maxLimit = member.getMaxBorrowLimit();
        int currentBorrowed = borrowDAO.countActiveBorrowedBooksByMember(memberId);
        int remainingQuota = maxLimit - currentBorrowed;
        if (remainingQuota <= 0) {
            System.out.println(">> Lỗi: Độc giả hạng " + member.getMembershipTier() + " đang mượn "
                    + currentBorrowed + " cuốn (Đã đạt trần tối đa " + maxLimit + " cuốn)!");
            return;
        }
        System.out.printf(">> Độc giả: %s | Hạng thẻ: %s (Ưu đãi -%.0f%%) | Còn được mượn thêm: %d cuốn.\n",
                member.getFullName(), member.getMembershipTier(), member.getDiscountRate() * 100, remainingQuota);

        int numBooks = DataInput.getIntegerNumber("Nhập số lượng đầu sách muốn mượn [1-" + remainingQuota + "]: ", 1, remainingQuota);
        List<Book> selectedBooks = new ArrayList<>();

        for (int i = 1; i <= numBooks; i++) {
            while (true) {
                String bookId = DataInput.getNonEmptyString("  Nhập mã cuốn sách thứ " + i + " (VD: B001): ").toUpperCase();
                Book book = bookDAO.findBookById(bookId);
                if (book == null) {
                    System.out.println("  >> Không tìm thấy sách có mã " + bookId + "!");
                    continue;
                }
                if (book.getQuantity() <= 0) {
                    System.out.println("  >> Sách '" + book.getTitle() + "' hiện đã HẾT HÀNG (SL = 0)!");
                    // Tính năng 7: Cho phép đặt trước ngay khi hết sách
                    if (DataInput.getBoolean("  >> Bạn có muốn đăng ký ĐẶT TRƯỚC (Reserve) cuốn sách này không?")) {
                        if (bookDAO.reserveBook(bookId, memberId)) {
                            System.out.println("  >> Đã đưa độc giả " + memberId + " vào hàng đợi ưu tiên của sách '" + book.getTitle() + "'!");
                        } else {
                            System.out.println("  >> Độc giả đã có tên trong hàng đợi của cuốn sách này!");
                        }
                    }
                    continue;
                }
                boolean alreadySelected = false;
                for (int j = 0; j < selectedBooks.size(); j++) {
                    if (selectedBooks.get(j).getBookId().equalsIgnoreCase(bookId)) {
                        alreadySelected = true;
                        break;
                    }
                }
                if (alreadySelected) {
                    System.out.println("  >> Cuốn sách này đã có trong phiếu mượn!");
                    continue;
                }
                selectedBooks.add(book);
                System.out.println("  >> Đã chọn: " + book.getTitle() + " (Tồn kho còn: " + book.getQuantity() + ")");
                break;
            }
        }

        int borrowDays = DataInput.getIntegerNumber("Nhập số ngày mượn [1-30] (Mặc định 14 ngày): ", 1, 30);
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(borrowDays);

        BorrowRecord newRecord = new BorrowRecord(recordId, member, selectedBooks, borrowDate, dueDate);
        if (borrowDAO.createBorrowRecord(newRecord)) {
            System.out.println("\n>> LẬP PHIẾU MƯỢN THÀNH CÔNG!");
            System.out.println(newRecord);
        } else {
            System.out.println(">> Lập phiếu mượn thất bại!");
        }
    }

    /**
     * Tính năng 1, 6, 7, 9: Trả sách, kiểm tra sách mất/hỏng, cộng/trừ điểm uy tín và xuất biên lai.
     */
    private void processReturnBooks() {
        System.out.println("--- XỬ LÝ TRẢ SÁCH, KIỂM TRA HƯ HỎNG & XUẤT HÓA ĐƠN ---");
        String recordId = DataInput.getNonEmptyString("Nhập mã phiếu mượn cần trả (VD: BR002): ").toUpperCase();
        BorrowRecord record = borrowDAO.findById(recordId);
        if (record == null) {
            System.out.println(">> Không tìm thấy phiếu mượn có mã: " + recordId);
            return;
        }
        if (record.isReturned()) {
            System.out.println(">> Phiếu mượn này đã được hoàn trả trước đó vào ngày "
                    + record.getReturnDate().format(BorrowRecord.DATE_FORMAT) + "!");
            return;
        }

        System.out.println("Thông tin phiếu mượn hiện tại:");
        System.out.println(record);

        LocalDate returnDate = DataInput.getDate("Nhập ngày trả thực tế (Nhấn Enter để lấy ngày hôm nay)");
        if (returnDate == null) {
            returnDate = LocalDate.now();
        }
        if (returnDate.isBefore(record.getBorrowDate())) {
            System.out.println(">> Lỗi: Ngày trả không thể trước ngày mượn ("
                    + record.getBorrowDate().format(BorrowRecord.DATE_FORMAT) + ")!");
            return;
        }

        // Tính năng 6: Kiểm tra xem có cuốn sách nào bị làm mất hoặc hư hỏng không
        List<String> damagedBookIds = new ArrayList<>();
        if (DataInput.getBoolean("Có cuốn sách nào bị LÀM MẤT hoặc HƯ HỎNG nặng không?")) {
            List<Book> books = record.getBorrowedBooks();
            for (int i = 0; i < books.size(); i++) {
                Book b = books.get(i);
                boolean isLost = DataInput.getBoolean("  - Sách [" + b.getBookId() + "] '" + b.getTitle() + "' có bị mất/hỏng không?");
                if (isLost) {
                    damagedBookIds.add(b.getBookId());
                    System.out.printf("    -> Phạt đền bù 150%% giá trị thực: %,.0f VNĐ\n", b.calculatePrice() * 1.5);
                }
            }
        }

        if (borrowDAO.returnBooksWithDamageCheck(recordId, returnDate, damagedBookIds)) {
            Member m = record.getMember();
            System.out.println("\n==================== HÓA ĐƠN HOÀN TRẢ SÁCH ====================");
            System.out.println(record);
            double rawFee = record.calculateTotalBorrowFee();
            double discountedFee = record.calculateDiscountedBorrowFee();
            double fine = record.calculateFine();
            double comp = record.getCompensationFee();
            System.out.printf("  + Phí mượn sách gốc (Đa hình)       : %,14.0f VNĐ\n", rawFee);
            System.out.printf("  + Ưu đãi hạng thẻ %-6s (-%2.0f%%)     : -%,13.0f VNĐ\n",
                    m.getMembershipTier(), m.getDiscountRate() * 100, rawFee - discountedFee);
            System.out.printf("  + Phí mượn sau ưu đãi               : %,14.0f VNĐ\n", discountedFee);
            System.out.printf("  + Tiền phạt quá hạn (%2d ngày)      : %,14.0f VNĐ\n", record.getOverdueDays(), fine);
            System.out.printf("  + Tiền đền bù sách mất/hỏng (150%%) : %,14.0f VNĐ\n", comp);
            System.out.println("---------------------------------------------------------------");
            System.out.printf("  => TỔNG THANH TOÁN CUỐI CÙNG        : %,14.0f VNĐ\n", record.calculateFinalPayment());
            System.out.printf("  * Điểm uy tín mới của độc giả       : %d điểm (Hạng: %s)\n",
                    m.getReputationPoints(), m.getMembershipTier());
            System.out.println("===============================================================");

            // Tính năng 9: Tự động xuất biên lai ra file Receipt_<MãPhiếu>.txt
            if (borrowDAO.exportReceiptToFile(recordId)) {
                System.out.println(">> Đã tự động xuất biên lai ra file: Receipt_" + recordId + ".txt!");
            }
        }
    }

    /**
     * Tính năng 5: Gia hạn phiếu mượn sách (+7 ngày).
     */
    private void extendLoanPeriod() {
        System.out.println("--- GIA HẠN PHIẾU MƯỢN SÁCH (LOAN EXTENSION - FEATURE 5) ---");
        String recordId = DataInput.getNonEmptyString("Nhập mã phiếu mượn cần gia hạn (VD: BR001): ").toUpperCase();
        BorrowRecord record = borrowDAO.findById(recordId);
        if (record == null) {
            System.out.println(">> Không tìm thấy phiếu mượn có mã: " + recordId);
            return;
        }
        if (record.isReturned()) {
            System.out.println(">> Lỗi: Phiếu mượn này đã hoàn trả, không thể gia hạn!");
            return;
        }
        if (record.isOverdue()) {
            System.out.println(">> Lỗi: Phiếu mượn đang bị QUÁ HẠN, không được phép gia hạn! Vui lòng trả sách.");
            return;
        }
        if (record.getExtensionCount() >= BorrowRecord.MAX_EXTENSIONS) {
            System.out.println(">> Lỗi: Phiếu mượn này đã hết lượt gia hạn (Tối đa " + BorrowRecord.MAX_EXTENSIONS + " lần)!");
            return;
        }

        int extraDays = DataInput.getIntegerNumber("Nhập số ngày muốn gia hạn thêm [1-7]: ", 1, 7);
        if (borrowDAO.extendBorrowRecord(recordId, extraDays)) {
            System.out.println(">> Gia hạn thành công thêm " + extraDays + " ngày! Hạn trả mới là: "
                    + record.getDueDate().format(BorrowRecord.DATE_FORMAT));
        } else {
            System.out.println(">> Không thể gia hạn phiếu mượn!");
        }
    }

    private void exportReceiptMenu() {
        System.out.println("--- XUẤT BIÊN LAI GIAO DỊCH RA FILE .TXT (FEATURE 9) ---");
        String recordId = DataInput.getNonEmptyString("Nhập mã phiếu mượn muốn xuất biên lai (VD: BR001): ").toUpperCase();
        if (borrowDAO.exportReceiptToFile(recordId)) {
            System.out.println(">> Đã xuất biên lai thành công ra file: Receipt_" + recordId + ".txt!");
        } else {
            System.out.println(">> Không tìm thấy phiếu mượn hoặc lỗi ghi file!");
        }
    }

    private void searchBorrowSubMenu() {
        System.out.println("1. Tìm theo Mã Phiếu Mượn | 2. Tìm theo Mã Độc Giả | 3. Xem các phiếu Đang Mượn");
        int choice = DataInput.getIntegerNumber("Chọn tiêu chí [1-3]: ");
        switch (choice) {
            case 1:
                String rId = DataInput.getNonEmptyString("Nhập mã phiếu mượn: ");
                BorrowRecord r = borrowDAO.findById(rId);
                if (r != null) {
                    System.out.println("\n" + r);
                } else {
                    System.out.println(">> Không tìm thấy phiếu mượn có mã: " + rId);
                }
                break;
            case 2:
                String mId = DataInput.getNonEmptyString("Nhập mã độc giả: ");
                displayBorrowRecords(borrowDAO.findByMemberId(mId), "LỊCH SỬ MƯỢN SÁCH CỦA ĐỘC GIẢ: " + mId.toUpperCase());
                break;
            case 3:
                displayBorrowRecords(borrowDAO.getActiveBorrowRecords(), "DANH SÁCH CÁC PHIẾU ĐANG MƯỢN CHƯA TRẢ");
                break;
            default:
                System.out.println(">> Lựa chọn không hợp lệ!");
                break;
        }
    }

    private void displayBorrowStatistics() {
        List<BorrowRecord> all = borrowDAO.getAll();
        List<BorrowRecord> active = borrowDAO.getActiveBorrowRecords();
        List<BorrowRecord> overdue = borrowDAO.getOverdueRecords();

        System.out.println("==========================================================================");
        System.out.println("                 BÁO CÁO THỐNG KÊ GIAO DỊCH MƯỢN - TRẢ                    ");
        System.out.println("==========================================================================");
        System.out.printf("  - Tổng số phiếu mượn đã lập          : %d phiếu\n", all.size());
        System.out.printf("  - Số phiếu đang mượn chưa trả        : %d phiếu\n", active.size());
        System.out.printf("  - Số phiếu đã hoàn trả               : %d phiếu\n", all.size() - active.size());
        System.out.printf("  - Số phiếu quá hạn (Overdue)         : %d phiếu\n", overdue.size());
        System.out.printf("  - Tổng doanh thu phí mượn (sau giảm) : %,.0f VNĐ\n", borrowDAO.calculateTotalBorrowFeeRevenue());
        System.out.printf("  - Tổng tiền phạt trễ hạn phát sinh   : %,.0f VNĐ\n", borrowDAO.calculateTotalFineRevenue());
        System.out.printf("  - Tổng tiền đền bù sách mất/hỏng     : %,.0f VNĐ\n", borrowDAO.calculateTotalCompensationRevenue());
        System.out.println("==========================================================================");

        // Đếm số lượt mượn theo từng đầu sách bằng Map và vòng lặp for truyền thống
        Map<String, Integer> bookCountMap = new HashMap<>();
        Map<String, String> bookTitleMap = new HashMap<>();
        for (int i = 0; i < all.size(); i++) {
            BorrowRecord r = all.get(i);
            List<Book> books = r.getBorrowedBooks();
            for (int j = 0; j < books.size(); j++) {
                Book b = books.get(j);
                String id = b.getBookId();
                Integer oldCount = bookCountMap.get(id);
                if (oldCount == null) {
                    bookCountMap.put(id, 1);
                } else {
                    bookCountMap.put(id, oldCount + 1);
                }
                bookTitleMap.put(id, "[" + id + "] " + b.getTitle());
            }
        }

        List<String> chartLabels = new ArrayList<>();
        List<Integer> chartValues = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : bookCountMap.entrySet()) {
            chartLabels.add(bookTitleMap.get(entry.getKey()));
            chartValues.add(entry.getValue());
        }

        // Tính năng 8: Vẽ biểu đồ cột ASCII cho các sách được mượn
        Menu.printAsciiBarChart("THỐNG KÊ SỐ LƯỢT MƯỢN THEO ĐẦU SÁCH", chartLabels, chartValues, "lượt");
    }

    private void deleteBorrowRecord() {
        String recordId = DataInput.getNonEmptyString("Nhập mã phiếu mượn cần xóa: ");
        BorrowRecord r = borrowDAO.findById(recordId);
        if (r == null) {
            System.out.println(">> Không tìm thấy phiếu mượn: " + recordId);
            return;
        }
        System.out.println(r);
        if (DataInput.getBoolean("Bạn có chắc chắn muốn xóa phiếu mượn này không?")) {
            if (borrowDAO.deleteBorrowRecord(recordId)) {
                System.out.println(">> Đã xóa phiếu mượn thành công!");
            }
        }
    }
}
