package com.sparta.ecommerce.controller;

import com.sparta.ecommerce.entity.Wishlist;
import com.sparta.ecommerce.service.WishlistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * 위시리스트에 아이템 추가
     *
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @return 추가된 위시리스트 항목 (중복일 경우 null 반환)
     */
    @PostMapping("/{userId}/{productId}")
    public Wishlist addToWishlist(@PathVariable Long userId, @PathVariable Long productId) {
        return wishlistService.addToWishlist(userId, productId);
    }

    /**
     * 특정 사용자의 위시리스트 조회
     *
     * @param userId 사용자 ID
     * @return 사용자의 위시리스트 목록
     */
    @GetMapping("/{userId}")
    public Wishlist getWishlistByUserId(@PathVariable Long userId) {
        return wishlistService.getWishlistByUserId(userId);
    }

    /**
     * 위시리스트에서 특정 아이템 삭제
     *
     * @param userId 사용자 ID
     * @param productId 상품 ID
     */
    @DeleteMapping("/{userId}/{productId}")
    public void removeFromWishlist(@PathVariable Long userId, @PathVariable Long productId) {
        wishlistService.removeFromWishlist(userId, productId);
    }
}
