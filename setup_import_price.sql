-- ==========================================
-- SQL SCRIPT CẤU HÌNH CỘT GIÁ NHẬP (IMPORT_PRICE)
-- VÀ TỰ ĐỘNG CẬP NHẬT GIÁ NHẬP THẤP HƠN GIÁ BÁN 100K
-- ==========================================

USE badminton_shop;

-- 1. Thêm cột 'import_price' (giá nhập) vào bảng 'products' nếu chưa có
ALTER TABLE products ADD COLUMN import_price DECIMAL(15,2) DEFAULT NULL;

-- 2. Đẩy dữ liệu giá nhập mẫu: Giá nhập thấp hơn giá bán chính xác 100k (tối thiểu là 0)
UPDATE products SET import_price = GREATEST(0, price - 100000);
