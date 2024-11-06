package com.sparta.ecommerce.service;

import com.sparta.ecommerce.entity.Wishlist;
import com.sparta.ecommerce.repository.ProductRepository;
import com.sparta.ecommerce.repository.UserRepository;
import com.sparta.ecommerce.repository.WishlistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        // 중복 방지: 이미 위시리스트에 존재하는 경우 예외 발생
        if (wishlistRepository.findByUserIdAndProductId(userId, productId) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setProductId(productId);
        return wishlistRepository.save(wishlist);
    }

    // 특정 사용자의 위시리스트 조회
    public List<Wishlist> getWishlistByUserId(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    // 위시리스트에서 특정 아이템 삭제
    public void removeFromWishlist(Long userId, Long productId) {
        Wishlist wishlistItem = wishlistRepository.findByUserIdAndProductId(userId, productId);
        if (wishlistItem != null) {
            wishlistRepository.delete(wishlistItem);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist item not found");
        }
    }
}
