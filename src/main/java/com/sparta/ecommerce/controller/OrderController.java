package com.sparta.ecommerce.controller;

import com.sparta.ecommerce.entity.Order;
import com.sparta.ecommerce.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 주문 생성
     *
     * @param order 생성할 주문 정보
     * @return 생성된 주문
     */
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    /**
     * 모든 주문 조회
     *
     * @return 주문 목록
     */
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * ID로 특정 주문 조회
     *
     * @param id 조회할 주문의 ID
     * @return 조회된 주문 정보
     */
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id).orElse(null);
    }

    /**
     * 특정 주문 정보 업데이트
     *
     * @param id 업데이트할 주문의 ID
     * @param orderDetails 업데이트할 주문 정보
     * @return 업데이트된 주문 정보
     */
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        return orderService.updateOrder(id, orderDetails);
    }

    /**
     * 특정 주문 삭제
     *
     * @param id 삭제할 주문의 ID
     */
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
