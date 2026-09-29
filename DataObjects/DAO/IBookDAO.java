package DataObjects.DAO;

import Core.Entities.Book;
import java.util.List;
import java.util.function.Predicate;

public interface IBookDAO {

    boolean addBook(Book book);
    boolean updateBook(Book book);
    boolean deleteBook(String bookId);

    Book findBookById(String bookId);

    List<Book> findBookByTitle(String title);
    List<Book> getBookList();
    List<Book> search(Predicate<Book> predicate);

    default boolean add(Book book) {
        return addBook(book);
    }

    default boolean update(Book book) {
        return updateBook(book);
    }

    default boolean delete(String bookId) {
        return deleteBook(bookId);
    }

    default Book findById(String bookId) {
        return findBookById(bookId);
    }

    default List<Book> getAll() {
        return getBookList();
    }

    default List<Book> searchByName(String keyword) {
        return findBookByTitle(keyword);
    }
}
