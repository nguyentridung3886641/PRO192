# 🏛️ SỔ TAY 4 TRỤ CỘT OOP & SOLID (MEMBER 4)
## Dành riêng cho câu hỏi Phân loại Điểm 9 - 10 trong buổi Vấn đáp PRO192
**Người phụ trách:** Nguyễn Trí Dũng (Member 4 - Integration, Utilities & Architecture)  
**Phạm vi:** Trả lời trực tiếp vào 4 trụ cột OOP, nguyên lý SOLID và Design Pattern với số dòng code minh chứng cụ thể.

---

## 📑 MỤC LỤC
1. [Bốn trụ cột OOP được thể hiện ở đâu trong Milestone 4](#1-bốn-trụ-cột-oop-được-thể-hiện-ở-đâu-trong-milestone-4)
2. [Nguyên lý SOLID trong thiết kế của Member 4](#2-nguyên-lý-solid-trong-thiết-kế-của-member-4)
3. [Các mẫu thiết kế (Design Patterns) đã vận dụng](#3-các-mẫu-thiết-kế-design-patterns-đã-vận-dụng)

---

# 1. BỐN TRỤ CỘT OOP ĐƯỢC THỂ HIỆN Ở ĐÂU TRONG MILESTONE 4

### 💎 Trụ cột 1: Encapsulation (Tính đóng gói)
* **Bản chất:** Giấu kín trạng thái nội tại của đối tượng, chỉ cho phép tương tác thông qua các phương thức công khai được kiểm soát.
* **Minh chứng dòng code trong Milestone 4:**
  1. **Trong `FileManager.java` (dòng 14, 27-29):**
     - Thuộc tính `private String fileName;` được bảo vệ chặt chẽ, bên ngoài muốn thay đổi phải gọi qua `setFileName(String fileName)`.
  2. **Trong `DataInput.java` (dòng 10):**
     - Thuộc tính `private static final Scanner scanner;` được ẩn hoàn toàn. Bên ngoài không thể can thiệp hay đóng nhầm luồng `scanner`, chỉ được dùng các hàm nhập tiện ích do lớp cung cấp.
  3. **Trong `DataValidation.java` (dòng 8-9) và `Menu.java` (dòng 10-11):**
     - **Private Constructor:** 
       ```java
       private DataValidation() {}
       private Menu() {}
       ```
       Đóng gói toàn bộ khả năng khởi tạo đối tượng của lớp tiện ích (Utility Class), ngăn chặn lập trình viên khác vô tình khởi tạo đối tượng thừa thãi trong bộ nhớ.

---

### 💎 Trụ cột 2: Abstraction (Tính trừu tượng)
* **Bản chất:** Tập trung vào đối tượng làm được gì (hành vi) thay vì làm như thế nào (chi tiết cài đặt cụ thể).
* **Minh chứng dòng code trong Milestone 4:**
  - **Trong `Program.java` (dòng 23-25):**
    ```java
    23: IBookDAO bookDAO = new BookDAO();
    24: IMemberDAO memberDAO = new MemberDAO();
    25: IBorrowDAO borrowDAO = new BorrowDAO(memberDAO, bookDAO);
    ```
  - Lớp `Program` hoàn toàn không quan tâm bên dưới `BookDAO` đọc ghi file `.txt` bằng `Files.lines` hay lưu vào SQL Database. `Program` chỉ giao tiếp thông qua giao diện trừu tượng `IBookDAO` với các hành vi nghiệp vụ cốt lõi: `getBookList()`, `getTotalQuantity()`, `calculateTotalInventoryValue()`.

---

### 💎 Trụ cột 3: Inheritance (Tính kế thừa)
* **Bản chất:** Cho phép lớp con kế thừa lại các thuộc tính và phương thức của lớp cha, mở rộng thêm các thuộc tính đặc thù mà không phải viết lại mã nguồn.
* **Minh chứng cách Milestone 4 kết nối:**
  - `Program.java` quản lý danh sách tổng quát `List<Book>`, nhưng thực tế bên trong chứa cả 2 lớp con kế thừa từ `Book`:
    - `TextBook` (kế thừa `Book`, bổ sung thêm thuộc tính `status` - tình trạng sách mới/cũ).
    - `ReferenceBook` (kế thừa `Book`, bổ sung thêm thuộc tính `tax` - thuế bản quyền).
  - Tương tự, `Member` kế thừa từ lớp cha `User` (kế thừa `userId`, `name`, `phone` và mở rộng thêm hạng thẻ `membershipTier`, điểm thưởng `points`).

---

### 💎 Trụ cột 4: Polymorphism (Tính đa hình)
* **Bản chất:** Một hành vi có thể thể hiện dưới nhiều hình thái khác nhau tùy thuộc vào đối tượng thực thi.
* **Minh chứng dòng code trong Milestone 4:**
  1. **Đa hình lúc Biên dịch (Compile-time Polymorphism / Method Overloading):**
     - Trong `DataInput.java` (dòng 29 & dòng 40): Cùng là phương thức `getIntegerNumber`, nhưng một hàm nhận 1 tham số `(String prompt)` và một hàm nhận 3 tham số `(String prompt, int min, int max)`. Trình biên dịch sẽ tự động chọn đúng hàm dựa vào số lượng đối số truyền vào.
  2. **Đa hình lúc Chạy (Runtime Polymorphism / Dynamic Binding):**
     - Khai báo kiểu Interface `IBookDAO` nhưng khi chạy thực tế lại thực thi mã nguồn của lớp cài đặt `BookDAO`.
     - Khi `Program` hoặc `BookManagement` in danh sách sách (`System.out.println(book)`), phương thức `toString()` của `TextBook` hoặc `ReferenceBook` sẽ tự động được gọi tương ứng tại thời điểm chạy (Late Binding).

---

# 2. NGUYÊN LÝ SOLID TRONG THIẾT KẾ CỦA MEMBER 4

Khi thầy hỏi: *"Em hiểu thế nào về SOLID và áp dụng vào phần code của em như thế nào?"*, hãy trả lời tự tin 3 nguyên lý nổi bật nhất:

### 🔹 S - Single Responsibility Principle (Nguyên lý Đơn trách nhiệm)
> *"Mỗi lớp chỉ nên có một lý do duy nhất để thay đổi."*

* **Minh chứng thực tế:**
  * **`DataInput`**: Chỉ có 1 lý do thay đổi là khi thay đổi cách giao tiếp với bàn phím của người dùng (`System.in`).
  * **`DataValidation`**: Chỉ có 1 lý do thay đổi là khi các quy tắc nghiệp vụ/định dạng thay đổi (ví dụ: đổi định dạng số điện thoại từ 10 số sang 11 số).
  * **`FileManager`**: Chỉ có 1 lý do thay đổi là khi thay đổi cơ chế đọc/ghi file vật lý trên ổ đĩa (ví dụ đổi charset hoặc cách ghi file).
  * **`Program`**: Chỉ làm nhiệm vụ điều phối menu chính, không trực tiếp xử lý nghiệp vụ hay I/O bàn phím.

---

### 🔹 O - Open/Closed Principle (Nguyên lý Đóng / Mở)
> *"Mở rộng để phát triển thêm tính năng, nhưng Đóng đối với việc sửa đổi mã nguồn sẵn có."*

* **Minh chứng thực tế:**
  * **Trong `Menu.print(menuString)`:**
    Khi cần bổ sung thêm chức năng số 7 vào Menu chính của `Program.java`, ta chỉ cần thêm chuỗi `"|  7. New Feature..."` vào chuỗi tham số mà **hoàn toàn không cần phải sửa đổi bất kỳ dòng code nào bên trong phương thức `Menu.print()`**.

---

### 🔹 D - Dependency Inversion Principle (Nguyên lý Đảo ngược phụ thuộc)
> *"Các module cấp cao không nên phụ thuộc trực tiếp vào module cấp thấp. Cả hai nên phụ thuộc vào sự trừu tượng (Abstraction / Interface)."*

* **Minh chứng thực tế trong `Program.java` (dòng 23-25):**
  * `Program` là module cấp cao (điều khiển toàn bộ ứng dụng), không phụ thuộc cứng vào `BookDAO` cụ thể.
  * Nếu sau này trường yêu cầu đồ án phải nộp cơ sở dữ liệu MySQL, ta chỉ việc tạo class `BookDAOMySQL implements IBookDAO` và đổi 1 dòng duy nhất tại `Program.java`:
    ```java
    IBookDAO bookDAO = new BookDAOMySQL();
    ```
    Toàn bộ mã nguồn còn lại của `Program.java` giữ nguyên vẹn 100%.

---

# 3. CÁC MẪU THIẾT KẾ (DESIGN PATTERNS) ĐÃ VẬN DỤNG

### 🎯 1. Facade Pattern (Mẫu thiết kế Mặt tiền)
* **Vị trí áp dụng:** `Program.java`
* **Ý nghĩa:** Hệ thống quản lý thư viện bên dưới rất phức tạp, bao gồm 3 phân hệ độc lập: Quản lý Sách (`BookDAO`), Quản lý Độc giả (`MemberDAO`), và Quản lý Mượn trả (`BorrowDAO`). 
* Lớp `Program` hoạt động như một **Mặt tiền (Facade)** duy nhất: cung cấp một giao diện điều khiển đơn giản (Menu 0-6), giúp người dùng tương tác với toàn bộ hệ thống mà không cần biết sự phức tạp kết nối bên dưới.

### 🎯 2. Data Access Object (DAO Pattern)
* **Vị trí áp dụng:** `FileManager.java` kết hợp với `BookDAO`, `MemberDAO`, `BorrowDAO`.
* **Ý nghĩa:** Tách rời hoàn toàn tầng Lưu trữ dữ liệu (Persistence Layer) ra khỏi tầng Giao diện người dùng (UI Layer). Tầng UI không cần quan tâm dữ liệu được ghi vào file `.txt` ra sao, định dạng phân tách bằng dấu `,` hay `|`, tất cả đã được đóng gói trong tầng DAO.
