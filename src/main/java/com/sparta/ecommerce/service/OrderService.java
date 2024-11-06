package com.sparta.ecommerce.service;

import com.sparta.ecommerce.entity.Order;
import com.sparta.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 새로운 주문을 생성합니다.
    public Order createOrder(Order order) {
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
        return orderRepository.findById(id)
                .map(order -> {
                    order.setUserId(orderDetails.getUserId());
                    order.setProductId(orderDetails.getProductId());
                    order.setQuantity(orderDetails.getQuantity());
                    // 주문 날짜는 업데이트하지 않음
                    return orderRepository.save(order);
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
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
