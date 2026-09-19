public class TextBook extends Book {
    private String genre;

    public TextBook(String bookId, String title, double price, int quantity, String genre) {
        super(bookId, title, price, quantity);
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public double calculateTotalValue() {
        return getPrice() * getQuantity();
    }

    @Override
    public String toString() {
        return "TextBook [Mã: " + getBookId() + " | Tên: " + title + " | Thể loại: " + genre
                + " | Giá: $" + getPrice() + " | Số Lượng: " + getQuantity() + "]";
    }
}
