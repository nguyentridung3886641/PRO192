package Core.Entities;

/**
 * Lớp trừu tượng Book đại diện cho thông tin chung của sách trong thư viện.
 * Hiện thực hóa tính Đa hình (Polymorphism) qua các hàm trừu tượng tính giá trị,
 * giá bán ưu đãi/sau thuế và phí mượn riêng biệt cho từng loại sách.
 * 
 * PRO192 - Phân hệ Sách & Đa hình (Member 2: Nguyen Thien Tue)
 */
public abstract class Book {
    protected String bookId;
    protected String title;
    protected String author;
    protected double price;
    protected int quantity;

    public Book(String bookId, String title, String author, double price, int quantity) {
        setBookId(bookId);
        setTitle(title);
        setAuthor(author);
        setPrice(price);
        setQuantity(quantity);
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
        this.author = (author != null && !author.trim().isEmpty()) ? author.trim() : "Nhiều tác giả";
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

    /**
     * Phương thức trừu tượng tính tổng giá trị lưu kho của đầu sách.
     * Áp dụng tính Đa hình (Polymorphism).
     * @return tổng trị giá lưu kho
     */
    public abstract double calculateTotalValue();

    /**
     * Phương thức trừu tượng tính giá bán / giá thanh toán thực tế sau ưu đãi hoặc thuế.
     * Milestone 3: Hiện thực hóa Đa hình qua hàm calculatePrice().
     * @return đơn giá thực tế sau khi tính toán theo từng loại sách
     */
    public abstract double calculatePrice();

    /**
     * Phương thức trừu tượng tính phí mượn riêng cho từng loại sách theo số ngày mượn.
     * Milestone 3: Hiện thực hóa tính phí mượn riêng cho từng loại sách.
     * @param days số ngày mượn sách
     * @return tổng phí mượn sách
     */
    public abstract double calculateBorrowFee(int days);

    @Override
    public String toString() {
        return String.format("Mã: %-6s | Tên: %-25s | Tác giả: %-18s | Giá: %-8.2f | SL: %-3d", 
                bookId, title, author, price, quantity);
    }
}
