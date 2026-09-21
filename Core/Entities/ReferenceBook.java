package Core.Entities;

public class ReferenceBook extends Book {
    private String publisher;

    public ReferenceBook(String bookId, String title, double price, int quantity, String publisher) {
        super(bookId, title, price, quantity);
        setPublisher(publisher);
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = (publisher != null && !publisher.trim().isEmpty()) ? publisher.trim() : "Chưa rõ NXB";
    }

    @Override
    public double calculateTotalValue() {
        return (getPrice() * getQuantity()) * 1.05; // Thuế 5%
    }

    @Override
    public String toString() {
        return String.format("ReferenceBook [%s | NXB: %-15s | Tổng trị giá (+5%% VAT): %.2f]", 
                super.toString(), publisher, calculateTotalValue());
    }
}