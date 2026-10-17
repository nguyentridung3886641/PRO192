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

/**
 * Lớp MemberFileHandler chuyên trách đọc/ghi dữ liệu Độc giả (Member) theo nguyên lý Đơn trách nhiệm (SRP).
 * Áp dụng Java NIO.2 (Files, Path) với chuẩn mã hóa UTF-8 và cơ chế xác thực dữ liệu từng dòng.
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

    public static void saveMembers(List<Member> memberList, Path filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# Định dạng: MemberID|FullName|Phone|DateOfBirth(dd/MM/yyyy)|RegistrationDate(dd/MM/yyyy)|Status|ReputationPoints");
        if (memberList != null) {
            for (Member member : memberList) {
                if (member != null) {
                    lines.add(toFileString(member));
                }
            }
        }
        if (filePath.getParent() != null && Files.notExists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    public static List<Member> loadMembers(Path filePath) throws IOException {
        List<Member> memberList = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return memberList;
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line == null || line.isBlank() || line.trim().startsWith("#")) {
                continue;
            }
            Member member = fromFileString(line.trim());
            if (member != null) {
                memberList.add(member);
            }
        }
        return memberList;
    }
}
