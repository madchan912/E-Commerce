package com.sparta.ecommerce.repository;

import com.sparta.ecommerce.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    // 상품 ID를 기반으로 상세 정보를 조회
    ProductDetail findByProductId(Long productId);
}
