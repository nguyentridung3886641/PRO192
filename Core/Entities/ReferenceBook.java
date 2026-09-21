public class ReferenceBook extends Book {
    private String publisher;

    public ReferenceBook(String bookId, String title, double price, int quantity, String publisher) {
        super(bookId, title, price, quantity);
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public double calculateTotalValue() {
        return (getPrice() * getQuantity()) * 1.05;
    }

    @Override
    public String toString() {
        return "ReferenceBook [Mã: " + getBookId() + " | Tên: " + title + " | NXB: " + publisher
                + " | Giá: $" + getPrice() + " | SL: " + getQuantity() + "]";
    }
}