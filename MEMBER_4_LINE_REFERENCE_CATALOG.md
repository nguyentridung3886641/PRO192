# 📌 BẢNG TRA CỨU SỐ DÒNG CHUẨN XÁC 100% CHO MEMBER 4 (BẢN CLEAN GỐC)
### Dự án: Library Management System — Khóa: PRO192 (OOP with Java)
**Phụ trách:** Nguyễn Trí Dũng (`SE210456`) — Phân hệ Hạ tầng, Tiện ích, Báo cáo & Điều phối Toàn hệ thống
**Cam kết:** Khớp chính xác 1:1 theo từng dòng code với bản sạch chú thích nộp cho giảng viên.

---

## 📑 MỤC LỤC TRA CỨU NHANH
1. [`Utilities/DataInput.java` (97 dòng)](#1-utilitiesdatainputjava-97-dòng)
2. [`Utilities/DataValidation.java` (45 dòng)](#2-utilitiesdatavalidationjava-45-dòng)
3. [`Utilities/Menu.java` (84 dòng)](#3-utilitiesmenujava-84-dòng)
4. [`DataObjects/DAO/FileManager.java` (59 dòng)](#4-dataobjectsdaofilemanagerjava-59-dòng)
5. [`BusinessObject/UI/Program.java` (174 dòng)](#5-businessobjectuiprogramjava-174-dòng)

---

## 1. `Utilities/DataInput.java` (97 dòng)
| Dòng | Phương thức / Thuộc tính | Tham số & Kiểu trả về | Giải thích kỹ thuật & Phản xạ 1 câu khi thầy hỏi |
| :--- | :--- | :--- | :--- |
| **L10** | `scanner` | `private static final Scanner` | Scanner duy nhất của hệ thống, không `close()` để tránh đóng luồng `System.in`. |
| **L11** | `DATE_FORMATTER` | `DateTimeFormatter ("dd/MM/yyyy")` | Chuẩn định dạng ngày tháng của toàn bộ đồ án, thread-safe (bất biến). |
| **L13-16** | `getString(prompt)` | `String prompt -> String` | In câu nhắc, đọc cả dòng bằng `nextLine()` và `.trim()` loại khoảng trắng thừa. |
| **L18-27** | `getNonEmptyString(prompt)` | `String prompt -> String` | Vòng `while(true)` bắt buộc nhập, gọi `DataValidation.checkStringEmpty(input)`. |
| **L29-38** | `getIntegerNumber(prompt)` | `String prompt -> int` | Chống crash: Bắt `NumberFormatException`, lặp đến khi người dùng nhập đúng số nguyên. |
| **L40-48** | `getIntegerNumber(prompt, min, max)` | `prompt, int, int -> int` | **Method Overloading**: Tái sử dụng hàm trên và chặn biên `[min - max]`. |
| **L50-59** | `getDoubleNumber(prompt)` | `String prompt -> double` | Nhập số thực an toàn, bắt `NumberFormatException` nếu nhập ký tự lạ. |
| **L61-69** | `getPositiveDouble(prompt)` | `String prompt -> double` | Ép số thực dương `> 0` (dùng khi nhập giá sách, tiền phạt, phí đền bù). |
| **L71-83** | `getDate(prompt)` | `String prompt -> LocalDate` | Chuyển chuỗi sang ngày tháng. Bắt `DateTimeParseException`. Trả về `null` nếu bỏ trống (Enter). |
| **L85-96** | `getBoolean(prompt)` | `String prompt -> boolean` | Nhận xác nhận Y/N (Yes/No), không phân biệt hoa thường (`.toUpperCase()`). |

---

## 2. `Utilities/DataValidation.java` (45 dòng)
| Dòng | Phương thức / Thuộc tính | Tham số & Kiểu trả về | Giải thích kỹ thuật & Phản xạ 1 câu khi thầy hỏi |
| :--- | :--- | :--- | :--- |
| **L6** | `public final class DataValidation` | Lớp tiện ích | Khai báo `final` ngăn cản kế thừa (Utility Class Pattern). |
| **L8-9** | `private DataValidation() {}` | Private Constructor | Khóa khởi tạo: Không cho phép `new DataValidation()` vì tất cả hàm đều là `static`. |
| **L11-13** | `checkStringEmpty(value)` | `String -> boolean` | Kiểm tra chuỗi rỗng an toàn: `value == null \|\| value.trim().isEmpty()`. |
| **L15-21** | `checkStringLengthInRange(value, min, max)` | `String, int, int -> boolean` | Kiểm tra độ dài chuỗi trong khoảng `[min, max]`, tự kiểm tra null trước. |
| **L23-28** | `checkStringWithFormat(value, pattern)` | `String, String -> boolean` | So khớp chuỗi với biểu thức chính quy bằng `Pattern.matches(pattern, value.trim())`. |
| **L30-32** | `checkNumberInMinMax(number, min, max)` | `int, int, int -> boolean` | Kiểm tra số nguyên nằm trong đoạn `[min, max]`. |
| **L34-36** | `checkDoubleInMinMax(number, min, max)` | `double, double, double -> boolean` | Kiểm tra số thực nằm trong đoạn `[min, max]`. |
| **L38-40** | `isValidPhone(phone)` | `String -> boolean` | Regex `0\d{9}`: Số điện thoại Việt Nam bắt đầu bằng số 0 và có đúng 10 chữ số. |
| **L42-44** | `isValidId(id, prefix)` | `String, String -> boolean` | Regex `^<prefix>\d{3,}$`: Bắt đầu bằng tiền tố (B, M, BR), theo sau ít nhất 3 chữ số. |

---

## 3. `Utilities/Menu.java` (84 dòng)
| Dòng | Phương thức / Thuộc tính | Tham số & Kiểu trả về | Giải thích kỹ thuật & Phản xạ 1 câu khi thầy hỏi |
| :--- | :--- | :--- | :--- |
| **L10-11** | `private Menu() {}` | Private Constructor | Utility Class, ngăn chặn việc tạo đối tượng `new Menu()`. |
| **L13-25** | `print(menuString)` | `String -> void` | **Stream API**: Tách chuỗi theo dấu `\|`, `map(String::trim)`, `filter`, in ngang nếu có `:` hoặc `?`, in xuống dòng nếu là mục chọn. |
| **L27-29** | `getUserChoice()` | `void -> int` | Gọi `DataInput.getIntegerNumber("")` để lấy lựa chọn của người dùng. |
| **L31-38** | `repeat(str, count)` | `String, int -> String` | Nhân bản chuỗi bằng `StringBuilder` (tương thích Java 8 vì `String.repeat()` chỉ có từ Java 11). |
| **L40-48** | `printHeader(title)` | `String -> void` | In tiêu đề đóng khung cân giữa 60 ký tự bằng `repeat("=", 60)`. |
| **L50-59** | `printAsciiBarChart` (Khởi đầu) | `title, labels, values, unit` | Kiểm tra an toàn: labels null, values null, rỗng, hoặc lệch kích thước thì in thông báo và return ngay. |
| **L61** | `int maxVal = ...` | `Stream mapToInt -> int` | Tìm giá trị lớn nhất kho: `values.stream().mapToInt(Integer::intValue).max().orElse(0)`. |
| **L62** | `int maxBarLength = 35` | Cột mốc | Độ dài tối đa của thanh biểu đồ ASCII trên Console là 35 ký tự `=`. |
| **L64-81** | `IntStream.range(0, labels.size())` | `Stream forEach Lambda` | Duyệt song song index, rút gọn nhãn `> 26` ký tự thành `...`, tính tỷ lệ độ dài thanh bar. |
| **L71-76** | Tính `barLength` | Công thức tỷ lệ | `(int) Math.round(((double) val / maxVal) * maxBarLength)`. Ép `(double)` để tránh chia số nguyên ra 0; nếu `val > 0` thì tối thiểu vẽ 1 dấu `=`. |

---

## 4. `DataObjects/DAO/FileManager.java` (59 dòng)
| Dòng | Phương thức / Thuộc tính | Tham số & Kiểu trả về | Giải thích kỹ thuật & Phản xạ 1 câu khi thầy hỏi |
| :--- | :--- | :--- | :--- |
| **L14** | `private String fileName` | Thuộc tính đối tượng | Cho phép dùng cả dạng hướng đối tượng `fileManager.readDataFromFile()` lẫn static utility. |
| **L39-50** | `readLines(filePath)` | `String -> List<String>` | **Java NIO.2 + Stream API**: Đọc file an toàn với UTF-8. Trả về list rỗng nếu file chưa tồn tại. |
| **L44** | `try (Stream<String> stream = ...)`| **Try-with-resources** | Tự động đóng luồng đọc file của OS (tránh File Descriptor Leak). Dùng `StandardCharsets.UTF_8` chống vỡ font. |
| **L45-48** | `.map(...).filter(...).collect(...)`| Pipeline xử lý | Tự động `trim()`, lọc bỏ các dòng rỗng và dòng chú thích bắt đầu bằng `#`. |
| **L52-58** | `writeLines(filePath, lines)` | `String, List<String> -> void` | Tự động tạo thư mục cha (`mkdirs()`) nếu chưa có, ghi đè toàn bộ dữ liệu bằng `Files.write` UTF-8. |

---

## 5. `BusinessObject/UI/Program.java` (174 dòng)
| Dòng | Phương thức / Khối lệnh | Chức năng chi tiết | Nguyên lý & Phản xạ khi thầy chất vấn |
| :--- | :--- | :--- | :--- |
| **L23-25** | Khởi tạo 3 DAO | `IBookDAO bookDAO = new BookDAO(); ...` | **DIP (Dependency Inversion)**: Khai báo bằng Interface, độc lập với triển khai cụ thể. |
| **L25** | `new BorrowDAO(memberDAO, bookDAO)` | **Dependency Injection** | BorrowDAO phụ thuộc vào MemberDAO và BookDAO để xác thực hạn mức thẻ và kiểm tra tồn kho sách. |
| **L27-29** | Khởi tạo 3 Subsystem UI | `BookManagement`, `MemberManagement`, `BorrowManagement` | Phân chia trách nhiệm: UI chỉ nhận DAO để thao tác, không tự quản lý dữ liệu gốc. |
| **L31-72** | `while (running)` | Vòng lặp điều phối chính | Chạy menu tổng, switch-case 7 lựa chọn (1: Book, 2: Member, 3: Borrow, 4: Dashboard, 5: Export, 6: Backup, 0: Exit). |
| **L61 & L66**| Case 0 (Exit) | `saveAllData(...)` rồi `running = false` | Đảm bảo tính toàn vẹn dữ liệu: Trước khi thoát chương trình luôn tự động ghi toàn bộ RAM ra đĩa cứng. |
| **L75-90** | `printMainBanner()` | Hiển thị menu hệ thống | Áp dụng `Menu.print(...)` để phân tích chuỗi menu bằng Stream pipeline. |
| **L92-125**| `displaySystemDashboard(...)`| Dashboard điều hành | Tập hợp số liệu thống kê từ cả 3 DAO; trích xuất `labels` và `values` bằng Stream API để vẽ biểu đồ tồn kho ASCII. |
| **L127-163**| `exportSystemReportToFile(...)`| Xuất báo cáo tổng hợp | Tập hợp báo cáo 3 phần (Sách, Độc giả, Doanh thu), xuất ra file `Library_Summary_Report.txt` qua `FileManager.writeLines`. |
| **L164-173**| `saveAllData(...)` | Đồng bộ dữ liệu | Gọi `saveToFile()` của cả 3 DAO, kiểm tra kết quả boolean để báo cáo thành công. |
