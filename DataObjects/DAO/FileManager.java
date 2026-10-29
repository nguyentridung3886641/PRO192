package DataObjects.DAO;

// Milestone 4 - Nguyen Tri Dung - File I/O Management
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager {

    private FileManager() {
    }

    public static List<String> readLines(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            return stream
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .collect(Collectors.toList());
        }
    }

    public static void writeLines(String filePath, List<String> lines) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Files.write(file.toPath(), lines != null ? lines : new ArrayList<>(), StandardCharsets.UTF_8);
    }
}
