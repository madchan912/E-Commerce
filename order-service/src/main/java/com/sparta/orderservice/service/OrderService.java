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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final UserClient userClient;
    private final ProductClient productClient;

    // Circuit Breaker 적용 메서드 (주석 해제 및 로직 연결)
    @CircuitBreaker(name = "productClient", fallbackMethod = "fallbackGetProduct")
    public ProductResponse getProduct(Long productId){
        //Feign Client 직접 호출
        return  productClient.getProductById(productId);
    }

    //Fallback 메서드: 장애 발생 시 실행
    public ProductResponse fallbackGetProduct(Long productId, Throwable t) {
        log.error("Circuit Breaker triggered for productId: {}. Reason: {}", productId, t.getMessage());
        // null을 반환하면 createOrder의 null 체크 로직에서 걸러짐
        return null;
    }

    // 단일 상품 주문 생성
    @Transactional
    public Order createOrder(Long userId, Long productId, int quantity, String token) {
        UserResponse user = userClient.getUserByIdWithToken(userId, token);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        // productClient 직접 호출 -> 내부 getProduct(서킷 적용됨) 호출로 변경
        ProductResponse product = getProduct(productId);

        if  (product == null) {
            // 서킷 브레이커가 null을 리턴하면 여기서 걸림
            throw new IllegalArgumentException("Product service is unavailable or product not found.");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.ORDER_PLACED);

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
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.ORDER_PLACED);

        List<OrderItem> orderItems = wishlist.getItems().stream()
                .map(item -> {
                    // 여기도 서킷 브레이커 적용된 메서드 호출
                    ProductResponse product = getProduct(item.getProductId());
                    if (product == null) {
                        throw new IllegalArgumentException("Product not found or Service Unavailable.");
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
        wishlistRepository.delete(wishlist); // 비었으면 삭제

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
        return orderRepository.findAllByUserId(userId);
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