package com.example.CoffeeShop.service;

import com.example.CoffeeShop.model.Barista;
import com.example.CoffeeShop.repository.BaristaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BaristaService {

    private final BaristaRepository baristaRepository;

    public BaristaService(BaristaRepository baristaRepository) {
        this.baristaRepository = baristaRepository;
    }

    @PostConstruct
    public void initBaristas() {
        if (baristaRepository.count() == 0) {
            baristaRepository.save(new Barista(null, "Barista-1", false, 0));
            baristaRepository.save(new Barista(null, "Barista-2", false, 0));
            baristaRepository.save(new Barista(null, "Barista-3", false, 0));
        }
    }

    public List<Barista> getFreeBaristas() {
        return baristaRepository.findByBusyFalse();
    }

    public List<Barista> getAll() {
        return baristaRepository.findAll();
    }

}
