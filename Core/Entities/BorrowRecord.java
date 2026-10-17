package Core.Entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Thực thể BorrowRecord đại diện cho Phiếu Mượn - Trả sách.
 * Tích hợp:
 * - Tính năng 1: Tính chiết khấu phí mượn theo Hạng thẻ độc giả (VIP -25%, GOLD -10%).
 * - Tính năng 5: Gia hạn phiếu mượn (extendDueDate, tối đa 1 lần +7 ngày).
 * - Tính năng 6: Xử lý đền bù làm mất / hư hỏng sách (compensationFee = 150% giá sách).
 * 
 * PRO192 - Phân hệ Giao dịch Mượn - Trả (Member 3)
 */
public class BorrowRecord {
    private String recordId;
    private Member member;
    private List<Book> borrowedBooks;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private int extensionCount;
    private double compensationFee;

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final double FINE_PER_DAY = 5000.0; // 5.000 VNĐ mỗi ngày trễ hạn
    public static final int MAX_EXTENSIONS = 1;       // Tối đa gia hạn 1 lần cho mỗi phiếu

    public BorrowRecord(String recordId, Member member, LocalDate borrowDate, int borrowPeriodDays) {
        setRecordId(recordId);
        setMember(member);
        this.borrowedBooks = new ArrayList<>();
        setBorrowDate(borrowDate);
        this.dueDate = this.borrowDate.plusDays(borrowPeriodDays > 0 ? borrowPeriodDays : 14);
        this.returnDate = null;
        this.returned = false;
        this.extensionCount = 0;
        this.compensationFee = 0.0;
    }

    public BorrowRecord(String recordId, Member member, List<Book> books, LocalDate borrowDate, LocalDate dueDate) {
        setRecordId(recordId);
        setMember(member);
        this.borrowedBooks = (books != null) ? new ArrayList<>(books) : new ArrayList<>();
        setBorrowDate(borrowDate);
        setDueDate(dueDate);
        this.returnDate = null;
        this.returned = false;
        this.extensionCount = 0;
        this.compensationFee = 0.0;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        if (recordId == null || recordId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Mã phiếu mượn không được để trống!");
        }
        this.recordId = recordId.trim();
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Lỗi: Thông tin độc giả không được để trống!");
        }
        this.member = member;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Lỗi: Cuốn sách cần thêm không hợp lệ!");
        }
        borrowedBooks.add(book);
    }

    public void removeBook(Book book) {
        borrowedBooks.remove(book);
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        if (borrowDate != null) {
            this.borrowDate = borrowDate;
        } else {
            this.borrowDate = LocalDate.now();
        }
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        if (dueDate != null && borrowDate != null && dueDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Lỗi: Hạn trả không thể trước ngày mượn!");
        }
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public int getExtensionCount() {
        return extensionCount;
    }

    public void setExtensionCount(int extensionCount) {
        this.extensionCount = Math.max(0, extensionCount);
    }

    public double getCompensationFee() {
        return compensationFee;
    }

    public void setCompensationFee(double compensationFee) {
        this.compensationFee = Math.max(0, compensationFee);
    }

    /**
     * Tính năng 5: Gia hạn thêm số ngày mượn sách (Loan Extension).
     * Điều kiện: Phiếu chưa trả, chưa bị quá hạn và số lần gia hạn < MAX_EXTENSIONS.
     */
    public boolean extendDueDate(int extraDays) {
        if (returned || isOverdue() || extensionCount >= MAX_EXTENSIONS || extraDays <= 0) {
            return false;
        }
        this.dueDate = this.dueDate.plusDays(extraDays);
        this.extensionCount = this.extensionCount + 1;
        return true;
    }

    public void markAsReturned(LocalDate actualReturnDate) {
        if (actualReturnDate != null) {
            this.returnDate = actualReturnDate;
        } else {
            this.returnDate = LocalDate.now();
        }
        this.returned = true;
    }

    public boolean isOverdue() {
        if (returned) {
            return returnDate != null && dueDate != null && returnDate.isAfter(dueDate);
        }
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    public long getOverdueDays() {
        LocalDate checkDate = returned ? returnDate : LocalDate.now();
        if (dueDate != null && checkDate != null && checkDate.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, checkDate);
        }
        return 0;
    }

    public int getBorrowDurationDays() {
        if (borrowDate == null || dueDate == null) {
            return 14;
        }
        long days = ChronoUnit.DAYS.between(borrowDate, dueDate);
        return (int) Math.max(1, days);
    }

    /**
     * [LAMBDA & STREAM API]: Tính tổng phí mượn gốc (Đa hình: gọi book.calculateBorrowFee).
     * 
     * So sánh:
     * - Cách cũ: Dùng biến accumulator `double total = 0;` và vòng lặp for i duyệt danh sách.
     * - Lambda/Stream API: Chuyển đổi danh sách thành Stream, ánh xạ (mapToDouble) từng cuốn sách sang phí mượn
     *   và dùng terminal operation `sum()` để cộng dồn.
     *   -> Giúp code ngắn gọn, loại bỏ biến trạng thái có thể bị lỗi gán và tối ưu hóa xử lý.
     */
     public double calculateTotalBorrowFee() {
         int days = getBorrowDurationDays();
         return borrowedBooks.stream()
                 .filter(Objects::nonNull)
                 .mapToDouble(b -> b.calculateBorrowFee(days))
                 .sum();
     }

    /**
     * Tính năng 1: Tính phí mượn sau khi đã giảm giá theo Hạng thẻ độc giả (VIP -25%, GOLD -10%).
     */
    public double calculateDiscountedBorrowFee() {
        double rawFee = calculateTotalBorrowFee();
        double discountRate = (member != null) ? member.getDiscountRate() : 0.0;
        return rawFee * (1.0 - discountRate);
    }

    public double calculateFine() {
        return getOverdueDays() * FINE_PER_DAY;
    }

    /**
     * Tổng số tiền thanh toán cuối cùng = Phí mượn sau giảm giá + Tiền phạt trễ hạn + Tiền đền bù hỏng/mất sách.
     */
    public double calculateFinalPayment() {
        return calculateDiscountedBorrowFee() + calculateFine() + compensationFee;
    }

    public String getStatusText() {
        if (returned) {
            return isOverdue() ? "Đã trả (Trễ hạn)" : "Đã trả đúng hạn";
        }
        if (isOverdue()) {
            return "QUÁ HẠN";
        }
        return (extensionCount > 0) ? "Đang mượn (Đã GH)" : "Đang mượn";
    }

    /**
     * [LAMBDA & STREAM API]: Xuất định dạng chuỗi lưu trữ file.
     * 
     * So sánh:
     * - Cách cũ: Dùng StringBuilder kết hợp vòng lặp for và `if (i > 0) append(",")` để nối ID sách.
     * - Lambda/Stream: Dùng `borrowedBooks.stream().filter(Objects::nonNull).map(Book::getBookId).collect(Collectors.joining(","))`.
     *   -> Sử dụng Method Reference `Book::getBookId` kết hợp Collector `Collectors.joining(",")`
     *      tự động xử lý dấu phân cách mà không cần kiểm tra điều kiện phần tử đầu tiên.
     */
    public String toFileString() {
        String bookIds = borrowedBooks.stream()
                .filter(Objects::nonNull)
                .map(Book::getBookId)
                .collect(Collectors.joining(","));

        String bDate = (borrowDate != null) ? borrowDate.format(DATE_FORMAT) : "01/01/2026";
        String dDate = (dueDate != null) ? dueDate.format(DATE_FORMAT) : "15/01/2026";
        String rDate = (returnDate != null) ? returnDate.format(DATE_FORMAT) : "NONE";
        return String.format("%s|%s|%s|%s|%s|%s|%s|%d|%.0f",
                recordId, member.getUserId(), bookIds, bDate, dDate, rDate,
                returned ? "RETURNED" : "BORROWING", extensionCount, compensationFee);
    }

    @Override
    public String toString() {
        String bDate = (borrowDate != null) ? borrowDate.format(DATE_FORMAT) : "N/A";
        String dDate = (dueDate != null) ? dueDate.format(DATE_FORMAT) : "N/A";
        String rDate = (returnDate != null) ? returnDate.format(DATE_FORMAT) : "Chưa trả";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Phiếu mượn: %-8s | Độc giả: %-20s (ID: %s | Hạng: %s)\n",
                recordId, member.getFullName(), member.getUserId(), member.getMembershipTier()));
        sb.append(String.format("  Ngày mượn: %-10s | Hạn trả: %-10s | Ngày trả: %-10s | Trạng thái: %s\n",
                bDate, dDate, rDate, getStatusText()));
        sb.append(String.format("  Phí mượn gốc: %,.0f VNĐ | Sau ưu đãi (-%.0f%%): %,.0f VNĐ",
                calculateTotalBorrowFee(), member.getDiscountRate() * 100, calculateDiscountedBorrowFee()));
        if (calculateFine() > 0) {
            sb.append(String.format(" | Trễ: %d ngày (Phạt: %,.0f VNĐ)", getOverdueDays(), calculateFine()));
        }
        if (compensationFee > 0) {
            sb.append(String.format(" | Đền bù sách: %,.0f VNĐ", compensationFee));
        }
        sb.append("\n  Danh sách sách mượn (").append(borrowedBooks.size()).append(" cuốn):\n");
        // [LAMBDA & STREAM API]: Dùng IntStream.range kết hợp biểu thức Lambda để in chỉ số và tên sách
        IntStream.range(0, borrowedBooks.size()).forEach(i -> {
            Book b = borrowedBooks.get(i);
            if (b != null) {
                sb.append(String.format("    %d. [%s] %s\n", i + 1, b.getBookId(), b.getTitle()));
            }
        });
        return sb.toString();
    }
}
