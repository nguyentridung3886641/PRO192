package DataObjects.DAO;

// Milestone 3 - Thieu Quang Tuan - Borrow DAO Interface
import Core.Entities.BorrowRecord;
import java.time.LocalDate;
import java.util.List;

public interface IBorrowDAO {

    boolean createBorrowRecord(BorrowRecord record);

    boolean returnBooksWithDamageCheck(String recordId, LocalDate actualReturnDate, List<String> lostOrDamagedBookIds);

    boolean extendBorrowRecord(String recordId, int extraDays);

    boolean exportReceiptToFile(String recordId);

    boolean deleteBorrowRecord(String recordId);

    BorrowRecord findById(String recordId);

    List<BorrowRecord> getAll();

    List<BorrowRecord> findByMemberId(String memberId);

    List<BorrowRecord> getOverdueRecords();

    List<BorrowRecord> getActiveBorrowRecords();

    int countActiveBorrowedBooksByMember(String memberId);

    double calculateTotalFineRevenue();

    double calculateTotalBorrowFeeRevenue();

    double calculateTotalCompensationRevenue();

    boolean loadFromFile();

    boolean loadFromFile(String filePath);

    boolean saveToFile();

    boolean saveToFile(String filePath);
}
