package DataObjects.DAO;

// Milestone 3 - Thieu Quang Tuan - Borrow DAO Implementation
import Core.Entities.Book;
import Core.Entities.BorrowRecord;
import Core.Entities.Member;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BorrowDAO implements IBorrowDAO {

    public static final String DEFAULT_FILE_PATH = "BorrowRecords.txt";
    public static final int MAX_BORROW_LIMIT = 5;

    private final List<BorrowRecord> recordList = new ArrayList<>();
    private final IMemberDAO memberDAO;
    private final IBookDAO bookDAO;

    public BorrowDAO(IMemberDAO memberDAO, IBookDAO bookDAO) {
        this.memberDAO = (memberDAO != null) ? memberDAO : new MemberDAO();
        this.bookDAO = (bookDAO != null) ? bookDAO : new BookDAO();

        File file = new File(DEFAULT_FILE_PATH);
        if (file.exists() && file.length() > 0) {
            boolean loaded = loadFromFile(DEFAULT_FILE_PATH);
            if (!loaded || recordList.isEmpty()) {
                initSampleData();
                saveToFile(DEFAULT_FILE_PATH);
            }
        } else {
            initSampleData();
            saveToFile(DEFAULT_FILE_PATH);
        }
    }

    public void initSampleData() {
        recordList.clear();
        Member m1 = memberDAO.findById("M001");
        Member m2 = memberDAO.findById("M002");
        Member m3 = memberDAO.findById("M003");

        Book b1 = bookDAO.findBookById("B001");
        Book b2 = bookDAO.findBookById("B002");
        Book b5 = bookDAO.findBookById("B005");
        Book b6 = bookDAO.findBookById("B006");

        if (m1 != null && b1 != null && b5 != null) {
            List<Book> list1 = new ArrayList<>();
            list1.add(b1);
            list1.add(b5);
            BorrowRecord r1 = new BorrowRecord("BR001", m1, list1,
                    LocalDate.now().minusDays(10), LocalDate.now().plusDays(4));
            recordList.add(r1);
        }

        if (m2 != null && b2 != null) {
            List<Book> list2 = new ArrayList<>();
            list2.add(b2);
            BorrowRecord r2 = new BorrowRecord("BR002", m2, list2,
                    LocalDate.now().minusDays(20), LocalDate.now().minusDays(6));
            recordList.add(r2);
        }

        if (m3 != null && b6 != null) {
            List<Book> list3 = new ArrayList<>();
            list3.add(b6);
            BorrowRecord r3 = new BorrowRecord("BR003", m3, list3,
                    LocalDate.now().minusDays(25), LocalDate.now().minusDays(11));
            r3.markAsReturned(LocalDate.now().minusDays(8));
            recordList.add(r3);
        }
    }

    @Override
    public BorrowRecord findById(String recordId) {
        if (recordId == null || recordId.trim().isEmpty()) {
            return null;
        }
        String target = recordId.trim();
        return recordList.stream()
                .filter(r -> r.getRecordId().equalsIgnoreCase(target))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int countActiveBorrowedBooksByMember(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            return 0;
        }
        String cleanId = memberId.trim();
        return recordList.stream()
                .filter(r -> !r.isReturned() && r.getMember().getUserId().equalsIgnoreCase(cleanId))
                .mapToInt(r -> r.getBorrowedBooks().size())
                .sum();
    }

    @Override
    public boolean createBorrowRecord(BorrowRecord record) {
        if (record == null || findById(record.getRecordId()) != null) {
            return false;
        }
        Member m = record.getMember();
        if (m == null || !m.isActive()) {
            return false;
        }
        List<Book> books = record.getBorrowedBooks();
        if (books == null || books.isEmpty()) {
            return false;
        }
        int currentBorrowed = countActiveBorrowedBooksByMember(m.getUserId());
        if (currentBorrowed + books.size() > m.getMaxBorrowLimit()) {
            return false;
        }
        boolean allInStock = books.stream().allMatch(b -> b.getQuantity() > 0);
        if (!allInStock) {
            return false;
        }
        books.forEach(b -> b.setQuantity(b.getQuantity() - 1));

        recordList.add(record);
        return true;
    }

    @Override
    public boolean returnBooks(String recordId, LocalDate actualReturnDate) {
        return returnBooksWithDamageCheck(recordId, actualReturnDate, new ArrayList<String>());
    }

    @Override
    public boolean returnBooksWithDamageCheck(String recordId, LocalDate actualReturnDate, List<String> lostOrDamagedBookIds) {
        BorrowRecord record = findById(recordId);
        if (record == null || record.isReturned()) {
            return false;
        }
        record.markAsReturned(actualReturnDate);

        List<Book> borrowed = record.getBorrowedBooks();
        double[] totalCompensation = {0.0};

        borrowed.forEach(b -> {
            boolean isDamaged = (lostOrDamagedBookIds != null) &&
                    lostOrDamagedBookIds.stream().anyMatch(id -> b.getBookId().equalsIgnoreCase(id.trim()));

            if (isDamaged) {
                totalCompensation[0] += b.calculatePrice() * 1.5;
            } else {
                b.setQuantity(b.getQuantity() + 1);

                String nextWaitingMember = b.popNextReservation();
                if (nextWaitingMember != null) {
                    System.out.println("  >> [RESERVATION NOTIFICATION]: Book '" + b.getTitle()
                            + "' has been returned! Next waiting member in queue: " + nextWaitingMember);
                }
            }
        });

        record.setCompensationFee(totalCompensation[0]);

        Member member = record.getMember();
        if (totalCompensation[0] > 0) {
            member.addReputationPoints(-20);
        } else if (record.isOverdue()) {
            member.addReputationPoints(-15);
        } else {
            member.addReputationPoints(10);
        }

        return true;
    }

    @Override
    public boolean extendBorrowRecord(String recordId, int extraDays) {
        BorrowRecord record = findById(recordId);
        if (record == null) {
            return false;
        }
        return record.extendDueDate(extraDays);
    }

    @Override
    public boolean exportReceiptToFile(String recordId) {
        BorrowRecord r = findById(recordId);
        if (r == null) {
            return false;
        }
        String fileName = "Receipt_" + r.getRecordId().toUpperCase() + ".txt";
        List<String> lines = new ArrayList<>();
        lines.add("======================================================================");
        lines.add("                    LIBRARY TRANSACTION RECEIPT                       ");
        lines.add("======================================================================");
        lines.add("  Record ID        : " + r.getRecordId());
        lines.add("  Member ID        : " + r.getMember().getUserId());
        lines.add("  Member Name      : " + r.getMember().getFullName());
        lines.add("  Current Tier     : " + r.getMember().getMembershipTier()
                + " (Reputation Points: " + r.getMember().getReputationPoints() + " pts)");
        lines.add("  Borrow Date      : " + r.getBorrowDate().format(BorrowRecord.DATE_FORMAT));
        lines.add("  Due Date         : " + r.getDueDate().format(BorrowRecord.DATE_FORMAT));
        lines.add("  Actual Return    : " + (r.getReturnDate() != null ? r.getReturnDate().format(BorrowRecord.DATE_FORMAT) : "Not returned"));
        lines.add("  Record Status    : " + r.getStatusText());
        lines.add("----------------------------------------------------------------------");
        lines.add("  BORROWED BOOK LIST (" + r.getBorrowedBooks().size() + " book(s)):");
        IntStream.range(0, r.getBorrowedBooks().size()).forEach(i -> {
            Book b = r.getBorrowedBooks().get(i);
            if (b != null) {
                lines.add(String.format("    %d. [%s] %-32s | Borrow Fee (%d days): %,.0f VND",
                        i + 1, b.getBookId(), b.getTitle(), r.getBorrowDurationDays(),
                        b.calculateBorrowFee(r.getBorrowDurationDays())));
            }
        });
        lines.add("----------------------------------------------------------------------");
        double rawFee = r.calculateTotalBorrowFee();
        double discountRate = r.getMember().getDiscountRate();
        double discountedFee = r.calculateDiscountedBorrowFee();
        double fine = r.calculateFine();
        double comp = r.getCompensationFee();
        lines.add(String.format("  + Total Raw Borrow Fee             : %,14.0f VND", rawFee));
        lines.add(String.format("  + Tier Discount %-6s (-%2.0f%%)       : -%,13.0f VND",
                r.getMember().getMembershipTier(), discountRate * 100, rawFee - discountedFee));
        lines.add(String.format("  + Fee After Discount               : %,14.0f VND", discountedFee));
        lines.add(String.format("  + Overdue Fine (%2d days)           : %,14.0f VND", r.getOverdueDays(), fine));
        lines.add(String.format("  + Book Damage/Loss Fee (150%%)      : %,14.0f VND", comp));
        lines.add("----------------------------------------------------------------------");
        lines.add(String.format("  => TOTAL PAYMENT AMOUNT            : %,14.0f VND", r.calculateFinalPayment()));
        lines.add("======================================================================");

        try {
            FileManager.writeLines(fileName, lines);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteBorrowRecord(String recordId) {
        BorrowRecord record = findById(recordId);
        if (record == null) {
            return false;
        }
        if (!record.isReturned()) {
            record.getBorrowedBooks().forEach(b -> b.setQuantity(b.getQuantity() + 1));
        }
        return recordList.remove(record);
    }

    @Override
    public List<BorrowRecord> getAll() {
        return new ArrayList<>(recordList);
    }

    @Override
    public List<BorrowRecord> findByMemberId(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String cleanId = memberId.trim();
        return recordList.stream()
                .filter(r -> r.getMember().getUserId().equalsIgnoreCase(cleanId))
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecord> getOverdueRecords() {
        return recordList.stream()
                .filter(BorrowRecord::isOverdue)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecord> getActiveBorrowRecords() {
        return recordList.stream()
                .filter(r -> !r.isReturned())
                .collect(Collectors.toList());
    }

    @Override
    public double calculateTotalFineRevenue() {
        return recordList.stream()
                .mapToDouble(BorrowRecord::calculateFine)
                .sum();
    }

    @Override
    public double calculateTotalBorrowFeeRevenue() {
        return recordList.stream()
                .mapToDouble(BorrowRecord::calculateDiscountedBorrowFee)
                .sum();
    }

    @Override
    public double calculateTotalCompensationRevenue() {
        return recordList.stream()
                .mapToDouble(BorrowRecord::getCompensationFee)
                .sum();
    }

    @Override
    public boolean loadFromFile() {
        return loadFromFile(DEFAULT_FILE_PATH);
    }

    private BorrowRecord parseBorrowRecord(String line) {
        if (line == null || line.trim().isEmpty() || line.trim().startsWith("#")) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length < 7) {
            return null;
        }
        try {
            String recordId = parts[0].trim();
            String memberId = parts[1].trim();
            String[] bookIds = parts[2].trim().split(",");
            LocalDate borrowDate = LocalDate.parse(parts[3].trim(), BorrowRecord.DATE_FORMAT);
            LocalDate dueDate = LocalDate.parse(parts[4].trim(), BorrowRecord.DATE_FORMAT);
            String returnStr = parts[5].trim();
            boolean returned = parts[6].trim().equalsIgnoreCase("RETURNED");

            Member member = memberDAO.findById(memberId);
            if (member == null) {
                return null;
            }

            List<Book> books = Arrays.stream(bookIds)
                    .map(String::trim)
                    .map(bookDAO::findBookById)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (books.isEmpty()) {
                return null;
            }

            BorrowRecord record = new BorrowRecord(recordId, member, books, borrowDate, dueDate);
            if (!returnStr.equalsIgnoreCase("NONE") && !returnStr.isEmpty()) {
                record.setReturnDate(LocalDate.parse(returnStr, BorrowRecord.DATE_FORMAT));
            }
            record.setReturned(returned);
            if (parts.length >= 8) {
                record.setExtensionCount(Integer.parseInt(parts[7].trim()));
            }
            if (parts.length >= 9) {
                record.setCompensationFee(Double.parseDouble(parts[8].trim()));
            }
            return record;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public boolean loadFromFile(String filePath) {
        try {
            List<String> lines = FileManager.readLines(filePath);
            List<BorrowRecord> loaded = lines.stream()
                    .map(this::parseBorrowRecord)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            recordList.clear();
            recordList.addAll(loaded);
            return true;
        } catch (Exception e) {
            System.out.println(">> Warning reading file BorrowRecords.txt: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean saveToFile() {
        return saveToFile(DEFAULT_FILE_PATH);
    }

    @Override
    public boolean saveToFile(String filePath) {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("# Format: RecordID|MemberID|BookIDs|BorrowDate|DueDate|ReturnDate|Status|ExtensionCount|CompensationFee");
            recordList.stream()
                    .map(BorrowRecord::toFileString)
                    .forEach(lines::add);
            FileManager.writeLines(filePath, lines);
            return true;
        } catch (Exception e) {
            System.out.println(">> Error writing file BorrowRecords.txt: " + e.getMessage());
            return false;
        }
    }
}
