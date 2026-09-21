package Core.Entities;

public class TextBook extends Book {
    private String genre;

    public TextBook(String bookId, String title, double price, int quantity, String genre) {
        super(bookId, title, price, quantity);
        setGenre(genre);
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = (genre != null && !genre.trim().isEmpty()) ? genre.trim() : "Tổng hợp";
    }

    @Override
    public double calculateTotalValue() {
        return getPrice() * getQuantity();
    }

    @Override
    public String toString() {
        return String.format("TextBook      [%s | Thể loại: %-12s | Tổng trị giá: %.2f]", 
                super.toString(), genre, calculateTotalValue());
    }
}
