package DataObjects.DAO;

// Milestone 2 - Nguyen Thien Tue - Book DAO Interface
import Core.Entities.Book;
import java.util.List;

public interface IBookDAO {

    boolean addBook(Book book);
    boolean updateBook(Book book);
    boolean deleteBook(String bookId);

    Book findBookById(String bookId);
    List<Book> findBookByTitle(String title);
    List<Book> getBookList();
    double calculateTotalInventoryValue();
    List<Book> findBookByAuthor(String author);
    List<Book> findBookByPriceRange(double minPrice, double maxPrice);
    List<Book> getSortedByAuthor(boolean ascending);
    List<Book> getSortedByQuantity(boolean ascending);
    List<Book> getSortedByTotalValue(boolean ascending);

    List<Book> sortByPriceAlgorithm(boolean ascending);
    List<Book> sortByTitleAlgorithm(boolean ascending);

    boolean saveToFile(String filePath);
    boolean loadFromFile(String filePath);

    default boolean saveToFile() {
        return saveToFile("Books.txt");
    }

    default boolean loadFromFile() {
        return loadFromFile("Books.txt");
    }

    int getTotalBooksCount();
    int getTotalQuantity();

    boolean reserveBook(String bookId, String memberId);

    List<Book> getBooksWithReservations();
}
