package Core.Entities;

public abstract class Book {
    protected String bookId;
    protected String title;
    protected double price;
    protected int quantity;

    public Book(String bookId, String title, double price, int quantity) {
        setBookId(bookId);
        setTitle(title);
        setPrice(price);
        setQuantity(quantity);
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

    public abstract double calculateTotalValue();

    @Override
    public String toString() {
        return String.format("Mã: %-6s | Tên: %-25s | Giá: %-8.2f | SL: %-3d", bookId, title, price, quantity);
    }
}
