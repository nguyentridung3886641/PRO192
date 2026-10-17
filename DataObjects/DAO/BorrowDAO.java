package DataObjects.DAO;

import Core.Entities.Book;
import Core.Entities.BorrowRecord;
import Core.Entities.Member;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Lớp BorrowDAO quản lý danh sách phiếu mượn (ArrayList<BorrowRecord>),
 * tích hợp:
 * - Tính năng 1: Kiểm tra hạn mức mượn theo Hạng thẻ (5/7/10 cuốn), cộng/trừ Điểm uy tín.
 * - Tính năng 5: Gia hạn phiếu mượn (extendBorrowRecord).
 * - Tính năng 6: Xử lý sách làm mất/hư hỏng và tính tiền đền bù 150%.
 * - Tính năng 7: Tự động thông báo cho độc giả trong Hàng đợi đặt trước khi có sách trả về.
 * - Tính năng 9: Xuất hóa đơn chi tiết ra file Receipt_<MãPhiếu>.txt.
 * 
 * PRO192 - Phân hệ Giao dịch Mượn - Trả (Member 3)
 */
public class BorrowDAO implements IBorrowDAO {

    public static final String DEFAULT_FILE_PATH = "BorrowRecords.txt";
    public static final int MAX_BORROW_LIMIT = 5;

    private final List<BorrowRecord> recordList = new ArrayList<>();
    private final IMemberDAO memberDAO;
    private final IBookDAO bookDAO;

    public BorrowDAO(IMemberDAO memberDAO, IBookDAO bookDAO) {
        this.memberDAO = (memberDAO != null) ? memberDAO : new MemberDAO();
        this.bookDAO = (bookDAO != null) ? bookDAO : new BookDAO();

        File file = new File(DEFAULT_FILE_PATH);
        if (file.exists() && file.length() > 0) {
            boolean loaded = loadFromFile(DEFAULT_FILE_PATH);
            if (!loaded || recordList.isEmpty()) {
                initSampleData();
                saveToFile(DEFAULT_FILE_PATH);
            }
        } else {
            initSampleData();
            saveToFile(DEFAULT_FILE_PATH);
        }
    }

    public void initSampleData() {
        recordList.clear();
        Member m1 = memberDAO.findById("M001");
        Member m2 = memberDAO.findById("M002");
        Member m3 = memberDAO.findById("M003");

        Book b1 = bookDAO.findBookById("B001");
        Book b2 = bookDAO.findBookById("B002");
        Book b5 = bookDAO.findBookById("B005");
        Book b6 = bookDAO.findBookById("B006");

        if (m1 != null && b1 != null && b5 != null) {
            List<Book> list1 = new ArrayList<>();
            list1.add(b1);
            list1.add(b5);
            BorrowRecord r1 = new BorrowRecord("BR001", m1, list1,
                    LocalDate.now().minusDays(10), LocalDate.now().plusDays(4));
            recordList.add(r1);
        }

        if (m2 != null && b2 != null) {
            List<Book> list2 = new ArrayList<>();
            list2.add(b2);
            BorrowRecord r2 = new BorrowRecord("BR002", m2, list2,
                    LocalDate.now().minusDays(20), LocalDate.now().minusDays(6));
            recordList.add(r2); // Phiếu đang quá hạn 6 ngày
        }

        if (m3 != null && b6 != null) {
            List<Book> list3 = new ArrayList<>();
            list3.add(b6);
            BorrowRecord r3 = new BorrowRecord("BR003", m3, list3,
                    LocalDate.now().minusDays(25), LocalDate.now().minusDays(11));
            r3.markAsReturned(LocalDate.now().minusDays(8)); // Đã trả nhưng trễ 3 ngày
            recordList.add(r3);
        }
    }

    /**
     * [LAMBDA & STREAM API]: Tìm kiếm phiếu mượn theo mã phiếu (Record ID).
     * 
     * So sánh:
     * - Cách cũ: Vòng lặp for duyệt từng phiếu mượn và if so sánh mã.
     * - Lambda/Stream: Dùng `filter` kết hợp `findFirst()` và `orElse(null)`.
     */
    @Override
    public BorrowRecord findById(String recordId) {
        if (recordId == null || recordId.trim().isEmpty()) {
            return null;
        }
        String target = recordId.trim();
        return recordList.stream()
                .filter(r -> r.getRecordId().equalsIgnoreCase(target))
                .findFirst()
                .orElse(null);
    }

    /**
     * [LAMBDA & STREAM API]: Đếm tổng số sách độc giả đang mượn nhưng chưa hoàn trả.
     * 
     * So sánh:
     * - Cách cũ: Vòng for cộng dồn `count += r.getBorrowedBooks().size()`.
     * - Lambda/Stream: `filter(!r.isReturned() && memberId) -> mapToInt -> sum()`.
     */
    @Override
    public int countActiveBorrowedBooksByMember(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            return 0;
        }
        String cleanId = memberId.trim();
        return recordList.stream()
                .filter(r -> !r.isReturned() && r.getMember().getUserId().equalsIgnoreCase(cleanId))
                .mapToInt(r -> r.getBorrowedBooks().size())
                .sum();
    }

    /**
     * [LAMBDA & STREAM API]: Lập phiếu mượn sách mới.
     * Áp dụng `books.stream().allMatch(b -> b.getQuantity() > 0)` để kiểm tra tồn kho
     * và `books.forEach(...)` để cập nhật số lượng tồn kho theo phong cách hàm.
     */
    @Override
    public boolean createBorrowRecord(BorrowRecord record) {
        if (record == null || findById(record.getRecordId()) != null) {
            return false;
        }
        Member m = record.getMember();
        if (m == null || !m.isActive()) {
            return false;
        }
        List<Book> books = record.getBorrowedBooks();
        if (books == null || books.isEmpty()) {
            return false;
        }
        // Kiểm tra hạn mức theo Hạng thẻ độc giả (VIP: 10, GOLD: 7, SILVER: 5)
        int currentBorrowed = countActiveBorrowedBooksByMember(m.getUserId());
        if (currentBorrowed + books.size() > m.getMaxBorrowLimit()) {
            return false;
        }
        // [LAMBDA & STREAM API]: Kiểm tra toàn bộ sách có tồn kho hợp lệ bằng allMatch
        boolean allInStock = books.stream().allMatch(b -> b.getQuantity() > 0);
        if (!allInStock) {
            return false;
        }
        // [LAMBDA & STREAM API]: Trừ số lượng tồn kho bằng forEach
        books.forEach(b -> b.setQuantity(b.getQuantity() - 1));

        bookDAO.saveToFile();
        recordList.add(record);
        saveToFile();
        return true;
    }

    @Override
    public boolean returnBooks(String recordId, LocalDate actualReturnDate) {
        return returnBooksWithDamageCheck(recordId, actualReturnDate, new ArrayList<String>());
    }

    /**
     * [LAMBDA & STREAM API]: Tính năng 1, 6, 7 - Xử lý trả sách, kiểm tra sách hỏng/mất,
     * tính phạt 150%, cập nhật điểm uy tín và kích hoạt hàng đợi đặt trước.
     * 
     * So sánh:
     * - Cách cũ: Vòng lặp lồng nhau duyệt sách và danh sách ID sách hỏng.
     * - Lambda/Stream: Dùng `borrowed.forEach` kết hợp `lostOrDamagedBookIds.stream().anyMatch(...)`.
     */
    @Override
    public boolean returnBooksWithDamageCheck(String recordId, LocalDate actualReturnDate, List<String> lostOrDamagedBookIds) {
        BorrowRecord record = findById(recordId);
        if (record == null || record.isReturned()) {
            return false;
        }
        record.markAsReturned(actualReturnDate);

        List<Book> borrowed = record.getBorrowedBooks();
        double[] totalCompensation = {0.0}; // Mảng 1 phần tử phục vụ Lambda mutation

        borrowed.forEach(b -> {
            boolean isDamaged = (lostOrDamagedBookIds != null) &&
                    lostOrDamagedBookIds.stream().anyMatch(id -> b.getBookId().equalsIgnoreCase(id.trim()));

            if (isDamaged) {
                // Tính năng 6: Sách mất/hỏng không hoàn lại kho và phạt đền bù 150% giá trị thực
                totalCompensation[0] += b.calculatePrice() * 1.5;
            } else {
                // Hoàn lại số lượng vào kho
                b.setQuantity(b.getQuantity() + 1);

                // Tính năng 7: Kiểm tra hàng đợi đặt trước của cuốn sách này
                String nextWaitingMember = b.popNextReservation();
                if (nextWaitingMember != null) {
                    System.out.println("  🔔 [RESERVATION NOTIFICATION]: Book '" + b.getTitle()
                            + "' has been returned! Next waiting member in queue: " + nextWaitingMember);
                }
            }
        });

        record.setCompensationFee(totalCompensation[0]);

        // Tính năng 1: Cập nhật điểm uy tín (Reputation Points) cho độc giả
        Member member = record.getMember();
        if (totalCompensation[0] > 0) {
            member.addReputationPoints(-20); // Làm mất/hỏng sách: trừ 20 điểm
        } else if (record.isOverdue()) {
            member.addReputationPoints(-15); // Trả trễ hạn: trừ 15 điểm
        } else {
            member.addReputationPoints(10);  // Trả đúng hạn và nguyên vẹn: thưởng +10 điểm
        }

        memberDAO.saveToFile();
        bookDAO.saveToFile();
        saveToFile();
        return true;
    }

    /**
     * Tính năng 5: Gia hạn phiếu mượn thêm extraDays ngày.
     */
    @Override
    public boolean extendBorrowRecord(String recordId, int extraDays) {
        BorrowRecord record = findById(recordId);
        if (record == null) {
            return false;
        }
        boolean extended = record.extendDueDate(extraDays);
        if (extended) {
            saveToFile();
        }
        return extended;
    }

    /**
     * Tính năng 9: Xuất hóa đơn phiếu mượn/trả ra file văn bản riêng Receipt_<RecordID>.txt
     */
    @Override
    public boolean exportReceiptToFile(String recordId) {
        BorrowRecord r = findById(recordId);
        if (r == null) {
            return false;
        }
        String fileName = "Receipt_" + r.getRecordId().toUpperCase() + ".txt";
        List<String> lines = new ArrayList<>();
        lines.add("======================================================================");
        lines.add("                    LIBRARY TRANSACTION RECEIPT                       ");
        lines.add("======================================================================");
        lines.add("  Record ID        : " + r.getRecordId());
        lines.add("  Member ID        : " + r.getMember().getUserId());
        lines.add("  Member Name      : " + r.getMember().getFullName());
        lines.add("  Current Tier     : " + r.getMember().getMembershipTier()
                + " (Reputation Points: " + r.getMember().getReputationPoints() + " pts)");
        lines.add("  Borrow Date      : " + r.getBorrowDate().format(BorrowRecord.DATE_FORMAT));
        lines.add("  Due Date         : " + r.getDueDate().format(BorrowRecord.DATE_FORMAT));
        lines.add("  Actual Return    : " + (r.getReturnDate() != null ? r.getReturnDate().format(BorrowRecord.DATE_FORMAT) : "Not returned"));
        lines.add("  Record Status    : " + r.getStatusText());
        lines.add("----------------------------------------------------------------------");
        lines.add("  BORROWED BOOK LIST (" + r.getBorrowedBooks().size() + " book(s)):");
        // [LAMBDA & STREAM API]: Duyệt in danh sách sách trong phiếu bằng IntStream.range
        IntStream.range(0, r.getBorrowedBooks().size()).forEach(i -> {
            Book b = r.getBorrowedBooks().get(i);
            if (b != null) {
                lines.add(String.format("    %d. [%s] %-32s | Borrow Fee (%d days): %,.0f VND",
                        i + 1, b.getBookId(), b.getTitle(), r.getBorrowDurationDays(),
                        b.calculateBorrowFee(r.getBorrowDurationDays())));
            }
        });
        lines.add("----------------------------------------------------------------------");
        double rawFee = r.calculateTotalBorrowFee();
        double discountRate = r.getMember().getDiscountRate();
        double discountedFee = r.calculateDiscountedBorrowFee();
        double fine = r.calculateFine();
        double comp = r.getCompensationFee();
        lines.add(String.format("  + Total Raw Borrow Fee             : %,14.0f VND", rawFee));
        lines.add(String.format("  + Tier Discount %-6s (-%2.0f%%)       : -%,13.0f VND",
                r.getMember().getMembershipTier(), discountRate * 100, rawFee - discountedFee));
        lines.add(String.format("  + Fee After Discount               : %,14.0f VND", discountedFee));
        lines.add(String.format("  + Overdue Fine (%2d days)           : %,14.0f VND", r.getOverdueDays(), fine));
        lines.add(String.format("  + Book Damage/Loss Fee (150%%)      : %,14.0f VND", comp));
        lines.add("----------------------------------------------------------------------");
        lines.add(String.format("  => TOTAL PAYMENT AMOUNT            : %,14.0f VND", r.calculateFinalPayment()));
        lines.add("======================================================================");

        try {
            FileManager.writeLines(fileName, lines);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * [LAMBDA & STREAM API]: Xóa phiếu mượn và hoàn trả tồn kho nếu phiếu chưa hoàn tất.
     */
    @Override
    public boolean deleteBorrowRecord(String recordId) {
        BorrowRecord record = findById(recordId);
        if (record == null) {
            return false;
        }
        if (!record.isReturned()) {
            record.getBorrowedBooks().forEach(b -> b.setQuantity(b.getQuantity() + 1));
            bookDAO.saveToFile();
        }
        boolean removed = recordList.remove(record);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    @Override
    public List<BorrowRecord> getAll() {
        return new ArrayList<>(recordList);
    }

    /**
     * [LAMBDA & STREAM API]: Lấy danh sách phiếu mượn theo mã độc giả.
     */
    @Override
    public List<BorrowRecord> findByMemberId(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String cleanId = memberId.trim();
        return recordList.stream()
                .filter(r -> r.getMember().getUserId().equalsIgnoreCase(cleanId))
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & STREAM API]: Lọc danh sách phiếu mượn quá hạn (Overdue Records).
     * Áp dụng Method Reference `BorrowRecord::isOverdue`.
     */
    @Override
    public List<BorrowRecord> getOverdueRecords() {
        return recordList.stream()
                .filter(BorrowRecord::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & STREAM API]: Lọc danh sách phiếu mượn đang hoạt động (chưa trả).
     */
    @Override
    public List<BorrowRecord> getActiveBorrowRecords() {
        return recordList.stream()
                .filter(r -> !r.isReturned())
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & STREAM API]: Tính tổng tiền phạt trễ hạn toàn thư viện.
     * Áp dụng Method Reference `BorrowRecord::calculateFine` và `mapToDouble().sum()`.
     */
    @Override
    public double calculateTotalFineRevenue() {
        return recordList.stream()
                .mapToDouble(BorrowRecord::calculateFine)
                .sum();
    }

    /**
     * [LAMBDA & STREAM API]: Tính tổng doanh thu phí mượn sách sau ưu đãi hạng thẻ.
     */
    @Override
    public double calculateTotalBorrowFeeRevenue() {
        return recordList.stream()
                .mapToDouble(BorrowRecord::calculateDiscountedBorrowFee)
                .sum();
    }

    /**
     * [LAMBDA & STREAM API]: Tính tổng tiền đền bù sách mất/hỏng.
     */
    @Override
    public double calculateTotalCompensationRevenue() {
        return recordList.stream()
                .mapToDouble(BorrowRecord::getCompensationFee)
                .sum();
    }

    @Override
    public boolean loadFromFile() {
        return loadFromFile(DEFAULT_FILE_PATH);
    }

    /**
     * [LAMBDA & STREAM API]: Parse một dòng từ file BorrowRecords.txt thành đối tượng BorrowRecord.
     * Tận dụng Stream API để phân tích danh sách mã sách (bookIds) một cách khai báo và an toàn.
     */
    private BorrowRecord parseBorrowRecord(String line) {
        if (line == null || line.trim().isEmpty() || line.trim().startsWith("#")) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length < 7) {
            return null;
        }
        try {
            String recordId = parts[0].trim();
            String memberId = parts[1].trim();
            String[] bookIds = parts[2].trim().split(",");
            LocalDate borrowDate = LocalDate.parse(parts[3].trim(), BorrowRecord.DATE_FORMAT);
            LocalDate dueDate = LocalDate.parse(parts[4].trim(), BorrowRecord.DATE_FORMAT);
            String returnStr = parts[5].trim();
            boolean returned = parts[6].trim().equalsIgnoreCase("RETURNED");

            Member member = memberDAO.findById(memberId);
            if (member == null) {
                return null;
            }

            // Áp dụng Stream API chuyển đổi chuỗi mã sách sang danh sách đối tượng Book
            List<Book> books = Arrays.stream(bookIds)
                    .map(String::trim)
                    .map(bookDAO::findBookById)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (books.isEmpty()) {
                return null;
            }

            BorrowRecord record = new BorrowRecord(recordId, member, books, borrowDate, dueDate);
            if (!returnStr.equalsIgnoreCase("NONE") && !returnStr.isEmpty()) {
                record.setReturnDate(LocalDate.parse(returnStr, BorrowRecord.DATE_FORMAT));
            }
            record.setReturned(returned);
            if (parts.length >= 8) {
                record.setExtensionCount(Integer.parseInt(parts[7].trim()));
            }
            if (parts.length >= 9) {
                record.setCompensationFee(Double.parseDouble(parts[8].trim()));
            }
            return record;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * [LAMBDA & STREAM API]: Nạp dữ liệu phiếu mượn từ file với Stream API.
     * Sử dụng FileManager.readLines và Stream.map(this::parseBorrowRecord).
     */
    @Override
    public boolean loadFromFile(String filePath) {
        try {
            List<String> lines = FileManager.readLines(filePath);
            List<BorrowRecord> loaded = lines.stream()
                    .map(this::parseBorrowRecord)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!loaded.isEmpty()) {
                recordList.clear();
                recordList.addAll(loaded);
                return true;
            }
        } catch (Exception e) {
            System.out.println(">> Warning reading file BorrowRecords.txt: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean saveToFile() {
        return saveToFile(DEFAULT_FILE_PATH);
    }

    /**
     * [LAMBDA & STREAM API]: Lưu trữ toàn bộ dữ liệu phiếu mượn ra file.
     * Áp dụng Method Reference `BorrowRecord::toFileString` để biến đổi đối tượng thành chuỗi lưu trữ.
     */
    @Override
    public boolean saveToFile(String filePath) {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("# Format: RecordID|MemberID|BookIDs|BorrowDate|DueDate|ReturnDate|Status|ExtensionCount|CompensationFee");
            recordList.stream()
                    .map(BorrowRecord::toFileString)
                    .forEach(lines::add);
            FileManager.writeLines(filePath, lines);
            return true;
        } catch (Exception e) {
            System.out.println(">> Error writing file BorrowRecords.txt: " + e.getMessage());
            return false;
        }
    }
}
