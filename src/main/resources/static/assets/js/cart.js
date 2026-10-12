// cart.js - Thao tác tương tác Giỏ hàng

document.addEventListener('DOMContentLoaded', function() {
    // Lắng nghe sự kiện đổi số lượng giỏ hàng
    const quantityInputs = document.querySelectorAll('.cart-qty-input');
    
    quantityInputs.forEach(input => {
        input.addEventListener('change', function() {
            const productId = this.dataset.productId;
            const newQuantity = this.value;
            const contextPath = this.dataset.contextPath;
            
            if (newQuantity <= 0) {
                if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?")) {
                    window.location.href = `${contextPath}/cart?action=remove&productId=${productId}`;
                } else {
                    this.value = 1;
                }
            } else {
                window.location.href = `${contextPath}/cart?action=update&productId=${productId}&quantity=${newQuantity}`;
            }
        });
    });
});
