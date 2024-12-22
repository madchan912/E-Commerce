package com.sparta.orderservice.controller;

import com.sparta.orderservice.entity.Wishlist;
import com.sparta.orderservice.entity.WishlistItem;
import com.sparta.orderservice.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * 사용자 위시리스트 조회
     * 
     * @param userId 사용자 ID
     * @return 위시리스트
     */
    @GetMapping("/{userId}")
    public Wishlist getWishlist(@PathVariable long userId) {
        return wishlistService.getWishlistByUserId(userId);
    }

    /**
     * 위시리스트 상품 추가
     *
     * @param userId    사용자 ID
     * @param productId 상품 ID
     * @param quantity  상품 수량
     * @return 추가된 위시리스트 아이템
     */
    @PostMapping("/{userId}/{productId}")
    public WishlistItem addToWishlist(@PathVariable Long userId,
                                      @PathVariable Long productId,
                                      @RequestParam int quantity,
                                      @RequestHeader("Authorization") String token) {
        return wishlistService.addToWishlist(userId, productId, quantity, token);
    }

    /**
     * 위시리스트 상품 삭제
     *
     * @param userId    사용자 ID
     * @param productId 상품 ID
     */
    @DeleteMapping("/{userId}/{productId}")
    public void removeFromWishlist(@PathVariable long userId,
                                   @PathVariable long productId){
        wishlistService.removeFromWishlist(userId, productId);
    }

    /**
     * 위시리스트 상품 수량 변경
     * 
     * @param userId    사용자 ID
     * @param productId 상품 ID
     * @param quantity  새로운 수량
     */
    @PutMapping("/{userId}/{productId}")
    public void updateWishlistItemQuantity(@PathVariable long userId,
                                           @PathVariable long productId,
                                           @RequestParam int quantity) {
        wishlistService.updateWishlistItemQuantity(userId, productId, quantity);
    }
}
