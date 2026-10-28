package DataObjects.DAO;

// Milestone 4 - Tran Ly Viet Tung - Member File Storage
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
            throw new IllegalArgumentException("Invalid data format (minimum 5 fields): " + line);
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
            active = status.equalsIgnoreCase("ACTIVE") || status.equalsIgnoreCase("true") || status.equalsIgnoreCase("Active") || status.equalsIgnoreCase("Đang hoạt động");
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
        lines.add("# Format: MemberID|FullName|Phone|DateOfBirth(dd/MM/yyyy)|RegistrationDate(dd/MM/yyyy)|Status|ReputationPoints");
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
