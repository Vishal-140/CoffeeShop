package com.example.CoffeeShop.scheduler;

import com.example.CoffeeShop.service.PriorityService;
import com.example.CoffeeShop.service.QueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QueueScheduler {

    private final PriorityService priorityService;
    private final QueueService queueService;

    public QueueScheduler(PriorityService priorityService,
                          QueueService queueService) {
        this.priorityService = priorityService;
        this.queueService = queueService;
    }

    // Recalculate priority every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void recalculatePriority() {
        priorityService.recalculate();
    }

    // Assign orders every 5 seconds (after priority update)
    @Scheduled(fixedRate = 5000)
    public void assignOrders() {
        queueService.assignOrders();
    }
}
