package com.example.CoffeeShop.service;

import com.example.CoffeeShop.dto.CreateOrderRequest;
import com.example.CoffeeShop.model.Order;
import com.example.CoffeeShop.repository.OrderRepository;
import com.example.CoffeeShop.util.MenuConfig;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // CREATE ORDER
    public Order createOrder(CreateOrderRequest req) {

        Order order = new Order();

        order.setOrderId(UUID.randomUUID().toString());
        order.setDrinkType(req.drinkType);
        order.setCustomerType(req.customerType);
        // Ensure arrival time is set exactly once here
        order.setArrivalTime(LocalDateTime.now());

        // MENU-BASED VALUES
        order.setPrepTime(MenuConfig.getPrepTime(req.drinkType));
        order.setPrice(MenuConfig.getPrice(req.drinkType));

        order.setStatus("WAITING");
        
        // Initial Priority Calculation
        // Note: wait time is 0, but other factors (Loyalty, Complexity) matter immediately
        order.setPriorityScore(com.example.CoffeeShop.util.PriorityCalculator.calculate(order));
        
        order.setSkipCount(0);
        order.setBaristaId(null);
        order.setStartTime(null);

        return orderRepository.save(order);
    }

    // WAITING ORDERS
    public List<Order> getWaitingOrders() {
        return orderRepository
                .findByStatusOrderByPriorityScoreDescArrivalTimeAsc("WAITING");
    }

    // IN-PROGRESS ORDERS
    public List<Order> getInProgressOrders() {
        return orderRepository.findByStatus("IN_PROGRESS");
    }

    // COMPLETED ORDERS
    public List<Order> getDoneOrders() {
        return orderRepository.findByStatus("DONE");
    }

    // ABANDONED ORDERS
    public List<Order> getAbandonedOrders() {
        return orderRepository.findByStatus("ABANDONED");
    }
}
