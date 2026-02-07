package com.example.CoffeeShop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private String drinkType;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int prepTime;

    @Column(nullable = false)
    private String customerType;

    @Column(nullable = false)
    private double priorityScore = 0.0;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    private LocalDateTime startTime;

    @Column(nullable = false)
    private String status; // WAITING, IN_PROGRESS, DONE, ABANDONED

    private Long baristaId;

    @Column(nullable = false)
    private int skipCount = 0;
}
