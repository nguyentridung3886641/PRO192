package DataObjects.DAO;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Lớp tiện ích FileManager chuyên trách đọc/ghi dữ liệu file văn bản với chuẩn mã hóa UTF-8.
 * Tích hợp Java NIO.2 Files kết hợp Stream API & Biểu thức Lambda.
 * 
 * PRO192 - Milestone 4: File I/O & Exception Handling (Member 4)
 */
public class FileManager {
    private String fileName;

    public FileManager() {
    }

    public FileManager(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> readDataFromFile() throws IOException {
        return readLines(this.fileName);
    }

    public void saveDataToFile(List<String> lines) throws IOException {
        writeLines(this.fileName, lines);
    }

    /**
     * [LAMBDA & STREAM API]: Đọc các dòng văn bản từ file với Java NIO.2 Files.lines.
     * Áp dụng Stream filter và map để chuẩn hóa dữ liệu và loại bỏ dòng rỗng, dòng chú thích.
     * 
     * So sánh:
     * - Cách cũ: Dùng BufferedReader duyệt từng dòng bằng vòng while `((line = reader.readLine()) != null)`.
     * - Lambda/Stream: Dùng `Files.lines` tạo luồng dữ liệu lười (lazy evaluation), kết hợp `map(String::trim)`
     *   và `filter(...)` để loại bỏ dòng chú thích/trống, sau đó thu thập bằng `collect(Collectors.toList())`.
     */
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

    /**
     * [LAMBDA & STREAM API]: Ghi các dòng dữ liệu ra file văn bản chuẩn mã hóa UTF-8.
     */
    public static void writeLines(String filePath, List<String> lines) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Files.write(file.toPath(), lines != null ? lines : new ArrayList<>(), StandardCharsets.UTF_8);
    }
}
