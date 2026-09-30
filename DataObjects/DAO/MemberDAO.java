import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO implements IMemberDAO {

    private final List<Member> memberList = new ArrayList<>();

    @Override
    public Member findById(String userId) {
        for(Member member : memberList){
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

        memberList.add(member);
        return true;
    }

    @Override
    public boolean update(Member member,
                          String fullName,
                          String phone,
                          LocalDate dateOfBirth) {

        if (member == null) {
            return false;
        }

        member.setFullName(fullName);
        member.setPhone(phone);
        member.setDateOfBirth(dateOfBirth);

        return true;
    }

    @Override
    public boolean delete(Member member) {

        if(member == null){
            return false;
        }

        return memberList.remove(member);
    }

    @Override
    public List<Member> getAll() {
        return new ArrayList<>(memberList);
    }

    @Override
    public List<Member> searchByName(String keyword) {

        List<Member> result = new ArrayList<>();

        for(Member member : memberList){
            if(member.getFullName().toLowerCase().contains(keyword.toLowerCase())){
                result.add(member);
            }
        }
        return result;
    }
}
