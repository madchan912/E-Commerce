package com.sparta.productservice.dto;

import com.sparta.productservice.entity.Product;
import com.sparta.productservice.entity.ProductDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDetailResponse {
    // 1. 상세 페이지에 필요한 기본 정보들 (Product에서 가져옴)
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String description; // 짧은 설명
    private Product.ProductStatus status;

    // 2. 진짜 상세 정보 (ProductDetail에서 가져옴)
    private String detailedDescription;
    private String imageUrl;

    public ProductDetailResponse(ProductDetail detail) {
        Product product = detail.getProduct();

        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.description = product.getDescription();
        this.status = product.getStatus();

        // 상세 정보 매핑
        this.detailedDescription = detail.getDetailedDescription();
        this.imageUrl = detail.getImageUrl();
    }
}
