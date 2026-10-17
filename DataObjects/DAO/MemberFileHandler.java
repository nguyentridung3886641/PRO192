package DataObjects.DAO;

import Core.Entities.Member;
import Core.Entities.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Lớp MemberFileHandler chuyên trách đọc/ghi dữ liệu Độc giả (Member) theo nguyên lý Đơn trách nhiệm (SRP).
 * Áp dụng Java NIO.2 (Files, Path) kết hợp Stream API & Biểu thức Lambda với chuẩn mã hóa UTF-8.
 * 
 * PRO192 - Milestone 4: File I/O & Exception Handling
 */
public class MemberFileHandler {

    public static String toFileString(Member member) {
        if (member == null) {
            return "";
        }
        return member.toFileString();
    }

    public static Member fromFileString(String line) {
        if (line == null || line.trim().isEmpty() || line.trim().startsWith("#")) {
            return null;
        }
        String[] data = line.split("\\|", -1);
        if (data.length < 5) {
            throw new IllegalArgumentException("Định dạng dữ liệu không hợp lệ (tối thiểu 5 trường): " + line);
        }

        String userId = data[0].trim();
        String fullName = data[1].trim();
        String phone = data[2].trim();

        LocalDate dob;
        try {
            dob = LocalDate.parse(data[3].trim(), User.DATE_FORMAT);
        } catch (Exception e) {
            try {
                dob = LocalDate.parse(data[3].trim());
            } catch (Exception ex) {
                dob = LocalDate.of(2000, 1, 1);
            }
        }

        LocalDate registrationDate;
        try {
            registrationDate = LocalDate.parse(data[4].trim(), User.DATE_FORMAT);
        } catch (Exception e) {
            try {
                registrationDate = LocalDate.parse(data[4].trim());
            } catch (Exception ex) {
                registrationDate = LocalDate.now();
            }
        }

        boolean active = true;
        if (data.length >= 6) {
            String status = data[5].trim();
            active = status.equalsIgnoreCase("ACTIVE") || status.equalsIgnoreCase("true") || status.equalsIgnoreCase("Đang hoạt động");
        }

        int points = 50;
        if (data.length >= 7) {
            try {
                points = Integer.parseInt(data[6].trim());
            } catch (NumberFormatException e) {
                points = 50;
            }
        }

        return new Member(userId, fullName, phone, dob, registrationDate, active, points);
    }

    /**
     * [LAMBDA & STREAM API]: Lưu danh sách độc giả ra file.
     * Áp dụng Stream filter loại bỏ phần tử null, map sang chuỗi bằng Method Reference
     * và gom lại thành danh sách ghi file.
     */
    public static void saveMembers(List<Member> memberList, Path filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# Định dạng: MemberID|FullName|Phone|DateOfBirth(dd/MM/yyyy)|RegistrationDate(dd/MM/yyyy)|Status|ReputationPoints");
        if (memberList != null) {
            memberList.stream()
                    .filter(Objects::nonNull)
                    .map(MemberFileHandler::toFileString)
                    .forEach(lines::add);
        }
        if (filePath.getParent() != null && Files.notExists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    /**
     * [LAMBDA & STREAM API]: Nạp danh sách độc giả từ file với Java NIO.2 Files.lines.
     * 
     * So sánh:
     * - Cách cũ: Đọc Files.readAllLines vào bộ nhớ rồi dùng vòng for lặp từng dòng.
     * - Lambda/Stream: Dùng Files.lines trả về Stream<String> lười (lazy evaluation), kết hợp:
     *   - map(String::trim): Chuẩn hóa khoảng trắng đầu cuối
     *   - filter: Loại bỏ dòng trống và dòng chú thích (#)
     *   - map(MemberFileHandler::fromFileString): Parse chuỗi thành đối tượng Member
     *   - filter(Objects::nonNull): Loại bỏ đối tượng không hợp lệ
     *   - collect(Collectors.toList()): Thu thập danh sách Member hoàn chỉnh.
     */
    public static List<Member> loadMembers(Path filePath) throws IOException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try (Stream<String> lineStream = Files.lines(filePath, StandardCharsets.UTF_8)) {
            return lineStream
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(MemberFileHandler::fromFileString)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }
}
