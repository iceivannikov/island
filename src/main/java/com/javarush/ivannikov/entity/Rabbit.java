package com.javarush.ivannikov.entity;

public class Rabbit extends Herbivore{
    public Rabbit() {
        super("Кролик", 5, 4, 10,10, 2, 4, true);
    }

    @Override
    public Organism createOffspring() {
        return new Rabbit();
    }
}
