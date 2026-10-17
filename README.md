# HỆ THỐNG QUẢN LÝ THƯ VIỆN (LIBRARY MANAGEMENT SYSTEM)
## PHIÊN BẢN HIỆN ĐẠI HÓA: BIỂU THỨC LAMBDA & STREAM API (JAVA 8+)

> **Học phần**: PRO192 - Lập trình Hướng đối tượng với Java (Object-Oriented Programming)  
> **Học kỳ**: Fall 2026 (FA26)  
> **Ngôn ngữ**: Java (Tương thích Java 8 đến Java 25+)  

---

## 📖 1. Giới thiệu tổng quan
Dự án **Library Management System** được nâng cấp và tái cấu trúc (refactoring) toàn diện từ phong cách lập trình mệnh lệnh truyền thống (Imperative Programming với vòng lặp `for`/`while`, lớp nội ẩn danh `Anonymous Classes`) sang **phong cách lập trình hàm hiện đại (Functional Programming)** với **Biểu thức Lambda (Lambda Expressions)**, **Tham chiếu phương thức (Method References)** và **Java Stream API**.

Toàn bộ các tác vụ xử lý tập hợp dữ liệu:
- Tìm kiếm (Search & Filter)
- Thuật toán sắp xếp & So sánh đa tiêu chí (Sorting & Multi-criteria Comparators)
- Tổng hợp & Thống kê tài chính/tồn kho (Reduction, Sum, Max, Min, Count)
- Nhóm dữ liệu & Đếm tần suất (Grouping & Counting với `flatMap`)
- Đọc/ghi tập tin văn bản (File I/O với Java NIO.2 `Files.lines` & `Files.write`)

đều được chuyển đổi sang cú pháp Lambda & Stream API chuẩn mực, tối ưu và có chú thích giải thích chi tiết trong từng dòng code.

---

## 🏛️ 2. Cấu trúc Kiến trúc Hệ thống (3-Tier Architecture)

```
Library Management/
├── Core/
│   └── Entities/
│       ├── Book.java                # Lớp trừu tượng Sách, hàng đợi đặt trước (Reservation Queue)
│       ├── TextBook.java            # Sách giáo trình (Đa hình: calculatePrice, calculateBorrowFee)
│       ├── ReferenceBook.java       # Sách tham khảo (Đa hình: tính thuế, bảo quản)
│       ├── User.java                # Lớp trừu tượng Người dùng
│       ├── Member.java              # Độc giả (Điểm uy tín, Hạng thẻ VIP/GOLD/SILVER)
│       └── BorrowRecord.java        # Phiếu mượn - trả (Gia hạn, đền bù, Stream tính phí)
├── DataObjects/
│   └── DAO/
│       ├── IBookDAO.java / BookDAO.java             # Quản lý kho sách (Stream search, sort, stats)
│       ├── IMemberDAO.java / MemberDAO.java         # Quản lý độc giả (Stream filter, ranking)
│       ├── IBorrowDAO.java / BorrowDAO.java         # Quản lý giao dịch mượn trả & doanh thu
│       ├── MemberFileHandler.java                   # Đọc/ghi Members.txt bằng NIO.2 Files.lines
│       └── FileManager.java                         # Tiện ích File I/O Stream
├── BusinessObject/
│   └── UI/
│       ├── Program.java             # Điều phối chính & System Dashboard
│       ├── BookManagement.java      # Giao diện quản lý Sách & Biểu đồ ASCII
│       ├── MemberManagement.java    # Giao diện quản lý Độc giả
│       └── BorrowManagement.java    # Giao diện mượn trả & Thống kê tần suất mượn
├── Utilities/
│   ├── Menu.java                    # Vẽ biểu đồ ASCII cột ngang bằng Stream & repeat
│   ├── DataInput.java               # Nhập liệu an toàn từ bàn phím
│   └── DataValidation.java          # Kiểm tra tính hợp lệ dữ liệu (Regex)
└── README.md                        # Tài liệu hướng dẫn chi tiết
```

---

## 💡 3. Bảng so sánh chi tiết: Vòng lặp truyền thống vs Biểu thức Lambda & Stream API

### 3.1. Tìm kiếm theo mã (Search / Find by ID)
* **Vị trí**: `BookDAO.findBookById`, `MemberDAO.findById`, `BorrowDAO.findById`

* **Cách cũ (Vòng lặp for truyền thống)**:
```java
public Book findBookById(String bookId) {
    for (Book b : bookList) {
        if (b.getBookId() != null && b.getBookId().equalsIgnoreCase(bookId.trim())) {
            return b;
        }
    }
    return null;
}
```

* **Cách mới (Stream API & Lambda)**:
```java
public Book findBookById(String bookId) {
    if (bookId == null || bookId.trim().isEmpty()) return null;
    String cleanId = bookId.trim();
    return bookList.stream()
            .filter(b -> b.getBookId() != null && b.getBookId().equalsIgnoreCase(cleanId))
            .findFirst()
            .orElse(null);
}
```
* **Giải thích**:
  - `filter(...)`: Nhận vào một `Predicate<Book>` dạng Lambda để kiểm tra điều kiện.
  - `findFirst()`: Cơ chế ngắt sớm (short-circuiting) – dừng duyệt luồng ngay khi tìm thấy phần tử đầu tiên thỏa mãn.
  - `orElse(null)`: Trả về đối tượng `Book` nếu có, ngược lại trả về `null` an toàn.

---

### 3.2. Sắp xếp đa tiêu chí (Sorting & Comparator Chaining)
* **Vị trí**: `MemberDAO.sortByNameAscending`, `MemberDAO.sortByReputationDescending`, `BookDAO.getSortedByPrice`

* **Cách cũ (Lớp nội ẩn danh Anonymous Class)**:
```java
Collections.sort(sorted, new Comparator<Member>() {
    @Override
    public int compare(Member m1, Member m2) {
        int cmp = m1.getFullName().compareToIgnoreCase(m2.getFullName());
        if (cmp != 0) return cmp;
        return m1.getUserId().compareToIgnoreCase(m2.getUserId());
    }
});
```

* **Cách mới (Comparator Chaining & Method Reference)**:
```java
return memberList.stream()
        .sorted(Comparator.comparing(Member::getFullName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Member::getUserId, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());
```
* **Giải thích**:
  - `Comparator.comparing(...)`: Nhận hàm trích xuất khóa (`Function`) qua Method Reference `Member::getFullName`.
  - `thenComparing(...)`: Kết chuỗi tiêu chí thứ hai một cách tự nhiên mà không cần câu lệnh `if (cmp != 0)`.
  - Giảm từ 12 dòng code lồng nhau xuống còn 4 dòng code trong sáng, dễ đọc.

---

### 3.3. Tính toán tổng hợp & Đa hình (Reduction: `sum`, `mapToDouble`, `mapToInt`)
* **Vị trí**: `BookDAO.calculateTotalInventoryValue`, `BorrowRecord.calculateTotalBorrowFee`, `BorrowDAO.calculateTotalFineRevenue`

* **Cách cũ (Biến tích lũy Accumulator)**:
```java
double sum = 0.0;
for (Book b : bookList) {
    sum += b.calculateTotalValue();
}
return sum;
```

* **Cách mới (DoubleStream reduction)**:
```java
return bookList.stream()
        .mapToDouble(Book::calculateTotalValue)
        .sum();
```
* **Giải thích**:
  - `mapToDouble(...)`: Chuyển đổi từ `Stream<Book>` sang `DoubleStream` nguyên thủy, tránh chi phí boxing/unboxing `Double`.
  - Đa hình (Polymorphism): Phương thức `calculateTotalValue()` được gọi động (dynamic dispatch) dựa trên kiểu thực tế của đối tượng (`TextBook` hay `ReferenceBook`).
  - `sum()`: Hàm thu gọn (terminal reduction operation) tính tổng an toàn.

---

### 3.4. Thống kê & Phân nhóm nâng cao (`flatMap` & `Collectors.groupingBy`)
* **Vị trí**: `BorrowManagement.displayBorrowStatistics`

* **Cách cũ (Vòng lặp for lồng nhau 2 cấp & Map thủ công)**:
```java
Map<String, Integer> bookCountMap = new HashMap<>();
for (int i = 0; i < all.size(); i++) {
    BorrowRecord r = all.get(i);
    List<Book> books = r.getBorrowedBooks();
    for (int j = 0; j < books.size(); j++) {
        Book b = books.get(j);
        Integer count = bookCountMap.get(b.getBookId());
        bookCountMap.put(b.getBookId(), count == null ? 1 : count + 1);
    }
}
```

* **Cách mới (FlatMap & GroupingBy)**:
```java
Map<String, Long> bookBorrowCounts = all.stream()
        .flatMap(r -> r.getBorrowedBooks().stream())
        .collect(Collectors.groupingBy(Book::getBookId, Collectors.counting()));
```
* **Giải thích**:
  - `flatMap(...)`: "Làm phẳng" danh sách sách từ từng phiếu mượn `List<BorrowRecord>` thành một luồng phẳng `Stream<Book>`.
  - `Collectors.groupingBy(Book::getBookId, Collectors.counting())`: Tự động gom nhóm theo mã sách và đếm tần suất xuất hiện chỉ trong một biểu thức duy nhất!

---

### 3.5. Đọc/Ghi tập tin văn bản bằng Java NIO.2, FileManager & Stream pipeline
* **Vị trí**: `FileManager.readLines/writeLines`, `BookDAO.loadFromFile/saveToFile`, `BorrowDAO.loadFromFile/saveToFile`, `MemberFileHandler.loadMembers/saveMembers`

* **Cách cũ (BufferedReader/BufferedWriter & for/while loop)**:
```java
BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
String line;
while ((line = reader.readLine()) != null) {
    String trimmed = line.trim();
    if (!trimmed.isEmpty() && !trimmed.startsWith("#")) lines.add(trimmed);
}
```

* **Cách mới (Files.lines & Stream pipeline)**:
```java
try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
    return stream
            .map(String::trim)
            .filter(line -> !line.isEmpty() && !line.startsWith("#"))
            .collect(Collectors.toList());
}
```
* **Giải thích**:
  - `Files.lines`: Đọc dòng theo cơ chế lười (on-demand), không tải toàn bộ file vào RAM cùng lúc.
  - Sử dụng cú pháp try-with-resources đảm bảo Stream và file descriptor được tự động đóng giải phóng tài nguyên.

---

## 🛠️ 4. Hướng dẫn Biên dịch & Chạy chương trình

### Yêu cầu môi trường:
- JDK 8 trở lên (Đã kiểm thử tối ưu trên **Java 17, 21 LTS** và **Java 25**).

### Biên dịch:
Từ thư mục dự án `Library Management`:
```bash
javac -encoding UTF-8 -d bin BusinessObject/UI/*.java Core/Entities/*.java DataObjects/DAO/*.java Utilities/*.java
```

### Chạy chương trình:
```bash
java -cp bin BusinessObject.UI.Program
```

---

## 👥 Danh sách Phân công Thành viên (PRO192 Team)
- **Member 1 (Trưởng nhóm)**: Phân hệ Độc giả & Người dùng (`Member`, `MemberDAO`, `MemberFileHandler`, `MemberManagement`)
- **Member 2**: Phân hệ Kho Sách & Đa hình (`Book`, `TextBook`, `ReferenceBook`, `BookDAO`, `BookManagement`)
- **Member 3**: Phân hệ Giao dịch Mượn - Trả (`BorrowRecord`, `BorrowDAO`, `BorrowManagement`)
- **Member 4**: Phân hệ Tiện ích & Điều phối Hệ thống (`Program`, `Menu`, `FileManager`, `DataInput`, `DataValidation`)
