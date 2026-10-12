package com.smashzone.dto;

import java.util.List;

public record ProductDetailResponse(
    ProductResponse product,
    List<ProductResponse> similarProducts
) {
    public Integer getProductId() { return product != null ? product.id() : null; }
    public String getProductName() { return product != null ? product.name() : null; }
    public Double getPrice() { return product != null ? product.price() : null; }
    public String getImage() { return product != null ? product.image() : null; }
    public String getDescription() { return product != null ? product.metadataForAi() : null; }
}
