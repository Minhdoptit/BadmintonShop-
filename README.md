# HƯỚNG DẪN DỰ ÁN & TÀI LIỆU VẤN ĐÁP: WEBSITE CỬA HÀNG DỤNG CỤ CẦU LÔNG "SMASH ZONE"
> **Dự án Thực tập Cơ sở**
> **Sinh viên thực hiện:** Do Hà Thanh Minh (Mã SV: B23DCCN543)
> **Giảng viên hướng dẫn:** ThS. Bùi Văn Kiên
> **Học viện Công nghệ Bưu chính Viễn thông (PTIT) - Khoa Công nghệ Thông tin 1**

---

## 📌 PHẦN 1: GIỚI THIỆU TỔNG QUAN DỰ ÁN
Dự án **"Smash Zone"** là website thương mại điện tử chuyên nghiệp cung cấp các dụng cụ cầu lông cao cấp (Vợt cầu lông, Giày cầu lông, Ống cầu lông và các phụ kiện đi kèm). Diện mạo và layout của website được thiết kế thể thao hiện đại lấy cảm hứng từ **fbshop.vn** với tone màu cam năng động (`#F66315`) và xanh navy đen sang trọng (`#031230`).

### Điểm nổi bật nhất của dự án:
Tích hợp **Hệ thống Gợi ý Sản phẩm AI thông minh (AI Recommender System)** gồm 3 khối kiến trúc hoàn chỉnh để cá nhân hóa hành trình mua sắm của khách hàng:
1. **Khối 1: Content-Based Filtering (Lọc dựa trên nội dung)**: Sử dụng mô hình toán học **TF-IDF** và **Cosine Similarity** (viết bằng Python) để đề xuất sản phẩm tương đồng về thông số kỹ thuật động và lối đánh với sản phẩm khách hàng vừa xem.
2. **Khối 2: Collaborative Filtering (Lọc cộng tác)**: Phân tích lịch sử mua sắm chéo của các người dùng có hành vi tương đồng để đề xuất các sản phẩm "thường được mua cùng nhau" (ví dụ: mua vợt cầu lông sẽ gợi ý quấn cán, bao vợt).
3. **Khối 3: Trending Products (Xu hướng)**: Gợi ý các sản phẩm bán chạy nhất hệ thống dựa trên dữ liệu bán hàng thực tế. Khối này giải quyết triệt để bài toán **"Khởi đầu lạnh" (Cold-Start)** khi khách hàng mới truy cập hoặc chưa đăng nhập.

---

## ⚙️ PHẦN 2: KIẾN TRÚC HỆ THỐNG & CÔNG NGHỆ SỬ DỤNG
Dự án được xây dựng theo mô hình kiến trúc **MVC (Model-View-Controller)** chuẩn hóa trong lập trình Java Web:
* **View (Giao diện JSP/HTML/CSS)**: Sử dụng các tệp **JSP (JavaServer Pages)** kết hợp thư viện thẻ chuẩn **JSTL 1.2** để kết xuất giao diện động. Styling bằng **Bootstrap 5** và **Vanilla CSS**.
* **Controller (Bộ điều khiển Servlet)**: Các lớp kế thừa `HttpServlet` nhận request từ client, tương tác với tầng Model để lấy dữ liệu và đẩy sang View bằng cơ chế `Forward/Redirect`.
* **Model (Tầng dữ liệu Bean/DAO)**: 
  * **Beans**: Các lớp thực thể (Entities) ánh xạ trực tiếp từ các bảng trong cơ sở dữ liệu.
  * **DAO (Data Access Object)**: Các lớp chứa mã nguồn tương tác trực tiếp với cơ sở dữ liệu MySQL qua kết nối **JDBC (Java Database Connectivity)**.
* **Cơ sở dữ liệu**: **MySQL Server 8.0** với hệ thống các bảng được thiết kế chuẩn hóa và tối ưu hóa hiệu năng truy vấn.
* **AI Engine (Engine gợi ý độc lập)**: Lập trình bằng kịch bản **Python 3.x** chạy song song với ứng dụng Java Web. Sử dụng các thư viện tính toán khoa học như **Pandas** và **Scikit-learn**.

---

## 🗃️ PHẦN 3: THIẾT KẾ CƠ SỞ DỮ LIỆU (DATABASE SCHEMA ENTITIES)
Hệ thống gồm 7 thực thể (bảng) chính dưới đây:

### 1. Bảng `users` (Quản lý tài khoản người dùng và quản trị viên)
* `user_id` (INT, Primary Key, Auto Increment): Mã định danh người dùng.
* `username` (VARCHAR): Tên đăng nhập duy nhất.
* `password_hash` (VARCHAR): Mật khẩu người dùng đã được mã hóa **MD5** bảo mật.
* `email` (VARCHAR): Địa chỉ email.
* `full_name` (VARCHAR): Họ và tên đầy đủ.
* `phone_number` (VARCHAR): Số điện thoại liên lạc.
* `address` (VARCHAR): Địa chỉ tỉnh/thành phố.
* `city` (VARCHAR): Thành phố/Khu vực.
* `detail_address` (VARCHAR): Địa chỉ chi tiết (số nhà, đường phố).
* `date_of_birth` (DATE): Ngày sinh nhật.
* `avatar` (VARCHAR): Đường dẫn ảnh đại diện.
* `role` (ENUM/VARCHAR): Vai trò của người dùng trên hệ thống (`ADMIN`, `CUSTOMER`, `DISABLED`).

### 2. Bảng `categories` (Quản lý danh mục sản phẩm)
* `category_id` (INT, Primary Key, Auto Increment): Mã danh mục.
* `name` (VARCHAR): Tên danh mục (ví dụ: Vợt Cầu Lông, Giày Cầu Lông, Ống Cầu Lông).

### 3. Bảng `products` (Quản lý thông tin chi tiết sản phẩm)
* `product_id` (INT, Primary Key, Auto Increment): Mã sản phẩm.
* `category_id` (INT, Foreign Key references `categories`): Mã danh mục.
* `name` (VARCHAR): Tên sản phẩm.
* `price` (DECIMAL): Giá bán hiện tại của sản phẩm.
* `import_price` (DECIMAL): Giá nhập của sản phẩm (sử dụng để tính lợi nhuận).
* `quantity` (INT): Số lượng sản phẩm hiện có trong kho.
* `image` / `product_link` (VARCHAR): Đường dẫn URL ảnh hiển thị sản phẩm.
* `metadata_for_ai` (TEXT): Chuỗi chứa thông tin tổng hợp của sản phẩm để mô hình AI vector hóa (ví dụ: tên, danh mục, lối đánh...).
* `specific_attributes` (JSON): Lưu trữ các thông số kỹ thuật động riêng biệt của từng loại sản phẩm dưới dạng cặp Key-Value.
* `created_at` (TIMESTAMP): Thời gian khởi tạo sản phẩm.

### 4. Bảng `orders` (Quản lý thông tin đơn hàng)
* `order_id` (INT, Primary Key, Auto Increment): Mã đơn hàng.
* `user_id` (INT, Foreign Key references `users`): Người đặt hàng.
* `total_amount` (DECIMAL): Tổng giá trị của đơn hàng.
* `status` (VARCHAR): Trạng thái đơn hàng (`PENDING`/`Chờ xác nhận`, `DELIVERED`/`Đã giao`, `CANCELLED`/`Đã hủy`).
* `delivery_address` (VARCHAR): Địa chỉ chi tiết nhận hàng của khách.
* `created_at` (TIMESTAMP): Thời điểm đặt đơn.

### 5. Bảng `order_details` (Quản lý chi tiết từng sản phẩm trong đơn hàng)
* `order_detail_id` (INT, Primary Key, Auto Increment): Mã chi tiết đơn.
* `order_id` (INT, Foreign Key references `orders`): Mã đơn hàng cha.
* `product_id` (INT, Foreign Key references `products`): Mã sản phẩm được mua.
* `quantity` (INT): Số lượng mua của sản phẩm đó.
* `unit_price` (DECIMAL): Đơn giá bán tại thời điểm mua hàng.

### 6. Bảng `user_interactions` (Ghi nhận hành vi tương tác người dùng phục vụ AI)
* `interaction_id` (INT, Primary Key, Auto Increment): Mã tương tác.
* `user_id` (INT, Foreign Key references `users`): Người tương tác.
* `product_id` (INT, Foreign Key references `products`): Sản phẩm bị tương tác.
* `interaction_type` (VARCHAR): Loại tương tác (`VIEW` - Xem, `ADD_TO_CART` - Thêm vào giỏ, `PURCHASE` - Mua hàng).
* `interaction_value` (INT): Trọng số tương tác tương ứng (`VIEW` = 1, `ADD_TO_CART` = 3, `PURCHASE` = 5).
* `created_at` (TIMESTAMP): Thời điểm tương tác.

### 7. Bảng `product_recommendations` (Bảng trung gian lưu kết quả gợi ý Content-Based từ Python)
* `recommendation_id` (INT, Primary Key, Auto Increment): Mã gợi ý.
* `product_id` (INT, Foreign Key references `products`): Sản phẩm gốc khách hàng đang xem.
* `recommended_product_id` (INT, Foreign Key references `products`): Sản phẩm tương đồng được đề xuất.
* `similarity_score` (DOUBLE): Điểm độ tương đồng Cosine (từ 0 đến 1).
* `algorithm_used` (VARCHAR): Tên thuật toán (`TF-IDF Content-Based`).

---

## 📈 PHẦN 4: SƠ ĐỒ VÀ MÔ TẢ CHI TIẾT LUỒNG HOẠT ĐỘNG CỦA TỪNG CHỨC NĂNG
Hệ thống vận hành thông qua sự phối hợp chặt chẽ giữa Giao diện (JSP), Bộ điều khiển (Servlet), và Tầng dữ liệu (DAO/DB). Dưới đây là mô tả chi tiết luồng xử lý của từng chức năng cốt lõi:

---

### 1. Luồng Đăng Ký Tài Khoản (Register Flow)
* **Khởi đầu (JSP)**: Người dùng truy cập trang đăng ký tại tệp [register.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/auth/register.jsp), điền thông tin (tên đăng nhập, mật khẩu, email, họ tên, điện thoại, địa chỉ, ngày sinh) và nhấn nút **Đăng Ký**. Form gửi yêu cầu qua phương thức **POST** tới URL `/register`.
* **Xử lý tại Servlet**: Lớp [RegisterServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/auth/RegisterServlet.java) tiếp nhận yêu cầu trong phương thức `doPost()`:
  1. Lấy dữ liệu từ các thẻ `<input>` thông qua `request.getParameter()`.
  2. Kiểm tra tên tài khoản đã tồn tại trong CSDL chưa bằng cách gọi hàm `userDAO.isUsernameExists(username)`.
  3. Nếu tài khoản đã tồn tại, Servlet đẩy thông báo lỗi về lại trang đăng ký.
  4. Nếu hợp lệ, Servlet khởi tạo đối tượng `User`, đồng thời gọi hàm `SecurityUtil.hashPasswordMD5(password)` để mã hóa mật khẩu thô thành chuỗi băm MD5 an toàn.
  5. Gọi hàm `userDAO.registerUser(user)` để thực hiện chèn dữ liệu vào bảng `users`.
* **Kết thúc**: Đăng ký thành công, Servlet dùng lệnh `response.sendRedirect("login")` chuyển hướng người dùng sang trang Đăng nhập.

---

### 2. Luồng Đăng Nhập Hệ Thống (Login Flow)
* **Khởi đầu (JSP)**: Người dùng điền tên đăng nhập và mật khẩu tại tệp [login.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/auth/login.jsp) rồi nhấn nút **Đăng Nhập**. Form gửi yêu cầu qua phương thức **POST** tới URL `/login`.
* **Xử lý tại Servlet**: Lớp [LoginServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/auth/LoginServlet.java) tiếp nhận trong phương thức `doPost()`:
  1. Lấy `username` và `password` người dùng nhập vào.
  2. Gọi hàm `userDAO.login(username, password)`. Trong hàm `login()`, mật khẩu nhập vào tiếp tục được băm MD5 và so khớp với mã hash lưu trong CSDL.
  3. Nếu khớp thông tin đăng nhập, hàm trả về đối tượng `User` đầy đủ thông tin.
  4. Servlet khởi tạo Session bằng `request.getSession()` và lưu thông tin người dùng vào Session: `session.setAttribute("user", user)`.
* **Phân quyền điều hướng (Authorization)**:
  * Nếu người dùng có `role = 'ADMIN'`: Servlet dùng lệnh `response.sendRedirect` chuyển hướng tới trang quản trị `/admin/dashboard`.
  * Nếu người dùng có `role = 'CUSTOMER'`: Chuyển hướng tới trang chủ `/home`.
  * Nếu đăng nhập thất bại: Servlet lưu thông báo lỗi vào request và dùng `request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response)` để kết xuất lại trang đăng nhập kèm thông báo lỗi.

---

### 3. Luồng Xem Chi Tiết Sản Phẩm & Gợi Ý AI (Product Detail & AI View Flow)
* **Khởi đầu (JSP)**: Người dùng nhấp vào một sản phẩm trên trang chủ [home.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/home.jsp) hoặc trang danh sách sản phẩm [product_list.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/product_list.jsp). Yêu cầu gửi dạng **GET** tới URL `/product-detail?id=X` (với `X` là mã ID sản phẩm).
* **Xử lý tại Servlet**: Lớp [ProductDetailServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/client/ProductDetailServlet.java) xử lý trong phương thức `doGet()`:
  1. Lấy tham số `id` từ URL qua `request.getParameter("id")`.
  2. Gọi hàm `productDAO.getProductById(productId)` để lấy thông tin chi tiết sản phẩm. Hàm này tự động tách chuỗi JSON `specific_attributes` trong DB thành Map để hiển thị thông số kỹ thuật động theo loại mặt hàng (Vợt, Giày, Cầu).
  3. **Lưu vết tương tác AI (Interaction Tracking)**: Nếu người dùng đã đăng nhập, Servlet gọi hàm `interactionDAO.recordInteraction(userId, productId, "VIEW")` để ghi nhận hành vi xem hàng với trọng số là 1 điểm vào bảng `user_interactions`.
  4. **Lấy gợi ý tương đồng AI (Khối 1)**: Gọi hàm `recommendationDAO.getSimilarProductsByContent(productId, 4)` để truy vấn các mã ID sản phẩm tương tự từ bảng `product_recommendations` (do Python tính toán sẵn). Sau đó, gọi `productDAO.getProductsByIds(similarIds)` để lấy chi tiết thông tin các sản phẩm gợi ý đó.
  5. **Fallback dự phòng thông minh**: Nếu bảng gợi ý AI trống (chưa chạy Engine Python), Servlet gọi hàm `productDAO.getProductsByCategoryId(categoryId, productId, 4)` để lấy ra 4 sản phẩm cùng danh mục để gợi ý dự phòng.
  6. Lưu sản phẩm chính và danh sách sản phẩm gợi ý vào request attributes.
* **Kết thúc**: Chuyển tiếp (forward) sang tệp hiển thị [product_detail.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/product_detail.jsp).

---

### 4. Luồng Thêm Vào Giỏ Hàng (Add to Cart Flow)
* **Khởi đầu (JSP)**: Người dùng tại trang chi tiết sản phẩm nhấp vào nút **Thêm vào giỏ hàng**. Yêu cầu gửi dạng **POST** tới URL `/cart?action=add&id=X&quantity=Y`.
* **Xử lý tại Servlet**: Lớp [CartServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/checkout/CartServlet.java) tiếp nhận:
  1. Lấy thông tin ID sản phẩm (`id`) và số lượng mua (`quantity`).
  2. Lấy giỏ hàng hiện tại lưu trong Session: `List<CartItem> cart = (List<CartItem>) session.getAttribute("cart")`. Nếu giỏ hàng chưa tồn tại (mua món đầu tiên), Servlet tự động khởi tạo một danh sách `ArrayList<CartItem>`.
  3. Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa. Nếu đã có thì cộng dồn số lượng mua mới vào số lượng cũ. Nếu chưa có, tạo mới đối tượng `CartItem` và thêm vào danh sách.
  4. Lưu lại giỏ hàng đã cập nhật vào Session: `session.setAttribute("cart", cart)`.
  5. **Lưu vết tương tác AI (Add to Cart Tracking)**: Nếu khách hàng đã đăng nhập, Servlet gọi hàm `interactionDAO.recordInteraction(userId, productId, "ADD_TO_CART")` để lưu vết hành vi quan tâm cao với trọng số 3 điểm.
* **Kết thúc**: Servlet chuyển hướng người dùng tới trang giỏ hàng tại địa chỉ `/cart` để kiểm tra.

---

### 5. Luồng Đặt Hàng & Trừ Kho Nguyên Tử (Checkout Transaction Flow)
* **Bước 1: Điền thông tin giao hàng**: Khách hàng tại trang giỏ hàng [cart.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/cart.jsp) nhấn nút **Thanh toán**. Servlet [CheckoutServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/checkout/CheckoutServlet.java) chuyển tiếp sang trang điền thông tin [checkout.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/checkout.jsp). Người dùng nhập địa chỉ giao hàng và nhấn **Xác nhận đặt hàng**. Yêu cầu gửi qua phương thức **POST** tới URL `/checkout`.
* **Bước 2: Xử lý Giao dịch nguyên tử (Atomic Transaction)**: Lớp [CheckoutServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/checkout/CheckoutServlet.java) tiếp nhận yêu cầu:
  1. Lấy thông tin tài khoản người dùng từ Session, danh sách giỏ hàng `cart` từ Session, và địa chỉ nhận hàng `deliveryAddress` từ Form.
  2. Tính tổng tiền của đơn hàng.
  3. Tạo đối tượng `Order` với trạng thái ban đầu là `"Chờ xác nhận"`.
  4. Gọi hàm giao dịch an toàn của DAO: `boolean success = orderDAO.checkout(order, cart)`.
  5. Trong lớp [OrderDAO.java](file:///c:/Users/Dell/TTCS_WEB/src/java/model/dao/OrderDAO.java), luồng xử lý Transaction diễn ra như sau:
     * Tắt chế độ tự động lưu thay đổi: `conn.setAutoCommit(false)`.
     * **Thêm đơn hàng**: Thực thi câu lệnh INSERT vào bảng `orders`, lấy ra mã `order_id` tự động sinh.
     * **Thêm chi tiết đơn hàng (Batch)**: Sử dụng phương pháp chạy hàng loạt (Batch Execution) chèn các dòng mặt hàng tương ứng vào bảng `order_details`.
     * **Trừ kho hàng tồn (Batch)**: Chạy lệnh UPDATE trừ số lượng kho của từng sản phẩm tương ứng trong bảng `products` (sử dụng hàm `GREATEST(0, quantity - ?)` để bảo vệ kho hàng không bị âm).
     * **Đồng bộ hóa dữ liệu**: Nếu toàn bộ các bước trên diễn ra thành công không gặp lỗi, gọi lệnh `conn.commit()` để lưu vĩnh viễn dữ liệu xuống MySQL. Nếu có bất kỳ ngoại lệ `SQLException` nào xảy ra, hệ thống chạy ngay lệnh `conn.rollback()` để hoàn tác toàn bộ dữ liệu trở về trạng thái sạch ban đầu trước khi đặt hàng.
* **Bước 3: Ghi nhận tương tác mua và dọn dẹp**: 
  * Nếu giao dịch thành công (`success == true`):
    * Hệ thống duyệt qua từng sản phẩm trong giỏ hàng vừa mua và gọi hàm `interactionDAO.recordInteraction(userId, productId, "PURCHASE")` để ghi nhận hành vi mua hàng với trọng số tối đa là 5 điểm.
    * Xóa giỏ hàng cũ trong Session: `session.removeAttribute("cart")`.
    * Chuyển hướng người dùng tới trang lịch sử mua hàng `/order-history` thông qua `response.sendRedirect(...)`.
  * Nếu giao dịch thất bại: Hệ thống chuyển tiếp người dùng quay lại trang checkout và hiển thị thông báo lỗi.

---

### 6. Luồng Xem Lịch Sử Mua Hàng (Order History Flow)
* **Khởi đầu (JSP)**: Khách hàng click vào mục **Lịch sử đơn hàng** trên thanh điều hướng đầu trang [header.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/common/header.jsp). Yêu cầu gửi dạng **GET** tới `/order-history`.
* **Xử lý tại Servlet**: Lớp [OrderHistoryServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/user/OrderHistoryServlet.java) tiếp nhận yêu cầu:
  1. Kiểm tra đối tượng người dùng đăng nhập trong Session.
  2. Gọi hàm `orderDAO.getOrdersByUserId(user.getUserId())` để lấy về toàn bộ danh sách đơn hàng đã mua của tài khoản này từ bảng `orders`, sắp xếp giảm dần theo thời gian lập.
  3. Lưu danh sách đơn hàng lấy được vào request attribute: `request.setAttribute("orders", orders)`.
* **Kết thúc**: Chuyển tiếp (forward) sang tệp giao diện [order_history.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/order_history.jsp). Tại đây, thẻ `<c:forEach>` duyệt danh sách, định dạng số tiền bằng thẻ `<fmt:formatNumber>` và đổi màu sắc nhãn Trạng thái đơn hàng (Thành công - màu xanh, Chờ xác nhận - màu vàng) hiển thị lên trình duyệt của khách hàng.

---

### 7. Luồng Thống Kê Doanh Thu & Lợi Nhuận Cho Admin (Admin Dashboard Flow)
* **Khởi đầu**: Admin đăng nhập thành công hoặc truy cập vào trang quản trị tại đường dẫn `/admin/dashboard`.
* **Xử lý tại Servlet**: Lớp [AdminDashboardServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/admin/AdminDashboardServlet.java) tiếp nhận yêu cầu:
  1. Gọi hàm thống kê từ lớp DAO: `orderDAO.getRevenueByMonth()`.
  2. Ở tầng DAO ([OrderDAO.java](file:///c:/Users/Dell/TTCS_WEB/src/java/model/dao/OrderDAO.java)), hàm này truy vấn doanh thu (`quantity * unit_price`) và lợi nhuận thực tế dựa trên chênh lệch giá bán và giá nhập (`quantity * (unit_price - import_price)`) gom nhóm theo từng tháng, tương thích với chế độ strict mode `ONLY_FULL_GROUP_BY`.
  3. Servlet nhận kết quả danh sách thống kê tháng và lưu vào request attribute.
* **Kết thúc**: Chuyển tiếp (forward) sang tệp giao diện [dashboard.jsp](file:///c:/Users/Dell/TTCS_WEB/web/admin/dashboard.jsp) để vẽ biểu đồ doanh thu và lợi nhuận hàng tháng trực quan bằng Chart.js.

---

### 8. Luồng Trang Chủ & Tải Gợi Ý AI (Home Page & AI Recommender Flow)
* **Khởi đầu (JSP)**: Người dùng truy cập trang chủ qua tệp [index.jsp](file:///c:/Users/Dell/TTCS_WEB/web/index.jsp) (hoặc trực tiếp gõ URL `/home`).
* **Xử lý tại Servlet**: Lớp [HomeServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/client/HomeServlet.java) xử lý yêu cầu GET:
  1. Khởi tạo 3 danh sách rỗng để lưu trữ sản phẩm: `recentRecommendations` (gợi ý hành vi xem gần đây), `collaborativeRecommendations` (gợi ý mua kèm), và `trendingProducts` (sản phẩm xu hướng bán chạy).
  2. **Tải Khối 3 - Sản phẩm bán chạy**: Gọi `productDAO.getTrendingProducts(10)` để lấy Top 10 sản phẩm bán chạy nhất hệ thống (giải quyết bài toán Cold Start khi chưa đăng nhập).
  3. **Tải Khối 1 & Khối 2 (Chỉ dành cho người dùng đã đăng nhập)**:
     * **Khối 1: Gợi ý theo hành vi gần đây (Content-Based)**:
       * Gọi `interactionDAO.getRecentViewedProducts(userId, 5)` để lấy danh sách các mã ID sản phẩm người dùng xem gần đây nhưng chưa mua.
       * Với mỗi ID sản phẩm này, Servlet gọi `recommendationDAO.getSimilarProductsByContent(productId, 4)` để lấy danh sách gợi ý AI. Sau đó gọi `productDAO.getProductsByIds()` để lấy chi tiết sản phẩm.
       * *Fallback*: Nếu AI chưa tính toán, Servlet gọi `productDAO.getProductsByCategoryId()` để lấy sản phẩm cùng loại thay thế.
     * **Khối 2: Gợi ý mua kèm cộng tác (Collaborative Filtering)**:
       * Gọi `recommendationDAO.getCollaborativeFilteringRecommendations(userId, 10)` để lấy danh sách sản phẩm do nhóm người dùng tương đồng đã mua mà người dùng hiện tại chưa mua.
       * Gọi `productDAO.getProductsByIds()` để lấy chi tiết thông tin sản phẩm đề xuất.
  4. Lưu cả 3 danh sách sản phẩm vào request attributes.
* **Kết thúc**: Chuyển tiếp (forward) sang tệp giao diện [home.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/home.jsp) hiển thị trực quan 3 khối sản phẩm gợi ý cá nhân hóa.

---

### 9. Luồng Tìm Kiếm Sản Phẩm (Search Flow)
* **Khởi đầu (JSP)**: Người dùng nhập từ khóa tìm kiếm tại ô Search trên thanh điều hướng [header.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/common/header.jsp) và bấm nút **Tìm Kiếm**. Form gửi yêu cầu GET tới URL `/search?keyword=từ_khóa`.
* **Xử lý tại Servlet**: Lớp [SearchServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/client/SearchServlet.java) tiếp nhận:
  1. Lấy chuỗi từ khóa qua `request.getParameter("keyword")`.
  2. Gọi hàm `productDAO.searchProducts(keyword)` để truy vấn trong MySQL với từ khóa dạng `%keyword%`.
  3. Lưu kết quả danh sách sản phẩm tìm thấy vào request attribute.
* **Kết thúc**: Chuyển tiếp (forward) sang tệp giao diện [product_list.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/product_list.jsp) hiển thị các sản phẩm khớp với từ khóa tìm kiếm.

---

### 10. Luồng Xem Danh Sách Sản Phẩm Theo Phân Loại (Product List Flow)
* **Khởi đầu (JSP)**: Người dùng nhấp chọn các danh mục "Vợt", "Giày", "Cầu" trên [header.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/common/header.jsp). Trình duyệt gửi yêu cầu tới URL `/products?category=tên_danh_mục`.
* **Xử lý tại Servlet**: Lớp [ProductListServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/client/ProductListServlet.java) tiếp nhận:
  1. Lấy tên danh mục từ tham số `category`.
  2. Nếu có chỉ định danh mục: Gọi `productDAO.getProductsByCategory(categoryName)` để lấy sản phẩm thuộc danh mục đó.
  3. Nếu không chỉ định: Gọi `productDAO.getAllProducts()` để lấy toàn bộ sản phẩm.
  4. Gọi thêm `categoryDAO.getAllCategories()` để truyền danh sách danh mục làm bộ lọc bên lề.
  5. Đẩy dữ liệu sản phẩm và danh mục vào request attributes.
* **Kết thúc**: Chuyển tiếp (forward) tới tệp giao diện [product_list.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/product_list.jsp).

---

### 11. Luồng Xem và Cập Nhật Hồ Sơ Cá Nhân (User Profile Flow)
* **Khởi đầu (JSP)**: Khách hàng click vào tên tài khoản trên [header.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/common/header.jsp) dẫn tới URL `/profile`.
* **Xử lý tại Servlet**: Lớp [ProfileServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/user/ProfileServlet.java) tiếp nhận:
  * **GET Request**: Lấy đối tượng `User` hiện tại từ Session và chuyển tiếp tới [profile.jsp](file:///c:/Users/Dell/TTCS_WEB/web/views/profile.jsp) để điền sẵn thông tin vào Form.
  * **POST Request (Cập nhật)**: Khi người dùng bấm **Cập Nhật**:
    1. Lấy dữ liệu mới (họ tên, email, điện thoại, địa chỉ, thành phố, ngày sinh, địa chỉ chi tiết) từ Form.
    2. Cập nhật các trường thông tin vào đối tượng `User`.
    3. Gọi hàm `userDAO.updateUser(user)` để thực hiện câu lệnh UPDATE trong MySQL.
    4. Ghi đè đối tượng `User` mới vào trong Session để cập nhật thông tin hiển thị trên toàn hệ thống.
* **Kết thúc**: Chuyển hướng quay lại trang `/profile` kèm thông báo thành công.

---

### 12. Luồng Quản Lý Sản Phẩm Của Admin (Admin Product CRUD Flow)
* **Khởi đầu (JSP)**: Admin click vào mục **Sản phẩm** trên menu quản trị dẫn tới URL `/admin/products` ứng với giao diện [manage_products.jsp](file:///c:/Users/Dell/TTCS_WEB/web/admin/manage_products.jsp).
* **Xử lý tại Servlet**: Lớp [ManageProductServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/admin/ManageProductServlet.java) tiếp nhận:
  * **GET Request**: Lấy toàn bộ sản phẩm qua `productDAO.getAllProducts()` và danh mục qua `categoryDAO.getAllCategories()` để hiển thị lên bảng quản lý.
  * **POST Request (Thêm/Sửa/Xóa)**: Xử lý theo tham số `action`:
    * *Thêm sản phẩm (`action = add`)*: Lấy thông tin từ Form (tên, giá bán, giá nhập, số lượng, ảnh, danh mục, metadata) ➔ khởi tạo `Product` ➔ gọi `productDAO.addProduct(product)` ➔ Redirect.
    * *Sửa sản phẩm (`action = edit`)*: Lấy thông tin sửa ➔ gọi `productDAO.updateProduct(product)` ➔ Redirect.
    * *Xóa sản phẩm (`action = delete`)*: Lấy `id` ➔ gọi `productDAO.deleteProduct(productId)` ➔ Redirect.
* **Kết thúc**: Chuyển hướng quay lại trang quản lý `/admin/products`.

---

### 13. Luồng Quản Lý Người Dùng Của Admin (Admin User Management Flow)
* **Khởi đầu (JSP)**: Admin click vào mục **Khách hàng** dẫn tới URL `/admin/users` ứng với giao diện [manage_users.jsp](file:///c:/Users/Dell/TTCS_WEB/web/admin/manage_users.jsp).
* **Xử lý tại Servlet**: Lớp [ManageUserServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/admin/ManageUserServlet.java) tiếp nhận:
  * **GET Request**: Gọi `userDAO.getAllUsers()` để lấy danh sách người dùng hiển thị lên bảng.
  * **POST Request (Khóa/Mở khóa)**:
    1. Nhận tham số `userId` và trạng thái `status` (1: Mở khóa, 0: Khóa).
    2. Gọi hàm `userDAO.updateUserStatus(userId, status)`. Nếu khóa, đổi vai trò thành `DISABLED` để chặn đăng nhập qua `LoginServlet`. Nếu mở khóa, khôi phục vai trò thành `CUSTOMER`.
* **Kết thúc**: Chuyển hướng quay lại trang `/admin/users`.

---

### 14. Luồng Quản Lý Đơn Hàng Của Admin (Admin Order Management Flow)
* **Khởi đầu (JSP)**: Admin click vào mục **Đơn hàng** dẫn tới URL `/admin/orders` ứng với giao diện [manage_orders.jsp](file:///c:/Users/Dell/TTCS_WEB/web/admin/manage_orders.jsp).
* **Xử lý tại Servlet**: Lớp [ManageOrderServlet.java](file:///c:/Users/Dell/TTCS_WEB/src/java/controller/admin/ManageOrderServlet.java) tiếp nhận:
  * **GET Request**: Gọi `orderDAO.getAllOrders()` để lấy danh sách đơn đặt hàng hiển thị lên bảng quản lý.
  * **POST Request (Cập nhật trạng thái)**:
    1. Nhận mã đơn hàng `orderId` và trạng thái cập nhật `status` (ví dụ: duyệt sang "DELIVERED" hoặc "CANCELLED").
    2. Gọi hàm `orderDAO.updateOrderStatus(orderId, status)` để ghi nhận trạng thái mới trong MySQL.
* **Kết thúc**: Chuyển hướng quay lại trang `/admin/orders`.

---

## 💻 PHẦN 5: GIẢI THÍCH CHI TIẾT CÁC HÀM TRONG TẦNG DAO
Tầng DAO đảm nhận nhiệm vụ truy vấn cơ sở dữ liệu và chuyển đổi từ các dòng kết quả ResultSet thành các đối tượng Java (Beans).

### 1. `UserDAO.java` (Thao tác với bảng `users`)
* `getAllUsers()`: Lấy danh sách tất cả các tài khoản khách hàng (`role = 'CUSTOMER'`) để Admin quản lý.
* `getUserById(int userId)`: Tìm kiếm thông tin chi tiết của một người dùng theo mã định danh.
* `getUserByUsername(String username)`: Lấy thông tin tài khoản dựa theo tên đăng nhập (dùng cho quá trình đăng nhập/đăng ký).
* `login(String username, String password)`: Kiểm tra thông tin đăng nhập. Gọi hàm băm MD5 trong `SecurityUtil.verifyPassword()` để so khớp mật khẩu đã mã hóa lưu trong DB. Trả về đối tượng `User` nếu khớp, ngược lại trả về `null`.
* `isUsernameExists(String username)`: Kiểm tra xem tên tài khoản đã tồn tại trong hệ thống chưa trước khi cho phép đăng ký mới.
* `registerUser(User user)`: Đăng ký thành viên mới. Thực hiện câu lệnh SQL `INSERT INTO Users` với các thông tin đăng ký và mặc định gán vai trò của tài khoản là `CUSTOMER`.
* `updateUser(User user)`: Cho phép người dùng cập nhật thông tin cá nhân (họ tên, email, điện thoại, địa chỉ chi tiết, ngày sinh, thành phố) tại trang Profile.
* `changePassword(int userId, String newPassword)`: Cập nhật mật khẩu mới cho tài khoản (mật khẩu mới được băm MD5 trước khi lưu).
* `updateUserStatus(int userId, int status)`: Cho phép Admin Khóa/Mở khóa tài khoản khách hàng bằng cách thay đổi giá trị cột `role` thành `DISABLED` hoặc ngược lại.

### 2. `CategoryDAO.java` (Thao tác với bảng `categories`)
* `getAllCategories()`: Trả về danh sách tất cả các danh mục sản phẩm trong cơ sở dữ liệu, sắp xếp theo thứ tự bảng chữ cái.
* `getCategoryById(String categoryId)`: Tìm và trả về danh mục tương ứng với ID truyền vào.
* `addCategory(Category category)`: Thêm một danh mục sản phẩm mới vào cơ sở dữ liệu.
* `updateCategory(Category category)`: Cập nhật lại tên hiển thị của danh mục.
* `deleteCategory(String categoryId)`: Xóa danh mục ra khỏi hệ thống theo mã ID.

### 3. `ProductDAO.java` (Thao tác với bảng `products`)
* `getAllProducts()`: Lấy danh sách tất cả sản phẩm hiện có trong cơ sở dữ liệu để hiển thị cho Admin hoặc màn hình chính.
* `getProductById(int productId)`: Lấy thông tin chi tiết của một sản phẩm cụ thể theo mã ID.
* `getProductsByCategory(String categoryName)`: Lấy toàn bộ sản phẩm thuộc một danh mục cụ thể bằng cách thực hiện phép JOIN giữa bảng `products` và `categories`.
* `searchProducts(String keyword)`: Tìm kiếm sản phẩm theo tên chứa từ khóa (dùng câu lệnh SQL chứa `LIKE %keyword%`).
* `getTrendingProducts(int limit)`: **[Khối 3 AI]** Lấy ra danh sách sản phẩm bán chạy nhất hệ thống. Hàm thực hiện truy vấn JOIN bảng `products` với `order_details`, gom nhóm (`GROUP BY`) và sắp xếp theo số lượt xuất hiện trong hóa đơn giảm dần (`ORDER BY COUNT(od.order_detail_id) DESC`), giới hạn số lượng trả về theo tham số `limit`.
* `getProductsByIds(List<Integer> productIds)`: Lấy nhanh danh sách thông tin sản phẩm từ danh sách các ID sản phẩm truyền vào (sử dụng toán tử `IN` trong câu lệnh SQL động).
* `addProduct(Product product)`: Thêm mới sản phẩm bao gồm đầy đủ các trường dữ liệu như giá bán, giá nhập, số lượng kho, ảnh và dữ liệu metadata dành cho AI.
* `updateProduct(Product product)`: Cập nhật toàn bộ thông tin sản phẩm khi Admin sửa đổi.
* `deleteProduct(int productId)`: Xóa sản phẩm khỏi cơ sở dữ liệu.
* `getProductsByCategoryId(int categoryId, int excludeProductId, int limit)`: Hàm Fallback thông minh. Nếu sản phẩm chưa có gợi ý từ mô hình AI Python, hệ thống sẽ gọi hàm này để lấy các sản phẩm cùng danh mục để gợi ý cho khách hàng (loại trừ chính sản phẩm đang xem).
* `mapResultSetToProduct(ResultSet rs)`: **[Hàm cực kỳ quan trọng]** Phục vụ việc ánh xạ cơ sở dữ liệu sang đối tượng Java. 
  * Hàm thực hiện cơ chế lập trình phòng thủ: Đọc các cột mới bổ sung (`image`, `quantity`, `import_price`) một cách an toàn bằng các khối `try-catch`, tránh sập trang 500 nếu cấu trúc DB thay đổi.
  * Phân tích cú pháp chuỗi JSON lưu trong cột `specific_attributes` bằng biểu thức chính quy (Regex Pattern) để tách các cặp thuộc tính kỹ thuật.
  * Tự động làm sạch văn bản (chuyển đổi thô không dấu như "Long vit" -> "Lông vịt", "12 qua/ong" -> "12 quả / ống") để hiển thị tiếng Việt có dấu đẹp mắt.
  * Gán các thuộc tính cơ bản linh hoạt theo loại danh mục (Vợt, Giày, Ống Cầu) giúp tối ưu hóa giao diện.

### 4. `OrderDAO.java` (Thao tác với bảng `orders` và `order_details`)
* `getAllOrders()`: Lấy toàn bộ đơn hàng của cửa hàng sắp xếp từ mới nhất đến cũ nhất.
* `getOrderById(int orderId)`: Lấy thông tin một hóa đơn cùng với danh sách chi tiết các mặt hàng nằm trong hóa đơn đó.
* `getOrdersByUserId(int userId)`: Lấy danh sách lịch sử mua hàng của một khách hàng cụ thể.
* `createOrder(Order order)`: Tạo đơn hàng thô đơn giản.
* `checkout(Order order, List<CartItem> cart)`: **[Hàm quan trọng nhất luồng nghiệp vụ]** Thực hiện quy trình đặt hàng và trừ kho nguyên tử trong một **Database Transaction** duy nhất:
  ```java
  Connection conn = DBContext.getConnection();
  conn.setAutoCommit(false); // Bắt đầu Transaction
  ```
  * *Bước 1*: Thực hiện thêm đơn hàng vào bảng `orders`, lấy ra `order_id` vừa được tự động sinh.
  * *Bước 2*: Lặp qua danh sách giỏ hàng `cart` để thêm chi tiết đơn vào bảng `order_details` thông qua kỹ thuật **Batch Execution** để tối ưu hóa hiệu năng kết nối DB.
  * *Bước 3*: Đồng thời thực hiện câu lệnh SQL trừ kho hàng trực tiếp cho từng sản phẩm:
    ```sql
    UPDATE products SET quantity = GREATEST(0, quantity - ?) WHERE product_id = ?
    ```
    Sử dụng hàm toán học `GREATEST(0, ...)` đảm bảo số lượng tồn kho tối thiểu là 0, không bao giờ bị âm kho kể cả khi xảy ra tranh chấp đồng thời.
  * *Bước 4*: Nếu toàn bộ các bước thành công không xảy ra lỗi, gọi lệnh `conn.commit()` để lưu vĩnh viễn thay đổi vào CSDL.
  * *Bước 5*: Nếu có bất kỳ ngoại lệ `SQLException` nào xảy ra ở bất kỳ dòng code nào, hệ thống lập tức bắt ngoại lệ và chạy lệnh **`conn.rollback()`** để khôi phục toàn bộ trạng thái dữ liệu trước khi thực hiện giao dịch, đảm bảo tính toàn vẹn dữ liệu tuyệt đối (Atomicity).
* `updateOrderStatus(int orderId, String status)`: Admin cập nhật trạng thái đơn hàng (ví dụ: duyệt đơn sang "Đã giao" hoặc hủy đơn).
* `getOrderDetails(int orderId)`: Truy vấn danh sách các mặt hàng chi tiết của một mã hóa đơn.
* `addOrderDetail(int orderId, OrderDetail detail)`: Thêm một chi tiết hóa đơn lẻ (cũng bọc trong một Transaction nhỏ kèm trừ kho hàng).
* `getRevenueByMonth()`: Hàm thống kê dữ liệu kinh doanh của doanh nghiệp. Thực hiện tính toán tổng doanh thu (`SUM(quantity * unit_price)`) và tổng lợi nhuận thực tế dựa trên chênh lệch giữa giá bán và giá nhập (`SUM(quantity * (unit_price - import_price))`). Hàm được tối ưu hóa tương thích với cấu hình strict mode **`ONLY_FULL_GROUP_BY`** của MySQL bằng cách gom nhóm chuẩn xác và sử dụng hàm gom nhóm `MIN(o.created_at)` trong mệnh đề sắp xếp.

### 5. `InteractionDAO.java` (Thao tác với bảng `user_interactions`)
* `recordInteraction(int userId, int productId, String interactionType)`: Ghi nhận hành vi tương tác của người dùng. Hệ thống tự động ánh xạ loại tương tác sang điểm số tương ứng để lưu vào DB: `VIEW` = 1 điểm, `ADD_TO_CART` = 3 điểm, `PURCHASE` = 5 điểm.
* `getLastViewedProduct(int userId)`: Lấy ra ID của sản phẩm mà khách hàng xem gần đây nhất.
* `getRecentViewedProducts(int userId, int limit)`: Lấy danh sách ID các sản phẩm khách hàng đã quan tâm gần đây (có hành vi `VIEW` hoặc `ADD_TO_CART`), nhưng bộ lọc SQL thông minh đã **loại trừ các sản phẩm mà khách hàng đã từng đặt mua thành công trước đó** để tránh gợi ý thừa thãi, nâng cao trải nghiệm mua sắm. Sắp xếp theo thời gian tối đa gần nhất (`ORDER BY MAX(created_at) DESC`).
* `getPurchasedProducts(int userId)`: Lấy danh sách ID các sản phẩm người dùng đã mua thành công (đơn hàng ở trạng thái `DELIVERED`).

### 6. `RecommendationDAO.java` (Thao tác với bảng `product_recommendations`)
* `getSimilarProductsByContent(int productId, int limit)`: **[Khối 1 AI - Content-Based]** Truy vấn danh sách sản phẩm tương tự với sản phẩm hiện tại bằng cách tìm trong bảng `product_recommendations` được tính toán sẵn bởi mô hình Python TF-IDF, sắp xếp theo điểm tương đồng giảm dần.
* `getCollaborativeFilteringRecommendations(int userId, int limit)`: **[Khối 2 AI - Collaborative Filtering]** Đây là hàm gợi ý mua kèm cực kỳ thông minh được viết hoàn toàn bằng câu lệnh SQL JOIN phức tạp:
  ```sql
  SELECT p.product_id FROM products p 
  JOIN order_details od ON p.product_id = od.product_id 
  WHERE od.order_id IN (
    SELECT o.order_id FROM orders o 
    WHERE o.user_id IN (
      SELECT DISTINCT o2.user_id FROM orders o2 
      JOIN order_details od2 ON o2.order_id = od2.order_id 
      WHERE od2.product_id IN (
        SELECT DISTINCT od3.product_id FROM order_details od3 
        JOIN orders o3 ON od3.order_id = o3.order_id 
        WHERE o3.user_id = ?
      )
    )
  )
  AND p.product_id NOT IN (
    SELECT DISTINCT od4.product_id FROM order_details od4 
    JOIN orders o4 ON od4.order_id = o4.order_id 
    WHERE o4.user_id = ?
  ) 
  GROUP BY p.product_id 
  ORDER BY COUNT(*) DESC 
  LIMIT ?
  ```
  * **Giải thích logic câu truy vấn**:
    1. Tìm tất cả các sản phẩm mà người dùng hiện tại đã từng mua.
    2. Tìm tất cả các người dùng khác (gọi là những người dùng có chung sở thích) cũng đã từng mua ít nhất một sản phẩm nằm trong danh sách sản phẩm đã mua của người dùng hiện tại.
    3. Lấy tất cả các hóa đơn đặt hàng của các người dùng tương đồng đó.
    4. Gom toàn bộ sản phẩm có trong các hóa đơn đó, lọc bỏ đi các sản phẩm mà người dùng hiện tại đã mua rồi (để tránh đề xuất lại).
    5. Sắp xếp các sản phẩm còn lại theo tần suất được mua nhiều nhất bởi nhóm người dùng tương đồng kia và gợi ý ra màn hình.
* `saveRecommendation(ProductRecommendation recommendation)`: Hàm được sử dụng bởi script Python để nạp trực tiếp kết quả tính toán độ tương đồng Cosine vào cơ sở dữ liệu (`ON DUPLICATE KEY UPDATE` để tránh trùng lặp bản ghi).
* `clearRecommendations()`: Xóa sạch bảng kết quả gợi ý cũ trước khi chạy lại tiến trình tính toán AI mới để làm sạch dữ liệu.

---

## 🤖 PHẦN 6: CƠ CHẾ HOẠT ĐỘNG CỦA HỆ THỐNG GỢI Ý AI
Hệ thống AI Engine được thiết kế lai (Hybrid Recommender System) kết hợp cả 3 phương pháp tiếp cận để mang lại kết quả tối ưu nhất:

### 1. Khối 1: Content-Based Filtering (Lọc dựa trên nội dung kỹ thuật)
* **Nguyên lý**: Tìm các sản phẩm tương tự nhau dựa trên đặc trưng nội dung.
* **Quy trình xử lý của Python script** (`thuat_toan_content.py`):
  1. Kết nối cơ sở dữ liệu MySQL, đọc toàn bộ thông tin sản phẩm bao gồm cột `metadata_for_ai` (chứa chuỗi đặc trưng được tổng hợp như: `"Vợt cầu lông Yonex Astrox Lite 45i Scarlet Danh mục: Vợt Thông số: Trọng lượng 4U Độ cứng Trung bình Mức căng 20-28 lbs Lối chơi Thiên công"`).
  2. Sử dụng thư viện **Scikit-learn** khởi tạo bộ hóa vector **TF-IDF (Term Frequency - Inverse Document Frequency)** để phân tích tần suất xuất hiện của các thuật ngữ chuyên ngành cầu lông và chuyển đổi các mô tả sản phẩm dạng văn bản thành các vector toán học trong không gian nhiều chiều.
  3. Sử dụng hàm **`cosine_similarity`** để tính toán góc Cosine giữa các vector đại diện của từng cặp sản phẩm. Điểm số nằm trong khoảng `[0, 1]`. Điểm số càng gần 1 chứng tỏ hai sản phẩm càng giống nhau về mặt kỹ thuật và lối chơi.
  4. Sắp xếp và lấy ra Top 4 sản phẩm có điểm tương đồng cao nhất cho mỗi sản phẩm gốc.
  5. Xóa dữ liệu cũ bằng hàm `clearRecommendations()` và gọi `saveRecommendation()` để ghi đè kết quả trực tiếp vào bảng `product_recommendations`.
* **Quy trình của Servlet**: Khi người dùng xem trang chi tiết sản phẩm A, `ProductDetailServlet` sẽ gọi `RecommendationDAO.getSimilarProductsByContent(productId, 4)` để lấy ra ngay danh sách các sản phẩm tương tự mà không cần phải tính toán lại từ đầu, đảm bảo thời gian tải trang nhanh tức thì.

### 2. Khối 2: Collaborative Filtering (Lọc cộng tác dựa trên hành vi người dùng)
* **Nguyên lý**: Gợi ý các sản phẩm dựa trên hành vi mua sắm của cộng đồng.
* **Quy trình**: Hệ thống phân tích lịch sử đặt hàng thông qua hàm `getCollaborativeFilteringRecommendations()` trong `RecommendationDAO.java`. Việc tính toán được thực hiện trực tiếp bằng câu lệnh SQL JOIN lồng nhau ở tầng Database, giúp hệ thống hoạt động hoàn toàn tự động theo thời gian thực mà không cần phụ thuộc vào tiến trình Python bên ngoài.
* **Kịch bản thực tế**: Người dùng B mua Vợt Scarlet (ID = 1) và Vợt Dark/Grey (ID = 2). Người dùng A cũng mua Vợt Scarlet (ID = 1). Hệ thống nhận diện A và B cùng sở thích mua Vợt Scarlet. Khi A truy cập trang chủ, hệ thống tự động gợi ý sản phẩm B đã mua mà A chưa mua là Vợt Dark/Grey (ID = 2) hiển thị ngay tại khối "Có thể bạn sẽ thích".

### 3. Khối 3: Trending Products (Giải quyết bài toán Cold Start)
* **Nguyên lý**: Khi khách hàng mới truy cập lần đầu hoặc chưa đăng nhập, hệ thống chưa có dữ liệu hành vi (`user_interactions`) và lịch sử mua sắm của họ để chạy Lọc cộng tác hay Lọc nội dung.
* **Quy trình**: Hệ thống tự động chuyển sang hiển thị các sản phẩm bán chạy nhất hệ thống (được lấy ra từ hàm `getTrendingProducts()` của `ProductDAO.java`). Điều này giúp trang chủ luôn hiển thị đầy đủ thông tin hấp dẫn, lôi cuốn người dùng tương tác để bắt đầu ghi nhận dữ liệu hành vi.

---

## 👩‍🏫 PHẦN 7: BỘ CÂU HỎI VẤN ĐÁP BẢO VỆ ĐỒ ÁN (DÀNH CHO SINH VIÊN)
*Dưới đây là các câu hỏi giảng viên thường đặt ra khi chấm vấn đáp và gợi ý câu trả lời chuẩn học thuật, giúp bạn tự tin đạt điểm tối đa.*

### ❓ Câu 1: Em hãy giải thích Cơ chế Database Transaction (Giao dịch cơ sở dữ liệu) được sử dụng khi Checkout đơn hàng trong code của em? Tại sao bắt buộc phải dùng nó?
* **Trả lời**: 
  * Trong dự án của em, khi khách hàng nhấn đặt hàng, hệ thống cần thực hiện 3 hành động xuống database: (1) Thêm 1 dòng vào bảng `orders`, (2) Thêm nhiều dòng vào bảng `order_details`, và (3) Trừ số lượng tồn kho của từng sản phẩm trong bảng `products`.
  * Để đảm bảo tính toàn vẹn dữ liệu, em đã đóng gói toàn bộ các hành động này vào trong một **Transaction** duy nhất bằng cách đặt lệnh `conn.setAutoCommit(false)` ở đầu hàm `checkout()`.
  * Nếu toàn bộ các bước thành công, lệnh `conn.commit()` được gọi để áp dụng thay đổi xuống CSDL. Nếu có bất kỳ lỗi nào xảy ra ở giữa quá trình (ví dụ: bị mất điện, lỗi cú pháp hoặc sản phẩm bị hết hàng), khối `catch` sẽ bắt ngoại lệ và chạy lệnh `conn.rollback()` để khôi phục toàn bộ dữ liệu trở lại trạng thái ban đầu giống như chưa hề có giao dịch đặt hàng xảy ra.
  * Nếu không sử dụng Transaction, hệ thống có thể rơi vào trạng thái bất nhất dữ liệu cực kỳ nguy hiểm: Ví dụ đơn hàng đã tạo thành công và trừ tiền của khách nhưng chi tiết đơn hàng bị lỗi không lưu được, dẫn tới đơn hàng trống rỗng; hoặc ngược lại tạo được hóa đơn nhưng kho hàng không được trừ đi, dẫn tới bán quá số lượng thực tế trong kho (Over-selling).

### ❓ Câu 2: Em giải quyết lỗi strict mode `ONLY_FULL_GROUP_BY` của MySQL khi viết câu lệnh SQL thống kê doanh thu theo tháng như thế nào?
* **Trả lời**:
  * Chế độ `ONLY_FULL_GROUP_BY` của MySQL yêu cầu tất cả các cột xuất hiện trong mệnh đề `SELECT` (mà không nằm trong các hàm gộp như `SUM`, `COUNT`, `AVG`) bắt buộc phải xuất hiện trong mệnh đề `GROUP BY`.
  * Trong câu lệnh thống kê doanh thu theo tháng của em trong hàm `getRevenueByMonth()`, em thực hiện `SELECT DATE_FORMAT(o.created_at, '%m-%Y') as month` và thực hiện gom nhóm `GROUP BY DATE_FORMAT(o.created_at, '%m-%Y')`.
  * Tuy nhiên, nếu em muốn sắp xếp theo ngày tạo `ORDER BY o.created_at`, MySQL strict mode sẽ báo lỗi vì `o.created_at` không nằm trong `GROUP BY`. Để khắc phục lỗi này mà vẫn giữ được logic sắp xếp theo thời gian chính xác, em đã thay đổi mệnh đề sắp xếp thành `ORDER BY MIN(o.created_at) DESC`. Hàm `MIN()` là một hàm gộp (aggregate function) nên nó hoàn toàn hợp lệ và được MySQL chấp nhận mà không cần tắt chế độ strict mode của database.

### ❓ Câu 3: Thuật toán TF-IDF và Cosine Similarity hoạt động thế nào trong Engine AI gợi ý của em?
* **Trả lời**:
  * **TF-IDF (Term Frequency - Inverse Document Frequency)** gồm hai thành phần:
    * **TF (Tần suất xuất hiện của từ)**: Đo lường mức độ thường xuyên của một từ xuất hiện trong mô tả sản phẩm. Từ nào xuất hiện càng nhiều trong mô tả của sản phẩm đó thì có trọng số càng lớn.
    * **IDF (Tần suất nghịch đảo của từ trong tập dữ liệu)**: Giảm trọng số của các từ xuất hiện quá phổ biến ở mọi sản phẩm (ví dụ: "sản phẩm", "đẹp", "chính hãng") và tăng trọng số cho các từ mang tính đặc trưng, chuyên sâu xuất hiện ít (ví dụ: "nặng đầu", "thiên công", "tru cushion").
    * Kết quả của TF-IDF giúp chuyển đổi chuỗi văn bản `metadata_for_ai` của từng sản phẩm thành một vector số học đại diện.
  * **Cosine Similarity (Độ tương đồng Cosine)**: Đo góc giữa hai vector sản phẩm trong không gian đa chiều. Công thức tính bằng tích vô hướng chia cho tích độ dài của hai vector. Kết quả trả về điểm số từ 0 đến 1. Điểm càng gần 1 chứng tỏ hai sản phẩm có thông số kỹ thuật và lối chơi cực kỳ tương đồng.
  * Trong dự án, Python script tính toán ma trận độ tương đồng này, lọc lấy Top 4 sản phẩm giống nhất cho mỗi sản phẩm và lưu sẵn vào bảng `product_recommendations` để Servlet Java truy cập nhanh chóng.

### ❓ Câu 4: Làm thế nào để phân biệt thuộc tính động của Vợt, Giày và Ống Cầu khi hiển thị lên giao diện chi tiết sản phẩm? Em thiết kế cơ sở dữ liệu như thế nào để đáp ứng điều này?
* **Trả lời**:
  * Các sản phẩm cầu lông có các thông số kỹ thuật hoàn toàn khác nhau. Vợt thì cần trọng lượng, độ cứng, mức căng dây. Giày cần công nghệ đệm, phân khúc. Ống cầu cần tốc độ, chất liệu cánh cầu. Nếu tạo mỗi cột riêng cho từng thông số trong bảng `products` thì bảng sẽ bị loãng, có nhiều trường bị giá trị `NULL` và rất khó mở rộng khi bán thêm mặt hàng mới.
  * Để giải quyết tối ưu bài toán này, em đã thiết kế cột **`specific_attributes`** trong bảng `products` lưu trữ dưới dạng chuỗi **JSON** (ví dụ: `{"TrongLuong": "4U", "DoCung": "Trung binh", "Play_Style": "Cong thu toan dien"}`).
  * Khi ánh xạ dữ liệu trong hàm `mapResultSetToProduct` của `ProductDAO.java`, em sử dụng Regex Pattern để phân tích chuỗi JSON này thành một Map `Map<String, String>`. Tùy thuộc vào mã danh mục (`category_id`) hoặc tên sản phẩm, em sẽ gán các giá trị tương ứng vào các thuộc tính hiển thị tương thích. Lối thiết kế này giúp hệ thống linh hoạt, dễ dàng mở rộng và tối ưu hiệu năng CSDL.

### ❓ Câu 5: Hệ thống bảo mật mật khẩu của người dùng như thế nào?
* **Trả lời**:
  * Hệ thống không bao giờ lưu mật khẩu dạng văn bản thô (plain text) vào cơ sở dữ liệu vì lý do bảo mật.
  * Khi người dùng đăng ký tài khoản hoặc đổi mật khẩu, mật khẩu thô sẽ được chuyển qua hàm `SecurityUtil.hashPasswordMD5()` để thực hiện băm bằng thuật toán **MD5**. Kết quả là một chuỗi mã hóa 32 ký tự không thể dịch ngược và chuỗi này mới được lưu vào cột `password_hash` của bảng `users`.
  * Khi đăng nhập, mật khẩu do người dùng nhập vào cũng sẽ được băm MD5 và so sánh với chuỗi băm sẵn trong cơ sở dữ liệu để xác minh danh tính.

### ❓ Câu 6: Filter (Bộ lọc) trong dự án của em giải quyết vấn đề gì?
* **Trả lời**:
  * Dự án của em sử dụng hai bộ lọc chính kế thừa giao diện `Filter` của Java Servlet:
    * **`AdminFilter.java`**: Phục vụ việc phân quyền (Authorization). Bộ lọc này chặn các request gửi tới đường dẫn quản trị `/admin/*`. Nó sẽ kiểm tra đối tượng `User` trong `Session`, nếu người dùng chưa đăng nhập hoặc có `role` không phải là `ADMIN` thì hệ thống lập tức từ chối quyền truy cập và chuyển hướng về trang đăng nhập, bảo vệ an toàn cho các trang quản lý.
    * **`EncodingFilter.java`**: Đảm bảo đồng bộ hóa bộ mã hóa ký tự UTF-8 cho toàn bộ các request và response trong hệ thống (`request.setCharacterEncoding("UTF-8")`), tránh triệt để lỗi hiển thị font chữ tiếng Việt trên giao diện web.

### ❓ Câu 7: Phép toán GREATEST(0, quantity - ?) trong câu lệnh UPDATE kho hàng có ý nghĩa gì?
* **Trả lời**:
  * Phép toán này nhằm ngăn ngừa lỗi số lượng tồn kho bị âm. Khi có nhiều người dùng đồng thời mua cùng một sản phẩm có số lượng tồn kho còn rất ít, nếu không kiểm soát tốt, việc trừ kho có thể làm giá trị cột `quantity` giảm xuống dưới 0.
  * Việc sử dụng hàm `GREATEST(0, quantity - ?)` của MySQL đảm bảo rằng nếu hiệu số `quantity - ?` nhỏ hơn 0, MySQL sẽ tự động lấy giá trị lớn hơn là `0`, giữ cho số lượng tồn kho luôn luôn hợp lệ.

---

## 🛠️ PHẦN 8: HƯỚNG DẪN KHỞI CHẠY DỰ ÁN
Để chạy dự án trên máy tính cục bộ, thực hiện theo các bước sau:

### 1. Cấu hình Cơ sở dữ liệu MySQL
* Khởi động MySQL Server.
* Tạo cơ sở dữ liệu mới tên là `badminton_shop`.
* Import database mẫu hoặc chạy hai file SQL cấu hình đi kèm dự án:
  1. Chạy file `setup_import_price.sql` để cấu hình trường giá nhập.
  2. Chạy file `setup_test_data.sql` để thiết lập dữ liệu kho hàng và tạo sẵn kịch bản người dùng mua hàng phục vụ chạy thử nghiệm gợi ý AI.
* Mở file [DBContext.java](file:///c:/Users/Dell/TTCS_WEB/src/java/util/DBContext.java) tại đường dẫn `src/java/util/DBContext.java` và chỉnh sửa thông số kết nối CSDL (URL, Username, Password) cho khớp với MySQL trên máy của bạn.

### 2. Khởi chạy Ứng dụng Web bằng NetBeans
* Mở NetBeans IDE và Import dự án `TTCS_WEB`.
* Chọn máy chủ **Apache Tomcat** (phiên bản 9.x hoặc 11.x) làm Web Server.
* Nhấp chuột phải vào dự án chọn **Clean and Build** để biên dịch ứng dụng.
* Chọn **Run** để khởi chạy Server. Trình duyệt sẽ tự động mở trang chủ của Smash Zone tại địa chỉ: `http://localhost:8080/TTCS_WEB/home`.

### 3. Chạy thuật toán gợi ý AI Python (Content-Based Engine)
* Đảm bảo máy tính đã cài đặt **Python 3.x**.
* Di chuyển vào thư mục dự án chứa thuật toán gợi ý (ví dụ: `C:\Users\Dell\PythonProject`).
* Cài đặt các thư viện cần thiết bằng lệnh:
  ```bash
  pip install pandas scikit-learn mysql-connector-python
  ```
* Chạy kịch bản Python để tính toán lại độ tương đồng và nạp dữ liệu gợi ý vào MySQL:
  ```bash
  python recomment/thuat_toan_content.py
  ```
* Sau khi tiến trình báo `HOAN TAT!`, bạn tải lại trang chi tiết sản phẩm trên trình duyệt để thưởng thức các gợi ý sản phẩm tương tự vô cùng thông minh từ mô hình AI.

---
*Chúc bạn có một buổi bảo vệ đồ án thành công rực rỡ và đạt điểm số xuất sắc!*
