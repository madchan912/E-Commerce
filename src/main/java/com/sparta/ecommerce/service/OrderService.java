package com.sparta.ecommerce.service;

import com.sparta.ecommerce.entity.Order;
import com.sparta.ecommerce.entity.OrderItem;
import com.sparta.ecommerce.entity.Wishlist;
import com.sparta.ecommerce.entity.WishlistItem;
import com.sparta.ecommerce.repository.OrderRepository;
import com.sparta.ecommerce.repository.UserRepository;
import com.sparta.ecommerce.repository.ProductRepository;
import com.sparta.ecommerce.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;

    // 단일 상품 주문 생성
    @Transactional
    public Order createOrder(Long userId, Long productId, int quantity) {
        // 사용자 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // 상품 확인
        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));

        // 주문 생성
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());

        // 주문 항목 생성
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);  // 주문과 연결
        orderItem.setProductId(productId);  // 상품 ID
        orderItem.setQuantity(quantity);  // **수량 설정**

        // 주문 저장
        order.setOrderItems(List.of(orderItem));  // 주문에 항목 설정
        return orderRepository.save(order);  // 주문과 주문 항목 저장
    }

    // 위시리스트 데이터를 주문으로 변환
    @Transactional
    public Order createOrderFromWishlist(Long userId) {
        // 사용자 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // 위시리스트 조회
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null || wishlist.getItems().isEmpty()) {
            throw new IllegalArgumentException("Wishlist is empty or not found.");
        }

        // 주문 생성
        Order order = new Order();
        order.setUserId(userId);

        // 위시리스트 아이템을 OrderItem으로 변환
        List<OrderItem> orderItems = wishlist.getItems().stream()
                .map(item -> {
                    productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("Product not found."));
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProductId(item.getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    return orderItem;
                })
                .collect(Collectors.toList());

        // 주문 저장
        order.setOrderItems(orderItems);
        orderRepository.save(order);

        // 위시리스트 초기화
        wishlist.getItems().clear();

        // 위시리스트가 비어 있으면 삭제
        if (wishlist.getItems().isEmpty()) {
            wishlistRepository.delete(wishlist);
        } else {
            wishlistRepository.save(wishlist);  // 위시리스트 아이템만 비우고 저장
        }

        return order;
    }

    // 주문 상태 변경
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        // 주문 확인
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // 특정 사용자 주문 조회
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}
