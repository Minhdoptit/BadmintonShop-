package com.smashzone.dto;

import java.util.Map;

public record ProductResponse(
    Integer id,
    Integer categoryId,
    String categoryName,
    String name,
    Double price,
    String productLink,
    String image,
    Integer quantity,
    Double importPrice,
    String metadataForAi,
    String createdAt,
    Map<String, String> specifications
) {
    public Integer getProductId() { return id; }
    public String getProductName() { return name; }
    public Double getUnitPrice() { return price; }
    public String getDescription() { return metadataForAi; }
    public String getWeight() {
        if (specifications != null && specifications.containsKey("Trọng lượng")) {
            return specifications.get("Trọng lượng");
        }
        return "4U (80-84g)";
    }
    public String getFlex() {
        if (specifications != null && specifications.containsKey("Độ dẻo")) {
            return specifications.get("Độ dẻo");
        }
        return "Trung bình";
    }
}
