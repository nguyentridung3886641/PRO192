package DataObjects.DAO;

import Core.Entities.Member;
import Core.Entities.User;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Lớp MemberDAO quản lý danh sách Độc giả trong bộ nhớ (ArrayList<Member>)
 * và đồng bộ lưu trữ với file Members.txt.
 * Toàn bộ các hàm tìm kiếm, lọc và sắp xếp đều viết bằng vòng lặp for và Comparator truyền thống.
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

    public void initSampleData() {
        memberList.clear();
        // Độc giả mẫu có đủ các hạng thẻ: VIP (>=100đ), GOLD (60-99đ), SILVER (<60đ)
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

    /**
     * Tính năng 1: Lọc danh sách độc giả theo Hạng thẻ (SILVER, GOLD, VIP) bằng vòng lặp for truyền thống.
     */
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

    /**
     * Tính năng 1: Sắp xếp độc giả theo Điểm uy tín giảm dần bằng Comparator truyền thống (if - else).
     */
    @Override
    public List<Member> sortByReputationDescending() {
        List<Member> sorted = new ArrayList<>(memberList);
        Collections.sort(sorted, new Comparator<Member>() {
            @Override
            public int compare(Member m1, Member m2) {
                if (m1.getReputationPoints() < m2.getReputationPoints()) {
                    return 1; // Đổi chỗ để điểm cao hơn lên trước
                } else if (m1.getReputationPoints() > m2.getReputationPoints()) {
                    return -1;
                } else {
                    return m1.getFullName().compareToIgnoreCase(m2.getFullName());
                }
            }
        });
        return sorted;
    }

    @Override
    public boolean loadFromFile() {
        return loadFromFile(DEFAULT_FILE_PATH);
    }

    @Override
    public boolean loadFromFile(String filePath) {
        try {
            List<String> lines = FileManager.readLines(filePath);
            List<Member> loaded = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String phone = parts[2].trim();
                    LocalDate dob = LocalDate.parse(parts[3].trim(), User.DATE_FORMAT);
                    LocalDate reg = LocalDate.parse(parts[4].trim(), User.DATE_FORMAT);
                    boolean active = true;
                    if (parts.length >= 6) {
                        active = parts[5].trim().equalsIgnoreCase("ACTIVE") || parts[5].trim().equalsIgnoreCase("true");
                    }
                    int points = 50;
                    if (parts.length >= 7) {
                        try {
                            points = Integer.parseInt(parts[6].trim());
                        } catch (NumberFormatException ex) {
                            points = 50;
                        }
                    }
                    loaded.add(new Member(id, name, phone, dob, reg, active, points));
                }
            }
            if (!loaded.isEmpty()) {
                memberList.clear();
                memberList.addAll(loaded);
                return true;
            }
        } catch (Exception e) {
            System.out.println(">> Cảnh báo đọc file Members.txt: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean saveToFile() {
        return saveToFile(DEFAULT_FILE_PATH);
    }

    @Override
    public boolean saveToFile(String filePath) {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("# Định dạng: MemberID|FullName|Phone|DateOfBirth(dd/MM/yyyy)|RegistrationDate(dd/MM/yyyy)|Status|ReputationPoints");
            for (int i = 0; i < memberList.size(); i++) {
                lines.add(memberList.get(i).toFileString());
            }
            FileManager.writeLines(filePath, lines);
            return true;
        } catch (Exception e) {
            System.out.println(">> Lỗi ghi file Members.txt: " + e.getMessage());
            return false;
        }
    }
}
