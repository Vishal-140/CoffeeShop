package com.example.CoffeeShop.repository;

import com.example.CoffeeShop.model.Barista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BaristaRepository extends JpaRepository<Barista, Long> {

    List<Barista> findByBusyFalse();
}
