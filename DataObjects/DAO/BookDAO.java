package DataObjects.DAO;

import Core.Entities.Book;
import Core.Entities.ReferenceBook;
import Core.Entities.TextBook;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class BookDAO implements IBookDAO {

    private final List<Book> bookList = new ArrayList<>();

    public BookDAO() {
        initSampleData();
    }

    public BookDAO(boolean loadSampleData) {
        if (loadSampleData) {
            initSampleData();
        }
    }

    public void initSampleData() {
        bookList.clear();
        addBook(new TextBook("B001", "Lập trình Hướng đối tượng Java", 125000, 30, "CNTT"));
        addBook(new TextBook("B002", "Cấu trúc dữ liệu & Giải thuật", 98000, 25, "Khoa học máy tính"));
        addBook(new TextBook("B003", "Cơ sở dữ liệu quan hệ SQL", 85000, 40, "Hệ thống thông tin"));
        addBook(new ReferenceBook("B004", "Clean Code - Nghệ thuật viết code sạch", 240000, 20, "O'Reilly Media"));
        addBook(new ReferenceBook("B005", "Design Patterns: Gang of Four", 320000, 15, "Addison-Wesley"));
    }

    @Override
    public boolean addBook(Book book) {
        if (book == null || book.getBookId() == null || book.getBookId().trim().isEmpty()) {
            return false;
        }
        if (findBookById(book.getBookId()) != null) {
            return false;
        }
        return bookList.add(book);
    }

    @Override
    public boolean updateBook(Book book) {
        if (book == null || book.getBookId() == null) {
            return false;
        }
        Book existing = findBookById(book.getBookId());
        if (existing == null) {
            return false;
        }
        int index = bookList.indexOf(existing);
        bookList.set(index, book);
        return true;
    }

    @Override
    public boolean deleteBook(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            return false;
        }
        Book existing = findBookById(bookId);
        if (existing == null) {
            return false;
        }
        return bookList.remove(existing);
    }

    @Override
    public Book findBookById(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            return null;
        }
        for (Book b : bookList) {
            if (b.getBookId() != null && b.getBookId().equalsIgnoreCase(bookId.trim())) {
                return b;
            }
        }
        return null;
    }

    @Override
    public List<Book> findBookByTitle(String title) {
        List<Book> results = new ArrayList<>();
        if (title == null || title.trim().isEmpty()) {
            return results;
        }
        String keyword = title.trim().toLowerCase();
        for (Book b : bookList) {
            if (b.getTitle() != null && b.getTitle().toLowerCase().contains(keyword)) {
                results.add(b);
            }
        }
        return results;
    }

    @Override
    public List<Book> getBookList() {
        return new ArrayList<>(bookList);
    }

    @Override
    public List<Book> search(Predicate<Book> predicate) {
        if (predicate == null) {
            return getBookList();
        }
        return bookList.stream().filter(predicate).collect(Collectors.toList());
    }

    public int getTotalBooksCount() {
        return bookList.size();
    }

    public int getTotalQuantity() {
        int total = 0;
        for (Book b : bookList) {
            total += b.getQuantity();
        }
        return total;
    }

    public double getTotalInventoryValue() {
        double sum = 0.0;
        for (Book b : bookList) {
            sum += b.calculateTotalValue();
        }
        return sum;
    }

    public List<Book> getSortedByTitle(boolean ascending) {
        List<Book> list = getBookList();
        list.sort((b1, b2) -> ascending
                ? b1.getTitle().compareToIgnoreCase(b2.getTitle())
                : b2.getTitle().compareToIgnoreCase(b1.getTitle()));
        return list;
    }

    public List<Book> getSortedByPrice(boolean ascending) {
        List<Book> list = getBookList();
        list.sort((b1, b2) -> ascending
                ? Double.compare(b1.getPrice(), b2.getPrice())
                : Double.compare(b2.getPrice(), b1.getPrice()));
        return list;
    }

    public List<Book> getSortedByQuantity(boolean ascending) {
        List<Book> list = getBookList();
        list.sort((b1, b2) -> ascending
                ? Integer.compare(b1.getQuantity(), b2.getQuantity())
                : Integer.compare(b2.getQuantity(), b1.getQuantity()));
        return list;
    }

    public List<Book> getSortedByTotalValue(boolean ascending) {
        List<Book> list = getBookList();
        list.sort((b1, b2) -> ascending
                ? Double.compare(b1.calculateTotalValue(), b2.calculateTotalValue())
                : Double.compare(b2.calculateTotalValue(), b1.calculateTotalValue()));
        return list;
    }
}
