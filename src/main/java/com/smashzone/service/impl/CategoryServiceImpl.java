package com.smashzone.service.impl;

import com.smashzone.dto.CategoryResponse;
import com.smashzone.entity.Category;
import com.smashzone.exception.ResourceNotFoundException;
import com.smashzone.repository.CategoryRepository;
import com.smashzone.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    private CategoryResponse mapToResponse(Category c) {
        long count = c.getProducts() != null ? c.getProducts().size() : 0;
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription(), count);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Integer id) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));
        return mapToResponse(c);
    }
}
