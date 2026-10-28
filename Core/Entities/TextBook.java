package Core.Entities;

// Milestone 3 - Nguyen Thien Tue - TextBook Entity
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

    @Override
    public double calculatePrice() {
        return getPrice() * 0.90;
    }

    @Override
    public double calculateBorrowFee(int days) {
        int validDays = (days > 0) ? days : 1;
        return validDays * 2000.0;
    }

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
