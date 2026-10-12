package DataObjects.DAO;

import Core.Entities.BorrowRecord;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface IBorrowDAO định nghĩa các thao tác quản lý giao dịch Mượn - Trả sách.
 * PRO192 - Phân hệ Giao dịch Mượn - Trả (Member 3)
 */
public interface IBorrowDAO {

    boolean createBorrowRecord(BorrowRecord record);

    boolean returnBooks(String recordId, LocalDate actualReturnDate);

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
