package com.example.CoffeeShop.util;

import com.example.CoffeeShop.model.Order;

import java.time.Duration;
import java.time.LocalDateTime;

public class PriorityCalculator {

    public static double calculate(Order order) {

        long waitMinutes = Duration
                .between(order.getArrivalTime(), LocalDateTime.now())
                .toMinutes();

        // 1. Wait time score (Max 40)
        double waitScore = Math.min(40, waitMinutes * 4);

        // 2. Complexity score (Max 25)
        double complexityScore = switch (order.getPrepTime()) {
            case 1 -> 25;
            case 2 -> 18;
            case 4 -> 10;
            case 6 -> 5;
            default -> 5;
        };

        // 3. Loyalty score (Max 10)
        double loyaltyScore = switch (order.getCustomerType()) {
            case "VIP" -> 20;
            case "PREMIUM" -> 15;
            case "GOLD" -> 10;
            case "REGULAR" -> 5;
            default -> 0;
        };

        // 4. Urgency score (Max 25)
        double urgencyScore = 0;
        if (waitMinutes >= 8) {
            urgencyScore = 25;
        } else if (waitMinutes >= 6) {
            urgencyScore = 15;
        }

        double total = waitScore + complexityScore + loyaltyScore + urgencyScore;

        // 5. Emergency Boost (> 8 min)
        if (waitMinutes > 8) {
            total += 50;
        }

        // 6. Fairness Boost (Recover skipped orders)
        // If an order has been skipped > 3 times, give it a significant boost
        if (order.getSkipCount() > 3) {
            total += 30; // Boost to ensure it gets picked
        }

        return total;
    }
}