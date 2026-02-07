package com.example.CoffeeShop.controller;

import com.example.CoffeeShop.model.Barista;
import com.example.CoffeeShop.service.BaristaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/baristas")
public class BaristaController {

    private final BaristaService baristaService;

    public BaristaController(BaristaService baristaService) {
        this.baristaService = baristaService;
    }

    @GetMapping
    public List<Barista> getAll() {
        return baristaService.getAll();
    }
}
