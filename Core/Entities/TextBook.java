package Core.Entities;

/**
 * Lớp TextBook (Sách giáo trình) kế thừa từ lớp Book.
 * Cài đặt các phương thức đa hình riêng biệt:
 * - calculatePrice(): Trợ giá sinh viên giảm 10% đơn giá bìa.
 * - calculateBorrowFee(days): Phí mượn ưu đãi học tập 2.000 VNĐ / ngày.
 * - calculateTotalValue(): Tổng giá trị lưu kho bằng đơn giá nhân số lượng.
 * 
 * PRO192 - Phân hệ Sách & Đa hình (Member 2: Nguyen Thien Tue)
 */
public class TextBook extends Book {
    private String genre;

    public TextBook(String bookId, String title, String author, double price, int quantity, String genre) {
        super(bookId, title, author, price, quantity);
        setGenre(genre);
    }

    public TextBook(String bookId, String title, double price, int quantity, String genre) {
        this(bookId, title, "Various Authors", price, quantity, genre);
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = (genre != null && !genre.trim().isEmpty()) ? genre.trim() : "General";
    }

    /**
     * Textbooks are subsidized with a 10% discount on cover price for students.
     */
    @Override
    public double calculatePrice() {
        return getPrice() * 0.90;
    }

    /**
     * Borrow fee for textbooks: 2,000 VND / day.
     */
    @Override
    public double calculateBorrowFee(int days) {
        int validDays = (days > 0) ? days : 1;
        return validDays * 2000.0;
    }

    /**
     * Total inventory value = Cover price * Quantity.
     */
    @Override
    public double calculateTotalValue() {
        return getPrice() * getQuantity();
    }

    @Override
    public String toString() {
        return String.format("TextBook      [%s | Genre: %-12s | Disc. Price: %,.2f | Borrow Fee/Day: %,.0f | Total Value: %,.2f]", 
                super.toString(), genre, calculatePrice(), calculateBorrowFee(1), calculateTotalValue());
    }
}
