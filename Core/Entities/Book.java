package Core.Entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp trừu tượng Book đại diện cho thông tin chung của sách trong thư viện.
 * Bổ sung:
 * - Tính năng Đặt trước sách (Reservation Queue) khi sách hết hàng.
 * 
 * PRO192 - Phân hệ Sách & Đa hình (Member 2: Nguyen Thien Tue)
 */
public abstract class Book {
    protected String bookId;
    protected String title;
    protected String author;
    protected double price;
    protected int quantity;
    protected List<String> reservationQueue;

    public Book(String bookId, String title, String author, double price, int quantity) {
        setBookId(bookId);
        setTitle(title);
        setAuthor(author);
        setPrice(price);
        setQuantity(quantity);
        this.reservationQueue = new ArrayList<>();
    }

    public Book(String bookId, String title, double price, int quantity) {
        this(bookId, title, "Various Authors", price, quantity);
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Book ID cannot be empty!");
        }
        this.bookId = bookId.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Book title cannot be empty!");
        }
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author != null && !author.trim().isEmpty()) {
            this.author = author.trim();
        } else {
            this.author = "Various Authors";
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Error: Book price must be greater than 0!");
        }
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Error: Quantity cannot be negative!");
        }
        this.quantity = quantity;
    }


    // ========================================================
    // Feature 7: Reservation Queue
    // ========================================================

    public List<String> getReservationQueue() {
        return reservationQueue;
    }

    /**
     * [LAMBDA & STREAM API]: Đăng ký đặt trước sách (Reservation Queue).
     */
    public boolean addReservation(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            return false;
        }
        String cleanId = memberId.trim().toUpperCase();
        boolean alreadyReserved = reservationQueue.stream()
                .anyMatch(cleanId::equalsIgnoreCase);
        if (alreadyReserved) {
            return false;
        }
        reservationQueue.add(cleanId);
        return true;
    }

    /**
     * Lấy mã độc giả đầu tiên trong hàng đợi (FIFO).
     */
    public String popNextReservation() {
        if (reservationQueue.isEmpty()) {
            return null;
        }
        return reservationQueue.remove(0);
    }

    // ========================================================
    // Polymorphic Methods
    // ========================================================

    public abstract double calculateTotalValue();

    public abstract double calculatePrice();

    public abstract double calculateBorrowFee(int days);

    @Override
    public String toString() {
        return String.format("ID: %-6s | Title: %-25s | Author: %-16s | Price: %-8.0f | Qty: %-3d",
                bookId, title, author, price, quantity);
    }
}
