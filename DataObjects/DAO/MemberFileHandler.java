package DataObjects.DAO;

// Milestone 4 - Tran Ly Viet Tung - Member File Storage
import Core.Entities.Member;
import Core.Entities.User;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MemberFileHandler {

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
        lines.add("# Format: MemberID|FullName|Phone|DateOfBirth(dd/MM/yyyy)|RegistrationDate(dd/MM/yyyy)|Status|ReputationPoints");
        if (memberList != null) {
            memberList.stream()
                    .filter(Objects::nonNull)
                    .map(Member::toFileString)
                    .forEach(lines::add);
        }
        FileManager.writeLines(filePath.toString(), lines);
    }

    public static List<Member> loadMembers(Path filePath) throws IOException {
        List<String> lines = FileManager.readLines(filePath.toString());
        return lines.stream()
                .map(MemberFileHandler::fromFileString)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
