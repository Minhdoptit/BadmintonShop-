# BadmintonShop - SmashZone 🏸

Hệ thống Website Thương mại Điện tử Dụng cụ Cầu lông xây dựng bằng **Spring Boot 3** và giao diện **Neobrutalism** hiện đại (phong cách PTIT AI Code).

---

## 🚀 Công nghệ sử dụng

- **Backend:** 
  - Java 17
  - Spring Boot 3.2.3
  - Spring Data JPA (Hibernate)
  - Spring Security / BCrypt
  - MySQL Database
- **Frontend:**
  - HTML5, CSS3 (Neobrutalism UI, tông cam `#f97316` & nền giấy kẻ ô ca-rô)
  - JavaScript Vanilla (Fetch API kết nối RESTful backend)
- **Build tool:** Apache Maven

---

## 📁 Cấu trúc dự án

```text
├── src/
│   ├── main/
│   │   ├── java/com/smashzone/
│   │   │   ├── config/          # Cấu hình Spring Security & MVC
│   │   │   ├── controller/      # REST API Controllers (Auth, Product, Cart, Order, User, Admin)
│   │   │   ├── model/           # JPA Entities
│   │   │   ├── repository/     # Spring Data JPA Repositories
│   │   │   ├── service/         # Business Logic Services
│   │   │   └── SmashZoneApplication.java
│   │   └── resources/
│   │       ├── application.properties  # Cấu hình kết nối MySQL và cổng Server
│   │       └── static/                 # Giao diện người dùng & Admin
│   │           ├── admin/              # Trang quản trị (Sản phẩm, Đơn hàng, Người dùng)
│   │           ├── assets/             # CSS Neobrutalism, JS và hình ảnh sản phẩm
│   │           ├── index.html          # Trang chủ
│   │           ├── products.html       # Danh mục sản phẩm (Phân trang 12 sp/trang, bộ lọc)
│   │           ├── product-detail.html # Chi tiết sản phẩm & Gợi ý liên quan
│   │           ├── cart.html           # Giỏ hàng
│   │           ├── checkout.html       # Thanh toán & Đặt hàng
│   │           ├── login.html          # Đăng nhập
│   │           ├── register.html       # Đăng ký
│   │           └── profile.html        # Thông tin cá nhân & Lịch sử đơn hàng
├── setup_test_data.sql          # Script khởi tạo cơ sở dữ liệu mẫu
├── pom.xml                      # Cấu hình phụ thuộc Maven
└── README.md
```

---

## 🛠️ Hướng dẫn cài đặt & Khởi chạy

### 1. Chuẩn bị Cơ sở dữ liệu
1. Mở MySQL Server và chạy script `setup_test_data.sql` để tạo cơ sở dữ liệu `smashzone_db` cùng bảng và dữ liệu mẫu:
   ```sql
   source setup_test_data.sql;
   ```
2. Cập nhật thông tin tài khoản MySQL trong `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/smashzone_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD
   ```

### 2. Khởi chạy ứng dụng
Chạy lệnh bằng Maven:
```bash
mvn spring-boot:run
```
Hoặc mở dự án trong IntelliJ IDEA / VS Code / Eclipse và khởi chạy class `SmashZoneApplication.java`.

### 3. Trải nghiệm
Truy cập trình duyệt tại địa chỉ:
```
http://localhost:8080
```
- **Tài khoản Khách hàng mẫu:** `minhha` / `123456`
- **Tài khoản Quản trị viên (Admin):** `admin` / `admin123`
