# 🛡️ BỘ KỊCH BẢN VẤN ĐÁP TOÀN DIỆN CHO MEMBER 4 (PRO192 DEFENSE GUIDE)
### Dự án: Library Management System — Học phần: PRO192 (OOP with Java)
**Sinh viên phụ trách:** Nguyễn Trí Dũng (`SE210456`)  
**Phân hệ đảm nhiệm:** Hạ tầng, Tiện ích, Báo cáo & Điều phối Toàn hệ thống (`DataInput`, `DataValidation`, `Menu`, `FileManager`, `Program`)

---

## 📌 HƯỚNG DẪN SỬ DỤNG BỘ KỊCH BẢN KHI NGỒI ĐỐI DIỆN THẦY
1. **Tâm thế**: Giữ bình tĩnh, nhìn thẳng vào màn hình hoặc mắt thầy, trả lời ngắn gọn câu kết luận trước, sau đó mới giải thích kỹ thuật và trích dẫn số dòng code.
2. **Nguyên tắc "Mở rộng góc nhìn"**: Thầy hỏi về 1 hàm nhỏ trong `DataInput`, bạn hãy liên kết nó với nguyên lý SOLID, cách mà `Program.java` sử dụng, và lý do tại sao thiết kế như vậy giúp hệ thống chạy ổn định không bao giờ crash.
3. **Tra cứu số dòng**: Mở file [MEMBER_4_LINE_REFERENCE_CATALOG.md](file:///C:/Users/Admin/OneDrive/FA26/PRO192/Project/Library%20Management/MEMBER_4_LINE_REFERENCE_CATALOG.md) ở tab kế bên để định vị số dòng trong 2 giây (đã đồng bộ 1:1 với bản nộp sạch).

---

# PHẦN 1: KIẾN TRÚC OOP & 5 NGUYÊN LÝ SOLID (GÓC ĐỘ THIẾT KẾ)

### ❓ Câu 1: "Tại sao trong `Program.java` (dòng 23-25) em lại khai báo kiểu Interface `IBookDAO bookDAO = new BookDAO();` mà không khai báo thẳng `BookDAO bookDAO = new BookDAO();`?"
* **Mục đích thầy hỏi**: Kiểm tra nguyên lý **Dependency Inversion Principle (DIP)** trong SOLID và tính chất **Đa hình (Polymorphism)**.
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, em áp dụng nguyên lý **Dependency Inversion Principle (DIP)** — chữ D trong SOLID: *Các module cấp cao không nên phụ thuộc trực tiếp vào module cấp thấp, cả hai nên phụ thuộc vào abstraction (Interface)*.  
  > Việc khai báo `IBookDAO bookDAO = new BookDAO();` giúp tầng điều phối `Program` chỉ giao tiếp thông qua bản hợp đồng nghiệp vụ do Interface quy định. Sau này nếu thư viện muốn nâng cấp từ lưu file text sang lưu Database MySQL hoặc Cloud API, em chỉ việc tạo lớp `BookDatabaseDAO implements IBookDAO` và thay thế tại đúng 1 dòng khởi tạo này mà **hoàn toàn không phải sửa lại một dòng code nào** trong `Program`, `displaySystemDashboard` hay `BookManagement`. Điều này đồng thời thỏa mãn nguyên lý **Open/Closed Principle (OCP)** ạ."

---

### ❓ Câu 2: "Tại sao em lại tách `DataInput` và `DataValidation` thành 2 class riêng biệt? Gộp chung vào `DataInput` có phải tiện hơn không?"
* **Mục đích thầy hỏi**: Kiểm tra nguyên lý **Single Responsibility Principle (SRP)**.
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, em cố ý tách biệt để tuân thủ nghiêm ngặt nguyên lý **Single Responsibility Principle (SRP)**:
  > 1. `DataInput` chỉ chịu một trách nhiệm duy nhất là **I/O tương tác với người dùng qua Console** (nhận prompt, đọc bàn phím qua Scanner, bắt ngoại lệ ép người dùng nhập lại).
  > 2. `DataValidation` chỉ chịu trách nhiệm **kiểm tra tính hợp lệ thuần túy của dữ liệu (Business Rule / Validation Logic)** mà hoàn toàn không tương tác với Console hay Scanner.  
  > Lợi ích thiết kế: Lớp `DataValidation` trở thành một module hoàn toàn độc lập (Pure Logic). Sau này nếu dự án mở rộng giao diện đồ họa JavaFX, Web REST API hay đọc dữ liệu từ File/Excel, em có thể tái sử dụng 100% các hàm kiểm tra trong `DataValidation` mà không bị phụ thuộc vào Console Scanner ạ."

---

### ❓ Câu 3: "Trong `DataValidation.java`, dòng 6 em khai báo `public final class DataValidation` và dòng 8 có constructor `private DataValidation() {}`. Giải thích ý nghĩa của 2 từ khóa này?"
* **Mục đích thầy hỏi**: Kiểm tra kiến thức về **Utility Class Pattern** và **Encapsulation**.
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy:
  > - **Constructor là `private` (dòng 8-9)**: Nhằm mục đích **ngăn chặn việc khởi tạo đối tượng vô nghĩa** bằng lệnh `new DataValidation()`. Vì `DataValidation` là một lớp tiện ích (Utility Class), tất cả các phương thức bên trong đều là `static` và không lưu trạng thái (stateless), nên việc cấp phát bộ nhớ Heap cho đối tượng là dư thừa.
  > - **Khai báo `final` ở cấp độ class (dòng 6)**: Nhằm **ngăn chặn việc kế thừa (Inheritance)**. Không một lớp nào có thể `extends DataValidation` để override hay làm sai lệch logic xác thực dữ liệu, đảm bảo tính bảo mật và toàn vẹn của hệ thống ạ."

---

### ❓ Câu 4: "Nguyên lý Liskov Substitution Principle (LSP) và Interface Segregation Principle (ISP) thể hiện ở đâu trong phần việc của em?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy:
  > - **ISP (Interface Segregation)**: Nhóm em không gộp chung tất cả các hàm vào một Interface quản lý khổng lồ, mà phân tách thành các Interface chuyên biệt: `IBookDAO`, `IMemberDAO`, `IBorrowDAO`. Nhờ đó, trong `Program.java`, mỗi phân hệ chỉ cần phụ thuộc vào đúng những hành vi mà nó quan tâm.
  > - **LSP (Liskov Substitution)**: Thể hiện rõ nhất khi `Program.java` (dòng 93 và 117-124) lấy danh sách `List<Book>` từ `bookDAO`. Các cuốn sách trong danh sách thực chất là các đối tượng con `TextBook` hoặc `ReferenceBook`. Tại dòng 121, khi em gọi `Book::getQuantity` hoặc khi `bookDAO.calculateTotalInventoryValue()` được gọi, các lớp con thay thế hoàn hảo cho lớp cha `Book` mà chương trình không hề xảy ra lỗi hay cần biết cụ thể đó là loại sách gì ạ."

---

### ❓ Câu 5: "Trong `Menu.java`, em giải quyết bài toán giao diện Console như thế nào để vừa linh hoạt vừa dễ mở rộng?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, em thiết kế hàm `Menu.print(String menuString)` ở dòng 13-25 nhận vào chuỗi định dạng phân cách bởi dấu gạch đứng `|`. Em áp dụng Stream API: `Arrays.stream(menuString.split("\\|")).map(String::trim).filter(...)`.
  > Thiết kế này đem lại 2 ưu điểm lớn:
  > 1. Không cần phải viết hàng chục dòng lệnh `System.out.println()` lặp đi lặp lại.
  > 2. Hàm tự động phân tích: nếu chuỗi kết thúc bằng dấu `:` hoặc `?` (câu nhắc nhập liệu) thì dùng `System.out.print()` để con trỏ chuột nằm cùng dòng; nếu là mục chọn menu thì dùng `System.out.println()` để xuống dòng đẹp mắt ạ."

---

# PHẦN 2: TRA VẤN CHI TIẾT TỪNG DÒNG CODE (LINE-BY-LINE DRILLING)

## Phân Tích File 1: `Utilities/DataInput.java` (97 dòng)

### ❓ Câu 6: "Dòng 10 `private static final Scanner scanner = new Scanner(System.in);` — Tại sao em lại để Scanner là static dùng chung mà không `close()` trong từng hàm nhập?"
* **Cạm bẫy**: Rất nhiều sinh viên viết `Scanner sc = new Scanner(System.in); ... sc.close();` trong hàm và bị trừ điểm nặng!
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, đây là kỹ thuật then chốt để bảo vệ luồng nhập liệu chuẩn của JVM:
  > `System.in` là một luồng dữ liệu chuẩn (Standard Input Stream) do máy ảo Java quản lý toàn cục. Nếu em tạo một `Scanner` cục bộ và gọi lệnh `scanner.close()`, hành động đó sẽ **đóng luôn cả luồng `System.in` bên dưới của hệ điều hành**. Sau khi luồng này bị đóng, mọi thao tác đọc bàn phím tiếp theo của chương trình sẽ văng ngoại lệ `java.util.NoSuchElementException` hoặc `IllegalStateException` và không thể mở lại được nữa.  
  > Do đó, chuẩn thiết kế công nghiệp trong ứng dụng Console là duy trì duy nhất một `Scanner` `static final` dùng chung trong suốt vòng đời của ứng dụng và để JVM tự thu hồi khi chương trình kết thúc ạ."

---

### ❓ Câu 7: "Dòng 18-27 trong `getNonEmptyString()`: Giải thích cơ chế hoạt động của vòng lặp `while (true)`? Khi nào vòng lặp này dừng?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy:
  > - Dòng 19 khởi tạo vòng lặp vô tận `while (true)`.
  > - Dòng 20 gọi `getString(prompt)` để đọc chuỗi từ bàn phím và tự động cắt bỏ khoảng trắng đầu cuối bằng `.trim()`.
  > - Dòng 21 kiểm tra điều kiện thông qua `DataValidation.checkStringEmpty(input)`. Nếu chuỗi không rỗng (`!checkStringEmpty`), lệnh `return input;` ở dòng 22 sẽ lập tức **ngắt vòng lặp và trả giá trị về cho nơi gọi**.
  > - Nếu người dùng chỉ bấm Enter hoặc gõ toàn dấu cách, điều kiện ở dòng 21 bị sai, chương trình rơi xuống dòng 24 in thông báo lỗi và tự động lặp lại, ép người dùng phải nhập dữ liệu hợp lệ mới được đi tiếp ạ."

---

### ❓ Câu 8: "Dòng 29-38 và 40-48: Hai hàm cùng tên `getIntegerNumber` thể hiện tính chất gì của Java? Phân tích cách xử lý lỗi tại dòng 34 `catch (NumberFormatException e)`?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy:
  > 1. Đây là kỹ thuật **Method Overloading (Nạp chồng phương thức)** — một dạng của Đa hình lúc biên dịch (Compile-time Polymorphism). Hàm thứ hai nhận thêm tham số `int min, int max` để giới hạn khoảng giá trị.
  > 2. Ở dòng 34, em bắt ngoại lệ `NumberFormatException`. Ngoại lệ này xảy ra khi người dùng nhập chuỗi ký tự không thể ép sang số nguyên (ví dụ nhập `abc`, `12.5`, hoặc để trống). Thay vì để chương trình bị crash văng màn hình đỏ, khối `catch` sẽ bắt lấy lỗi, in cảnh báo thân thiện và vòng `while (true)` cho phép người dùng nhập lại ngay lập tức ạ."

---

### ❓ Câu 9: "Dòng 71-83 trong `getDate(String prompt)`: Nếu người dùng không nhập gì mà chỉ ấn phím Enter thì kết quả là gì? Tại sao em lại thiết kế như vậy?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, ở dòng 74-76:
  > ```java
  > if (input.isEmpty()) {
  >     return null;
  > }
  > ```
  > Nếu người dùng ấn Enter thì hàm sẽ trả về `null`.  
  > Đây là thiết kế có chủ đích nhằm phục vụ tính năng **Cập nhật dữ liệu (Update)**: Khi người dùng muốn cập nhật thông tin sách hoặc độc giả, nếu họ giữ nguyên ngày sinh/ngày mượn cũ thì họ chỉ cần ấn Enter bỏ qua. Lớp nghiệp vụ nhận được giá trị `null` sẽ hiểu là không thay đổi ngày và giữ nguyên giá trị ban đầu ạ."

---

## Phân Tích File 2: `Utilities/DataValidation.java` (45 dòng)

### ❓ Câu 10: "Dòng 11-13 `checkStringEmpty(String value)`: Tại sao lại viết `value == null || value.trim().isEmpty()`? Đổi thứ tự thành `value.trim().isEmpty() || value == null` có được không?"
* **Cạm bẫy**: Kiểm tra cơ chế đánh giá đoản mạch (Short-circuit Evaluation) và lỗi `NullPointerException`.
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, **tuyệt đối không được đổi thứ tự ạ**!  
  > Toán tử logic `||` trong Java hoạt động theo cơ chế **Short-circuit Evaluation (Đánh giá đoản mạch)**: Nếu vế trái `value == null` đúng thì Java sẽ lập tức trả về `true` mà không duyệt tiếp vế phải.  
  > Nếu đổi thứ tự đưa `value.trim()` lên trước, khi biến `value` bị `null`, việc gọi phương thức `.trim()` trên đối tượng null sẽ lập tức ném ra ngoại lệ `NullPointerException` làm chết chương trình ngay lập tức ạ."

---

### ❓ Câu 11: "Dòng 38-40 và 42-44: Giải thích tường tận biểu thức chính quy (Regex) `0\\d{9}` và `^" + prefix + "\\d{3,}$`?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy:
  > 1. Hàm `isValidPhone`: biểu thức `0\\d{9}`:
  >    - Ký tự `0`: Bắt buộc số điện thoại phải bắt đầu bằng chữ số 0.
  >    - `\\d`: Đại diện cho một chữ số bất kỳ từ `0` đến `9`. (Dùng 2 dấu gạch chéo `\\` vì dấu gạch chéo đầu tiên là escape character của chuỗi Java).
  >    - `{9}`: Bắt buộc lặp lại đúng 9 lần chữ số. Tổng cộng là đúng 10 chữ số theo chuẩn số điện thoại di động Việt Nam.
  > 2. Hàm `isValidId`: biểu thức `^" + prefix + "\\d{3,}$`:
  >    - Dấu `^`: Khớp với điểm bắt đầu chuỗi.
  >    - `prefix`: Tiền tố nghiệp vụ truyền vào (ví dụ `B` cho Book, `M` cho Member, `BR` cho BorrowRecord).
  >    - `\\d{3,}`: Theo sau tiền tố phải có **ít nhất 3 chữ số trở lên** (ví dụ `B001`, `B002`, `M100`).
  >    - Dấu `$`: Khớp với điểm kết thúc chuỗi, chống việc người dùng nhập thừa ký tự lạ ở đuôi ạ."

---

## Phân Tích File 3: `Utilities/Menu.java` (84 dòng)

### ❓ Câu 12: "Dòng 31-38: Tại sao em lại tự viết hàm `repeat(String str, int count)` bằng `StringBuilder` mà không dùng hàm có sẵn `str.repeat(count)` của lớp `String`?"
* **Cạm bẫy**: Kiểm tra phiên bản Java (Java 8 vs Java 11+).
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, phương thức `String.repeat(int count)` chỉ mới được Oracle bổ sung từ **Java 11**.  
  > Trong khi đó, đề tài đồ án PRO192 yêu cầu đảm bảo khả năng tương thích tuyệt đối với **chuẩn công nghiệp Java 8 (JDK 1.8)**. Vì vậy, em tự hiện thực hàm `repeat` bằng cách sử dụng `StringBuilder` với dung tích khởi tạo được tính toán trước `new StringBuilder(str.length() * count)` ở dòng 33. Việc cấp phát kích thước trước giúp tránh việc `StringBuilder` phải re-allocate mảng bộ nhớ nhiều lần, mang lại hiệu năng tối ưu nhất trên nền tảng Java 8 ạ."

---

### ❓ Câu 13: "Dòng 50-83: Phân tích thuật toán vẽ biểu đồ cột ASCII trong `printAsciiBarChart`? Công thức tính độ dài thanh bar ở dòng 71-76 hoạt động thế nào?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, hàm `printAsciiBarChart` gồm 3 bước logic:
  > 1. **Dòng 61 - Tìm giá trị lớn nhất (Pivot)**: Dùng Stream API `values.stream().mapToInt(Integer::intValue).max().orElse(0)`.
  > 2. **Dòng 62 - Giới hạn độ dài Console**: Đặt `maxBarLength = 35` ký tự để biểu đồ vừa vặn với kích thước màn hình Console chuẩn.
  > 3. **Dòng 64-81 - Duyệt song song & Chuẩn hóa tỷ lệ**: Dùng `IntStream.range(0, labels.size())`:
  >    - Nếu tên nhãn dài quá 26 ký tự, dòng 66-68 cắt ngắn và thêm `...` để không làm vỡ khung bảng.
  >    - **Công thức cốt lõi (dòng 72)**: `barLength = (int) Math.round(((double) val / maxVal) * maxBarLength);`. Em bắt buộc phải ép kiểu `(double) val` trước khi chia cho `maxVal`. Nếu không ép sang `double`, phép chia hai số nguyên trong Java sẽ làm tròn xuống 0, khiến thanh bar luôn có độ dài bằng 0!
  >    - **Dòng 73-75**: Nếu `val > 0` mà tỷ lệ quá nhỏ ra `barLength == 0`, em gán tối thiểu `barLength = 1` để người dùng vẫn nhìn thấy cột dữ liệu tồn kho ạ."

---

## Phân Tích File 4: `DataObjects/DAO/FileManager.java` (59 dòng)

### ❓ Câu 14: "Dòng 44: `try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8))` — Giải thích cú pháp này? Tại sao phải dùng Try-with-resources cho `Files.lines`?"
* **Mục đích thầy hỏi**: Kiểm tra kiến thức về **Try-with-resources (AutoCloseable)** và nguy cơ rò rỉ tài nguyên hệ thống (Resource Leak).
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy:
  > 1. Đây là cú pháp **Try-with-resources** được giới thiệu từ Java 7. Bất kỳ đối tượng nào hiện thực interface `java.lang.AutoCloseable` được khai báo trong ngoặc tròn `try (...)` sẽ được **máy ảo Java tự động đóng (gọi hàm `.close()`) khi thoát khỏi khối try**, kể cả khi có ngoại lệ xảy ra.
  > 2. **Tại sao `Files.lines` bắt buộc phải đóng?** Vì phương thức `Files.lines` mở một con trỏ đọc file (File Descriptor/Channel) trực tiếp từ hệ điều hành. Khác với các Stream thông thường tạo từ Collection trong RAM, Stream từ file giữ tài nguyên I/O của OS. Nếu không dùng Try-with-resources, con trỏ file sẽ bị treo, dẫn đến lỗi **File Descriptor Leak**, làm khóa file và khiến hệ thống không thể xóa hoặc ghi đè file sau đó ạ."

---

### ❓ Câu 15: "Dòng 44 và 57: Tại sao em truyền tường minh `StandardCharsets.UTF_8` mà không dùng phương thức mặc định không truyền charset?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, nếu không truyền tham số mã hóa, Java sẽ tự động lấy bảng mã mặc định của hệ điều hành máy tính (`Charset.defaultCharset()`).  
  > Trên hệ điều hành Windows phiên bản tiếng Anh hoặc tiếng Nhật, charset mặc định thường là `Windows-1252` hoặc `MS932`. Nếu dữ liệu sách và tên độc giả có tiếng Việt có dấu, khi đọc và ghi sẽ lập tức bị lỗi vỡ font (**Mojibake**, xuất hiện các dấu chấm hỏi `???` hoặc ký tự rác). Việc khai báo tường minh `StandardCharsets.UTF_8` đảm bảo ứng dụng chạy đồng nhất và hiển thị tiếng Việt hoàn hảo trên mọi hệ điều hành từ Windows, Linux đến macOS ạ."

---

### ❓ Câu 16: "Dòng 54-56 trong `writeLines`: Khối lệnh này kiểm tra và xử lý điều gì?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, ở dòng 54-56:
  > ```java
  > if (file.getParentFile() != null && !file.getParentFile().exists()) {
  >     file.getParentFile().mkdirs();
  > }
  > ```
  > Khối lệnh này kiểm tra xem thư mục cha chứa file có tồn tại hay không. Nếu đường dẫn truyền vào là một thư mục lồng nhau (ví dụ `reports/monthly/Library_Report.txt`) mà các thư mục cha chưa được tạo, lệnh `mkdirs()` sẽ tự động tạo toàn bộ cây thư mục đó. Điều này ngăn chặn ngoại lệ `java.io.FileNotFoundException (The system cannot find the path specified)` khi gọi `Files.write` ở dòng 57 ạ."

---

## Phân Tích File 5: `BusinessObject/UI/Program.java` (174 dòng)

### ❓ Câu 17: "Dòng 25: `IBorrowDAO borrowDAO = new BorrowDAO(memberDAO, bookDAO);` — Tại sao `BorrowDAO` lại cần truyền `memberDAO` và `bookDAO` vào constructor?"
* **Mục đích thầy hỏi**: Kiểm tra mối quan hệ giữa các đối tượng (**Dependency Injection / Aggregation**).
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, đây là kỹ thuật **Constructor-based Dependency Injection (Tiêm phụ thuộc qua Constructor)**:
  > Phân hệ Mượn - Trả sách (`BorrowDAO`) không thể hoạt động độc lập mà có mối quan hệ phụ thuộc chặt chẽ với hai phân hệ còn lại:
  > 1. Cần `memberDAO` để: Kiểm tra mã độc giả có tồn tại không, thẻ có bị khóa không, độc giả thuộc hạng thẻ nào (VIP/GOLD/SILVER) để áp dụng hạn mức mượn (5/7/10 cuốn) và chiết khấu phí mượn.
  > 2. Cần `bookDAO` để: Kiểm tra mã sách, kiểm tra tồn kho (`quantity > 0`), tự động trừ số lượng sách khi mượn và cộng trả lại kho khi độc giả hoàn sách.  
  > Việc tiêm trực tiếp 2 DAO này từ `Program` giúp đảm bảo tính nhất quán của dữ liệu trong RAM (Single Source of Truth) xuyên suốt toàn bộ ứng dụng ạ."

---

### ❓ Câu 18: "Dòng 61 và dòng 66 trong Case 0: Phân tích cơ chế bảo toàn dữ liệu khi người dùng chọn Thoát chương trình?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, tại Case 0 (khi người dùng chọn 0 để thoát):
  > - Dòng 62: Trước khi gán cờ `running = false` ở dòng 66, chương trình luôn bắt buộc gọi phương thức `saveAllData(bookDAO, memberDAO, borrowDAO);`.
  > - Bên trong hàm `saveAllData` (dòng 164-173), hệ thống gọi tuần tự `saveToFile()` của cả 3 phân hệ để serialize toàn bộ danh sách `List<Book>`, `List<Member>`, `List<BorrowRecord>` từ bộ nhớ RAM xuống các file vật lý `Books.txt`, `Members.txt`, `BorrowRecords.txt`.  
  > Cơ chế này đảm bảo dữ liệu luôn được lưu trữ an toàn (Persistence), ngăn chặn nguy cơ mất dữ liệu do người dùng quên lưu trước khi thoát ạ."

---

### ❓ Câu 19: "Dòng 117-124 trong `displaySystemDashboard`: Em trích xuất dữ liệu để vẽ biểu đồ ASCII như thế nào bằng Stream API?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, em sử dụng phương thức trung gian `.map()` và phương thức kết thúc `.collect()` của Stream API:
  > - Dòng 117-119: `books.stream().map(b -> "[" + b.getBookId() + "] " + b.getTitle()).collect(Collectors.toList());` — Biến đổi luồng đối tượng `Stream<Book>` thành luồng chuỗi `Stream<String>` biểu diễn nhãn sách gồm ID và Tiêu đề.
  > - Dòng 120-122: `books.stream().map(Book::getQuantity).collect(Collectors.toList());` — Sử dụng cú pháp **Method Reference `Book::getQuantity`** để rút trích số lượng tồn kho của từng cuốn sách thành `List<Integer>`.  
  > Sau đó em truyền 2 danh sách này vào `Menu.printAsciiBarChart` ở dòng 124 để hiển thị biểu đồ trực quan ạ."

---

# PHẦN 3: BẢN CHẤT BỘ NHỚ & JVM INTERNALS (JAVA CORE CHUYÊN SÂU)

### ❓ Câu 20: "Trong Java, String là Immutable (bất biến). Việc em dùng phép cộng chuỗi `lines.add("..." + ...)` trong hàm `exportSystemReportToFile` (dòng 132-154) có làm tốn RAM không? Trình biên dịch tối ưu nó ra sao?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, trên lý thuyết String trong Java là bất biến (Immutable), mỗi phép cộng chuỗi bằng toán tử `+` có thể tạo ra đối tượng trung gian trong String Constant Pool / Heap.  
  > Tuy nhiên, từ phiên bản Java 5 trở đi, trình biên dịch `javac` tự động tối ưu hóa các phép nối chuỗi tĩnh bằng cách ngầm chuyển thành `new StringBuilder().append(...).toString()`.  
  > Trong hàm `exportSystemReportToFile`, vì số lượng dòng báo cáo là cố định (khoảng 20 dòng) và chạy theo sự kiện bấm nút của người dùng (không nằm trong vòng lặp lặp lại hàng triệu lần), nên chi phí cấp phát bộ nhớ là cực kỳ nhỏ (vài Kilobyte) và được bộ dọn rác **Garbage Collector (Young Generation / Eden Space)** thu hồi ngay lập tức, hoàn toàn không gây áp lực lên bộ nhớ JVM ạ."

---

### ❓ Câu 21: "Phương thức `Pattern.matches(pattern, value.trim())` ở `DataValidation.java` dòng 27 hoạt động như thế nào? Có khác gì so với việc dùng `Pattern.compile`?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy:
  > - Phương thức tiện ích `Pattern.matches(regex, input)` thực chất bên dưới sẽ gọi `Pattern.compile(regex).matcher(input).matches()`. Mỗi lần gọi hàm, máy ảo Java sẽ phải biên dịch chuỗi regex thành một cây cú pháp hữu hạn (Finite State Automaton) rồi mới so khớp.
  > - Nếu là hệ thống tải cao (hàng triệu transaction/giây), tối ưu nhất là biên dịch trước `private static final Pattern PHONE_PATTERN = Pattern.compile("0\\d{9}")` để tái sử dụng.
  > - Nhưng đối với ứng dụng quản lý thư viện Console của chúng em, thao tác nhập liệu phụ thuộc vào tốc độ gõ phím của con người (vài giây một thao tác), nên việc dùng `Pattern.matches` giúp mã nguồn ngắn gọn, tường minh và thời gian thực thi chỉ mất vài micro-giây, hoàn toàn đáp ứng tốt hiệu năng ạ."

---

### ❓ Câu 22: "Sự khác biệt giữa Exception và RuntimeException trong Java? Trong các file của em, loại ngoại lệ nào được sử dụng?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy:
  > 1. **Checked Exception (kế thừa từ `java.lang.Exception`)**: Là ngoại lệ bắt buộc phải xử lý lúc biên dịch, nếu không trình biên dịch sẽ báo lỗi. Trong code của em, `IOException` ở `FileManager.readLines()` và `writeLines()` là Checked Exception, bắt buộc phải dùng `throws IOException` hoặc bọc trong khối `try-catch`.
  > 2. **Unchecked Exception (kế thừa từ `java.lang.RuntimeException`)**: Là ngoại lệ xảy ra trong lúc chạy (runtime), trình biên dịch không ép buộc phải bắt. Trong code của em, `NumberFormatException` (khi parse số sai) và `DateTimeParseException` (khi parse ngày sai) trong `DataInput.java` chính là Unchecked Exception. Em đã chủ động dùng `try-catch` để bọc chúng lại nhằm bảo vệ chương trình không bị sập ạ."

---

# PHẦN 4: BẮT NGOẠI LỆ & HOSTILE EDGE CASES (KIỂM THỬ BIÊN CỰC ĐOAN)

### ❓ Câu 23: "Nếu thầy mở file `Books.txt` lên, xóa sạch toàn bộ nội dung thành file rỗng 0 byte hoặc xóa hẳn file đi, sau đó chạy `Program`, chương trình của em có bị crash không? Nó sẽ xử lý thế nào?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, chương trình **hoàn toàn không bị crash** mà chạy cực kỳ thông minh ạ!
  > - Trong `FileManager.readLines()` (dòng 41-43), nếu file không tồn tại, hàm kiểm tra `if (!file.exists())` và trả về một `new ArrayList<>()` rỗng chứ không bắn lỗi.
  > - Sau đó, trong constructor của `BookDAO`, hệ thống kiểm tra nếu file rỗng (`file.length() == 0`) hoặc `bookList.isEmpty()`, hệ thống sẽ **tự động kích hoạt hàm `initSampleData()`** để nạp lại danh sách sách mẫu tiêu chuẩn và tự động lưu ngược ra file `Books.txt`. Nhờ đó hệ thống luôn tự phục hồi (Self-healing) về trạng thái chuẩn ạ!"

---

### ❓ Câu 24: "Trong hàm `printAsciiBarChart`, nếu tất cả số lượng sách trong kho đều bằng 0 (`maxVal = 0`), biểu đồ có bị lỗi chia cho 0 (`ArithmeticException: / by zero`) không?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, **hoàn toàn không bị lỗi chia cho 0 ạ**!
  > Em đã lường trước trường hợp biên này tại dòng 71-76 trong `Menu.java`:
  > ```java
  > int barLength = 0;
  > if (maxVal > 0) {
  >     barLength = (int) Math.round(((double) val / maxVal) * maxBarLength);
  >     if (val > 0 && barLength == 0) {
  >         barLength = 1;
  >     }
  > }
  > ```
  > Em đặt điều kiện `if (maxVal > 0)` trước khi thực hiện phép chia. Nếu `maxVal == 0`, biến `barLength` sẽ giữ nguyên giá trị mặc định là 0, biểu đồ sẽ vẽ cột trống với số lượng 0 và in ra bình thường mà không hề có ngoại lệ số học nào xảy ra ạ."

---

### ❓ Câu 25: "Nếu người dùng cố tình nhập chuỗi có độ dài 1000 ký tự vào ô chọn Menu subsystem `[0-6]`, hàm `DataInput.getIntegerNumber` xử lý thế nào?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, hàm `getString(prompt)` sẽ đọc toàn bộ chuỗi 1000 ký tự đó vào bộ nhớ. Sau đó lệnh `Integer.parseInt(input)` ở dòng 33 sẽ phát hiện chuỗi này vượt quá giới hạn giá trị của kiểu `int` trong Java (`Integer.MAX_VALUE = 2,147,483,647`) và ném ra `NumberFormatException`. Khối `catch` ở dòng 34-36 bắt được ngoại lệ này, in ra `>> Error: Please enter a valid integer!` và yêu cầu người dùng nhập lại, chương trình hoàn toàn an toàn ạ."

---

# PHẦN 5: VAI TRÒ ĐIỀU PHỐI TOÀN HỆ THỐNG (COORDINATOR ROLE)

### ❓ Câu 26: "Với vai trò Member 4, em kết nối công việc của 3 thành viên còn lại như thế nào trong dự án?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, em đóng vai trò là **Kỹ sư Hạ tầng & Tích hợp (Infrastructure & System Integrator)**:
  > 1. **Quy chuẩn hóa giao tiếp (Interface Contract)**: Em thống nhất các Interface DAO (`IBookDAO`, `IMemberDAO`, `IBorrowDAO`) với các mẫu hàm chuẩn (`add`, `update`, `delete`, `findById`, `getAll`, `loadFromFile`, `saveToFile`).
  > 2. **Cung cấp tiện ích dùng chung (Core Utilities)**: Em xây dựng bộ đôi `DataInput` và `DataValidation` để 3 thành viên còn lại khi viết giao diện `BookManagement`, `MemberManagement`, `BorrowManagement` chỉ cần gọi dùng, đảm bảo toàn bộ dự án đồng nhất 100% về định dạng ngày tháng, kiểm tra số điện thoại, mã định danh và chống crash.
  > 3. **Phân hệ Lưu trữ tập trung (FileManager)**: Em đảm bảo toàn bộ việc đọc/ghi file `Books.txt`, `Members.txt`, `BorrowRecords.txt` và xuất `Receipt` đều đi qua lớp `FileManager` với chuẩn UTF-8 thống nhất.
  > 4. **Tổng chỉ huy điều phối (`Program.java`)**: Em khởi tạo Dependency Injection, kết nối luồng nghiệp vụ giữa các module, xây dựng Dashboard quản trị và xuất Báo cáo toàn thư viện ạ."

---

### ❓ Câu 27: "Dữ liệu của hệ thống được lưu ở đâu? Trong RAM hay trên đĩa? Phân tích ưu nhược điểm của mô hình lưu trữ này?"
* **Câu trả lời chuẩn 10 điểm**:
  > "Dạ thưa thầy, nhóm em áp dụng mô hình **In-Memory Cache kết hợp File Persistence**:
  > - **Trong lúc chạy**: Toàn bộ dữ liệu được nạp lên các cấu trúc dữ liệu `ArrayList<Book>`, `ArrayList<Member>`, `ArrayList<BorrowRecord>` nằm trên bộ nhớ **RAM**.
  >   - *Ưu điểm*: Tốc độ truy vấn, tìm kiếm, lọc và sắp xếp bằng Stream API đạt tốc độ nano-giây, cực kỳ mượt mà, không bị nghẽn cổ chai I/O đĩa.
  > - **Khi lưu trữ**: Dữ liệu được đồng bộ xuống file text dạng phân cách thanh đứng (`|`) trên **Ổ cứng (Disk)** qua `FileManager`.
  >   - *Thời điểm đồng bộ*: Đồng bộ khi người dùng chọn Menu 6 (Backup), Menu 0 (Exit), hoặc khi thực hiện các giao dịch làm thay đổi trạng thái tồn kho mượn - trả sách.
  >   - *Đánh giá*: Đây là mô hình kinh điển của các hệ quản trị CSDL In-Memory như Redis, cực kỳ phù hợp với quy mô phần mềm quản lý vừa và nhỏ ạ."

---

# PHẦN 6: KỊCH BẢN THẦY YÊU CẦU SỬA/VIẾT CODE TRỰC TIẾP (LIVE CODING DEFENSE)

### 💻 Tình huống 1: "Bây giờ thầy muốn em viết thêm một hàm kiểm tra Email hợp lệ `isValidEmail(String email)` trong `DataValidation.java`, em viết thế nào?"
* **Code viết ngay tại chỗ**:
  ```java
  // Thêm vào DataValidation.java (ngay dưới isValidPhone)
  public static boolean isValidEmail(String email) {
      if (checkStringEmpty(email)) {
          return false;
      }
      String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
      return checkStringWithFormat(email, emailRegex);
  }
  ```
* **Giải thích nhanh cho thầy**:
  > "Dạ thưa thầy, em tái sử dụng hàm `checkStringWithFormat` có sẵn ở dòng 23. Regex đảm bảo có tiền tố ký tự trước dấu `@`, theo sau là tên miền và đuôi domain có ít nhất 2 chữ số (ví dụ `.com`, `.vn`, `.edu.vn`) ạ."

---

### 💻 Tình huống 2: "Trong `DataInput.java`, hãy viết thêm hàm cho phép nhập số nguyên có giá trị mặc định: nếu người dùng ấn Enter thì trả về giá trị mặc định đó?"
* **Code viết ngay tại chỗ**:
  ```java
  // Thêm vào DataInput.java
  public static int getIntegerWithDefault(String prompt, int defaultValue) {
      while (true) {
          String input = getString(prompt + " [Default: " + defaultValue + "]: ");
          if (input.isEmpty()) {
              return defaultValue;
          }
          try {
              return Integer.parseInt(input);
          } catch (NumberFormatException e) {
              System.out.println(">> Error: Please enter a valid integer or press Enter to keep default!");
          }
      }
  }
  ```
* **Giải thích nhanh cho thầy**:
  > "Dạ thưa thầy, hàm này giúp tối ưu trải nghiệm người dùng khi cập nhật số lượng sách tồn kho: nếu không muốn sửa thì chỉ việc bấm Enter để lấy `defaultValue` ạ."

---

### 💻 Tình huống 3: "Thầy không thích ký tự vẽ biểu đồ là dấu `=`. Thầy muốn đổi sang ký tự khối `█` hoặc `#`, em sửa dòng nào trong `Menu.java`?"
* **Chỉ số dòng và trả lời ngay**:
  > "Dạ thưa thầy, em mở file `Menu.java`, nhìn vào **dòng 78**:
  > Hiện tại code đang là:
  > ```java
  > String bar = repeat("=", barLength);
  > ```
  > Em chỉ cần thay dấu `"="` thành `"#"` hoặc ký tự Unicode `"█"`:
  > ```java
  > String bar = repeat("█", barLength);
  > ```
  > Là toàn bộ biểu đồ ASCII trên Dashboard sẽ tự động hiển thị dạng cột khối đặc cực kỳ trực quan và hiện đại ngay lập tức ạ!"
