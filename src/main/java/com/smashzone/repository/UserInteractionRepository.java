package com.smashzone.repository;

import com.smashzone.entity.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Integer> {
    List<UserInteraction> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<UserInteraction> findByUserId(Integer userId);
    List<UserInteraction> findByProductId(Integer productId);
}
