package Core.Entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp trừu tượng Book đại diện cho thông tin chung của sách trong thư viện.
 * Bổ sung:
 * - Tính năng 3: Đánh giá Sao (Rating 1-5 ⭐) phục vụ gợi ý sách thông minh.
 * - Tính năng 7: Hàng đợi Đặt trước sách (Reservation Queue) khi sách hết hàng.
 * 
 * PRO192 - Phân hệ Sách & Đa hình (Member 2: Nguyen Thien Tue)
 */
public abstract class Book {
    protected String bookId;
    protected String title;
    protected String author;
    protected double price;
    protected int quantity;
    protected double rating;
    protected int ratingCount;
    protected List<String> reservationQueue;

    public Book(String bookId, String title, String author, double price, int quantity) {
        setBookId(bookId);
        setTitle(title);
        setAuthor(author);
        setPrice(price);
        setQuantity(quantity);
        this.rating = 4.5;
        this.ratingCount = 1;
        this.reservationQueue = new ArrayList<>();
    }

    public Book(String bookId, String title, double price, int quantity) {
        this(bookId, title, "Nhiều tác giả", price, quantity);
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Mã sách không được để trống!");
        }
        this.bookId = bookId.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Tên sách không được để trống!");
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
            this.author = "Nhiều tác giả";
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Lỗi: Giá sách phải lớn hơn 0!");
        }
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Lỗi: Số lượng không được âm!");
        }
        this.quantity = quantity;
    }

    // ========================================================
    // Tính năng 3: Đánh giá Sao (Rating 1 - 5 ⭐)
    // ========================================================

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating >= 1.0 && rating <= 5.0) {
            this.rating = rating;
        }
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        if (ratingCount >= 0) {
            this.ratingCount = ratingCount;
        }
    }

    public void addRating(int stars) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Số sao đánh giá phải từ 1 đến 5!");
        }
        double totalScore = this.rating * this.ratingCount + stars;
        this.ratingCount = this.ratingCount + 1;
        this.rating = totalScore / this.ratingCount;
    }

    public String getStarDisplay() {
        return String.format("%.1f★ (%d)", rating, ratingCount);
    }

    // ========================================================
    // Tính năng 7: Hàng đợi Đặt trước sách (Reservation Queue)
    // ========================================================

    public List<String> getReservationQueue() {
        return reservationQueue;
    }

    /**
     * [LAMBDA & STREAM API]: Đăng ký đặt trước sách (Reservation Queue).
     * 
     * So sánh:
     * - Cách cũ: Dùng vòng for duyệt từng phần tử `reservationQueue.get(i)` và so sánh equalsIgnoreCase.
     * - Lambda/Stream: Dùng `reservationQueue.stream().anyMatch(cleanId::equalsIgnoreCase)`.
     *   -> anyMatch là một short-circuiting terminal operation: nó dừng duyệt ngay khi tìm thấy phần tử trùng,
     *      giúp mã nguồn súc tích, dễ đọc và tối ưu hóa hiệu năng.
     */
    public boolean addReservation(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            return false;
        }
        String cleanId = memberId.trim().toUpperCase();
        boolean alreadyReserved = reservationQueue.stream()
                .anyMatch(cleanId::equalsIgnoreCase);
        if (alreadyReserved) {
            return false; // Độc giả đã có tên trong hàng đợi
        }
        reservationQueue.add(cleanId);
        return true;
    }

    /**
     * Lấy mã độc giả đầu tiên trong hàng đợi (FIFO - vào trước phục vụ trước).
     */
    public String popNextReservation() {
        if (reservationQueue.isEmpty()) {
            return null;
        }
        return reservationQueue.remove(0);
    }

    // ========================================================
    // Các phương thức Đa hình (Polymorphism)
    // ========================================================

    public abstract double calculateTotalValue();

    public abstract double calculatePrice();

    public abstract double calculateBorrowFee(int days);

    @Override
    public String toString() {
        return String.format("Mã: %-6s | Tên: %-25s | Tác giả: %-16s | Giá: %-8.0f | SL: %-3d | Đánh giá: %s",
                bookId, title, author, price, quantity, getStarDisplay());
    }
}
