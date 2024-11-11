package com.sparta.ecommerce.repository;

import com.sparta.ecommerce.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // userId를 이용해 Wishlist를 조회하는 메서드
    Wishlist findByUserId(Long userId);
}
