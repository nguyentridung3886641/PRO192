# 📚 SYSTEM CLASS & METHOD CATALOG (API SPECIFICATION)
## Project: Library Management System — Course: PRO192 (OOP with Java)
**Group:** Group 4 | **Class:** SE2111  
**Member Responsibilities:**
- **Member 1 (Leader):** Trần Lý Việt Tùng (`SE210308`) — Phân hệ Thành viên & Quản lý Thẻ đọc (`User`, `Member`, `IMemberDAO`, `MemberDAO`, `MemberFileHandler`, `MemberManagement`)
- **Member 2:** Nguyễn Thiên Tuệ (`SE210176`) — Phân hệ Sách & Đa hình (`Book`, `TextBook`, `ReferenceBook`, `IBookDAO`, `BookDAO`, `BookManagement`)
- **Member 3:** Thiều Quang Tuấn (`SE210531`) — Phân hệ Mượn Trả & Hóa đơn (`BorrowRecord`, `IBorrowDAO`, `BorrowDAO`, `BorrowManagement`)
- **Member 4:** Nguyễn Trí Dũng (`SE210456`) — Hạ tầng, Báo cáo & Tiện ích (`FileManager`, `DataInput`, `DataValidation`, `Menu`, `Program`)

---

## 📑 MỤC LỤC NHANH THEO PACKAGE
1. [Package: `Core.Entities` (Domain Models)](#1-package-coreentities)
   - [`User` (Abstract Class)](#11-class-coreentitiesuser-abstract)
   - [`Member` (Inherits User)](#12-class-coreentitiesmember-extends-user)
   - [`Book` (Abstract Class)](#13-class-coreentitiesbook-abstract)
   - [`TextBook` (Inherits Book)](#14-class-coreentitiestextbook-extends-book)
   - [`ReferenceBook` (Inherits Book)](#15-class-coreentitiesreferencebook-extends-book)
   - [`BorrowRecord` (Aggregation Has-A)](#16-class-coreentitiesborrowrecord)
2. [Package: `DataObjects.DAO` (Data Access Object Layer)](#2-package-dataobjectsdao)
   - [`IBookDAO` (Interface) & `BookDAO`](#21-ibookdao-interface--bookdao-implementation)
   - [`IMemberDAO` (Interface) & `MemberDAO`](#22-imemberdao-interface--memberdao-implementation)
   - [`IBorrowDAO` (Interface) & `BorrowDAO`](#23-iborrowdao-interface--borrowdao-implementation)
   - [`FileManager` & `MemberFileHandler`](#24-filemanager--memberfilehandler)
3. [Package: `BusinessObject.UI` (Controllers & Views)](#3-package-businessobjectui)
   - [`BookManagement`](#31-class-businessobjectuibookmanagement)
   - [`MemberManagement`](#32-class-businessobjectuimembermanagement)
   - [`BorrowManagement`](#33-class-businessobjectuiborrowmanagement)
   - [`Program` (Main Entry Point)](#34-class-businessobjectuiprogram)
4. [Package: `Utilities` (Helper & Validation Tools)](#4-package-utilities)
   - [`DataInput`](#41-class-utilitiesdatainput)
   - [`DataValidation`](#42-class-utilitiesdatavalidation)
   - [`Menu`](#43-class-utilitiesmenu)

---

## 1. PACKAGE: `Core.Entities`

### 1.1. Class `Core.Entities.User` (Abstract)
- **Mô tả:** Lớp cha trừu tượng đại diện cho thông tin định danh người dùng trong hệ sinh thái thư viện (tính trừu tượng & bao đóng).
- **Thuộc tính (Fields):**
  - `protected String userId`
  - `protected String fullName`
  - `protected String phone`
  - `protected LocalDate dateOfBirth`
  - `public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")`

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `User(...)` | `String userId, String fullName, String phone, LocalDate dateOfBirth` | Constructor | Khởi tạo thuộc tính, kiểm tra hợp lệ |
| `getUserId()` | Không | `String` | Getter mã người dùng |
| `setUserId(String)` | `String userId` | `void` | Setter kiểm tra không rỗng |
| `getFullName()` | Không | `String` | Getter họ và tên |
| `setFullName(String)` | `String fullName` | `void` | Setter kiểm tra không rỗng |
| `getPhone()` | Không | `String` | Getter số điện thoại |
| `setPhone(String)` | `String phone` | `void` | Setter kiểm tra 9-11 chữ số |
| `getDateOfBirth()` | Không | `LocalDate` | Getter ngày sinh (Java 8 Time API) |
| `setDateOfBirth(LocalDate)` | `LocalDate dateOfBirth` | `void` | Setter kiểm tra ngày sinh không ở tương lai |
| `toString()` | Không | `String` | **@Override** định dạng chuỗi hiển thị ID, tên, SĐT, ngày sinh |

---

### 1.2. Class `Core.Entities.Member` (Extends `User`)
- **Mô tả:** Đại diện độc giả/thành viên thư viện. Kế thừa `User`, tích hợp hệ thống chấm điểm uy tín (*Reputation Points*) và phân hạng hội viên tự động (*VIP, GOLD, SILVER*).
- **Thuộc tính bổ sung:**
  - `private LocalDate registrationDate`
  - `private boolean active` (Trạng thái thẻ: `true` = Active, `false` = Locked)
  - `private int reputationPoints` (Mặc định: 50 điểm)

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `Member(...)` | `5 tham số` | Constructor | Chaining: mặc định active = true, points = 50 |
| `Member(...)` | `6 tham số` | Constructor | Chaining: tùy biến active, points = 50 |
| `Member(...)` | `7 tham số` | Constructor | Khởi tạo đầy đủ, gọi `super(...)` nạp User |
| `getRegistrationDate()` | Không | `LocalDate` | Getter ngày đăng ký thẻ |
| `setRegistrationDate(LocalDate)` | `LocalDate` | `void` | Setter ngày đăng ký |
| `isActive()` | Không | `boolean` | Getter trạng thái hoạt động của thẻ |
| `setActive(boolean)` | `boolean active` | `void` | Setter trạng thái kích hoạt / khóa thẻ |
| `getReputationPoints()` | Không | `int` | Getter điểm uy tín |
| `setReputationPoints(int)` | `int points` | `void` | Cập nhật điểm, tự động khóa thẻ nếu điểm < 0 |
| `addReputationPoints(int)` | `int delta` | `void` | Cộng/trừ điểm và auto-lock nếu < 0 |
| `getMembershipTier()` | Không | `String` | Phân hạng: `>=100: VIP`, `60-99: GOLD`, `<60: SILVER` |
| `getMaxBorrowLimit()` | Không | `int` | Hạn mức mượn sách: `VIP: 10`, `GOLD: 7`, `SILVER: 5` |
| `getDiscountRate()` | Không | `double` | Tỉ lệ giảm phí: `VIP: 0.25 (25%)`, `GOLD: 0.10 (10%)`, `SILVER: 0.0` |
| `getStatusText()` | Không | `String` | Trả về `"Active"` hoặc `"Locked"` |
| `toFileString()` | Không | `String` | Xuất chuỗi định dạng lưu file `Members.txt` |
| `toString()` | Không | `String` | **@Override** hiển thị thông tin kèm hạng thẻ và điểm uy tín |

---

### 1.3. Class `Core.Entities.Book` (Abstract)
- **Mô tả:** Lớp cha trừu tượng cho tất cả sách và tài liệu trong thư viện. Định nghĩa các phương thức đa hình tính phí mượn, giá trị lưu kho, cùng hàng đợi đặt trước FIFO (*Reservation Queue*) khi hết hàng.
- **Thuộc tính:**
  - `protected String bookId`, `protected String title`, `protected String author`
  - `protected double price`, `protected int quantity`
  - `protected List<String> reservationQueue` (Hàng đợi đặt trước sách FIFO)

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `Book(...)` | `5 tham số` | Constructor | Khởi tạo đầy đủ thuộc tính chính thức |
| `Book(...)` | `4 tham số` | Constructor | **Constructor Overloading**: gán author = "Various Authors" |
| `getBookId()` / `setBookId(String)` | Getter/Setter | `String` / `void` | Đóng gói mã sách |
| `getTitle()` / `setTitle(String)` | Getter/Setter | `String` / `void` | Đóng gói tiêu đề sách |
| `getAuthor()` / `setAuthor(String)` | Getter/Setter | `String` / `void` | Đóng gói tên tác giả |
| `getPrice()` / `setPrice(double)` | Getter/Setter | `double` / `void` | Đóng gói giá bìa niêm yết |
| `getQuantity()` / `setQuantity(int)`| Getter/Setter | `int` / `void` | Đóng gói số lượng tồn kho |
| `getReservationQueue()` | Không | `List<String>` | Getter danh sách độc giả chờ trong hàng đợi |
| `addReservation(String memberId)` | `String memberId` | `boolean` | Đăng ký độc giả vào hàng đợi đặt trước (Stream anyMatch chống trùng) |
| `popNextReservation()` | Không | `String` | Lấy và gỡ độc giả đầu tiên theo cơ chế FIFO |
| `calculatePrice()` | Không | `abstract double` | **Phương thức đa hình trừu tượng** (tính giá bìa sau ưu đãi/thuế) |
| `calculateBorrowFee(int days)` | `int days` | `abstract double` | **Phương thức đa hình trừu tượng** (tính phí mượn theo ngày) |
| `calculateTotalValue()` | Không | `abstract double` | **Phương thức đa hình trừu tượng** (tính tổng giá trị kho) |
| `toString()` | Không | `String` | **@Override** chuỗi hiển thị ID, Title, Author, Price, Qty |

---

### 1.4. Class `Core.Entities.TextBook` (Extends `Book`)
- **Mô tả:** Sách giáo trình, kế thừa `Book`. Ưu đãi giảm 10% đơn giá hỗ trợ học sinh - sinh viên.
- **Thuộc tính riêng:** `private String genre` (Chuyên ngành: Information Technology, Computer Science, ...)

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `TextBook(...)` | `6 tham số` | Constructor | Gọi `super(...)` nạp Book, gán genre |
| `TextBook(...)` | `5 tham số` | Constructor | **Constructor Overloading** (Constructor Chaining) |
| `getGenre()` / `setGenre(String)` | Getter/Setter | `String` / `void` | Đóng gói trường dữ liệu chuyên ngành |
| `calculatePrice()` | Không | `double` | **@Override Đa hình:** Giảm 10% giá bìa: `getPrice() * 0.90` |
| `calculateBorrowFee(int days)` | `int days` | `double` | **@Override Đa hình:** Phí mượn ưu đãi học tập `days * 2,000 VND` |
| `calculateTotalValue()` | Không | `double` | **@Override Đa hình:** Tổng giá trị kho: `getPrice() * getQuantity()` |
| `toString()` | Không | `String` | **@Override** in thông tin TextBook chi tiết |

---

### 1.5. Class `Core.Entities.ReferenceBook` (Extends `Book`)
- **Mô tả:** Sách tham khảo/chuyên khảo, kế thừa `Book`. Cộng thêm 10% thuế bản quyền/VAT, tổng trị giá kho cộng 5% phí bảo quản.
- **Thuộc tính riêng:** `private String publisher` (Nhà xuất bản: O'Reilly, Addison-Wesley, MIT Press, ...)

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `ReferenceBook(...)` | `6 tham số` | Constructor | Gọi `super(...)` nạp Book, gán publisher |
| `ReferenceBook(...)` | `5 tham số` | Constructor | **Constructor Overloading** (Constructor Chaining) |
| `getPublisher()` / `setPublisher(String)` | Getter/Setter | `String` / `void` | Đóng gói thông tin nhà xuất bản |
| `calculatePrice()` | Không | `double` | **@Override Đa hình:** Thuế bản quyền 10%: `getPrice() * 1.10` |
| `calculateBorrowFee(int days)` | `int days` | `double` | **@Override Đa hình:** Phí tài liệu nghiên cứu `days * 5,000 VND` |
| `calculateTotalValue()` | Không | `double` | **@Override Đa hình:** Giá trị kho: `(getPrice() * getQuantity()) * 1.05` |
| `toString()` | Không | `String` | **@Override** in thông tin ReferenceBook chi tiết |

---

### 1.6. Class `Core.Entities.BorrowRecord` (Aggregation: Has-A)
- **Mô tả:** Phiếu mượn - trả sách, kết tập thực thể `Member` và danh sách đa hình `List<Book>`. Xử lý gia hạn (+7 ngày), kiểm soát quá hạn và bồi thường hỏng/mất sách (150% giá bìa).
- **Thuộc tính:**
  - `private String recordId`, `private Member member`, `private List<Book> borrowedBooks`
  - `private LocalDate borrowDate`, `private LocalDate dueDate`, `private LocalDate returnDate`
  - `private boolean returned`, `private int extensionCount`, `private double compensationFee`

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `BorrowRecord(...)` | `5 tham số` | Constructor | Khởi tạo phiếu mượn mới (mặc định chưa trả) |
| `calculateTotalBorrowFee()` | Không | `double` | Stream API: tính tổng phí mượn gốc của các sách đa hình |
| `calculateDiscountedBorrowFee()` | Không | `double` | Phí mượn sau khi áp dụng tỉ lệ giảm giá hạng thẻ hội viên |
| `calculateFine()` | Không | `double` | Tiền phạt quá hạn: `5,000 VND * ngày quá hạn * số lượng sách` |
| `calculateFinalPayment()` | Không | `double` | Tổng thanh toán: `(Phí sau giảm) + Tiền phạt + Tiền bồi thường` |
| `extendDueDate(int days)` | `int days` | `boolean` | Gia hạn hạn trả (tối đa 2 lần, tối đa 7 ngày/lần, không áp dụng cho phiếu trễ) |
| `isOverdue()` | Không | `boolean` | Kiểm tra phiếu có đang bị quá hạn so với ngày hiện tại |
| `getOverdueDays()` | Không | `long` | Tính chính xác số ngày bị quá hạn bằng `ChronoUnit.DAYS` |
| `markAsReturned(LocalDate)` | `LocalDate` | `void` | Ghi nhận trả sách và thiết lập trạng thái returned = true |
| `toFileString()` | Không | `String` | Định dạng chuỗi lưu file `BorrowRecords.txt` |
| `toString()` | Không | `String` | **@Override** hiển thị chi tiết phiếu mượn |

---

## 2. PACKAGE: `DataObjects.DAO`

### 2.1. `IBookDAO` (Interface) & `BookDAO` (Implementation)
- **Mô tả:** Tầng truy xuất dữ liệu Sách. Quản lý danh sách trong bộ nhớ (`ArrayList<Book>`), tìm kiếm theo tiêu chí, sắp xếp theo giải thuật và đồng bộ lưu trữ file `Books.txt`.

| Phương thức | Tham số | Kiểu trả về | Ghi chú kỹ thuật & Stream API |
| :--- | :--- | :--- | :--- |
| `addBook(Book)` | `Book book` | `boolean` | Thêm sách mới, kiểm tra trùng ID |
| `updateBook(Book)` | `Book book` | `boolean` | Cập nhật thông tin sách theo Book ID |
| `deleteBook(String)` | `String bookId` | `boolean` | Xóa sách khỏi kho lưu trữ |
| `findBookById(String)` | `String bookId` | `Book` | Stream API: `filter -> findFirst -> orElse(null)` |
| `findBookByTitle(String)` | `String title` | `List<Book>` | Stream API: tìm kiếm tên sách (case-insensitive) |
| `findBookByAuthor(String)` | `String author` | `List<Book>` | Stream API: tìm kiếm theo tác giả |
| `findBookByPriceRange(min, max)`| `double, double` | `List<Book>` | Stream API: lọc theo khoảng giá niêm yết |
| `getBookList()` | Không | `List<Book>` | Trả về bản sao an toàn danh sách sách |
| `getTotalBooksCount()` | Không | `int` | Đếm tổng số đầu sách (`bookList.size()`) |
| `getTotalQuantity()` | Không | `int` | Stream API: `bookList.stream().mapToInt(Book::getQuantity).sum()` |
| `calculateTotalInventoryValue()` | Không | `double` | Stream API: `mapToDouble(Book::calculateTotalValue).sum()` (Đa hình) |
| `calculateTotalBorrowFee(List, days)`| `List<Book>, int` | `double` | Stream API: tính tổng phí mượn của danh sách sách |
| `getSortedByTitle(ascending)` | `boolean` | `List<Book>` | Stream API `.sorted(Comparator.comparing(Book::getTitle))` |
| `getSortedByAuthor(ascending)` | `boolean` | `List<Book>` | Stream API `.sorted(Comparator.comparing(Book::getAuthor))` |
| `getSortedByPrice(ascending)` | `boolean` | `List<Book>` | Stream API `.sorted(Comparator.comparingDouble(Book::getPrice))` |
| `getSortedByQuantity(ascending)` | `boolean` | `List<Book>` | Stream API `.sorted(Comparator.comparingInt(Book::getQuantity))` |
| `getSortedByTotalValue(ascending)` | `boolean` | `List<Book>` | Stream API sắp xếp theo tổng giá trị đa hình |
| `sortByPriceAlgorithm(ascending)` | `boolean` | `List<Book>` | **Giải thuật thủ công Selection Sort** kết hợp Lambda Comparator |
| `sortByTitleAlgorithm(ascending)` | `boolean` | `List<Book>` | **Giải thuật thủ công Insertion Sort** kết hợp Lambda Comparator |
| `reserveBook(bookId, memberId)` | `String, String` | `boolean` | Đặt chỗ trước vào hàng đợi FIFO khi sách hết hàng |
| `getBooksWithReservations()` | Không | `List<Book>` | Lấy danh sách các sách đang có hàng đợi đặt trước |
| `loadFromFile(filePath)` | `String` | `boolean` | Nạp dữ liệu từ `Books.txt` qua Stream pipeline |
| `saveToFile(filePath)` | `String` | `boolean` | Lưu danh sách sách ra `Books.txt` chuẩn UTF-8 |

---

### 2.2. `IMemberDAO` (Interface) & `MemberDAO` (Implementation)
- **Mô tả:** Tầng truy xuất dữ liệu Độc giả (`Member`). Hỗ trợ CRUD, phân loại hạng hội viên, bảo vệ toàn vẹn tài khoản và chống trùng mã qua HashSet.

| Phương thức | Tham số | Kiểu trả về | Ghi chú kỹ thuật & Stream API |
| :--- | :--- | :--- | :--- |
| `add(Member)` | `Member member` | `boolean` | Thêm hội viên mới, kiểm tra trùng lặp ID |
| `update(member, name, phone, dob)`| `Member, String, String, LocalDate`| `boolean`| Cập nhật thông tin độc giả |
| `delete(Member)` | `Member member` | `boolean` | Xóa độc giả khỏi danh sách |
| `findById(String)` | `String userId` | `Member` | Stream API: `filter -> findFirst -> orElse(null)` |
| `searchByName(String)` | `String keyword` | `List<Member>` | Stream API: tìm kiếm tên mờ (case-insensitive) |
| `searchByPhone(String)` | `String phoneKeyword` | `List<Member>` | Stream API: tìm kiếm theo số điện thoại |
| `getAll()` | Không | `List<Member>` | Lấy danh sách toàn bộ độc giả |
| `filterByTier(String)` | `String tier` | `List<Member>` | Stream API: Lọc theo hạng (`VIP`, `GOLD`, `SILVER`) |
| `sortByNameAscending()` | Không | `List<Member>` | Stream API `.sorted` theo họ tên A -> Z |
| `sortByIdAscending()` | Không | `List<Member>` | Stream API `.sorted` theo Member ID |
| `sortByReputationDescending()` | Không | `List<Member>` | Stream API `.sorted` theo điểm uy tín cao -> thấp |
| `loadFromFile(filePath)` | `String` | `boolean` | Nạp dữ liệu độc giả từ file qua `MemberFileHandler` |
| `saveToFile(filePath)` | `String` | `boolean` | Lưu danh sách độc giả vào file qua `MemberFileHandler` |

---

### 2.3. `IBorrowDAO` (Interface) & `BorrowDAO` (Implementation)
- **Mô tả:** Quản lý giao dịch mượn - trả sách, kiểm soát quá hạn, đền bù hư hỏng/mất sách, tự động kích hoạt thông báo đặt trước và xuất biên lai giao dịch.

| Phương thức | Tham số | Kiểu trả về | Ghi chú kỹ thuật & Stream API |
| :--- | :--- | :--- | :--- |
| `createBorrowRecord(BorrowRecord)` | `BorrowRecord` | `boolean` | Tạo phiếu mượn mới, kiểm tra hạn mức thẻ và trừ kho |
| `findById(String)` | `String recordId` | `BorrowRecord` | Tìm phiếu mượn theo Record ID |
| `getAll()` | Không | `List<BorrowRecord>` | Lấy toàn bộ danh sách phiếu mượn |
| `getActiveBorrowRecords()` | Không | `List<BorrowRecord>` | Stream API: lọc các phiếu đang mượn (chưa hoàn trả) |
| `getOverdueRecords()` | Không | `List<BorrowRecord>` | Stream API: lọc các phiếu đang bị quá hạn |
| `findByMemberId(String)` | `String memberId` | `List<BorrowRecord>` | Stream API: lấy lịch sử mượn sách của độc giả |
| `countActiveBorrowedBooksByMember(id)`| `String memberId` | `int` | Stream API: đếm số sách độc giả đang mượn chưa trả |
| `returnBooks(recordId, returnDate)`| `String, LocalDate` | `boolean` | Trả sách, hoàn kho, tính phạt, cập nhật uy tín |
| `returnBooksWithDamageCheck(...)` | `recordId, date, lostIds`| `boolean` | Trả sách kèm kiểm tra sách hỏng/mất đền bù 150% |
| `extendBorrowRecord(recordId, days)` | `String, int` | `boolean` | Gia hạn ngày mượn cho phiếu hợp lệ |
| `exportReceiptToFile(recordId)` | `String recordId` | `boolean` | Tự động xuất biên lai chi tiết ra `Receipt_<ID>.txt` |
| `deleteBorrowRecord(String)` | `String recordId` | `boolean` | Xóa phiếu mượn và hoàn trả tồn kho nếu chưa trả |
| `calculateTotalBorrowFeeRevenue()` | Không | `double` | Stream API tính tổng doanh thu phí mượn |
| `calculateTotalFineRevenue()` | Không | `double` | Stream API tính tổng tiền phạt quá hạn |
| `calculateTotalCompensationRevenue()`| Không | `double` | Stream API tính tổng tiền đền bù sách mất/hỏng |
| `loadFromFile(filePath)` | `String` | `boolean` | Nạp dữ liệu từ `BorrowRecords.txt` |
| `saveToFile(filePath)` | `String` | `boolean` | Lưu trữ dữ liệu phiếu mượn vào `BorrowRecords.txt` |

---

### 2.4. `FileManager` & `MemberFileHandler`
- **Mô tả:** Tiện ích tầng DAO xử lý đọc/ghi file văn bản mã hóa chuẩn UTF-8 sử dụng Java 8 `java.nio.file.Files`.

| Phương thức | Tham số | Kiểu trả về | Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `FileManager.readLines(path)` | `String filePath` | `List<String>` | Đọc toàn bộ các dòng text UTF-8 an toàn |
| `FileManager.writeLines(path, lines)`| `String, List<String>`| `boolean` | Ghi đè toàn bộ danh sách chuỗi ra file UTF-8 |
| `FileManager.appendLine(path, line)` | `String, String` | `boolean` | Ghi nối thêm 1 dòng vào cuối file |
| `MemberFileHandler.loadMembers(path)`| `Path filePath` | `List<Member>` | Đọc file `Members.txt` và parse từng token |
| `MemberFileHandler.saveMembers(...)` | `List<Member>, Path` | `void` | Format danh sách hội viên và ghi file qua NIO.2 |

---

## 3. PACKAGE: `BusinessObject.UI`

### 3.1. Class `BusinessObject.UI.BookManagement`
- **Mô tả:** Giao diện điều khiển phân hệ Quản lý Sách và Kho tài liệu.
- **Cơ chế Lưu / Thoát thống nhất (Unified Save/Exit):**
  - **Tùy chọn 11:** Lưu danh sách sách ra file (`Books.txt`).
  - **Tùy chọn 12:** Thoát mà không lưu (hủy bỏ toàn bộ thay đổi trên RAM bằng `loadFromFile()`).
  - **Tùy chọn 0:** Lưu dữ liệu & quay về Main Menu.
- **Các phương thức chính:**
  - `processMenuForBook()`: Vòng lặp điều khiển menu chính phân hệ Sách (tùy chọn [0-12]).
  - `printAllBooks()`: In toàn bộ sách với bảng chi tiết đa hình và tổng tài sản kho.
  - `addNewBook()`: Thêm sách mới (`TextBook` / `ReferenceBook`) vào bộ nhớ RAM.
  - `updateBook()`: Cập nhật thông tin sách theo Book ID trên RAM.
  - `deleteBook()`: Xóa sách khỏi kho lưu trữ trên RAM.
  - `searchBooksSubMenu()`: Menu tìm kiếm (theo ID, Tên, Tác giả, Khoảng giá niêm yết).
  - `sortBooksSubMenu()`: Menu sắp xếp (theo Tên, Tác giả, Giá, Số lượng, Giá trị kho, Selection/Insertion Sort).
  - `simulateBorrowFeePolymorphism()`: Mô phỏng tính phí mượn đa hình theo ngày thực tế.
  - `displayInventoryReport()`: Báo cáo tổng hợp tình trạng kho sách và cảnh báo tồn thấp (< 5 cuốn).
  - `reservationQueueSubMenu()`: Quản lý hàng đợi đặt trước khi sách hết hàng (FIFO).
  - `displayBookInventoryChart()`: Vẽ biểu đồ cột ASCII trực quan hóa số lượng tồn kho từng đầu sách.

---

### 3.2. Class `BusinessObject.UI.MemberManagement`
- **Mô tả:** Giao diện điều khiển phân hệ Quản lý Độc giả và Hạng thẻ thành viên.
- **Cơ chế Lưu / Thoát thống nhất (Unified Save/Exit):**
  - **Tùy chọn 8:** Lưu danh sách thành viên ra file (`Members.txt`).
  - **Tùy chọn 9:** Thoát mà không lưu (hủy bỏ toàn bộ thay đổi trên RAM bằng `loadFromFile()`).
  - **Tùy chọn 0:** Lưu dữ liệu & quay về Main Menu.
- **Các phương thức chính:**
  - `processMenuForMember()`: Vòng lặp điều khiển menu phân hệ Độc giả (tùy chọn [0-9]).
  - `displayMembers(List<Member>, title)`: In bảng danh sách độc giả kèm hạng thẻ, điểm uy tín và trạng thái.
  - `addMember()`: Thêm độc giả mới kèm kiểm tra tính hợp lệ định dạng số điện thoại, ngày sinh.
  - `updateMember()`: Cập nhật thông tin độc giả, bao gồm cả đổi trạng thái thẻ (`Active` / `Locked`).
  - `deleteMember()`: Xóa độc giả khỏi danh sách trong RAM.
  - `searchMemberSubMenu()`: Tìm kiếm theo ID, Tên, Số điện thoại hoặc Lọc theo hạng (`VIP`, `GOLD`, `SILVER`).
  - `sortMemberSubMenu()`: Sắp xếp theo Điểm uy tín (Cao -> Thấp), Họ tên (A -> Z), Mã độc giả.
  - `displayMembershipTierAnalytics()`: Vẽ biểu đồ cột ASCII phân bố số lượng thành viên theo từng hạng thẻ.

---

### 3.3. Class `BusinessObject.UI.BorrowManagement`
- **Mô tả:** Giao diện điều khiển phân hệ Giao dịch Mượn - Trả sách và thanh toán hóa đơn.
- **Cơ chế Lưu / Thoát thống nhất (Unified Save/Exit):**
  - **Tùy chọn 10:** Lưu danh sách phiếu mượn và cập nhật tồn kho/điểm thành viên ra file (`BorrowRecords.txt`, `Books.txt`, `Members.txt`).
  - **Tùy chọn 11:** Thoát mà không lưu (hủy bỏ toàn bộ thay đổi trên RAM bằng `loadFromFile()` trên cả 3 DAO).
  - **Tùy chọn 0:** Lưu toàn bộ dữ liệu giao dịch & tồn kho và quay về Main Menu.
- **Các phương thức chính:**
  - `processMenuForBorrow()`: Vòng lặp điều khiển menu phân hệ Mượn - Trả (tùy chọn [0-11]).
  - `displayBorrowRecords(List<BorrowRecord>, title)`: In bảng danh sách phiếu mượn chi tiết.
  - `createNewBorrowRecord()`: Lập phiếu mượn mới (kiểm tra hạn mức hạng thẻ và hỗ trợ đặt trước khi hết kho).
  - `processReturnBooks()`: Xử lý trả sách, đền bù mất/hỏng (150%), cập nhật điểm uy tín và tự động xuất biên lai.
  - `extendLoanPeriod()`: Gia hạn thời gian mượn (+7 ngày) cho phiếu chưa quá hạn.
  - `searchBorrowSubMenu()`: Tìm kiếm phiếu mượn theo Record ID, Member ID hoặc Lọc phiếu đang mượn.
  - `displayBorrowStatistics()`: Thống kê doanh thu phí mượn, tiền phạt, đền bù và vẽ biểu đồ cột ASCII lượt mượn sách.
  - `exportReceiptMenu()`: Xuất hóa đơn giao dịch ra tệp `Receipt_<RecordID>.txt`.
  - `deleteBorrowRecord()`: Xóa phiếu mượn và hoàn trả tồn kho.

---

### 3.4. Class `BusinessObject.UI.Program` (Main Entry Point)
- **Mô tả:** Điểm khởi chạy chính của chương trình (`public static void main(String[] args)`). Khởi tạo các DAO, đồng bộ dữ liệu ban đầu, điều hướng Menu tổng, hiển thị Dashboard hệ thống và xuất Báo cáo toàn thư viện.
- **Các phương thức chính:**
  - `main(String[] args)`: Điều phối vòng đời ứng dụng qua switch-case phân hệ.
  - `displaySystemDashboard(IBookDAO, IMemberDAO, IBorrowDAO)`: In tổng quan thống kê kho, bạn đọc, doanh thu và vẽ đồ thị thanh ngang ASCII trực quan.
  - `exportSystemReportToFile(IBookDAO, IMemberDAO, IBorrowDAO)`: Xuất toàn bộ thống kê thư viện ra tệp `Library_Summary_Report.txt`.
  - `saveAllData(IBookDAO, IMemberDAO, IBorrowDAO)`: Đồng bộ toàn bộ dữ liệu ra 3 file lưu trữ.

---

## 4. PACKAGE: `Utilities`

### 4.1. Class `Utilities.DataInput`
- **Mô tả:** Bộ tiện ích chuẩn hóa nhập liệu từ bàn phím console, chống crash chương trình (`InputMismatchException`, nuốt dòng thừa).
- **Các phương thức:**
  - `getString(prompt)`: Nhập chuỗi tự do (cho phép rỗng).
  - `getNonEmptyString(prompt)`: Bắt buộc nhập chuỗi không được để trống.
  - `getIntegerNumber(prompt)`: Nhập số nguyên an toàn, tự động lặp lại khi nhập sai định dạng.
  - `getIntegerNumber(prompt, min, max)`: Nhập số nguyên trong khoảng `[min, max]`.
  - `getDoubleNumber(prompt)`: Nhập số thực an toàn.
  - `getPositiveDouble(prompt)`: Bắt buộc nhập số thực dương (> 0).
  - `getDate(prompt)`: Nhập ngày tháng chuẩn `dd/MM/yyyy` (cho phép Enter lấy giá trị mặc định).
  - `getBoolean(prompt)`: Nhập xác nhận Yes/No (`Y/N`).

---

### 4.2. Class `Utilities.DataValidation`
- **Mô tả:** Lớp tiện ích kiểm tra tính hợp lệ của dữ liệu đầu vào sử dụng Biểu thức chính quy (Regex).
- **Các phương thức:**
  - `checkStringEmpty(value)`: Kiểm tra chuỗi null hoặc rỗng.
  - `checkStringLengthInRange(value, min, max)`: Kiểm tra độ dài chuỗi nằm trong khoảng `[min, max]`.
  - `checkStringWithFormat(value, pattern)`: Kiểm tra chuỗi khớp mẫu regex.
  - `checkNumberInMinMax(number, min, max)`: Kiểm tra số nguyên nằm trong khoảng `[min, max]`.
  - `checkDoubleInMinMax(number, min, max)`: Kiểm tra số thực nằm trong khoảng `[min, max]`.
  - `isValidPhone(phone)`: Kiểm tra định dạng số điện thoại Việt Nam (10 chữ số bắt đầu bằng 0).
  - `isValidId(id, prefix)`: Kiểm tra định dạng mã thực thể với tiền tố cho trước (ví dụ: `B`, `M`, `BR`).

---

### 4.3. Class `Utilities.Menu`
- **Mô tả:** Tiện ích định dạng giao diện console, in menu đóng khung phân tách bằng ký tự pipe `|` và vẽ biểu đồ cột ngang ASCII. Tương thích hoàn toàn Java 8 (`repeat` helper).
- **Các phương thức:**
  - `print(menuString)`: In danh sách tùy chọn menu phân tách bằng dấu gạch đứng `|` qua Stream pipeline.
  - `getUserChoice()`: Đọc lựa chọn của người dùng qua `DataInput`.
  - `repeat(String str, int count)`: Sinh chuỗi lặp lại tương thích Java 8 (thay thế cho `String.repeat()` của Java 11).
  - `printHeader(title)`: In tiêu đề đóng khung chuẩn căn giữa.
  - `printAsciiBarChart(chartTitle, labels, values, unit)`: Vẽ biểu đồ cột ngang ASCII tự động co giãn tỉ lệ thanh bar theo giá trị lớn nhất (max).

---

*(Bản quyền tài liệu thuộc về Nhóm 4 - Lớp SE2111 - Dự án Hệ Thống Quản Lý Thư Viện PRO192)*
