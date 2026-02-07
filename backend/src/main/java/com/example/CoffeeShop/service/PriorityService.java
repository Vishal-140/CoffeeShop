package com.example.CoffeeShop.service;

import com.example.CoffeeShop.model.Order;
import com.example.CoffeeShop.repository.OrderRepository;
import com.example.CoffeeShop.util.PriorityCalculator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriorityService {

    private final OrderRepository orderRepository;

    public PriorityService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void recalculate() {
        List<Order> orders = orderRepository.findByStatus("WAITING");

        for (Order order : orders) {
            double score = PriorityCalculator.calculate(order);
            order.setPriorityScore(score);
        }

        orderRepository.saveAll(orders);
    }
}
