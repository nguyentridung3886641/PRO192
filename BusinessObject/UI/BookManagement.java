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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            int choice = DataInput.getIntegerNumber(">> Enter your choice [0-12]: ");
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
                    System.out.println(">> Saving data and exiting Book Management subsystem. See you again!");
                    bookDAO.saveToFile();
                    running = false;
                    break;
                default:
                    System.out.println(">> Invalid choice! Please select a number from 0 to 12.");
                    break;
            }
            if (running) {
                System.out.println("\nPress Enter to continue...");
                DataInput.getString("");
            }
        }
    }

    private void printMenuHeader() {
        System.out.println("\n" + "=".repeat(104));
        System.out.println("                      BOOK & DOCUMENT INVENTORY MANAGEMENT SYSTEM                                       ");
        System.out.println("                      PRO192 - Member 2: Nguyen Thien Tue (Milestones 3 & 4)                            ");
        System.out.println("=".repeat(104));
        Menu.print(
            "  1. View all books (Detailed table, Polymorphism & Upcasting)|" +
            "  2. Add new book to inventory (TextBook / ReferenceBook, supports Author)|" +
            "  3. Update book details by Book ID|" +
            "  4. Delete book from inventory|" +
            "  5. Multi-criteria search (By ID, Title, Author, Price Range, Combined)|" +
            "  6. Sort book list (Multi-criteria: Title, Author, Price, Qty, Selection/Insertion Sort)|" +
            "  7. Polymorphic borrow fee simulator (Demonstrates dynamic method dispatch)|" +
            "  8. Manage Books.txt file storage (Save to file / Reload from file)|" +
            "  9. Inventory statistical report & Total asset valuation|" +
            " 10. Star rating (1-5 *) & Smart book recommendations (Feature 3)|" +
            " 11. Book reservation queue when out of stock (Feature 7)|" +
            " 12. Book inventory visual bar chart (Console ASCII Bar Chart - Feature 8)|" +
            "  0. Return to Main Menu / Exit"
        );
        System.out.println("=".repeat(104));
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
                "BOOK ID", "BOOK TITLE", "AUTHOR", "CATEGORY", "GENRE / PUBLISHER", "QTY", "PRICE (VND)", "POLY PRICE", "FEE/DAY (VND)", "TOTAL VALUE(VND)");
        System.out.println(sep);

        if (books == null || books.isEmpty()) {
            System.out.printf("|%s|%n", centerText("NO BOOK DATA TO DISPLAY", totalWidth - 2));
            System.out.println(sep);
            return;
        }

        // [LAMBDA & STREAM API]: Tính toán tổng số lượng và tổng trị giá bằng Stream API IntStream/DoubleStream
        int totalQty = books.stream().mapToInt(Book::getQuantity).sum();
        double totalVal = books.stream().mapToDouble(Book::calculateTotalValue).sum();

        // [LAMBDA & STREAM API]: Hiển thị chi tiết từng dòng sách bằng forEach Lambda
        books.forEach(b -> {
            String typeStr;
            String extraInfo;

            // Áp dụng Upcasting & Dynamic Method Dispatch
            if (b instanceof TextBook) {
                typeStr = "TextBook";
                extraInfo = ((TextBook) b).getGenre();
            } else if (b instanceof ReferenceBook) {
                typeStr = "Reference";
                extraInfo = ((ReferenceBook) b).getPublisher();
            } else {
                typeStr = "General";
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
        });

        System.out.println(sep);
        String summaryPart1 = String.format("Total: %d book titles", books.size());
        String summaryPart2 = String.format("Total inventory copies: %,d copies", totalQty);
        String summaryPart3 = String.format("Total inventory value: %,.2f VND", totalVal);

        System.out.printf("| %-45s | %-55s | %-62s |%n", summaryPart1, summaryPart2, summaryPart3);
        System.out.println(makeLine('=', totalWidth));
    }

    public void printAllBooks() {
        List<Book> list = bookDAO.getBookList();
        printBookTable(list, "ALL BOOKS IN LIBRARY INVENTORY");
    }

    public void addNewBook() {
        System.out.println("\n--- [ ADD NEW BOOK TO INVENTORY ] ---");
        System.out.println("Select book type to add:");
        System.out.println("  1. TextBook - 10% student subsidy discount, borrow fee 2,000 VND/day");
        System.out.println("  2. ReferenceBook - 10% VAT/copyright tax, borrow fee 5,000 VND/day");
        int typeChoice = DataInput.getIntegerNumber(">> Select option [1 or 2]: ", 1, 2);

        String bookId;
        while (true) {
            bookId = DataInput.getNonEmptyString("Enter Book ID (e.g. B009, TB001): ").toUpperCase();
            if (bookDAO.findBookById(bookId) != null) {
                System.out.println(">> Error: Book ID '" + bookId + "' already exists! Please enter another ID.");
            } else {
                break;
            }
        }

        String title = DataInput.getNonEmptyString("Enter Book Title: ");
        String author = DataInput.getNonEmptyString("Enter Author: ");
        double price = DataInput.getPositiveDouble("Enter Listed Cover Price (VND): ");
        int quantity = DataInput.getIntegerNumber("Enter Inventory Quantity (>= 0): ", 0, 100000);

        Book newBook;
        if (typeChoice == 1) {
            String genre = DataInput.getNonEmptyString("Enter Genre / Textbook Discipline: ");
            newBook = new TextBook(bookId, title, author, price, quantity, genre);
        } else {
            String publisher = DataInput.getNonEmptyString("Enter Publisher Name: ");
            newBook = new ReferenceBook(bookId, title, author, price, quantity, publisher);
        }

        boolean success = bookDAO.addBook(newBook);
        if (success) {
            bookDAO.saveToFile();
            System.out.println("\n>> SUCCESS: Added new book and synchronized to Books.txt!");
            List<Book> single = new ArrayList<>();
            single.add(newBook);
            printBookTable(single, "NEWLY ADDED BOOK DETAILS");
        } else {
            System.out.println("\n>> FAILED: Unable to add book to inventory!");
        }
    }

    public void updateBook() {
        System.out.println("\n--- [ UPDATE BOOK INFORMATION ] ---");
        String bookId = DataInput.getNonEmptyString("Enter Book ID to update: ");
        Book current = bookDAO.findBookById(bookId);

        if (current == null) {
            System.out.println(">> No book found with ID: " + bookId);
            return;
        }

        List<Book> preview = new ArrayList<>();
        preview.add(current);
        printBookTable(preview, "CURRENT BOOK INFORMATION");

        System.out.println(">> Guide: Enter new value or press Enter to keep current value.");

        String newTitle = DataInput.getString(String.format("New Title [Current: '%s']: ", current.getTitle()));
        if (!newTitle.trim().isEmpty()) {
            current.setTitle(newTitle.trim());
        }

        String newAuthor = DataInput.getString(String.format("New Author [Current: '%s']: ", current.getAuthor()));
        if (!newAuthor.trim().isEmpty()) {
            current.setAuthor(newAuthor.trim());
        }

        String priceStr = DataInput.getString(String.format("New Cover Price [Current: %,.2f VND]: ", current.getPrice()));
        if (!priceStr.trim().isEmpty()) {
            try {
                double newPrice = Double.parseDouble(priceStr.trim());
                if (newPrice > 0) {
                    current.setPrice(newPrice);
                } else {
                    System.out.println(">> Invalid price, keeping current price.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">> Invalid number format, keeping current price.");
            }
        }

        String qtyStr = DataInput.getString(String.format("New Quantity [Current: %d]: ", current.getQuantity()));
        if (!qtyStr.trim().isEmpty()) {
            try {
                int newQty = Integer.parseInt(qtyStr.trim());
                if (newQty >= 0) {
                    current.setQuantity(newQty);
                } else {
                    System.out.println(">> Quantity cannot be negative, keeping current quantity.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">> Invalid number format, keeping current quantity.");
            }
        }

        if (current instanceof TextBook) {
            TextBook tb = (TextBook) current;
            String newGenre = DataInput.getString(String.format("New Genre [Current: '%s']: ", tb.getGenre()));
            if (!newGenre.trim().isEmpty()) {
                tb.setGenre(newGenre.trim());
            }
        } else if (current instanceof ReferenceBook) {
            ReferenceBook rb = (ReferenceBook) current;
            String newPub = DataInput.getString(String.format("New Publisher [Current: '%s']: ", rb.getPublisher()));
            if (!newPub.trim().isEmpty()) {
                rb.setPublisher(newPub.trim());
            }
        }

        boolean ok = bookDAO.updateBook(current);
        if (ok) {
            bookDAO.saveToFile();
            System.out.println("\n>> SUCCESS: Book information updated and synchronized to Books.txt!");
            printBookTable(preview, "BOOK INFORMATION AFTER UPDATE");
        } else {
            System.out.println("\n>> FAILED: Updating book information failed!");
        }
    }

    public void deleteBook() {
        System.out.println("\n--- [ REMOVE BOOK FROM INVENTORY ] ---");
        String bookId = DataInput.getNonEmptyString("Enter Book ID to remove: ");
        Book book = bookDAO.findBookById(bookId);

        if (book == null) {
            System.out.println(">> No book found with ID: " + bookId);
            return;
        }

        List<Book> preview = new ArrayList<>();
        preview.add(book);
        printBookTable(preview, "BOOK SELECTED FOR REMOVAL");

        boolean confirm = DataInput.getBoolean(">> Are you sure you want to remove this book from inventory? ");
        if (confirm) {
            boolean ok = bookDAO.deleteBook(bookId);
            if (ok) {
                bookDAO.saveToFile();
                System.out.println(">> SUCCESS: Removed book with ID '" + bookId + "' and synchronized to Books.txt!");
            } else {
                System.out.println(">> FAILED: Unable to remove book!");
            }
        } else {
            System.out.println(">> Book removal cancelled.");
        }
    }

    // ========================================================
    // Milestone 4: Multi-criteria search menu
    // ========================================================

    public void searchBooksSubMenu() {
        System.out.println("\n--- [ MULTI-CRITERIA BOOK SEARCH (MILESTONE 4) ] ---");
        System.out.println("  1. Search by Book ID");
        System.out.println("  2. Search by Book Title");
        System.out.println("  3. Search by Author");
        System.out.println("  4. Search by Listed Price Range");
        System.out.println("  5. Combined Multi-Criteria Search (Title + Author + Price + Classification)");
        System.out.println("  0. Back");

        int choice = DataInput.getIntegerNumber(">> Select search criteria [0-5]: ", 0, 5);
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
        String bookId = DataInput.getNonEmptyString("Enter Book ID to search: ");
        Book book = bookDAO.findBookById(bookId);
        if (book != null) {
            List<Book> list = new ArrayList<>();
            list.add(book);
            printBookTable(list, "SEARCH RESULTS FOR ID: " + bookId.toUpperCase());
        } else {
            System.out.println(">> No book found with ID: " + bookId);
        }
    }

    public void searchBooksByTitle() {
        String keyword = DataInput.getNonEmptyString("Enter Book Title keyword: ");
        List<Book> results = bookDAO.findBookByTitle(keyword);
        if (!results.isEmpty()) {
            printBookTable(results, "SEARCH RESULTS BY TITLE: '" + keyword + "' (" + results.size() + " results)");
        } else {
            System.out.println(">> No books found containing title keyword: '" + keyword + "'");
        }
    }

    public void searchBooksByAuthor() {
        String author = DataInput.getNonEmptyString("Enter Author keyword: ");
        List<Book> results = bookDAO.findBookByAuthor(author);
        if (!results.isEmpty()) {
            printBookTable(results, "SEARCH RESULTS BY AUTHOR: '" + author + "' (" + results.size() + " results)");
        } else {
            System.out.println(">> No books found for author: '" + author + "'");
        }
    }

    public void searchBooksByPriceRange() {
        double minPrice = DataInput.getDoubleNumber("Enter Minimum Price (VND): ");
        double maxPrice = DataInput.getDoubleNumber("Enter Maximum Price (VND): ");
        List<Book> results = bookDAO.findBookByPriceRange(minPrice, maxPrice);
        if (!results.isEmpty()) {
            String title = String.format("SEARCH RESULTS IN PRICE RANGE [%,.0f - %,.0f VND] (%d results)", 
                    Math.min(minPrice, maxPrice), Math.max(minPrice, maxPrice), results.size());
            printBookTable(results, title);
        } else {
            System.out.printf(">> No books found within price range [%,.0f - %,.0f VND]%n", minPrice, maxPrice);
        }
    }

    public void searchBooksCombinedMultiCriteria() {
        System.out.println("\n--- [ COMBINED MULTI-CRITERIA SEARCH ] ---");
        System.out.println(">> Note: Press Enter to skip any criteria.");

        String title = DataInput.getString("1. Book Title keyword: ");
        String author = DataInput.getString("2. Author keyword: ");

        String minStr = DataInput.getString("3. Minimum Price (VND): ");
        Double minPrice = null;
        if (!minStr.trim().isEmpty()) {
            try { minPrice = Double.parseDouble(minStr.trim()); } catch (NumberFormatException ignored) {}
        }

        String maxStr = DataInput.getString("4. Maximum Price (VND): ");
        Double maxPrice = null;
        if (!maxStr.trim().isEmpty()) {
            try { maxPrice = Double.parseDouble(maxStr.trim()); } catch (NumberFormatException ignored) {}
        }

        System.out.println("5. Book Classification: (1) TextBook, (2) ReferenceBook, (Enter for all)");
        String typeInput = DataInput.getString(">> Select classification: ");
        String bookType = "";
        if ("1".equals(typeInput.trim())) {
            bookType = "TEXT";
        } else if ("2".equals(typeInput.trim())) {
            bookType = "REF";
        }

        List<Book> results = bookDAO.searchMultiCriteria(title, author, minPrice, maxPrice, bookType);
        if (!results.isEmpty()) {
            printBookTable(results, "COMBINED MULTI-CRITERIA SEARCH RESULTS (" + results.size() + " results)");
        } else {
            System.out.println(">> No books found matching all specified criteria.");
        }
    }

    // ========================================================
    // Milestone 4: Sorting algorithms menu
    // ========================================================

    public void sortBooksSubMenu() {
        System.out.println("\n--- [ SORT BOOK LIST (MILESTONE 4) ] ---");
        System.out.println("  1. Sort by Title (A -> Z) [Insertion Sort Algorithm]");
        System.out.println("  2. Sort by Title (Z -> A)");
        System.out.println("  3. Sort by Author (A -> Z)");
        System.out.println("  4. Sort by Author (Z -> A)");
        System.out.println("  5. Sort by Listed Price Ascending [Selection Sort Algorithm]");
        System.out.println("  6. Sort by Listed Price Descending [Selection Sort Algorithm]");
        System.out.println("  7. Sort by Inventory Quantity Descending");
        System.out.println("  8. Sort by Total Inventory Value Descending");
        System.out.println("  0. Back");

        int sortChoice = DataInput.getIntegerNumber(">> Select sorting option [0-8]: ", 0, 8);
        List<Book> list;
        String title;

        switch (sortChoice) {
            case 1:
                list = bookDAO.sortByTitleAlgorithm(true);
                title = "SORT BY TITLE (A - Z) [INSERTION SORT ALGORITHM]";
                break;
            case 2:
                list = bookDAO.sortByTitleAlgorithm(false);
                title = "SORT BY TITLE (Z - A)";
                break;
            case 3:
                list = bookDAO.getSortedByAuthor(true);
                title = "SORT BY AUTHOR (A - Z)";
                break;
            case 4:
                list = bookDAO.getSortedByAuthor(false);
                title = "SORT BY AUTHOR (Z - A)";
                break;
            case 5:
                list = bookDAO.sortByPriceAlgorithm(true);
                title = "SORT BY LISTED PRICE ASCENDING [SELECTION SORT ALGORITHM]";
                break;
            case 6:
                list = bookDAO.sortByPriceAlgorithm(false);
                title = "SORT BY LISTED PRICE DESCENDING [SELECTION SORT ALGORITHM]";
                break;
            case 7:
                list = bookDAO.getSortedByQuantity(false);
                title = "SORT BY INVENTORY QUANTITY DESCENDING";
                break;
            case 8:
                list = bookDAO.getSortedByTotalValue(false);
                title = "SORT BY TOTAL INVENTORY VALUE DESCENDING";
                break;
            default:
                return;
        }

        printBookTable(list, title);
    }

    // ========================================================
    // ========================================================
    // Milestone 3: Simulate borrow fee calculation via Polymorphism
    // ========================================================

    public void simulateBorrowFeePolymorphism() {
        System.out.println("\n--- [ SIMULATE BORROW FEE VIA POLYMORPHISM (MILESTONE 3) ] ---");
        System.out.println(">> Polymorphism & Upcasting Mechanism:");
        System.out.println("   - TextBook: calculateBorrowFee(days) = days * 2,000 VND (student subsidy)");
        System.out.println("   - ReferenceBook: calculateBorrowFee(days) = days * 5,000 VND (specialized material)");
        System.out.println();

        String bookId = DataInput.getNonEmptyString("Enter Book ID to simulate borrow fee: ");
        Book book = bookDAO.findBookById(bookId);
        if (book == null) {
            System.out.println(">> No book found with ID: " + bookId);
            return;
        }

        int days = DataInput.getIntegerNumber("Enter projected borrow days (1 - 365 days): ", 1, 365);
        double fee = book.calculateBorrowFee(days);
        double polyPrice = book.calculatePrice();

        System.out.println();
        System.out.println("==========================================================================");
        System.out.println("             POLYMORPHIC BORROW FEE CALCULATION RESULT                    ");
        System.out.println("==========================================================================");
        System.out.printf("  * Book Title             : %s (ID: %s)%n", book.getTitle(), book.getBookId());
        System.out.printf("  * Author                 : %s%n", book.getAuthor());
        System.out.printf("  * Entity Classification  : %s%n", book.getClass().getSimpleName());
        System.out.printf("  * Listed Cover Price     : %,.2f VND%n", book.getPrice());
        System.out.printf("  * Effective Price (poly) : %,.2f VND ", polyPrice);
        if (book instanceof TextBook) {
            System.out.println("(10% student subsidy discount applied)");
        } else {
            System.out.println("(10% VAT / copyright tax added)");
        }
        System.out.printf("  * Borrow Duration        : %d days%n", days);
        System.out.printf("  * Estimated Borrow Fee   : %,.2f VND%n", fee);
        System.out.println("==========================================================================");
    }

    // ========================================================
    // Milestone 4: Books.txt file storage management
    // ========================================================

    public void fileManagementSubMenu() {
        System.out.println("\n--- [ BOOKS.TXT FILE STORAGE MANAGEMENT (MILESTONE 4) ] ---");
        System.out.println("  1. Save current book list to Books.txt");
        System.out.println("  2. Reload/Sync book list from Books.txt");
        System.out.println("  0. Back");

        int choice = DataInput.getIntegerNumber(">> Select operation [0-2]: ", 0, 2);
        switch (choice) {
            case 1:
                boolean saved = bookDAO.saveToFile();
                if (saved) {
                    System.out.println(">> SUCCESS: Saved all book data to Books.txt in UTF-8 format!");
                } else {
                    System.out.println(">> FAILED: An error occurred while writing to file!");
                }
                break;
            case 2:
                boolean loaded = bookDAO.loadFromFile();
                if (loaded) {
                    System.out.println(">> SUCCESS: Successfully loaded book data from Books.txt!");
                    printAllBooks();
                } else {
                    System.out.println(">> FAILED: Books.txt does not exist or contains invalid data!");
                }
                break;
            default:
                break;
        }
    }

    public void displayInventoryReport() {
        List<Book> all = bookDAO.getBookList();
        int totalTitles = all.size();

        // [LAMBDA & STREAM API]: Thống kê phân loại sách bằng Stream filter, count, mapToInt, mapToDouble
        long textBookCount = all.stream().filter(b -> b instanceof TextBook).count();
        long refBookCount = all.stream().filter(b -> b instanceof ReferenceBook).count();
        int textBookQty = all.stream().filter(b -> b instanceof TextBook).mapToInt(Book::getQuantity).sum();
        int refBookQty = all.stream().filter(b -> b instanceof ReferenceBook).mapToInt(Book::getQuantity).sum();
        double textBookVal = all.stream().filter(b -> b instanceof TextBook).mapToDouble(Book::calculateTotalValue).sum();
        double refBookVal = all.stream().filter(b -> b instanceof ReferenceBook).mapToDouble(Book::calculateTotalValue).sum();

        // [LAMBDA & STREAM API]: Tìm cuốn sách max / min theo đơn giá và tồn kho bằng Comparator Lambda
        Book highestPriceBook = all.stream().max(Comparator.comparingDouble(Book::getPrice)).orElse(null);
        Book lowestPriceBook = all.stream().min(Comparator.comparingDouble(Book::getPrice)).orElse(null);
        Book highestQtyBook = all.stream().max(Comparator.comparingInt(Book::getQuantity)).orElse(null);

        // [LAMBDA & STREAM API]: Lọc các sách có cảnh báo tồn kho thấp (< 5 cuốn)
        List<Book> lowStockBooks = all.stream().filter(b -> b.getQuantity() < 5).collect(Collectors.toList());

        int totalQty = textBookQty + refBookQty;
        double totalVal = textBookVal + refBookVal;

        System.out.println();
        System.out.println("==========================================================================================");
        System.out.println("                         OVERVIEW BOOK INVENTORY REPORT                                   ");
        System.out.println("==========================================================================================");
        System.out.printf("  * Total book titles in library       : %,d titles%n", totalTitles);
        System.out.printf("  * Total inventory copies             : %,d copies%n", totalQty);
        System.out.printf("  * Total inventory asset value        : %,.2f VND%n", totalVal);
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("  [ BREAKDOWN BY BOOK TYPE (POLYMORPHISM) ]");
        System.out.printf("    - TextBook                         : %d titles | %,d copies | Value: %,.2f VND%n",
                textBookCount, textBookQty, textBookVal);
        System.out.printf("    - ReferenceBook                    : %d titles | %,d copies | Value: %,.2f VND%n",
                refBookCount, refBookQty, refBookVal);
        System.out.println("------------------------------------------------------------------------------------------");
        if (highestPriceBook != null) {
            System.out.printf("  * Highest priced book                : [%s] %s (%,.2f VND)%n",
                    highestPriceBook.getBookId(), highestPriceBook.getTitle(), highestPriceBook.getPrice());
        }
        if (lowestPriceBook != null) {
            System.out.printf("  * Lowest priced book                 : [%s] %s (%,.2f VND)%n",
                    lowestPriceBook.getBookId(), lowestPriceBook.getTitle(), lowestPriceBook.getPrice());
        }
        if (highestQtyBook != null) {
            System.out.printf("  * Highest inventory stock book       : [%s] %s (%d copies)%n",
                    highestQtyBook.getBookId(), highestQtyBook.getTitle(), highestQtyBook.getQuantity());
        }
        if (!lowStockBooks.isEmpty()) {
            System.out.printf("  * LOW STOCK WARNING (< 5 copies)     : %d book titles require restocking!%n", lowStockBooks.size());
            lowStockBooks.forEach(lsb ->
                System.out.printf("      + [%s] %s (Current stock: %d copies)%n", lsb.getBookId(), lsb.getTitle(), lsb.getQuantity())
            );
        } else {
            System.out.println("  * Inventory Status                   : All book titles maintain safe stock levels (>= 5 copies).");
        }
        System.out.println("==========================================================================================");
    }

    // --- Helper methods căn chỉnh hiển thị bảng biểu ---

    private static String makeLine(char ch, int length) {
        return String.valueOf(ch).repeat(Math.max(0, length));
    }

    private static String centerText(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text.substring(0, width);
        int totalPad = width - text.length();
        int leftPad = totalPad / 2;
        int rightPad = totalPad - leftPad;
        return " ".repeat(leftPad) + text + " ".repeat(rightPad);
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    // ========================================================
    // Features 3, 7, 8: Star Rating & Book Recommendations, Reservation Queue, ASCII Chart
    // ========================================================

    private void rateAndRecommendSubMenu() {
        System.out.println("==========================================================================================");
        System.out.println("            STAR RATING (1-5 *) & SMART BOOK RECOMMENDATIONS (FEATURE 3)                  ");
        System.out.println("==========================================================================================");
        System.out.println("  1. Submit Star Rating (1-5 *) for a book you have read");
        System.out.println("  2. Recommend Top 3 highest-rated related books");
        System.out.println("  3. View Book Leaderboard ranked by Star Rating");
        int subChoice = DataInput.getIntegerNumber(">> Select feature [1-3]: ");

        if (subChoice == 1) {
            String bookId = DataInput.getNonEmptyString("Enter Book ID to rate (e.g. B001): ").toUpperCase();
            Book book = bookDAO.findBookById(bookId);
            if (book == null) {
                System.out.println(">> No book found with ID: " + bookId);
                return;
            }
            System.out.println("Selected book: " + book.getTitle() + " | Current Rating: " + book.getStarDisplay());
            int stars = DataInput.getIntegerNumber("Enter rating stars [1-5]: ", 1, 5);
            if (bookDAO.rateBook(bookId, stars)) {
                System.out.println(">> Thank you for rating! New rating for '" + book.getTitle() + "': " + book.getStarDisplay());
            }
        } else if (subChoice == 2) {
            String bookId = DataInput.getNonEmptyString("Enter Book ID of interest (e.g. B001): ").toUpperCase();
            Book target = bookDAO.findBookById(bookId);
            if (target == null) {
                System.out.println(">> No book found with ID: " + bookId);
                return;
            }
            System.out.println("\n>> Based on '" + target.getTitle() + "', recommended Top 3 highest-rated books in the same group:");
            List<Book> recommended = bookDAO.getRecommendedBooks(bookId, 3);
            // [LAMBDA & STREAM API]: Duyệt danh sách gợi ý bằng IntStream
            IntStream.range(0, recommended.size()).forEach(i -> {
                Book rec = recommended.get(i);
                System.out.printf("   %d. [%s] %-35s | Author: %-18s | Rating: %s | In Stock: %d copies\n",
                        i + 1, rec.getBookId(), rec.getTitle(), rec.getAuthor(), rec.getStarDisplay(), rec.getQuantity());
            });
        } else if (subChoice == 3) {
            List<Book> list = bookDAO.getBookList();
            // [LAMBDA & COMPARATOR]: Sắp xếp bảng xếp hạng theo số Sao giảm dần, sau đó theo số lượt đánh giá
            list.sort(Comparator.comparingDouble(Book::getRating).reversed()
                    .thenComparing(Comparator.comparingInt(Book::getRatingCount).reversed()));

            System.out.println("\n--- MOST POPULAR BOOKS LEADERBOARD ---");
            IntStream.range(0, list.size()).forEach(i -> {
                Book b = list.get(i);
                System.out.printf("  Rank %d: [%s] %-36s | Rating: %s | Price: %,.0f VND\n",
                        i + 1, b.getBookId(), b.getTitle(), b.getStarDisplay(), b.getPrice());
            });
        }
    }

    private void reservationQueueSubMenu() {
        System.out.println("==========================================================================================");
        System.out.println("            BOOK RESERVATION QUEUE WHEN OUT OF STOCK (FEATURE 7)                          ");
        System.out.println("==========================================================================================");
        System.out.println("  1. Register Book Reservation for Member");
        System.out.println("  2. View books with waiting Reservation Queues");
        int subChoice = DataInput.getIntegerNumber(">> Select feature [1-2]: ");

        if (subChoice == 1) {
            String bookId = DataInput.getNonEmptyString("Enter Book ID to reserve (e.g. B005): ").toUpperCase();
            Book book = bookDAO.findBookById(bookId);
            if (book == null) {
                System.out.println(">> No book found with ID: " + bookId);
                return;
            }
            String memberId = DataInput.getNonEmptyString("Enter Member ID to register (e.g. M001): ").toUpperCase();
            if (bookDAO.reserveBook(bookId, memberId)) {
                System.out.println(">> Added member " + memberId + " to the reservation queue for book '" + book.getTitle() + "'!");
                System.out.println(">> Position in queue: #" + book.getReservationQueue().size()
                        + " (When a copy is returned, the system will automatically notify priority!)");
            } else {
                System.out.println(">> Member " + memberId + " is already in the reservation queue for this book!");
            }
        } else if (subChoice == 2) {
            List<Book> reservedBooks = bookDAO.getBooksWithReservations();
            if (reservedBooks.isEmpty()) {
                System.out.println(">> There are currently no books in the reservation queue.");
                return;
            }
            System.out.println("\n--- BOOKS WITH ACTIVE RESERVATION QUEUES ---");
            // [LAMBDA & STREAM API]: In danh sách đặt trước bằng IntStream
            IntStream.range(0, reservedBooks.size()).forEach(i -> {
                Book b = reservedBooks.get(i);
                System.out.printf("  %d. [%s] %-35s (Stock: %d) -> Queue (%d members): %s\n",
                        i + 1, b.getBookId(), b.getTitle(), b.getQuantity(),
                        b.getReservationQueue().size(), b.getReservationQueue().toString());
            });
        }
    }

    private void displayBookInventoryChart() {
        List<Book> books = bookDAO.getBookList();
        // [LAMBDA & STREAM API]: Ánh xạ nhãn và giá trị biểu đồ ASCII bằng map và Collectors.toList
        List<String> labels = books.stream()
                .map(b -> "[" + b.getBookId() + "] " + b.getTitle())
                .collect(Collectors.toList());
        List<Integer> values = books.stream()
                .map(Book::getQuantity)
                .collect(Collectors.toList());

        Menu.printAsciiBarChart("INVENTORY QUANTITY PER BOOK TITLE", labels, values, "copies");
    }

    public static void main(String[] args) {
        IBookDAO bookDAO = new BookDAO();
        BookManagement management = new BookManagement(bookDAO);
        management.processMenuForBook();
    }
}
