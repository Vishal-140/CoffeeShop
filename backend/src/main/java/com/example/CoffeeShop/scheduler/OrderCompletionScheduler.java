package com.example.CoffeeShop.scheduler;

import com.example.CoffeeShop.model.Barista;
import com.example.CoffeeShop.model.Order;
import com.example.CoffeeShop.repository.BaristaRepository;
import com.example.CoffeeShop.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderCompletionScheduler {

    private final OrderRepository orderRepository;
    private final BaristaRepository baristaRepository;

    public OrderCompletionScheduler(OrderRepository orderRepository,
                                    BaristaRepository baristaRepository) {
        this.orderRepository = orderRepository;
        this.baristaRepository = baristaRepository;
    }

    // runs every 5 seconds
    @Scheduled(fixedRate = 5000)
    public void completeOrders() {

        List<Order> inProgressOrders =
                orderRepository.findByStatus("IN_PROGRESS");

        for (Order order : inProgressOrders) {

            // safety check
            if (order.getStartTime() == null) continue;

            long minutesPassed = Duration
                    .between(order.getStartTime(), LocalDateTime.now())
                    .toMinutes();

            if (minutesPassed >= order.getPrepTime()) {

                // mark order done
                order.setStatus("DONE");

                // free barista
                Barista barista =
                        baristaRepository.findById(order.getBaristaId()).orElse(null);

                if (barista != null) {
                    barista.setBusy(false);

                    // reduce workload
                    barista.setWorkload(
                            Math.max(0, barista.getWorkload() - order.getPrepTime())
                    );

                    baristaRepository.save(barista);
                }

                orderRepository.save(order);
            }
        }
    }
}
