package com.javarush.ivannikov.entity;

public class Wolf extends Predator {
    public Wolf() {
        super("Волк", 20, 3, 10,10, 4, 3, true);
    }

    @Override
    public Organism createOffspring() {
        return new Wolf();
    }
}