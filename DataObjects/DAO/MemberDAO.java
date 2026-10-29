package DataObjects.DAO;

// Milestone 2 - Tran Ly Viet Tung - Member DAO Implementation
import Core.Entities.Member;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class MemberDAO implements IMemberDAO {

    public static final String DEFAULT_FILE_PATH = "Members.txt";
    private final List<Member> memberList = new ArrayList<>();

    public MemberDAO() {
        loadFromFile(DEFAULT_FILE_PATH);
    }

    @Override
    public Member findById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        String targetId = userId.trim();
        return memberList.stream()
                .filter(m -> m.getUserId().equalsIgnoreCase(targetId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean add(Member member) {
        if (member == null || findById(member.getUserId()) != null) {
            return false;
        }
        return memberList.add(member);
    }

    @Override
    public boolean update(Member member, String fullName, String phone, LocalDate dateOfBirth) {
        if (member == null) {
            return false;
        }
        if (fullName != null && !fullName.trim().isEmpty()) {
            member.setFullName(fullName);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            member.setPhone(phone);
        }
        if (dateOfBirth != null) {
            member.setDateOfBirth(dateOfBirth);
        }
        return true;
    }

    @Override
    public boolean delete(Member member) {
        if (member == null) {
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
        if (keyword == null) {
            return new ArrayList<>();
        }
        String kw = keyword.trim().toLowerCase();
        return memberList.stream()
                .filter(m -> m.getFullName().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    @Override
    public List<Member> searchByPhone(String phoneKeyword) {
        if (phoneKeyword == null) {
            return new ArrayList<>();
        }
        String kw = phoneKeyword.trim();
        return memberList.stream()
                .filter(m -> m.getPhone().contains(kw))
                .collect(Collectors.toList());
    }

    @Override
    public List<Member> filterByTier(String tier) {
        if (tier == null) {
            return new ArrayList<>();
        }
        String targetTier = tier.trim().toUpperCase();
        return memberList.stream()
                .filter(m -> m.getMembershipTier().equalsIgnoreCase(targetTier))
                .collect(Collectors.toList());
    }

    @Override
    public List<Member> sortByNameAscending() {
        return memberList.stream()
                .sorted(Comparator.comparing(Member::getFullName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Member::getUserId, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    public List<Member> sortByIdAscending() {
        return memberList.stream()
                .sorted(Comparator.comparing(Member::getUserId, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    public List<Member> sortByReputationDescending() {
        return memberList.stream()
                .sorted(Comparator.comparingInt(Member::getReputationPoints).reversed()
                        .thenComparing(Member::getFullName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public void loadToMemberList(Path filePath) throws IOException {
        List<Member> loadedMemberList = MemberFileHandler.loadMembers(filePath);
        Set<String> ids = new HashSet<>();
        boolean hasDuplicate = loadedMemberList.stream()
                .map(m -> m.getUserId().toLowerCase(Locale.ROOT))
                .anyMatch(id -> !ids.add(id));
        if (hasDuplicate) {
            throw new IOException("Duplicate Member ID detected in file: " + filePath);
        }
        memberList.clear();
        memberList.addAll(loadedMemberList);
    }

    public void saveToFile(Path filePath) throws IOException {
        MemberFileHandler.saveMembers(memberList, filePath);
    }

    @Override
    public boolean loadFromFile() {
        return loadFromFile(DEFAULT_FILE_PATH);
    }

    @Override
    public boolean loadFromFile(String filePath) {
        try {
            loadToMemberList(Paths.get(filePath));
            return true;
        } catch (Exception e) {
            System.out.println(">> Warning reading file Members.txt: " + e.getMessage());
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
            saveToFile(Paths.get(filePath));
            return true;
        } catch (Exception e) {
            System.out.println(">> Error writing file Members.txt: " + e.getMessage());
            return false;
        }
    }
}
