package com.javarush.ivannikov.entity;

public class Bear extends Predator {
    public Bear() {
        super("Медведь", 150, 2, 10,10, 4, 5, true);
    }

    @Override
    public Organism createOffspring() {
        return new Bear();
    }
}
