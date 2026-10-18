package DataObjects.DAO;

import Core.Entities.Book;
import Core.Entities.ReferenceBook;
import Core.Entities.TextBook;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
        addBook(new TextBook("B001", "Object-Oriented Programming with Java", "Herbert Schildt", 125000, 30, "Information Technology"));
        addBook(new TextBook("B002", "Data Structures & Algorithms in Java", "Robert Lafore", 98000, 25, "Computer Science"));
        addBook(new TextBook("B003", "Database System Concepts (SQL)", "Abraham Silberschatz", 85000, 40, "Information Systems"));
        addBook(new TextBook("B004", "Computer Networks Principles", "Andrew S. Tanenbaum", 110000, 20, "Telecommunications"));
        addBook(new ReferenceBook("B005", "Clean Code: A Handbook of Agile Craftsmanship", "Robert C. Martin", 240000, 20, "O'Reilly Media"));
        addBook(new ReferenceBook("B006", "Design Patterns: Gang of Four", "Erich Gamma", 320000, 15, "Addison-Wesley"));
        addBook(new ReferenceBook("B007", "Refactoring: Improving Code Design", "Martin Fowler", 280000, 18, "Addison-Wesley"));
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

    /**
     * [LAMBDA & STREAM API]: Tìm kiếm sách theo mã (Book ID).
     * 
     * So sánh:
     * - Cách cũ: Duyệt vòng lặp for-each qua bookList, kiểm tra điều kiện bằng if và return.
     * - Lambda/Stream: Dùng Stream pipeline `filter(...) -> findFirst() -> orElse(null)`.
     *   -> filter nhận Predicate lambda: `b.getBookId() != null && b.getBookId().equalsIgnoreCase(cleanId)`
     *   -> findFirst() dừng quét luồng ngay khi tìm thấy phần tử đầu tiên (short-circuiting).
     */
    @Override
    public Book findBookById(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            return null;
        }
        String cleanId = bookId.trim();
        return bookList.stream()
                .filter(b -> b.getBookId() != null && b.getBookId().equalsIgnoreCase(cleanId))
                .findFirst()
                .orElse(null);
    }

    /**
     * [LAMBDA & STREAM API]: Tìm kiếm sách theo từ khóa tên sách.
     * 
     * So sánh:
     * - Cách cũ: Khởi tạo ArrayList rỗng, duyệt vòng lặp for và add vào danh sách kết quả.
     * - Lambda/Stream: Dùng Stream `filter` và đóng gói bằng `collect(Collectors.toList())`.
     */
    @Override
    public List<Book> findBookByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String keyword = title.trim().toLowerCase();
        return bookList.stream()
                .filter(b -> b.getTitle() != null && b.getTitle().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
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

    @Override
    public int getTotalBooksCount() {
        return bookList.size();
    }

    /**
     * [LAMBDA & STREAM API]: Tính tổng số bản sách trong kho.
     * Áp dụng Method Reference `Book::getQuantity` và IntStream `sum()`.
     */
    @Override
    public int getTotalQuantity() {
        return bookList.stream()
                .mapToInt(Book::getQuantity)
                .sum();
    }

    // ========================================================
    // 2. Milestone 3: Nghiệp vụ Đa hình & Upcasting
    // ========================================================

    /**
     * [LAMBDA & STREAM API & ĐA HÌNH]: Tính tổng giá trị lưu kho của toàn bộ sách.
     * 
     * So sánh:
     * - Cách cũ: Vòng lặp for cộng dồn `sum += b.calculateTotalValue();`.
     * - Lambda/Stream: `bookList.stream().mapToDouble(Book::calculateTotalValue).sum();`.
     *   -> Áp dụng Method Reference đa hình: Tùy loại sách TextBook/ReferenceBook mà phương thức
     *      tính giá trị kho tương ứng sẽ được gọi động trong DoubleStream!
     */
    @Override
    public double calculateTotalInventoryValue() {
        return bookList.stream()
                .mapToDouble(Book::calculateTotalValue)
                .sum();
    }

    /**
     * [LAMBDA & STREAM API & ĐA HÌNH]: Tính tổng phí mượn cho một danh sách sách cụ thể theo số ngày mượn.
     * Áp dụng Upcasting & Polymorphism: Mỗi loại sách tự quyết định cách tính phí mượn riêng.
     */
    @Override
    public double calculateTotalBorrowFee(List<Book> books, int days) {
        if (books == null || books.isEmpty()) {
            return 0.0;
        }
        return books.stream()
                .filter(Objects::nonNull)
                .mapToDouble(b -> b.calculateBorrowFee(days))
                .sum();
    }

    // ========================================================
    // 3. Milestone 4: Tìm kiếm đa tiêu chí
    // ========================================================

    /**
     * [LAMBDA & STREAM API]: Tìm kiếm sách theo tác giả.
     * Áp dụng filter kết hợp biểu thức Lambda kiểm tra chuỗi con không phân biệt hoa thường.
     */
    @Override
    public List<Book> findBookByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String keyword = author.trim().toLowerCase();
        return bookList.stream()
                .filter(b -> b.getAuthor() != null && b.getAuthor().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & STREAM API]: Tìm kiếm sách theo khoảng đơn giá bìa.
     */
    @Override
    public List<Book> findBookByPriceRange(double minPrice, double maxPrice) {
        double lower = Math.min(minPrice, maxPrice);
        double upper = Math.max(minPrice, maxPrice);
        return bookList.stream()
                .filter(b -> b.getPrice() >= lower && b.getPrice() <= upper)
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & STREAM API]: Tìm kiếm kết hợp đa tiêu chí (Multi-criteria Search).
     * 
     * So sánh:
     * - Cách cũ: Khởi tạo biến boolean lồng nhau phức tạp bên trong vòng lặp for.
     * - Lambda/Stream: Dùng pipeline các filter liên tiếp. Mỗi filter đảm nhận một tiêu chí độc lập,
     *   tuân thủ nguyên tắc Single Responsibility và dễ dàng thêm bớt tiêu chí tìm kiếm.
     */
    @Override
    public List<Book> searchMultiCriteria(String title, String author, Double minPrice, Double maxPrice, String bookType) {
        String titleKey = (title != null) ? title.trim().toLowerCase() : "";
        String authorKey = (author != null) ? author.trim().toLowerCase() : "";
        String typeKey = (bookType != null) ? bookType.trim().toUpperCase() : "";

        return bookList.stream()
                .filter(b -> titleKey.isEmpty() || (b.getTitle() != null && b.getTitle().toLowerCase().contains(titleKey)))
                .filter(b -> authorKey.isEmpty() || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(authorKey)))
                .filter(b -> minPrice == null || b.getPrice() >= minPrice)
                .filter(b -> maxPrice == null || b.getPrice() <= maxPrice)
                .filter(b -> {
                    if (typeKey.isEmpty()) {
                        return true;
                    }
                    if (typeKey.contains("TEXT")) {
                        return b instanceof TextBook;
                    }
                    if (typeKey.contains("REF")) {
                        return b instanceof ReferenceBook;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    // ========================================================
    // 4. Milestone 4: Thuật toán sắp xếp (Sorting Algorithms)
    // ========================================================

    /**
     * [LAMBDA & COMPARATOR]: Sắp xếp theo Tên sách (A-Z hoặc Z-A).
     * Sử dụng Comparator.comparing với Method Reference Book::getTitle.
     */
    @Override
    public List<Book> getSortedByTitle(boolean ascending) {
        Comparator<Book> comp = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
        return bookList.stream()
                .sorted(ascending ? comp : comp.reversed())
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & COMPARATOR]: Sắp xếp theo Tác giả (Author).
     */
    @Override
    public List<Book> getSortedByAuthor(boolean ascending) {
        Comparator<Book> comp = Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER);
        return bookList.stream()
                .sorted(ascending ? comp : comp.reversed())
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & COMPARATOR]: Sắp xếp theo Đơn giá bìa (Price).
     * Sử dụng Comparator.comparingDouble để tránh boxing/unboxing overhead.
     */
    @Override
    public List<Book> getSortedByPrice(boolean ascending) {
        Comparator<Book> comp = Comparator.comparingDouble(Book::getPrice);
        return bookList.stream()
                .sorted(ascending ? comp : comp.reversed())
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & COMPARATOR]: Sắp xếp theo Số lượng tồn kho (Quantity).
     * Sử dụng Comparator.comparingInt.
     */
    @Override
    public List<Book> getSortedByQuantity(boolean ascending) {
        Comparator<Book> comp = Comparator.comparingInt(Book::getQuantity);
        return bookList.stream()
                .sorted(ascending ? comp : comp.reversed())
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & COMPARATOR & ĐA HÌNH]: Sắp xếp theo Tổng giá trị lưu kho (Total Inventory Value).
     * Method Reference Book::calculateTotalValue được kích hoạt đa hình cho từng phần tử.
     */
    @Override
    public List<Book> getSortedByTotalValue(boolean ascending) {
        Comparator<Book> comp = Comparator.comparingDouble(Book::calculateTotalValue);
        return bookList.stream()
                .sorted(ascending ? comp : comp.reversed())
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & GIẢI THUẬT SELECTION SORT]:
     * Cài đặt giải thuật Selection Sort thủ công kết hợp Lambda Comparator để tùy biến tiêu chí so sánh.
     * Vừa đáp ứng tư duy giải thuật O(n^2) vừa áp dụng lập trình hàm hiện đại.
     */
    @Override
    public List<Book> sortByPriceAlgorithm(boolean ascending) {
        List<Book> list = getBookList();
        int n = list.size();
        // Áp dụng biểu thức Lambda định nghĩa chiến lược so sánh
        Comparator<Book> comp = ascending
                ? Comparator.comparingDouble(Book::getPrice)
                : Comparator.comparingDouble(Book::getPrice).reversed();

        for (int i = 0; i < n - 1; i++) {
            int targetIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (comp.compare(list.get(j), list.get(targetIdx)) < 0) {
                    targetIdx = j;
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
     * [LAMBDA & GIẢI THUẬT INSERTION SORT]:
     * Cài đặt giải thuật Insertion Sort thủ công kết hợp Lambda Comparator theo tên sách.
     */
    @Override
    public List<Book> sortByTitleAlgorithm(boolean ascending) {
        List<Book> list = getBookList();
        int n = list.size();
        Comparator<Book> comp = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);

        for (int i = 1; i < n; i++) {
            Book key = list.get(i);
            int j = i - 1;
            while (j >= 0) {
                int cmp = comp.compare(list.get(j), key);
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
     * [LAMBDA & STREAM API]: Chuyển đổi đối tượng Book thành chuỗi định dạng lưu file.
     */
    public static String toFileString(Book b) {
        if (b == null) return null;
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
            extraInfo = "General";
        }

        return String.format("%s|%s|%s|%s|%.2f|%d|%s|%.2f|%d",
                typeStr,
                b.getBookId(),
                b.getTitle(),
                b.getAuthor(),
                b.getPrice(),
                b.getQuantity(),
                extraInfo,
                b.getRating(),
                b.getRatingCount());
    }

    /**
     * [LAMBDA & STREAM API]: Ghi toàn bộ dữ liệu sách ra file với FileManager và Stream map.
     * Áp dụng Method Reference BookDAO::toFileString và filter loại bỏ null.
     */
    @Override
    public boolean saveToFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = DEFAULT_FILE_PATH;
        }
        try {
            List<String> lines = new ArrayList<>();
            lines.add("# Book Storage Repository (Library Management System - PRO192)");
            lines.add("# Format: BOOK_TYPE|BOOK_ID|TITLE|AUTHOR|PRICE|QUANTITY|GENRE_OR_PUBLISHER|RATING|RATING_COUNT");
            bookList.stream()
                    .filter(Objects::nonNull)
                    .map(BookDAO::toFileString)
                    .filter(Objects::nonNull)
                    .forEach(lines::add);
            FileManager.writeLines(filePath, lines);
            return true;
        } catch (IOException e) {
            System.err.println(">> Error saving file: " + e.getMessage());
            return false;
        }
    }

    /**
     * [LAMBDA & STREAM API]: Parse một dòng văn bản từ file thành đối tượng Book (TextBook hoặc ReferenceBook).
     */
    public static Book parseBookFromLine(String line) {
        if (line == null || line.trim().isEmpty() || line.trim().startsWith("#")) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length < 6) {
            return null;
        }
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
                author = "Various Authors";
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

            if (parts.length >= 9) {
                book.setRating(Double.parseDouble(parts[7].trim()));
                book.setRatingCount(Integer.parseInt(parts[8].trim()));
            }
            return book;
        } catch (Exception ex) {
            System.err.println(">> Warning: Skipping invalid data line: " + line);
            return null;
        }
    }

    /**
     * [LAMBDA & STREAM API]: Đọc và parse dữ liệu sách từ file Books.txt với Stream pipeline.
     * Sử dụng FileManager.readLines và Stream.map(BookDAO::parseBookFromLine).
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

        try {
            List<String> lines = FileManager.readLines(filePath);
            List<Book> loadedList = lines.stream()
                    .map(BookDAO::parseBookFromLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!loadedList.isEmpty()) {
                this.bookList.clear();
                this.bookList.addAll(loadedList);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println(">> Error reading file: " + e.getMessage());
            return false;
        }
    }

    // ========================================================
    // 6. Tính năng 3 & 7: Đánh giá Sao, Gợi ý Sách & Đặt trước
    //    (Hiện thực hóa bằng Stream API & Biểu thức Lambda)
    // ========================================================

    @Override
    public boolean rateBook(String bookId, int stars) {
        Book book = findBookById(bookId);
        if (book == null || stars < 1 || stars > 5) {
            return false;
        }
        book.addRating(stars);
        saveToFile();
        return true;
    }

    /**
     * [LAMBDA & STREAM API PIPELINE]: Tính năng 3 - Gợi ý Top N cuốn sách liên quan có điểm đánh giá cao nhất.
     * 
     * So sánh:
     * - Cách cũ: Dùng 3 bước riêng biệt: (1) for loop lọc, (2) Collections.sort với anonymous Comparator 25 dòng, (3) for loop cắt top N.
     * - Lambda/Stream: Toàn bộ được thu gọn vào một Stream pipeline thông suốt và mang tính khai báo:
     *   1. filter: Lọc bỏ chính cuốn sách đang xem và chỉ giữ lại sách còn hàng (quantity > 0)
     *   2. sorted: Định nghĩa chiến lược sắp xếp bằng biểu thức Lambda đa tiêu chí:
     *      - Tiêu chí 1: Ưu tiên cùng phân loại thực thể (TextBook / ReferenceBook)
     *      - Tiêu chí 2: Điểm đánh giá sao giảm dần (High -> Low)
     *      - Tiêu chí 3: Tên sách theo thứ tự bảng chữ cái A-Z
     *   3. limit(topN): Giới hạn lấy tối đa N cuốn sách dẫn đầu (tích hợp sẵn, không lo IndexOutOfBoundsException)
     *   4. collect(Collectors.toList()): Đóng gói luồng dữ liệu vào danh sách kết quả.
     */
    @Override
    public List<Book> getRecommendedBooks(String bookId, int topN) {
        Book target = findBookById(bookId);

        // Biểu thức Lambda Comparator đa tiêu chí cho việc gợi ý sách
        Comparator<Book> recommendationComparator = (b1, b2) -> {
            if (target != null) {
                boolean b1SameType = b1.getClass().equals(target.getClass());
                boolean b2SameType = b2.getClass().equals(target.getClass());
                if (b1SameType && !b2SameType) {
                    return -1; // b1 cùng loại -> ưu tiên lên trước
                } else if (!b1SameType && b2SameType) {
                    return 1;
                }
            }
            int ratingCompare = Double.compare(b2.getRating(), b1.getRating());
            if (ratingCompare != 0) {
                return ratingCompare;
            }
            return b1.getTitle().compareToIgnoreCase(b2.getTitle());
        };

        return bookList.stream()
                .filter(b -> target == null || !b.getBookId().equalsIgnoreCase(target.getBookId()))
                .filter(b -> b.getQuantity() > 0)
                .sorted(recommendationComparator)
                .limit(Math.max(0, topN))
                .collect(Collectors.toList());
    }

    /**
     * Tính năng 7: Đặt trước sách khi sách hết hàng (hoặc đăng ký vào hàng đợi).
     */
    @Override
    public boolean reserveBook(String bookId, String memberId) {
        Book book = findBookById(bookId);
        if (book == null) {
            return false;
        }
        return book.addReservation(memberId);
    }

    /**
     * [LAMBDA & STREAM API]: Lấy danh sách các cuốn sách đang có người chờ trong hàng đợi.
     * Sử dụng filter kiểm tra !getReservationQueue().isEmpty() và thu thập kết quả.
     */
    @Override
    public List<Book> getBooksWithReservations() {
        return bookList.stream()
                .filter(b -> !b.getReservationQueue().isEmpty())
                .collect(Collectors.toList());
    }
}
