# 🔍 SỔ TAY DỊCH NGHĨA DÒNG LỆNH & BẪY CÚ PHÁP (MEMBER 4)
## Dành riêng cho buổi Vấn đáp Đồ án PRO192 — Trọng số câu hỏi: ~70%
**Người phụ trách:** Nguyễn Trí Dũng (Member 4 - Integration, Utilities & Architecture)  
**Phạm vi file:** `DataInput.java`, `DataValidation.java`, `Menu.java`, `FileManager.java`, `Program.java`

---

## 📑 MỤC LỤC TRA CỨU NHANH
1. [Bẫy dòng lệnh trong DataInput.java](#sec-datainput)
2. [Bẫy dòng lệnh trong DataValidation.java](#sec-datavalidation)
3. [Bẫy dòng lệnh trong Menu.java](#sec-menu)
4. [Bẫy dòng lệnh trong FileManager.java](#sec-filemanager)
5. [Bẫy dòng lệnh trong Program.java](#sec-program)

---

<a id="sec-datainput"></a>
<a id="1-bay-dong-lenh-trong-datainputjava"></a>
<a id="1-bẫy-dòng-lệnh-trong-datainputjava"></a>
# 1. BẪY DÒNG LỆNH TRONG `DataInput.java` (97 dòng)

### 📌 Câu hỏi 1: Dòng 10 `private static final Scanner scanner = new Scanner(System.in);`
* **Dịch nghĩa:** Khởi tạo duy nhất 1 đối tượng Scanner gắn với luồng nhập bàn phím chuẩn `System.in`, dùng chung cho toàn bộ ứng dụng.
* **Thầy hỏi: "Tại sao phải để `static final`? Nếu tạo `Scanner` mới trong từng hàm thì sao?"**
  * 👉 **Trả lời:** Nếu mỗi hàm `getString`, `getIntegerNumber` đều `new Scanner(System.in)` rồi `scanner.close()`, nó sẽ đóng luôn luồng `System.in` của hệ điều hành. Các lần nhập sau sẽ bị văng lỗi `NoSuchElementException` hoặc `IllegalStateException` ngay lập tức. Để `static final` đảm bảo an toàn tài nguyên và dùng xuyên suốt vòng đời chương trình.

---

### 📌 Câu hỏi 2: Dòng 13-16 `public static String getString(String prompt)`
```java
13: public static String getString(String prompt) {
14:     System.out.print(prompt);
15:     return scanner.nextLine().trim();
16: }
```
* **Dịch nghĩa:** In câu nhắc `prompt` ra màn hình (không xuống dòng), đọc toàn bộ chuỗi người dùng gõ đến khi ấn Enter (`nextLine()`), sau đó cắt sạch khoảng trắng thừa ở 2 đầu (`.trim()`).
* **Thầy hỏi: "Nếu xóa `.trim()` ở dòng 15 thì bị lỗi gì?"**
  * 👉 **Trả lời:** Nếu người dùng vô tình gõ dấu cách `"  Java  "`, chuỗi sẽ lưu cả dấu cách vào cơ sở dữ liệu. Sau này tìm kiếm theo mã hoặc so sánh `equals()` sẽ bị sai lệch hoàn toàn.
* **Thầy hỏi: "Tại sao không dùng `scanner.next()` mà dùng `scanner.nextLine()`?"**
  * 👉 **Trả lời:** `scanner.next()` chỉ đọc đến dấu cách đầu tiên (ví dụ gõ `"Lap trinh Java"` thì chỉ lấy được `"Lap"`). Dùng `nextLine()` mới đọc được toàn bộ chuỗi có dấu cách.

---

### 📌 Câu hỏi 3: Dòng 18-27 `public static String getNonEmptyString(String prompt)`
```java
18: public static String getNonEmptyString(String prompt) {
19:     String input;
20:     while (true) {
21:         input = getString(prompt);
22:         if (!DataValidation.checkStringEmpty(input)) {
23:             return input;
24:         }
25:         System.out.println(">> Error: Data cannot be empty. Please enter again!");
26:     }
27: }
```
* **Dịch nghĩa:** Lặp vô tận (`while(true)`), gọi `getString()`. Nếu chuỗi không rỗng thì `return input` (thoát hàm). Nếu rỗng thì in thông báo lỗi và bắt nhập lại.
* **Thầy hỏi: "Tại sao hàm này lại KHÔNG dùng try-catch như hàm `getIntegerNumber`?"**
  * 👉 **Trả lời:** Vì phương thức `scanner.nextLine()` **không bao giờ ném ngoại lệ kiểu dữ liệu** khi đọc từ bàn phím (người dùng gõ chữ, số, hay chỉ ấn Enter thì nó vẫn coi là một chuỗi hợp lệ). Lỗi ở đây là lỗi **vi phạm logic dữ liệu rỗng**, nên chỉ cần `if (!DataValidation.checkStringEmpty(input))` là chuẩn nhất, không cần `try-catch` làm chậm JVM.

---

### 📌 Câu hỏi 4: Dòng 29-38 `public static int getIntegerNumber(String prompt)`
```java
29: public static int getIntegerNumber(String prompt) {
30:     while (true) {
31:         try {
32:             String input = getString(prompt);
33:             return Integer.parseInt(input);
34:         } catch (NumberFormatException e) {
35:             System.out.println(">> Error: Please enter a valid integer!");
36:         }
37:     }
38: }
```
* **Dịch nghĩa:** Nhận vào một chuỗi, dùng `Integer.parseInt(input)` để ép kiểu sang số nguyên. Nếu người dùng gõ chữ `"abc"`, lệnh `parseInt` sẽ ném ngoại lệ `NumberFormatException`, khối `catch` bắt lại, in thông báo lỗi và vòng lặp `while(true)` tiếp tục bắt nhập lại cho đến khi đúng.
* **Thầy hỏi: "Tại sao không dùng `scanner.nextInt()` mà lại dùng `getString()` rồi `Integer.parseInt()`?"**
  * 👉 **Trả lời (Cực kỳ quan trọng):** Đây là kỹ thuật **tránh lỗi nuốt trôi dòng (buffer overflow / trailing newline)** kinh điển trong Java. Nếu dùng `nextInt()`, phím Enter (`\n`) vẫn còn sót lại trong bộ đệm bàn phím, khiến hàm đọc chuỗi `nextLine()` tiếp theo sẽ nuốt mất phím Enter đó và bị nhảy cóc qua bước nhập! Đọc cả dòng `getString()` rồi parse số giải quyết triệt để 100% lỗi này.

---

### 📌 Câu hỏi 5: Dòng 71-83 `public static LocalDate getDate(String prompt)`
```java
71: public static LocalDate getDate(String prompt) {
72:     while (true) {
73:         String input = getString(prompt + " (dd/MM/yyyy): ");
74:         if (input.isEmpty()) {
75:             return null;
76:         }
77:         try {
78:             return LocalDate.parse(input, DATE_FORMATTER);
79:         } catch (DateTimeParseException e) {
80:             System.out.println(">> Error: Date is not in valid format dd/MM/yyyy!");
81:         }
82:     }
83: }
```
* **Dịch nghĩa:** Đọc chuỗi ngày tháng, nếu người dùng ấn Enter bỏ qua (`input.isEmpty()`) thì trả về `null` (hỗ trợ cho chức năng Update không muốn đổi ngày). Nếu có nhập thì parse theo định dạng `dd/MM/yyyy`. Nếu sai (ví dụ `32/13/2026`) sẽ văng `DateTimeParseException` và bắt nhập lại.
* **Thầy hỏi: "Xóa dòng 74-76 `if (input.isEmpty()) return null;` thì sao?"**
  * 👉 **Trả lời:** Khi cập nhật dữ liệu (Update), nếu người dùng không muốn đổi ngày mượn/ngày sinh mà chỉ muốn ấn Enter giữ nguyên thì hệ thống sẽ báo lỗi định dạng ngày và bắt buộc phải gõ lại ngày.

---

<a id="sec-datavalidation"></a>
<a id="2-bay-dong-lenh-trong-datavalidationjava"></a>
<a id="2-bẫy-dòng-lệnh-trong-datavalidationjava"></a>
# 2. BẪY DÒNG LỆNH TRONG `DataValidation.java` (45 dòng)

### 📌 Câu hỏi 6: Dòng 6 & 8-9 `public final class DataValidation { private DataValidation() {} }`
* **Dịch nghĩa:** Khai báo lớp là `final` (không cho kế thừa) và đặt constructor là `private` (không cho khởi tạo đối tượng `new DataValidation()`).
* **Thầy hỏi: "Tại sao lại để constructor `private`? Để `public` có chạy được không?"**
  * 👉 **Trả lời:** Để `public` chương trình vẫn chạy được, nhưng **vi phạm chuẩn thiết kế Utility Class**. Lớp này chỉ chứa các phương thức tiện ích `static`, không có thuộc tính instance. Đặt constructor `private` để ngăn chặn bất kỳ ai `new DataValidation()`, tiết kiệm bộ nhớ và đúng chuẩn Clean Code.

---

### 📌 Câu hỏi 7: Dòng 11-13 `public static boolean checkStringEmpty(String value)`
```java
11: public static boolean checkStringEmpty(String value) {
12:     return value == null || value.trim().isEmpty();
13: }
```
* **Thầy hỏi: "Nếu đổi thứ tự thành `value.trim().isEmpty() || value == null` thì có sao không?"**
  * 👉 **Trả lời (Bẫy kinh điển):** Sẽ bị **văng lỗi sập chương trình ngay lập tức (`NullPointerException`)**! Toán tử `||` trong Java có tính chất **Short-circuit (đánh giá ngắn mạch)**: nếu `value == null` đúng thì nó dừng ngay không kiểm tra vế sau. Nếu đảo `value.trim()` lên trước mà `value` bị `null`, gọi phương thức trên đối tượng null sẽ ném `NullPointerException`.

---

### 📌 Câu hỏi 8: Dòng 23-28 `checkStringWithFormat` & Dòng 38-44 Regex
```java
39: return checkStringWithFormat(phone, "0\\d{9}");
43: return checkStringWithFormat(id, "^" + prefix + "\\d{3,}$");
```
* **Thầy hỏi: "Giải thích ý nghĩa regex `0\\d{9}` và `^" + prefix + "\\d{3,}$`?"**
  * 👉 **Trả lời:**
    - `0\\d{9}`: Số điện thoại phải bắt đầu bằng chữ số `0`, theo sau là đúng 9 chữ số (`\\d` là digit 0-9), tổng cộng đủ 10 số chuẩn Việt Nam.
    - `^` và `$`: Đánh dấu bắt đầu chuỗi và kết thúc chuỗi (khớp chính xác tuyệt đối, không chứa ký tự lạ ở đầu/cuối).
    - `\\d{3,}`: Theo sau tiền tố prefix (ví dụ `B-` hay `M-`) phải có **ít nhất 3 chữ số** trở lên (ví dụ `B-001`, `M-1024`).

---

<a id="sec-menu"></a>
<a id="3-bay-dong-lenh-trong-menujava"></a>
<a id="3-bẫy-dòng-lệnh-trong-menujava"></a>
# 3. BẪY DÒNG LỆNH TRONG `Menu.java` (84 dòng)

### 📌 Câu hỏi 9: Dòng 13-25 `Menu.print(String menuString)`
```java
15: Arrays.stream(menuString.split("\\|"))
16:         .map(String::trim)
17:         .filter(item -> !item.isEmpty())
18:         .forEach(item -> {
19:             if (item.endsWith(":") || item.endsWith("?")) {
20:                 System.out.print(item + " ");
21:             } else {
22:                 System.out.println(item);
23:             }
24:         });
```
* **Dịch nghĩa:** Tách chuỗi menu dài theo ký tự gạch đứng `|` bằng Stream API: cắt khoảng trắng (`map`), lọc bỏ mục rỗng (`filter`), duyệt từng dòng (`forEach`). Nếu dòng kết thúc bằng dấu hai chấm `:` hoặc hỏi chấm `?` (câu nhắc) thì in không xuống dòng (`print`), còn lại in từng mục xuống dòng (`println`).
* **Thầy hỏi: "Tại sao trong `split(\"\\\\|\")` lại phải có hai dấu gạch chéo `\\\\`?"**
  * 👉 **Trả lời:** Vì tham số của phương thức `split()` là một biểu thức chính quy (Regex). Trong Regex, ký tự gạch đứng `|` là toán tử logic OR. Ta phải dùng `\\|` để escape ký tự đặc biệt, báo cho Java hiểu đây là ký tự gạch đứng thông thường.

---

### 📌 Câu hỏi 10: Dòng 31-38 `Menu.repeat(String str, int count)`
* **Thầy hỏi: "Tại sao trong hàm này em dùng `StringBuilder` mà không dùng phép cộng chuỗi `str + str` trong vòng for?"**
  * 👉 **Trả lời:** Vì trong Java, kiểu `String` là **bất biến (Immutable)**. Mỗi lần dùng toán tử `+` trong vòng lặp for, JVM lại phải tạo ra một đối tượng `String` mới trong bộ nhớ Heap và giải phóng chuỗi cũ, gây tốn RAM và rác bộ nhớ. Dùng `StringBuilder` sẽ nối chuỗi trực tiếp trên cùng một mảng đệm (buffer), tối ưu hiệu năng vượt trội.

---

<a id="sec-filemanager"></a>
<a id="4-bay-dong-lenh-trong-filemanagerjava"></a>
<a id="4-bẫy-dòng-lệnh-trong-filemanagerjava"></a>
# 4. BẪY DÒNG LỆNH TRONG `FileManager.java` (59 dòng)

### 📌 Câu hỏi 11: Dòng 39-50 `readLines(String filePath)`
```java
39: public static List<String> readLines(String filePath) throws IOException {
40:     File file = new File(filePath);
41:     if (!file.exists()) {
42:         return new ArrayList<>();
43:     }
44:     try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
45:         return stream
46:                 .map(String::trim)
47:                 .filter(line -> !line.isEmpty() && !line.startsWith("#"))
48:                 .collect(Collectors.toList());
49:     }
50: }
```
* **Thầy hỏi: "Nếu xóa dòng 41-43 `if (!file.exists()) return new ArrayList<>();` thì bị sao?"**
  * 👉 **Trả lời:** Khi người dùng lần đầu cài đặt phần mềm và chạy lên, các file `Books.txt`, `Members.txt` chưa được tạo. Nếu không kiểm tra `exists()`, lệnh `Files.lines` sẽ ném ngoại lệ `NoSuchFileException` và làm sập chương trình ngay khi khởi động.
* **Thầy hỏi: "Cú pháp `try (Stream<String> stream = ...)` ở dòng 44 là gì?"**
  * 👉 **Trả lời:** Đây là cú pháp **Try-with-resources** của Java 7 trở lên. Bất kỳ tài nguyên nào cài đặt giao diện `AutoCloseable` được khai báo trong ngoặc tròn `try(...)` sẽ được Java **tự động đóng (close) luồng đọc file** khi kết thúc, kể cả khi xảy ra ngoại lệ, giúp chống rò rỉ bộ nhớ (memory leak) và không bị khóa file (file lock).
* **Thầy hỏi: "Dòng 47 `.filter(line -> !line.isEmpty() && !line.startsWith(\"#\"))` làm gì?"**
  * 👉 **Trả lời:** Lọc bỏ các dòng trắng trống và các dòng bắt đầu bằng ký tự thăng `#` (dòng ghi chú tiêu đề cột trong file dữ liệu), chỉ giữ lại các dòng dữ liệu thực sự để nạp vào RAM.

---

### 📌 Câu hỏi 12: Dòng 52-58 `writeLines(String filePath, List<String> lines)`
```java
52: public static void writeLines(String filePath, List<String> lines) throws IOException {
53:     File file = new File(filePath);
54:     if (file.getParentFile() != null && !file.getParentFile().exists()) {
55:         file.getParentFile().mkdirs();
56:     }
57:     Files.write(file.toPath(), lines != null ? lines : new ArrayList<>(), StandardCharsets.UTF_8);
58: }
```
* **Thầy hỏi: "Dòng 54-56 kiểm tra `getParentFile()` để làm gì?"**
  * 👉 **Trả lời:** Để kiểm tra xem thư mục cha chứa file đó có tồn tại hay không. Nếu đường dẫn là `data/backup/Books.txt` mà thư mục `data/backup` chưa có, lệnh `mkdirs()` sẽ tự động tạo toàn bộ cây thư mục cha trước khi ghi file, tránh lỗi `FileNotFoundException: The system cannot find the path specified`.
* **Thầy hỏi: "Tại sao ở dòng 57 lại dùng toán tử 3 ngôi `lines != null ? lines : new ArrayList<>()`?"**
  * 👉 **Trả lời:** Để phòng thủ chống lỗi `NullPointerException`. Nếu danh sách truyền vào bị `null`, nó sẽ ghi một danh sách rỗng thay vì làm sập ứng dụng.

---

<a id="sec-program"></a>
<a id="5-bay-dong-lenh-trong-programjava"></a>
<a id="5-bẫy-dòng-lệnh-trong-programjava"></a>
# 5. BẪY DÒNG LỆNH TRONG `Program.java` (174 dòng)

### 📌 Câu hỏi 13: Dòng 23-29 Khởi tạo kiến trúc phân tầng trong `main()`
```java
23: IBookDAO bookDAO = new BookDAO();
24: IMemberDAO memberDAO = new MemberDAO();
25: IBorrowDAO borrowDAO = new BorrowDAO(memberDAO, bookDAO);
26: 
27: BookManagement bookMenu = new BookManagement(bookDAO);
28: MemberManagement memberMenu = new MemberManagement(memberDAO);
29: BorrowManagement borrowMenu = new BorrowManagement(borrowDAO, memberDAO, bookDAO);
```
* **Thầy hỏi: "Tại sao dòng 25 `BorrowDAO` lại phải truyền `memberDAO` và `bookDAO` vào constructor?"**
  * 👉 **Trả lời (Quan trọng):** Đây là kỹ thuật **Dependency Injection (Tiêm phụ thuộc)**. Nghiệp vụ mượn trả sách (`BorrowDAO`) bắt buộc phải kiểm tra tính hợp lệ của Thành viên (thành viên có tồn tại không, thẻ có hết hạn không) và Sách (sách có tồn tại không, số lượng tồn kho có $> 0$ không). Việc truyền trực tiếp `memberDAO` và `bookDAO` vào giúp tái sử dụng chung bộ nhớ dữ liệu trên RAM, không bị tình trạng đọc dữ liệu phân mảnh.
* **Thầy hỏi: "Tại sao bên trái dấu `=` là Interface (`IBookDAO`) mà bên phải là Class (`BookDAO`)?"**
  * 👉 **Trả lời:** Đây là nguyên lý **Dependency Inversion Principle (chữ D trong SOLID)** và tính **Đa hình (Polymorphism)**. Lớp `Program` chỉ phụ thuộc vào bản hợp đồng giao tiếp trừu tượng (`IBookDAO`), sau này nếu nhóm muốn đổi từ lưu file text sang lưu Database MySQL thì chỉ cần thay `new BookDAOMySQL()` ở vế phải mà không phải sửa bất kỳ dòng code nào trong `Program.java`.

---

### 📌 Câu hỏi 14: Dòng 61-67 Case 0 Thoát chương trình
```java
61: case 0:
62:     saveAllData(bookDAO, memberDAO, borrowDAO);
63:     System.out.println("==========================================================================");
64:     System.out.println("   Thank you for using the Library Management System! See you again!     ");
65:     System.out.println("==========================================================================");
66:     running = false;
67:     break;
```
* **Thầy hỏi: "Nếu xóa dòng 62 `saveAllData(...)` thì điều gì sẽ xảy ra?"**
  * 👉 **Trả lời:** Nếu người dùng thao tác thêm, sửa, xóa sách hoặc mượn sách trong suốt phiên làm việc mà quên không bấm menu 6 (Backup), khi bấm 0 thoát thì toàn bộ dữ liệu trên RAM sẽ biến mất vĩnh viễn. Dòng 62 đảm bảo cơ chế **Auto-save on Exit (Tự động lưu an toàn trước khi đóng)**.
* **Thầy hỏi: "Tại sao ở dòng 66 em dùng cờ `running = false` mà không dùng lệnh `System.exit(0)`?"**
  * 👉 **Trả lời:** Dùng cờ `running = false` giúp vòng lặp kết thúc một cách tự nhiên (Graceful Shutdown), cho phép luồng thực thi chạy hết các dòng lệnh dọn dẹp tài nguyên bên dưới nếu có. Dùng `System.exit(0)` sẽ cưỡng bức dừng ngay lập tức máy ảo JVM, có thể ngắt quãng các luồng ghi file chưa kịp flush dữ liệu ra đĩa.
