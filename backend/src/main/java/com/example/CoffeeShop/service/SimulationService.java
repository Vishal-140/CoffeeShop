package com.example.CoffeeShop.service;

import com.example.CoffeeShop.model.Order;
import com.example.CoffeeShop.util.MenuConfig;
import com.example.CoffeeShop.util.PriorityCalculator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SimulationService {

    private static final int SIM_DURATION_MINS = 180; // 3 hours
    private static final double ARRIVAL_RATE = 1.4;

    public Map<String, Object> runSimulation(long seed) {
        Random random = new Random(seed);
        List<Order> orders = new ArrayList<>();
        List<Order> completedOrders = new ArrayList<>();
        
        LocalDateTime startTime = LocalDateTime.now().withHour(7).withMinute(0).withSecond(0).withNano(0);
        
        // 1. Generate Orders
        double currentTime = 0;
        long orderIdCounter = 1;
        
        while (currentTime < SIM_DURATION_MINS) {
            double interArrivalTime = -Math.log(1 - random.nextDouble()) / ARRIVAL_RATE;
            currentTime += interArrivalTime;
            
            if (currentTime > SIM_DURATION_MINS) break;
            
            Order order = new Order();
            order.setId(orderIdCounter++);
            order.setOrderId(UUID.randomUUID().toString());
            order.setArrivalTime(startTime.plusMinutes((long)currentTime));
            
            // Drink & Prep
            String[] drinks = {"Cold Brew", "Espresso", "Americano", "Cappuccino", "Latte", "Specialty"};
            double[] dFreq = {0.25, 0.20, 0.15, 0.20, 0.12, 0.08};
            String drink = select(random, drinks, dFreq);
            order.setDrinkType(drink);
            order.setPrepTime(MenuConfig.getPrepTime(drink));
            
            // Customer
            String[] customers = {"REGULAR", "GOLD", "VIP", "NEW"};
            double[] cFreq = {0.6, 0.2, 0.1, 0.1};
            order.setCustomerType(select(random, customers, cFreq));
            
            order.setStatus("WAITING");
            order.setSkipCount(0);
            
            orders.add(order);
        }
        
        // 2. Process
        // Sim Baristas: id -> busyUntil (minutes from start)
        double[] baristas = new double[3]; // 0, 1, 2
        double[] workloads = new double[3];
        
        List<Order> queue = new ArrayList<>(orders);
        double clock = 0;
        double timeStep = 0.5; // 30 seconds
        
        while (true) {
            boolean anyBusy = false;
            for (double b : baristas) {
                if (b > clock) {
                    anyBusy = true;
                    break;
                }
            }
            if (queue.isEmpty() && !anyBusy) break;

            LocalDateTime simNow = startTime.plusMinutes((long)clock);
            
            // Find arrived orders
            double finalClock = clock;
            List<Order> arrived = new ArrayList<>();
            for (Order o : queue) {
                double arrivalMin = java.time.Duration.between(startTime, o.getArrivalTime()).toMinutes();
                if (arrivalMin <= finalClock && "WAITING".equals(o.getStatus())) {
                    arrived.add(o);
                }
            }
            
            // ABANDONMENT
            for (Order o : arrived) {
                long wait = java.time.Duration.between(o.getArrivalTime(), simNow).toMinutes();
                if ("NEW".equals(o.getCustomerType()) && wait >= 8) {
                    o.setStatus("ABANDONED");
                    // Just for sim reporting
                    o.setStartTime(simNow); 
                    completedOrders.add(o);
                }
            }
            arrived.removeIf(o -> "ABANDONED".equals(o.getStatus()));
            
            // PRIORITY
            for (Order o : arrived) {
                o.setPriorityScore(PriorityCalculator.calculate(o, simNow));
            }
            
            // Sort
            arrived.sort(Comparator.comparingDouble(Order::getPriorityScore).reversed()
                    .thenComparing(Order::getArrivalTime));
            
            // ASSIGNMENT
            // Identify free baristas
            List<Integer> freeBaristaIndices = new ArrayList<>();
            for (int i=0; i<3; i++) {
                if (baristas[i] <= clock) freeBaristaIndices.add(i);
            }
            // Sort by workload
            freeBaristaIndices.sort(Comparator.comparingDouble(i -> workloads[i]));
            
            double totalLoad = Arrays.stream(workloads).sum();
            double avgLoad = totalLoad / 3.0;
            
            // Assignment Loop
            Set<Long> assignedIds = new HashSet<>();
            
            for (Order candidate : arrived) {
                if (freeBaristaIndices.isEmpty()) break;
                
                long wait = java.time.Duration.between(candidate.getArrivalTime(), simNow).toMinutes();
                boolean isEmergency = wait >= 10;
                
                Integer selectedB = null;
                
                for (int i=0; i<freeBaristaIndices.size(); i++) {
                    int bIdx = freeBaristaIndices.get(i);
                    boolean isOverloaded = workloads[bIdx] > (avgLoad * 1.2);
                    boolean isShort = candidate.getPrepTime() <= 2;
                    
                    if (isEmergency) {
                        selectedB = bIdx;
                        freeBaristaIndices.remove(i);
                        break;
                    }
                    
                    if (isOverloaded && !isShort) continue;
                    
                    selectedB = bIdx;
                    freeBaristaIndices.remove(i);
                    break;
                }
                
                if (selectedB != null) {
                    // Assign
                    candidate.setStatus("DONE");
                    candidate.setBaristaId((long)(selectedB + 1));
                    candidate.setStartTime(simNow);
                    
                    baristas[selectedB] = clock + candidate.getPrepTime();
                    workloads[selectedB] += candidate.getPrepTime();
                    
                    assignedIds.add(candidate.getId());
                    completedOrders.add(candidate);
                }
            }
            
            // Skips
             if (!assignedIds.isEmpty()) {
                 for (Order o : arrived) {
                     if (!assignedIds.contains(o.getId())) {
                        // Check if it was skipped (simple approximation for sim)
                         o.setSkipCount(o.getSkipCount() + 1);
                     }
                 }
             }
             
             // Cleanup queue
             queue.removeIf(o -> "ABANDONED".equals(o.getStatus()) || "DONE".equals(o.getStatus()));
             
             clock += timeStep;
             if (clock > 300) break; // safety
        }
        
        // ... existing logic ...
        
        // 3. Summary Metrics (Moved up for JSON response)
        long abandonedCount = completedOrders.stream().filter(o -> "ABANDONED".equals(o.getStatus())).count();
        double totalWait = completedOrders.stream().mapToDouble(o -> {
            long wait = java.time.Duration.between(o.getArrivalTime(), o.getStartTime()).toMinutes();
            return wait;
        }).sum();
        double avgWait = completedOrders.isEmpty() ? 0 : totalWait / completedOrders.size();

        Map<String, Object> result = new LinkedHashMap<>(); // Use LinkedHashMap for order
        result.put("seed", seed);
        result.put("totalOrders", completedOrders.size());
        
        result.put("averageWaitTime", String.format("%.2f min", avgWait));
        result.put("abandonedCount", abandonedCount);
        result.put("abandonedRate", String.format("%.1f%%", (double)abandonedCount / completedOrders.size() * 100));
        
        List<Map<String, Object>> bStats = new ArrayList<>();
        for(int i=0; i<3; i++) {
            Map<String, Object> b = new HashMap<>();
            b.put("id", i+1);
            b.put("workload", String.format("%.0f min", workloads[i]));
            bStats.add(b);
        }
        result.put("baristas", bStats);
        result.put("orders", completedOrders);

        // LOGGING TO CONSOLE
        System.out.println("\n=== SIMULATION RESULTS (Seed: " + seed + ") ===");
        System.out.println("----------------------------------------");
        System.out.println("Result Generation Complete.");

        // 1. Barista Workloads
        System.out.println("\n--- Barista Workloads ---");
        for(int i=0; i<3; i++) {
             System.out.println("Barista " + (i+1) + ": " + String.format("%.0f", workloads[i]) + " min");
        }

        // 2. Order Details
        System.out.println("\n--- Order Details (Sample: First 20) ---");
        System.out.println(String.format("%-10s | %-15s | %-10s | %-10s | %-10s", "Time", "Drink", "Customer", "Wait(m)", "Status"));
        System.out.println("-------------------------------------------------------------------");
        
        completedOrders.stream()
            .sorted(Comparator.comparing(Order::getArrivalTime))
            .limit(20)
            .forEach(o -> {
                long wait = java.time.Duration.between(o.getArrivalTime(), o.getStartTime()).toMinutes();
                String timeStr = o.getArrivalTime().toLocalTime().toString();
                System.out.println(String.format("%-10s | %-15s | %-10s | %-10d | %-10s", 
                    timeStr, o.getDrinkType(), o.getCustomerType(), wait, o.getStatus()));
            });
            
        if (completedOrders.size() > 20) {
            System.out.println("... (showing first 20 of " + completedOrders.size() + " orders)");
        }

        System.out.println("\n--- Summary ---");
        System.out.println("Total Orders: " + completedOrders.size());
        System.out.println("Average Wait Time: " + String.format("%.2f", avgWait) + " min");
        System.out.println("Abandoned: " + abandonedCount + " (" + String.format("%.1f", (double)abandonedCount/completedOrders.size()*100) + "%)");
        System.out.println("========================================\n");
        
        return result;
    }

    private String select(Random r, String[] items, double[] weights) {
        double val = r.nextDouble();
        double cum = 0;
        for (int i=0; i<items.length; i++) {
            cum += weights[i];
            if (val < cum) return items[i];
        }
        return items[items.length-1];
    }
}
