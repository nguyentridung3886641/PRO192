package DataObjects.DAO;

import Core.Entities.Book;
import java.util.List;
import java.util.function.Predicate;

/**
 * Interface IBookDAO defining CRUD and search operations for Book entities.
 * Milestone 2 - PRO192 (Member 2: Book & Publication Module)
 */
public interface IBookDAO {

    /**
     * Add a new book to the collection.
     * @param book the Book object to add
     * @return true if added successfully, false if book is null or ID already exists
     */
    boolean addBook(Book book);

    /**
     * Update an existing book's information.
     * @param book the updated Book object
     * @return true if updated successfully, false if book does not exist
     */
    boolean updateBook(Book book);

    /**
     * Delete a book from the collection by its ID.
     * @param bookId the unique identifier of the book to remove
     * @return true if removed successfully, false if book not found
     */
    boolean deleteBook(String bookId);

    /**
     * Find a book by its unique ID (case-insensitive).
     * @param bookId the ID to search for
     * @return the Book object if found, null otherwise
     */
    Book findBookById(String bookId);

    /**
     * Find all books whose title contains the given keyword (case-insensitive).
     * @param title the search keyword
     * @return list of matching books (empty list if none found)
     */
    List<Book> findBookByTitle(String title);

    /**
     * Retrieve all books currently in the collection.
     * @return a list containing all books
     */
    List<Book> getBookList();

    /**
     * Search books matching a specific condition.
     * @param predicate filter condition
     * @return list of matching books
     */
    List<Book> search(Predicate<Book> predicate);

    // ==========================================
    // Convenience aliases for CRUD compatibility
    // ==========================================

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
