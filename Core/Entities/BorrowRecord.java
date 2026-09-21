package Core.Entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecord {
    private String recordId;
    private Member member;
    private List<Book> borrowedBooks;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final double FINE_PER_DAY = 5000.0; // 5.000 VNĐ mỗi ngày trễ hạn

    public BorrowRecord(String recordId, Member member, LocalDate borrowDate, int borrowPeriodDays) {
        setRecordId(recordId);
        setMember(member);
        this.borrowedBooks = new ArrayList<>();
        setBorrowDate(borrowDate);
        this.dueDate = this.borrowDate.plusDays(borrowPeriodDays > 0 ? borrowPeriodDays : 14); // Mặc định 14 ngày
        this.returnDate = null;
        this.returned = false;
    }

    public BorrowRecord(String recordId, Member member, List<Book> books, LocalDate borrowDate, LocalDate dueDate) {
        setRecordId(recordId);
        setMember(member);
        this.borrowedBooks = (books != null) ? new ArrayList<>(books) : new ArrayList<>();
        setBorrowDate(borrowDate);
        setDueDate(dueDate);
        this.returnDate = null;
        this.returned = false;
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
        this.borrowDate = (borrowDate != null) ? borrowDate : LocalDate.now();
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

    // Nghiệp vụ hoàn trả sách
    public void markAsReturned(LocalDate actualReturnDate) {
        this.returnDate = (actualReturnDate != null) ? actualReturnDate : LocalDate.now();
        this.returned = true;
    }

    // Kiểm tra xem phiếu có đang bị quá hạn hay không
    public boolean isOverdue() {
        if (returned) {
            return returnDate != null && dueDate != null && returnDate.isAfter(dueDate);
        }
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    // Tính số ngày trễ hạn
    public long getOverdueDays() {
        LocalDate checkDate = returned ? returnDate : LocalDate.now();
        if (dueDate != null && checkDate != null && checkDate.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, checkDate);
        }
        return 0;
    }

    // Tính tiền phạt quá hạn
    public double calculateFine() {
        return getOverdueDays() * FINE_PER_DAY;
    }

    public String getStatusText() {
        if (returned) {
            return isOverdue() ? "Đã trả (Trễ hạn)" : "Đã trả đúng hạn";
        }
        return isOverdue() ? "QUÁ HẠN" : "Đang mượn";
    }

    @Override
    public String toString() {
        String bDate = (borrowDate != null) ? borrowDate.format(DATE_FORMAT) : "N/A";
        String dDate = (dueDate != null) ? dueDate.format(DATE_FORMAT) : "N/A";
        String rDate = (returnDate != null) ? returnDate.format(DATE_FORMAT) : "Chưa trả";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Phiếu mượn: %-8s | Độc giả: %-15s (ID: %s)\n", 
                recordId, member.getFullName(), member.getUserId()));
        sb.append(String.format("  Ngày mượn: %-10s | Hạn trả: %-10s | Ngày trả: %-10s | Trạng thái: %s\n", 
                bDate, dDate, rDate, getStatusText()));
        if (calculateFine() > 0) {
            sb.append(String.format("  Trễ: %d ngày | Tiền phạt: %,.0f VNĐ\n", getOverdueDays(), calculateFine()));
        }
        sb.append("  Danh sách sách mượn (").append(borrowedBooks.size()).append(" cuốn):\n");
        for (int i = 0; i < borrowedBooks.size(); i++) {
            sb.append(String.format("    %d. %s (Mã: %s)\n", i + 1, borrowedBooks.get(i).getTitle(), borrowedBooks.get(i).getBookId()));
        }
        return sb.toString();
    }
}
