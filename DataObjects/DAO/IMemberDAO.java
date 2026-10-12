package DataObjects.DAO;

import Core.Entities.Member;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface IMemberDAO định nghĩa các thao tác quản lý Độc giả (Member).
 * PRO192 - Phân hệ Độc giả & Người dùng (Member 1)
 */
public interface IMemberDAO {

    boolean add(Member member);

    boolean update(Member member, String fullName, String phone, LocalDate dateOfBirth);

    boolean delete(Member member);

    Member findById(String userId);

    List<Member> getAll();

    List<Member> searchByName(String keyword);

    List<Member> searchByPhone(String phoneKeyword);

    List<Member> filterByTier(String tier);

    List<Member> sortByNameAscending();

    List<Member> sortByIdAscending();

    List<Member> sortByReputationDescending();

    boolean loadFromFile();

    boolean loadFromFile(String filePath);

    boolean saveToFile();

    boolean saveToFile(String filePath);
}
