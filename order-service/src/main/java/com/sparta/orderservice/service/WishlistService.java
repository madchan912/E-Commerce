package com.sparta.orderservice.service;

import com.sparta.orderservice.dto.ProductResponse;
import com.sparta.orderservice.dto.UserResponse;
import com.sparta.orderservice.entity.Wishlist;
import com.sparta.orderservice.entity.WishlistItem;
import com.sparta.orderservice.feign.ProductClient;
import com.sparta.orderservice.feign.UserClient;
import com.sparta.orderservice.repository.WishlistItemRepository;
import com.sparta.orderservice.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserClient userClient;
    private final ProductClient productClient;

    // 위시리스트에 상품 추가
    public WishlistItem addToWishlist(long userId, Long productId, int quantity, String token) {
        UserResponse user = userClient.getUserByIdWithToken(userId, token);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null) {
            wishlist = new Wishlist();
            wishlist.setUserId(userId);
            wishlist = wishlistRepository.save(wishlist);
        }

        if (wishlist.getItems() == null) {
            wishlist.setItems(new ArrayList<>());
        }

        Optional<WishlistItem> existingItem = wishlist.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product already in wishlist");
        }

        ProductResponse product = productClient.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setWishlist(wishlist);
        wishlistItem.setProductId(productId);
        wishlistItem.setQuantity(quantity);

        wishlist.getItems().add(wishlistItem);
        return wishlistItemRepository.save(wishlistItem);
    }


    // 사용자 위시리스트 조회
    public Wishlist getWishlistByUserId(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }
        return wishlist;
    }

    // 위시리스트에서 특정 상품 삭제
    public void removeFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }

        WishlistItem wishlistItem = wishlistItemRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WishlistItem not found"));

        log.info("Deleting WishlistItem: " + wishlistItem);

        wishlistItemRepository.delete(wishlistItem);

        log.info("Deleted WishlistItem: " + wishlistItem);
    }

    // 위시리스트 상품 수량 변경
    public void updateWishlistItemQuantity(Long userId, Long productId, int quantity) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }

        Optional<WishlistItem> itemToUpdate = wishlist.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (itemToUpdate.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in wishlist");
        }

        WishlistItem wishlistItem = itemToUpdate.get();
        wishlistItem.setQuantity(quantity);
        wishlistItemRepository.save(wishlistItem);
    }
}
