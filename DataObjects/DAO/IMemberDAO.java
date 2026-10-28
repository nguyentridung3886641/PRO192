package DataObjects.DAO;

// Milestone 2 - Tran Ly Viet Tung - Member DAO Interface
import Core.Entities.Member;
import java.time.LocalDate;
import java.util.List;

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
