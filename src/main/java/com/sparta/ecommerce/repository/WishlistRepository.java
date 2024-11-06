package com.sparta.ecommerce.repository;

import com.sparta.ecommerce.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // 특정 사용자의 위시리스트 목록을 조회
    List<Wishlist> findByUserId(Long userId);

    // 특정 사용자의 위시리스트에서 특정 상품을 조회
    Wishlist findByUserIdAndProductId(Long userId, Long productId);
}
