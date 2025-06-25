package com.javarush.ivannikov.entity;

public class Goat extends Herbivore {
    public Goat() {
        super("Коза", 40, 2, 10,10, 5, 4, true);
    }

    @Override
    public Organism createOffspring() {
        return new Goat();
    }
}
