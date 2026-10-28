# 📋 SỔ TAY NGHIỆP VỤ & QUYẾT ĐỊNH THIẾT KẾ HÀM (MEMBER 4)
## Dành riêng cho buổi Vấn đáp Đồ án PRO192 — Trọng số câu hỏi: ~20%
**Người phụ trách:** Nguyễn Trí Dũng (Member 4 - Integration, Utilities & Architecture)  
**Phạm vi:** Lý giải tại sao lại thiết kế hàm như vậy, tại sao tách/gộp hàm, và luồng vận hành dữ liệu.

---

## 📑 MỤC LỤC
1. [Tại sao tách/gộp các hàm nhập liệu trong DataInput](#1-tại-sao-táchgộp-các-hàm-nhập-liệu-trong-datainput)
2. [Tại sao tách riêng DataInput và DataValidation](#2-tại-sao-tách-riêng-datainput-và-datavalidation)
3. [Quyết định thiết kế Menu dạng chuỗi phân cách và Biểu đồ ASCII](#3-quyết-định-thiết-kế-menu-dạng-chuỗi-phân-cách-và-biểu-đồ-ascii)
4. [Chiến lược lưu trữ: RAM-First kết hợp Disk Persistence](#4-chiến-lược-lưu-trữ-ram-first-kết-hợp-disk-persistence)
5. [Luồng dữ liệu nghiệp vụ toàn hệ thống (End-to-End Data Flow)](#5-luồng-dữ-liệu-nghiệp-vụ-toàn-hệ-thống-end-to-end-data-flow)

---

# 1. TẠI SAO TÁCH/GỘP CÁC HÀM NHẬP LIỆU TRONG `DataInput`

### ❓ Câu 1: Tại sao không gộp `getString` và `getNonEmptyString` lại làm một hàm duy nhất?
* **Bối cảnh thầy hỏi:** *"Tôi thấy `getString` và `getNonEmptyString` cùng đọc một chuỗi từ bàn phím, tại sao không bắt buộc người dùng luôn luôn phải nhập chữ, bỏ `getString` đi cho đỡ thừa?"*
* 🎯 **Câu trả lời chuẩn nghiệp vụ:**
  * **Hai hàm này phục vụ 2 nghiệp vụ hoàn toàn khác nhau trong dự án:**
    1. **Khi Thêm mới (Add New):** Bắt buộc phải dùng `getNonEmptyString` vì các trường định danh như `Book ID`, `Title`, `Member Name` là thông tin sống còn, **tuyệt đối không được phép để rỗng**.
    2. **Khi Cập nhật thông tin (Update):** Bắt buộc phải dùng `getString` vì nghiệp vụ cập nhật thông minh cho phép người dùng: **"Nhấn Enter để giữ nguyên giá trị cũ nếu không muốn thay đổi trường này"**.
  * 👉 **Hệ quả nếu gộp làm một:** Nếu gộp lại và ép không được rỗng, người dùng khi muốn sửa 1 thông tin duy nhất (ví dụ chỉ sửa số điện thoại của Member) sẽ bị hệ thống bắt buộc phải gõ lại từ đầu tất cả các thông tin cũ (Tên, Địa chỉ, Email...), gây ức chế và trải nghiệm người dùng cực kỳ tồi tệ.

---

### ❓ Câu 2: Tại sao lại tạo 2 hàm `getIntegerNumber(prompt)` và `getIntegerNumber(prompt, min, max)`?
* **Bối cảnh thầy hỏi:** *"Tại sao lại có 2 hàm trùng tên nhau như thế này? Có thể gộp lại được không?"*
* 🎯 **Câu trả lời chuẩn nghiệp vụ & kỹ thuật:**
  * Đây là kỹ thuật **Method Overloading (Nạp chồng phương thức)** trong OOP:
    - Hàm `getIntegerNumber(prompt)`: Dùng khi người dùng cần nhập số nguyên bất kỳ không giới hạn cận (ví dụ: Số lượng nhập kho ban đầu, Mã số tự do).
    - Hàm `getIntegerNumber(prompt, min, max)`: Dùng khi cần kiểm soát lựa chọn nằm trong khoảng cố định (ví dụ: Chọn chức năng menu từ `[0 - 6]`, hoặc nhập số tháng từ `[1 - 12]`).
  * **Hàm 2 tái sử dụng trực tiếp hàm 1 (Dòng 42):**
    ```java
    int number = getIntegerNumber(prompt); // Tận dụng chống lỗi nhập chữ
    if (DataValidation.checkNumberInMinMax(number, min, max)) return number;
    ```
    Cách làm này giúp code không bị lặp lại (DRY - Don't Repeat Yourself), hàm sau chỉ làm thêm nhiệm vụ chặn miền giá trị.

---

### ❓ Câu 3: Tại sao hàm `getDate` lại trả về `null` khi chuỗi nhập rỗng?
* **Dòng code:** `Utilities/DataInput.java` dòng 74-76:
  ```java
  if (input.isEmpty()) {
      return null;
  }
  ```
* 🎯 **Câu trả lời chuẩn nghiệp vụ:**
  * Cũng xuất phát từ nghiệp vụ **Cập nhật dữ liệu (Update)**: Nếu thủ thư cập nhật thông tin thẻ thành viên hoặc sách nhưng không muốn thay đổi ngày cấp thẻ (`joinDate`), họ chỉ việc bấm **Enter**.
  * Hàm trả về `null`, tầng nghiệp vụ `MemberManagement` nhận được `null` sẽ biết là người dùng không muốn đổi ngày và tự động giữ nguyên ngày cũ trong đối tượng.

---

### ❓ Câu 4: Tại sao hàm `getBoolean` lại hỗ trợ cả `Y/N` lẫn `YES/NO` và gọi `.toUpperCase()`?
* **Dòng code:** `Utilities/DataInput.java` dòng 87:
  ```java
  String input = getString(prompt + " (Y/N): ").toUpperCase();
  ```
* 🎯 **Câu trả lời:**
  * **Thân thiện với người dùng (User Experience - UX):** Người dùng có thể gõ chữ thường `y`, `yes`, `n`, `no` hoặc chữ hoa `Y`, `YES`.
  * Hàm tự động chuyển thành chữ hoa bằng `.toUpperCase()` trước khi so sánh, giúp người dùng không bao giờ bị báo lỗi oan chỉ vì quên bật/tắt phím CapsLock.

---

# 2. TẠI SAO TÁCH RIÊNG `DataInput` VÀ `DataValidation`

### ❓ Câu 5: Tại sao không viết các hàm kiểm tra regex số điện thoại, kiểm tra rỗng trực tiếp vào trong `DataInput` luôn mà phải tách ra lớp `DataValidation`?
* 🎯 **Câu trả lời chuẩn kiến trúc:**
  * **Tuân thủ nguyên lý Đơn trách nhiệm (Single Responsibility Principle - chữ S trong SOLID):**
    - `DataInput`: Chuyên trách giao tiếp với phần cứng bàn phím (`System.in`), giải quyết bài toán chống trôi lệnh, ép kiểu dữ liệu cơ bản từ chuỗi sang `int`, `double`, `LocalDate`.
    - `DataValidation`: Chuyên trách kiểm tra logic và định dạng nghiệp vụ (Regex số điện thoại Việt Nam `0\\d{9}`, Regex mã định danh `B-\\d{3,}`, kiểm tra chuỗi rỗng, kiểm tra khoảng số).
  * **Khả năng tái sử dụng (Reusability):**
    - Không chỉ tầng Nhập liệu (`DataInput`) cần kiểm tra, mà tầng Đọc file (`MemberFileHandler`, `BookDAO`) khi đọc các dòng text từ file đĩa lên cũng có thể gọi trực tiếp `DataValidation.checkStringEmpty()` để kiểm tra tính toàn vẹn của dữ liệu file mà không cần phụ thuộc vào bàn phím.

---

# 3. QUYẾT ĐỊNH THIẾT KẾ MENU CHUỖI PHÂN CÁCH VÀ BIỂU ĐỒ ASCII

### ❓ Câu 6: Tại sao phương thức `Menu.print` lại dùng chuỗi phân cách dấu `|` thay vì truyền vào một mảng `String[]` hay `List<String>`?
* **Dòng code:** `Utilities/Menu.java` dòng 13-25 và `BusinessObject/UI/Program.java` dòng 80-88:
  ```java
  Menu.print(
      "  1. Book & Inventory Management    (Polymorphism, Reservation)|" +
      "  2. Library Member Management      (Tiers VIP/GOLD/SILVER, Points)|" +
      ...
      "  0. Save Data & Exit Program"
  );
  ```
* 🎯 **Câu trả lời:**
  * **Tối ưu hóa mã nguồn khi gọi hàm (Call-site Simplicity):**
    - Thay vì phải khởi tạo mảng phức tạp `new String[]{"1. ...", "2. ..."}`, người viết menu chỉ cần viết 1 chuỗi liên tục, trực quan, dễ đọc như một đoạn văn bản.
  * **Nhận diện thông minh câu nhắc (Smart Prompt Detection):**
    - Ở dòng 19-20: Nếu mục menu kết thúc bằng dấu `:` hoặc `?`, hàm `Menu.print` tự động nhận biết đây là câu nhắc nhập liệu và dùng `System.out.print` (không xuống dòng) để con trỏ dừng ngay sau câu hỏi. Còn các mục danh sách thì tự động `System.out.println` xuống dòng.

---

### ❓ Câu 7: Mục đích của hàm vẽ biểu đồ ASCII `printAsciiBarChart` trong `Menu.java` là gì?
* **Dòng code:** `Utilities/Menu.java` dòng 50-83.
* 🎯 **Câu trả lời:**
  * Vì đồ án PRO192 chạy trên nền tảng **Console (Dòng lệnh)**, không có giao diện đồ họa GUI (như JavaFX hay Swing).
  * Hàm này tự động tính toán giá trị lớn nhất (`maxVal`) của kho sách, chia tỷ lệ thanh đồ thị (tối đa 35 ký tự `=`), và in ra biểu đồ cột trực quan:
    ```
    | [B-001] Clean Code        | ===================================   15 copies     |
    | [B-002] Design Patterns   | =======================               10 copies     |
    ```
  * Giúp thủ thư có cái nhìn trực quan, lập tức biết được đầu sách nào đang tồn kho nhiều, đầu sách nào sắp hết để nhập thêm.

---

# 4. CHIẾN LƯỢC LƯU TRỮ: RAM-FIRST KẾT HỢP DISK PERSISTENCE

### ❓ Câu 8: Hãy giải thích cơ chế lưu trữ dữ liệu của đồ án: Dữ liệu được đọc và ghi vào lúc nào?
* 🎯 **Câu trả lời chuẩn cơ chế:**
  * Hệ thống áp dụng mô hình **RAM-First In-Memory Processing kết hợp Disk Persistence (Lưu đĩa bền vững)**:
    1. **Khi khởi động (`Program.java` khởi tạo DAO):**
       - Lớp DAO gọi `FileManager.readLines()` để đọc toàn bộ dữ liệu từ các file `Books.txt`, `Members.txt`, `BorrowRecords.txt` vào các `ArrayList` lưu trữ trên bộ nhớ RAM.
    2. **Trong quá trình người dùng thao tác (Thêm, Sửa, Xóa, Mượn, Trả):**
       - Toàn bộ thao tác diễn ra **trực tiếp 100% trên RAM** (tìm kiếm qua `Stream`, cập nhật thuộc tính đối tượng trên `List`).
       - 👉 *Lợi ích:* Tốc độ phản hồi cực nhanh (micro-giây), không bị trễ thời gian đọc/ghi đĩa I/O, không làm hao mòn ổ cứng.
    3. **Khi lưu trữ dữ liệu:**
       - Dữ liệu trên RAM chỉ được ghi ngược lại file đĩa khi:
         - Người dùng chủ động chọn **Menu 6 (Backup All Data to Files)**.
         - Hoặc khi người dùng chọn **Menu 0 (Save Data & Exit Program)** trước khi thoát.

---

# 5. LUỒNG DỮ LIỆU NGHIỆP VỤ TOÀN HỆ THỐNG (END-TO-END DATA FLOW)

### ❓ Câu 9: Trình bày luồng đi của dữ liệu từ khi người dùng thao tác cho đến khi dữ liệu được ghi xuống file?
* 🎯 **Câu trả lời:**
  Luồng thực thi đi qua 4 tầng kiến trúc rõ ràng:
  1. **Tầng Điều phối (Main UI - `Program.java`):**
     - Nhận lựa chọn menu của người dùng thông qua `DataInput.getIntegerNumber()`.
     - Điều hướng (Dispatch) gọi phương thức tương ứng của các Controller phân hệ (ví dụ: `bookMenu.processMenuForBook()`).
  2. **Tầng Giao diện Phân hệ (Subsystem UI - `BookManagement.java`):**
     - Hiển thị menu chi tiết của sách.
     - Dùng `DataInput` để lấy dữ liệu từ bàn phím và gọi `DataValidation` để kiểm tra tính hợp lệ.
     - Đóng gói dữ liệu thành đối tượng thực thể (`Book`, `TextBook` hoặc `ReferenceBook`).
  3. **Tầng Xử lý Nghiệp vụ & Dữ liệu (DAO Layer - `BookDAO.java`):**
     - Nhận đối tượng từ UI, thực hiện các quy tắc nghiệp vụ (Kiểm tra trùng mã sách, trừ số lượng tồn kho khi mượn).
     - Thêm/cập nhật đối tượng vào danh sách `List<Book>` trên RAM.
  4. **Tầng Quản lý File (File I/O Layer - `FileManager.java`):**
     - Khi có lệnh lưu (`saveToFile()`), DAO chuyển đổi từng đối tượng thành các dòng văn bản chuẩn hóa.
     - Gọi `FileManager.writeLines()` để ghi toàn bộ danh sách dòng xuống file vật lý `Books.txt` với chuẩn mã hóa `UTF-8`.
