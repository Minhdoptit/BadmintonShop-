package com.smashzone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interactions")
public class UserInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interaction_id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "interaction_type", nullable = false)
    private String interactionType;

    @Column(name = "interaction_score")
    private Double interactionScore = 1.0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserInteraction() {}

    public UserInteraction(Integer userId, Integer productId, String interactionType, Double interactionScore) {
        this.userId = userId;
        this.productId = productId;
        this.interactionType = interactionType;
        this.interactionScore = interactionScore;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getInteractionType() { return interactionType; }
    public void setInteractionType(String interactionType) { this.interactionType = interactionType; }

    public Double getInteractionScore() { return interactionScore; }
    public void setInteractionScore(Double interactionScore) { this.interactionScore = interactionScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
