package DataObjects.DAO;

// Milestone 2 - Nguyen Thien Tue - Book DAO Implementation
import Core.Entities.Book;
import Core.Entities.ReferenceBook;
import Core.Entities.TextBook;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BookDAO implements IBookDAO {

    public static final String DEFAULT_FILE_PATH = "Books.txt";
    private final List<Book> bookList = new ArrayList<>();

    public BookDAO() {
        loadFromFile(DEFAULT_FILE_PATH);
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
        String cleanId = bookId.trim();
        return bookList.stream()
                .filter(b -> b.getBookId() != null && b.getBookId().equalsIgnoreCase(cleanId))
                .findFirst()
                .orElse(null);
    }

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
    public int getTotalBooksCount() {
        return bookList.size();
    }

    @Override
    public int getTotalQuantity() {
        return bookList.stream()
                .mapToInt(Book::getQuantity)
                .sum();
    }

    @Override
    public double calculateTotalInventoryValue() {
        return bookList.stream()
                .mapToDouble(Book::calculateTotalValue)
                .sum();
    }

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

    @Override
    public List<Book> findBookByPriceRange(double minPrice, double maxPrice) {
        double lower = Math.min(minPrice, maxPrice);
        double upper = Math.max(minPrice, maxPrice);
        return bookList.stream()
                .filter(b -> b.getPrice() >= lower && b.getPrice() <= upper)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> getSortedByAuthor(boolean ascending) {
        Comparator<Book> comp = Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER);
        return bookList.stream()
                .sorted(ascending ? comp : comp.reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> getSortedByQuantity(boolean ascending) {
        Comparator<Book> comp = Comparator.comparingInt(Book::getQuantity);
        return bookList.stream()
                .sorted(ascending ? comp : comp.reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> getSortedByTotalValue(boolean ascending) {
        Comparator<Book> comp = Comparator.comparingDouble(Book::calculateTotalValue);
        return bookList.stream()
                .sorted(ascending ? comp : comp.reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> sortByPriceAlgorithm(boolean ascending) {
        List<Book> list = getBookList();
        int n = list.size();
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

    public static String toFileString(Book b) {
        if (b == null)
            return null;
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

        return String.format("%s|%s|%s|%s|%.2f|%d|%s",
                typeStr,
                b.getBookId(),
                b.getTitle(),
                b.getAuthor(),
                b.getPrice(),
                b.getQuantity(),
                extraInfo);
    }

    @Override
    public boolean saveToFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = DEFAULT_FILE_PATH;
        }
        try {
            List<String> lines = new ArrayList<>();
            lines.add("# Book Storage Repository (Library Management System - PRO192)");
            lines.add("# Format: BOOK_TYPE|BOOK_ID|TITLE|AUTHOR|PRICE|QUANTITY|GENRE_OR_PUBLISHER");
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

            return book;
        } catch (Exception ex) {
            System.err.println(">> Warning: Skipping invalid data line: " + line);
            return null;
        }
    }

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

            this.bookList.clear();
            this.bookList.addAll(loadedList);
            return true;
        } catch (IOException e) {
            System.err.println(">> Error reading file: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean reserveBook(String bookId, String memberId) {
        Book book = findBookById(bookId);
        if (book == null) {
            return false;
        }
        return book.addReservation(memberId);
    }

    @Override
    public List<Book> getBooksWithReservations() {
        return bookList.stream()
                .filter(b -> !b.getReservationQueue().isEmpty())
                .collect(Collectors.toList());
    }
}
