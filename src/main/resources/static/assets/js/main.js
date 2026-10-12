// Helper function to format currency
function formatCurrency(amount) {
    if (!amount && amount !== 0) return '0 đ';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

// Global Auth & Header State Management
document.addEventListener('DOMContentLoaded', function() {
    checkCurrentUser();
    updateCartCount();
});

function checkCurrentUser() {
    fetch('/api/auth/me')
        .then(res => res.json())
        .then(data => {
            const userNav = document.getElementById('user-nav');
            if (!userNav) return;

            if (data.authenticated && data.user) {
                const user = data.user;
                const isAdmin = user.role === 'ADMIN';
                userNav.innerHTML = `
                    <div class="user-dropdown">
                        <span class="user-name">👋 Xin chào, <strong>${user.fullName || user.username}</strong></span>
                        <div class="dropdown-content">
                            <a href="/profile.html">👤 Trang cá nhân</a>
                            <a href="/order-history.html">📦 Lịch sử đơn hàng</a>
                            ${isAdmin ? '<a href="/admin/dashboard.html" style="color: var(--brand-primary); font-weight: bold;">⚙️ Quản trị Admin</a>' : ''}
                            <a href="#" onclick="logout(); return false;" style="color: #ef4444;">🚪 Đăng xuất</a>
                        </div>
                    </div>
                `;
            } else {
                userNav.innerHTML = `
                    <a href="/login.html" class="btn btn-secondary">Đăng Nhập</a>
                    <a href="/register.html" class="btn btn-primary">Đăng Ký</a>
                `;
            }
        })
        .catch(err => console.error("Auth check failed:", err));
}

function updateCartCount() {
    fetch('/api/cart')
        .then(res => res.json())
        .then(data => {
            const cartBadge = document.getElementById('cart-count');
            if (cartBadge) {
                cartBadge.textContent = data.totalQuantity || 0;
            }
        })
        .catch(err => console.error("Cart count failed:", err));
}

function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(res => res.json())
        .then(() => {
            window.location.href = '/index.html';
        });
}

function addToCart(productId, quantity = 1) {
    if (!productId) {
        alert("❌ Lỗi: Mã sản phẩm không hợp lệ!");
        return;
    }

    // Kiểm tra đăng nhập trước khi thêm vào giỏ hàng
    fetch('/api/auth/me')
        .then(res => res.json())
        .then(auth => {
            if (!auth.authenticated || !auth.user) {
                alert("⚠️ Bạn cần đăng nhập tài khoản để thêm sản phẩm vào giỏ hàng!");
                window.location.href = '/login.html';
                return;
            }

            // Đã đăng nhập -> Gửi yêu cầu thêm vào giỏ
            fetch('/api/cart/add', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ productId: productId, quantity: quantity })
            })
            .then(res => {
                if (res.status === 401) {
                    alert("⚠️ Bạn cần đăng nhập tài khoản để thêm sản phẩm vào giỏ hàng!");
                    window.location.href = '/login.html';
                    return null;
                }
                return res.json();
            })
            .then(data => {
                if (!data) return;
                if (data.success !== false && (data.items || data.totalQuantity !== undefined || data.success)) {
                    alert("✅ Đã thêm sản phẩm vào giỏ hàng thành công!");
                    updateCartCount();
                } else {
                    alert("❌ " + (data.message || "Không thể thêm vào giỏ hàng"));
                }
            })
            .catch(err => {
                alert("❌ Lỗi khi thêm vào giỏ hàng!");
                console.error(err);
            });
        })
        .catch(err => {
            console.error("Auth check failed:", err);
            alert("⚠️ Vui lòng đăng nhập để thêm vào giỏ hàng!");
            window.location.href = '/login.html';
        });
}
