package com.example.CoffeeShop.controller;

import com.example.CoffeeShop.dto.CreateOrderRequest;
import com.example.CoffeeShop.model.Order;
import com.example.CoffeeShop.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order create(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/waiting")
    public List<Order> getWaitingOrders() {
        return orderService.getWaitingOrders();
    }

    @GetMapping("/in-progress")
    public List<Order> inProgress() {
        return orderService.getInProgressOrders();
    }

    @GetMapping("/done")
    public List<Order> done() {
        return orderService.getDoneOrders();
    }

}
