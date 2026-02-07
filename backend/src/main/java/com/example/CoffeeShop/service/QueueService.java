package com.example.CoffeeShop.service;

import com.example.CoffeeShop.model.Barista;
import com.example.CoffeeShop.model.Order;
import com.example.CoffeeShop.repository.BaristaRepository;
import com.example.CoffeeShop.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueueService {

    private final OrderRepository orderRepository;
    private final BaristaRepository baristaRepository;

    public QueueService(OrderRepository orderRepository,
                        BaristaRepository baristaRepository) {
        this.orderRepository = orderRepository;
        this.baristaRepository = baristaRepository;
    }

    public void assignOrders() {
        // 1. Abandonment Check (NEW customers waiting > 8 min)
        List<Order> allWaiting = orderRepository.findByStatus("WAITING");
        LocalDateTime now = LocalDateTime.now();

        for (Order order : allWaiting) {
            long waitMinutes = Duration.between(order.getArrivalTime(), now).toMinutes();
            
            // Abadonment Check
            if ("NEW".equals(order.getCustomerType()) && waitMinutes >= 8) {
                order.setStatus("ABANDONED");
                orderRepository.save(order);
                continue;
            }
            
            // Warning Log (> 8 min, approaching 10 min hard constraint)
            if (waitMinutes >= 8) {
                 System.out.println("WARNING: Order " + order.getOrderId() + " is approaching 10 min timeout (Wait: " + waitMinutes + "m)");
            }
        }

        // 2. Refresh Queue (Clean status) & Baristas
        List<Order> queue = orderRepository
                .findByStatusOrderByPriorityScoreDescArrivalTimeAsc("WAITING");
        List<Barista> freeBaristas = baristaRepository.findByBusyFalse();
        
        if (queue.isEmpty() || freeBaristas.isEmpty()) return;

        // 3. Calculate Average Workload
        List<Barista> allBaristas = baristaRepository.findAll();
        double totalWorkload = allBaristas.stream().mapToInt(Barista::getWorkload).sum();
        double avgWorkload = allBaristas.isEmpty() ? 0 : totalWorkload / allBaristas.size();

        // 4. Assignment Loop
        // We iterate through the priority queue and try to match orders to baristas
        // We use a manual iterator to safely manage skips
        
        // Track assigned orders to avoid double processing in this cycle
        java.util.Set<Long> assignedOrderIds = new java.util.HashSet<>();

        for (Order candidate : queue) {
            if (freeBaristas.isEmpty()) break;

            long waitMinutes = Duration.between(candidate.getArrivalTime(), now).toMinutes();
            boolean isEmergency = waitMinutes >= 10;

            // Find best barista for this order
            Barista selectedBarista = null;
            
            // Sort free baristas by workload (asc) to balance load
            freeBaristas.sort(java.util.Comparator.comparingInt(Barista::getWorkload));

            for (Barista b : freeBaristas) {
                
                boolean isOverloaded = b.getWorkload() > (avgWorkload * 1.2);
                boolean isShortOrder = candidate.getPrepTime() <= 2;
                
                // If Emergency, ignore workload constraints
                if (isEmergency) {
                    selectedBarista = b;
                    break; 
                }
                
                // Normal Logic
                if (isOverloaded && !isShortOrder) {
                    // Skip this barista for this order
                    continue; 
                }
                
                // Barista is eligible
                selectedBarista = b;
                break;
            }

            if (selectedBarista != null) {
                // ASSIGN
                assign(candidate, selectedBarista);
                assignedOrderIds.add(candidate.getId());
                freeBaristas.remove(selectedBarista);
                
                // FAIRNESS CHECK
                // Any order currently in the queue (WAITING) that arrived BEFORE this candidate
                // and has NOT been assigned yet, was SKIPPED.
                for (Order other : queue) {
                    if (other.getId().equals(candidate.getId())) continue;
                    if (assignedOrderIds.contains(other.getId())) continue;
                    
                    if (other.getArrivalTime().isBefore(candidate.getArrivalTime())) {
                        other.setSkipCount(other.getSkipCount() + 1);
                        orderRepository.save(other);
                    }
                }
            }
        }
    }

    private void assign(Order order, Barista barista) {
        order.setStatus("IN_PROGRESS");
        order.setBaristaId(barista.getId());
        order.setStartTime(LocalDateTime.now());

        barista.setBusy(true);
        barista.setWorkload(barista.getWorkload() + order.getPrepTime());

        orderRepository.save(order);
        baristaRepository.save(barista);
        
        System.out.println("ASSIGNED Order: " + order.getOrderId() +
                           " (Pri: " + order.getPriorityScore() +
                           ") to " + barista.getName());
    }
}
