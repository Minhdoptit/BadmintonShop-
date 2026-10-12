package com.smashzone.repository;

import com.smashzone.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryId(Integer categoryId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.category.name) = LOWER(:categoryName) " +
           "OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :categoryName, '%')) " +
           "OR LOWER(:categoryName) LIKE LOWER(CONCAT('%', p.category.name, '%')) " +
           "OR (:categoryName LIKE '%Phụ kiện%' AND p.category.id = 3) " +
           "OR (:categoryName LIKE '%phu kien%' AND p.category.id = 3)")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(p.metadataForAi) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<Product> searchProducts(@Param("kw") String keyword);
}
