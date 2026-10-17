package DataObjects.DAO;

import Core.Entities.Member;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Lớp MemberDAO quản lý danh sách Độc giả (Member) trong bộ nhớ và đồng bộ với file Members.txt.
 * Tích hợp:
 * - Tính năng 1: Phân hạng thẻ độc giả (VIP, GOLD, SILVER) & sắp xếp theo Điểm uy tín.
 * - Tính năng kiểm tra chống trùng lặp mã độc giả (Duplicate ID Check via HashSet).
 * - Tách lớp MemberFileHandler chuyên trách File I/O theo nguyên lý SRP và NIO.2.
 * 
 * PRO192 - Phân hệ Độc giả & Người dùng (Member 1)
 */
public class MemberDAO implements IMemberDAO {

    public static final String DEFAULT_FILE_PATH = "Members.txt";
    private final List<Member> memberList = new ArrayList<>();

    public MemberDAO() {
        File file = new File(DEFAULT_FILE_PATH);
        if (file.exists() && file.length() > 0) {
            boolean loaded = loadFromFile(DEFAULT_FILE_PATH);
            if (!loaded || memberList.isEmpty()) {
                initSampleData();
                saveToFile(DEFAULT_FILE_PATH);
            }
        } else {
            initSampleData();
            saveToFile(DEFAULT_FILE_PATH);
        }
    }

    public MemberDAO(boolean loadSampleData) {
        if (loadSampleData) {
            initSampleData();
        }
    }

    public void initSampleData() {
        memberList.clear();
        add(new Member("M001", "Nguyễn Trí Dũng", "0901234567",
                LocalDate.of(2005, 5, 15), LocalDate.of(2026, 1, 10), true, 120)); // VIP
        add(new Member("M002", "Nguyễn Thiên Tuệ", "0912345678",
                LocalDate.of(2005, 8, 20), LocalDate.of(2026, 1, 12), true, 85));  // GOLD
        add(new Member("M003", "Trần Minh Quân", "0987654321",
                LocalDate.of(2005, 3, 25), LocalDate.of(2026, 2, 1), true, 65));   // GOLD
        add(new Member("M004", "Lê Hoàng Nam", "0933445566",
                LocalDate.of(2005, 11, 5), LocalDate.of(2026, 2, 15), true, 50));  // SILVER
        add(new Member("M005", "Phạm Thu Hà", "0977889900",
                LocalDate.of(2006, 2, 18), LocalDate.of(2026, 3, 1), false, -5));  // Bị khóa
    }

    @Override
    public Member findById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        String targetId = userId.trim();
        for (int i = 0; i < memberList.size(); i++) {
            Member member = memberList.get(i);
            if (member.getUserId().equalsIgnoreCase(targetId)) {
                return member;
            }
        }
        return null;
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
        List<Member> result = new ArrayList<>();
        if (keyword == null) {
            return result;
        }
        String kw = keyword.trim().toLowerCase();
        for (int i = 0; i < memberList.size(); i++) {
            Member member = memberList.get(i);
            if (member.getFullName().toLowerCase().contains(kw)) {
                result.add(member);
            }
        }
        return result;
    }

    @Override
    public List<Member> searchByPhone(String phoneKeyword) {
        List<Member> result = new ArrayList<>();
        if (phoneKeyword == null) {
            return result;
        }
        String kw = phoneKeyword.trim();
        for (int i = 0; i < memberList.size(); i++) {
            Member member = memberList.get(i);
            if (member.getPhone().contains(kw)) {
                result.add(member);
            }
        }
        return result;
    }

    @Override
    public List<Member> filterByTier(String tier) {
        List<Member> result = new ArrayList<>();
        if (tier == null) {
            return result;
        }
        String targetTier = tier.trim().toUpperCase();
        for (int i = 0; i < memberList.size(); i++) {
            Member member = memberList.get(i);
            if (member.getMembershipTier().equalsIgnoreCase(targetTier)) {
                result.add(member);
            }
        }
        return result;
    }

    @Override
    public List<Member> sortByNameAscending() {
        List<Member> sorted = new ArrayList<>(memberList);
        Collections.sort(sorted, new Comparator<Member>() {
            @Override
            public int compare(Member m1, Member m2) {
                int cmp = m1.getFullName().compareToIgnoreCase(m2.getFullName());
                if (cmp != 0) {
                    return cmp;
                }
                return m1.getUserId().compareToIgnoreCase(m2.getUserId());
            }
        });
        return sorted;
    }

    @Override
    public List<Member> sortByIdAscending() {
        List<Member> sorted = new ArrayList<>(memberList);
        Collections.sort(sorted, new Comparator<Member>() {
            @Override
            public int compare(Member m1, Member m2) {
                return m1.getUserId().compareToIgnoreCase(m2.getUserId());
            }
        });
        return sorted;
    }

    @Override
    public List<Member> sortByReputationDescending() {
        List<Member> sorted = new ArrayList<>(memberList);
        Collections.sort(sorted, new Comparator<Member>() {
            @Override
            public int compare(Member m1, Member m2) {
                if (m1.getReputationPoints() < m2.getReputationPoints()) {
                    return 1;
                } else if (m1.getReputationPoints() > m2.getReputationPoints()) {
                    return -1;
                } else {
                    return m1.getFullName().compareToIgnoreCase(m2.getFullName());
                }
            }
        });
        return sorted;
    }

    /**
     * Nạp dữ liệu từ Path với cơ chế kiểm tra chống trùng lặp mã độc giả (Duplicate ID Check via HashSet).
     */
    public void loadToMemberList(Path filePath) throws IOException {
        List<Member> loadedMemberList = MemberFileHandler.loadMembers(filePath);
        Set<String> ids = new HashSet<>();
        for (Member member : loadedMemberList) {
            String id = member.getUserId().toLowerCase(Locale.ROOT);
            if (!ids.add(id)) {
                throw new IOException("Duplicate Member ID: " + id);
            }
        }
        memberList.clear();
        memberList.addAll(loadedMemberList);
    }

    /**
     * Lưu danh sách thành viên ra Path qua MemberFileHandler.
     */
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
            System.out.println(">> Cảnh báo đọc file Members.txt: " + e.getMessage());
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
            System.out.println(">> Lỗi ghi file Members.txt: " + e.getMessage());
            return false;
        }
    }
}
