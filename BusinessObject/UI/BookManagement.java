package BusinessObject.UI;

import Core.Entities.Book;
import Core.Entities.ReferenceBook;
import Core.Entities.TextBook;
import DataObjects.DAO.BookDAO;
import DataObjects.DAO.IBookDAO;
import Utilities.DataInput;
import Utilities.DataValidation;
import Utilities.Menu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Giao diện điều khiển phân hệ Kho Tài liệu / Sách (Book Management UI).
 * Thể hiện đầy đủ:
 * - Milestone 3: Hiện thực hóa Đa hình (Polymorphism) và Upcasting trên danh sách sách.
 * - Milestone 4: Tích hợp đọc/ghi file Books.txt, tìm kiếm đa tiêu chí, thuật toán sắp xếp.
 * 
 * PRO192 - Phân hệ Sách & Đa hình (Member 2: Nguyen Thien Tue)
 */
public class BookManagement {

    private final IBookDAO bookDAO;

    public BookManagement() {
        this.bookDAO = new BookDAO();
    }

    public BookManagement(IBookDAO bookDAO) {
        this.bookDAO = (bookDAO != null) ? bookDAO : new BookDAO();
    }

    public void processMenuForBook() {
        boolean running = true;
        while (running) {
            printMenuHeader();
            int choice = DataInput.getIntegerNumber(">> Nhập lựa chọn của bạn [0-12]: ");
            System.out.println();
            switch (choice) {
                case 1:
                    printAllBooks();
                    break;
                case 2:
                    addNewBook();
                    break;
                case 3:
                    updateBook();
                    break;
                case 4:
                    deleteBook();
                    break;
                case 5:
                    searchBooksSubMenu();
                    break;
                case 6:
                    sortBooksSubMenu();
                    break;
                case 7:
                    simulateBorrowFeePolymorphism();
                    break;
                case 8:
                    fileManagementSubMenu();
                    break;
                case 9:
                    displayInventoryReport();
                    break;
                case 10:
                    rateAndRecommendSubMenu();
                    break;
                case 11:
                    reservationQueueSubMenu();
                    break;
                case 12:
                    displayBookInventoryChart();
                    break;
                case 0:
                    System.out.println(">> Đang lưu dữ liệu và thoát khỏi phân hệ Quản lý Sách. Hẹn gặp lại!");
                    bookDAO.saveToFile();
                    running = false;
                    break;
                default:
                    System.out.println(">> Lựa chọn không hợp lệ! Vui lòng chọn số từ 0 đến 12.");
                    break;
            }
            if (running) {
                System.out.println("\nNhấn Enter để tiếp tục...");
                DataInput.getString("");
            }
        }
    }

    private void printMenuHeader() {
        System.out.println();
        System.out.println("========================================================================================================");
        System.out.println("                      HỆ THỐNG QUẢN LÝ KHO SÁCH & TÀI LIỆU (BOOK MANAGEMENT)                            ");
        System.out.println("                      PRO192 - Member 2: Nguyen Thien Tue (Milestones 3 & 4)                            ");
        System.out.println("========================================================================================================");
        System.out.println("  1. Xem danh sách toàn bộ sách (Bảng biểu chi tiết, áp dụng Đa hình & Upcasting)                       ");
        System.out.println("  2. Thêm sách mới vào kho (Sách giáo trình / Sách tham khảo, hỗ trợ Tác giả)                           ");
        System.out.println("  3. Cập nhật thông tin sách theo Mã sách                                                               ");
        System.out.println("  4. Xóa sách khỏi kho lưu trữ                                                                          ");
        System.out.println("  5. Tìm kiếm sách đa tiêu chí (Theo mã, tên, tác giả, khoảng giá, kết hợp)                            ");
        System.out.println("  6. Sắp xếp danh sách sách (Đa tiêu chí: Tên, Tác giả, Giá, SL, Thuật toán Selection/Insertion Sort)  ");
        System.out.println("  7. Mô phỏng tính phí mượn theo Đa hình (Polymorphic Borrow Fee Simulator)                             ");
        System.out.println("  8. Quản lý lưu trữ file Books.txt (Lưu file / Tải lại file)                                           ");
        System.out.println("  9. Báo cáo thống kê & Tổng quan giá trị kho sách                                                      ");
        System.out.println(" 10. Đánh giá Sao (1-5 ⭐) & Gợi ý Sách Thông minh (Smart Book Recommendation)                          ");
        System.out.println(" 11. Hàng đợi Đặt trước Sách khi hết hàng (Book Reservation Queue)                                      ");
        System.out.println(" 12. Biểu đồ Trực quan Tồn kho theo Đầu sách (Console ASCII Bar Chart)                                  ");
        System.out.println("  0. Quay lại Menu chính / Thoát                                                                        ");
        System.out.println("========================================================================================================");
    }

    /**
     * In danh sách sách dưới dạng bảng biểu chi tiết.
     * Minh họa rõ nét tính Đa hình (Polymorphism) và Upcasting trong danh sách List<Book>:
     * - calculatePrice(): Đơn giá ưu đãi/sau thuế của từng loại sách
     * - calculateBorrowFee(1): Phí mượn theo ngày riêng biệt
     * - calculateTotalValue(): Tổng giá trị lưu kho
     */
    public void printBookTable(List<Book> books, String tableTitle) {
        String sep = "+----------+----------------------------------+----------------------+-----------------+------------------------+-----+----------------+----------------+----------------+----------------+";
        int totalWidth = sep.length();

        System.out.println();
        System.out.println(makeLine('=', totalWidth));
        System.out.println(centerText(tableTitle.toUpperCase(), totalWidth));
        System.out.println(sep);
        System.out.printf("| %-8s | %-32s | %-20s | %-15s | %-22s | %-3s | %-14s | %-14s | %-14s | %-14s |%n",
                "MÃ SÁCH", "TÊN SÁCH", "TÁC GIẢ", "PHÂN LOẠI", "THỂ LOẠI / NXB", "SL", "GIÁ BÌA(VNĐ)", "GIÁ ĐA HÌNH", "PHÍ MƯỢN/NGÀY", "THÀNH TIỀN(VNĐ)");
        System.out.println(sep);

        if (books == null || books.isEmpty()) {
            System.out.printf("|%s|%n", centerText("KHÔNG CÓ DỮ LIỆU SÁCH ĐỂ HIỂN THỊ", totalWidth - 2));
            System.out.println(sep);
            return;
        }

        int totalQty = 0;
        double totalVal = 0.0;

        for (Book b : books) {
            String typeStr;
            String extraInfo;

            // Áp dụng Upcasting & Dynamic Method Dispatch
            if (b instanceof TextBook) {
                typeStr = "Giáo trình";
                extraInfo = ((TextBook) b).getGenre();
            } else if (b instanceof ReferenceBook) {
                typeStr = "Tham khảo";
                extraInfo = ((ReferenceBook) b).getPublisher();
            } else {
                typeStr = "Sách chung";
                extraInfo = "-";
            }

            String displayTitle = truncate(b.getTitle(), 32);
            String displayAuthor = truncate(b.getAuthor(), 20);
            String displayExtra = truncate(extraInfo, 22);
            double polyPrice = b.calculatePrice();
            double borrowFee = b.calculateBorrowFee(1);
            double itemTotal = b.calculateTotalValue();

            System.out.printf("| %-8s | %-32s | %-20s | %-15s | %-22s | %3d | %,14.2f | %,14.2f | %,14.2f | %,14.2f |%n",
                    b.getBookId(),
                    displayTitle,
                    displayAuthor,
                    typeStr,
                    displayExtra,
                    b.getQuantity(),
                    b.getPrice(),
                    polyPrice,
                    borrowFee,
                    itemTotal);

            totalQty += b.getQuantity();
            totalVal += itemTotal;
        }

        System.out.println(sep);
        String summaryPart1 = String.format("Tổng: %d đầu sách", books.size());
        String summaryPart2 = String.format("Tổng số bản in: %,d cuốn", totalQty);
        String summaryPart3 = String.format("Tổng giá trị kho: %,.2f VNĐ", totalVal);

        System.out.printf("| %-45s | %-55s | %-62s |%n", summaryPart1, summaryPart2, summaryPart3);
        System.out.println(makeLine('=', totalWidth));
    }

    public void printAllBooks() {
        List<Book> list = bookDAO.getBookList();
        printBookTable(list, "DANH SÁCH TOÀN BỘ SÁCH TRONG KHO THƯ VIỆN");
    }

    public void addNewBook() {
        System.out.println("\n--- [ THÊM SÁCH MỚI VÀO KHO ] ---");
        System.out.println("Chọn loại sách cần thêm:");
        System.out.println("  1. Sách giáo trình (TextBook) - Giảm 10% ưu đãi sinh viên, phí mượn 2.000đ/ngày");
        System.out.println("  2. Sách tham khảo (ReferenceBook) - Thuế VAT/bản quyền 10%, phí mượn 5.000đ/ngày");
        int typeChoice = DataInput.getIntegerNumber(">> Lựa chọn [1 hoặc 2]: ", 1, 2);

        String bookId;
        while (true) {
            bookId = DataInput.getNonEmptyString("Nhập Mã sách (VD: B009, TB001): ").toUpperCase();
            if (bookDAO.findBookById(bookId) != null) {
                System.out.println(">> Lỗi: Mã sách '" + bookId + "' đã tồn tại! Vui lòng nhập mã khác.");
            } else {
                break;
            }
        }

        String title = DataInput.getNonEmptyString("Nhập Tên sách: ");
        String author = DataInput.getNonEmptyString("Nhập Tác giả: ");
        double price = DataInput.getPositiveDouble("Nhập Giá bìa niêm yết (VNĐ): ");
        int quantity = DataInput.getIntegerNumber("Nhập Số lượng tồn kho (>= 0): ", 0, 100000);

        Book newBook;
        if (typeChoice == 1) {
            String genre = DataInput.getNonEmptyString("Nhập Thể loại / Chuyên ngành giáo trình: ");
            newBook = new TextBook(bookId, title, author, price, quantity, genre);
        } else {
            String publisher = DataInput.getNonEmptyString("Nhập Tên Nhà xuất bản (NXB): ");
            newBook = new ReferenceBook(bookId, title, author, price, quantity, publisher);
        }

        boolean success = bookDAO.addBook(newBook);
        if (success) {
            bookDAO.saveToFile();
            System.out.println("\n>> THÀNH CÔNG: Đã thêm sách mới và lưu trữ đồng bộ vào Books.txt!");
            List<Book> single = new ArrayList<>();
            single.add(newBook);
            printBookTable(single, "THÔNG TIN SÁCH VỪA THÊM");
        } else {
            System.out.println("\n>> THẤT BÀI: Không thể thêm sách vào kho!");
        }
    }

    public void updateBook() {
        System.out.println("\n--- [ CẬP NHẬT THÔNG TIN SÁCH ] ---");
        String bookId = DataInput.getNonEmptyString("Nhập Mã sách cần cập nhật: ");
        Book current = bookDAO.findBookById(bookId);

        if (current == null) {
            System.out.println(">> Không tìm thấy sách nào với mã: " + bookId);
            return;
        }

        List<Book> preview = new ArrayList<>();
        preview.add(current);
        printBookTable(preview, "THÔNG TIN HIỆN TẠI CỦA SÁCH");

        System.out.println(">> Hướng dẫn: Nhập thông tin mới hoặc nhấn Enter để giữ nguyên giá trị cũ.");

        String newTitle = DataInput.getString(String.format("Tên mới [Hiện tại: '%s']: ", current.getTitle()));
        if (!newTitle.trim().isEmpty()) {
            current.setTitle(newTitle.trim());
        }

        String newAuthor = DataInput.getString(String.format("Tác giả mới [Hiện tại: '%s']: ", current.getAuthor()));
        if (!newAuthor.trim().isEmpty()) {
            current.setAuthor(newAuthor.trim());
        }

        String priceStr = DataInput.getString(String.format("Đơn giá bìa mới [Hiện tại: %,.2f VNĐ]: ", current.getPrice()));
        if (!priceStr.trim().isEmpty()) {
            try {
                double newPrice = Double.parseDouble(priceStr.trim());
                if (newPrice > 0) {
                    current.setPrice(newPrice);
                } else {
                    System.out.println(">> Đơn giá không hợp lệ, giữ nguyên giá cũ.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">> Định dạng số không hợp lệ, giữ nguyên giá cũ.");
            }
        }

        String qtyStr = DataInput.getString(String.format("Số lượng mới [Hiện tại: %d]: ", current.getQuantity()));
        if (!qtyStr.trim().isEmpty()) {
            try {
                int newQty = Integer.parseInt(qtyStr.trim());
                if (newQty >= 0) {
                    current.setQuantity(newQty);
                } else {
                    System.out.println(">> Số lượng không được âm, giữ nguyên số lượng cũ.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">> Định dạng số không hợp lệ, giữ nguyên số lượng cũ.");
            }
        }

        if (current instanceof TextBook) {
            TextBook tb = (TextBook) current;
            String newGenre = DataInput.getString(String.format("Thể loại mới [Hiện tại: '%s']: ", tb.getGenre()));
            if (!newGenre.trim().isEmpty()) {
                tb.setGenre(newGenre.trim());
            }
        } else if (current instanceof ReferenceBook) {
            ReferenceBook rb = (ReferenceBook) current;
            String newPub = DataInput.getString(String.format("Nhà xuất bản mới [Hiện tại: '%s']: ", rb.getPublisher()));
            if (!newPub.trim().isEmpty()) {
                rb.setPublisher(newPub.trim());
            }
        }

        boolean ok = bookDAO.updateBook(current);
        if (ok) {
            bookDAO.saveToFile();
            System.out.println("\n>> THÀNH CÔNG: Đã cập nhật thông tin sách và đồng bộ vào Books.txt!");
            printBookTable(preview, "THÔNG TIN SÁCH SAU KHI CẬP NHẬT");
        } else {
            System.out.println("\n>> THẤT BÀI: Cập nhật thông tin sách không thành công!");
        }
    }

    public void deleteBook() {
        System.out.println("\n--- [ XÓA SÁCH KHỎI HỆ THỐNG ] ---");
        String bookId = DataInput.getNonEmptyString("Nhập Mã sách cần xóa: ");
        Book book = bookDAO.findBookById(bookId);

        if (book == null) {
            System.out.println(">> Không tìm thấy sách nào với mã: " + bookId);
            return;
        }

        List<Book> preview = new ArrayList<>();
        preview.add(book);
        printBookTable(preview, "SÁCH ĐƯỢC CHỌN ĐỂ XÓA");

        boolean confirm = DataInput.getBoolean(">> Bạn có chắc chắn muốn xóa cuốn sách này khỏi kho? ");
        if (confirm) {
            boolean ok = bookDAO.deleteBook(bookId);
            if (ok) {
                bookDAO.saveToFile();
                System.out.println(">> THÀNH CÔNG: Đã xóa cuốn sách có mã '" + bookId + "' và đồng bộ vào Books.txt!");
            } else {
                System.out.println(">> THẤT BÀI: Không thể xóa cuốn sách!");
            }
        } else {
            System.out.println(">> Đã hủy thao tác xóa sách.");
        }
    }

    // ========================================================
    // Milestone 4: Menu tìm kiếm đa tiêu chí
    // ========================================================

    public void searchBooksSubMenu() {
        System.out.println("\n--- [ TÌM KIẾM SÁCH ĐA TIÊU CHÍ (MILESTONE 4) ] ---");
        System.out.println("  1. Tìm kiếm theo Mã sách (Book ID)");
        System.out.println("  2. Tìm kiếm theo Tên sách (Title)");
        System.out.println("  3. Tìm kiếm theo Tác giả (Author)");
        System.out.println("  4. Tìm kiếm theo Khoảng giá bìa (Price Range)");
        System.out.println("  5. Tìm kiếm kết hợp đa tiêu chí (Tên + Tác giả + Giá + Phân loại)");
        System.out.println("  0. Quay lại");

        int choice = DataInput.getIntegerNumber(">> Chọn tiêu chí tìm kiếm [0-5]: ", 0, 5);
        switch (choice) {
            case 1:
                findBookById();
                break;
            case 2:
                searchBooksByTitle();
                break;
            case 3:
                searchBooksByAuthor();
                break;
            case 4:
                searchBooksByPriceRange();
                break;
            case 5:
                searchBooksCombinedMultiCriteria();
                break;
            default:
                break;
        }
    }

    public void findBookById() {
        String bookId = DataInput.getNonEmptyString("Nhập Mã sách cần tìm: ");
        Book book = bookDAO.findBookById(bookId);
        if (book != null) {
            List<Book> list = new ArrayList<>();
            list.add(book);
            printBookTable(list, "KẾT QUẢ TÌM KIẾM CHO MÃ: " + bookId.toUpperCase());
        } else {
            System.out.println(">> Không tìm thấy sách nào có mã: " + bookId);
        }
    }

    public void searchBooksByTitle() {
        String keyword = DataInput.getNonEmptyString("Nhập từ khóa Tên sách: ");
        List<Book> results = bookDAO.findBookByTitle(keyword);
        if (!results.isEmpty()) {
            printBookTable(results, "KẾT QUẢ TÌM KIẾM THEO TÊN: '" + keyword + "' (" + results.size() + " kết quả)");
        } else {
            System.out.println(">> Không tìm thấy cuốn sách nào chứa từ khóa tên: '" + keyword + "'");
        }
    }

    public void searchBooksByAuthor() {
        String author = DataInput.getNonEmptyString("Nhập từ khóa Tác giả: ");
        List<Book> results = bookDAO.findBookByAuthor(author);
        if (!results.isEmpty()) {
            printBookTable(results, "KẾT QUẢ TÌM KIẾM THEO TÁC GIẢ: '" + author + "' (" + results.size() + " kết quả)");
        } else {
            System.out.println(">> Không tìm thấy cuốn sách nào của tác giả: '" + author + "'");
        }
    }

    public void searchBooksByPriceRange() {
        double minPrice = DataInput.getDoubleNumber("Nhập Giá thấp nhất (VNĐ): ");
        double maxPrice = DataInput.getDoubleNumber("Nhập Giá cao nhất (VNĐ): ");
        List<Book> results = bookDAO.findBookByPriceRange(minPrice, maxPrice);
        if (!results.isEmpty()) {
            String title = String.format("KẾT QUẢ TÌM KIẾM TRONG KHOẢNG GIÁ [%,.0f - %,.0f VNĐ] (%d kết quả)", 
                    Math.min(minPrice, maxPrice), Math.max(minPrice, maxPrice), results.size());
            printBookTable(results, title);
        } else {
            System.out.printf(">> Không tìm thấy sách nào trong khoảng giá [%,.0f - %,.0f VNĐ]%n", minPrice, maxPrice);
        }
    }

    public void searchBooksCombinedMultiCriteria() {
        System.out.println("\n--- [ TÌM KIẾM KẾT HỢP ĐA TIÊU CHÍ ] ---");
        System.out.println(">> Ghi chú: Nhấn Enter để bỏ qua tiêu chí không quan tâm.");

        String title = DataInput.getString("1. Từ khóa Tên sách: ");
        String author = DataInput.getString("2. Từ khóa Tác giả: ");

        String minStr = DataInput.getString("3. Giá thấp nhất (VNĐ): ");
        Double minPrice = null;
        if (!minStr.trim().isEmpty()) {
            try { minPrice = Double.parseDouble(minStr.trim()); } catch (NumberFormatException ignored) {}
        }

        String maxStr = DataInput.getString("4. Giá cao nhất (VNĐ): ");
        Double maxPrice = null;
        if (!maxStr.trim().isEmpty()) {
            try { maxPrice = Double.parseDouble(maxStr.trim()); } catch (NumberFormatException ignored) {}
        }

        System.out.println("5. Phân loại sách: (1) Giáo trình, (2) Tham khảo, (Enter để chọn tất cả)");
        String typeInput = DataInput.getString(">> Lựa chọn phân loại: ");
        String bookType = "";
        if ("1".equals(typeInput.trim())) {
            bookType = "TEXT";
        } else if ("2".equals(typeInput.trim())) {
            bookType = "REF";
        }

        List<Book> results = bookDAO.searchMultiCriteria(title, author, minPrice, maxPrice, bookType);
        if (!results.isEmpty()) {
            printBookTable(results, "KẾT QUẢ TÌM KIẾM ĐA TIÊU CHÍ KẾT HỢP (" + results.size() + " kết quả)");
        } else {
            System.out.println(">> Không tìm thấy sách nào thỏa mãn đồng thời các tiêu chí trên.");
        }
    }

    // ========================================================
    // Milestone 4: Menu thuật toán sắp xếp
    // ========================================================

    public void sortBooksSubMenu() {
        System.out.println("\n--- [ SẮP XẾP DANH SÁCH SÁCH (MILESTONE 4) ] ---");
        System.out.println("  1. Sắp xếp theo Tên sách (A -> Z) [Cài đặt giải thuật Insertion Sort]");
        System.out.println("  2. Sắp xếp theo Tên sách (Z -> A)");
        System.out.println("  3. Sắp xếp theo Tác giả (A -> Z)");
        System.out.println("  4. Sắp xếp theo Tác giả (Z -> A)");
        System.out.println("  5. Sắp xếp theo Đơn giá tăng dần [Cài đặt giải thuật Selection Sort]");
        System.out.println("  6. Sắp xếp theo Đơn giá giảm dần [Cài đặt giải thuật Selection Sort]");
        System.out.println("  7. Sắp xếp theo Số lượng tồn kho giảm dần");
        System.out.println("  8. Sắp xếp theo Tổng giá trị lưu kho giảm dần");
        System.out.println("  0. Quay lại");

        int sortChoice = DataInput.getIntegerNumber(">> Chọn tiêu chí sắp xếp [0-8]: ", 0, 8);
        List<Book> list;
        String title;

        switch (sortChoice) {
            case 1:
                list = bookDAO.sortByTitleAlgorithm(true);
                title = "SẮP XẾP THEO TÊN (A - Z) [INSERTION SORT ALGORITHM]";
                break;
            case 2:
                list = bookDAO.sortByTitleAlgorithm(false);
                title = "SẮP XẾP THEO TÊN (Z - A)";
                break;
            case 3:
                list = bookDAO.getSortedByAuthor(true);
                title = "SẮP XẾP THEO TÁC GIẢ (A - Z)";
                break;
            case 4:
                list = bookDAO.getSortedByAuthor(false);
                title = "SẮP XẾP THEO TÁC GIẢ (Z - A)";
                break;
            case 5:
                list = bookDAO.sortByPriceAlgorithm(true);
                title = "SẮP XẾP THEO ĐƠN GIÁ TĂNG DẦN [SELECTION SORT ALGORITHM]";
                break;
            case 6:
                list = bookDAO.sortByPriceAlgorithm(false);
                title = "SẮP XẾP THEO ĐƠN GIÁ GIẢM DẦN [SELECTION SORT ALGORITHM]";
                break;
            case 7:
                list = bookDAO.getSortedByQuantity(false);
                title = "SẮP XẾP THEO SỐ LƯỢNG TỒN KHO GIẢM DẦN";
                break;
            case 8:
                list = bookDAO.getSortedByTotalValue(false);
                title = "SẮP XẾP THEO TỔNG GIÁ TRỊ LƯU KHO GIẢM DẦN";
                break;
            default:
                return;
        }

        printBookTable(list, title);
    }

    // ========================================================
    // Milestone 3: Mô phỏng tính phí mượn theo Đa hình (Polymorphism)
    // ========================================================

    public void simulateBorrowFeePolymorphism() {
        System.out.println("\n--- [ MÔ PHỎNG TÍNH PHÍ MƯỢN THEO ĐA HÌNH (MILESTONE 3) ] ---");
        System.out.println(">> Cơ chế Đa hình (Polymorphism & Upcasting):");
        System.out.println("   - TextBook (Giáo trình): calculateBorrowFee(days) = days * 2.000 VNĐ (trợ giá sinh viên)");
        System.out.println("   - ReferenceBook (Tham khảo): calculateBorrowFee(days) = days * 5.000 VNĐ (tài liệu chuyên sâu)");
        System.out.println();

        String bookId = DataInput.getNonEmptyString("Nhập Mã sách cần tính phí mượn thử nghiệm: ");
        Book book = bookDAO.findBookById(bookId);
        if (book == null) {
            System.out.println(">> Không tìm thấy sách nào có mã: " + bookId);
            return;
        }

        int days = DataInput.getIntegerNumber("Nhập số ngày dự kiến mượn (1 - 365 ngày): ", 1, 365);
        double fee = book.calculateBorrowFee(days);
        double polyPrice = book.calculatePrice();

        System.out.println();
        System.out.println("==========================================================================");
        System.out.println("                KẾT QUẢ TÍNH PHÍ MƯỢN THEO TÍNH ĐA HÌNH                   ");
        System.out.println("==========================================================================");
        System.out.printf("  * Tên sách               : %s (Mã: %s)%n", book.getTitle(), book.getBookId());
        System.out.printf("  * Tác giả                : %s%n", book.getAuthor());
        System.out.printf("  * Phân loại thực thể     : %s%n", book.getClass().getSimpleName());
        System.out.printf("  * Đơn giá bìa niêm yết   : %,.2f VNĐ%n", book.getPrice());
        System.out.printf("  * Đơn giá thực tế (đa hình): %,.2f VNĐ ", polyPrice);
        if (book instanceof TextBook) {
            System.out.println("(Giảm 10% hỗ trợ học tập)");
        } else {
            System.out.println("(Cộng 10% thuế VAT / bản quyền)");
        }
        System.out.printf("  * Số ngày mượn           : %d ngày%n", days);
        System.out.printf("  * Tổng phí mượn tạm tính : %,.2f VNĐ%n", fee);
        System.out.println("==========================================================================");
    }

    // ========================================================
    // Milestone 4: Quản lý lưu trữ file Books.txt
    // ========================================================

    public void fileManagementSubMenu() {
        System.out.println("\n--- [ QUẢN LÝ LƯU TRỮ FILE BOOKS.TXT (MILESTONE 4) ] ---");
        System.out.println("  1. Lưu toàn bộ danh sách sách hiện tại ra file Books.txt");
        System.out.println("  2. Tải/Đồng bộ lại danh sách sách từ file Books.txt");
        System.out.println("  0. Quay lại");

        int choice = DataInput.getIntegerNumber(">> Chọn thao tác [0-2]: ", 0, 2);
        switch (choice) {
            case 1:
                boolean saved = bookDAO.saveToFile();
                if (saved) {
                    System.out.println(">> THÀNH CÔNG: Đã lưu toàn bộ dữ liệu sách vào file Books.txt chuẩn UTF-8!");
                } else {
                    System.out.println(">> THẤT BÀI: Có lỗi xảy ra khi ghi file!");
                }
                break;
            case 2:
                boolean loaded = bookDAO.loadFromFile();
                if (loaded) {
                    System.out.println(">> THÀNH CÔNG: Đã nạp thành công dữ liệu sách từ file Books.txt!");
                    printAllBooks();
                } else {
                    System.out.println(">> THẤT BÀI: File Books.txt không tồn tại hoặc không có dữ liệu hợp lệ!");
                }
                break;
            default:
                break;
        }
    }

    public void displayInventoryReport() {
        List<Book> all = bookDAO.getBookList();
        int totalTitles = all.size();
        int textBookCount = 0;
        int refBookCount = 0;
        int textBookQty = 0;
        int refBookQty = 0;
        double textBookVal = 0.0;
        double refBookVal = 0.0;

        Book highestPriceBook = null;
        Book lowestPriceBook = null;
        Book highestQtyBook = null;
        List<Book> lowStockBooks = new ArrayList<>();

        for (Book b : all) {
            double val = b.calculateTotalValue();
            if (b instanceof TextBook) {
                textBookCount++;
                textBookQty += b.getQuantity();
                textBookVal += val;
            } else if (b instanceof ReferenceBook) {
                refBookCount++;
                refBookQty += b.getQuantity();
                refBookVal += val;
            }

            if (highestPriceBook == null || b.getPrice() > highestPriceBook.getPrice()) {
                highestPriceBook = b;
            }
            if (lowestPriceBook == null || b.getPrice() < lowestPriceBook.getPrice()) {
                lowestPriceBook = b;
            }
            if (highestQtyBook == null || b.getQuantity() > highestQtyBook.getQuantity()) {
                highestQtyBook = b;
            }
            if (b.getQuantity() < 5) {
                lowStockBooks.add(b);
            }
        }

        int totalQty = textBookQty + refBookQty;
        double totalVal = textBookVal + refBookVal;

        System.out.println();
        System.out.println("==========================================================================================");
        System.out.println("                    BÁO CÁO THỐNG KÊ TỔNG QUAN KHO SÁCH                                   ");
        System.out.println("==========================================================================================");
        System.out.printf("  * Tổng số đầu sách trong thư viện    : %,d đầu sách%n", totalTitles);
        System.out.printf("  * Tổng số lượng bản in tồn kho       : %,d cuốn%n", totalQty);
        System.out.printf("  * Tổng giá trị tài sản kho sách      : %,.2f VNĐ%n", totalVal);
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("  [ PHÂN BỐ THEO LOẠI SÁCH (ĐA HÌNH) ]");
        System.out.printf("    - Sách giáo trình (TextBook)       : %d đầu sách | %,d cuốn | Trị giá: %,.2f VNĐ%n",
                textBookCount, textBookQty, textBookVal);
        System.out.printf("    - Sách tham khảo (ReferenceBook)   : %d đầu sách | %,d cuốn | Trị giá: %,.2f VNĐ%n",
                refBookCount, refBookQty, refBookVal);
        System.out.println("------------------------------------------------------------------------------------------");
        if (highestPriceBook != null) {
            System.out.printf("  * Sách có đơn giá cao nhất           : [%s] %s (%,.2f VNĐ)%n",
                    highestPriceBook.getBookId(), highestPriceBook.getTitle(), highestPriceBook.getPrice());
        }
        if (lowestPriceBook != null) {
            System.out.printf("  * Sách có đơn giá thấp nhất          : [%s] %s (%,.2f VNĐ)%n",
                    lowestPriceBook.getBookId(), lowestPriceBook.getTitle(), lowestPriceBook.getPrice());
        }
        if (highestQtyBook != null) {
            System.out.printf("  * Sách có số lượng tồn kho nhiều nhất: [%s] %s (%d cuốn)%n",
                    highestQtyBook.getBookId(), highestQtyBook.getTitle(), highestQtyBook.getQuantity());
        }
        if (!lowStockBooks.isEmpty()) {
            System.out.printf("  * CẢNH BÁO TỒN KHO THẤP (< 5 cuốn)  : Có %d đầu sách cần nhập thêm!%n", lowStockBooks.size());
            for (Book lsb : lowStockBooks) {
                System.out.printf("      + [%s] %s (Hiện còn: %d cuốn)%n", lsb.getBookId(), lsb.getTitle(), lsb.getQuantity());
            }
        } else {
            System.out.println("  * Tình trạng tồn kho                 : Mọi đầu sách đều duy trì tồn kho an toàn (>= 5 cuốn).");
        }
        System.out.println("==========================================================================================");
    }

    // --- Helper methods căn chỉnh hiển thị bảng biểu ---

    private static String makeLine(char ch, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    private static String centerText(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text.substring(0, width);
        int totalPad = width - text.length();
        int leftPad = totalPad / 2;
        int rightPad = totalPad - leftPad;

        StringBuilder sb = new StringBuilder(width);
        for (int i = 0; i < leftPad; i++) sb.append(' ');
        sb.append(text);
        for (int i = 0; i < rightPad; i++) sb.append(' ');
        return sb.toString();
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    // ========================================================
    // Tính năng 3, 7, 8: Đánh giá Sao & Gợi ý Sách, Hàng đợi Đặt trước, Biểu đồ ASCII
    // ========================================================

    private void rateAndRecommendSubMenu() {
        System.out.println("==========================================================================================");
        System.out.println("              ĐÁNH GIÁ SAO (1-5 ⭐) & GỢI Ý SÁCH THÔNG MINH (FEATURE 3)                    ");
        System.out.println("==========================================================================================");
        System.out.println("  1. Chấm điểm Sao (1-5 ⭐) cho cuốn sách bạn đã đọc");
        System.out.println("  2. Gợi ý Top 3 cuốn sách liên quan có điểm đánh giá cao nhất");
        System.out.println("  3. Xem bảng xếp hạng Sách theo số Sao đánh giá");
        int subChoice = DataInput.getIntegerNumber(">> Chọn chức năng [1-3]: ");

        if (subChoice == 1) {
            String bookId = DataInput.getNonEmptyString("Nhập mã sách muốn đánh giá (VD: B001): ").toUpperCase();
            Book book = bookDAO.findBookById(bookId);
            if (book == null) {
                System.out.println(">> Không tìm thấy sách có mã: " + bookId);
                return;
            }
            System.out.println("Sách đang chọn: " + book.getTitle() + " | Điểm hiện tại: " + book.getStarDisplay());
            int stars = DataInput.getIntegerNumber("Nhập số sao đánh giá [1-5]: ", 1, 5);
            if (bookDAO.rateBook(bookId, stars)) {
                System.out.println(">> Cảm ơn bạn đã đánh giá! Điểm mới của sách '" + book.getTitle() + "' là: " + book.getStarDisplay());
            }
        } else if (subChoice == 2) {
            String bookId = DataInput.getNonEmptyString("Nhập mã cuốn sách bạn đang quan tâm (VD: B001): ").toUpperCase();
            Book target = bookDAO.findBookById(bookId);
            if (target == null) {
                System.out.println(">> Không tìm thấy sách có mã: " + bookId);
                return;
            }
            System.out.println("\n>> Dựa trên cuốn '" + target.getTitle() + "', hệ thống gợi ý Top 3 cuốn sách cùng nhóm có đánh giá cao nhất:");
            List<Book> recommended = bookDAO.getRecommendedBooks(bookId, 3);
            for (int i = 0; i < recommended.size(); i++) {
                Book rec = recommended.get(i);
                System.out.printf("   %d. [%s] %-35s | Tác giả: %-18s | Đánh giá: %s | Còn: %d cuốn\n",
                        i + 1, rec.getBookId(), rec.getTitle(), rec.getAuthor(), rec.getStarDisplay(), rec.getQuantity());
            }
        } else if (subChoice == 3) {
            List<Book> list = bookDAO.getBookList();
            Collections.sort(list, new Comparator<Book>() {
                @Override
                public int compare(Book b1, Book b2) {
                    if (b1.getRating() < b2.getRating()) {
                        return 1;
                    } else if (b1.getRating() > b2.getRating()) {
                        return -1;
                    } else {
                        return Integer.compare(b2.getRatingCount(), b1.getRatingCount());
                    }
                }
            });
            System.out.println("\n--- BẢNG XẾP HẠNG SÁCH ĐƯỢC YÊU THÍCH NHẤT ---");
            for (int i = 0; i < list.size(); i++) {
                Book b = list.get(i);
                System.out.printf("  Hạng %d: [%s] %-36s | ⭐ %s | Giá: %,.0f VNĐ\n",
                        i + 1, b.getBookId(), b.getTitle(), b.getStarDisplay(), b.getPrice());
            }
        }
    }

    private void reservationQueueSubMenu() {
        System.out.println("==========================================================================================");
        System.out.println("              HÀNG ĐỢI ĐẶT TRƯỚC SÁCH KHI HẾT HÀNG (RESERVATION QUEUE - FEATURE 7)       ");
        System.out.println("==========================================================================================");
        System.out.println("  1. Đăng ký Đặt trước sách cho Độc giả");
        System.out.println("  2. Xem danh sách các đầu sách đang có người chờ Đặt trước");
        int subChoice = DataInput.getIntegerNumber(">> Chọn chức năng [1-2]: ");

        if (subChoice == 1) {
            String bookId = DataInput.getNonEmptyString("Nhập mã sách muốn đặt trước (VD: B005): ").toUpperCase();
            Book book = bookDAO.findBookById(bookId);
            if (book == null) {
                System.out.println(">> Không tìm thấy sách có mã: " + bookId);
                return;
            }
            String memberId = DataInput.getNonEmptyString("Nhập mã độc giả đăng ký chờ (VD: M001): ").toUpperCase();
            if (bookDAO.reserveBook(bookId, memberId)) {
                System.out.println(">> Đã thêm độc giả " + memberId + " vào hàng đợi đặt trước của sách '" + book.getTitle() + "'!");
                System.out.println(">> Vị trí trong hàng đợi: #" + book.getReservationQueue().size()
                        + " (Khi có người trả sách, hệ thống sẽ tự động thông báo ưu tiên!)");
            } else {
                System.out.println(">> Độc giả " + memberId + " đã có trong danh sách chờ của cuốn sách này rồi!");
            }
        } else if (subChoice == 2) {
            List<Book> reservedBooks = bookDAO.getBooksWithReservations();
            if (reservedBooks.isEmpty()) {
                System.out.println(">> Hiện tại chưa có cuốn sách nào trong danh sách chờ đặt trước.");
                return;
            }
            System.out.println("\n--- DANH SÁCH SÁCH ĐANG CÓ ĐỘC GIẢ ĐẶT TRƯỚC ---");
            for (int i = 0; i < reservedBooks.size(); i++) {
                Book b = reservedBooks.get(i);
                System.out.printf("  %d. [%s] %-35s (Tồn kho: %d) -> Hàng đợi (%d người): %s\n",
                        i + 1, b.getBookId(), b.getTitle(), b.getQuantity(),
                        b.getReservationQueue().size(), b.getReservationQueue().toString());
            }
        }
    }

    private void displayBookInventoryChart() {
        List<Book> books = bookDAO.getBookList();
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            labels.add("[" + b.getBookId() + "] " + b.getTitle());
            values.add(b.getQuantity());
        }
        Menu.printAsciiBarChart("SỐ LƯỢNG TỒN KHO THEO TỪNG ĐẦU SÁCH", labels, values, "cuốn");
    }

    public static void main(String[] args) {
        IBookDAO bookDAO = new BookDAO();
        BookManagement management = new BookManagement(bookDAO);
        management.processMenuForBook();
    }
}
