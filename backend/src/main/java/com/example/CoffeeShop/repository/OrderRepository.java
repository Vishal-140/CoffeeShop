package com.example.CoffeeShop.repository;

import com.example.CoffeeShop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(String status);
    List<Order> findByStatusOrderByPriorityScoreDescArrivalTimeAsc(String status);
}
