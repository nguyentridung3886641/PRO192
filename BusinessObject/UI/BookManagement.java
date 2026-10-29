package BusinessObject.UI;

// Milestone 3 - Nguyen Thien Tue - Book Management UI
import Core.Entities.Book;
import Core.Entities.ReferenceBook;
import Core.Entities.TextBook;
import DataObjects.DAO.BookDAO;
import DataObjects.DAO.IBookDAO;
import Utilities.DataInput;
import Utilities.Menu;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BookManagement {

    private final IBookDAO bookDAO;

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
                    displayInventoryReport();
                    break;
                case 9:
                    reservationQueueSubMenu();
                    break;
                case 10:
                    displayBookInventoryChart();
                    break;
                case 11:
                    if (bookDAO.saveToFile()) {
                        System.out.println(">> Successfully saved book inventory to Books.txt!");
                    } else {
                        System.out.println(">> Failed to save book inventory!");
                    }
                    break;
                case 12:
                    bookDAO.loadFromFile();
                    System.out.println(">> Changes discarded from RAM. Returning to Main Menu!");
                    running = false;
                    break;
                case 0:
                    bookDAO.saveToFile();
                    System.out.println(">> Book inventory saved. Returning to Main Menu!");
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
        System.out.println("\n" + Menu.repeat("=", 104));
        System.out.println("                      BOOK & DOCUMENT INVENTORY MANAGEMENT SYSTEM                                       ");
        System.out.println("                      PRO192 - Member 2: Nguyen Thien Tue (Milestones 3 & 4)                            ");
        System.out.println(Menu.repeat("=", 104));
        Menu.print(
            "  1. View all books (Detailed table, Polymorphism & Upcasting)|" +
            "  2. Add new book to inventory (TextBook / ReferenceBook, supports Author)|" +
            "  3. Update book details by Book ID|" +
            "  4. Delete book from inventory|" +
            "  5. Search book list (By ID, Title, Author, Price Range)|" +
            "  6. Sort book list (Multi-criteria: Title, Author, Price, Qty, Total Value, Selection/Insertion Sort)|" +
            "  7. Polymorphic borrow fee simulator (Demonstrates dynamic method dispatch)|" +
            "  8. Inventory statistical report & Total asset valuation|" +
            "  9. Book reservation queue when out of stock (Feature 7)|" +
            " 10. Book inventory visual bar chart (Console ASCII Bar Chart - Feature 8)|" +
            " 11. Save book inventory to file (Books.txt)|" +
            " 12. Exit without saving (discard changes on RAM)|" +
            "  0. Save & return to Main Menu"
        );
        System.out.println(Menu.repeat("=", 104));
    }

    public void printBookTable(List<Book> books, String tableTitle) {
        String sep = "+----------+----------------------------------+----------------------+-----------------+------------------------+-----+----------------+----------------+----------------+----------------+";
        int totalWidth = sep.length();

        System.out.println();
        System.out.println(Menu.repeat("=", totalWidth));
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

        int totalQty = books.stream().mapToInt(Book::getQuantity).sum();
        double totalVal = books.stream().mapToDouble(Book::calculateTotalValue).sum();

        books.forEach(b -> {
            String typeStr;
            String extraInfo;

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
        System.out.println(Menu.repeat("=", totalWidth));
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
            System.out.println("\n>> SUCCESS: Added new book to inventory!");
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
            System.out.println("\n>> SUCCESS: Book information updated!");
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
                System.out.println(">> SUCCESS: Removed book with ID '" + bookId + "'!");
            } else {
                System.out.println(">> FAILED: Unable to remove book!");
            }
        } else {
            System.out.println(">> Book removal cancelled.");
        }
    }

    public void searchBooksSubMenu() {
        System.out.println("\n--- [ BOOK SEARCH BY CRITERIA (MILESTONE 4) ] ---");
        System.out.println("  1. Search by Book ID");
        System.out.println("  2. Search by Book Title");
        System.out.println("  3. Search by Author");
        System.out.println("  4. Search by Listed Price Range");
        System.out.println("  0. Back");

        int choice = DataInput.getIntegerNumber(">> Select search criteria [0-4]: ", 0, 4);
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

    public void displayInventoryReport() {
        List<Book> all = bookDAO.getBookList();
        int totalTitles = all.size();

        long textBookCount = all.stream().filter(b -> b instanceof TextBook).count();
        long refBookCount = all.stream().filter(b -> b instanceof ReferenceBook).count();
        int textBookQty = all.stream().filter(b -> b instanceof TextBook).mapToInt(Book::getQuantity).sum();
        int refBookQty = all.stream().filter(b -> b instanceof ReferenceBook).mapToInt(Book::getQuantity).sum();
        double textBookVal = all.stream().filter(b -> b instanceof TextBook).mapToDouble(Book::calculateTotalValue).sum();
        double refBookVal = all.stream().filter(b -> b instanceof ReferenceBook).mapToDouble(Book::calculateTotalValue).sum();

        Book highestPriceBook = all.stream().max(Comparator.comparingDouble(Book::getPrice)).orElse(null);
        Book lowestPriceBook = all.stream().min(Comparator.comparingDouble(Book::getPrice)).orElse(null);
        Book highestQtyBook = all.stream().max(Comparator.comparingInt(Book::getQuantity)).orElse(null);

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

    private static String centerText(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text.substring(0, width);
        int totalPad = width - text.length();
        int leftPad = totalPad / 2;
        int rightPad = totalPad - leftPad;
        return Menu.repeat(" ", leftPad) + text + Menu.repeat(" ", rightPad);
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
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
        List<String> labels = books.stream()
                .map(b -> "[" + b.getBookId() + "] " + b.getTitle())
                .collect(Collectors.toList());
        List<Integer> values = books.stream()
                .map(Book::getQuantity)
                .collect(Collectors.toList());

        Menu.printAsciiBarChart("INVENTORY QUANTITY PER BOOK TITLE", labels, values, "copies");
    }
}
