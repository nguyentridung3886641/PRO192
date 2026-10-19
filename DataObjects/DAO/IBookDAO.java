package DataObjects.DAO;

import Core.Entities.Book;
import java.util.List;

/**
 * Interface IBookDAO định nghĩa các thao tác quản lý dữ liệu Sách:
 * - CRUD cơ bản: thêm, sửa, xóa, tìm kiếm theo mã, tên
 * - Milestone 3: Nghiệp vụ đa hình, tính tổng trị giá kho và phí mượn
 * - Milestone 4: Tìm kiếm theo tiêu chí, thuật toán sắp xếp và đọc/ghi file Books.txt
 * 
 * PRO192 - Phân hệ Sách & Đa hình (Member 2: Nguyen Thien Tue)
 */
public interface IBookDAO {

    // ==========================================
    // 1. Thao tác CRUD cơ bản
    // ==========================================

    boolean addBook(Book book);
    boolean updateBook(Book book);
    boolean deleteBook(String bookId);

    Book findBookById(String bookId);
    List<Book> findBookByTitle(String title);
    List<Book> getBookList();

    // ==========================================
    // 2. Milestone 3: Nghiệp vụ Đa hình & Upcasting
    // ==========================================

    double calculateTotalInventoryValue();
    double calculateTotalBorrowFee(List<Book> books, int days);

    // ==========================================
    // 3. Milestone 4: Tìm kiếm theo tiêu chí
    // ==========================================

    List<Book> findBookByAuthor(String author);
    List<Book> findBookByPriceRange(double minPrice, double maxPrice);

    // ==========================================
    // 4. Milestone 4: Thuật toán sắp xếp (Sorting)
    // ==========================================

    List<Book> getSortedByTitle(boolean ascending);
    List<Book> getSortedByAuthor(boolean ascending);
    List<Book> getSortedByPrice(boolean ascending);
    List<Book> getSortedByQuantity(boolean ascending);
    List<Book> getSortedByTotalValue(boolean ascending);

    // Cài đặt thuật toán sắp xếp thủ công (Selection Sort / Insertion Sort)
    List<Book> sortByPriceAlgorithm(boolean ascending);
    List<Book> sortByTitleAlgorithm(boolean ascending);

    // ==========================================
    // 5. Milestone 4: Lưu trữ & Nạp từ file Books.txt
    // ==========================================

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

    // ==========================================
    // 6. Tính năng mở rộng: Đặt trước sách khi hết hàng
    // ==========================================

    boolean reserveBook(String bookId, String memberId);

    List<Book> getBooksWithReservations();
}
