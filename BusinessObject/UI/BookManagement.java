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
 * Giao diện điều khiển và quản trị phân hệ Sách (Book Management UI).
 * Hiển thị bảng biểu chi tiết, thẩm mỹ và hỗ trợ đầy đủ các thao tác CRUD.
 * Milestone 2 - PRO192 (Member 2: Book & Publication Module)
 */
public class BookManagement {

    private final IBookDAO bookDAO;

    public BookManagement() {
        this.bookDAO = new BookDAO();
    }

    public BookManagement(IBookDAO bookDAO) {
        this.bookDAO = (bookDAO != null) ? bookDAO : new BookDAO();
    }

    /**
     * Vòng lặp Menu chính cho phân hệ Quản lý Sách.
     */
    public void processMenuForBook() {
        boolean running = true;
        while (running) {
            printMenuHeader();
            int choice = DataInput.getIntegerNumber(">> Nhập lựa chọn của bạn [0-8]: ");
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
                    findBookById();
                    break;
                case 6:
                    searchBooksByTitle();
                    break;
                case 7:
                    sortBooksSubMenu();
                    break;
                case 8:
                    displayInventoryReport();
                    break;
                case 0:
                    System.out.println(">> Đang thoát khỏi phân hệ Quản lý Sách. Hẹn gặp lại!");
                    running = false;
                    break;
                default:
                    System.out.println(">> Lựa chọn không hợp lệ! Vui lòng chọn số từ 0 đến 8.");
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
        System.out.println("==========================================================================================");
        System.out.println("                HỆ THỐNG QUẢN LÝ KHO SÁCH & TÀI LIỆU (BOOK MANAGEMENT)                    ");
        System.out.println("==========================================================================================");
        System.out.println("  1. Xem danh sách toàn bộ sách (Bảng biểu chi tiết)                                      ");
        System.out.println("  2. Thêm sách mới vào kho (Sách giáo trình / Sách tham khảo)                             ");
        System.out.println("  3. Cập nhật thông tin sách theo Mã sách                                                 ");
        System.out.println("  4. Xóa sách khỏi kho lưu trữ                                                            ");
        System.out.println("  5. Tìm kiếm sách theo Mã sách (Book ID)                                                 ");
        System.out.println("  6. Tìm kiếm sách theo Tên sách (Title)                                                  ");
        System.out.println("  7. Sắp xếp danh sách sách (Đa tiêu chí: Tên, Giá, Số lượng, ...)                        ");
        System.out.println("  8. Báo cáo thống kê & Tổng quan giá trị kho sách                                        ");
        System.out.println("  0. Quay lại Menu chính / Thoát                                                          ");
        System.out.println("==========================================================================================");
    }

    /**
     * In danh sách sách dưới dạng bảng biểu chuyên nghiệp.
     */
    public void printBookTable(List<Book> books, String tableTitle) {
        String sep = "+----------+----------------------------------+-----------------+------------------------+----------+----------------+----------------+";
        int totalWidth = sep.length();

        System.out.println();
        System.out.println(makeLine('=', totalWidth));
        System.out.println(centerText(tableTitle.toUpperCase(), totalWidth));
        System.out.println(sep);
        System.out.printf("| %-8s | %-32s | %-15s | %-22s | %-8s | %-14s | %-14s |%n",
                "MÃ SÁCH", "TÊN SÁCH", "PHÂN LOẠI", "THỂ LOẠI / NXB", "SỐ LƯỢNG", "ĐƠN GIÁ (VNĐ)", "THÀNH TIỀN(VNĐ)");
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

            if (b instanceof TextBook) {
                typeStr = "Sách giáo trình";
                extraInfo = ((TextBook) b).getGenre();
            } else if (b instanceof ReferenceBook) {
                typeStr = "Sách tham khảo";
                extraInfo = ((ReferenceBook) b).getPublisher();
            } else {
                typeStr = "Sách thông thường";
                extraInfo = "-";
            }

            String displayTitle = truncate(b.getTitle(), 32);
            String displayExtra = truncate(extraInfo, 22);
            double itemTotal = b.calculateTotalValue();

            System.out.printf("| %-8s | %-32s | %-15s | %-22s | %8d | %,14.2f | %,14.2f |%n",
                    b.getBookId(),
                    displayTitle,
                    typeStr,
                    displayExtra,
                    b.getQuantity(),
                    b.getPrice(),
                    itemTotal);

            totalQty += b.getQuantity();
            totalVal += itemTotal;
        }

        System.out.println(sep);
        String summaryPart1 = String.format("Tổng: %d đầu sách", books.size());
        String summaryPart2 = String.format("Tổng số lượng: %,d cuốn", totalQty);
        String summaryPart3 = String.format("Tổng giá trị: %,.2f VNĐ", totalVal);

        System.out.printf("| %-38s | %-40s | %-43s |%n", summaryPart1, summaryPart2, summaryPart3);
        System.out.println(makeLine('=', totalWidth));
    }

    /**
     * 1. Hiển thị toàn bộ danh sách sách.
     */
    public void printAllBooks() {
        List<Book> list = bookDAO.getBookList();
        printBookTable(list, "DANH SÁCH TOÀN BỘ SÁCH TRONG KHO");
    }

    /**
     * 2. Thêm sách mới vào kho.
     */
    public void addNewBook() {
        System.out.println("\n--- [ THÊM SÁCH MỚI VÀO KHO ] ---");
        System.out.println("Chọn loại sách cần thêm:");
        System.out.println("  1. Sách giáo trình (TextBook)");
        System.out.println("  2. Sách tham khảo (ReferenceBook)");
        int typeChoice = DataInput.getIntegerNumber(">> Lựa chọn [1 hoặc 2]: ", 1, 2);

        String bookId;
        while (true) {
            bookId = DataInput.getNonEmptyString("Nhập Mã sách (VD: B006, TB001): ").toUpperCase();
            if (bookDAO.findBookById(bookId) != null) {
                System.out.println(">> Lỗi: Mã sách '" + bookId + "' đã tồn tại! Vui lòng nhập mã khác.");
            } else {
                break;
            }
        }

        String title = DataInput.getNonEmptyString("Nhập Tên sách: ");
        double price = DataInput.getPositiveDouble("Nhập Đơn giá (VNĐ): ");
        int quantity = DataInput.getIntegerNumber("Nhập Số lượng tồn kho (>= 0): ", 0, 100000);

        Book newBook;
        if (typeChoice == 1) {
            String genre = DataInput.getNonEmptyString("Nhập Thể loại / Chuyên ngành giáo trình: ");
            newBook = new TextBook(bookId, title, price, quantity, genre);
        } else {
            String publisher = DataInput.getNonEmptyString("Nhập Tên Nhà xuất bản (NXB): ");
            newBook = new ReferenceBook(bookId, title, price, quantity, publisher);
        }

        boolean success = bookDAO.addBook(newBook);
        if (success) {
            System.out.println("\n>> THÀNH CÔNG: Đã thêm sách mới vào kho lưu trữ!");
            List<Book> single = new ArrayList<>();
            single.add(newBook);
            printBookTable(single, "THÔNG TIN SÁCH VỪA THÊM");
        } else {
            System.out.println("\n>> THẤT BÀI: Không thể thêm sách vào kho!");
        }
    }

    /**
     * 3. Cập nhật thông tin sách theo mã sách.
     */
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

        // Cập nhật tên
        String newTitle = DataInput.getString(String.format("Tên mới [Hiện tại: '%s']: ", current.getTitle()));
        if (!newTitle.trim().isEmpty()) {
            current.setTitle(newTitle.trim());
        }

        // Cập nhật giá
        String priceStr = DataInput.getString(String.format("Đơn giá mới [Hiện tại: %,.2f VNĐ]: ", current.getPrice()));
        if (!priceStr.trim().isEmpty()) {
            try {
                double newPrice = Double.parseDouble(priceStr.trim());
                if (newPrice > 0) {
                    current.setPrice(newPrice);
                } else {
                    System.out.println(">> Đơn giá không hợp lệ, giữ nguyên giá trị cũ.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">> Định dạng số không hợp lệ, giữ nguyên giá cũ.");
            }
        }

        // Cập nhật số lượng
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

        // Cập nhật trường riêng
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
            System.out.println("\n>> THÀNH CÔNG: Đã cập nhật thông tin sách!");
            printBookTable(preview, "THÔNG TIN SÁCH SAU KHI CẬP NHẬT");
        } else {
            System.out.println("\n>> THẤT BÀI: Cập nhật thông tin sách không thành công!");
        }
    }

    /**
     * 4. Xóa sách khỏi kho lưu trữ.
     */
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
                System.out.println(">> THÀNH CÔNG: Đã xóa cuốn sách có mã '" + bookId + "' khỏi hệ thống!");
            } else {
                System.out.println(">> THẤT BÀI: Không thể xóa cuốn sách!");
            }
        } else {
            System.out.println(">> Đã hủy thao tác xóa sách.");
        }
    }

    /**
     * 5. Tìm kiếm sách theo Mã sách (Book ID).
     */
    public void findBookById() {
        System.out.println("\n--- [ TÌM KIẾM SÁCH THEO MÃ ] ---");
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

    /**
     * 6. Tìm kiếm sách theo Tên sách (Title).
     */
    public void searchBooksByTitle() {
        System.out.println("\n--- [ TÌM KIẾM SÁCH THEO TÊN ] ---");
        String keyword = DataInput.getNonEmptyString("Nhập từ khóa tên sách cần tìm: ");
        List<Book> results = bookDAO.findBookByTitle(keyword);

        if (!results.isEmpty()) {
            printBookTable(results, "KẾT QUẢ TÌM KIẾM THEO TÊN: '" + keyword + "' (" + results.size() + " kết quả)");
        } else {
            System.out.println(">> Không tìm thấy cuốn sách nào chứa từ khóa: '" + keyword + "'");
        }
    }

    /**
     * 7. Submenu sắp xếp danh sách sách.
     */
    public void sortBooksSubMenu() {
        System.out.println("\n--- [ SẮP XẾP DANH SÁCH SÁCH ] ---");
        System.out.println("  1. Sắp xếp theo Tên sách (A -> Z)");
        System.out.println("  2. Sắp xếp theo Tên sách (Z -> A)");
        System.out.println("  3. Sắp xếp theo Đơn giá tăng dần");
        System.out.println("  4. Sắp xếp theo Đơn giá giảm dần");
        System.out.println("  5. Sắp xếp theo Số lượng tồn kho giảm dần");
        System.out.println("  6. Sắp xếp theo Tổng thành tiền giảm dần");
        System.out.println("  0. Quay lại");

        int sortChoice = DataInput.getIntegerNumber(">> Chọn tiêu chí sắp xếp [0-6]: ", 0, 6);
        List<Book> list = bookDAO.getBookList();
        String title;

        switch (sortChoice) {
            case 1:
                list.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
                title = "DANH SÁCH SÁCH: SẮP XẾP THEO TÊN (A - Z)";
                break;
            case 2:
                list.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());
                title = "DANH SÁCH SÁCH: SẮP XẾP THEO TÊN (Z - A)";
                break;
            case 3:
                list.sort(Comparator.comparingDouble(Book::getPrice));
                title = "DANH SÁCH SÁCH: SẮP XẾP THEO ĐƠN GIÁ TĂNG DẦN";
                break;
            case 4:
                list.sort(Comparator.comparingDouble(Book::getPrice).reversed());
                title = "DANH SÁCH SÁCH: SẮP XẾP THEO ĐƠN GIÁ GIẢM DẦN";
                break;
            case 5:
                list.sort(Comparator.comparingInt(Book::getQuantity).reversed());
                title = "DANH SÁCH SÁCH: SẮP XẾP THEO SỐ LƯỢNG TỒN KHO GIẢM DẦN";
                break;
            case 6:
                list.sort(Comparator.comparingDouble(Book::calculateTotalValue).reversed());
                title = "DANH SÁCH SÁCH: SẮP XẾP THEO TỔNG THÀNH TIỀN GIẢM DẦN";
                break;
            default:
                return;
        }

        printBookTable(list, title);
    }

    /**
     * 8. Báo cáo thống kê tổng quan kho sách.
     */
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
        System.out.println("  [ PHÂN BỐ THEO LOẠI SÁCH ]");
        System.out.printf("    - Sách giáo trình (TextBook)       : %d đầu sách | %,d cuốn | Trị giá: %,.2f VNĐ%n",
                textBookCount, textBookQty, textBookVal);
        System.out.printf("    - Sách tham khảo (ReferenceBook)   : %d đầu sách | %,d cuốn | Trị giá: %,.2f VNĐ%n",
                refBookCount, refBookQty, refBookVal);
        System.out.println("------------------------------------------------------------------------------------------");
        if (highestPriceBook != null) {
            System.out.printf("  * Sách có đơn giá cao nhất           : [%s] %s (%,.2f VNĐ)%n",
                    highestPriceBook.getBookId(), highestPriceBook.getTitle(), highestPriceBook.getPrice());
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

    /**
     * Phương thức main cho phép chạy trực tiếp và kiểm thử độc lập module BookManagement.
     */
    public static void main(String[] args) {
        IBookDAO bookDAO = new BookDAO();
        BookManagement management = new BookManagement(bookDAO);
        management.processMenuForBook();
    }
}
