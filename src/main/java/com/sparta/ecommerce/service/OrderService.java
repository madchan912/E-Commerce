package com.sparta.ecommerce.service;

import com.sparta.ecommerce.entity.Order;
import com.sparta.ecommerce.repository.OrderRepository;
import com.sparta.ecommerce.repository.ProductRepository;
import com.sparta.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // 새로운 주문을 생성합니다.
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
        return orderRepository.save(order);
    }

    // 모든 주문을 조회합니다.
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ID로 특정 주문을 조회합니다.
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // 특정 주문의 정보를 업데이트합니다.
    public Order updateOrder(Long id, Order orderDetails) {
        // 주문이 존재하는지 확인
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // 사용자와 상품이 존재하는지 확인
        if (!userRepository.existsById(orderDetails.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (!productRepository.existsById(orderDetails.getProductId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        // 존재하는 경우 업데이트 진행

        existingOrder.setUserId(orderDetails.getUserId());
        existingOrder.setProductId(orderDetails.getProductId());
        existingOrder.setQuantity(orderDetails.getQuantity());

        return orderRepository.save(existingOrder);
    }

    // 특정 주문을 삭제합니다.
    public void deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
    }
}
