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
import java.util.stream.Collectors;

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

    /**
     * [LAMBDA & STREAM API]: Tìm kiếm độc giả theo mã định danh (User ID).
     * 
     * So sánh:
     * - Cách cũ: Duyệt for loop `i = 0 -> memberList.size()`, so sánh chuỗi bằng if và return member.
     * - Lambda/Stream: `filter(m -> m.getUserId().equalsIgnoreCase(targetId)).findFirst().orElse(null);`
     *   -> Ngắn gọn, loại bỏ việc quản lý chỉ số index, tự động dừng quét khi tìm thấy (short-circuit).
     */
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

    /**
     * [LAMBDA & STREAM API]: Tìm kiếm độc giả theo từ khóa họ tên.
     * Sử dụng Stream filter kết hợp Lambda expression và Collectors.toList().
     */
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

    /**
     * [LAMBDA & STREAM API]: Tìm kiếm độc giả theo số điện thoại.
     */
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

    /**
     * [LAMBDA & STREAM API]: Lọc danh sách độc giả theo Hạng thẻ (VIP / GOLD / SILVER).
     */
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

    /**
     * [LAMBDA & COMPARATOR CHAINING]: Sắp xếp danh sách độc giả theo Họ tên (A -> Z).
     * 
     * So sánh:
     * - Cách cũ: new Comparator<Member>() { public int compare(...) { ... } } với 15 dòng code ẩn danh.
     * - Lambda/Stream: Sử dụng `Comparator.comparing(...).thenComparing(...)` cực kỳ tường minh:
     *   - Tiêu chí 1: Họ tên không phân biệt hoa thường (`String.CASE_INSENSITIVE_ORDER`)
     *   - Tiêu chí 2: Nếu trùng tên thì so sánh tiếp theo Mã độc giả (ID)
     */
    @Override
    public List<Member> sortByNameAscending() {
        return memberList.stream()
                .sorted(Comparator.comparing(Member::getFullName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Member::getUserId, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & COMPARATOR]: Sắp xếp danh sách độc giả theo Mã ID (A -> Z).
     */
    @Override
    public List<Member> sortByIdAscending() {
        return memberList.stream()
                .sorted(Comparator.comparing(Member::getUserId, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & COMPARATOR CHAINING]: Sắp xếp danh sách độc giả theo Điểm uy tín giảm dần (Cao -> Thấp).
     * 
     * So sánh:
     * - Lambda: `Comparator.comparingInt(Member::getReputationPoints).reversed().thenComparing(...)`
     *   -> Giúp xếp hạng độc giả VIP lên đầu bảng một cách trực quan và tối ưu.
     */
    @Override
    public List<Member> sortByReputationDescending() {
        return memberList.stream()
                .sorted(Comparator.comparingInt(Member::getReputationPoints).reversed()
                        .thenComparing(Member::getFullName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /**
     * [LAMBDA & STREAM API]: Nạp dữ liệu từ Path với cơ chế kiểm tra chống trùng lặp mã độc giả qua Stream anyMatch & HashSet.
     */
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
