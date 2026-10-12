package DataObjects.DAO;

import Core.Entities.Book;
import Core.Entities.ReferenceBook;
import Core.Entities.TextBook;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Lớp BookDAO hiện thực hóa IBookDAO quản lý danh sách sách trong bộ nhớ (ArrayList<Book>)
 * và đồng bộ lưu trữ với file Books.txt.
 * 
 * Milestone 3: Áp dụng Đa hình (Polymorphism) và Upcasting trong danh sách sách.
 * Milestone 4: Tích hợp đọc/ghi file Books.txt, tìm kiếm đa tiêu chí, cài đặt thuật toán sắp xếp.
 * 
 * PRO192 - Phân hệ Sách & Đa hình (Member 2: Nguyen Thien Tue)
 */
public class BookDAO implements IBookDAO {

    public static final String DEFAULT_FILE_PATH = "Books.txt";
    private final List<Book> bookList = new ArrayList<>();

    public BookDAO() {
        // Tự động kiểm tra nạp dữ liệu từ file Books.txt
        File file = new File(DEFAULT_FILE_PATH);
        if (file.exists() && file.length() > 0) {
            boolean loaded = loadFromFile(DEFAULT_FILE_PATH);
            if (!loaded || bookList.isEmpty()) {
                initSampleData();
                saveToFile(DEFAULT_FILE_PATH);
            }
        } else {
            initSampleData();
            saveToFile(DEFAULT_FILE_PATH);
        }
    }

    public BookDAO(boolean loadSampleData) {
        if (loadSampleData) {
            initSampleData();
        }
    }

    /**
     * Khởi tạo dữ liệu mẫu ban đầu gồm cả Sách giáo trình và Sách tham khảo.
     */
    public void initSampleData() {
        bookList.clear();
        addBook(new TextBook("B001", "Lập trình Hướng đối tượng Java", "Herbert Schildt", 125000, 30, "CNTT"));
        addBook(new TextBook("B002", "Cấu trúc dữ liệu & Giải thuật", "Robert Lafore", 98000, 25, "Khoa học máy tính"));
        addBook(new TextBook("B003", "Cơ sở dữ liệu quan hệ SQL", "Abraham Silberschatz", 85000, 40, "Hệ thống thông tin"));
        addBook(new TextBook("B004", "Mạng máy tính căn bản", "Andrew S. Tanenbaum", 110000, 20, "Mạng & Viễn thông"));
        addBook(new ReferenceBook("B005", "Clean Code - Nghệ thuật viết code sạch", "Robert C. Martin", 240000, 20, "O'Reilly Media"));
        addBook(new ReferenceBook("B006", "Design Patterns: Gang of Four", "Erich Gamma", 320000, 15, "Addison-Wesley"));
        addBook(new ReferenceBook("B007", "Refactoring - Cải tiến mã nguồn", "Martin Fowler", 280000, 18, "Addison-Wesley"));
        addBook(new ReferenceBook("B008", "Head First Design Patterns", "Eric Freeman", 210000, 22, "O'Reilly Media"));
    }

    // ========================================================
    // 1. Thao tác CRUD cơ bản
    // ========================================================

    @Override
    public boolean addBook(Book book) {
        if (book == null || book.getBookId() == null || book.getBookId().trim().isEmpty()) {
            return false;
        }
        if (findBookById(book.getBookId()) != null) {
            return false; // Trùng mã sách
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

    // ========================================================
    // 2. Milestone 3: Nghiệp vụ Đa hình & Upcasting
    // ========================================================

    /**
     * Tính tổng giá trị lưu kho của toàn bộ sách.
     * Áp dụng Upcasting: Các đối tượng TextBook và ReferenceBook được lưu trữ
     * trong List<Book> và phương thức calculateTotalValue() được gọi đa hình.
     */
    @Override
    public double calculateTotalInventoryValue() {
        double sum = 0.0;
        for (Book b : bookList) {
            sum += b.calculateTotalValue();
        }
        return sum;
    }

    /**
     * Tính tổng phí mượn cho một danh sách sách cụ thể theo số ngày mượn.
     * Áp dụng Upcasting & Polymorphism: Mỗi loại sách tự quyết định cách tính phí mượn riêng.
     */
    @Override
    public double calculateTotalBorrowFee(List<Book> books, int days) {
        if (books == null || books.isEmpty()) {
            return 0.0;
        }
        double totalFee = 0.0;
        for (Book b : books) {
            if (b != null) {
                totalFee += b.calculateBorrowFee(days);
            }
        }
        return totalFee;
    }

    // ========================================================
    // 3. Milestone 4: Tìm kiếm đa tiêu chí
    // ========================================================

    @Override
    public List<Book> findBookByAuthor(String author) {
        List<Book> results = new ArrayList<>();
        if (author == null || author.trim().isEmpty()) {
            return results;
        }
        String keyword = author.trim().toLowerCase();
        for (Book b : bookList) {
            if (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(keyword)) {
                results.add(b);
            }
        }
        return results;
    }

    @Override
    public List<Book> findBookByPriceRange(double minPrice, double maxPrice) {
        List<Book> results = new ArrayList<>();
        double lower = Math.min(minPrice, maxPrice);
        double upper = Math.max(minPrice, maxPrice);
        for (Book b : bookList) {
            if (b.getPrice() >= lower && b.getPrice() <= upper) {
                results.add(b);
            }
        }
        return results;
    }

    @Override
    public List<Book> searchMultiCriteria(String title, String author, Double minPrice, Double maxPrice, String bookType) {
        List<Book> results = new ArrayList<>();
        String titleKey = (title != null) ? title.trim().toLowerCase() : "";
        String authorKey = (author != null) ? author.trim().toLowerCase() : "";
        String typeKey = (bookType != null) ? bookType.trim().toUpperCase() : "";

        for (Book b : bookList) {
            boolean matchTitle = titleKey.isEmpty() || (b.getTitle() != null && b.getTitle().toLowerCase().contains(titleKey));
            boolean matchAuthor = authorKey.isEmpty() || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(authorKey));

            boolean matchPrice = true;
            if (minPrice != null && b.getPrice() < minPrice) {
                matchPrice = false;
            }
            if (maxPrice != null && b.getPrice() > maxPrice) {
                matchPrice = false;
            }

            boolean matchType = true;
            if (!typeKey.isEmpty()) {
                if (typeKey.contains("TEXT") && !(b instanceof TextBook)) {
                    matchType = false;
                } else if (typeKey.contains("REF") && !(b instanceof ReferenceBook)) {
                    matchType = false;
                }
            }

            if (matchTitle && matchAuthor && matchPrice && matchType) {
                results.add(b);
            }
        }
        return results;
    }

    // ========================================================
    // 4. Milestone 4: Thuật toán sắp xếp (Sorting Algorithms)
    // ========================================================

    @Override
    public List<Book> getSortedByTitle(boolean ascending) {
        List<Book> list = getBookList();
        list.sort((b1, b2) -> ascending
                ? b1.getTitle().compareToIgnoreCase(b2.getTitle())
                : b2.getTitle().compareToIgnoreCase(b1.getTitle()));
        return list;
    }

    @Override
    public List<Book> getSortedByAuthor(boolean ascending) {
        List<Book> list = getBookList();
        list.sort((b1, b2) -> ascending
                ? b1.getAuthor().compareToIgnoreCase(b2.getAuthor())
                : b2.getAuthor().compareToIgnoreCase(b1.getAuthor()));
        return list;
    }

    @Override
    public List<Book> getSortedByPrice(boolean ascending) {
        List<Book> list = getBookList();
        list.sort((b1, b2) -> ascending
                ? Double.compare(b1.getPrice(), b2.getPrice())
                : Double.compare(b2.getPrice(), b1.getPrice()));
        return list;
    }

    @Override
    public List<Book> getSortedByQuantity(boolean ascending) {
        List<Book> list = getBookList();
        list.sort((b1, b2) -> ascending
                ? Integer.compare(b1.getQuantity(), b2.getQuantity())
                : Integer.compare(b2.getQuantity(), b1.getQuantity()));
        return list;
    }

    @Override
    public List<Book> getSortedByTotalValue(boolean ascending) {
        List<Book> list = getBookList();
        list.sort((b1, b2) -> ascending
                ? Double.compare(b1.calculateTotalValue(), b2.calculateTotalValue())
                : Double.compare(b2.calculateTotalValue(), b1.calculateTotalValue()));
        return list;
    }

    /**
     * Cài đặt giải thuật Selection Sort thủ công để sắp xếp danh sách sách theo đơn giá.
     * Minh họa tư duy tính toán (Computational Thinking) theo yêu cầu môn học.
     * Độ phức tạp: O(n^2).
     */
    @Override
    public List<Book> sortByPriceAlgorithm(boolean ascending) {
        List<Book> list = getBookList();
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int targetIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (ascending) {
                    if (list.get(j).getPrice() < list.get(targetIdx).getPrice()) {
                        targetIdx = j;
                    }
                } else {
                    if (list.get(j).getPrice() > list.get(targetIdx).getPrice()) {
                        targetIdx = j;
                    }
                }
            }
            if (targetIdx != i) {
                Book temp = list.get(i);
                list.set(i, list.get(targetIdx));
                list.set(targetIdx, temp);
            }
        }
        return list;
    }

    /**
     * Cài đặt giải thuật Insertion Sort thủ công để sắp xếp danh sách sách theo tên (Title).
     * Minh họa tư duy tính toán (Computational Thinking) theo yêu cầu môn học.
     * Độ phức tạp: O(n^2).
     */
    @Override
    public List<Book> sortByTitleAlgorithm(boolean ascending) {
        List<Book> list = getBookList();
        int n = list.size();
        for (int i = 1; i < n; i++) {
            Book key = list.get(i);
            int j = i - 1;
            while (j >= 0) {
                int cmp = list.get(j).getTitle().compareToIgnoreCase(key.getTitle());
                if ((ascending && cmp > 0) || (!ascending && cmp < 0)) {
                    list.set(j + 1, list.get(j));
                    j--;
                } else {
                    break;
                }
            }
            list.set(j + 1, key);
        }
        return list;
    }

    // ========================================================
    // 5. Milestone 4: Đọc / Ghi file Books.txt
    // ========================================================

    /**
     * Ghi toàn bộ dữ liệu sách ra file theo định dạng chuẩn UTF-8:
     * LOAI_SACH|BOOK_ID|TITLE|AUTHOR|PRICE|QUANTITY|EXTRA_INFO
     */
    @Override
    public boolean saveToFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = DEFAULT_FILE_PATH;
        }
        File file = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            
            writer.write("# Danh sách lưu trữ sách (Library Management System - PRO192)");
            writer.newLine();
            writer.write("# Format: LOAI_SACH|MA_SACH|TEN_SACH|TAC_GIA|GIA|SO_LUONG|THE_LOAI_HOAC_NXB");
            writer.newLine();

            for (Book b : bookList) {
                String typeStr;
                String extraInfo;
                if (b instanceof TextBook) {
                    typeStr = "TEXTBOOK";
                    extraInfo = ((TextBook) b).getGenre();
                } else if (b instanceof ReferenceBook) {
                    typeStr = "REFERENCEBOOK";
                    extraInfo = ((ReferenceBook) b).getPublisher();
                } else {
                    typeStr = "BOOK";
                    extraInfo = "Chung";
                }

                String line = String.format("%s|%s|%s|%s|%.2f|%d|%s",
                        typeStr,
                        b.getBookId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getPrice(),
                        b.getQuantity(),
                        extraInfo);
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println(">> Lỗi khi lưu file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Đọc và parse dữ liệu sách từ file Books.txt chuẩn UTF-8.
     * Hỗ trợ tự động phân tích cả định dạng 6 trường (không tác giả) và 7 trường (có tác giả).
     */
    @Override
    public boolean loadFromFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = DEFAULT_FILE_PATH;
        }
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        List<Book> loadedList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Bỏ qua dòng trống hoặc comment
                }

                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    try {
                        String type = parts[0].trim().toUpperCase();
                        String id = parts[1].trim();
                        String title = parts[2].trim();
                        String author;
                        double price;
                        int quantity;
                        String extra;

                        if (parts.length >= 7) {
                            author = parts[3].trim();
                            price = Double.parseDouble(parts[4].trim());
                            quantity = Integer.parseInt(parts[5].trim());
                            extra = parts[6].trim();
                        } else {
                            author = "Nhiều tác giả";
                            price = Double.parseDouble(parts[3].trim());
                            quantity = Integer.parseInt(parts[4].trim());
                            extra = parts[5].trim();
                        }

                        Book book;
                        if (type.contains("TEXT")) {
                            book = new TextBook(id, title, author, price, quantity, extra);
                        } else {
                            book = new ReferenceBook(id, title, author, price, quantity, extra);
                        }
                        loadedList.add(book);
                    } catch (Exception ex) {
                        System.err.println(">> Cảnh báo: Bỏ qua dòng dữ liệu không hợp lệ: " + line);
                    }
                }
            }

            if (!loadedList.isEmpty()) {
                this.bookList.clear();
                this.bookList.addAll(loadedList);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println(">> Lỗi khi đọc file: " + e.getMessage());
            return false;
        }
    }
}
