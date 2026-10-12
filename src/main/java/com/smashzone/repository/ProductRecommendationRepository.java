package com.smashzone.repository;

import com.smashzone.entity.ProductRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface ProductRecommendationRepository extends JpaRepository<ProductRecommendation, Integer> {
    List<ProductRecommendation> findByUserIdOrderByScoreDesc(Integer userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductRecommendation r WHERE r.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}
