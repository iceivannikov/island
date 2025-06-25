package com.javarush.ivannikov.factory;

import com.javarush.ivannikov.entity.*;

import java.util.concurrent.ThreadLocalRandom;

public class AnimalFactory {
    public static Predator randomPredator() {
        int type = ThreadLocalRandom.current().nextInt(0, 2);
        return switch (type) {
            case 0 -> new Wolf();
            case 1 -> new Bear();
            default -> new Wolf();
        };
    }

    public static Herbivore randomHerbivore() {
        int type = ThreadLocalRandom.current().nextInt(0, 2);
        return switch (type) {
            case 0 -> new Rabbit();
            case 1 -> new Goat();
            default -> new Rabbit();
        };
    }
}
