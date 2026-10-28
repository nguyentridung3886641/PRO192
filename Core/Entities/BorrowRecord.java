package Core.Entities;

// Milestone 3 - Thieu Quang Tuan - BorrowRecord Entity
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public static final double FINE_PER_DAY = 5000.0;
    public static final int MAX_EXTENSIONS = 1;

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
            throw new IllegalArgumentException("Error: Borrow record ID cannot be empty!");
        }
        this.recordId = recordId.trim();
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Error: Member information cannot be empty!");
        }
        this.member = member;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Error: Book to add is invalid!");
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
            throw new IllegalArgumentException("Error: Due date cannot be before borrow date!");
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

    public double calculateTotalBorrowFee() {
        int days = getBorrowDurationDays();
        return borrowedBooks.stream()
                .filter(Objects::nonNull)
                .mapToDouble(b -> b.calculateBorrowFee(days))
                .sum();
    }

    public double calculateDiscountedBorrowFee() {
        double rawFee = calculateTotalBorrowFee();
        double discountRate = (member != null) ? member.getDiscountRate() : 0.0;
        return rawFee * (1.0 - discountRate);
    }

    public double calculateFine() {
        return getOverdueDays() * FINE_PER_DAY;
    }

    public double calculateFinalPayment() {
        return calculateDiscountedBorrowFee() + calculateFine() + compensationFee;
    }

    public String getStatusText() {
        if (returned) {
            return isOverdue() ? "Returned (Late)" : "Returned (On time)";
        }
        if (isOverdue()) {
            return "OVERDUE";
        }
        return (extensionCount > 0) ? "Borrowing (Ext.)" : "Borrowing";
    }

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
        String rDate = (returnDate != null) ? returnDate.format(DATE_FORMAT) : "Not returned";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Record ID: %-8s | Member: %-20s (ID: %s | Tier: %s)\n",
                recordId, member.getFullName(), member.getUserId(), member.getMembershipTier()));
        sb.append(String.format("  Borrow Date: %-10s | Due Date: %-10s | Return Date: %-10s | Status: %s\n",
                bDate, dDate, rDate, getStatusText()));
        sb.append(String.format("  Base Fee: %,.0f VND | Discounted (-%.0f%%): %,.0f VND",
                calculateTotalBorrowFee(), member.getDiscountRate() * 100, calculateDiscountedBorrowFee()));
        if (calculateFine() > 0) {
            sb.append(String.format(" | Overdue: %d day(s) (Fine: %,.0f VND)", getOverdueDays(), calculateFine()));
        }
        if (compensationFee > 0) {
            sb.append(String.format(" | Compensation: %,.0f VND", compensationFee));
        }
        sb.append("\n  Borrowed Books (").append(borrowedBooks.size()).append(" item(s)):\n");
        IntStream.range(0, borrowedBooks.size()).forEach(i -> {
            Book b = borrowedBooks.get(i);
            if (b != null) {
                sb.append(String.format("    %d. [%s] %s\n", i + 1, b.getBookId(), b.getTitle()));
            }
        });
        return sb.toString();
    }
}
