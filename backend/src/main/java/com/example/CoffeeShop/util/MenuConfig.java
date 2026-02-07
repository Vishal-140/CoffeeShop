package com.example.CoffeeShop.util;

public class MenuConfig {

    public static int getPrepTime(String drink) {
        return switch (drink) {
            case "Cold Brew" -> 1;
            case "Espresso", "Americano" -> 2;
            case "Cappuccino", "Latte" -> 4;
            case "Specialty", "Mocha" -> 6;
            default -> 6; // Default to max for safety
        };
    }

    public static int getPrice(String drink) {
        return switch (drink) {
            case "Cold Brew" -> 120;
            case "Americano" -> 140;
            case "Espresso" -> 150;
            case "Cappuccino" -> 180;
            case "Latte" -> 200;
            case "Specialty", "Mocha" -> 250;
            default -> 250;
        };
    }
}
