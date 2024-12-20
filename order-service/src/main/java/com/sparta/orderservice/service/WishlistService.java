package com.sparta.orderservice.service;

import com.sparta.orderservice.entity.Wishlist;
import com.sparta.orderservice.entity.WishlistItem;
import com.sparta.productservice.repository.ProductRepository;
import com.sparta.orderservice.repository.WishlistItemRepository;
import com.sparta.orderservice.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;

    // 위시리스트에 상품 추가
    public WishlistItem  addToWishlist(Long userId, Long productId, int quantity) {
        // 사용자 위시리스트 조회 또는 새로 생성
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null) {
            wishlist = new Wishlist();
            wishlist.setUserId(userId);
            wishlist = wishlistRepository.save(wishlist);
        }

        // 위시리스트에 중복된 상품이 있는지 확인
        Optional<WishlistItem> existingItem = wishlist.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        // 중복된 상품이 있으면 예외 처리
        if (existingItem.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product already in wishlist");
        }

        // 상품 존재 여부 확인
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        // WishlistItem 생성 및 저장
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setWishlist(wishlist);
        wishlistItem.setProductId(productId);
        wishlistItem.setQuantity(quantity);

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
        // 사용자 위시리스트 조회
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }

        // 삭제할 상품을 영속성 컨텍스트에서 다시 조회
        WishlistItem wishlistItem = wishlistItemRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WishlistItem not found"));

        // 삭제 대상 로그
        System.out.println("Deleting WishlistItem: " + wishlistItem);

        // 삭제 실행
        wishlistItemRepository.delete(wishlistItem);

        // 삭제 완료 로그
        System.out.println("Deleted WishlistItem: " + wishlistItem);
    }

    // 위시리스트 상품 수량 변경
    public void updateWishlistItemQuantity(Long userId, Long productId, int quantity) {
        //사용자 위시리스트 조회
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }

        // 수정할 상품 찾기
        Optional<WishlistItem> itemToUpdate = wishlist.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        // 상품이 없으면 예외 처리
        if (itemToUpdate.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in wishlist");
        }

        // 상품 수량 업데이트
        WishlistItem wishlistItem = itemToUpdate.get();
        wishlistItem.setQuantity(quantity);
        wishlistItemRepository.save(wishlistItem);
    }
}
