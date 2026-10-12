package Core.Entities;

/**
 * Lớp ReferenceBook (Sách tham khảo) kế thừa từ lớp Book.
 * Cài đặt các phương thức đa hình riêng biệt:
 * - calculatePrice(): Sách tham khảo cộng thêm 10% phí bản quyền / thuế VAT.
 * - calculateBorrowFee(days): Phí mượn tài liệu nghiên cứu chuyên sâu 5.000 VNĐ / ngày.
 * - calculateTotalValue(): Tổng giá trị lưu kho cộng thêm 5% thuế nhập khẩu và bảo quản.
 * 
 * PRO192 - Phân hệ Sách & Đa hình (Member 2: Nguyen Thien Tue)
 */
public class ReferenceBook extends Book {
    private String publisher;

    public ReferenceBook(String bookId, String title, String author, double price, int quantity, String publisher) {
        super(bookId, title, author, price, quantity);
        setPublisher(publisher);
    }

    public ReferenceBook(String bookId, String title, double price, int quantity, String publisher) {
        this(bookId, title, "Nhiều tác giả", price, quantity, publisher);
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = (publisher != null && !publisher.trim().isEmpty()) ? publisher.trim() : "Chưa rõ NXB";
    }

    /**
     * Sách tham khảo là sách chuyên ngành, có chi phí bản quyền quốc tế và thuế VAT 10%.
     */
    @Override
    public double calculatePrice() {
        return getPrice() * 1.10;
    }

    /**
     * Phí mượn sách tham khảo chuyên sâu và quý hiếm: 5.000 VNĐ/ngày.
     */
    @Override
    public double calculateBorrowFee(int days) {
        int validDays = (days > 0) ? days : 1;
        return validDays * 5000.0;
    }

    /**
     * Tổng giá trị kho sách tham khảo = (Đơn giá bìa * Số lượng) * 1.05 (phí thuế và bảo quản).
     */
    @Override
    public double calculateTotalValue() {
        return (getPrice() * getQuantity()) * 1.05;
    }

    @Override
    public String toString() {
        return String.format("ReferenceBook [%s | NXB: %-15s | Giá sau thuế: %,.2f | Phí mượn/ngày: %,.0f | Tổng trị giá (+5%% VAT): %,.2f]", 
                super.toString(), publisher, calculatePrice(), calculateBorrowFee(1), calculateTotalValue());
    }
}