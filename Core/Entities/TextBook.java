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
        this(bookId, title, "Nhiều tác giả", price, quantity, genre);
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = (genre != null && !genre.trim().isEmpty()) ? genre.trim() : "Tổng hợp";
    }

    /**
     * Sách giáo trình được nhà trường và thư viện trợ giá học tập cho học sinh/sinh viên.
     * Áp dụng chiết khấu 10% so với giá bìa niêm yết.
     */
    @Override
    public double calculatePrice() {
        return getPrice() * 0.90;
    }

    /**
     * Phí mượn sách giáo trình ưu đãi dành cho sinh viên: 2.000 VNĐ/ngày.
     */
    @Override
    public double calculateBorrowFee(int days) {
        int validDays = (days > 0) ? days : 1;
        return validDays * 2000.0;
    }

    /**
     * Tổng giá trị kho sách giáo trình = Đơn giá bìa * Số lượng.
     */
    @Override
    public double calculateTotalValue() {
        return getPrice() * getQuantity();
    }

    @Override
    public String toString() {
        return String.format("TextBook      [%s | Thể loại: %-12s | Giá ưu đãi: %,.2f | Phí mượn/ngày: %,.0f | Tổng trị giá: %,.2f]", 
                super.toString(), genre, calculatePrice(), calculateBorrowFee(1), calculateTotalValue());
    }
}
