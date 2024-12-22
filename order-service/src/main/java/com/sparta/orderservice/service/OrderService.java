package com.sparta.orderservice.service;

import com.sparta.orderservice.dto.ProductResponse;
import com.sparta.orderservice.dto.UserResponse;
import com.sparta.orderservice.entity.Order;
import com.sparta.orderservice.entity.OrderItem;
import com.sparta.orderservice.entity.Wishlist;
import com.sparta.orderservice.feign.ProductClient;
import com.sparta.orderservice.feign.UserClient;
import com.sparta.orderservice.repository.OrderRepository;
import com.sparta.orderservice.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// OrderService: 주문 관련 비즈니스 로직 제공
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final UserClient userClient;
    private final ProductClient productClient;

    // 단일 상품 주문 생성
    @Transactional
    public Order createOrder(Long userId, Long productId, int quantity, String token) {
        UserResponse user = userClient.getUserByIdWithToken(userId, token);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        ProductResponse product = productClient.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);

        order.setOrderItems(List.of(orderItem));
        return orderRepository.save(order);
    }

    // 위시리스트 데이터를 주문으로 변환
    @Transactional
    public Order createOrderFromWishlist(Long userId, String token) {
        UserResponse user = userClient.getUserByIdWithToken(userId, token);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null || wishlist.getItems().isEmpty()) {
            throw new IllegalArgumentException("Wishlist is empty or not found.");
        }

        Order order = new Order();
        order.setUserId(userId);

        List<OrderItem> orderItems = wishlist.getItems().stream()
                .map(item -> {
                    ProductResponse product = productClient.getProductById(item.getProductId());
                    if (product == null) {
                        throw new IllegalArgumentException("Product not found.");
                    }
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProductId(item.getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
        orderRepository.save(order);

        wishlist.getItems().clear();

        if (wishlist.getItems().isEmpty()) {
            wishlistRepository.delete(wishlist);
        } else {
            wishlistRepository.save(wishlist);
        }

        return order;
    }

    // 주문 상태 변경
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
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

    // 주문 취소
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        if (order.getStatus() != Order.OrderStatus.ORDER_PLACED) {
            throw new IllegalArgumentException("Order can only be canceled if it is in ORDER_PLACED status.");
        }

        order.setStatus(Order.OrderStatus.CANCELED);
        return orderRepository.save(order);
    }

    // 주문 반품
    @Transactional
    public Order returnOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        if (order.getStatus() != Order.OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Order can only be returned if it is in DELIVERED status.");
        }

        order.setStatus(Order.OrderStatus.RETURNED);
        return orderRepository.save(order);
    }
}