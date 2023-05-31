package com.example.flowshop.utils;

import java.io.Serializable;

public class Product implements Serializable {
    private String name, price, brand, modelo, image;

    public Product(String name, String price, String brand, String modelo, String image) {
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.modelo = modelo;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getBrand() {
        return brand;
    }

    public String getModelo() {
        return modelo;
    }

    public String getImage() {
        return image;
    }
}
