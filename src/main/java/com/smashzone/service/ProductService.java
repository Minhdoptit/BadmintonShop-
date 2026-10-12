package com.smashzone.service;

import com.smashzone.dto.ProductCreateRequest;
import com.smashzone.dto.ProductResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Integer id);
    List<ProductResponse> getProductsByCategoryName(String categoryName);
    List<ProductResponse> getProductsByCategoryId(Integer categoryId);
    List<ProductResponse> searchProducts(String keyword);
    ProductResponse createProduct(ProductCreateRequest request);
    ProductResponse updateProduct(Integer id, ProductCreateRequest request);
    void deleteProduct(Integer id);
}
