# 📚 SYSTEM CLASS & METHOD CATALOG (API SPECIFICATION)
## Project: Library Management System — Course: PRO192 (OOP with Java)
**Group:** Group 4 | **Class:** SE2111  
**Member Responsibilities:**
- **Member 1 (Leader):** Nguyễn Thiên Tuệ (`SE210176`) — Phân hệ Thành viên & Quản lý Thẻ đọc (`User`, `Member`, `IMemberDAO`, `MemberDAO`, `MemberFileHandler`, `MemberManagement`)
- **Member 2:** Nguyễn Trí Dũng (`SE210456`) — Phân hệ Sách & Đa hình (`Book`, `TextBook`, `ReferenceBook`, `IBookDAO`, `BookDAO`, `BookManagement`)
- **Member 3:** Trần Lý Việt Tùng (`SE210308`) — Phân hệ Mượn Trả & Hóa đơn (`BorrowRecord`, `IBorrowDAO`, `BorrowDAO`, `BorrowManagement`)
- **Member 4:** Thiều Quang Tuấn (`SE210531`) — Hạ tầng, Báo cáo & Tiện ích (`FileManager`, `DataInput`, `DataValidation`, `Menu`, `Program`)

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
- **Mô tả:** Lớp cha trừu tượng đại diện cho một người dùng trong hệ sinh thái thư viện (thể hiện tính trừu tượng & bao đóng).
- **Thuộc tính (Fields):**
  - `protected String userId`
  - `protected String fullName`
  - `protected String phone`
  - `protected LocalDate dateOfBirth`
  - `public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")`

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `User(...)` | `String id, String name, String phone, LocalDate dob` | Constructor | Khởi tạo đầy đủ thuộc tính |
| `getUserId()` | Không | `String` | Getter mã người dùng |
| `setUserId(String)` | `String userId` | `void` | Setter có validation chuỗi rỗng |
| `getFullName()` | Không | `String` | Getter tên đầy đủ |
| `setFullName(String)` | `String fullName` | `void` | Setter kiểm tra null/blank |
| `getPhone()` | Không | `String` | Getter số điện thoại |
| `setPhone(String)` | `String phone` | `void` | Setter kiểm tra định dạng điện thoại |
| `getDateOfBirth()` | Không | `LocalDate` | Getter ngày sinh chuẩn Java 8 Date API |
| `setDateOfBirth(LocalDate)` | `LocalDate dob` | `void` | Setter ngày sinh |
| `displayInfo()` | Không | `abstract void` | **Phương thức trừu tượng** bắt buộc lớp con override |
| `toFileString()` | Không | `abstract String` | **Phương thức trừu tượng** xuất chuỗi lưu file |

---

### 1.2. Class `Core.Entities.Member` (Extends `User`)
- **Mô tả:** Đại diện độc giả/thành viên thư viện. Kế thừa `User`, tích hợp hệ thống chấm điểm uy tín (*Reputation Points*) và phân hạng hội viên tự động (*VIP, GOLD, SILVER*).
- **Thuộc tính bổ sung:**
  - `private LocalDate registrationDate`
  - `private String status` (`"ACTIVE"`, `"LOCKED"`)
  - `private int reputationPoints`

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `Member(...)` | `String id, String name, String phone, LocalDate dob, LocalDate regDate, String status, int points` | Constructor | Gọi `super(...)` để nạp cha |
| `getRegistrationDate()` | Không | `LocalDate` | Getter ngày đăng ký |
| `getStatus()` | Không | `String` | Getter trạng thái thẻ |
| `setStatus(String)` | `String status` | `void` | Cập nhật ACTIVE/LOCKED |
| `getReputationPoints()` | Không | `int` | Getter điểm uy tín |
| `setReputationPoints(int)` | `int points` | `void` | Tự động cập nhật LOCKED nếu điểm < 0 |
| `adjustReputationPoints(int)` | `int delta` | `void` | Cộng/trừ điểm và auto-lock nếu < 0 |
| `getMembershipTier()` | Không | `String` | Phân hạng: `>=150: VIP`, `100-149: GOLD`, `<100: SILVER` |
| `getMaxBorrowLimit()` | Không | `int` | Hạn mức mượn sách: `VIP: 10`, `GOLD: 7`, `SILVER: 5` |
| `getDiscountRate()` | Không | `double` | Tỉ lệ giảm phí: `VIP: 0.25 (25%)`, `GOLD: 0.10 (10%)`, `SILVER: 0.0` |
| `displayInfo()` | Không | `void` | **@Override** in thông tin chi tiết hội viên |
| `toFileString()` | Không | `String` | **@Override** định dạng chuỗi lưu vào `Members.txt` |

---

### 1.3. Class `Core.Entities.Book` (Abstract)
- **Mô tả:** Lớp cha trừu tượng cho tất cả tài liệu/sách trong thư viện. Chứa các phương thức đa hình nghiệp vụ tính phí mượn, giá trị tồn kho, hàng đợi đặt trước FIFO và đánh giá sao.
- **Thuộc tính:**
  - `protected String bookId`, `protected String title`, `protected String author`
  - `protected double basePrice`, `protected int quantity`
  - `protected double rating`, `protected int ratingCount`
  - `protected Queue<String> reservationQueue` (Hàng đợi đặt trước sách khi hết hàng)

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `Book(...)` | `String id, String title, String author, double price, int qty` | Constructor | Constructor 5 tham số chính thức |
| `Book(...)` | `String id, String title, double price, int qty` | Constructor | **Constructor Overloading**: mặc định author = "Various Authors" |
| `calculatePrice()` | Không | `abstract double` | **Phương thức đa hình trừu tượng** (tính giá bìa) |
| `calculateBorrowFee(int days)` | `int days` | `abstract double` | **Phương thức đa hình trừu tượng** (tính phí mượn) |
| `calculateTotalValue()` | Không | `double` | Tính tổng giá trị tồn kho: `calculatePrice() * quantity` |
| `addRating(int stars)` | `int stars` | `void` | Cập nhật trung bình cộng số sao (1-5 sao) |
| `reserveBook(String memberId)` | `String memberId` | `boolean` | Thêm hội viên vào hàng đợi `Queue<String>` (FIFO) |
| `pollNextReservation()` | Không | `String` | Lấy ra hội viên đặt trước đầu tiên khi có sách trả |
| `getReservationQueueList()` | Không | `List<String>` | Lấy danh sách ID đặt trước |
| `toFileString()` | Không | `abstract String` | Định dạng xuất file `Books.txt` |

---

### 1.4. Class `Core.Entities.TextBook` (Extends `Book`)
- **Mô tả:** Sách giáo khoa, kế thừa `Book`. Ưu đãi giảm giá 10% hỗ trợ học sinh - sinh viên.
- **Thuộc tính:** `private String subject` (Chuyên ngành: IT, CS, SE, Math...)

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `TextBook(...)` | `String id, String title, String author, double price, int qty, String subject` | Constructor | Constructor 6 tham số chuẩn |
| `TextBook(...)` | `String id, String title, double price, int qty, String subject` | Constructor | **Constructor Overloading** (Constructor Chaining) |
| `calculatePrice()` | Không | `double` | **@Override Đa hình:** Giảm 10% giá bìa: `basePrice * 0.9` |
| `calculateBorrowFee(int days)` | `int days` | `double` | **@Override Đa hình:** Phí mượn ưu đãi `3,000 VND / ngày` |
| `getSubject()` / `setSubject()` | Getter/Setter | `String` / `void` | Đóng gói trường dữ liệu chuyên ngành |
| `displayInfo()` | Không | `void` | **@Override** in thông tin chi tiết TextBook |
| `toFileString()` | Không | `String` | **@Override** định dạng `TEXTBOOK|...` |

---

### 1.5. Class `Core.Entities.ReferenceBook` (Extends `Book`)
- **Mô tả:** Sách tham khảo/chuyên khảo, kế thừa `Book`. Có tính thêm thuế bản quyền quốc tế 10%.
- **Thuộc tính:** `private String publisher` (Nhà xuất bản: O'Reilly, Pearson, MIT Press...)

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `ReferenceBook(...)` | `String id, String title, String author, double price, int qty, String publisher` | Constructor | Constructor 6 tham số chuẩn |
| `ReferenceBook(...)` | `String id, String title, double price, int qty, String publisher` | Constructor | **Constructor Overloading** (Constructor Chaining) |
| `calculatePrice()` | Không | `double` | **@Override Đa hình:** Thuế bản quyền 10%: `basePrice * 1.1` |
| `calculateBorrowFee(int days)` | `int days` | `double` | **@Override Đa hình:** Phí mượn chất lượng cao `5,000 VND / ngày` |
| `getPublisher()` / `setPublisher()` | Getter/Setter | `String` / `void` | Đóng gói thông tin nhà xuất bản |
| `displayInfo()` | Không | `void` | **@Override** in thông tin chi tiết ReferenceBook |
| `toFileString()` | Không | `String` | **@Override** định dạng `REFERENCEBOOK|...` |

---

### 1.6. Class `Core.Entities.BorrowRecord` (Aggregation: Has-A)
- **Mô tả:** Phiếu mượn trả sách, kết tập thực thể `Member` và danh sách đa hình `List<Book>`. Xử lý hạn trả, quá hạn, bồi thường mất/hỏng sách (150% giá sách).
- **Thuộc tính:**
  - `private String recordId`, `private Member member`, `private List<Book> borrowedBooks`
  - `private LocalDate borrowDate`, `private LocalDate dueDate`, `private LocalDate returnDate`
  - `private String status` (`"BORROWING"`, `"RETURNED"`), `private int extensionCount`, `private double compensationFee`

| Phương thức | Tham số | Kiểu trả về | OOP & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `BorrowRecord(...)` | Đầy đủ thông tin | Constructor | Khởi tạo phiếu mượn |
| `calculateRawBorrowFee()` | Không | `double` | Tính tổng phí mượn gốc của các sách qua Stream API |
| `calculateDiscountAmount()` | Không | `double` | Tiền giảm theo hạng thẻ: `rawFee * member.getDiscountRate()` |
| `calculateOverdueFine()` | Không | `double` | Tiền phạt trễ hạn: `5,000 VND * số ngày trễ * số lượng sách` |
| `calculateFinalPayment()` | Không | `double` | Tổng thanh toán: `(Phí sau giảm) + Tiền phạt + Tiền bồi thường` |
| `extendDueDate(int days)` | `int days` | `boolean` | Gia hạn thêm ngày mượn (tối đa 1 lần nếu chưa trễ hạn) |
| `isOverdue()` | Không | `boolean` | Kiểm tra xem phiếu có đang bị quá hạn so với hôm nay |
| `getOverdueDays()` | Không | `long` | Tính chính xác số ngày bị quá hạn bằng `ChronoUnit.DAYS` |
| `toFileString()` | Không | `String` | Định dạng chuỗi lưu file `BorrowRecords.txt` |

---

## 2. PACKAGE: `DataObjects.DAO`

### 2.1. `IBookDAO` (Interface) & `BookDAO` (Implementation)
- **Mô tả:** Tầng giao tiếp dữ liệu Sách. Quản lý `ArrayList<Book>`, tìm kiếm lọc đa tiêu chí và tính toán thống kê bằng Java Stream API.

| Phương thức | Tham số | Kiểu trả về | Ghi chú kỹ thuật & Stream API |
| :--- | :--- | :--- | :--- |
| `addBook(Book)` | `Book book` | `boolean` | Thêm sách mới vào kho |
| `updateBook(Book)` | `Book book` | `boolean` | Cập nhật thông tin sách theo ID |
| `deleteBook(String)` | `String bookId` | `boolean` | Xóa sách khỏi kho nếu không có ai mượn |
| `findBookById(String)` | `String bookId` | `Book` | Stream API: `.filter().findFirst().orElse(null)` |
| `findBookByTitle(String)` | `String title` | `List<Book>` | Stream API: lọc theo tiêu đề không phân biệt hoa thường |
| `getBookList()` | Không | `List<Book>` | Trả về danh sách sách hiện tại |
| `search(Predicate<Book>)` | `Predicate<Book>` | `List<Book>` | **Functional Programming:** Lọc linh hoạt theo Predicate |
| `getTotalBooksCount()` | Không | `int` | Đếm tổng số đầu sách (`bookList.size()`) |
| `getTotalQuantity()` | Không | `int` | Stream API: `bookList.stream().mapToInt(Book::getQuantity).sum()` |
| `calculateTotalInventoryValue()` | Không | `double` | Stream API: `mapToDouble(Book::calculateTotalValue).sum()` (Đa hình) |
| `calculateTotalBorrowFee(List, days)` | `List<Book>, int` | `double` | Stream API: tính tổng phí mượn của danh sách sách |
| `findBookByAuthor(String)` | `String author` | `List<Book>` | Stream API lọc theo tác giả |
| `findBookByPriceRange(min, max)` | `double, double` | `List<Book>` | Stream API lọc theo khoảng giá tính toán |
| `searchMultiCriteria(...)` | 5 tiêu chí lọc | `List<Book>` | Stream API lọc kết hợp: Tên, Tác giả, Giá min-max, Loại sách |
| `getSortedByPrice(ascending)` | `boolean` | `List<Book>` | Stream API `.sorted(Comparator.comparingDouble(...))` |
| `getSortedByTitle(ascending)` | `boolean` | `List<Book>` | Stream API `.sorted(Comparator.comparing(Book::getTitle))` |
| `rateBook(bookId, stars)` | `String, int` | `boolean` | Cho điểm đánh giá sao cho sách |
| `getRecommendedBooks(bookId, topN)` | `String, int` | `List<Book>` | Gợi ý top N sách tương tự cùng thể loại/tác giả có rating cao nhất |
| `reserveBook(bookId, memberId)` | `String, String` | `boolean` | Đặt chỗ trước vào hàng đợi FIFO |
| `loadFromFile(filePath)` | `String` | `boolean` | Đọc dữ liệu từ file văn bản qua `FileManager` |
| `saveToFile(filePath)` | `String` | `boolean` | Ghi danh sách sách ra file văn bản |

---

### 2.2. `IMemberDAO` (Interface) & `MemberDAO` (Implementation)
- **Mô tả:** Tầng truy xuất dữ liệu Độc giả (`Member`). Hỗ trợ CRUD, phân loại hạng hội viên, bảo vệ toàn vẹn tài khoản.

| Phương thức | Tham số | Kiểu trả về | Ghi chú kỹ thuật & Stream API |
| :--- | :--- | :--- | :--- |
| `addMember(Member)` | `Member member` | `boolean` | Thêm hội viên mới |
| `updateMember(Member)` | `Member member` | `boolean` | Cập nhật thông tin hội viên |
| `deleteMember(String)` | `String memberId` | `boolean` | Xóa hội viên theo mã |
| `findById(String)` | `String memberId` | `Member` | Stream API: `.filter().findFirst().orElse(null)` |
| `findByName(String)` | `String name` | `List<Member>` | Stream API: tìm kiếm tên mờ (case-insensitive) |
| `getAll()` | Không | `List<Member>` | Lấy danh sách toàn bộ hội viên |
| `filterByTier(String)` | `String tier` | `List<Member>` | Stream API: Lọc theo hạng (`VIP`, `GOLD`, `SILVER`) |
| `filterByStatus(String)` | `String status` | `List<Member>` | Stream API: Lọc trạng thái (`ACTIVE`, `LOCKED`) |
| `adjustReputationPoints(id, delta)` | `String, int` | `boolean` | Cộng/trừ điểm uy tín hội viên |
| `loadFromFile(filePath)` | `String` | `boolean` | Nạp dữ liệu độc giả từ `Members.txt` |
| `saveToFile(filePath)` | `String` | `boolean` | Lưu danh sách độc giả vào `Members.txt` |

---

### 2.3. `IBorrowDAO` (Interface) & `BorrowDAO` (Implementation)
- **Mô tả:** Quản lý giao dịch mượn/trả sách, kiểm soát quá hạn, bồi thường, xuất hóa đơn biên lai giao dịch.

| Phương thức | Tham số | Kiểu trả về | Ghi chú kỹ thuật & Stream API |
| :--- | :--- | :--- | :--- |
| `addBorrowRecord(BorrowRecord)` | `BorrowRecord` | `boolean` | Tạo phiếu mượn mới |
| `findRecordById(String)` | `String recordId` | `BorrowRecord` | Tìm phiếu mượn theo mã |
| `getAll()` | Không | `List<BorrowRecord>` | Lấy toàn bộ phiếu mượn |
| `getActiveBorrowRecords()` | Không | `List<BorrowRecord>` | Stream API: lọc các phiếu đang mượn (`BORROWING`) |
| `getOverdueRecords()` | Không | `List<BorrowRecord>` | Stream API: lọc các phiếu đang bị trễ hạn |
| `returnBooks(recordId, returnDate)` | `String, LocalDate` | `boolean` | Trả sách, hoàn kho, tính phạt, cộng điểm uy tín |
| `extendLoan(recordId, days)` | `String, int` | `boolean` | Gia hạn mượn thêm ngày |
| `reportLostOrDamagedBook(...)` | `recordId, bookId, fee`| `boolean` | Phạt bồi thường sách hỏng/mất, trừ điểm uy tín |
| `exportReceiptToFile(recordId)` | `String recordId` | `boolean` | Xuất hóa đơn chi tiết ra file `Receipt_<ID>.txt` |
| `calculateTotalBorrowFeeRevenue()` | Không | `double` | Stream API tính tổng doanh thu tiền mượn |
| `calculateTotalFineRevenue()` | Không | `double` | Stream API tính tổng tiền phạt quá hạn |
| `calculateTotalCompensationRevenue()` | Không | `double` | Stream API tính tổng tiền bồi thường sách mất |

---

### 2.4. `FileManager` & `MemberFileHandler`
- **Mô tả:** Tiện ích tầng DAO xử lý đọc/ghi file văn bản mã hóa chuẩn UTF-8 sử dụng Java 8 `java.nio.file.Files`.

| Phương thức | Tham số | Kiểu trả về | Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| `FileManager.readLines(path)` | `String filePath` | `List<String>` | Đọc toàn bộ các dòng text UTF-8, bỏ qua file không tồn tại |
| `FileManager.writeLines(path, lines)`| `String, List<String>`| `boolean` | Ghi ghi đè toàn bộ danh sách chuỗi ra file UTF-8 |
| `FileManager.appendLine(path, line)` | `String, String` | `boolean` | Ghi nối thêm 1 dòng vào cuối file |
| `MemberFileHandler.loadMembers(path)`| `String` | `List<Member>` | Đọc file `Members.txt` và parse từng token |
| `MemberFileHandler.saveMembers(...)` | `String, List<Member>`| `boolean` | Format danh sách hội viên và lưu file `Members.txt` |

---

## 3. PACKAGE: `BusinessObject.UI`

### 3.1. Class `BusinessObject.UI.BookManagement`
- **Mô tả:** Giao diện tương tác console cho toàn bộ tính năng quản lý Sách.
- **Các hàm chính:**
  - `manageBooks(IBookDAO, IBorrowDAO)`: Vòng lặp menu chính quản lý sách.
  - `addNewBook(IBookDAO)`: Nhập liệu và phân loại thêm `TextBook` hoặc `ReferenceBook`.
  - `displayAllBooks(IBookDAO)`: Xuất bảng danh sách sách định dạng cột chuyên nghiệp.
  - `searchBooks(IBookDAO)`: Menu con tìm kiếm (theo tên, tác giả, khoảng giá, đa tiêu chí).
  - `sortBooks(IBookDAO)`: Sắp xếp theo giá tính toán, tiêu đề, số lượng tồn kho.
  - `rateAndRecommendBooks(IBookDAO)`: Đánh giá chất lượng 1-5 sao và hiển thị Top 3 gợi ý.
  - `reserveOutOfStockBook(IBookDAO, IMemberDAO)`: Đưa hội viên vào hàng đợi mượn sách hết kho.

---

### 3.2. Class `BusinessObject.UI.MemberManagement`
- **Mô tả:** Giao diện tương tác console cho toàn bộ phân hệ Hội viên.
- **Các hàm chính:**
  - `manageMembers(IMemberDAO, IBorrowDAO)`: Vòng lặp menu điều khiển hội viên.
  - `addNewMember(IMemberDAO)`: Thêm bạn đọc mới, xác thực định dạng số điện thoại, ngày sinh.
  - `displayAllMembers(IMemberDAO)`: In bảng thông tin độc giả kèm phân hạng tự động và trạng thái thẻ.
  - `filterMembers(IMemberDAO)`: Lọc thành viên theo hạng VIP/GOLD/SILVER hoặc ACTIVE/LOCKED.
  - `adjustReputationPoints(IMemberDAO)`: Thao tác nạp/trừ điểm uy tín thủ công và tự động khóa thẻ.

---

### 3.3. Class `BusinessObject.UI.BorrowManagement`
- **Mô tả:** Giao diện tương tác console cho các nghiệp vụ Mượn - Trả sách và thanh toán.
- **Các hàm chính:**
  - `manageBorrow(IBorrowDAO, IBookDAO, IMemberDAO)`: Vòng lặp menu chính phân hệ mượn trả.
  - `processBorrowBook(...)`: Tạo phiếu mượn, kiểm tra hạn mức thẻ hội viên, trừ tồn kho tức thời.
  - `processReturnBook(...)`: Nhận trả sách, phục hồi tồn kho, tính phạt trễ hạn, thông báo người đặt trước.
  - `processLoanExtension(...)`: Gia hạn thêm ngày mượn cho phiếu hợp lệ.
  - `processLostOrDamaged(...)`: Lập biên bản bồi thường sách mất/rách, trừ điểm độc giả.
  - `printTransactionReceipt(...)`: Xem trước và xuất biên lai thanh toán ra file text.

---

### 3.4. Class `BusinessObject.UI.Program` (Main Entry Point)
- **Mô tả:** Điểm khởi động hệ thống (`public static void main(String[] args)`). Khởi tạo các DAO, đồng bộ dữ liệu ban đầu, điều hướng Menu tổng, hiển thị Dashboard ASCII và xuất Báo cáo toàn diện thư viện.
- **Các hàm chính:**
  - `main(String[] args)`: Điều phối vòng đời ứng dụng.
  - `displaySystemDashboard(IBookDAO, IMemberDAO, IBorrowDAO)`: In tổng quan thống kê kho, bạn đọc, doanh thu và vẽ đồ thị thanh ngang ASCII trực quan.
  - `exportSystemReportToFile(IBookDAO, IMemberDAO, IBorrowDAO)`: Xuất toàn bộ thống kê ra tệp `Library_Summary_Report.txt`.

---

## 4. PACKAGE: `Utilities`

### 4.1. Class `Utilities.DataInput`
- **Mô tả:** Bộ tiện ích chuẩn hóa nhập liệu từ bàn phím console, chống crash chương trình (`InputMismatchException`, nuốt dòng thừa).
- **Các phương thức:**
  - `inputString(prompt, allowEmpty)`: Nhập chuỗi, hỗ trợ bắt buộc hoặc cho phép rỗng.
  - `inputInt(prompt, min, max)`: Nhập số nguyên an toàn trong giới hạn `[min, max]`.
  - `inputDouble(prompt, min, max)`: Nhập số thực an toàn trong giới hạn `[min, max]`.
  - `inputLocalDate(prompt, allowCurrentOrPastOnly)`: Nhập ngày tháng định dạng `dd/MM/yyyy`.

---

### 4.2. Class `Utilities.DataValidation`
- **Mô tả:** Lớp tiện ích kiểm tra tính hợp lệ của dữ liệu đầu vào sử dụng Biểu thức chính quy (Regex).
- **Các phương thức:**
  - `checkStringWithFormat(value, pattern)`: Kiểm tra chuỗi khớp mẫu regex.
  - `isValidPhone(phone)`: Kiểm tra định dạng số điện thoại Việt Nam (10 chữ số, bắt đầu bằng đầu số hợp lệ).
  - `isValidEmail(email)`: Kiểm tra cấu trúc hòm thư điện tử chuẩn RFC `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$`.
  - `isValidId(id, prefix)`: Kiểm tra định dạng mã thực thể (ví dụ `B001`, `M001`, `BR001`).

---

### 4.3. Class `Utilities.Menu`
- **Mô tả:** Tiện ích định dạng giao diện console, in menu tùy biến và vẽ biểu đồ thanh ngang ASCII.
- **Các phương thức:**
  - `print(title, options)`: In danh sách tùy chọn menu đóng khung chuẩn chỉ.
  - `getUserChoice(maxChoice)`: Nhập lựa chọn menu từ `0` đến `maxChoice`.
  - `printAsciiBarChart(title, labels, values, unit)`: Vẽ biểu đồ cột ngang ASCII tự động căn chỉnh tỉ lệ độ rộng cột dựa trên giá trị tối đa.

---

*(Bản quyền tài liệu thuộc về Nhóm 4 - Lớp SE2111 - Dự án Hệ Thống Quản Lý Thư Viện PRO192)*
