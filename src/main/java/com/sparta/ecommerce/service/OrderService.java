package com.sparta.ecommerce.service;

import com.sparta.ecommerce.entity.Order;
import com.sparta.ecommerce.entity.Product;
import com.sparta.ecommerce.repository.OrderRepository;
import com.sparta.ecommerce.repository.ProductRepository;
import com.sparta.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 새로운 주문을 생성
    public Order createOrder(Order order) {
        // 사용자 존재 확인
        if (!userRepository.existsById(order.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // 상품 존재 확인
        if (!productRepository.existsById(order.getProductId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        order.setOrderDate(LocalDateTime.now());  // 주문 날짜를 현재 시간으로 설정
        order.setStatus(Order.OrderStatus.PENDING); // 상태가 기본값 PENDING으로 설정되어 있음
        return orderRepository.save(order);
    }

    // 모든 주문을 조회
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ID로 특정 주문을 조회
    public Optional<Order> getOrderById(Long id) {
        // 주문이 존재하는지 확인
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        return orderRepository.findById(id);
    }

    // 특정 주문의 정보를 업데이트
    public Order updateOrder(Long id, Order orderDetails) {
        // 주문이 존재하는지 확인
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // 사용자와 상품이 존재하는지 확인
        if (!userRepository.existsById(orderDetails.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // 상품이 존재하는지 확인
        if (!productRepository.existsById(orderDetails.getProductId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        // 존재하는 경우 업데이트 진행

        existingOrder.setUserId(orderDetails.getUserId());
        existingOrder.setProductId(orderDetails.getProductId());
        existingOrder.setQuantity(orderDetails.getQuantity());

        return orderRepository.save(existingOrder);
    }

    // 주문 상태 조회
    public Order.OrderStatus getOrderStatus(Long orderId) {
        // 주문 존재 여부 확인
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // 주문이 존재하면 상태 반환
        return existingOrder.getStatus();
    }

    // 주문 취소
    public void cancelOrder(Long orderId) {
        // 주문 조회
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // 주문 상태가 'SHIPPED'인 경우 취소 불가
        if (existingOrder.getStatus() != Order.OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel an order that has already been shipped.");
        }

        // 주문 상태를 'CANCELED'로 업데이트
        existingOrder.setStatus(Order.OrderStatus.CANCELED);
        orderRepository.save(existingOrder);
    }

    // 주문 반품
    public void returnOrder(Long orderId) {
        // 주문 조회
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // 주문 상태가 'DELIVERED'이어야 반품 가능
        if (existingOrder.getStatus() != Order.OrderStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only delivered orders can be returned.");
        }

        // 배송 완료 후 D+1일까지만 반품 가능
        LocalDateTime deliveryDate = existingOrder.getOrderDate().plusDays(1); // 주문 날짜 + 1일
        if (LocalDateTime.now().isAfter(deliveryDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return period has expired.");
        }

        // 주문 상태를 'RETURNED'로 업데이트
        existingOrder.setStatus(Order.OrderStatus.RETURNED);
        orderRepository.save(existingOrder);
    }

}
