package com.sparta.ecommerce.service;

import com.sparta.ecommerce.entity.Wishlist;
import com.sparta.ecommerce.repository.ProductRepository;
import com.sparta.ecommerce.repository.UserRepository;
import com.sparta.ecommerce.repository.WishlistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // 위시리스트에 아이템 추가
    public Wishlist addToWishlist(Long userId, Long productId) {
        // 사용자 존재 확인
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // 상품 존재 확인
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        // 위시리스트 조회 또는 새로 생성
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null) {
            wishlist = new Wishlist();
            wishlist.setUserId(userId);
            wishlist.setProductIds(new ArrayList<>());
        }

        // 중복 방지: 이미 위시리스트에 존재하는 경우 예외 발생
        if (wishlist.getProductIds().contains(productId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product already in wishlist");
        }

        wishlist.getProductIds().add(productId);
        return wishlistRepository.save(wishlist);
    }

    // 특정 사용자의 위시리스트 조회
    public Wishlist getWishlistByUserId(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }
        return wishlist;
    }

    // 위시리스트에서 특정 아이템 삭제
    public void removeFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null || !wishlist.getProductIds().contains(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist item not found");
        }

        wishlist.getProductIds().remove(productId);
        wishlistRepository.save(wishlist); // 변경된 위시리스트 저장
    }
}
