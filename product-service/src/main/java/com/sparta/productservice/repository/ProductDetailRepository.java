package com.sparta.productservice.repository;

import com.sparta.productservice.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    // 상품 ID를 기반으로 상세 정보를 조회
    ProductDetail findByProductId(Long productId);
}
