# ⚡ TỜ PHAO CỨU SINH PHẢN XẠ 3 GIÂY - MEMBER 4 (EMERGENCY CHEAT-SHEET)
### Giữ file này mở ở một góc màn hình máy tính cá nhân khi ngồi đối diện thầy!

---

## 🎯 1. BẢNG TRA CỨU SỐ DÒNG SIÊU TỐC (INSTANT LINE MATRIX)
| File | Dòng quan trọng | Chức năng cốt lõi / Điểm thầy hay chỉ |
| :--- | :--- | :--- |
| **`DataInput.java`** | **L10** | `Scanner` static final dùng chung (không close để tránh đóng `System.in`). |
| | **L18-27** | `getNonEmptyString`: `while(true)` + `!checkStringEmpty` mới return. |
| | **L29-38** | `getIntegerNumber`: bắt `NumberFormatException` chống crash. |
| | **L40-48** | `getIntegerNumber(min, max)`: Method Overloading. |
| | **L71-83** | `getDate`: `input.isEmpty() -> return null` (Enter để giữ ngày cũ khi update). |
| **`DataValidation.java`**| **L6 & L8** | `public final class` + `private constructor` (Utility Class, chặn new & extends). |
| | **L11-13** | `checkStringEmpty`: Short-circuit `value == null \|\| value.trim().isEmpty()`. |
| | **L38-40** | `isValidPhone`: Regex `0\d{9}` (đúng 10 số, bắt đầu bằng 0). |
| | **L42-44** | `isValidId`: Regex `^<prefix>\d{3,}$` (bắt đầu bằng B/M/BR + tối thiểu 3 chữ số). |
| **`Menu.java`** | **L13-25** | `print`: Stream API tách `\|`, tự nhận diện `:` hoặc `?` để in ngang. |
| | **L31-38** | `repeat`: Dùng `StringBuilder` để tương thích Java 8 (chưa có `String.repeat`). |
| | **L61** | `maxVal`: Stream `values.stream().mapToInt(Integer::intValue).max().orElse(0)`. |
| | **L71-76** | `barLength`: `(int) Math.round(((double) val / maxVal) * maxBarLength)` (ép double tránh chia ra 0). |
| **`FileManager.java`** | **L39-50** | `readLines`: Java NIO.2 `Files.lines` + Try-with-resources + UTF-8. |
| | **L44** | Try-with-resources: Tự động close OS File Descriptor, chống rò rỉ file lock. |
| | **L54-56** | `writeLines`: `file.getParentFile().mkdirs()` tự tạo thư mục nếu thiếu. |
| **`Program.java`** | **L23-25** | Khai báo Interface `IBookDAO`, `IMemberDAO`, `IBorrowDAO` (DIP - SOLID). |
| | **L25** | `new BorrowDAO(memberDAO, bookDAO)` (Constructor Dependency Injection). |
| | **L61 & L66** | Case 0: Gọi `saveAllData` trước khi thoát, bảo toàn dữ liệu RAM xuống Disk. |
| | **L117-124** | Dashboard ASCII: Stream map nhãn và số lượng sách vẽ biểu đồ. |

---

## 💎 2. 5 CÂU "THẦN CHÚ" TRẢ LỜI ĐIỂM 10 (THE GOLDEN FIVE)

1. **Thầy hỏi: "Tại sao khai báo Interface mà không khai báo thẳng Class?"**
   > *"Dạ em áp dụng nguyên lý **Dependency Inversion Principle (DIP)** trong SOLID. Giúp tầng điều phối Program độc lập với tầng lưu trữ, sau này đổi sang lưu Database MySQL không cần sửa 1 dòng code nào ở tầng trên ạ."*

2. **Thầy hỏi: "Tại sao tách DataInput và DataValidation riêng?"**
   > *"Dạ em tuân thủ **Single Responsibility Principle (SRP)**. DataInput chỉ lo I/O Console, còn DataValidation là logic kiểm tra thuần túy, có thể tái sử dụng cho giao diện GUI, Web hay đọc File mà không bị phụ thuộc vào Scanner ạ."*

3. **Thầy hỏi: "Tại sao constructor của DataValidation lại là private?"**
   > *"Dạ đây là **Utility Class Pattern**. Mọi hàm đều là static không lưu trạng thái (stateless), constructor private để ngăn chặn việc khởi tạo đối tượng vô nghĩa gây lãng phí bộ nhớ Heap ạ."*

4. **Thầy hỏi: "Try-with-resources ở dòng 44 FileManager có tác dụng gì?"**
   > *"Dạ `Files.lines` mở con trỏ File Descriptor trực tiếp từ hệ điều hành. Dùng Try-with-resources để máy ảo Java tự động đóng luồng kể cả khi có ngoại lệ, tránh lỗi File Descriptor Leak làm khóa file trên đĩa ạ."*

5. **Thầy hỏi: "Hệ thống lưu trữ dữ liệu ở đâu?"**
   > *"Dạ mô hình **In-Memory Cache kết hợp File Persistence**. Trong lúc chạy dữ liệu nằm trên RAM trong các `ArrayList` để xử lý Stream tốc độ nano-giây; khi thoát hoặc sao lưu thì đồng bộ xuống file text UTF-8 trên đĩa cứng qua `FileManager` ạ."*

---

## ⚠️ 3. TOP CÁC BẪY THẦY CÓ THỂ GÀI (COUNTER-TRAPS)
- **Bẫy 1: "scanner.close() trong DataInput"** -> Tuyệt đối KHÔNG close vì sẽ đóng vĩnh viễn luồng `System.in` của JVM!
- **Bẫy 2: "Đảo thứ tự `value.trim().isEmpty() || value == null`"** -> Sai nghiêm trọng! Bị `NullPointerException` ngay vì vi phạm cơ chế đánh giá đoản mạch (Short-circuit).
- **Bẫy 3: "Bỏ ép kiểu `(double)` trong công thức chia thanh bar Menu"** -> Phép chia số nguyên `int / int` trong Java luôn ra 0, biểu đồ sẽ bị mất cột!
- **Bẫy 4: "Không truyền StandardCharsets.UTF_8"** -> Máy thầy nếu cài Windows tiếng Anh hoặc máy Mac sẽ bị lỗi vỡ font tiếng Việt (Mojibake).

---

## 🚀 4. TỪ KHÓA KỸ THUẬT QUỐC TẾ NÊN DÙNG (BUZZWORDS GHI ĐIỂM)
- `Dependency Inversion Principle (DIP)` / `Single Responsibility Principle (SRP)`
- `Polymorphism (Đa hình)` / `Method Overloading`
- `Try-with-resources` / `AutoCloseable` / `File Descriptor Leak`
- `Short-circuit Evaluation (Đánh giá đoản mạch)`
- `Lazy Evaluation (Đánh giá lười trong Stream API)`
- `Constructor-based Dependency Injection`
- `In-Memory Caching & File Persistence`
- `Stateless Utility Class Pattern`
