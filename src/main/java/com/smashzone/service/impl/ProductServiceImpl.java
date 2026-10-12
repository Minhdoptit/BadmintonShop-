package com.smashzone.service.impl;

import com.smashzone.dto.ProductCreateRequest;
import com.smashzone.dto.ProductResponse;
import com.smashzone.entity.Category;
import com.smashzone.entity.Product;
import com.smashzone.exception.ResourceNotFoundException;
import com.smashzone.repository.CategoryRepository;
import com.smashzone.repository.ProductRepository;
import com.smashzone.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    private ProductResponse mapToResponse(Product p) {
        Integer catId = p.getCategory() != null ? p.getCategory().getId() : null;
        String catName = p.getCategory() != null ? p.getCategory().getName() : "Khác";
        String createdStr = p.getCreatedAt() != null ? p.getCreatedAt().format(FORMATTER) : "";

        // Default specifications for badminton equipment
        Map<String, String> specs = new LinkedHashMap<>();
        specs.put("Xuất xứ", "Nhật Bản / Đài Loan");
        specs.put("Trọng lượng", "4U (80 - 84g)");
        specs.put("Điểm cân bằng", "295 ± 3mm (Hơi nặng đầu)");
        specs.put("Độ dẻo", "Trung bình (Medium)");
        specs.put("Sức căng tối đa", "28 - 30 lbs (12.5 - 13.5 kg)");
        specs.put("Bảo hành", "6 tháng chính hãng Smash Zone");

        return new ProductResponse(
                p.getId(),
                catId,
                catName,
                p.getName(),
                p.getPrice(),
                p.getProductLink(),
                p.getImage(),
                p.getQuantity(),
                p.getImportPrice(),
                p.getMetadataForAi() != null ? p.getMetadataForAi() : p.getName(),
                createdStr,
                specs
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        return mapToResponse(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return getAllProducts();
        }
        String lower = categoryName.trim().toLowerCase();
        if (lower.contains("vợt") || lower.contains("vot")) {
            return getProductsByCategoryId(1);
        }
        if (lower.contains("giày") || lower.contains("giay")) {
            return getProductsByCategoryId(2);
        }
        if (lower.contains("phụ kiện") || lower.contains("phu kien") || lower.contains("cầu") || lower.contains("cau")) {
            return getProductsByCategoryId(3);
        }
        List<Product> list = productRepository.findByCategoryName(categoryName);
        if (list.isEmpty()) {
            list = productRepository.searchProducts(categoryName);
        }
        return list.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategoryId(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product p = new Product();
        p.setName(request.name());
        p.setPrice(request.price());
        p.setProductLink(request.productLink());
        p.setImage(request.image());
        p.setQuantity(request.quantity() != null ? request.quantity() : 10);
        p.setImportPrice(request.importPrice() != null ? request.importPrice() : request.price() * 0.7);
        p.setMetadataForAi(request.metadataForAi());
        p.setCreatedAt(LocalDateTime.now());

        if (request.categoryId() != null) {
            Category cat = categoryRepository.findById(request.categoryId()).orElse(null);
            p.setCategory(cat);
        }

        Product saved = productRepository.save(p);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Integer id, ProductCreateRequest request) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

        p.setName(request.name());
        p.setPrice(request.price());
        if (request.productLink() != null) p.setProductLink(request.productLink());
        if (request.image() != null) p.setImage(request.image());
        if (request.quantity() != null) p.setQuantity(request.quantity());
        if (request.importPrice() != null) p.setImportPrice(request.importPrice());
        if (request.metadataForAi() != null) p.setMetadataForAi(request.metadataForAi());

        if (request.categoryId() != null) {
            Category cat = categoryRepository.findById(request.categoryId()).orElse(null);
            p.setCategory(cat);
        }

        Product saved = productRepository.save(p);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id);
        }
        productRepository.deleteById(id);
    }
}
