package Core.Entities;

// Milestone 3 - Nguyen Thien Tue - ReferenceBook Entity
public class ReferenceBook extends Book {
    private String publisher;

    public ReferenceBook(String bookId, String title, String author, double price, int quantity, String publisher) {
        super(bookId, title, author, price, quantity);
        setPublisher(publisher);
    }

    public ReferenceBook(String bookId, String title, double price, int quantity, String publisher) {
        this(bookId, title, "Various Authors", price, quantity, publisher);
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = (publisher != null && !publisher.trim().isEmpty()) ? publisher.trim() : "Unknown Publisher";
    }

    @Override
    public double calculatePrice() {
        return getPrice() * 1.10;
    }

    @Override
    public double calculateBorrowFee(int days) {
        int validDays = (days > 0) ? days : 1;
        return validDays * 5000.0;
    }

    @Override
    public double calculateTotalValue() {
        return (getPrice() * getQuantity()) * 1.05;
    }

    @Override
    public String toString() {
        return String.format("ReferenceBook [%s | Publisher: %-15s | Taxed Price: %,.2f | Borrow Fee/Day: %,.0f | Total Value (+5%% Tax): %,.2f]", 
                super.toString(), publisher, calculatePrice(), calculateBorrowFee(1), calculateTotalValue());
    }
}
