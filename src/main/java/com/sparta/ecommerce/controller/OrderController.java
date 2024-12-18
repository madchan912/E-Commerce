package com.sparta.ecommerce.controller;

import com.sparta.ecommerce.entity.Order;
import com.sparta.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 단일 상품 주문 생성
     *
     * @param userId    주문한 사용자 ID
     * @param productId 주문한 상품 ID
     * @param quantity  주문한 상품 수량
     * @return 생성된 주문 정보
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestParam Long userId,
                             @RequestParam Long productId,
                             @RequestParam int quantity) {
        return orderService.createOrder(userId, productId, quantity);
    }

    /**
     * 위시리스트를 기반으로 주문 생성
     *
     * @param userId 주문한 사용자 ID
     * @return 생성된 주문 정보
     */
    @PostMapping("/create-from-wishlist")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrderFromWishlist(@RequestParam Long userId) {
        return orderService.createOrderFromWishlist(userId);
    }

    /**
     * 주문 상태 업데이트
     *
     * @param orderId 주문 ID
     * @param status  주문 상태
     * @return 상태가 업데이트된 주문 정보
     */
    @PutMapping("/{orderId}/status")
    public Order updateOrderStatus(@PathVariable Long orderId, @RequestParam Order.OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    /**
     * 특정 사용자의 주문 목록 조회
     *
     * @param userId 주문한 사용자 ID
     * @return 사용자의 주문 목록
     */
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }
}
