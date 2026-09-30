import java.time.LocalDate;
import java.util.List;

public interface IMemberDAO {

    boolean add(Member member);

    boolean update(Member member,
                   String fullName,
                   String phone,
                   LocalDate dateOfBirth);

    boolean delete(Member member);

    Member findById(String userId);

    List<Member> getAll();

    List<Member> searchByName(String keyword);
}
