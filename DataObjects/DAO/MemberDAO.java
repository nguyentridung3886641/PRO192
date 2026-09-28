import java.util.ArrayList;
import java.util.List;

public class MemberDAO implements IMemberDAO {

    private final List<Member> membersList = new ArrayList<>();

    @Override
    public Member findById(String userId) {
        for(Member member : membersList){
            if(member.getUserId().equalsIgnoreCase(userId)) {
                return member;
            }
        }
        System.out.print("Member doesn't exist");
        return null;
    }

    @Override
    public boolean add(Member member) {
        if(member == null){
            return false;
        }

        if(findById(member.getUserId()) != null){
            return false;
        }

        membersList.add(member);
        return true;
    }

    @Override
    public boolean update(Member member) {
        return false;
    }

    @Override
    public boolean delete(String userId) {
        Member member = findById(userId);

        if(member == null){
            return false;
        }
        membersList.remove(member);
        return true;
    }

    @Override
    public List<Member> getAll() {
        return new ArrayList<>(membersList);
    }

    @Override
    public List<Member> searchByName(String keyword) {
        return List.of();
    }
}
