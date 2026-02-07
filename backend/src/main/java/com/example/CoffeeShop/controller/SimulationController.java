package com.example.CoffeeShop.controller;

import com.example.CoffeeShop.service.SimulationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/{seed}")
    public Map<String, Object> runSim(@PathVariable Long seed) {
        return simulationService.runSimulation(seed);
    }
}
