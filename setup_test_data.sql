-- ==========================================
-- SQL SCRIPT CẤU HÌNH CỘT SỐ LƯỢNG (QUANTITY)
-- VÀ TẠO DATA TEST CHỨC NĂNG GỢI Ý BÁN KÈM (COLLABORATIVE FILTERING)
-- ==========================================

USE badminton_shop;

-- ------------------------------------------
-- BƯỚC 1: THIẾT KẾ CỘT SỐ LƯỢNG (QUANTITY)
-- ------------------------------------------
-- 1. Thêm cột 'quantity' vào bảng 'products' nếu chưa có (mặc định số lượng ban đầu là 100 sản phẩm)
ALTER TABLE products ADD COLUMN quantity INT DEFAULT 100;

-- 2. Cập nhật số lượng của toàn bộ sản phẩm hiện tại về 100 để bắt đầu quản lý kho và chạy test trừ kho
UPDATE products SET quantity = 100;

-- ------------------------------------------
-- BƯỚC 2: TẠO DỮ LIỆU ĐỂ TEST AI BÁN KÈM (COLLABORATIVE FILTERING - KHỐI 2)
-- KỊCH BẢN TEST:
-- - Ta có Khách hàng A (Tester, User ID = 4, Username = B23DCCN543)
-- - Ta tạo thêm một Khách hàng B (Collaborator, User ID = 999) làm người dùng tương đồng mua chung.
-- - Khách hàng B mua: "Vợt Yonex Astrox Lite 45i - Scarlet" (ID = 1) VÀ "Vợt Yonex Astrox Lite 45i - Dark/Grey" (ID = 2)
-- - Khách hàng A (Tester) cũng mua: "Vợt Yonex Astrox Lite 45i - Scarlet" (ID = 1).
-- => KẾT QUẢ: Hệ thống nhận diện Khách hàng A và B có chung sở thích mua Vợt Scarlet (ID = 1). 
--    Khi A đăng nhập vào trang chủ, hệ thống sẽ gợi ý sản phẩm B bought mà A chưa mua: "Vợt Yonex Astrox Lite 45i - Dark/Grey" (ID = 2) 
--    ngay tại Khối 2: "Có thể bạn sẽ thích"!
-- ------------------------------------------

-- 1. Tạo Khách hàng B (Người dùng cộng tác mua chung)
INSERT INTO users (user_id, username, password_hash, email, full_name, role)
VALUES (999, 'collaborator_user', '202cb962ac59075b964b07152d234b70', 'collaborator@gmail.com', 'Nguyễn Văn Cộng Tác', 'CUSTOMER')
ON DUPLICATE KEY UPDATE full_name = 'Nguyễn Văn Cộng Tác';

-- 2. Xóa các đơn hàng thử nghiệm cũ của User 4 và User 999 để dữ liệu test sạch sẽ, chính xác 100%
DELETE FROM order_details WHERE order_id IN (9998, 9999);
DELETE FROM orders WHERE order_id IN (9998, 9999);

-- 3. Tạo Đơn hàng cho Khách hàng B (User 999): Đã mua cùng lúc cả sản phẩm ID = 1 và ID = 2
-- Sử dụng status = 'DELIVERED' khớp với ENUM của bảng orders
INSERT INTO orders (order_id, user_id, total_amount, status, created_at)
VALUES (9999, 999, 1418000.00, 'DELIVERED', CURRENT_TIMESTAMP);

-- Thêm chi tiết đơn hàng cho Khách hàng B
INSERT INTO order_details (order_detail_id, order_id, product_id, quantity, unit_price)
VALUES 
(99991, 9999, 1, 1, 709000.00), -- Vợt Yonex Astrox Lite 45i Scarlet (ID = 1)
(99992, 9999, 2, 1, 709000.00); -- Vợt Yonex Astrox Lite 45i Dark/Grey (ID = 2)

-- 4. Tạo Đơn hàng cho Khách hàng A (Tester, User ID = 4): Đã mua sản phẩm ID = 1 (Scarlet)
-- Sử dụng status = 'DELIVERED' khớp với ENUM của bảng orders
INSERT INTO orders (order_id, user_id, total_amount, status, created_at)
VALUES (9998, 4, 709000.00, 'DELIVERED', CURRENT_TIMESTAMP);

-- Thêm chi tiết đơn hàng cho Khách hàng A
INSERT INTO order_details (order_detail_id, order_id, product_id, quantity, unit_price)
VALUES 
(99981, 9998, 1, 1, 709000.00); -- Vợt Yonex Astrox Lite 45i Scarlet (ID = 1)

-- 5. Ghi nhận tương tác cho AI Recommender System (Bảng user_interactions)
-- (VIEW = 1 điểm, ADD_TO_CART = 3 điểm, PURCHASE = 5 điểm)
DELETE FROM user_interactions WHERE user_id IN (4, 999);

-- Tương tác của Khách hàng B (User 999)
INSERT INTO user_interactions (user_id, product_id, interaction_type, interaction_value)
VALUES 
(999, 1, 'PURCHASE', 5),
(999, 2, 'PURCHASE', 5);

-- Tương tác của Khách hàng A (Tester, User 4)
INSERT INTO user_interactions (user_id, product_id, interaction_type, interaction_value)
VALUES 
(4, 1, 'PURCHASE', 5);

-- -------------------------------------------------------------
-- BƯỚC 3: CẬP NHẬT LẠI KHO SỐ LƯỢNG CHO SẢN PHẨM SAU KHI ĐÃ TRỪ KHO MUA HÀNG
-- -------------------------------------------------------------
-- Sản phẩm ID = 1 bị mua 2 lần (User 999 và User 4) => kho giảm từ 100 xuống 98
-- Sản phẩm ID = 2 bị mua 1 lần (User 999) => kho giảm từ 100 xuống 99
UPDATE products SET quantity = 98 WHERE product_id = 1;
UPDATE products SET quantity = 99 WHERE product_id = 2;
